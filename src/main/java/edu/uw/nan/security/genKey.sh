#!/bin/bash

function genKey {
	KN=$1
	TN=$2
	# Create keystore. Contains the asymetric key pair
	KEY_STORE=${KN}Key.jck
	if [ ! -e $KEY_STORE ]; then 
		keytool -genkeypair -alias ${KN}PrivKey -keyalg RSA -keysize 512 -validity 365 \
			-storetype JCEKS -keystore $KEY_STORE -storepass ${KN}StorePass \
			-dname "cn=$KN, ou=cp130, o=UW, l=Seattle, st=Washington, c=US" \
			-keypass ${KN}PrivKeyPass
	else
		echo "Keystore already exits, skipping creation of $KEY_STORE."
	fi

	# Create a trust store containing the public key certifcate.
	TRUST_STORE=${TN}Trust.jck
	if [ ! -e $TRUST_STORE ]; then
		# Extract the certifcate from the keystore.
		TMP_CERT_FILE=/tmp/${KN}_$$.crt
		keytool -export -file $TMP_CERT_FILE \
			-alias ${KN}PrivKey -keypass ${KN}PrivKeyPass \
			-storetype JCEKS -keystore $KEY_STORE -storepass ${KN}StorePass
		
		# Place the certifcate in a trust store.
		keytool -importcert -noprompt -file $TMP_CERT_FILE \
			-alias ${KN}Cert \
			-storetype JCEKS -keystore $TRUST_STORE -storepass ${TN}TrustPass
		rm -f $TMP_CERT_FILE
	else
		echo "Truststore already exist, skipping creation of $TRUST_STORE"
	fi
}

genKey client broker
genKey broker client

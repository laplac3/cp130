package edu.uw.nan.security;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.cert.Certificate;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uw.ext.framework.order.ClientOrder;
import edu.uw.ext.framework.order.ClientOrderCodec;

public class LaplaceClientOrderCodec implements ClientOrderCodec {

	private static final String ROOT_PATH = "/";
	private static final String JCEKS = "JCEKS";
	private static final String AES_ALGORITHM = "AES";
	private static final int AES_KEY_SIZE = 128;
	private static final int AES_KEY_LENGTH = 16;	
	private static final String SIGN_ALGORITH = "MD5withRSA";
	
	 public LaplaceClientOrderCodec() {
		 
	 }
	
	private static class Tri {
		
		public byte[] encipheredSharedKey;
		public byte[] cipherText;
		public byte[] signature;
	}
	

	@Override
	public List<ClientOrder> decipher(final File orderFile,
			final String recipientKeyStoreName, 
			final char[] reccipientKeyStorePassword,
			final String recipientKeyName,
			final char[] recipientKeyPassword,
			final String trustStoreName,
			final char[] trustStorePassword,
			String signerCertName) throws GeneralSecurityException, IOException {
	    

        Tri triple = readFile(orderFile);
        KeyStore keyStore = loadKeyStore(recipientKeyStoreName, reccipientKeyStorePassword);
        Key key = keyStore.getKey(recipientKeyName, recipientKeyPassword);
        byte[] decipheredSharedKeyBytes = decipher(key, triple.encipheredSharedKey);

        SecretKey sharedSecretKey = keyBytesToAesSecretKey(decipheredSharedKeyBytes);
        byte[] orderData = decipher(sharedSecretKey, triple.cipherText);

        boolean verified = verifySignature(orderData, triple.signature, trustStoreName, trustStorePassword, signerCertName);

        List<ClientOrder> orders;
			
			if ( verified ) {
				ObjectMapper mapper = new ObjectMapper();
				JavaType type = mapper.getTypeFactory()
						.constructCollectionType(List.class,
								ClientOrder.class);
				try {
					orders = mapper.readValue(orderData, type);
				} catch ( IOException e ) {
					throw new IOException("Error parsing order data",e);
				}
			} else {
				throw new GeneralSecurityException("Signature failed to verify.");
			}

        return orders;
	}
	
	private byte[] decipher(final Key cipherKey, final byte[] cipherText ) throws GeneralSecurityException, IOException {
		try {
			Cipher cipher = Cipher.getInstance(cipherKey.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, cipherKey);
			byte[] plaintext = cipher.doFinal(cipherText);
			return plaintext;
		} catch ( InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e ) {
			throw new GeneralSecurityException("Error encrypting data", e);
		}
	}

	
	@Override
	public void encipher(final List<ClientOrder> orders, final File orderFile,
			final String senderKeyStoreName, final char[] senderKeyStorePassword,
			final String senderKeyName, final char[] senderKeyPassword,
			final String senderTrustStoreName, final char[] senderTrustStorePassword,
			final String recipientCertName ) throws GeneralSecurityException, IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		byte[] data = mapper.writeValueAsBytes(orders);
		Tri triplet = new Tri();
		
		SecretKey sharedSecretKey = generateAesSecretKey(); 
		byte[] shareSecretKeyBytes = sharedSecretKey.getEncoded();
		
		KeyStore sendTrustStore = loadKeyStore(senderTrustStoreName,
				senderTrustStorePassword);
		PublicKey key = sendTrustStore.getCertificate(recipientCertName)
				.getPublicKey();
		
		triplet.encipheredSharedKey =  encipher(key,shareSecretKeyBytes );
		triplet.cipherText = encipher(sharedSecretKey, data );
		triplet.signature = sign( data, senderKeyStoreName, 
				senderKeyStorePassword, senderKeyName, senderKeyPassword);
		
		writeFile(orderFile, triplet);
	}

	private byte[] encipher( final Key cipherKey, final byte[] painText) 
			throws GeneralSecurityException, IOException {
		try {
			Cipher cipher = Cipher.getInstance(cipherKey.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, cipherKey);
			byte[] cipherText = cipher.doFinal(painText);
			return cipherText;
		} catch (  InvalidKeyException  | NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new GeneralSecurityException("Error when encrypting data.", e);
		}
		
	}

	private boolean verifySignature( final byte[] data, final byte[] signature, final String trustStoreName,
			final char[] trustStorePassword, final String signerPubKeyName) throws GeneralSecurityException, IOException {
		try {
			KeyStore clientTrustStore = loadKeyStore(trustStoreName,trustStorePassword );
			Signature verifier = Signature.getInstance(SIGN_ALGORITH);
			Certificate cert = clientTrustStore.getCertificate(signerPubKeyName);
			PublicKey publicKey = cert.getPublicKey();
			verifier.initVerify(publicKey);
			verifier.update(data);
			return verifier.verify(signature);
		} catch ( KeyStoreException | CertificateException | IOException e ) {
			throw new GeneralSecurityException("Unable to retrieve signing key.",e);
		} catch ( NoSuchAlgorithmException | InvalidKeyException | SignatureException e ) {
			throw new GeneralSecurityException("Invalid signing key.",e);
		}
	}

	private byte[] sign(final byte[] data,
			final String signerKeyStoreName,
			final char[] signerKeyStorePassword,
			final String signerName,
			char[] signerPassword) throws GeneralSecurityException, IOException {
		byte[] signature;
		try {
			KeyStore clientKeyStore = loadKeyStore(signerKeyStoreName,
					signerKeyStorePassword);
			PrivateKey privateKey = (PrivateKey) clientKeyStore
					.getKey(signerName, signerPassword);
			if ( privateKey == null ) {
				throw new GeneralSecurityException(String.format("No key exist for %s", signerName));
			}
			Signature signer = Signature.getInstance(SIGN_ALGORITH);
			signer.initSign(privateKey);
			signer.update(data);
			signature = signer.sign();
			return signature;
		} catch (KeyStoreException |
				UnrecoverableKeyException |
				InvalidKeyException |
				NoSuchAlgorithmException |
				CertificateException | 
				SignatureException e) {
			throw new GeneralSecurityException("Error in signing data.", e);
		}
	}
	
	public static KeyStore loadKeyStore( final String storeFile, final char[] storePasswd)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
			try (InputStream stream = ClientOrderCodec.class.getResourceAsStream(ROOT_PATH + storeFile)) {
				if (stream == null ) {
					throw new KeyStoreException( "Unable to locate key store: " + storeFile);
				}
				KeyStore keyStore = KeyStore.getInstance(JCEKS); 
				keyStore.load(stream, storePasswd);
				return keyStore;
			}
			
	}
	
	public static SecretKey generateAesSecretKey() throws NoSuchAlgorithmException {
		KeyGenerator generator = KeyGenerator.getInstance(AES_ALGORITHM);
		generator.init(AES_KEY_SIZE);
		SecretKey key = generator.generateKey();
		return key;
		}
	
	public static SecretKey keyBytesToAesSecretKey(final byte[] key) throws NoSuchAlgorithmException {
			KeyGenerator generator = KeyGenerator.getInstance(AES_ALGORITHM);
			generator.init(AES_KEY_SIZE);
		SecretKey secKey = new SecretKeySpec(key, 0, 
				AES_KEY_LENGTH, AES_ALGORITHM);
		return secKey;
		}
		
    private static byte[] readByteArray(DataInputStream in) throws  IOException {
        byte[] bytes = null;
        int len = in.readInt();

        if (len >= 0) {
            bytes = new byte[len];
            in.readFully(bytes);
        }
        return bytes;
    }
    
    private static void writeByeArray(DataOutputStream dos,
            byte[] data) throws IOException {
    	int len = (data == null) ? -1 : data.length;
    	dos.writeInt(len);

    	if (len > 0) {
    	dos.write(data);
    }
}

    private static void writeFile(File orderFile, Tri triple) throws IOException {
        try(FileOutputStream fout = new FileOutputStream(orderFile);
            DataOutputStream dataOutputStream = new DataOutputStream(fout)) {
            writeByeArray(dataOutputStream, triple.encipheredSharedKey);
            writeByeArray(dataOutputStream, triple.cipherText);
            writeByeArray(dataOutputStream, triple.signature);
            dataOutputStream.flush();
        } catch (IOException io) {
            throw new IOException("Error attempting to write orders file.", io);
        }
    }
    
    private static Tri readFile(File orderFile) throws IOException {

        try(FileInputStream inputStream = new FileInputStream(orderFile);
        DataInputStream dataIn = new DataInputStream(inputStream)) {

            Tri triple = new Tri();
            triple.encipheredSharedKey = readByteArray(dataIn);
            triple.cipherText = readByteArray(dataIn);
            triple.signature = readByteArray(dataIn);
            return triple;
        } catch (IOException e) {
            throw new IOException("Error reading data file", e);
        }
    }
    
}

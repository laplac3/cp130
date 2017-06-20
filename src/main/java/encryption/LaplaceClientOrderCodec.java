package encryption;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import edu.uw.ext.framework.order.ClientOrder;
import edu.uw.ext.framework.order.ClientOrderCodec;

public class LaplaceClientOrderCodec implements ClientOrderCodec {

	private PbeCipherText encText;
	@Override
	public List<ClientOrder> decipher(File arg0, String arg1, char[] arg2, String arg3, char[] arg4, String arg5,
			char[] arg6, String arg7) throws GeneralSecurityException, IOException {
		
		return null;
	}

	@Override
	public void encipher(List<ClientOrder> orders, File arg1, String arg2, char[] arg3, String arg4, char[] arg5,
			String arg6, char[] arg7, String arg8) throws GeneralSecurityException, IOException {
		
		
	}

	public static KeyStore loadKeyStore(String storeType, char[] storePasswd, InputStream inputStream)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
			KeyStore keyStore = KeyStore.getInstance(storeType); keyStore.load(inputStream, storePasswd);
			return keyStore;
	}
	
	public static KeyStore loadKeyStore(String storeFile, String storeType, char[] storePasswd)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		
			try (InputStream stream = ClassLoader.class.getClassLoader(). getResourceAsStream(storeFile)) {
				return loadKeyStore(storeType, storePasswd, stream);
			}
			
	}
	
	public static SecretKey generateAesSecretKey() throws NoSuchAlgorithmException {
		KeyGenerator generator = KeyGenerator.getInstance("AES"); generator.init(128);
		SecretKey key = generator.generateKey();
		return key;
		}
	public static SecretKey keyBytesToAesSecretKey(final byte[] key) throws NoSuchAlgorithmException {
			KeyGenerator generator = KeyGenerator.getInstance("AES"); generator.init(128);
		SecretKey secKey = new SecretKeySpec(key, 0, 16, "AES"); return secKey;
		}
		
	private static byte[] doHash(final byte[] password, final byte[] salt) throws NoSuchAlgorithmException {
			MessageDigest md = MessageDigest.getInstance(ALGORITHM); md.update(salt);
			md.update(password);
			byte[] digest = md.digest();
			return digest;
		}
		
	public static void storePassword(final byte[] password, final DataOutputStream dest)
			throws PasswordException {
			try {
				if (password == null || password.length == 0 || dest == null) {
					throw new IllegalArgumentException("Invalid paramter(s).");
					}
				byte[] salt = new byte[SALT_SIZE]; new SecureRandom().nextBytes(salt); byte[] hash = doHash(password, salt); dest.write(salt); dest.writeShort(hash.length); dest.write(hash);
				} catch (NoSuchAlgorithmException | IOException e) { 
					throw new PasswordException("Unable to persist password representation.", e);
				} finally {
					// Zero-ize the the provided password Arrays.fill(password, (byte)0);
					Arrays.fill(password, (byte)0);
				}
			}

		public static boolean verifyPassword(final byte[] password, final DataInputStream src)
				throws PasswordException { 
			try {
				if (password == null || password.length == 0 || src == null) { 
					throw new IllegalArgumentException("Invalid paramter(s).");
				}
					byte[] salt = new byte[SALT_SIZE]; src.readFully(salt);
					short len = src.readShort();
					byte[] hashAuth = new byte[len]; src.readFully(hashAuth);
					byte[] hash = doHash(password, salt);
					return MessageDigest.isEqual(hashAuth, hash);
				} catch (NoSuchAlgorithmException | IOException e) {
					throw new PasswordException("Unable to evaluate password.", e);
				} finally {
				// Zero-ize the the provided password Arrays.fill(password, (byte)0);
					Arrays.fill(password, (byte)0);
				} 
		}
	
}

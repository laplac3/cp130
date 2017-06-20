package encryption;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PbeCipher {
	
	private static int ITERATION_CNT = Short.MAX_VALUE;
	private static int KEY_SIZE = 256;
	private static String PBK_ALGORITHM = "PBKDF2WithHmacSHA256";
	private static String CIPHER = "AES/CBC/PKCS5Padding";
	private static String KEY_ALGORITHM = "AES";
	
	private static SecretKeySpec deriveKey(final char[] password, final byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory =
					SecretKeyFactory.getInstance(PBK_ALGORITHM);
		PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATION_CNT, KEY_SIZE);
		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(),KEY_ALGORITHM);
				return secret;
	}
	
	public static PbeCipherText encrypt(final byte[] plaintext, final char[] password)
			throws Exception { // simplified for space
			byte[] salt = new byte[8];
			new SecureRandom().nextBytes(salt);;
			SecretKey secret = deriveKey(password, salt);
			Cipher cipher = Cipher.getInstance(CIPHER);
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			byte[] ciphertext = cipher.doFinal(plaintext);
			byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
			return new PbeCipherText(salt, iv, ciphertext);
	}
	
	public static byte[] decrypt(final PbeCipherText ciphertext, final char[] password)
			throws Exception { 
				
			SecretKey secret = deriveKey(password, ciphertext.getSalt());
			Cipher cipher = Cipher.getInstance(CIPHER); cipher.init(Cipher.DECRYPT_MODE, secret,
					new IvParameterSpec(ciphertext.getIv()));
			byte[] plaintext = cipher.doFinal(ciphertext.getCiphertext());
			return plaintext ;
	}
}

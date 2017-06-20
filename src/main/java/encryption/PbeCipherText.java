package encryption;

import java.util.Arrays;

public class PbeCipherText {
	private byte[] iv;
	private byte[] salt; private byte[] ciphertext;
	
	public PbeCipherText(byte[] salt, byte[] iv, byte[] ciphertext) { 
		this.salt = salt;
		this.iv = iv;
		this.ciphertext = ciphertext;
		
	}

	public byte[] getIv() { 
		return iv; 
	}
	

	public byte[] getCiphertext() { 
		return ciphertext;
	} 
	
	public byte[] getSalt() { 
		return salt; 
	}

	public void clear() {
		 Arrays.fill(salt, (byte)0);
		 salt = null;
		 Arrays.fill(iv, (byte)0);
		 iv = null; Arrays.fill(ciphertext, (byte)0); ciphertext = null;
	}
		
}

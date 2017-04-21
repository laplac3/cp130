package edu.uw.nan.dao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
 

public class SerUtil {
	
	private static final String NULL_STR = "<null>";
	
	private SerUtil() {
		
	}
	
	public static void writeByteArray( final DataOutputStream out, final byte[] bytes ) throws IOException {
		final int length = bytes == null ? -1 : bytes.length;
		out.writeInt(length);
		
		if ( length > 0) {
			out.write(bytes);
		}
	}
	
	public static byte[]  readByteArray( final DataInputStream in ) throws IOException {
		byte[] bytes = null;
		final int length = in.readInt();
		
		if ( length >= 0) {
			bytes = new byte[length];
			in.readFully(bytes);
		}
		return bytes;
	}

	public static void writeString(final DataOutputStream dos, final String str) throws IOException {
		dos.writeUTF(str == null ? NULL_STR : str);
	}
	
	public static String readString( final DataInputStream in ) throws IOException {
		final String str = in.readUTF();
		return NULL_STR.equals(str) ? null : str;
	}
}

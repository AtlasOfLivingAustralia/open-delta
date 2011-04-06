package au.org.ala.delta.io;

import java.nio.charset.Charset;


/**
 * To maintain compatibility with existing DELTA data sets, Strings stored in binary files will always
 * be stored using the Cp1252 character encoding. 
 */
public class BinFileEncoding {

	public static final Charset BIN_FILE_CHAR_ENCODING = Charset.forName("Cp1252");
	
	public static byte[] encode(String str) {
		return str.getBytes(BIN_FILE_CHAR_ENCODING);
	}
	
	public static String decode(byte[] bytes) {
		return new String(bytes, BIN_FILE_CHAR_ENCODING);
	}
	
	public static String decode(byte[] bytes, int offset, int length) {
		return new String(bytes, offset, length, BIN_FILE_CHAR_ENCODING);
	}
}

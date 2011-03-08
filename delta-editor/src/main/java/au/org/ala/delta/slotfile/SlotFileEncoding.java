package au.org.ala.delta.slotfile;

import java.nio.charset.Charset;


/**
 * To maintain compatibility with existing DELTA data sets, Strings stored in the slot file will always
 * be stored using the Cp1252 character encoding. 
 */
public class SlotFileEncoding {

	public static final Charset SLOT_FILE_CHAR_ENCODING = Charset.forName("Cp1252");
	
	public static byte[] encode(String str) {
		return str.getBytes(SLOT_FILE_CHAR_ENCODING);
	}
	
	public static String decode(byte[] bytes) {
		return new String(bytes, SLOT_FILE_CHAR_ENCODING);
	}
	
	public static String decode(byte[] bytes, int offset, int length) {
		return new String(bytes, offset, length, SLOT_FILE_CHAR_ENCODING);
	}
}

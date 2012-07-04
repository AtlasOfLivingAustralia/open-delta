/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.io;

import java.nio.charset.Charset;


/**
 * To maintain compatibility with existing DELTA data sets, Strings stored in binary files will always
 * be stored using the Cp1252 character encoding.
 *
 * Strings stored in the SlotFile are RTF encoded, allowing unicode code points to be represented as a
 * character sequence, e.g. "\u1234".  Hence characters not representable in Cp1252 can be stored in the slot file,
 * just not in a particularly efficient manner.
 */
public class BinFileEncoding {

	public static final Charset BIN_FILE_CHAR_ENCODING = Charset.forName("Cp1252");
	
	public static byte[] encode(String str) {
		if (str == null) {
			return new byte[0];
		}
		return str.getBytes(BIN_FILE_CHAR_ENCODING);
	}
	
	public static String decode(byte[] bytes) {
		return new String(bytes, BIN_FILE_CHAR_ENCODING);
	}
	
	public static String decode(byte[] bytes, int offset, int length) {
		return new String(bytes, offset, length, BIN_FILE_CHAR_ENCODING);
	}
}

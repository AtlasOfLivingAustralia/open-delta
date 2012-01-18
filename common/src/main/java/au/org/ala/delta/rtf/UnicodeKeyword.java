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
package au.org.ala.delta.rtf;

import java.io.IOException;

/**
 * A unicode keyword knows how to handle an RTF keyword of the form:
 * 
 * <code>\\u<code point><ascii representation></code>
 * e.g. <code>\u3456? 
 * 
 * It is supposed to skip n characters after reading the parameter where n is the current value
 * of the \\uc keyword.  For this first cut, I am saying n=1, which is the default and should work for most
 * cases.
 */
public class UnicodeKeyword extends SpecialKeyword {
	public UnicodeKeyword(String keyword) {
		super(keyword);
	}
	
	/**
	 * Converts the supplied param to a code point then reads and discards the next character from the stream.
	 * Note that the RTF spec expects that parameters to keywords are 16 bit signed integers.  This means
	 * that a negative parameter is used to represent code points above 0x7FFF.  
	 * Not sure what happens to code points above FFFF though, probably not supported by RTF).
	 */
	public char[] process(int param, RTFReader reader) throws IOException {
		
		
		if ((param < 0) && (param >= Short.MIN_VALUE)) { 
			// in this case the value has been written as a signed 16 bit number.  We need to convert to an
			// unsigned value as negative code points are invalid.
			param = (short)param & 0xFFFF;
		}
		
		// Convert the code point to one or more characters.
		char[] characters = Character.toChars(param);
		
		// skip the next character, or the next keyword. (e.g. if the
		// replacement char is > 127 the RTF may be : \u1234\'3f )
		int ch = reader.read();
		if (ch == '\\') {
			ch = reader.read();
			while (ch >= 0 && ch != ' ' && ch != '\\' && ch != '}' && ch != '{') {
				ch = reader.read();
			}
			if (ch >= 0 && ch != ' ') {
				reader.unread(ch);
			}
		}
		
		
		return characters;
		
	}
}

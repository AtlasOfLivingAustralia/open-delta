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
import java.io.PushbackReader;
import java.io.StringReader;

public class FontTableParser {

	public static void parse(String fonttable, FontTableHandler handler) {
		int nestlevel = 0;
		RTFFontInfo font = null;
		StringBuilder nameBuffer = new StringBuilder();
		PushbackReader reader = new PushbackReader(new StringReader(fonttable));
		try {
			int iChar = reader.read();
			while (iChar >= 0) {
				if (iChar >= 0) {
					char ch = (char) iChar;
					switch (ch) {
					case '{':
						nestlevel++;
						if (nestlevel == 1) {
							font = new RTFFontInfo();
							nameBuffer = new StringBuilder();
						}
						break;
					case '}':
						nestlevel--;
						if (nestlevel == 0) {
							if (handler != null) {
								font.setName(nameBuffer.toString());
								handler.onFont(font);
							}
						} else if (nestlevel < 0) {
							throw new RuntimeException("Invalid font table, unmatched '}'");
						}
						break;
					case '\\':
						handleKeyword(reader, font);
						break;
					case ';':
						break;
					default:
						nameBuffer.append(ch);
					}
				}
				iChar = reader.read();
			}
		} catch (IOException ioex) {
			throw new RuntimeException(ioex);
		}

	}

	private static void handleKeyword(PushbackReader reader, RTFFontInfo font) throws IOException {
		
		if (font == null) {
			return;
		}

		StringBuilder keywordBuffer = new StringBuilder();
		StringBuilder paramBuffer = new StringBuilder();

		StringBuilder bucket = keywordBuffer;
		int iChar = reader.read();
		while (iChar >= 0) {

			char ch = (char) iChar;

			if (Character.isLetter(ch)) {
				bucket.append(ch);
			} else if (Character.isDigit(ch) || ch == '-') {
				bucket = paramBuffer;
				bucket.append(ch);
			} else if (ch == ' ') {
				// bucket.append(ch); // Space belongs to control word, but is trimmed off...
				break;
			} else {
				reader.unread(ch);
				break;
			}

			iChar = reader.read();
		}

		String controlWord = keywordBuffer.toString();
		String param = paramBuffer.toString();

		if (controlWord.equalsIgnoreCase("f")) {
			int index = Integer.parseInt(param);
			font.setIndex(index);
		} else if (controlWord.equalsIgnoreCase("froman")) {
			font.setName("Times New Roman");
		} else if (controlWord.equalsIgnoreCase("fswiss")) {
			font.setName("Arial");
		} else if (controlWord.equalsIgnoreCase("fmodern")) {
			font.setName("Courier");
		} else if (controlWord.equalsIgnoreCase("ftech")) {
			font.setName("Symbol");
		}

	}

	public interface FontTableHandler {
		void onFont(RTFFontInfo font);
	}

	public static class RTFFontInfo {
		private int _index = -1;
		private String _name;

		public RTFFontInfo() {
		}

		public int getIndex() {
			return _index;
		}

		void setIndex(int index) {
			_index = index;
		}

		public String getName() {
			return _name;
		}

		void setName(String name) {
			_name = name;
		}
	}
}

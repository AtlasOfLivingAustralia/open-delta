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
package au.org.ala.delta.util;

import java.awt.Dimension;
import java.awt.Window;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.swing.JFrame;

public class Utils {
	
	public static void centreWindow(Window c, JFrame frame) {
		Dimension app = frame.getSize();
		int x = frame.getX() + (app.width - c.getWidth()) / 2;
		int y = frame.getY() + (app.height - c.getHeight()) / 3;
		if (y < frame.getY()) {
			y = frame.getY();
		}
		c.setLocation(x, y);
	}

	public static String truncate(String str, int length) {
		if (str == null) {
			return "";
		}

		if (str.length() > length) {
			return ".." + str.substring(str.length() - (length - 2));
		} else {
			return str;
		}
	}

	public static short LOWORD(int dword) {
		return (short) (dword & 0x0000ffff);
	}

	public static short HIWORD(int dword) {
		return (short) ((dword & 0xffff0000) >> 16);
	}

	public static long dateToFILETIME(Date d) {
		return (d.getTime() + 11644473600000L) * 10000L;
	}

	public static Date FILETIMEToDate(long FILETIME) {
		return new Date((FILETIME / 10000L) - 11644473600000L);
	}
	
	public static int strtol(String buf) {
		return strtol(buf, null, 10);
	}
	
	public static int strtol(String buf, int[] endpos) {
		return strtol(buf, endpos, 10);
	}
	
	public static int strtol(String buf, int[] endpos, int radix) {
		StringBuffer digits = new StringBuffer();
		int i = 0;
		for (; i < buf.length(); ++i) {
			char ch = buf.charAt(i);
			if (Character.isDigit(ch)) {
				digits.append(ch);
			} else {
				break;
			}
		}
		
		if (endpos != null && endpos.length > 0) {
			endpos[0] = i;
		}
		
		if (digits.length() > 0) {				
			return Integer.parseInt(digits.toString(), radix);
		} else {
			return 0;
		}
		
	}

	public static byte[] RTFToUTF8(String text) {
		// Same as RTFToANSI, overall. But returns a string of UTF8 encoded Unicode,
		// rather than ANSI.
		// We first build up a UCS2 "wide" string, then convert it to UTF8
		boolean hadControl = false;

		char[] wideBuf = new char[text.length()];

		int outPos = 0;
		if (text.length() > 0) {
			int[] RTFstart = new int[] { 0 };
			int[] RTFnext = new int[] { 0 };
			int prevStart;
			do {
				prevStart = RTFstart[0] = RTFnext[0];
				char aChar = GetNextChar(text, RTFstart, RTFnext);
				if (aChar != 0) {
					wideBuf[outPos++] = aChar;
				}
				if (!hadControl && RTFstart[0] != prevStart) {
					hadControl = true;
				}
			} while (RTFnext[0] < (int) text.length());
		}

		String str = new String(wideBuf);
		try {
			return str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String RTFToANSI(String text) {

		boolean hadControl = false;

		StringBuffer out = new StringBuffer();

		if (text.length() > 0) {
			int[] RTFstart = new int[] { 0 };
			int[] RTFnext = new int[] { 0 };
			// int outPos = 0;
			int prevStart;
			do {
				prevStart = RTFstart[0] = RTFnext[0];
				char aChar = (char) LOWORD(GetNextChar(text, RTFstart, RTFnext));
				if (aChar != 0) {
					out.append(aChar);
				}
				if (!hadControl && RTFstart[0] != prevStart)
					hadControl = true;
			} while (RTFnext[0] < (int) text.length());
		}
		return out.toString();
	}

	public static class NextCharResult {
		public int retVal;
		public int startPos;
		public int endPos;
	}

	public static class RTFcmdReplace {
		public RTFcmdReplace(String a, String b, char c) {
			cmdString = a;
			repString = b;
			unicodeValue = c;
		}

		public String cmdString;
		public String repString;
		public char unicodeValue;
	}

	static RTFcmdReplace[] RTFreps = new RTFcmdReplace[] { new RTFcmdReplace("par", "\r\n", (char) 0x0d), new RTFcmdReplace("line", "\r\n", (char) 0x0b), new RTFcmdReplace("tab", "\t", (char) 0x09),
			new RTFcmdReplace("page", "\f", (char) 0x0c), new RTFcmdReplace("lquote", "\221", (char) 0x2018), // 145
																												// ANSI
			new RTFcmdReplace("rquote", "\222", (char) 0x2019), // 146
			new RTFcmdReplace("ldblquote", "\223", (char) 0x201c), // 147
			new RTFcmdReplace("rdblquote", "\224", (char) 0x201d), // 148
			new RTFcmdReplace("bullet", "\225", (char) 0x2022), // 149
			new RTFcmdReplace("endash", "\226", (char) 0x2013), // 150 UGH!
																// Microsoft got
																// it
			new RTFcmdReplace("emdash", "\227", (char) 0x2014), // 151 wrong
																// again! When
																// Vers. 2
			new RTFcmdReplace("enspace", " ", (char) 0x2002), // RichEdit
																// controls
																// stream these
																// in with this
																// form,
			new RTFcmdReplace("emspace", " ", (char) 0x2003) // they're
																// converted to
																// plain ol'
																// dashes and
																// spaces
	};

	static int nRTFCmds = RTFreps.length;

	static String[] skipWords = new String[] { "author",
			// "buptim",
			"colortbl",
			// "comment",
			// "company",
			// "creatim",
			// "doccomm",
			"fonttbl",
			// "footer",
			// "footerf",
			// "footerl",
			// "footerr",
			// "footnote",
			// "ftncn",
			// "ftnsep",
			// "header",
			// "headerf",
			// "headerl",
			// "heaerr",
			// "info",
			// "keywords",
			// "listtable",
			// "operator",
			// "pict",
			// "printim",
			// "private1",
			// "revtim",
			// "rxe",
			// "stylesheet",
			"subject",
			// "tc",
			"title",
	// "txe",
	// "xe"
	};

	static int nSkipWords = skipWords.length;

	// Obtain the next single printable character encoded in an RTF string
	// Begins the search at the position passed in "startPos".
	// Returns the value of the character. If the character was in Unicode,
	// the low word of the result gives its Unicode value, and the high word
	// its "substitution" value.
	// On return, "startPos" points to the position at which the encoding of
	// the character began (which might be on an RTF \), and "endPos" returns
	// the position immediately after the end of the character's encoding.
	// This will be trivial for most text, (where "startPos" will point to the
	// character, and "endPos" to the next position), but for Unicode characters
	// or other
	// "special" characters (like \lquote), it gets more complicated.
	// Note that the calling function must be careful about setting startPos
	// correctly.
	// If it gets things wrong, parts of RTF command strings might look like
	// text.
	public static char GetNextChar(String RTFString, int[] startPos, int[] endPos) {
		char result = 0;
		int skipLevel = 0;
		endPos[0] = RTFString.length();
		while (result == 0 && startPos[0] < endPos[0]) {
			char ch = RTFString.charAt(startPos[0]);
			if (ch == '{' || ch == '}') {
				++startPos[0];
				if (skipLevel != 0) {
					if (ch == '{') {
						++skipLevel;
					} else {
						--skipLevel;
					}
				}
			} else if (skipLevel != 0) {
				++startPos[0];
			} else if (ch == '\\') {
				int cmdStart = startPos[0] + 1;
				if (cmdStart >= endPos[0]) {
					// A pathological case - not actually good RTF

					result = ch;
				} else {
					ch = RTFString.charAt(cmdStart);
					if (Character.isLetter(ch)) {
						int[] curPos = new int[] { cmdStart };
						while (++curPos[0] < endPos[0] && Character.isLetter(RTFString.charAt(curPos[0]))) {
						}

						String test = RTFString.substring(cmdStart, cmdStart + curPos[0] - cmdStart);

						int numStart = curPos[0];
						boolean hasParam = false;
						if (curPos[0] < endPos[0] && (RTFString.charAt(curPos[0]) == '-' || Character.isDigit(RTFString.charAt(curPos[0])))) {
							hasParam = true;
							while (++curPos[0] < endPos[0] && Character.isDigit(RTFString.charAt(curPos[0]))) {
							}
						}

						if (curPos[0] < endPos[0] && RTFString.charAt(curPos[0]) == ' ') {
							++curPos[0];
						}

						for (int i = 0; i < nSkipWords; ++i) {
							if (skipWords[i] == test) {
								skipLevel = 1;
								break;
							}
						}
						if (skipLevel != 0) {

						} else if (test == "u") {
							// Actually had RTF unicode...
							result = (char) Integer.parseInt(RTFString.substring(numStart, curPos[0] - numStart));
							char ansiVal = GetNextChar(RTFString, curPos, endPos);
							curPos[0] = endPos[0];
							result |= ansiVal << 16;
						} else if (!hasParam) {
							// Currently match only parameter-less commands
							for (int i = 0; i < nRTFCmds; ++i) {
								if (RTFreps[i].cmdString == test) {
									result = RTFreps[i].unicodeValue;
									if (result > 0x100)
										result |= (char) RTFreps[i].repString.charAt(0) << 16;
								}
							}
						}
						if (result != 0) {
							// && endPos == RTFString.size())

							endPos[0] = curPos[0];
						} else {
							startPos[0] = curPos[0];
						}
					} else if (ch == '{' || ch == '}' || ch == '\\') {
						result = ch;
						endPos[0] = cmdStart + 1;
					} else if (ch == '~') {
						result = 0xa0;
						endPos[0] = cmdStart + 1;
					} else if (ch == '-') {
						result = 0xad;
						endPos[0] = cmdStart + 1;
					} else if (ch == '\'' && cmdStart + 2 < endPos[0]) {
						char[] buff = new char[3];
						buff[0] = RTFString.charAt(cmdStart + 1);
						buff[1] = RTFString.charAt(cmdStart + 2);
						buff[2] = 0;
						result = (char) Integer.parseInt(new String(buff));
						endPos[0] = cmdStart + 1 + 2;
					} else {
						result = ch;
						endPos[0] = cmdStart + 1;
					}
				}
			} else if (!Character.isISOControl(ch) || ch >= 0x80) {
				if (ch >= 0x80 && ch < 0xa0) {
					result = (char) (winANSIChars[ch - 0x80] | ch << 16);
				} else {
					result = ch;
				}
				endPos[0] = startPos[0] + 1;
			} else
				++startPos[0];
		}

		if ((result >> 16) == 0)
			result |= (result << 16);

		return result;
	}

	static char[] winANSIChars = new char[] { 0x20AC, // €
			0x81, // �?
			0x201A, // ‚
			0x192, // ƒ
			0x201E, // „
			0x2026, // …
			0x2020, // †
			0x2021, // ‡
			0x2C6, // ˆ
			0x2030, // ‰
			0x160, // Š
			0x2039, // ‹
			0x152, // Œ
			0x8D, // �?
			0x17D, // Ž
			0x8F, // �?
			0x90, // �?
			0x2018, // ‘
			0x2019, // ’
			0x201C, // “
			0x201D, // �?
			0x2022, // •
			0x2013, // –
			0x2014, // —
			0x2DC, // ˜
			0x2122, // ™
			0x161, // š
			0x203A, // ›
			0x153, // œ
			0x9D, // �?
			0x17E, // ž
			0x178 // Ÿ
	};

	public static String removeComments(String src, int level) {
		if (level == 0) {
			return src;
		}
		return src + " - Pretend this has the comments removed!";
	}

	// Strip extra spaces from a string. This means reducing multiple spaces to a
	// single space AND stripping leading and trailing spaces from comments
	public static String stripExtraSpaces(String str) {
		// TODO Needs to be done properly!
		String tmp = str.replaceAll("  ", " ");
		return tmp.trim();
		
	}
	
	public static String getVersionFromManifest() {
		String versionString = Utils.class.getPackage().getImplementationVersion();
		return versionString;
	}
}

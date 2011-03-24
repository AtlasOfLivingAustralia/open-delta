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
package au.org.ala.delta.editor.slotfile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.model.DeltaParseException;
import au.org.ala.delta.util.ArrayUtils;
import au.org.ala.delta.util.Utils;

public class Attribute implements Iterable<AttrChunk> {
	
	enum AttributeParseError {
		EAP_NULL, EAP_BAD_STATE_NUMBER, //
		EAP_UNMATCHED_CLOSEBRACK, //
		EAP_MISSING_CLOSEBRACK, EAP_BADATTR_SYMBOL, //
		EAP_IS_INAPPLICABLE, EAP_CHARTYPE_UNDEFINED, EAP_EXCLUSIVE_ERROR, EAP_BAD_NUMERIC_ORDER, EAP_BAD_RTF, EAP_BAD_RTF_BRACKET, EAP_BAD_SLASH
	};

	public static class AttributeParseException extends DeltaParseException {
		
		private static final long serialVersionUID = -6900898497848554617L;

		private AttributeParseError _error;
		private int _value;

		public AttributeParseException(AttributeParseError error, int value) {
			super(error.name(), value);
			_error = error;
			_value = value;
		}

		/**
		 * @return the _error
		 */
		public AttributeParseError getError() {
			return _error;
		}

		/**
		 * @return the _value
		 */
		public int getValue() {
			return _value;
		}
		
		@Override
		public String getMessage() {
			return _error.name();
		}
	}

	static class Delimiters {
		public static char LITERAL = '|'; // Treat next character as literal;
											// not yet implemented.
		public static char HEX = '!'; // Treat next 2 characters as Hex byte;
										// not yet implemented.
		public static char DIRECTIVE = '*'; // Start of directive
		public static char ELEMSTART = '#'; // Start of character, item, etc.
		public static char ELEMEND = '/'; // End of character, item, etc.
		public static char OPENBRACK = '<'; // Opening bracket
		public static char CLOSEBRACK = '>'; // Closing bracket
		public static char KEYWORD = '@'; //
		public static char QUOTE = '\"'; // Quotation mark
		public static char SETDELIM = ':'; // Separates character numbers (or
											// ranges) in character sets
		public static char ORSTATE = '/'; // Separates "or"ed state values
		public static char ANDSTATE = '&'; // Separates "and"ed state values
		public static char STATERANGE = '-'; // Separates a range of values
	}

	public static short MAKEWORD(byte lo, byte hi) {
		return (short) (((hi << 8) & 0xFF00) + (lo & 0x00FF));
	}

	public static int MAKEDWORD(byte[] buf, int start) {
		ByteBuffer b = ByteBuffer.wrap(buf, start, 4);
		b.order(ByteOrder.LITTLE_ENDIAN);
		return b.getInt();
	}

	private int _charId;
	private int _nChunks;

	private byte[] _data;

	public Attribute() {
		this(0);
	}

	public Attribute(int charId) {
		init(charId);
	}

	public Attribute(String text, VOCharBaseDesc charBase) {
		init(charBase != null ? charBase.getUniId() : 0);
		parse(text, charBase, false);
	}

	@Override
	public String toString() {
		return String.format("Attribute: charID=%d nChunks=%d", _charId,
				_nChunks);
	}

	public void init(int aCharId) {
		_charId = aCharId;
		_data = new byte[] {ChunkType.CHUNK_STOP};
		_nChunks = 0;

	}

	public void setData(byte[] newdata) {
		_data = newdata;
	}

	enum ParseState {
		NUMBER, VARIABLE, UNKNOWN, INAPPLICABLE, NOWHERE
	};

	public void parse(String text, VOCharBaseDesc charBase, boolean isIntkey) {
		if (charBase == null) {
			if (text.length() > 0) {
				insert(end(), new AttrChunk(text));
				return;
			}
		}
		int charType = charBase.getCharType();
		if (text.length() > 0
				&& (charType == CharType.UNKNOWN || charType >= CharType.LISTEND)) {
			throw new AttributeParseException(
					AttributeParseError.EAP_CHARTYPE_UNDEFINED, 0);
		}
		// Insert comments around text characters if they are not already present.
		if (CharType.isText(charType)) {
			if (!text.startsWith("<")) {
				text = "<"+text+">";
			}
		}

		// Ignore whether exclusive if parsing Intkey "use" directive
		boolean isExclusive = charBase
				.testCharFlag(VOCharBaseDesc.CHAR_EXCLUSIVE) && !isIntkey;

		ParseState parseState = ParseState.NOWHERE;
		int commentLevel = 0;
		int bracketLevel = 0;
		int startPos = -1;
		int textStart = -1;
		boolean onlyText = true;
		boolean inserted = false;
		boolean hadDecimal = false;
		boolean hadState = false;
		boolean pseudoOK = !isIntkey; // true; // Disallow pseudo-values if
										// Intkey "USE"
		boolean hadPseudo = false;
		boolean hadExLo = false;
		boolean hadExHi = false;
		int numbCount = 0;
		int i;
		int nHidden = 0;
		boolean inRTF = false;
		boolean inParam = false;
		DeltaNumber prevNumb = new DeltaNumber(-Float.MAX_VALUE);
		char ch;

		for (i = 0; i < text.length(); ++i) {
			ch = text.charAt(i);
			if (commentLevel == 0) {
				if (inRTF) {
					// This is not quite what's needed here, when dealing with
					// the attribute
					// editor. The editor should keep track of whether the user
					// is inside
					// a comment or not and enable/disable formatting
					// accordingly.
					// But that will be awfully hard to get right.....
					throw new AttributeParseException(
							AttributeParseError.EAP_BAD_RTF, i - nHidden);
				}
				// if (bracketLevel != 0)
				// throw TAttributeParseEx(EAP_BAD_RTF_BRACKET, i - nHidden);
				// Not really an error. But this would indicate that there are
				// RTF brackets enclosing text, rather than an RTF command
				if (ch == Delimiters.CLOSEBRACK)
					throw new AttributeParseException(
							AttributeParseError.EAP_UNMATCHED_CLOSEBRACK, i
									- nHidden);

				else if (ch == Delimiters.OPENBRACK) {
					if (isIntkey) {// Disallow comments if Intkey "use"
						throw new AttributeParseException(
								AttributeParseError.EAP_BADATTR_SYMBOL, i
										- nHidden);
					}
					++commentLevel; // We have entered a textual comment.
					textStart = i;
					if (parseState != ParseState.NOWHERE && !inserted) // If we
																		// were
																		// in
																		// the
																		// middle
																		// of
																		// "something",
					{ // first save that "something", but don't otherwise change
						// parse state

						
						switch (parseState) {
						case VARIABLE:
							insert(end(), new AttrChunk(
									ChunkType.CHUNK_VARIABLE));
							break;

						case UNKNOWN:
							insert(end(),
									new AttrChunk(ChunkType.CHUNK_UNKNOWN));
							break;

						case INAPPLICABLE:
							insert(end(), new AttrChunk(
									ChunkType.CHUNK_INAPPLICABLE));
							break;

						case NUMBER:
							if (CharType.isNumeric(charType)) {
								DeltaNumber aNumb = new DeltaNumber(substring(text, startPos, i - startPos + 1));
								if (aNumb.lessThan(prevNumb)) {
									throw new AttributeParseException(
											AttributeParseError.EAP_BAD_NUMERIC_ORDER,
											startPos - nHidden);
								}
								insert(end(), new AttrChunk(aNumb));
								prevNumb = aNumb;
							} else if (CharType.isMultistate(charType)) {
								int stateNo = Utils.strtol((substring(text, startPos, i - startPos + 1)));
								int stateId = charBase
										.uniIdFromStateNo(stateNo);
								if (stateId == VOCharBaseDesc.STATEID_NULL)
									throw new AttributeParseException(
											AttributeParseError.EAP_BAD_STATE_NUMBER,
											startPos - nHidden);
								if (isExclusive && hadState)
									throw new AttributeParseException(
											AttributeParseError.EAP_EXCLUSIVE_ERROR,
											startPos - nHidden);
								insert(end(), new AttrChunk(
										ChunkType.CHUNK_STATE, stateId));
								hadState = true;
							}
							break;

						default:
							throw new AttributeParseException(
									AttributeParseError.EAP_BADATTR_SYMBOL, i
											- nHidden);
						}
						// If we jumped here by hitting the end of the loop,
						// then let's
						// get outta here!
						// Actually, it would probably work to just let the code
						// flow
						// on, but I suppose this is slightly safer.
						if (i >= text.length())
							return;

						inserted = true;
					}
				}

				else if (ch == Delimiters.STATERANGE /* && pseudoOK */
						&& parseState == ParseState.NOWHERE) {

					// / Start test block for handling negative numerics
					if (CharType.isNumeric(charType)
							&& i < text.length() - 1
							&& (text.charAt(i + 1) == '.' || Character
									.isDigit(text.charAt(i + 1)))) {
						if (++numbCount > 3)
							throw new AttributeParseException(
									AttributeParseError.EAP_BADATTR_SYMBOL, i
											- nHidden);
						parseState = ParseState.NUMBER;
						startPos = i;
						hadDecimal = false;
					} else

					if (pseudoOK)
					// / End test block for negative numerics
					{
						parseState = ParseState.INAPPLICABLE;
						hadPseudo = true;
					} else
						throw new AttributeParseException(
								AttributeParseError.EAP_BADATTR_SYMBOL, i
										- nHidden);
				}

				else if (ch == 'U') {
					if (!(pseudoOK && parseState == ParseState.NOWHERE))
						throw new AttributeParseException(
								AttributeParseError.EAP_BADATTR_SYMBOL, i
										- nHidden);
					parseState = ParseState.UNKNOWN;
					hadPseudo = true;
				}

				else if (CharType.isText(charType))
					throw new AttributeParseException(
							AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);

				else if (ch == ',') // Should only occur at the start, after a
									// comment
				{
					if (!onlyText)
						throw new AttributeParseException(
								AttributeParseError.EAP_BADATTR_SYMBOL, i
										- nHidden);
				}

				else if (ch == 'V') {
					if (!(pseudoOK && parseState == ParseState.NOWHERE))
						throw new AttributeParseException(
								AttributeParseError.EAP_BADATTR_SYMBOL, i
										- nHidden);
					parseState = ParseState.VARIABLE;
					hadPseudo = true;
				}

				else if (ch == Delimiters.STATERANGE
						|| ch == Delimiters.ANDSTATE) {
					// Is this a pseudo-value?
					if (parseState == ParseState.NUMBER) {
						if (!inserted) {
							if (CharType.isNumeric(charType)) {
								DeltaNumber aNumb = new DeltaNumber(substring(text, startPos, i - startPos + 1));
								if (aNumb.lessThan(prevNumb))
									throw new AttributeParseException(
											AttributeParseError.EAP_BAD_NUMERIC_ORDER,
											startPos - nHidden);
								insert(end(), new AttrChunk(aNumb));
								prevNumb = aNumb;
							} else if (CharType.isMultistate(charType)) {
								int stateNo = Utils.strtol((substring(text, startPos, i - startPos + 1)));
								int stateId = charBase
										.uniIdFromStateNo(stateNo);
								if (stateId == VOCharBaseDesc.STATEID_NULL)
									throw new AttributeParseException(
											AttributeParseError.EAP_BAD_STATE_NUMBER,
											startPos - nHidden);
								if (isExclusive && hadState)
									throw new AttributeParseException(
											AttributeParseError.EAP_EXCLUSIVE_ERROR,
											startPos - nHidden);
								insert(end(), new AttrChunk(
										ChunkType.CHUNK_STATE, stateId));
								hadState = true;
							}
						}
					} else
						throw new AttributeParseException(
								AttributeParseError.EAP_BADATTR_SYMBOL, i
										- nHidden);
					if (ch == Delimiters.ANDSTATE) {
						if (isIntkey) // Disallow & if Intkey "use"
							throw new AttributeParseException(
									AttributeParseError.EAP_BADATTR_SYMBOL, i
											- nHidden);
						insert(end(), new AttrChunk(ChunkType.CHUNK_AND));
						numbCount = 0;
						prevNumb = new DeltaNumber(-Float.MAX_VALUE);
						hadExLo = false; // /
						hadExHi = false; // /
					} else if (ch == Delimiters.STATERANGE) {
						insert(end(), new AttrChunk(ChunkType.CHUNK_TO));
					}
					parseState = ParseState.NOWHERE;
					inserted = false;
					pseudoOK = false;
				}

				else if (ch == Delimiters.ORSTATE) {
					if (parseState == ParseState.UNKNOWN)
						throw new AttributeParseException(
								AttributeParseError.EAP_BADATTR_SYMBOL, i
										- nHidden);
					if (!inserted) // If we were in the middle of "something",
					{ // first save that "something", but don't otherwise change
						// parse state
						switch (parseState) {
						case VARIABLE:
							insert(end(), new AttrChunk(
									ChunkType.CHUNK_VARIABLE));
							break;

						case UNKNOWN:
							insert(end(),
									new AttrChunk(ChunkType.CHUNK_UNKNOWN));
							break;

						case INAPPLICABLE:
							insert(end(), new AttrChunk(
									ChunkType.CHUNK_INAPPLICABLE));
							break;

						case NUMBER:
							if (CharType.isNumeric(charType)) {
								DeltaNumber aNumb = new DeltaNumber(substring(text, startPos, i - startPos + 1));
								if (aNumb.lessThan(prevNumb)) {
									throw new AttributeParseException(
											AttributeParseError.EAP_BAD_NUMERIC_ORDER,
											startPos - nHidden);
								}
								insert(end(), new AttrChunk(aNumb));
								prevNumb = aNumb;
							} else if (CharType.isMultistate(charType)) {
								int stateNo = Utils.strtol(substring(text, startPos, i -startPos + 1));
								int stateId = charBase.uniIdFromStateNo(stateNo);
								if (stateId == VOCharBaseDesc.STATEID_NULL)
									throw new AttributeParseException(
											AttributeParseError.EAP_BAD_STATE_NUMBER,
											startPos - nHidden);
								if (isExclusive && hadState)
									throw new AttributeParseException(
											AttributeParseError.EAP_EXCLUSIVE_ERROR,
											startPos - nHidden);
								insert(end(), new AttrChunk(
										ChunkType.CHUNK_STATE, stateId));
								hadState = true;
							}
							break;

						default:
							throw new AttributeParseException(
									AttributeParseError.EAP_BADATTR_SYMBOL, i
											- nHidden);
						}
					}
					insert(end(), new AttrChunk(ChunkType.CHUNK_OR));
					// Hitting an "or" resets just about everything...
					parseState = ParseState.NOWHERE;
					inserted = false;
					pseudoOK = !isIntkey; // true;
					hadPseudo = false;
					numbCount = 0;
					hadExLo = false;
					hadExHi = false;
					prevNumb = new DeltaNumber(-Float.MAX_VALUE);
				}

				else if (ch == '.'
						&& (charType != CharType.REAL || (parseState == ParseState.NUMBER && hadDecimal)))
					throw new AttributeParseException(
							AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);

				else if (ch == '.' || Character.isDigit(ch)) {
					if (hadPseudo || hadExHi)
						throw new AttributeParseException(
								AttributeParseError.EAP_BADATTR_SYMBOL, i
										- nHidden);
					if (parseState == ParseState.NOWHERE) {
						if (CharType.isText(charType)
								|| (CharType.isNumeric(charType) && ++numbCount > 3))
							throw new AttributeParseException(
									AttributeParseError.EAP_BADATTR_SYMBOL, i
											- nHidden);
						parseState = ParseState.NUMBER;
						startPos = i;
						hadDecimal = false;
					}
					if (ch == '.')
						hadDecimal = true;
					if (parseState != ParseState.NUMBER || inserted)
						throw new AttributeParseException(
								AttributeParseError.EAP_BADATTR_SYMBOL, i
										- nHidden);
				} else if (ch == '(' && !isIntkey) // Should be "extreme" low or
													// high value.
				{ // Handle this specially, since it requires multi-character
					// scanning.
					if (hadPseudo || hadExHi)
						throw new AttributeParseException(
								AttributeParseError.EAP_BADATTR_SYMBOL, i
										- nHidden);
					if (!CharType.isNumeric(charType))
						throw new AttributeParseException(
								AttributeParseError.EAP_BADATTR_SYMBOL, i
										- nHidden);
					int j;
					hadDecimal = false;
					if (numbCount > 0 && i + 1 < text.length()
							&& text.charAt(i + 1) == Delimiters.STATERANGE) // (extreme
																			// high)
					{
						if (parseState == ParseState.NUMBER) {
							if (!inserted) {
								DeltaNumber aNumb = new DeltaNumber(substring(text, startPos, i - startPos + 1));
								if (aNumb.lessThan(prevNumb)) {
									throw new AttributeParseException(
											AttributeParseError.EAP_BAD_NUMERIC_ORDER,
											startPos - nHidden);
								}
								insert(end(), new AttrChunk(aNumb));
								prevNumb = aNumb;
								inserted = true;
							}
						} else if (parseState != ParseState.UNKNOWN)
							throw new AttributeParseException(
									AttributeParseError.EAP_BADATTR_SYMBOL, i
											- nHidden);
						// insert(end(), TAttrChunk(CHUNK_TO)); // Is this
						// needed, or is it implicit in the extreme hi flag?
						pseudoOK = false;
						startPos = -1;
						for (j = i + 2; j < text.length()
								&& text.charAt(j) != ')'; ++j) {
							if (!(Character.isDigit(text.charAt(j))
									|| (text.charAt(j) == '.' && charType == CharType.REAL)

							|| (j == i + 2 && text.charAt(j) == '-')))
								throw new AttributeParseException(
										AttributeParseError.EAP_BADATTR_SYMBOL,
										j - nHidden);
							if (startPos < 0)
								startPos = j;
							if (text.charAt(j) == '.') {
								if (hadDecimal)
									throw new AttributeParseException(
											AttributeParseError.EAP_BADATTR_SYMBOL,
											j - nHidden);
								else
									hadDecimal = true;
							}
						}
						if (startPos < 0 || j == text.length())
							throw new AttributeParseException(
									AttributeParseError.EAP_BADATTR_SYMBOL,
									startPos - nHidden);
						i = j;
						DeltaNumber exhiNumb = new DeltaNumber(substring(text, startPos, i - startPos));
						if (exhiNumb.lessThan(prevNumb)) {
							throw new AttributeParseException(
									AttributeParseError.EAP_BAD_NUMERIC_ORDER,
									startPos - nHidden);
						}
						insert(end(), new AttrChunk(
								ChunkType.CHUNK_EXHI_NUMBER, exhiNumb));
						prevNumb = exhiNumb;
					} else // Ought to be the start of an extreme low value
					{
						if (parseState != ParseState.NOWHERE || hadExLo
								|| numbCount > 0)
							throw new AttributeParseException(
									AttributeParseError.EAP_BADATTR_SYMBOL, i
											- nHidden);
						startPos = -1;
						for (j = i + 1; j < text.length() - 1
								&& (text.charAt(j) != Delimiters.STATERANGE || j == i + 1); ++j) {
							if (!(Character.isDigit(text.charAt(j))
									|| (text.charAt(j) == '.' && charType == CharType.REAL) || (j == i + 1 && text
									.charAt(j) == '-')))
								throw new AttributeParseException(
										AttributeParseError.EAP_BADATTR_SYMBOL,
										j - nHidden);
							if (startPos < 0)
								startPos = j;
							if (text.charAt(j) == '.') {
								if (hadDecimal)
									throw new AttributeParseException(
											AttributeParseError.EAP_BADATTR_SYMBOL,
											j - nHidden);
								else
									hadDecimal = true;
							}
						}
						if (startPos < 0 || j == text.length() - 1
								|| text.charAt(j + 1) != ')')
							throw new AttributeParseException(
									AttributeParseError.EAP_BADATTR_SYMBOL,
									startPos - nHidden);
						i = j + 1;
						DeltaNumber exloNumb = new DeltaNumber(substring(text, startPos, i - startPos - 1));
						insert(end(), new AttrChunk(
								ChunkType.CHUNK_EXLO_NUMBER, exloNumb));
						prevNumb = exloNumb;
						hadExLo = true;
						parseState = ParseState.NOWHERE;
						inserted = false;
					}
				} else if (ch == '\\' && !isIntkey)
					throw new AttributeParseException(
							AttributeParseError.EAP_BAD_RTF, i - nHidden);
				else
					throw new AttributeParseException(
							AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);

				if (commentLevel == 0)
					onlyText = false;
			} else if (ch == Delimiters.OPENBRACK) {
				++commentLevel;
			} else if (ch == Delimiters.CLOSEBRACK && --commentLevel == 0
					&& i - textStart > 1) // Save text if length > 0
			{
				if (bracketLevel != 0)
					throw new AttributeParseException(
							AttributeParseError.EAP_BAD_RTF_BRACKET, i
									- nHidden);
				// The "+1" and "-1" strip off the outermost pair of brackets.
				int start = textStart + 1;
				int finish = i - 1;
				// Strip off any leading or trailing blanks
				while (text.charAt(start) == ' ' && start <= finish)
					++start;
				while (text.charAt(finish) == ' ' && finish >= start)
					--finish;
				if (finish >= start)
					insert(end(),
							new AttrChunk(substring(text, start, finish - start + 1)));
				// insert(end(), TAttrChunk(text.substr(textStart + 1, i -
				// textStart - 1)));
			}
			// ///
			else if (inRTF) {
				++nHidden;
				if (Character.isDigit(ch) || (!inParam && ch == '-')) {
					inParam = true;
				} else if (inParam || !Character.isLetter(ch)) {
					inParam = inRTF = false;
					if (ch == '\'' && text.charAt(i - 1) == '\\')
						++nHidden;
					else if (ch == '\\' && text.charAt(i - 1) != '\\')
						inRTF = true;
					else if (ch == '{') {
						++bracketLevel;
					}
					else if (ch != ' ')
						--nHidden;
					
				}
			} else if (ch == '{') {
				++bracketLevel;
				++nHidden;
			} else if (ch == '}') {
				--bracketLevel;
				++nHidden;
			} else if (ch == '\\') {
				++nHidden;
				inRTF = true;
				inParam = false;
			}
		}

		if (commentLevel > 0)
			throw new AttributeParseException(
					AttributeParseError.EAP_MISSING_CLOSEBRACK, i - nHidden);

		if (!inserted && !onlyText) {
			switch (parseState) {
			case VARIABLE:
				insert(end(), new AttrChunk(ChunkType.CHUNK_VARIABLE));
				break;

			case UNKNOWN:
				insert(end(), new AttrChunk(ChunkType.CHUNK_UNKNOWN));
				break;

			case INAPPLICABLE:
				insert(end(), new AttrChunk(ChunkType.CHUNK_INAPPLICABLE));
				break;

			case NUMBER:
				if (CharType.isNumeric(charType)) {
					DeltaNumber aNumb = new DeltaNumber(substring(text, startPos, i - startPos + 1));
					if (aNumb.lessThan(prevNumb)) {
						throw new AttributeParseException(
								AttributeParseError.EAP_BAD_NUMERIC_ORDER,
								startPos - nHidden);
					}
					insert(end(), new AttrChunk(aNumb));
					prevNumb = aNumb;
				} else if (CharType.isMultistate(charType)) {
					int stateNo = Utils.strtol((substring(text, startPos, i - startPos + 1)));
					int stateId = charBase.uniIdFromStateNo(stateNo);
					if (stateId == VOCharBaseDesc.STATEID_NULL)
						throw new AttributeParseException(
								AttributeParseError.EAP_BAD_STATE_NUMBER,
								startPos - nHidden);
					if (isExclusive && hadState)
						throw new AttributeParseException(
								AttributeParseError.EAP_EXCLUSIVE_ERROR,
								startPos - nHidden);
					insert(end(), new AttrChunk(ChunkType.CHUNK_STATE, stateId));
					hadState = true;
				}
				break;

			default:
				throw new AttributeParseException(
						AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
			}
		}
	}
	
	/**
	 * Mimics the behaviour of the c++ string::substr.  Main differences from String.substring are:
	 * 1) It takes a start position and length instead of start position and end position
	 * 2) It is tolerant of requests for a substring that go past the end of the string.
	 * @param source the source string
	 * @param startPos the start position in the source String
	 * @param length the desired length for the substring.
	 * @return
	 */
	private String substring(String source, int beginIndex, int length) {
		
		int endIndex = beginIndex + length;
		endIndex = Math.min(source.length(), endIndex);
		return source.substring(beginIndex, endIndex);
	}

	public int getDataLength() {
		return _data.length;
	}

	public byte[] getData() {
		return _data;
	}

	public int getNChunks() {
		return _nChunks;
	}

	public AttrIterator begin() {
		return new AttrIterator(this);
	}

	public int end() {
		return _data.length-1;
	}

	/**
	 * Inserts the supplied chunk at the nominated position in the backing byte array for this
	 * attribute.
	 * @param where the position to insert the chunk.
	 * @param chunk the chunk to insert.
	 * @return the index into the backing byte array where the next insert should go
	 */
	public int insert(int where, AttrChunk chunk) {

		ByteBuffer chunkData = null;

		switch (chunk.getType()) {
		case ChunkType.CHUNK_STOP:
			// Don't allow insert of "STOP", at least for now.
			// This should probably be interpretted as a request to
			// "delete" everything from here on...
			return where;

		case ChunkType.CHUNK_TEXT: {
			// TODO deal with character encoding here...
			byte[] stringBytes = SlotFileEncoding.encode(chunk.getString());
			int strLeng = stringBytes.length;
			chunkData = initialiseBufferForChunk(strLeng + 2/*
															 * size of unsigned
															 * short
															 */+ 1, chunk);

			chunkData.put((byte) (strLeng & 0xff));
			chunkData.put((byte) ((strLeng >> 8) & 0xff));
			chunkData.put(stringBytes);
			break;
		}

		case ChunkType.CHUNK_LONGTEXT: {
			byte[] stringBytes = SlotFileEncoding.encode(chunk.getString());
			int strLeng = stringBytes.length;

			chunkData = initialiseBufferForChunk(
					strLeng + 4/* size of int */+ 1, chunk);

			chunkData.put((byte) (strLeng & 0xff));
			chunkData.put((byte) ((strLeng >> 8) & 0xff));
			chunkData.put((byte) ((strLeng >> 16) & 0xff));
			chunkData.put((byte) ((strLeng >> 24) & 0xff));
			chunkData.put(stringBytes);
			break;
		}

		case ChunkType.CHUNK_NUMBER:
		case ChunkType.CHUNK_EXLO_NUMBER:
		case ChunkType.CHUNK_EXHI_NUMBER:
			chunkData = initialiseBufferForChunk(DeltaNumber.size() + 1, chunk);
			chunkData.put(chunk.getNumber().toBinary());
			break;

		case ChunkType.CHUNK_STATE:
			chunkData = initialiseBufferForChunk(4 + 1, chunk);
			chunkData.putInt(chunk.getStateId());
			break;

		default:
			// Just write the chunk type (e.g. ChuckType.CHUNK_TO has no data, just represents a '-')
			chunkData = initialiseBufferForChunk(1, chunk);
			break;
		}

		if (chunkData == null) {
			return where;
		}
		byte[] chunkDataArray = chunkData.array();
		_data = ArrayUtils.insert(_data, where, chunkDataArray);
		++_nChunks;
		return (where + chunkDataArray.length);

	}

	private ByteBuffer initialiseBufferForChunk(int size, AttrChunk chunk) {
		ByteBuffer chunkData = ByteBuffer.allocate(size);
		chunkData.order(ByteOrder.LITTLE_ENDIAN);
		chunkData.put((byte) chunk.getType());
		return chunkData;
	}

	public void erase(int where) {
		long start = where;
		  if (start >= _data.length - 1) { // Don't erase the terminal STOP chunk
		    return;
		  }
		  AttrIterator i = new AttrIterator(this, where);
		  i.increment();
		 
		  ArrayUtils.deleteRange(_data, where, i.getPos());
		  --_nChunks; 
	}

	public void erase(int start, int end) {
		
		  if (start >= _data.length - 1 || end > _data.length - 1) {
		    return;
		  }
		  // Count the number of chunks between start & end to keep _nchunks up to date.
		  AttrIterator i = new AttrIterator(this, start);
		  long nDel = 0;
		  while (i.getPos() < end) {
		      i.increment();
		      ++nDel;
		  }
		 
		  _data = ArrayUtils.deleteRange(_data, start, end);
		 _nChunks -= nDel;
	}

	public void setCharId(int newCharId) {
		_charId = newCharId;
	}

	public int getCharId() {
		return _charId;
	}

	public int size() {
		return _nChunks;
	}

	public boolean isTextOnly() {
		for (AttrChunk chunk : this) {
			if (!chunk.isTextChunk()) {
				return false;
			}
		}
		return true;
	}

	public boolean isSimple(VOCharBaseDesc charBase) {
		if ((_charId == 0) || (_charId != charBase.getUniId())) {
		    return false;
		}
		int charType = charBase.getCharType();
		  
		  if (CharType.isMultistate(charType)) { /// Must have only state values, in ascending order, and "or" separators.
		    
		      int lastState = 0;
		      for (AttrChunk chunk : this) {
		          int chunkType = chunk.getType();
		          if (chunkType == ChunkType.CHUNK_STATE) {
		              int stateId = chunk.getStateId();
		              if (stateId == VOCharBaseDesc.STATEID_NULL) {
		            	  return false;
		              }
		              int newState = charBase.stateNoFromUniId(stateId);
		              if (newState < lastState) {
		                return false;
		              }
		              lastState = newState;
		            }
		          else if (chunkType != ChunkType.CHUNK_OR)
		            return false;
		        }
		    }
		  else if (CharType.isNumeric(charType)) /// Must have only non-extreme numeric values, in ascending order, or range separators.
		    {
		      DeltaNumber lastValue = new DeltaNumber(-Float.MAX_VALUE, (byte)0);
		      int valueCount = 0;
		      
		      for (AttrChunk chunk : this) {
		          int chunkType = chunk.getType();
		          if (chunkType == ChunkType.CHUNK_NUMBER) {
		              DeltaNumber value = chunk.getNumber();
		              if (++valueCount > 3 || value.lessThan(lastValue)) {
		                return false;
		              }
		              lastValue = value;
		            }
		          else if (chunkType != ChunkType.CHUNK_TO) {
		            return false;
		          }
		        }
		    }
		  else if (CharType.isText(charType)) { /// Must not have any RTF codes
		    
		      for (AttrChunk chunk : this) {
		        
		          String text = chunk.getAsText(charBase);
		          if (!text.equals(Utils.RTFToANSI(text))) {
		            return false;
		          }
		        }
		    }
		  return true;
	}

	public boolean deleteState(VOCharBaseDesc charBase, int stateId) {
		throw new NotImplementedException();
	}

	public boolean getEncodedStates(VOCharBaseDesc charBase,
			Set<Integer> stateIds, short[] pseudoValues) {
		throw new NotImplementedException();
	}

	public boolean initReadData() {
		// Must be called immediately after reading raw data directly into our
		// buffer.
		// Gets count of valid "chunks" and reduces data length down to the
		// minimum needed.
		// If there are errors in the structure, the data is truncated "at" the
		// error.
		// A return value of true indicates that the length was altered.
		boolean retVal = false;
		int pos = 0;
		int leng = _data.length;

		while (pos < leng && _data[pos] != ChunkType.CHUNK_STOP) {
			switch (_data[pos]) {
			case ChunkType.CHUNK_TEXT:
				if (pos + 2 > leng) {
					// No length data. Bad news. Convert to STOP
					_data[pos--] = (byte) ChunkType.CHUNK_STOP;
				} else {
					short strLeng = MAKEWORD(_data[pos + 1], _data[pos + 2]);
					if (pos + 2 + strLeng > leng) {
						// Not enough string data.
						_data[pos--] = (byte) ChunkType.CHUNK_STOP;
					} else {
						pos += 2 + strLeng;
						++_nChunks;
					}
				}
				break;

			case ChunkType.CHUNK_LONGTEXT:
				if (pos + 4 > leng) {
					// No length data. Bad news. Convert to STOP
					_data[pos--] = ChunkType.CHUNK_STOP;
				} else {
					int strLeng = MAKEDWORD(_data, pos + 1);
					if (pos + 4 + strLeng > leng) {
						// Not enough string data.
						_data[pos--] = (byte) ChunkType.CHUNK_STOP;
					} else {
						pos += 4 + strLeng;
						++_nChunks;
					}
				}
				break;

			case ChunkType.CHUNK_STATE:
				if (pos + 4 > leng)
					_data[pos--] = ChunkType.CHUNK_STOP;
				else {
					pos += 4;
					++_nChunks;
				}
				break;

			case ChunkType.CHUNK_NUMBER:
			case ChunkType.CHUNK_EXLO_NUMBER:
			case ChunkType.CHUNK_EXHI_NUMBER:
				if (pos + 4 + 1 > leng)
					_data[pos--] = ChunkType.CHUNK_STOP;
				else {
					pos += 4 + 1;
					++_nChunks;
				}
				break;

			case ChunkType.CHUNK_VARIABLE:
			case ChunkType.CHUNK_UNKNOWN:
			case ChunkType.CHUNK_INAPPLICABLE:
			case ChunkType.CHUNK_OR:
			case ChunkType.CHUNK_AND:
			case ChunkType.CHUNK_TO:
				// case CHUNK_IMPLICIT:
				++_nChunks;
				break;

			default: // Unrecognized chunk type. Turn it into a STOP.
				_data[pos--] = ChunkType.CHUNK_STOP;
			}
			++pos;
		}
		++pos;
		if (pos < leng) {
			_data = Arrays.copyOfRange(_data, 0, pos);
			retVal = true;
		} else if (pos > leng) // Occurs if CHUNK_STOP was missing
		{
			_data = Arrays.copyOf(_data, _data.length + 1);
			_data[_data.length - 1] = ChunkType.CHUNK_STOP;
			retVal = true;
		}
		return retVal;
	}

	public String getAsText(int showComments, VOP vop) {
		VOCharBaseDesc charBase = (VOCharBaseDesc) vop.getDescFromId(_charId);
		StringBuffer dest = new StringBuffer();
		for (AttrChunk chunk : this) {
			if (showComments == 0 || !chunk.isTextChunk()
					|| CharType.isText(charBase.getCharType())) {
				dest.append(chunk.getAsText(charBase));
			}
		}
		return dest.toString();
	}
	
	
	public boolean encodesState(VOCharBaseDesc charBase, int stateId, boolean checkRanges) {
		return encodesState(charBase, stateId, checkRanges, false);
	}
	
	public boolean encodesState(VOCharBaseDesc charBase, int stateId, boolean checkRanges, boolean wasImplicit) {
		  if ((_charId == 0) || (_charId != charBase.getUniId())) {
		    return false;
		  }
		  int charType = charBase.getCharType();
		  if (!CharType.isMultistate(charType)) {
		    return false;
		  }

		  boolean isOrdered = (charType == CharType.ORDERED);
		  int rangeStart = -1;
		  boolean inRange = false;
		  boolean textOnly = true;
		  for (AttrChunk chunk : this) {
		     
		        textOnly = false;
		      if (chunk.getType() == ChunkType.CHUNK_STATE)
		        {
		          if  (chunk.getStateId() == stateId) {
		        	  return true;
		          }
		          if (isOrdered && checkRanges) {
		              if (inRange) {
		                  int startState = charBase.stateNoFromUniId(rangeStart);
		                  int endState = charBase.stateNoFromUniId(chunk.getStateId());
		                  int testState = charBase.stateNoFromUniId(stateId);
		                  if (startState > 0 &&
		                      endState > 0 &&
		                      testState > Math.min(startState, endState) &&
		                      testState < Math.max(startState, endState)) {
		                    return true;
		                  }
		                  inRange = false;
		                }
		              rangeStart = chunk.getStateId();
		            }
		        }
		      if (isOrdered && chunk.getType() == ChunkType.CHUNK_TO) {
		        inRange = true;
		      }
		    }
		  if (textOnly && charBase.getUncodedImplicit() == stateId) {
		      wasImplicit = true;
		      return true;
		  }
		  return false;
	}

	/**
	 * AttrIterator
	 * 
	 * @author baird
	 * 
	 */
	public class AttrIterator implements Iterator<AttrChunk> {

		private Attribute _owner;
		private int _pos;

		AttrIterator(Attribute owner) {
			_owner = owner;
			_pos = 0;
		}

		AttrIterator(Attribute owner, int pos) {
			_owner = owner;
			_pos = pos;
		}

		public AttrChunk get() {
			AttrChunk ret = new AttrChunk(_owner._data[_pos]);
			switch (ret.getType()) {
			case ChunkType.CHUNK_TEXT:
				short varLeng = MAKEWORD(_owner._data[_pos + 1],
						_owner._data[_pos + 2]);
				String val = SlotFileEncoding.decode(_owner._data, _pos + 3, varLeng);
				ret.setString(val);
				break;
			case ChunkType.CHUNK_LONGTEXT:
				int varleng = MAKEDWORD(_owner._data, _pos + 1);
				String str = SlotFileEncoding.decode(_owner._data, _pos + 5, varleng);
				ret.setString(str);
				break;
			case ChunkType.CHUNK_STATE:
				ret.setStateId(MAKEDWORD(_owner._data, _pos + 1));
				break;

			case ChunkType.CHUNK_NUMBER:
			case ChunkType.CHUNK_EXLO_NUMBER:
			case ChunkType.CHUNK_EXHI_NUMBER:
				DeltaNumber d = new DeltaNumber();
				d.fromBinary(_owner._data, _pos + 1);
				ret.setNumber(d);
				break;

			case ChunkType.CHUNK_VARIABLE:
			case ChunkType.CHUNK_UNKNOWN:
			case ChunkType.CHUNK_INAPPLICABLE:
			case ChunkType.CHUNK_OR:
			case ChunkType.CHUNK_AND:
			case ChunkType.CHUNK_TO:
				// case CHUNK_IMPLICIT:
				break;

			default:
				// ERROR. Should probably throw something....
				break;
			}

			return ret;
		}

		public boolean increment() {
			switch (_owner._data[_pos]) {
			case ChunkType.CHUNK_STOP:
				return false;
			case ChunkType.CHUNK_TEXT:
				_pos += 2 + MAKEWORD(_owner._data[_pos + 1],
						_owner._data[_pos + 2]);
				;
				break;
			case ChunkType.CHUNK_LONGTEXT:
				_pos += 4 + MAKEDWORD(_owner._data, _pos + 1);
				break;

			case ChunkType.CHUNK_STATE:
				_pos += 4;
				break;

			case ChunkType.CHUNK_NUMBER:
			case ChunkType.CHUNK_EXLO_NUMBER:
			case ChunkType.CHUNK_EXHI_NUMBER:
				_pos += DeltaNumber.size();
				break;

			case ChunkType.CHUNK_VARIABLE:
			case ChunkType.CHUNK_UNKNOWN:
			case ChunkType.CHUNK_INAPPLICABLE:
			case ChunkType.CHUNK_OR:
			case ChunkType.CHUNK_AND:
			case ChunkType.CHUNK_TO:
				// case CHUNK_IMPLICIT:
				break;

			default:
				// ERROR. Should probably throw something.
				break;
			}
			++_pos;
			return true;
		}

		public byte getChunkType() {
			return _owner._data[_pos];
		}

		public boolean isTextChunk() {
			byte type = getChunkType();
			return type == ChunkType.CHUNK_TEXT
					|| type == ChunkType.CHUNK_LONGTEXT;
		}

		@Override
		public boolean hasNext() {
			if (_pos < 0 && _owner._nChunks > 0) {
				return true;
			}

			return _owner._data[_pos] != ChunkType.CHUNK_STOP;
		}

		@Override
		public AttrChunk next() {
			AttrChunk chunk = get();
			increment();
			return chunk;
		}
		
		public int getPos() {
			return _pos;
		}

		@Override
		public void remove() {
			throw new NotImplementedException();

		}

	}

	@Override
	public Iterator<AttrChunk> iterator() {
		return begin();
	}

}

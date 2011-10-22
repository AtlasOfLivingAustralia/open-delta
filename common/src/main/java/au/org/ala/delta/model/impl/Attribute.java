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
package au.org.ala.delta.model.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaParseException;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.util.Utils;

/**
 * TODO - borrowed from the Editor to save rewriting the parsing code but
 * I haven't refactored the Editor to use this yet....
 */
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

	private static final short PSEUDO_INAPPLICABLE = 0;
	private static final short PSEUDO_VARIABLE = 0;
	private static final short PSEUDO_UNKNOWN = 0;
	private static final short PSEUDO_NONE = 0;

	private Character _character;
	private int _nChunks;
	private List<AttrChunk> _chunks;

	public Attribute(Character charBase) {
		init(charBase);
	}

	public Attribute(String text, Character charBase) {
		init(charBase);
		parse(text, false);
	}

	@Override
	public String toString() {
		return String.format("Attribute: charID=%d nChunks=%d", _character, _nChunks);
	}

	public void init(Character character) {
		_character = character;
		_chunks = new ArrayList<AttrChunk>();
		_chunks.add(new AttrChunk(ChunkType.CHUNK_STOP));
	}

	enum ParseState {
		NUMBER, VARIABLE, UNKNOWN, INAPPLICABLE, NOWHERE
	};

	public void parse(String text,  boolean isIntkey) {
		
		CharacterType charType = _character.getCharacterType();
		
		// Insert comments around text characters if they are not already present.
		if (charType.isText()) {
			if (!text.startsWith("<")) {
				text = "<" + text + ">";
			}
		}

		// Ignore whether exclusive if parsing Intkey "use" directive
		boolean isExclusive = false;
		if (charType.isMultistate()) {
			MultiStateCharacter multiStateChar = (MultiStateCharacter)_character;
			isExclusive = multiStateChar.isExclusive() && !isIntkey;
		}

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
		BigDecimal prevNumb = new BigDecimal(-Float.MAX_VALUE);
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
					throw new AttributeParseException(AttributeParseError.EAP_BAD_RTF, i - nHidden);
				}
				// if (bracketLevel != 0)
				// throw TAttributeParseEx(EAP_BAD_RTF_BRACKET, i - nHidden);
				// Not really an error. But this would indicate that there are
				// RTF brackets enclosing text, rather than an RTF command
				if (ch == Delimiters.CLOSEBRACK)
					throw new AttributeParseException(AttributeParseError.EAP_UNMATCHED_CLOSEBRACK, i - nHidden);

				else if (ch == Delimiters.OPENBRACK) {
					if (isIntkey) {// Disallow comments if Intkey "use"
						throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
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
							insert(end(), new AttrChunk(ChunkType.CHUNK_VARIABLE));
							break;

						case UNKNOWN:
							insert(end(), new AttrChunk(ChunkType.CHUNK_UNKNOWN));
							break;

						case INAPPLICABLE:
							insert(end(), new AttrChunk(ChunkType.CHUNK_INAPPLICABLE));
							break;

						case NUMBER:
							if (charType.isNumeric()) {
								BigDecimal aNumb = Utils.stringToBigDecimal(substring(text, startPos, i - startPos + 1), new int[1]);
								if (aNumb.compareTo(prevNumb) < 0) {
									throw new AttributeParseException(AttributeParseError.EAP_BAD_NUMERIC_ORDER, startPos - nHidden);
								}
								insert(end(), new AttrChunk(aNumb));
								prevNumb = aNumb;
							} else if (charType.isMultistate()) {
								int stateNo = Utils.strtol((substring(text, startPos, i - startPos + 1)));
								int stateId = stateNo;
								if (stateId <= 0)
									throw new AttributeParseException(AttributeParseError.EAP_BAD_STATE_NUMBER, startPos - nHidden);
								if (isExclusive && hadState)
									throw new AttributeParseException(AttributeParseError.EAP_EXCLUSIVE_ERROR, startPos - nHidden);
								insert(end(), new AttrChunk(ChunkType.CHUNK_STATE, stateId));
								hadState = true;
							}
							break;

						default:
							throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
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
					if (charType.isNumeric() && i < text.length() - 1 && (text.charAt(i + 1) == '.' || java.lang.Character.isDigit(text.charAt(i + 1)))) {
						if (++numbCount > 3)
							throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
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
						throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
				}

				else if (ch == 'U') {
					if (!(pseudoOK && parseState == ParseState.NOWHERE))
						throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
					parseState = ParseState.UNKNOWN;
					hadPseudo = true;
				}

				else if (charType.isText())
					throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);

				else if (ch == ',') // Should only occur at the start, after a
									// comment
				{
					if (!onlyText)
						throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
				}

				else if (ch == 'V') {
					if (!(pseudoOK && parseState == ParseState.NOWHERE))
						throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
					parseState = ParseState.VARIABLE;
					hadPseudo = true;
				}

				else if (ch == Delimiters.STATERANGE || ch == Delimiters.ANDSTATE) {
					// Is this a pseudo-value?
					if (parseState == ParseState.NUMBER) {
						if (!inserted) {
							if (charType.isNumeric()) {
								BigDecimal aNumb = Utils.stringToBigDecimal(substring(text, startPos, i - startPos + 1), new int[1]);
								if (aNumb.compareTo(prevNumb) < 0)
									throw new AttributeParseException(AttributeParseError.EAP_BAD_NUMERIC_ORDER, startPos - nHidden);
								insert(end(), new AttrChunk(aNumb));
								prevNumb = aNumb;
							} else if (charType.isMultistate()) {
								int stateNo = Utils.strtol((substring(text, startPos, i - startPos + 1)));
								int stateId = stateNo;
								if (stateId <= 0)
									throw new AttributeParseException(AttributeParseError.EAP_BAD_STATE_NUMBER, startPos - nHidden);
								if (isExclusive && hadState)
									throw new AttributeParseException(AttributeParseError.EAP_EXCLUSIVE_ERROR, startPos - nHidden);
								insert(end(), new AttrChunk(ChunkType.CHUNK_STATE, stateId));
								hadState = true;
							}
						}
					} else
						throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
					if (ch == Delimiters.ANDSTATE) {
						if (isIntkey) // Disallow & if Intkey "use"
							throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
						insert(end(), new AttrChunk(ChunkType.CHUNK_AND));
						numbCount = 0;
						prevNumb = new BigDecimal(-Float.MAX_VALUE);
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
						throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
					if (!inserted) // If we were in the middle of "something",
					{ // first save that "something", but don't otherwise change
						// parse state
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
							if (charType.isNumeric()) {
								BigDecimal aNumb = Utils.stringToBigDecimal(substring(text, startPos, i - startPos + 1), new int[1]);
								if (aNumb.compareTo(prevNumb) < 0) {
									throw new AttributeParseException(AttributeParseError.EAP_BAD_NUMERIC_ORDER, startPos - nHidden);
								}
								insert(end(), new AttrChunk(aNumb));
								prevNumb = aNumb;
							} else if (charType.isMultistate()) {
								int stateNo = Utils.strtol(substring(text, startPos, i - startPos + 1));
								int stateId = stateNo;
								if (stateId <= 0)
									throw new AttributeParseException(AttributeParseError.EAP_BAD_STATE_NUMBER, startPos - nHidden);
								if (isExclusive && hadState)
									throw new AttributeParseException(AttributeParseError.EAP_EXCLUSIVE_ERROR, startPos - nHidden);
								insert(end(), new AttrChunk(ChunkType.CHUNK_STATE, stateId));
								hadState = true;
							}
							break;

						default:
							throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
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
					prevNumb = new BigDecimal(-Float.MAX_VALUE);
				}

				else if (ch == '.' && (charType != CharacterType.RealNumeric || (parseState == ParseState.NUMBER && hadDecimal)))
					throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);

				else if (ch == '.' || java.lang.Character.isDigit(ch)) {
					if (hadPseudo || hadExHi)
						throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
					if (parseState == ParseState.NOWHERE) {
						if (charType.isText() || (charType.isNumeric() && ++numbCount > 3))
							throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
						parseState = ParseState.NUMBER;
						startPos = i;
						hadDecimal = false;
					}
					if (ch == '.')
						hadDecimal = true;
					if (parseState != ParseState.NUMBER || inserted)
						throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
				} else if (ch == '(' && !isIntkey) // Should be "extreme" low or
													// high value.
				{ // Handle this specially, since it requires multi-character
					// scanning.
					if (hadPseudo || hadExHi)
						throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
					if (!charType.isNumeric())
						throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
					int j;
					hadDecimal = false;
					if (numbCount > 0 && i + 1 < text.length() && text.charAt(i + 1) == Delimiters.STATERANGE) // (extreme
																												// high)
					{
						if (parseState == ParseState.NUMBER) {
							if (!inserted) {
								BigDecimal aNumb = Utils.stringToBigDecimal(substring(text, startPos, i - startPos + 1), new int[1]);
								if (aNumb.compareTo(prevNumb) < 0) {
									throw new AttributeParseException(AttributeParseError.EAP_BAD_NUMERIC_ORDER, startPos - nHidden);
								}
								insert(end(), new AttrChunk(aNumb));
								prevNumb = aNumb;
								inserted = true;
							}
						} else if (parseState != ParseState.UNKNOWN)
							throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
						// insert(end(), TAttrChunk(CHUNK_TO)); // Is this
						// needed, or is it implicit in the extreme hi flag?
						pseudoOK = false;
						startPos = -1;
						for (j = i + 2; j < text.length() && text.charAt(j) != ')'; ++j) {
							if (!(java.lang.Character.isDigit(text.charAt(j)) || (text.charAt(j) == '.' && charType == CharacterType.RealNumeric)

							|| (j == i + 2 && text.charAt(j) == '-')))
								throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, j - nHidden);
							if (startPos < 0)
								startPos = j;
							if (text.charAt(j) == '.') {
								if (hadDecimal)
									throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, j - nHidden);
								else
									hadDecimal = true;
							}
						}
						if (startPos < 0 || j == text.length())
							throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, startPos - nHidden);
						i = j;
						BigDecimal exhiNumb = Utils.stringToBigDecimal(substring(text, startPos, i - startPos), new int[1]);
						if (exhiNumb.compareTo(prevNumb) < 0) {
							throw new AttributeParseException(AttributeParseError.EAP_BAD_NUMERIC_ORDER, startPos - nHidden);
						}
						insert(end(), new AttrChunk(ChunkType.CHUNK_EXHI_NUMBER, exhiNumb));
						prevNumb = exhiNumb;
					} else // Ought to be the start of an extreme low value
					{
						if (parseState != ParseState.NOWHERE || hadExLo || numbCount > 0)
							throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
						startPos = -1;
						for (j = i + 1; j < text.length() - 1 && (text.charAt(j) != Delimiters.STATERANGE || j == i + 1); ++j) {
							if (!(java.lang.Character.isDigit(text.charAt(j)) || (text.charAt(j) == '.' && charType == CharacterType.RealNumeric) || (j == i + 1 && text.charAt(j) == '-')))
								throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, j - nHidden);
							if (startPos < 0)
								startPos = j;
							if (text.charAt(j) == '.') {
								if (hadDecimal)
									throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, j - nHidden);
								else
									hadDecimal = true;
							}
						}
						if (startPos < 0 || j == text.length() - 1 || text.charAt(j + 1) != ')')
							throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, startPos - nHidden);
						i = j + 1;
						BigDecimal exloNumb = Utils.stringToBigDecimal(substring(text, startPos, i - startPos - 1), new int[1]);
						insert(end(), new AttrChunk(ChunkType.CHUNK_EXLO_NUMBER, exloNumb));
						prevNumb = exloNumb;
						hadExLo = true;
						parseState = ParseState.NOWHERE;
						inserted = false;
					}
				} else if (ch == '\\' && !isIntkey)
					throw new AttributeParseException(AttributeParseError.EAP_BAD_RTF, i - nHidden);
				else
					throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);

				if (commentLevel == 0)
					onlyText = false;
			} else if (ch == Delimiters.OPENBRACK) {
				++commentLevel;
			} else if (ch == Delimiters.CLOSEBRACK && --commentLevel == 0 && i - textStart > 1) // Save text if length > 0
			{
				if (bracketLevel != 0)
					throw new AttributeParseException(AttributeParseError.EAP_BAD_RTF_BRACKET, i - nHidden);
				// The "+1" and "-1" strip off the outermost pair of brackets.
				int start = textStart + 1;
				int finish = i - 1;
				// Strip off any leading or trailing blanks
				while (text.charAt(start) == ' ' && start <= finish)
					++start;
				while (text.charAt(finish) == ' ' && finish >= start)
					--finish;
				if (finish >= start)
					insert(end(), new AttrChunk(substring(text, start, finish - start + 1)));
				// insert(end(), TAttrChunk(text.substr(textStart + 1, i -
				// textStart - 1)));
			}
			// ///
			else if (inRTF) {
				++nHidden;
				if (java.lang.Character.isDigit(ch) || (!inParam && ch == '-')) {
					inParam = true;
				} else if (inParam || !java.lang.Character.isLetter(ch)) {
					inParam = inRTF = false;
					if (ch == '\'' && text.charAt(i - 1) == '\\')
						++nHidden;
					else if (ch == '\\' && text.charAt(i - 1) != '\\')
						inRTF = true;
					else if (ch == '{') {
						++bracketLevel;
					} else if (ch != ' ')
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
			throw new AttributeParseException(AttributeParseError.EAP_MISSING_CLOSEBRACK, i - nHidden);

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
				if (charType.isNumeric()) {
					BigDecimal aNumb = Utils.stringToBigDecimal(substring(text, startPos, i - startPos + 1), new int[1]);
					if (aNumb.compareTo(prevNumb) < 0) {
						throw new AttributeParseException(AttributeParseError.EAP_BAD_NUMERIC_ORDER, startPos - nHidden);
					}
					insert(end(), new AttrChunk(aNumb));
					prevNumb = aNumb;
				} else if (charType.isMultistate()) {
					int stateNo = Utils.strtol((substring(text, startPos, i - startPos + 1)));
					int stateId = stateNo;
					if (stateId <= 0)
						throw new AttributeParseException(AttributeParseError.EAP_BAD_STATE_NUMBER, startPos - nHidden);
					if (isExclusive && hadState)
						throw new AttributeParseException(AttributeParseError.EAP_EXCLUSIVE_ERROR, startPos - nHidden);
					insert(end(), new AttrChunk(ChunkType.CHUNK_STATE, stateId));
					hadState = true;
				}
				break;

			default:
				throw new AttributeParseException(AttributeParseError.EAP_BADATTR_SYMBOL, i - nHidden);
			}
		}
	}

	/**
	 * Mimics the behaviour of the c++ string::substr. Main differences from String.substring are: 1) It takes a start position and length instead of start position and end position 2) It is tolerant
	 * of requests for a substring that go past the end of the string.
	 * 
	 * @param source
	 *            the source string
	 * @param startPos
	 *            the start position in the source String
	 * @param length
	 *            the desired length for the substring.
	 * @return
	 */
	private String substring(String source, int beginIndex, int length) {

		int endIndex = beginIndex + length;
		endIndex = Math.min(source.length(), endIndex);
		return source.substring(beginIndex, endIndex);
	}

	public int getNChunks() {
		return _nChunks;
	}
	
	public int end() {
		return  _chunks.size();
	}

	/**
	 * Inserts the supplied chunk at the nominated position in the backing byte array for this attribute.
	 * 
	 * @param where
	 *            the position to insert the chunk.
	 * @param chunk
	 *            the chunk to insert.
	 * @return the index into the backing byte array where the next insert should go
	 */
	public void insert(int where, AttrChunk chunk) {
		_chunks.add(where, chunk);
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

	public boolean isSimple() {
		
		CharacterType charType = _character.getCharacterType();

		if (charType.isMultistate()) { // / Must have only state values, in ascending order, and "or" separators.

			int lastState = 0;
			for (AttrChunk chunk : this) {
				int chunkType = chunk.getType();
				if (chunkType == ChunkType.CHUNK_STATE) {
					int stateId = chunk.getStateId();
					if (stateId <= 0) {
						return false;
					}
					int newState = stateId;
					if (newState < lastState) {
						return false;
					}
					lastState = newState;
				} else if (chunkType != ChunkType.CHUNK_OR)
					return false;
			}
		} else if (charType.isNumeric()) // / Must have only non-extreme numeric values, in ascending order, or range separators.
		{
			BigDecimal lastValue = new BigDecimal(-Float.MAX_VALUE);
			int valueCount = 0;

			for (AttrChunk chunk : this) {
				int chunkType = chunk.getType();
				if (chunkType == ChunkType.CHUNK_NUMBER) {
					BigDecimal value = chunk.getNumber();
					if (++valueCount > 3 || value.compareTo(lastValue) < 0) {
						return false;
					}
					lastValue = value;
				} else if (chunkType != ChunkType.CHUNK_TO) {
					return false;
				}
			}
		} else if (charType.isText()) { // / Must not have any RTF codes

			for (AttrChunk chunk : this) {

				String text = chunk.getAsText(_character.getCharacterType().isText());
				if (!text.equals(Utils.RTFToANSI(text))) {
					return false;
				}
			}
		}
		return true;
	}

	

	public boolean getEncodedStates(List<Integer> stateIds, short[] pseudoValues) {

		stateIds.clear();
		pseudoValues[0] = PSEUDO_NONE;

		CharacterType charType = _character.getCharacterType();
		if (!charType.isMultistate()) {
			return false;
		}

		boolean isOrdered = (charType == CharacterType.OrderedMultiState);
		int rangeStart = 0;
		boolean inRange = false;
		boolean empty = true;
		
		for (AttrChunk chunk : _chunks) {
			int chunkType = chunk.getType();
			if (chunkType != ChunkType.CHUNK_STOP) {
				empty = false;
			}

			switch (chunkType) {
			case ChunkType.CHUNK_INAPPLICABLE:
				pseudoValues[0] |= PSEUDO_INAPPLICABLE;
				break;
			case ChunkType.CHUNK_VARIABLE:
				pseudoValues[0] |= PSEUDO_VARIABLE;
				break;
			case ChunkType.CHUNK_UNKNOWN:
				pseudoValues[0] |= PSEUDO_UNKNOWN;
				break;
			case ChunkType.CHUNK_STATE:
				stateIds.add(chunk.getStateId());
				if (isOrdered) {
					if (inRange) {
						int startState = rangeStart;
						int endState = chunk.getStateId();
						int aState = Math.min(startState, endState);
						if (aState > 0) {
							// a check to ensure the state IDs were valid
							endState = Math.max(startState, endState);
							// The delimiting states will have already been inserted.
							for (++aState; aState < endState; ++aState) {
								stateIds.add(aState);
							}
							inRange = false;
						}
					}
					rangeStart = chunk.getStateId();
				}
				break;
			case ChunkType.CHUNK_TO:
				if (isOrdered)
					inRange = true;
				break;
			default:
				break;
			}
		}
		// Had nothing, so use implicit value
		if (empty) {
			int implicitId = ((MultiStateCharacter)_character).getUncodedImplicitState();
			if (implicitId != 0)
				stateIds.add(implicitId);
		}
		return !stateIds.isEmpty();
	}


	public String getAsText(int showComments) {
		StringBuffer dest = new StringBuffer();
		for (AttrChunk chunk : this) {
			if (showComments == 0 || !chunk.isTextChunk() || _character.getCharacterType().isText()) {
				dest.append(chunk.getAsText(_character.getCharacterType().isText()));
			}
		}
		return dest.toString();
	}

	public boolean encodesState(int stateId, boolean checkRanges) {
		return encodesState(stateId, checkRanges, false);
	}

	public boolean encodesState(int stateId, boolean checkRanges, boolean wasImplicit) {

		boolean isOrdered = (_character.getCharacterType() == CharacterType.OrderedMultiState);
		int rangeStart = -1;
		boolean inRange = false;
		boolean textOnly = true;
		for (AttrChunk chunk : this) {

			textOnly = false;
			if (chunk.getType() == ChunkType.CHUNK_STATE) {
				if (chunk.getStateId() == stateId) {
					return true;
				}
				if (isOrdered && checkRanges) {
					if (inRange) {
						int startState = rangeStart;
						int endState = chunk.getStateId();
						int testState = stateId;
						if (startState > 0 && endState > 0 && testState > Math.min(startState, endState) && testState < Math.max(startState, endState)) {
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
		if (textOnly && ((MultiStateCharacter)_character).getUncodedImplicitState() == stateId) {
			wasImplicit = true;
			return true;
		}
		return false;
	}

	

	@Override
	public Iterator<AttrChunk> iterator() {
		return _chunks.iterator();
	}

	public boolean isUnknown() {
		
		for (AttrChunk chunk : this)
			if (chunk.getType() == ChunkType.CHUNK_UNKNOWN) {
				return true;
		}
		return false;
	}
	
	/**
	 * Returns true if any of the encoded data is inapplicable.
	 */
	public boolean isInapplicable() {
		
		for (AttrChunk chunk : this)
			if (chunk.getType() == ChunkType.CHUNK_INAPPLICABLE) {
				return true;
		}
		return false;
	}
	
	/**
	 * @return true if the only value of this attribute is "inapplicable".
	 * For example an attribute coded as 1/- is not exclusively inapplicable, either is 
	 * -<comment>.
	 * @param ignoreComment if this parameter is true -<comment> will be 
	 * regarded as exclusively inapplicable.
	 */
	public boolean isExclusivelyInapplicable(boolean ignoreComment) {
		boolean inapplicableFound = false;
		for (AttrChunk chunk : this) {
			int type = chunk.getType();
			if (type != ChunkType.CHUNK_INAPPLICABLE && type != ChunkType.CHUNK_STOP) {
				if (!ignoreComment || (type != ChunkType.CHUNK_LONGTEXT && type != ChunkType.CHUNK_TEXT)) {
				    return false;
				}
			}
			else if (type == ChunkType.CHUNK_INAPPLICABLE) {
				inapplicableFound = true;
			}
		}
		return inapplicableFound;
	}
}

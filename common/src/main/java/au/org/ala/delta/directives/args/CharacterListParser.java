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
package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.TextCharacter;

/**
 * Parses the CHARACTER LIST directive, populating the character descriptions
 * in the data set as it goes.
 */
public class CharacterListParser extends DirectiveArgsParser {

	private static Pattern FEATURE_DESCRIPTION_PATTERN = Pattern.compile("^(\\d+)\\s*[.] (.*)$", Pattern.DOTALL);

	public CharacterListParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}

	public void parse() throws ParseException {
		skipTo('#');
		int previousCharNumber = 0;
		int count = 0;
		int maxChars = getContext().getNumberOfCharacters();
		boolean[] foundChars = new boolean[maxChars];
		Arrays.fill(foundChars, false);
		
		while (_currentChar == '#') {
			// parseCharacter will consume all characters up until the last / of
			// the feature description or last state,
			Character character = parseCharacter();
			int charNumber = character.getCharacterId();
			
			if (charNumber != previousCharNumber + 1) {
				_context.addError(DirectiveError.Error.CHARACTER_OUT_OF_ORDER, _position);
			}
			if (charNumber > maxChars) {
				_context.addError(DirectiveError.Error.CHARACTER_NUMBER_TOO_HIGH, _position, maxChars);
			}
			if (foundChars[charNumber-1] == true) {
				_context.addError(DirectiveError.Error.CHARACTER_ALREADY_SPECIFIED, _position, charNumber);
			}
			if (skipWhitespace() && _currentChar != '#') {
				if (character instanceof NumericCharacter<?>) {
					_context.addError(DirectiveError.Error.TOO_MANY_UNITS, _position);
				}
				else if (character instanceof TextCharacter) {
					_context.addError(DirectiveError.Error.STATES_NOT_ALLOWED, _position);
				}
			}
			foundChars[charNumber-1] = true;
			previousCharNumber = charNumber;
			count ++;
			skipTo('#');
			
		}
		if (count != maxChars) {
			_context.addError(DirectiveError.Error.WRONG_CHARACTER_COUNT, _position, maxChars);
		}
	}

	private Character parseCharacter() throws ParseException {
		// Current char should be '#'
		assert _currentChar == '#';
		// read the next character
		readNext();
		
		String desc = readToNextEndSlashSpace();
		Matcher m = FEATURE_DESCRIPTION_PATTERN.matcher(desc);

		Character character = null;
		if (m.matches()) {
			int charId = Integer.parseInt(m.group(1));
			character = getContext().getCharacter(charId);
			String description = cleanWhiteSpace(m.group(2));
			
			character.setDescription(description);
			
			if (skipWhitespace()) {
				if (_currentChar != '#') {
					if (character instanceof MultiStateCharacter) {
						MultiStateCharacter multistateChar = (MultiStateCharacter) character;
						if (multistateChar.getNumberOfStates() == 0) {
							// If this character was not specified in the NUMBERS OF STATES 
							// directive, the default value is 2.
							multistateChar.setNumberOfStates(2);
						}
						int numStates = multistateChar.getNumberOfStates();
						boolean haveStates = java.lang.Character.isDigit(_currentChar);
						int stateId = -1;
						while (haveStates) {
							stateId = parseState(multistateChar);
							skipWhitespace();
							haveStates = java.lang.Character.isDigit(_currentChar);
						}
						if (stateId > 0 && stateId != numStates) {
							_context.addError(DirectiveError.Error.NUMBER_OF_STATES_WRONG, _position, numStates);
						}
					} else if (character instanceof NumericCharacter) {
						// we might see a units descriptor...
						@SuppressWarnings("rawtypes")
						NumericCharacter nc = (NumericCharacter) character;
						String units = readToNextEndSlashSpace();
						nc.setUnits(units);
					}
				} else {
					if (character instanceof MultiStateCharacter) {
						MultiStateCharacter msc = (MultiStateCharacter) character;
						int numStates = msc.getNumberOfStates();
						
						if (numStates > 0) {
							_context.addError(DirectiveError.Error.NUMBER_OF_STATES_WRONG, _position, numStates);
						}
					}
					
				}
			}
		} else {
			_context.addError(DirectiveError.Error.EXPECTED_CHARACTER_NUMBER, _position);
		}
		return character;
	}

	protected DeltaContext getContext() {
		return (DeltaContext)_context;
	}
	
	private int parseState(MultiStateCharacter ch) throws ParseException {
		String state = readToNextEndSlashSpace();
		Matcher m = FEATURE_DESCRIPTION_PATTERN.matcher(state);
		int stateId = -1;
		if (m.matches()) {
			stateId = Integer.parseInt(m.group(1));
			if (stateId > ch.getNumberOfStates()) {
				_context.addError(DirectiveError.Error.STATE_NUMBER_GREATER_THAN_MAX, _position, ch.getNumberOfStates());
				stateId = -1;
			}
			else {
				ch.setState(stateId, cleanWhiteSpace(m.group(2)));
			}
		} else {
			_context.addError(DirectiveError.Error.STATE_NUMBER_EXPECTED, _position);
		}
		return stateId;
	}
	

}

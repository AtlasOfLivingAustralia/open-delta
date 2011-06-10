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
package au.org.ala.delta.directives;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;

public class CharacterList extends AbstractTextDirective {

	public CharacterList() {
		super("character", "list");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARLIST;
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
		
		String data = args.getFirstArgumentText();
		
		StringReader reader = new StringReader(data);
		CharacterListParser parser = new CharacterListParser(context, reader);
		parser.parse();
	}

}

class CharacterListParser extends DirectiveArgsParser {

	private static Pattern FEAT_DESC_PATTERN = Pattern.compile("^(\\d+)\\s*[.] (.*)$", Pattern.DOTALL);

	public CharacterListParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}

	public void parse() throws Exception {
		while (skipTo('#')) {
			// parseCharacter will consume all characters up until the last / of
			// the feature description or last state,
			parseCharacter();
		}
	}

	private void parseCharacter() throws Exception {
		// Current char should be '#'
		assert _currentChar == '#';
		// read the next character
		readNext();
		
		String desc = readToNextEndSlashSpace();
		Matcher m = FEAT_DESC_PATTERN.matcher(desc);

		if (m.matches()) {
			int charId = Integer.parseInt(m.group(1));
			au.org.ala.delta.model.Character ch = _context.getCharacter(charId);
			String description = cleanWhiteSpace(m.group(2));
			try {
			ch.setDescription(description);
			}
			catch (NullPointerException e) {
				System.out.println("Breakpoint!");
			}
			if (skipWhitespace()) {
				if (_currentChar != '#') {
					if (ch instanceof MultiStateCharacter) {
						MultiStateCharacter multistateChar = (MultiStateCharacter) ch;
						if (multistateChar.getNumberOfStates() == 0) {
							// If this character was not specified in the NUMBERS OF STATES 
							// directive, the default value is 2.
							multistateChar.setNumberOfStates(2);
						}
						boolean haveStates = java.lang.Character.isDigit(_currentChar);
						while (haveStates) {
							parseState(multistateChar);
							skipWhitespace();
							haveStates = java.lang.Character.isDigit(_currentChar);
						}
					} else if (ch instanceof NumericCharacter) {
						// we might see a units descriptor...
						@SuppressWarnings("rawtypes")
						NumericCharacter nc = (NumericCharacter) ch;
						String units = readToNextEndSlashSpace();
						nc.setUnits(units);
					}
				} else {
					if (ch instanceof MultiStateCharacter) {
						MultiStateCharacter msc = (MultiStateCharacter) ch;
						if (msc.getNumberOfStates() > 0) {
							throw new RuntimeException("Expected " + msc.getNumberOfStates() + " states for character " + ch.getCharacterId() + ". Found none!");
						}

					}
				}
			}
		} else {
			throw new IllegalStateException("Invalid character feature description: " + desc);
		}

	}

	private void parseState(MultiStateCharacter ch) throws Exception {
		String state = readToNextEndSlashSpace();
		Matcher m = FEAT_DESC_PATTERN.matcher(state);
		if (m.matches()) {
			int stateId = Integer.parseInt(m.group(1));
			ch.setState(stateId, cleanWhiteSpace(m.group(2)));
		} else {
			throw new IllegalStateException("State does not match expected format!:" + state);
		}
	}
	

}

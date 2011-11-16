package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.TextCharacter;

public class CharacterListParser extends DirectiveArgsParser {

	private static Pattern FEAT_DESC_PATTERN = Pattern.compile("^(\\d+)\\s*[.] (.*)$", Pattern.DOTALL);

	public CharacterListParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}

	public void parse() throws ParseException {
		skipTo('#');
		while (_currentChar == '#') {
			// parseCharacter will consume all characters up until the last / of
			// the feature description or last state,
			Character character = parseCharacter();
			
			if (skipWhitespace() && _currentChar != '#') {
				if (character instanceof NumericCharacter<?>) {
					_context.addError(DirectiveError.Error.TOO_MANY_UNITS, _position);
				}
				else if (character instanceof TextCharacter) {
					_context.addError(DirectiveError.Error.STATES_NOT_ALLOWED, _position);
				}
			}
			skipTo('#');
		}
	}

	private Character parseCharacter() throws ParseException {
		// Current char should be '#'
		assert _currentChar == '#';
		// read the next character
		readNext();
		
		String desc = readToNextEndSlashSpace();
		Matcher m = FEAT_DESC_PATTERN.matcher(desc);

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
			throw new IllegalStateException("Invalid character feature description: " + desc);
		}
		return character;
	}

	protected DeltaContext getContext() {
		return (DeltaContext)_context;
	}
	
	private int parseState(MultiStateCharacter ch) throws ParseException {
		String state = readToNextEndSlashSpace();
		Matcher m = FEAT_DESC_PATTERN.matcher(state);
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
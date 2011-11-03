package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.MutableDeltaDataSet;

/**
 * Parses the KEY STATES directive.
 */
public class KeyStateParser extends DirectiveArgsParser {

	private MutableDeltaDataSet _dataSet;
	
	public KeyStateParser(DeltaContext context, Reader reader) {
		super(context, reader);
		_dataSet = context.getDataSet();
	}
	
	@Override
	public void parse() throws ParseException {
		_args = new DirectiveArguments();
		readNext();
		while (_currentInt >= 0) {
			skipWhitespace();
			IntRange charNums = readIds();
			
			expect(',');
			readNext();
			
			CharacterType type = getType(charNums);
			
			int state = 1;
			parseState(charNums, state, type);
			
			while (_currentChar == '/') {
				readNext();
				state++;
				parseState(charNums, state, type);
			}
			skipWhitespace();
 		}
	}

	private CharacterType getType(IntRange charNums) throws ParseException {
		CharacterType type = _dataSet.getCharacter(charNums.getMinimumInteger()).getCharacterType();
		for (int i=charNums.getMaximumInteger()+1; i<charNums.getMaximumInteger(); i++) {
			if (type != _dataSet.getCharacter(i).getCharacterType()) {
				throw new ParseException("Characters in a range must have the same type!", _position);
			}
		}
		return type;
	}

	private void parseState(IntRange characters, int state, CharacterType type) throws ParseException {
		int first = characters.getMinimumInteger();
		DirectiveArgument<Integer> arg = _args.addDirectiveArgument(first);
		arg.setValue(state);
		
		while (_currentInt >= 0 && !isWhiteSpace(_currentChar) && _currentChar != '/') {
			
			switch (type) {
			case UnorderedMultiState:
				parseUnorderedMultistateChar(arg);
				break;
			case OrderedMultiState:
				parseOrderedMultistateChar(arg);
				break;
			case IntegerNumeric:
			case RealNumeric:
				parseNumericChar(arg);
				break;
			default:
				throw new ParseException("Key state is not valid for character type "+type, _position);
			}
		}
		for (int i=first+1; i<=characters.getMaximumInteger(); i++) {
			arg = new DirectiveArgument<Integer>(arg);
			arg.setId(i);
			_args.add(arg);
		}
	}
	
	private void parseUnorderedMultistateChar(DirectiveArgument<Integer> arg) throws ParseException {
		arg.add(readInteger());
		while (_currentInt >=0 && _currentChar == '&') {
			readNext();
			arg.add(readInteger());
		}
	}
	
	private void parseOrderedMultistateChar(DirectiveArgument<Integer> arg) throws ParseException {
		IntRange states = readIds();
		arg.add(states.getMinimumInteger());
		arg.add(states.getMaximumInteger());
	}
	
	private void parseNumericChar(DirectiveArgument<Integer> arg) throws ParseException {
		if (_currentChar == '~') {
			arg.add(-Float.MAX_VALUE);
			readNext();
			arg.add(readReal());
		}
		else {
			arg.add(readReal());
			if (_currentChar == '~') {
				arg.add(Float.MAX_VALUE);
				readNext();
			}
			else {
				if (_currentChar == '-') {
					
					readNext();
					arg.add(readReal());
				}
				else {
					arg.add(arg.getData().get(0));
				}
			}
		}
	}
}

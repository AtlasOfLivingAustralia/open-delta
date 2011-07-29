package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;

/**
 * Parses the KEY STATES directive.
 */
public class KeyStateParser extends DirectiveArgsParser {

	private DeltaDataSet _dataSet;
	
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
	
	
	
	
		
//		while (GetNextWord(nextWord) > 0)
//	          {
//	            if (!ExtractUintRange(nextWord, lower, upper) ||
//	                upper < lower || upper > deltaDoc->GetNChars())
//	              throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//	            if (nextWord.length() == 0 || nextWord.charAt(0] != ',')
//	              throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//	            nextWord.erase(0, 1);
//	            if (nextWord.length() == 0)
//	              throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//
//	            TCharType charType = CHARTYPE_LISTEND;
//	            for (int i = lower; i <= upper; ++i)
//	              {
//	                TVOCharBaseDesc* charBase = GetCharBase(i);
//	                TCharType curCharType = charBase->GetCharType();
//
//	                if (curCharType != charType)
//	                  {
//	                    if (charType == CHARTYPE_LISTEND)
//	                      charType = curCharType;
//	                    else
//	                      throw new RuntimeException("TDirInOutEx(ED_MIXED_CHAR_TYPES, charBase->GetUniId())");
//	                  }
//	              }
//	            int keyState = 0;
//	            while (nextWord.length() > 0)
//	              {
//	                ++keyState;
//	                switch (charType)
//	                  {
//	                    case DirectiveArgType.CHARTYPE_UNORDERED:
//	                      {
//	                        TStateIdVector states;
//	                        int stateNo = 0;
//	                        if (!ExtractSingleUint(nextWord, stateNo))
//	                          throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//	                        states.add(stateNo);
//	                        while (nextWord.length() > 0 && nextWord.charAt(0) == '&')
//	                          {
//	                            nextWord.erase(0, 1);
//	                            if (!ExtractSingleUint(nextWord, stateNo))
//	                              throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//	                            states.add(stateNo);
//	                          }
//	                        for (int i = lower; i <= upper; ++i)
//	                          {
//	                            TVOCharBaseDesc* charBase = GetCharBase(i);
//	                            TDirArgs anArg(charBase->GetUniId());
//	                            anArg.value.SetFromValue(keyState, 0);
//	                            anArg.dataVect.resize(states.size());
//	                            for (int j = 0; j < states.size(); ++j)
//	                              {
//	                                anArg.dataVect[j].stateId = charBase->UniIdFromStateNo(states[j]);
//	                                if (anArg.dataVect[j].stateId == STATEID_NULL)
//	                                  throw new RuntimeException("TDirInOutEx(ED_BAD_STATE_NUMBER, charBase->GetUniId())");
//	                              }
//	                            args.add(anArg);
//	                          }
//	                        break;
//	                      }
//
//	                    // case DirectiveArgType.CHARTYPE_CYCLIC: This will require special handling, since a
//	                    // range of Nov-Mar is quite different from a range of Mar-Nov....
//
//	                    case DirectiveArgType.CHARTYPE_ORDERED:
//	                    case DirectiveArgType.CHARTYPE_LIST:
//	                      {
//	                        int minState, maxState;
//	                        if (!ExtractSingleUint(nextWord, minState))
//	                          throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//	                        if (nextWord.length() == 0 || nextWord.charAt(0) == '/')
//	                          maxState = minState;
//	                        else if (nextWord.charAt(0) == '-')
//	                          {
//	                            nextWord.erase(0, 1);
//	                            if (!ExtractSingleUint(nextWord, maxState))
//	                              throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//	                          }
//	                        else
//	                          throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//	                        for (int i = lower; i <= upper; ++i)
//	                          {
//	                            TVOCharBaseDesc* charBase = GetCharBase(i);
//	                            TDirArgs anArg(charBase->GetUniId());
//	                            anArg.value.SetFromValue(keyState, 0);
//	                            anArg.dataVect.resize(2);
//	                            anArg.dataVect[0].stateId = charBase->UniIdFromStateNo(minState);
//	                            anArg.dataVect[1].stateId = charBase->UniIdFromStateNo(maxState);
//	                            if (anArg.dataVect[0].stateId == STATEID_NULL ||
//	                                anArg.dataVect[0].stateId == STATEID_NULL)
//	                              throw new RuntimeException("TDirInOutEx(ED_BAD_STATE_NUMBER, charBase->GetUniId())");
//	                            args.add(anArg);
//	                          }
//	                        break;
//	                      }
//
//	                    case DirectiveArgType.CHARTYPE_INTEGER:
//	                    case DirectiveArgType.CHARTYPE_REAL:
//	                      {
//	                        TDirListDataVector dataVect;
//	                        TDeltaNumber aNumber;
//	                        dataVect.resize(2);
//	                        if (nextWord.charAt(0) == '~')
//	                          {
//	                            nextWord.erase(0, 1);
//	                            dataVect[0] = -MAXFLOAT;
//	                            if (!ExtractSingleNumber(nextWord, aNumber))
//	                              throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//	                            dataVect[1] = aNumber;
//	                          }
//	                        else
//	                          {
//	                            if (!ExtractSingleNumber(nextWord, aNumber))
//	                              throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//	                            dataVect[0] = aNumber;
//	                            if (nextWord.length() == 0 || nextWord.charAt(0) == '/')
//	                              dataVect[1] = dataVect[0];
//	                            else if (nextWord.charAt(0) == '~')
//	                              {
//	                                nextWord.erase(0, 1);
//	                                dataVect[1] = MAXFLOAT;
//	                              }
//	                            else if (nextWord.charAt(0) == '-')
//	                              {
//	                                nextWord.erase(0, 1);
//	                                if (!ExtractSingleNumber(nextWord, aNumber))
//	                                  throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//	                                dataVect[1] = aNumber;
//	                              }
//	                            else
//	                              throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//	                          }
//	                        if (dataVect[0].realNumb > dataVect[1].realNumb)
//	                          throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//	                        for (int i = lower; i <= upper; ++i)
//	                          {
//	                            TDirArgs anArg(GetCharBase(i)->GetUniId());
//	                            anArg.value.SetFromValue(keyState, 0);
//	                            anArg.dataVect = dataVect;
//	                            args.add(anArg);
//	                          }
//	                        break;
//	                      }
//
//	                    default:
//	                      throw new RuntimeException("TDirInOutEx(ED_INAPPROPRIATE_TYPE)");
//	                      //break;
//	                  }
//
//	                if (nextWord.length() > 0)
//	                  {
//	                    if (nextWord.charAt(0) == '/')
//	                      nextWord.erase(0, 1);
//	                    else
//	                      throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//	                  }
//	              }
//	          }
//	}


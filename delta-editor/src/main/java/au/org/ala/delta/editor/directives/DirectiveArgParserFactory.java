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
package au.org.ala.delta.editor.directives;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdListParser;
import au.org.ala.delta.directives.args.IdSetParser;
import au.org.ala.delta.directives.args.IdValueListParser;
import au.org.ala.delta.directives.args.IdWithIdListParser;
import au.org.ala.delta.directives.args.IntegerIdArgParser;
import au.org.ala.delta.directives.args.IntegerTextListParser;
import au.org.ala.delta.directives.args.KeyStateParser;
import au.org.ala.delta.directives.args.NumericArgParser;
import au.org.ala.delta.directives.args.PresetCharactersParser;
import au.org.ala.delta.directives.args.StringTextListParser;
import au.org.ala.delta.directives.args.TextArgParser;
import au.org.ala.delta.directives.validation.CharacterNumberValidator;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.ItemNumberValidator;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DirectiveArgParserFactory {
	
	public static DirectiveArgsParser parserFor(Directive directive, ImportContext context, String data) {
		DirectiveArgsParser parser = null;
		StringReader reader = new StringReader(data);
		switch (directive.getArgType()) {
		
		case DirectiveArgType.DIRARG_COMMENT:
		case DirectiveArgType.DIRARG_TRANSLATION:
		case DirectiveArgType.DIRARG_TEXT:
		case DirectiveArgType.DIRARG_FILE:
		case DirectiveArgType.DIRARG_NONE:
		case DirectiveArgType.DIRARG_INTKEY_INCOMPLETE:
		case DirectiveArgType.DIRARG_OTHER:
			parser = new TextArgParser(context, reader);
			break;
		case DirectiveArgType.DIRARG_INTEGER:
		case DirectiveArgType.DIRARG_REAL:
			parser = new NumericArgParser(context, reader);
			break;
		case DirectiveArgType.DIRARG_CHAR:
		case DirectiveArgType.DIRARG_ITEM:
			parser = new IntegerIdArgParser(context, reader);
			break;
		case DirectiveArgType.DIRARG_CHARLIST:
            parser = new IdListParser(context, reader, new CharacterNumberValidator(context));
            break;
		case DirectiveArgType.DIRARG_ITEMLIST:
			parser = new IdListParser(context, reader, new ItemNumberValidator(context));
			break;
		case DirectiveArgType.DIRARG_TEXTLIST:
		case DirectiveArgType.DIRARG_CHARTEXTLIST:
			parser = new IntegerTextListParser(context, reader);
			break;
		case DirectiveArgType.DIRARG_CHARINTEGERLIST:
		case DirectiveArgType.DIRARG_CHARREALLIST:
		case DirectiveArgType.DIRARG_ITEMREALLIST:
			parser = new IdValueListParser(context, reader);
			break;
		case DirectiveArgType.DIRARG_ITEMTEXTLIST:
		case DirectiveArgType.DIRARG_ITEMFILELIST:
			parser = new StringTextListParser(context, reader);
			break;
		case DirectiveArgType.DIRARG_ITEMCHARLIST:
			parser = new IdWithIdListParser(context, reader);
			break;
		case DirectiveArgType.DIRARG_KEYSTATE:
			parser = new KeyStateParser(context, reader);
			break;
		case DirectiveArgType.DIRARG_CHARGROUPS:
			parser = new IdSetParser(context, reader);
			break;
		case DirectiveArgType.DIRARG_INTKEY_ONOFF:
	        parser = new IntKeyOnOffParser(context, reader);
	        break;
		case DirectiveArgType.DIRARG_INTKEY_ITEM: 
		case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
      	case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
      	case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
        case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:
        case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST:
        case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:
        case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
        	parser = new IntKeyParser(context, reader, directive.getArgType());
        	break;
        case DirectiveArgType.DIRARG_PRESET:
        	parser = new PresetCharactersParser(context, reader);
        	break;
        default:
			throw new RuntimeException("No parser for :"+directive.joinNameComponents()+
					", type="+directive.getArgType()+", data="+data);
		}
		return parser;
	}
	
	private static class IntKeyOnOffParser extends DirectiveArgsParser {

		private static final String ON_VALUE = "On";
		private static final String OFF_VALUE = "Off";
		
		public IntKeyOnOffParser(AbstractDeltaContext context, Reader reader) {
			super(context, reader);
		}

		@Override
		public void parse() throws ParseException {
			_args = new DirectiveArguments();
			
			String value = readFully().trim();
			if (ON_VALUE.equalsIgnoreCase(value)) {
				_args.addValueArgument(new BigDecimal("1"));
			}
			else if (OFF_VALUE.equalsIgnoreCase(value) || OFF_VALUE.substring(0,2).equalsIgnoreCase(value)) {
				_args.addValueArgument(new BigDecimal("-1"));
			}
			else {
				throw DirectiveError.asException(DirectiveError.Error.ILLEGAL_VALUE_NO_ARGS, _position);
			}
		}
		
	}
	
	private static class IntKeyParser extends DirectiveArgsParser {
		enum State
	    { IN_MODIFIERS,
	      IN_TAXA,
	      IN_CHARS;
	    }
		private int _argType;
		public IntKeyParser(AbstractDeltaContext context, Reader reader, int argType) {
			super(context, reader);
			_argType = argType;
		}
		
		@Override
		public void parse() throws ParseException {
			_args = new DirectiveArguments();
			readNext();
			Pair<String, IntRange> next;
			switch (_argType) {
			case DirectiveArgType.DIRARG_INTKEY_ITEM: 
	         
				next = readIntkeyRange(false);
	            String value = next.getFirst();
	            IntRange range = next.getSecond();
	            if (StringUtils.isEmpty(value) && range == null) {
	            	return;
	            }
	            
	            if (range != null) {
	                _args.addDirectiveArgument(range.getMinimumInteger());
	            }
	            else {
	                _args.addTextArgument(value);
	            }
	            break;

	      	case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
	      	case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
	            // Get keyword, then drop through
	      		next = readIntkeyRange(true);
	      		if (StringUtils.isEmpty(next.getFirst())) {
	      			break;
	      		}
	      		_args.addTextArgument(next.getFirst());
	        case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
	        case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:
	        
	        	// TODO use to validate item or char number argument.
	            //boolean isChar = (_argType == DirectiveArgType.DIRARG_KEYWORD_CHARLIST ||
	            //                    _argType == DirectiveArgType.DIRARG_INTKEY_CHARLIST);

	            next = readIntkeyRange(false);
	            while (!empty(next)) {
	                if (StringUtils.isEmpty(next.getFirst())) {
	                  
		                for (int i : next.getSecond().toArray()) {
		                    _args.addDirectiveArgument(i);
		                }
		            }
	                else {
	              	  _args.addTextArgument(next.getFirst());
	                }
	                next = readIntkeyRange(false);
	            }
	        
	            break;
	        case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST:
	        	Pair<String[], IntRange> values = readIntkeyValuePair();
	            while (emptyIntkeyValuePair(values)) {
	            	
	            	String buffer = values.getFirst()[1];
	            	BigDecimal number = new BigDecimal("-1");
	            	if (StringUtils.isNotBlank(buffer)) {
	            		number = Utils.stringToBigDecimal(buffer, new int[1]);
	            	}
	            	String nextWord = values.getFirst()[0];
	                //TDirArgs anArg(number);
	                if (StringUtils.isEmpty(nextWord)) {
	                    for (int charNum : values.getSecond().toArray()) {
	                    	DirectiveArgument<?> arg = _args.addDirectiveArgument(charNum);
	                    	arg.setValue(number);
	                    }
	                }
	                else {
	                	DirectiveArgument<?> arg = _args.addTextArgument(nextWord); 
	                	arg.setValue(number);
	                }
	            }
	            break;
	        case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET: {

	            List<String> strings;
	            boolean grouped;
	            State curState = State.IN_MODIFIERS;
	            Pair<List<String>, Boolean> result = readIntkeyGroup();
	            while (result.getSecond() != null) {
	                strings = result.getFirst();
	                grouped = result.getSecond();
	            	if (curState == State.IN_MODIFIERS && (grouped || strings.get(0).charAt(0) != '/'))
	                    curState = State.IN_TAXA;
	                
	                for (int k = 0; k < strings.size(); ++k) {
	                    BigDecimal number = (curState == State.IN_MODIFIERS ? new BigDecimal("0.0") : (curState == State.IN_TAXA ?  new BigDecimal("-1.0") : new BigDecimal("1.0")));
	                    if (Character.isDigit(strings.get(k).charAt(0))) {
	                    	range = extractIntRange(new StringBuilder(strings.get(k)));
	                       
	                        for (int i : range.toArray()) {
	                             DirectiveArgument<?> arg = _args.addDirectiveArgument(i);
	                             arg.add(number);
	                        }
	                    }
	                    else {
	                        DirectiveArgument<?> arg = _args.addTextArgument(strings.get(k));
	                        arg.add(number);
	                      }
	                }
	                if (curState == State.IN_TAXA)  // Can only have a single taxon or group of taxa
	                    curState = State.IN_CHARS;
	            }
	            break;
	        }
	        case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
	        	values = readIntkeyValuePair();
	            while (emptyIntkeyValuePair(values)) {
	            	String nextWord = values.getFirst()[0];
	            	String buffer = values.getFirst()[1];
	                if (StringUtils.isEmpty(nextWord)) {
	                    for (int i : values.getSecond().toArray()) {
	                        DirectiveArgument<?> arg = _args.addDirectiveArgument(i);
	                    	if (StringUtils.isNotEmpty(buffer)) {
	                            arg.setAttributeText(buffer);
	                        }
	                    }
	                }
	                else { // The "character" was a keyword
	                
	            	    DirectiveArgument<?> arg = _args.addTextArgument(nextWord);
	                    if (StringUtils.isNotEmpty(buffer))  {
	                        arg.setAttributeText(buffer);
	                    }
	                }
	            }
	            break;
			}
		}
		
		// Intkey requires slightly different handling, partly because it is quite
		// acceptable for values to be absent, partly because the entity to be read might
		// be keyword strings rather than numeric values, and partly because values
		// can be enclosed in quotation marks. Relatively few combinations are considered
		// "fatal" errors.
		// If the next parameter read is a string, it is returned in 'dest';
		// if it is a numeric range or single number, the low value is returned in 'lower'
		// and the high value in 'upper'. (If only a single value is read, 'lower' and 'upper'
		// are equal.
		// isKeyword indicates whether we REQUIRE a keyword (rather than range).
		// Returns "false" if no data was available, and throws an exception
		// if a numeric range looks bad.
		protected Pair<String, IntRange> readIntkeyRange (boolean isKeyword) throws ParseException {
		  
		  skipWhitespace();
		  if (Character.isDigit(_currentChar)) {
			  return new Pair<String, IntRange>("", readIds());
		  }
		  
		  
		  String nextWord = getNextWord();
		  if (StringUtils.isEmpty(nextWord)) {
			  return new Pair<String, IntRange>("", null);
		  }
		  
		  StringBuilder tmpWord = new StringBuilder(nextWord);
		  if (tmpWord.charAt(0) == '\"') {
			  tmpWord.deleteCharAt(0);
		      boolean moreData = true;
		      while ((tmpWord.length() == 0 || tmpWord.charAt(tmpWord.length() - 1) != '\"') && moreData) {
		    	  String dataBuf = getNextWord(); 
		    	  moreData = StringUtils.isNotEmpty(dataBuf);
		    	  tmpWord.append(dataBuf);
		      }
		           
		  
		      if (tmpWord.length() > 0 && tmpWord.charAt(tmpWord.length() - 1) == '\"') {
		          tmpWord.deleteCharAt(tmpWord.length() - 1);
		      }
		    }

		  return new Pair<String, IntRange>(tmpWord.toString(), null);
		}
		
		protected String getNextWord() throws ParseException {
			StringBuilder word = new StringBuilder();
			
			while (Character.isWhitespace(_currentChar)) {
				word.append(_currentChar);
				readNext();
			}
			boolean finished = false;
			while (!finished) {
				while (_currentInt >= 0 && !Character.isWhitespace(_currentChar)) {
					word.append(_currentChar);
					readNext();
				}
				IntRange rtf = RTFUtils.markKeyword(word.toString());
				if (rtf.getMaximumInteger() < word.length()) {
					finished = true;
				}
			}
			return word.toString();
			
		}
		
		private boolean empty(Pair<String, IntRange> nextValue) {
			return StringUtils.isEmpty(nextValue.getFirst()) && nextValue.getSecond() == null;
		}
		private boolean emptyIntkeyValuePair(Pair<String[], IntRange> nextValue) {
			return StringUtils.isEmpty(nextValue.getFirst()[0]) && nextValue.getSecond() == null;
		}
		
		private Pair<String[], IntRange> readIntkeyValuePair () throws ParseException {
				  
			int quoteLoc = -1;
			int commaLoc = -1;
			StringBuilder remainder = new StringBuilder();
			StringBuilder nextWord = new StringBuilder(getNextWord());
			if (StringUtils.isEmpty(nextWord.toString())) {
				return new Pair<String[], IntRange>(new String[0], null);
			}
				    
		    if (nextWord.charAt(0) == '\"')  {  // Have a quoted string; need to find its close
				    
				nextWord.deleteCharAt(0);
				String dataBuf;
				
				while (quoteLoc == -1)  {
				    quoteLoc = nextWord.indexOf("\"");
				    if (quoteLoc != -1)  {   // Found a possible closing quote
				            
				        if (quoteLoc == nextWord.length() - 1)           // To close, it must be at end of word,
				            break;
				        else if (nextWord.charAt(quoteLoc + 1) == ',')  {// or be followed by comma				             
				            commaLoc = quoteLoc + 1;
				            break;
				        }
				        else {
				            quoteLoc = -1;
				        }
				    }
				    dataBuf = getNextWord();
				    if (StringUtils.isEmpty(dataBuf)) {
				        break;
				    }
				    nextWord.append(dataBuf);
				}
		    }
			else if (nextWord.charAt(0) != '/') { // If this is a switch, don't look for comma separator
				commaLoc = nextWord.indexOf(",");
			}

			if (commaLoc != -1) {
				remainder.append(nextWord.substring(commaLoc));
				remainder.deleteCharAt(0);
				nextWord.setLength(commaLoc);
				if (remainder.length() != 0 && remainder.charAt(0) == '\"') {
				    remainder.deleteCharAt(0);
				    String dataBuf = getNextWord();
				    while ((remainder.length() == 0 || remainder.charAt(remainder.length() - 1) != '\"') &&
				            StringUtils.isNotEmpty(dataBuf)) {            
				            remainder.append(dataBuf);
				            dataBuf = getNextWord();
				    }
				    if (remainder.length() != 0 && remainder.charAt(remainder.length() - 1) == '\"')
				            remainder.deleteCharAt(remainder.length() - 1);
				}
			}

			if (nextWord.length() == 0){
				return new Pair<String[], IntRange>(new String[0], null);
			}
			
			IntRange range = null;
			String[] values = new String[2];
			values[1] = remainder.toString();
			if (Character.isDigit(nextWord.charAt(0)))  {
			    range = extractIntRange(nextWord);			
			}
			else {
				values[0] = nextWord.toString();
		    }
			
			return new Pair<String[], IntRange>(values, range);
		}
		
		private IntRange extractIntRange(StringBuilder rangeStr) throws ParseException {
			Pair<Integer, Integer> result = readInteger(rangeStr);
			int first = result.getFirst();
			int i = result.getSecond();
			if (rangeStr.charAt(i) == '-') {
				
				int last = readInteger(rangeStr.substring(i+1)).getFirst();
				return new IntRange(first, last);
			}
			return new IntRange(first);
		}
		
		protected Pair<Integer, Integer> readInteger(CharSequence intStr) throws ParseException {
			StringBuilder b = new StringBuilder();
			int i = 0;
			while (Character.isDigit(intStr.charAt(i))) {
				b.append(intStr.charAt(i));
				i++;
			}
			if (b.length() == 0) {
				throw DirectiveError.asException(DirectiveError.Error.INTEGER_EXPECTED, _position-1);
			}
			int result;
			try {
				result = Integer.parseInt(b.toString());
			}
			catch (NumberFormatException e) {
				throw DirectiveError.asException(DirectiveError.Error.INTEGER_EXPECTED, _position-1);
			}
			return new Pair<Integer, Integer>(result, i);
		}
		
		private Pair<List<String>, Boolean> readIntkeyGroup() throws ParseException {
			List<String> tokens = new ArrayList<String>();
			boolean grouped = false;
			
			
			StringBuilder nextWord = new StringBuilder(getNextWord());
			if (StringUtils.isEmpty(nextWord.toString())) {
				return new Pair<List<String>, Boolean>(null, null);
			}

			if (nextWord.charAt(0) == '\"')  { // Have an ungrouped, quoted string. Collect until closing quote found
	
			    nextWord.deleteCharAt(0);
			    String dataBuf = getNextWord();
				while ((nextWord.length() == 0 || nextWord.charAt(nextWord.length() - 1) != '\"') &&
				        StringUtils.isNotEmpty(dataBuf)) {
				        
				    nextWord.append(dataBuf);
				    dataBuf = getNextWord();
				}
				if (nextWord.length() > 0 && nextWord.charAt(nextWord.length() - 1) == '\"') {
				    nextWord.deleteCharAt(nextWord.length() - 1);
				}
				if (nextWord.length() == 0) {
					return new Pair<List<String>, Boolean>(null, null);
				}
				else {
				    tokens.add(nextWord.toString());
				    return new Pair<List<String>, Boolean>(tokens, grouped);
				}
			}


			if (nextWord.charAt(0) != '(') {  // Not grouped. Just push the string and return				   
				tokens.add(nextWord.toString());
				return new Pair<List<String>, Boolean>(tokens, grouped);
			}

		    // Was grouped. Try to find the end of the group
			grouped = true;
			nextWord.deleteCharAt(0);
			String dataBuf = getNextWord();
			while ((nextWord.length() == 0 || nextWord.charAt(nextWord.length() - 1) != ')') &&
			        StringUtils.isNotEmpty(dataBuf)) {
			    nextWord.append(dataBuf);
			    dataBuf = getNextWord();
			}
			if (nextWord.length() > 0 && nextWord.charAt(nextWord.length() - 1) == ')') {
			    nextWord.deleteCharAt(nextWord.length() - 1);
			}
			// We now have the whole group in "nextWord", but we need to break it up into its components
			boolean inQuote = false;
			int wordStart = -1;
			for (int i = 0; i <= nextWord.length(); ++i) {
			    char ch;
				if (i == nextWord.length())  // To slightly simplify handling of the end of the string
				    ch = ' ';
				else
				    ch = nextWord.charAt(i);

				if (inQuote) { // In a quote, keep going til the closing quote is found...
				        
				    if (i == nextWord.length() || (ch == '\"' && Character.isWhitespace(nextWord.charAt(i+1)))) { // Must be at end of string, or followed by space
				        dataBuf = nextWord.substring(wordStart, i);
				        if (dataBuf.length() > 0) {
				            tokens.add(dataBuf);
				        }
				        wordStart = -1;
				        inQuote = false;
				    }
				}
				else if (Character.isWhitespace(ch)) { // Not in a quote, to if at a space, end the current word
				        
				    if (wordStart >= 0) {
				        dataBuf = nextWord.substring(wordStart, i);
				        tokens.add(dataBuf);
				        wordStart = -1;
				    }
				}
				else if (wordStart < 0) { // Not in a quote, starting a new word
				    wordStart = i;
				    if (ch == '\"')  {  // Starting a quote, so increment pointer
				        ++wordStart;
				        inQuote = true;
				    }
				}
			}
			return new Pair<List<String>, Boolean>(tokens, grouped);
		}

	}
	
}

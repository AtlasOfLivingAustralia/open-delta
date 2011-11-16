package au.org.ala.delta.editor.directives;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdListParser;
import au.org.ala.delta.directives.args.IdSetParser;
import au.org.ala.delta.directives.args.IdValueListParser;
import au.org.ala.delta.directives.args.IdWithIdListParser;
import au.org.ala.delta.directives.args.IntegerIdArgParser;
import au.org.ala.delta.directives.args.IntegerTextListParser;
import au.org.ala.delta.directives.args.KeyStateParser;
import au.org.ala.delta.directives.args.NumericArgParser;
import au.org.ala.delta.directives.args.StringTextListParser;
import au.org.ala.delta.directives.args.TextArgParser;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.util.Pair;

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
		case DirectiveArgType.DIRARG_ITEMLIST:
			parser = new IdListParser(context, reader);
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
        	parser = new IntKeyParser(context, reader, directive.getArgType());
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
		private int _argType;
		public IntKeyParser(AbstractDeltaContext context, Reader reader, int argType) {
			super(context, reader);
		}
		
		@Override
		public void parse() throws ParseException {
			_args = new DirectiveArguments();
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
	            boolean isChar = (_argType == DirectiveArgType.DIRARG_KEYWORD_CHARLIST ||
	                                _argType == DirectiveArgType.DIRARG_INTKEY_CHARLIST);

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
		  
		  readNext();
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
		      while ((tmpWord.length() == 0 && tmpWord.charAt(tmpWord.length() - 1) != '\"') && moreData) {
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
			readNext();
			skipWhitespace();
			boolean finished = false;
			while (!finished) {
				while (!Character.isWhitespace(_currentChar)) {
					word.append(_currentChar);
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
		
	}
	
}
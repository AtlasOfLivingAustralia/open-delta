package au.org.ala.delta.editor.directives;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;

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
import au.org.ala.delta.editor.slotfile.Directive;

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
			
			try {
				String value = readFully().trim();
				if (ON_VALUE.equalsIgnoreCase(value)) {
					_args.addValueArgument(new BigDecimal("1"));
				}
				else if (OFF_VALUE.equalsIgnoreCase(value) || OFF_VALUE.substring(0,2).equalsIgnoreCase(value)) {
					
				}
				else {
					throw new ParseException("Invalid value", _position);
				}
			}
			catch (Exception e) {
				throw new ParseException(e.getMessage(), _position);
			}
		}
		
	}
	
}
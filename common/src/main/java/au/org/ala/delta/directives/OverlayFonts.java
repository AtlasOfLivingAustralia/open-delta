package au.org.ala.delta.directives;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IntegerTextListParser;
import au.org.ala.delta.model.image.ImageSettings.FontInfo;


public class OverlayFonts extends AbstractInternalDirective {

	private DirectiveArguments _args;
	
	public OverlayFonts() {
		super("overlay", "fonts");
	}
	
	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		_args.addTextArgument(data);
		
		FontInfoParser parser = new FontInfoParser(context, new StringReader(data));
		parser.parse();
	}

	@Override
	public void process(DeltaContext context,
			DirectiveArguments directiveArguments) throws Exception {
		
		
	}

	
	class FontInfoParser extends IntegerTextListParser {
		public FontInfoParser(DeltaContext context, Reader reader) {
			super(context, reader);
		}

		@Override
		protected String readText() throws ParseException {
			
			FontInfo fontInfo = new FontInfo();
			skipWhitespace();
			
			// Any of the digits are optional.
			if (Character.isDigit(_currentChar)) {
				fontInfo.size = readInteger();
				skipWhitespace();
			}
			if (Character.isDigit(_currentChar)) {
				fontInfo.weight = readInteger();
				skipWhitespace();
			}
			if (Character.isDigit(_currentChar)) {
				fontInfo.italic = readInteger() != 0;
				skipWhitespace();
			}
			if (Character.isDigit(_currentChar)) {
				fontInfo.pitch = readInteger();
				skipWhitespace();
			}
			if (Character.isDigit(_currentChar)) {
				fontInfo.family = readInteger();
				skipWhitespace();
			}
			if (Character.isDigit(_currentChar)) {
				fontInfo.charSet = readInteger();
				skipWhitespace();
			}
			fontInfo.name = readToNext('#');
			
			return "";
		}
		
		
	}
	
}

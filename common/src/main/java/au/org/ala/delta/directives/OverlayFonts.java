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
			fontInfo.size = readInteger();
			skipWhitespace();
			fontInfo.weight = readInteger();
			skipWhitespace();
			fontInfo.italic = readInteger() != 0;
			skipWhitespace();
			fontInfo.pitch = readInteger();
			skipWhitespace();
			fontInfo.family = readInteger();
			skipWhitespace();
			fontInfo.charSet = readInteger();
			
			fontInfo.name = readToNext('#');
			
			return "";
		}
		
		
	}
	
}

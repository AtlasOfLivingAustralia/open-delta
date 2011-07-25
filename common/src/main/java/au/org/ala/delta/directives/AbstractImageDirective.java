package au.org.ala.delta.directives;

import java.io.StringReader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.ImageParser;

/**
 * Processes the CHARACTER IMAGES directive.
 */
public class AbstractImageDirective extends AbstractInternalDirective {

	private DirectiveArguments _args;
	private int _imageType;
	
	public AbstractImageDirective(int imageType, String... controlWords) {
		super("character", "images");
		_imageType = imageType;
	}
	

	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		_args.addTextArgument(data);
	}

	@Override
	public void process(DeltaContext context,
			DirectiveArguments directiveArguments) throws ParseException {
		
		String data = directiveArguments.getFirstArgumentText();
		
		ImageParser parser = new ImageParser(context, new StringReader(data), _imageType);
		parser.parse();
		
		context.setImages(_imageType, parser.getImageInfo());
	}

	
	
	
}

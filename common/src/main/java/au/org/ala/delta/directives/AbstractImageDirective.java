package au.org.ala.delta.directives;

import java.io.StringReader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.ImageParser;

/**
 * Base class for parsing directives that specify images and image overlays.
 */
public abstract class AbstractImageDirective extends AbstractInternalDirective {

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
		
		ImageParser parser = createParser(context, new StringReader(data));
		parser.parse();
		
		context.setImages(_imageType, parser.getImageInfo());
	}
	
	protected abstract ImageParser createParser(DeltaContext context, StringReader reader);
}

package au.org.ala.delta.directives;

import java.text.ParseException;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageOverlayParser;
import au.org.ala.delta.model.image.ImageType;

/**
 * Processes the CHARACTER IMAGES directive.
 */
public class CharacterImages extends AbstractDirective<DeltaContext> {

	private DirectiveArguments _args;
	private ImageOverlayParser _parser;
	
	public CharacterImages() {
		super("character", "images");
		_parser = new ImageOverlayParser();
	}
	
	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_INTERNAL;
	}

	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		_args = new DirectiveArguments();
		_args.addTextArgument(data);
	}

	@Override
	public void process(DeltaContext context,
			DirectiveArguments directiveArguments) throws Exception {
		
		String data = directiveArguments.getFirstArgumentText();
		List<ImageOverlay> overlays = _parser.parseOverlays(data, ImageType.IMAGE_CHARACTER);
		
	}

	
	
	
}

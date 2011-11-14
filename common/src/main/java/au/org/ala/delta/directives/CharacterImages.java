package au.org.ala.delta.directives;

import java.io.StringReader;
import java.text.ParseException;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.ImageParser;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.image.ImageInfo;
import au.org.ala.delta.model.image.ImageType;

/**
 * Processes the CHARACTER IMAGES directive.
 */
public class CharacterImages extends AbstractImageDirective {

	public CharacterImages() {
		super(ImageType.IMAGE_CHARACTER, "character", "images");
	}
	
	@Override
	protected ImageParser createParser(DeltaContext context, StringReader reader) {
		return new ImageParser(context, reader, ImageType.IMAGE_CHARACTER);
	}
	
	
	@Override
	public void process(DeltaContext context,
			DirectiveArguments directiveArguments) throws ParseException {
		super.process(context, directiveArguments);
		
		List<ImageInfo> images = context.getImages(ImageType.IMAGE_CHARACTER);
		
		for (ImageInfo imageInfo : images) {
			int charNum = (Integer)imageInfo.getId();
			Character character = context.getCharacter(charNum);
			imageInfo.addOrUpdate(character);
		}
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
	
}

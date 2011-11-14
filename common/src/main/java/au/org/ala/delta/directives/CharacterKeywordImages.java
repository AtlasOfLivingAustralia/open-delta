package au.org.ala.delta.directives;

import java.io.StringReader;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.ImageParser;
import au.org.ala.delta.model.image.ImageType;

/**
 * Processes the CHARACTER KEYWORD IMAGES directive.
 */
public class CharacterKeywordImages extends AbstractImageDirective {

	public CharacterKeywordImages() {
		super(ImageType.IMAGE_CHARACTER_KEYWORD, "character", "keyword", "images");
	}
	
	@Override
	protected ImageParser createParser(DeltaContext context, StringReader reader) {
		return new ImageParser(context, reader, ImageType.IMAGE_CHARACTER_KEYWORD);
	}

	@Override
	public int getOrder() {
		return 4;
	}
	
}

package au.org.ala.delta.directives;

import au.org.ala.delta.model.image.ImageType;

/**
 * Processes the CHARACTER IMAGES directive.
 */
public class CharacterImages extends AbstractImageDirective {

	public CharacterImages() {
		super(ImageType.IMAGE_CHARACTER, "character", "images");
	}
}

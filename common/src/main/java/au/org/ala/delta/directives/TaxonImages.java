package au.org.ala.delta.directives;

import java.io.StringReader;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.ImageParser;
import au.org.ala.delta.model.image.ImageType;

/**
 * Handles the TAXON IMAGES directive.
 */
public class TaxonImages extends AbstractImageDirective {

	public static final String[] CONTROL_WORDS = {"taxon", "images"};
	
	public TaxonImages() {
		super(ImageType.IMAGE_TAXON, CONTROL_WORDS);
	}
	
	@Override
	protected ImageParser createParser(DeltaContext context, StringReader reader) {
		return new ImageParser(context, reader, ImageType.IMAGE_TAXON);
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}

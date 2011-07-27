package au.org.ala.delta.directives;

import java.io.StringReader;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.ImageParser;
import au.org.ala.delta.model.image.ImageType;

/**
 * Handles the TAXON IMAGES directive.
 */
public class TaxonImages extends AbstractImageDirective {

	public TaxonImages() {
		super(ImageType.IMAGE_TAXON, "taxon", "images");
	}
	
	@Override
	protected ImageParser createParser(DeltaContext context, StringReader reader) {
		return new ImageParser(context, reader, ImageType.IMAGE_TAXON);
	}
}

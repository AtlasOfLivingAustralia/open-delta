package au.org.ala.delta.directives;

import java.io.StringReader;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.ImageParser;
import au.org.ala.delta.model.image.ImageType;

/**
 * Processes the TAXON KEYWORD IMAGES directive.
 */
public class TaxonKeywordImages extends AbstractImageDirective {

	public TaxonKeywordImages() {
		super(ImageType.IMAGE_TAXON_KEYWORD, "taxon", "keyword", "images");
	}
	
	@Override
	protected ImageParser createParser(DeltaContext context, StringReader reader) {
		return new ImageParser(context, reader, ImageType.IMAGE_TAXON_KEYWORD);
	}

	@Override
	public int getOrder() {
		return 4;
	}
	
}

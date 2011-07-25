package au.org.ala.delta.directives;

import au.org.ala.delta.model.image.ImageType;

/**
 * Handles the TAXON IMAGES directive.
 */
public class TaxonImages extends AbstractImageDirective {

	public TaxonImages() {
		super(ImageType.IMAGE_TAXON, "taxon", "images");
	}
	
}

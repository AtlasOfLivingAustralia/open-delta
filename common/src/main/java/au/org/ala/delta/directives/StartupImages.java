package au.org.ala.delta.directives;

import java.io.StringReader;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.ImageParser;
import au.org.ala.delta.directives.args.NoIdImageParser;
import au.org.ala.delta.model.image.ImageType;

/**
 * Handles the STARTUP IMAGES directive.
 */
public class StartupImages extends AbstractImageDirective {

	protected StartupImages() {
		super(ImageType.IMAGE_STARTUP, "startup", "images");
	}
	
	@Override
	protected ImageParser createParser(DeltaContext context, StringReader reader) {
		return new NoIdImageParser(context, reader, ImageType.IMAGE_STARTUP);
	}

}

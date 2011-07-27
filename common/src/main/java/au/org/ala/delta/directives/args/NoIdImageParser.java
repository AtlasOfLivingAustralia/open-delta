package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.image.ImageInfo;

/**
 * Parses the arguments to the STARTUP IMAGES, CHARACTER KEY IMAGES and
 * TAXON KEY IMAGES directives.
 */
public class NoIdImageParser extends ImageParser {
	
	public NoIdImageParser(DeltaContext context, Reader reader, int imageType) {
		super(context, reader, imageType);
	}
	
	@Override
	public void parse() throws ParseException {
		
		_args = new DirectiveArguments();
		_imageInfo = new ArrayList<ImageInfo>();
		readNext();
		skipWhitespace();
		
		int id = 0;
		while (_currentInt > 0) {
	
			readImage(id);
			skipWhitespace();
		}
		
	}
}

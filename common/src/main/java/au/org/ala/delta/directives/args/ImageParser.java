package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageOverlayParser;
import au.org.ala.delta.util.Pair;

/**
 * Parses the arguments to the CHARACTER IMAGES, TAXON IMAGES and STARTUP IMAGES
 * directives.
 */
public class ImageParser extends DirectiveArgsParser {

	private ImageOverlayParser _overlayParser;
	private int _imageType;
	private Map<Object, Pair<String, List<ImageOverlay>>> _imageInfo;
	
	public ImageParser(DeltaContext context, Reader reader, int imageType) {
		super(context, reader);
		_overlayParser = new ImageOverlayParser();
		_imageType = imageType;
	}
	
	/**
	 * Unfortunately we cannot create Image objects as they need to be 
	 * attached to a taxon or character and these are the last directives
	 * parsed.  Hence we store the data in a form that can be used when
	 * the characters and taxa are created.
	 */
	public Map<Object, Pair<String, List<ImageOverlay>>> getImageInfo() {
		return _imageInfo;
	}
	@Override
	public void parse() throws ParseException {
		
		_args = new DirectiveArguments();
		_imageInfo = new HashMap<Object, Pair<String,List<ImageOverlay>>>();
		skipWhitespace();
		
		while (_currentChar == '#') {
			expect('#');
			Object id = readId();
			skipWhitespace();
			
			String fileName = readFileName();
			skipWhitespace();
			
			String overlayText = readToNext('#');
			
			List<ImageOverlay> overlays = _overlayParser.parseOverlays(overlayText, _imageType);
		
			_imageInfo.put(id, new Pair<String, List<ImageOverlay>>(fileName, overlays));
		}
		
	}
	
	protected Object readId() throws ParseException {
		expect('#');
		
		mark();
		readNext();
		if (Character.isDigit(_currentChar)) {
			reset();
			
			return readListId();
		}
		else {
			reset();
			
			return readItemDescription();
		}
	}
	
	protected String readFileName() throws ParseException {
		return readToNextWhiteSpaceOrEnd();
	}
}

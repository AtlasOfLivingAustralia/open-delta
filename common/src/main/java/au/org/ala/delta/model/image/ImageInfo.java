package au.org.ala.delta.model.image;

import java.util.List;

import au.org.ala.delta.model.impl.DefaultImageData;

/**
 * The ImageInfo class is used as intermediate storage during directive parsing
 * as actual Image objects are created and owned by Characters and Items 
 * and the image directives are parsed before the Item and Character 
 * directives.
 *
 */
public class ImageInfo extends DefaultImageData {

	private int _imageType;
	private Object _subjectId;
	
	public ImageInfo(Object subjectid, int imageType, String fileName, List<ImageOverlay> overlays) {
		super(fileName);
		_subjectId = subjectid;
		_imageType = imageType;
		setOverlays(overlays);
	}
	
	public int getImageType() {
		return _imageType;
	}
	
	public Object getId() {
		return _subjectId;
	}
	
}

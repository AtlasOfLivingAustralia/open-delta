package au.org.ala.delta.editor.slotfile.model;

import java.util.List;

import au.org.ala.delta.editor.slotfile.VOImageDesc;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.impl.ImageData;

/**
 * An adaptor class that adapts the VOImageDesc class to the ImageData interface.
 */
public class VOImageAdaptor implements ImageData {

	private VOImageDesc _imageDesc;
	
	public VOImageAdaptor(VOImageDesc imageDesc) {
		_imageDesc = imageDesc;
	}
	
	@Override
	public List<ImageOverlay> getOverlays() {
		return _imageDesc.readAllOverlays();
	}

	public String getFileName() {
		return _imageDesc.readFileName();
	}
	
}

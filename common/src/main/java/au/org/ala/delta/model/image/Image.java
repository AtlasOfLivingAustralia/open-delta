package au.org.ala.delta.model.image;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.impl.ImageData;

public class Image {

	private ImageData _impl;
	
	public Image(ImageData data) {
		_impl = data;
	}
	
	public List<ImageOverlay> getOverlays() {
		return _impl.getOverlays();
	}

	public String getFileName() {
		return _impl.getFileName();
	}
	
	public URL getImageLocation(String imagePath) {
		
		String fileName = getFileName();
		if (StringUtils.isEmpty(fileName)) {
			return null;
		}
		URL imageURL = null;
		try {
		
		if (fileName.startsWith("http")) {
			
			imageURL = new URL(fileName);
			
		}
		else if (fileName.contains("/") || fileName.contains("\\")) {
			File f = new File(fileName);
			imageURL = f.toURI().toURL();
		}
		else {
			// need the image path - what is a nice way to get it?  could 
			// put this method in the dataset instead?
			
			File f = new File(imagePath+File.separator+fileName);
			imageURL = f.toURI().toURL();
		}
		} catch (Exception e) {
			throw new RuntimeException("Invalid image file path specified: "+fileName, e);
		}
		
		return imageURL;
	}
}

package au.org.ala.delta.model.image;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.impl.ImageData;

/**
 * DELTA data sets support the inclusion of images to illustrate Taxa and Characters.
 * Images can be decorated by descriptive text, sounds and in the case of MultistateCharacters
 * "hot spot" sections that distinguish Character states.  These "hot spots" are used
 * for interactive identification in the IntKey program.
 * The Image class represents an image attached to an Item or Character.
 */
public class Image {

	private ImageData _impl;
	private Illustratable _subject;
	
	public Image(ImageData data) {
		_impl = data;
	}
	
	public ImageData getImageData() {
		return _impl;
	}
	
	public List<ImageOverlay> getOverlays() {
		return _impl.getOverlays();
	}

	public String getFileName() {
		return _impl.getFileName();
	}
	
	public Illustratable getSubject() {
		return _subject;
	}
	
	public void setSubject(Illustratable subject) {
		_subject = subject;
	}
	
	/**
	 * If this image has an overlay of type OverlayType.OLSUBJECT the
	 * text from this overlay will be returned, otherwise an empty string.
	 * @return any subject text specified for this image.
	 */
	public String getSubjectText() {
		ImageOverlay subjectOverlay = getFirstOverlayOfType(OverlayType.OLSUBJECT);
		if (subjectOverlay != null) {
			return subjectOverlay.overlayText;
		}
		return "";
	}
	
	private ImageOverlay getFirstOverlayOfType(int type) {
		List<ImageOverlay> overlays = getOverlays();
		
		for (ImageOverlay overlay : overlays) {
			if (overlay.isType(type)) {
				return overlay;
			}
		}
		return null;
	}
	
	public List<ImageOverlay> getSounds() {
		return getOverlaysOfType(OverlayType.OLSOUND);
	}
	
	private List<ImageOverlay> getOverlaysOfType(int type) {
		List<ImageOverlay> results = new ArrayList<ImageOverlay>();
		List<ImageOverlay> overlays = getOverlays();
		
		for (ImageOverlay overlay : overlays) {
			if (overlay.isType(type)) {
				results.add(overlay);
			}
		}
		return results;
	}
	
	public URL soundToURL(ImageOverlay soundOverlay, String imagePath) {
		return relativePathToURL(imagePath, soundOverlay.overlayText);
	}
	
	private URL relativePathToURL(String imagePath, String relativePath) {
		
		if (StringUtils.isEmpty(relativePath)) {
			return null;
		}
		URL imageURL = null;
		try {
		
		if (relativePath.startsWith("http")) {
			
			imageURL = new URL(relativePath);
			
		}
		else if (relativePath.contains("/") || relativePath.contains("\\")) {
			File f = new File(relativePath);
			imageURL = f.toURI().toURL();
		}
		else {
			File f = new File(imagePath+File.separator+relativePath);
			imageURL = f.toURI().toURL();
		}
		} catch (Exception e) {
			throw new RuntimeException("Invalid image file path specified: "+relativePath, e);
		}
		
		return imageURL;
	}
	
	public URL getImageLocation(String imagePath) {
		return relativePathToURL(imagePath, getFileName());
	}
	
	public void addOverlay(ImageOverlay overlay) {
		_impl.addOverlay(overlay);
	}
	
	public void updateOverlay(ImageOverlay overlay) {
		_impl.updateOverlay(overlay);
	}
	
	public void deleteOverlay(ImageOverlay overlay) {
		_impl.addOverlay(overlay);
	}
	
	/**
	 * Returns the first overlay of the specified type or null if there is no such overlay.
	 * @param overlayType the type of image overlay to get.
	 * @return the first overlay of the supplied type or null if there is none.
	 */
	public ImageOverlay getOverlay(int overlayType) {
		List<ImageOverlay> overlays = getOverlays();
		
		for (ImageOverlay overlay : overlays) {
			if (overlayType == overlay.type) {
				return overlay;
			}
		}
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Image)) {
			return false;
		}
		Image otherImage = (Image)other;
		
		return getFileName().equals(otherImage.getFileName());
	}
	
	@Override
	public int hashCode() {
		return getFileName().hashCode();
	}
}

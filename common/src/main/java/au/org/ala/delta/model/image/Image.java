package au.org.ala.delta.model.image;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.impl.ImageData;
import au.org.ala.delta.model.observer.ImageObserver;

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
	private List<ImageObserver> _observers;
	/** A flag to temporarily prevent notification of observers during a bulk add/delete operation */
	private boolean _suspendNotify;
	
	public Image(ImageData data) {
		_impl = data;
		_suspendNotify = false;
	}
	
	public ImageData getImageData() {
		return _impl;
	}
	
	public List<ImageOverlay> getOverlays() {
		return _impl.getOverlays();
	}
	
	public void setOverlays(List<ImageOverlay> overlays) {
		_impl.setOverlays(overlays);
		 notifyObservers();
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
	
	/**
	 * @return the overlay of type OLENTER associated with this Image, or
	 * null if this image has no overlay of this type.
	 */
	public ImageOverlay getEnterOverlay() {
		return getFirstOverlayOfType(OverlayType.OLENTER);
	}
	
	/**
	 * @return a new List containing all of the sounds overlays associated
	 * with this image.
	 */
	public List<ImageOverlay> getSounds() {
		return getOverlaysOfType(OverlayType.OLSOUND);
	}
	
	public List<ImageOverlay> getOverlaysOfType(int type) {
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
		 notifyObservers();
	}
	
	public ImageOverlay addOverlay(int overlayType) {
		ImageOverlay overlay = new ImageOverlay(overlayType);
		addOverlay(overlay);
		return overlay;
	}
	
	public void updateOverlay(ImageOverlay overlay) {
		_impl.updateOverlay(overlay);
		 notifyObservers();
	}
	
	public void deleteOverlay(ImageOverlay overlay) {
		_impl.deleteOverlay(overlay);
		notifyObservers();
	}
	
	public void deleteAllOverlays() {
		
		try {
			_suspendNotify = true;
		
			 List<ImageOverlay> overlays = getOverlays();
			 for (ImageOverlay overlay : overlays) {
				 deleteOverlay(overlay);
			 }
		}
		finally {
			_suspendNotify = false;
		}
		notifyObservers();
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

	public String getNotes() {
		ImageOverlay notesOverlay = getFirstOverlayOfType(OverlayType.OLIMAGENOTES);
		if (notesOverlay != null) {
			return notesOverlay.overlayText;
		}
		return "";
	}
	
	public boolean hasNotes() {
		ImageOverlay notesOverlay = getFirstOverlayOfType(OverlayType.OLIMAGENOTES);
		return notesOverlay != null;
	}
	
	/**
     * Registers interest in being notified of changes to this Image.
     * 
     * @param observer
     *            the object interested in receiving notification of changes.
     */
    public void addImageObserver(ImageObserver observer) {
        if (_observers == null) {
            _observers = new ArrayList<ImageObserver>(1);
        }
        if (!_observers.contains(observer)) {
            _observers.add(observer);
        }
    }

    /**
     * De-registers interest in changes to this Image.
     * 
     * @param observer
     *            the object no longer interested in receiving notification of
     *            changes.
     */
    public void removeImageObserver(ImageObserver observer) {
        if (_observers == null) {
            return;
        }
        _observers.remove(observer);
    }

    /**
     * Notifies all registered ImageObservers that this Image has
     * changed.
     */
    protected void notifyObservers() {
        if (_observers == null || _suspendNotify) {
            return;
        }
        // Notify observers in reverse order to support observer removal during
        // event handling.
        for (int i = _observers.size() - 1; i >= 0; i--) {
            _observers.get(i).imageChanged(this);
        }
    }
}

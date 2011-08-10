package au.org.ala.delta.model.observer;

import au.org.ala.delta.model.image.Image;

/**
 * This interface should be implemented by classes interested in being notified of changes to Images.
 * They should then call addImageObserver(this) to register interest in changes to an
 * image.
 */
public interface ImageObserver {

	/**
	 * Invoked when the Image changes.
	 * @param image the changed image.
	 */
	public void imageChanged(Image image);
}

package au.org.ala.delta.model;

import java.util.List;

import au.org.ala.delta.model.image.Image;

/**
 * Identifies model elements that can have images associated with them.
 */
public interface Illustratable {

	/**
	 * Associates an image with this object.
	 * @param fileName identifies the image.
	 * @param comments details about the image. (parsed into hotspots etc.).
	 */
	public void addImage(String fileName, String comments);
	
	/**
	 * @return a list of images associated with this object.
	 */
	public List<Image> getImages();
}

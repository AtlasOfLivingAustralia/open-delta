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
	public Image addImage(String fileName, String comments);
	
	/**
	 * Associates an image with this object
	 * @param image the image
	 */
	public void addImage(Image image);
	
	/**
	 * @return a list of images associated with this object.
	 */
	public List<Image> getImages();
	
	/**
	 * Deletes the supplied image from the data set.
	 * @param image the image to delete.
	 */
	public void deleteImage(Image image);
	
	/**
	 * Changes the position of an Image in the list of images.
	 * @param image the image to move.
	 * @param position the new position for the image in the list.  The position should be
	 * between 1 (the first position in the list) and the number of images (the last position 
	 * in the list).
	 */
	public void moveImage(Image image, int position);
	
	/**
	 * @return the number of images associated with this Illustratable.
	 */
	public int getImageCount();
}

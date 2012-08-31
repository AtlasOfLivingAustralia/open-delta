/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
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

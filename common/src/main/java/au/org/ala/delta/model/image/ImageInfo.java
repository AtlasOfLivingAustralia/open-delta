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
package au.org.ala.delta.model.image;

import java.util.List;

import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.impl.DefaultImageData;

/**
 * The ImageInfo class is used as intermediate storage during directive parsing
 * as actual Image objects are created and owned by Characters and Items 
 * and the image directives are parsed before the Item Descriptions directive.
 * Also some image types (startup images for example) aren't a part of the
 * DeltaDataSet.
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
	
	/** 
	 * Adds or updates the supplied illustratable with the supplied ImageInfo.
	 * If no image exists with the filename in the ImageInfo, a new image
	 * is created.  Otherwise the overlays will be updated with those in the
	 * supplied ImageInfo.
	 * @param illustratable the subject to update with the supplied ImageInfo.
	 * @param imageInfo details of the image to add or update.
	 */
	public void addOrUpdate(Illustratable illustratable) {
		
		List<Image> images = illustratable.getImages();
		Image image = null;
		for (Image tmpImage : images) {
			if (tmpImage.getFileName().equals(getFileName())) {
				image = tmpImage;
				break;
			}
		}
		if (image == null) {
			image = illustratable.addImage(getFileName(), "");
		}
		image.setOverlays(getOverlays());
	}
}

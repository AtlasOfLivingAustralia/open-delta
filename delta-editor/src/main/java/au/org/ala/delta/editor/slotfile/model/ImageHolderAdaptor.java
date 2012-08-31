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
package au.org.ala.delta.editor.slotfile.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VOImageDesc;
import au.org.ala.delta.editor.slotfile.VOImageHolderDesc;
import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageType;

/**
 * Helper class for working with VOImageHolderDesc objects.
 */
public abstract class ImageHolderAdaptor implements Illustratable {
	
	/**
	 * Should be overriden by subclasses to return an appropriate instance of
	 * VOImageHolderDesc.
	 */
	protected abstract VOImageHolderDesc getImageHolder();
	
	
	/**
	 * Should be overriden by subclasses to return a reference to the VOP to work with.
	 */
	protected abstract DeltaVOP getVOP();
	
	/**
	 * Adds a new image to the image holder returned by getImageHolder().
	 * @param fileName the path to the image.
	 * @param comments encoded overlay data.
	 */
	@Override
	public Image addImage(String fileName, String comments) {
		synchronized (getVOP()) {
			
			if (StringUtils.isEmpty(fileName)) {
				throw new IllegalArgumentException("Image file name cannot be null");
			}
			VOImageDesc.ImageFixedData imageFixedData = new VOImageDesc.ImageFixedData(
					getImageHolder().getUniId(), ImageType.IMAGE_TAXON);
			VOImageDesc imageDesc = (VOImageDesc) getVOP().insertObject(imageFixedData,
					imageFixedData.size(), null, 0, 0);
			if (imageDesc != null) {
				imageDesc.writeFileName(fileName);
				try {
					imageDesc.parseOverlays(comments);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
	
				int imageId = imageDesc.getUniId();
				List<Integer> images = getImageHolder().readImageList();
				images.add(imageId);
				getImageHolder().writeImageList(images);
				
				return new Image(new VOImageAdaptor(getVOP(), imageDesc));
			}
			
			return null;
		}
	}
	
    @Override
    public void addImage(Image image) {
        throw new NotImplementedException();
    }
	
	/**
	 * @return the list of Images managed by the ImageHolder.  If there are no
	 * images an empty list will be returned.
	 */
	@Override
	public List<Image> getImages() {
		synchronized (getVOP()) {
			List<Integer> imageIds = getImageHolder().readImageList();
			
			List<Image> images = new ArrayList<Image>();
		
			for (int id : imageIds) {
				images.add(createImage(id));
			}
			
			return images;
		}
	}
	
	@Override
	public int getImageCount() {
		synchronized (getVOP()) {
			return getImageHolder().getNImages();
		}
	}
	
	/**
	 * Wraps a VOImageDesc in a new Image.
	 * @param id the id of the VOImageDesc.
	 * @return a new Image object that will delegate to the VOImageDesc identified by
	 * the supplied id.
	 */
	protected Image createImage(int id) {
		VOImageDesc imageDesc = (VOImageDesc)getVOP().getDescFromId(id);
		
		return new Image(new VOImageAdaptor(getVOP(), imageDesc));
	}
	
	@Override
	public void deleteImage(Image image) {
		synchronized (getVOP()) {
			VOImageAdaptor imageAdaptor = (VOImageAdaptor)image.getImageData();
			VOImageDesc imageDesc = imageAdaptor.getImageDesc();
			if (imageDesc.getOwnerId() != getImageHolder().getUniId()) {
				throw new IllegalArgumentException("Image is not owned by this object");
			}
			getImageHolder().deleteImage(imageDesc.getUniId());
			
			getVOP().deleteObject(imageDesc);
		}
	}
	
	private int getImageId(Image image) {
		VOImageAdaptor imageAdaptor = (VOImageAdaptor)image.getImageData();
		return imageAdaptor.getId();
	}
	
	@Override
	public void moveImage(Image image, int position) {
		synchronized (getVOP()) {
			List<Integer> images = getImageHolder().readImageList();
			int id = getImageId(image);
			
			for (int i=0; i<images.size(); i++) {
				if (images.get(i) == id) {
					images.remove(i);
					images.add(position, id);
					break;
				}
			}
			getImageHolder().writeImageList(images);
		}
	}
}

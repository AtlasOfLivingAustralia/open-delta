package au.org.ala.delta.editor.slotfile.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.ImageType;
import au.org.ala.delta.editor.slotfile.VOImageDesc;
import au.org.ala.delta.editor.slotfile.VOImageHolderDesc;
import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.image.Image;

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
	public void addImage(String fileName, String comments) {
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
		}
	}
	
	/**
	 * @return the list of Images managed by the ImageHolder.  If there are no
	 * images an empty list will be returned.
	 */
	@Override
	public List<Image> getImages() {
		List<Integer> imageIds = getImageHolder().readImageList();
		
		List<Image> images = new ArrayList<Image>();
	
		for (int id : imageIds) {
			images.add(createImage(id));
		}
		
		return images;
	}
	
	/**
	 * Wraps a VOImageDesc in a new Image.
	 * @param id the id of the VOImageDesc.
	 * @return a new Image object that will delegate to the VOImageDesc identified by
	 * the supplied id.
	 */
	protected Image createImage(int id) {
		VOImageDesc imageDesc = (VOImageDesc)getVOP().getDescFromId(id);
		
		return new Image(new VOImageAdaptor(imageDesc));
	}
	
	@Override
	public void deleteImage(Image image) {
		VOImageAdaptor imageAdaptor = (VOImageAdaptor)image.getImageData();
		VOImageDesc imageDesc = imageAdaptor.getImageDesc();
		if (imageDesc.getOwnerId() != getImageHolder().getUniId()) {
			throw new IllegalArgumentException("Image is not owned by this object");
		}
		getImageHolder().deleteImage(imageDesc.getUniId());
		
		getVOP().deleteObject(imageDesc);
	}
	
	@Override
	public void moveImage(Image image, int position) {
		
	}
}

package au.org.ala.delta.editor.slotfile.model;

import java.text.ParseException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.ImageType;
import au.org.ala.delta.editor.slotfile.VOImageDesc;
import au.org.ala.delta.editor.slotfile.VOImageHolderDesc;

/**
 * Helper class for working with VOImageHolderDesc objects.
 */
public abstract class ImageHolderAdatptor {
	
	/**
	 * Should be overriden by subclasses to return an appropriate instance of
	 * VOImageHolderDesc.
	 */
	protected abstract VOImageHolderDesc getImageHolder();
	
	
	/**
	 * Should be overriden by subclasses to return a reference to the VOP to work with.
	 */
	protected abstract DeltaVOP getVOP();
	
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
}

package au.org.ala.delta.editor.slotfile.model;

import java.util.List;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOImageDesc;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.model.impl.ImageData;

/**
 * An adaptor class that adapts the VOImageDesc class to the ImageData interface.
 */
public class VOImageAdaptor implements ImageData {

	private VOImageDesc _imageDesc;
	private DeltaVOP _vop;
	
	public VOImageAdaptor(DeltaVOP vop, VOImageDesc imageDesc) {
		_imageDesc = imageDesc;
		_vop = vop;
	}
	
	/**
	 * @return the unique (slotfile) id of the underlying VOImageDesc.
	 */
	public int getId() {
		return _imageDesc.getUniId();
	}
	
	public VOImageDesc getImageDesc() {
		return _imageDesc;
	}
	
	@Override
	public List<ImageOverlay> getOverlays() {
		List<ImageOverlay> overlays = _imageDesc.readAllOverlays();
		
		for (ImageOverlay overlay : overlays) {
			if (overlay.isType(OverlayType.OLSTATE)) {
				int id = _imageDesc.getOwnerId();
				VOCharBaseDesc charBase = (VOCharBaseDesc)_vop.getDescFromId(id);
				overlay.stateId = charBase.stateNoFromUniId(overlay.stateId);
			}
		}
		
		return overlays;
	}

	public String getFileName() {
		return _imageDesc.readFileName();
	}
	
	public void addOverlay(ImageOverlay overlay) {
		_imageDesc.insertOverlay(overlay, 0);
	}
	
	public void updateOverlay(ImageOverlay overlay) {
		_imageDesc.replaceOverlay(overlay, true);
	}
	
	public void deleteOverlay(ImageOverlay overlay) {
		_imageDesc.removeOverlay(overlay.getId());
	}
	
}

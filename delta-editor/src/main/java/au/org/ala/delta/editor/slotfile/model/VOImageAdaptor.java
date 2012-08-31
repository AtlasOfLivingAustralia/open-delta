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
		synchronized (_vop) {
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
	}
	
	@Override
	public void setOverlays(List<ImageOverlay> overlays) {
		
		for (ImageOverlay overlay : getOverlays()) {
			deleteOverlay(overlay);
		}
		
		// Insert in reverse order as each insert goes to the top.
		for (int i=overlays.size()-1; i>=0; i--) {
			addOverlay(overlays.get(i));
		}
	}

	public String getFileName() {
		synchronized (_vop) {
			return _imageDesc.readFileName();
		}
	}
	
	public void addOverlay(ImageOverlay overlay) {
		synchronized (_vop) {
			_imageDesc.insertOverlay(updateStateId(overlay));
		}
	}
	
	public void updateOverlay(ImageOverlay overlay) {
		synchronized (_vop) {
			_imageDesc.replaceOverlay(updateStateId(overlay), false);
		}
	}
	
	public void deleteOverlay(ImageOverlay overlay) {
		synchronized (_vop) {
			_imageDesc.removeOverlay(overlay.getId());
		}
	}
	
	private ImageOverlay updateStateId(ImageOverlay overlay) {
		if (overlay.isType(OverlayType.OLSTATE)) {
			ImageOverlay copy = new ImageOverlay(overlay);
			int id = _imageDesc.getOwnerId();
			VOCharBaseDesc charBase = (VOCharBaseDesc)_vop.getDescFromId(id);
			copy.stateId = charBase.uniIdFromStateNo(overlay.stateId);
			return copy;
		}
		return overlay;
	}
	
}

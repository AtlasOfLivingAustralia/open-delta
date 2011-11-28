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
package au.org.ala.delta.model.impl;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.image.ImageOverlay;

public class DefaultImageData implements ImageData {

	private ArrayList<ImageOverlay> _overlays;
	private String _fileName;
	
	public DefaultImageData(String fileName) {
		_fileName = fileName;
		_overlays = new ArrayList<ImageOverlay>();
	}
	
	@Override
	public List<ImageOverlay> getOverlays() {
		return _overlays;
	}
	
	@Override
	public void setOverlays(List<ImageOverlay> overlays) {
		_overlays = new ArrayList<ImageOverlay>(overlays);
	}


	@Override
	public String getFileName() {
		return _fileName;
	}

	@Override
	public void addOverlay(ImageOverlay overlay) {
		_overlays.add(overlay);
	}

	@Override
	public void updateOverlay(ImageOverlay overlay) {
		
		int id = overlay.getId();
		ImageOverlay toReplace = null;
		for (ImageOverlay tmp : _overlays) {
			if (tmp.getId() == id) {
				toReplace = tmp;
				break;
			}
		}
		if (toReplace == null) {
			throw new RuntimeException("Cannot replace overlay.  No overlay with id "+id+ "exists");
		}
		int index = _overlays.indexOf(toReplace);
		_overlays.set(index, overlay);
	}

	@Override
	public void deleteOverlay(ImageOverlay overlay) {
		_overlays.remove(overlay);
	}

}

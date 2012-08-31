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

import java.util.List;

import au.org.ala.delta.model.image.ImageOverlay;

public interface ImageData {

	public List<ImageOverlay> getOverlays();
	
	public void setOverlays(List<ImageOverlay> overlays);

	public String getFileName();
	
	public void addOverlay(ImageOverlay overlay);
	
	public void updateOverlay(ImageOverlay overlay);
	
	public void deleteOverlay(ImageOverlay overlay);
}

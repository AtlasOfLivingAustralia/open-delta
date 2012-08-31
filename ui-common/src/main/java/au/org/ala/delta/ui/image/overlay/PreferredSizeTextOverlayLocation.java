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
package au.org.ala.delta.ui.image.overlay;

import javax.swing.JComponent;

import au.org.ala.delta.ui.image.ImageViewer;

/**
 * The PreferredSizeTextOverlayLocation behaves the same as the
 * TextOverlayLocation except that is always uses the components
 * preferred size rather than the data in the OverlayLocation.
 */
public class PreferredSizeTextOverlayLocation extends TextOverlayLocation {

	public PreferredSizeTextOverlayLocation(ImageViewer image, JComponent component, au.org.ala.delta.model.image.OverlayLocation location) {
		super(image, component, location);
	}
	
	@Override
	public int getHeight() {
		return _component.getPreferredSize().height;
	}
	
	@Override
	public int getWidth() {
		return _component.getPreferredSize().width;
	}
}

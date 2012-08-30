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
 * The RelativeOverlayLocation determines the location of the overlay
 * component based on the location of another component.
 */
public class RelativeOverlayLocation extends TextOverlayLocation {

	protected OverlayLocation _relativeTo;
	
	public RelativeOverlayLocation(ImageViewer image, JComponent component, 
			au.org.ala.delta.model.image.OverlayLocation location,
			OverlayLocation relativeTo) {
		super(image, component, location);
		_relativeTo = relativeTo;
	}
	
	@Override
	public int getX() {
		if (_location.X == Short.MIN_VALUE) {
			return _relativeTo.getX() + _relativeTo.getWidth() + 1;
		}
		else {
			return super.getX();
		}
	}
	
	@Override
	public int getY() {
		if (_location.X == Short.MIN_VALUE) {
			return _relativeTo.getY();
		}
		else {
			return super.getY();
		}
	}
	
	
}

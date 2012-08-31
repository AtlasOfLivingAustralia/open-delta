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
package au.org.ala.delta.editor.ui.image;

import java.awt.Point;

import javax.swing.JComponent;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayLocation;
import au.org.ala.delta.ui.image.overlay.HotSpot;

/**
 * Tracks selections made in an ImageEditorPanel.
 */
public class ImageEditorSelectionModel {

	private Point _selectedPoint;
	private Image _selectedImage;
	private JComponent _selectedComponent;
	
	public Point getSelectedPoint() {
		return _selectedPoint;
	}
	public void setSelectedPoint(Point selectedPoint) {
		_selectedPoint = selectedPoint;
	}
	public Image getSelectedImage() {
		return _selectedImage;
	}
	public void setSelectedImage(Image selectedImage) {
		_selectedImage = selectedImage;
	}
	public ImageOverlay getSelectedOverlay() {
		if (_selectedComponent == null) {
			return null;
		}
		return (ImageOverlay)_selectedComponent.getClientProperty("ImageOverlay");
	}
	
	public boolean isHotSpotSelected() {
		return _selectedComponent instanceof HotSpot;
	}
	public OverlayLocation getSelectedOverlayLocation() {
		if (_selectedComponent == null) {
			return null;
		}
		
		ImageOverlay overlay = getSelectedOverlay();
		OverlayLocation location;
		if (isHotSpotSelected()) {
			location = ((HotSpot)_selectedComponent).getOverlayLocation();
		}
		else {
			location = overlay.getLocation(0);
		}
		return location;
	}
	
	public void setSelectedOverlayComponent(JComponent component) {
		_selectedComponent = component;
	}
}

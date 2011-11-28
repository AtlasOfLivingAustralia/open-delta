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

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.ImageViewer;

/**
 * The RelativePositionedTextOverlay behaves the same as a RichTextLabel
 * except that it's position can be expressed relative to another overlay.
 * This is used mainly for a units overlay.
 */
public class RelativePositionedTextOverlay extends RichTextLabel {

	private static final long serialVersionUID = 4782350523854302175L;
	
	private OverlayLocationProvider _relativeTo;
	
	public RelativePositionedTextOverlay(ImageOverlay overlay, String text) {
		super(overlay, text);
	}
	
	@Override
	public OverlayLocation location(ImageViewer viewer) {
		OverlayLocation location = null;
		if (_relativeTo != null) {
			location = _relativeTo.location(viewer);
		}
		return new RelativeOverlayLocation(viewer, this, _overlay.getLocation(0), location);
	
	}

	public void makeRelativeTo(OverlayLocationProvider relativeTo) {
		_relativeTo = relativeTo;
	}
}

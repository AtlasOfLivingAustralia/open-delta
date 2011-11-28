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
package au.org.ala.delta.editor.directives;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayLocation;

public abstract class DirOutImageOverlayTest extends DirOutTest {

	public DirOutImageOverlayTest() {
		super();
	}

	public DirOutImageOverlayTest(String name) {
		super(name);
	}

	protected ImageOverlay addOverlay(Image image, int type) {
		ImageOverlay overlay = new ImageOverlay(type);
		image.addOverlay(overlay);
		return overlay;
	}

	protected ImageOverlay addOverlay(Image image, int type, int x,
			int y, int w, int h) {
				ImageOverlay overlay = addOverlay(image, type);
				
				addLocation(overlay, x, y, w, h);
				return overlay;
			}

	protected OverlayLocation addLocation(ImageOverlay overlay, int x, int y,
			int w, int h) {
				OverlayLocation location = new OverlayLocation();
				location.X = (short)x;
				location.Y = (short)y;
				location.W = (short)w;
				location.H = (short)h;
				
				overlay.addLocation(location);
				return location;
			}

}

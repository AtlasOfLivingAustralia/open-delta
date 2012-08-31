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
package au.org.ala.delta.editor.slotfile.directive;

import java.util.List;

import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.translation.delta.ImageOverlayWriter;

/**
 * Base class for exporters of directives that work with images and image
 * overlays.
 */
public abstract class DirOutImageOverlay extends AbstractDirOutFunctor {
	
	/**
	 * Outputs the supplied List of ImageOverlays to the Printer provided by the
	 * state.
	 * 
	 * @param overlayList
	 *            the overlays to output.
	 * @param startIndent
	 *            the indent to use when outputting the first overlay.
	 * @param subject
	 *            the subject of the image overlays. May be null if the list
	 *            does not contain value or state overlays.
	 */
	protected void writeOverlays(List<ImageOverlay> overlayList,
			int startIndent, Illustratable subject) {
		
		ImageOverlayWriter overlayWriter = new ImageOverlayWriter(_deltaWriter);
		overlayWriter.writeOverlays(overlayList, startIndent, subject);
	}
}

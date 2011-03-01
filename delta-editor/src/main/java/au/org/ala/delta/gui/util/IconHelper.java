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

package au.org.ala.delta.gui.util;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Utilities for working with image icons.
 *
 */
public class IconHelper {

	private static final String ICON_PATH = "/icons";
	private static final String DELTA_IMAGE_32 = "Delta_blue_32.png";
	private static final String DELTA_IMAGE_16 = "Delta_blue_16.png";
	private static final String DELTA_IMAGE_14 = "Delta_blue_14.png";
	private static final String DELTA_IMAGE_12 = "Delta_blue_12.png";
	
	public static ImageIcon createImageIcon(String imageFileName) {		
		return new ImageIcon(imageURLFromFileName(imageFileName));
	}
	
	public static ImageIcon createLargeIcon() {
		return createImageIcon(DELTA_IMAGE_32);
	}
	
	public static ImageIcon createDeltaImageIcon() {
		return createImageIcon(DELTA_IMAGE_16);
	}
	
	public static ImageIcon createInternalFrameNormalIcon() {
		return createImageIcon(DELTA_IMAGE_14);
	}
	
	public static ImageIcon createInternalFrameMaximizedIcon() {
		return createImageIcon(DELTA_IMAGE_12);
	}
	
	private static URL imageURLFromFileName(String imageFileName) {
		URL imageUrl = IconHelper.class.getResource(ICON_PATH+"/"+imageFileName);
		return imageUrl;
	}
}

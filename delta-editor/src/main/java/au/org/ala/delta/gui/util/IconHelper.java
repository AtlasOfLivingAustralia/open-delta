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

import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.OrderedMultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;
import au.org.ala.delta.util.Platform;

/**
 * Utilities for working with image icons.
 * 
 */
public class IconHelper {

	private static final String ICON_PATH = "/icons";
	private static final String DELTA_IMAGE_48 = "Delta_blue_48.png";
	private static final String DELTA_IMAGE_32 = "Delta_blue_32.png";
	private static final String DELTA_IMAGE_16 = "Delta_blue_16.png";
	private static final String DELTA_IMAGE_14 = "Delta_blue_14.png";
	private static final String DELTA_IMAGE_12 = "Delta_blue_12.png";

	private static final ImageIcon _textIcon = createImageIcon("textchar.png");
	private static final ImageIcon _realIcon = createImageIcon("realchar.png");
	private static final ImageIcon _intIcon = createImageIcon("intchar.png");
	private static final ImageIcon _omIcon = createImageIcon("omchar.png");
	private static final ImageIcon _umIcon = createImageIcon("umchar.png");

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

		if (Platform.isWindowsAero()) {
			return createImageIcon(DELTA_IMAGE_14);
		} else {
			return createImageIcon(DELTA_IMAGE_16);
		}

	}

	public static ImageIcon createInternalFrameMaximizedIcon() {
		if (Platform.isWindowsAero()) {
			return createImageIcon(DELTA_IMAGE_12);
		} else {
			return createImageIcon(DELTA_IMAGE_16);
		}
	}

	/**
	 * Returns the appropriate icon for the supplied character.
	 * 
	 * @param ch
	 *            the character to get the icon for.
	 * @return an icon representing the type of the supplied Character
	 */
	public static ImageIcon iconForCharacter(au.org.ala.delta.model.Character ch) {
		ImageIcon icon = null;
		if (ch instanceof TextCharacter) {
			icon = _textIcon;
		} else if (ch instanceof RealCharacter) {
			icon = _realIcon;
		} else if (ch instanceof IntegerCharacter) {
			icon = _intIcon;
		} else if (ch instanceof OrderedMultiStateCharacter) {
			icon = _omIcon;
		} else if (ch instanceof UnorderedMultiStateCharacter) {
			icon = _umIcon;
		}
		return icon;
	}

	private static URL imageURLFromFileName(String imageFileName) {
		URL imageUrl = IconHelper.class.getResource(ICON_PATH + "/" + imageFileName);
		return imageUrl;
	}

	public static List<? extends Image> getDeltaIconList() {
		List<Image> list = new ArrayList<Image>();
		
		list.add(createImageIcon(DELTA_IMAGE_48).getImage());
		list.add(createImageIcon(DELTA_IMAGE_32).getImage());
		list.add(createImageIcon(DELTA_IMAGE_16).getImage());
		list.add(createImageIcon(DELTA_IMAGE_14).getImage());
		list.add(createImageIcon(DELTA_IMAGE_12).getImage());

		return list;
	}

}

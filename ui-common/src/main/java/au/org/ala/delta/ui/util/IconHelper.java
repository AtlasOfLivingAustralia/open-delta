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

package au.org.ala.delta.ui.util;

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
import au.org.ala.delta.ui.util.IconHelper;
import au.org.ala.delta.util.Platform;

/**
 * Utilities for working with image icons.
 * 
 */
public class IconHelper {

    private static final String ICON_PATH = "/au/org/ala/delta/resources/icons";
    private static final String DELTA_BLUE_IMAGE_48 = ICON_PATH + "/Delta_blue_48.png";
    private static final String DELTA_BLUE_IMAGE_32 = ICON_PATH + "/Delta_blue_32.png";
    private static final String DELTA_BLUE_IMAGE_16 = ICON_PATH + "/Delta_blue_16.png";
    private static final String DELTA_BLUE_IMAGE_14 = ICON_PATH + "/Delta_blue_14.png";
    private static final String DELTA_BLUE_IMAGE_12 = ICON_PATH + "/Delta_blue_12.png";

    public static ImageIcon createImageIcon(String imageFileName) {
        return new ImageIcon(imageURLFromFileName(imageFileName));
    }

    public static ImageIcon createBlue32ImageIcon() {
        return createImageIcon(IconHelper.DELTA_BLUE_IMAGE_32);
    }

    public static ImageIcon createBlue16ImageIcon() {
        return createImageIcon(IconHelper.DELTA_BLUE_IMAGE_16);
    }

    public static ImageIcon createBlue14ImageIcon() {
        return createImageIcon(IconHelper.DELTA_BLUE_IMAGE_14);
    }
    
    public static ImageIcon createBlue12ImageIcon() {
        return createImageIcon(IconHelper.DELTA_BLUE_IMAGE_12);
    }

    public static List<? extends Image> getBlueIconList() {
        List<Image> list = new ArrayList<Image>();

        list.add(createImageIcon(DELTA_BLUE_IMAGE_48).getImage());
        list.add(createImageIcon(DELTA_BLUE_IMAGE_32).getImage());
        list.add(createImageIcon(DELTA_BLUE_IMAGE_16).getImage());
        list.add(createImageIcon(DELTA_BLUE_IMAGE_14).getImage());
        list.add(createImageIcon(DELTA_BLUE_IMAGE_12).getImage());

        return list;
    }

    private static URL imageURLFromFileName(String imageFileName) {
        URL imageUrl = IconHelper.class.getResource(ICON_PATH + "/" + imageFileName);
        return imageUrl;
    }
}

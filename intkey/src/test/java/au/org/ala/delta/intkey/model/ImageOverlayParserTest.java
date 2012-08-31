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
package au.org.ala.delta.intkey.model;

import java.util.List;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageOverlayParser;
import au.org.ala.delta.model.image.ImageType;
import junit.framework.TestCase;

public class ImageOverlayParserTest extends TestCase {

    public void testImageNotesWithAtSymbolInContent() throws Exception {
        String overlayText = "<@subject Adiantum raddianum (Starr)> <@imagenotes x=469 y=24 t=\\i{}Adiantum raddianum\\i0{} K. Presl. Photos by Forest and Kim Starr (starrimages@hear.org)>";
        ImageOverlayParser parser = new ImageOverlayParser();
        parser.setColorsBGR(true);
        List<ImageOverlay> overlayList = parser.parseOverlays(overlayText, ImageType.IMAGE_TAXON);
    }
}

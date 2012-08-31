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
package au.org.ala.delta.intkey.directives.invocation;

import java.awt.Color;
import java.net.URL;
import java.util.List;

import au.org.ala.delta.intkey.model.DisplayImagesReportType;
import au.org.ala.delta.intkey.model.ImageDisplayMode;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.rtf.RTFBuilder;

public class DisplayImagesDirectiveInvocation extends BasicIntkeyDirectiveInvocation {

    private ImageDisplayMode _displayMode;
    private DisplayImagesReportType _reportType;

    private ItemFormatter _itemFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, false);

    public void setDisplayMode(ImageDisplayMode displayMode) {
        this._displayMode = displayMode;
    }

    public void setReportType(DisplayImagesReportType reportType) {
        this._reportType = reportType;
    }

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        if (_displayMode != null) {
            context.setImageDisplayMode(_displayMode);
        }

        if (_reportType != null) {
            RTFBuilder builder = new RTFBuilder();
            builder.startDocument();

            switch (_reportType) {
            case MISSING_IMAGE_LIST:
                generateMissingImageList(context, builder);
                break;
            case CHARACTER_IMAGE_LIST:
                generateCharacterImageList(context, builder);
                break;
            case TAXON_IMAGE_LIST:
                generateTaxonImageList(context, builder);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized display image report type");
            }

            builder.endDocument();
            context.getUI().displayRTFReport(builder.toString(), "Display");
        }

        return true;
    }

    private void generateMissingImageList(IntkeyContext context, RTFBuilder builder) {
        builder.setTextColor(Color.RED);

        ImageSettings imgSettings = context.getImageSettings();
        List<Character> characters = context.getDataset().getCharactersAsList();
        List<Item> taxa = context.getDataset().getItemsAsList();

        int imgCount = 0;

        boolean missingImages = false;

        for (Character ch : characters) {
            List<Image> images = ch.getImages();
            for (Image image : images) {
                imgCount++;
                String fileName = image.getFileName();
                URL fileURL = imgSettings.findFileOnResourcePath(fileName, true);
                if (fileURL == null) {
                    builder.appendText(UIUtils.getResourceString("MissingCharactersList.CharacterImageFileNotFound", fileName, ch.getCharacterId()));
                    missingImages = true;
                }
            }
        }

        for (Item taxon : taxa) {
            List<Image> images = taxon.getImages();
            for (Image image : images) {
                imgCount++;
                String fileName = image.getFileName();
                URL fileURL = imgSettings.findFileOnResourcePath(fileName, true);
                if (fileURL == null) {
                    builder.appendText(UIUtils.getResourceString("MissingCharactersList.TaxonImageFileNotFound", fileName, _itemFormatter.formatItemDescription(taxon)));
                    missingImages = true;
                }
            }
        }

        // Display message if there are no images
        if (imgCount == 0) {
            builder.appendText(UIUtils.getResourceString("MissingImageList.NoImages"));
        } else {
            builder.appendText(UIUtils.getResourceString("MissingImageList.AllPresent"));
        }
    }

    private void generateCharacterImageList(IntkeyContext context, RTFBuilder builder) {
        List<Character> characters = context.getDataset().getCharactersAsList();

        int imgCount = 0;

        for (Character ch : characters) {
            List<Image> images = ch.getImages();
            for (Image image : images) {
                imgCount++;
                String fileName = image.getFileName();
                builder.appendText(fileName);
            }
        }

        // Display message if there are no images
        if (imgCount == 0) {
            builder.setTextColor(Color.RED);
            builder.appendText(UIUtils.getResourceString("CharacterImageList.NoImages"));
        }
    }

    private void generateTaxonImageList(IntkeyContext context, RTFBuilder builder) {
        List<Item> taxa = context.getDataset().getItemsAsList();

        int imgCount = 0;

        for (Item taxon : taxa) {
            List<Image> images = taxon.getImages();
            for (Image image : images) {
                imgCount++;
                String fileName = image.getFileName();
                builder.appendText(fileName);
            }
        }

        // Display message if there are no images
        if (imgCount == 0) {
            builder.setTextColor(Color.RED);
            builder.appendText(UIUtils.getResourceString("TaxonImageList.NoImages"));
        }
    }
}

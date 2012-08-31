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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import au.org.ala.delta.ui.image.ImageViewer;

/**
 * The TextOverlayLocation scales by converting the location information of the
 * overlay into pixels using the actual size of the image. If the height of the
 * overlay is specified as a number of lines, no scaling is applied to the
 * height of the overlay.
 */
public class TextOverlayLocation implements OverlayLocation {

    protected JComponent _component;
    protected ImageViewer _image;
    protected au.org.ala.delta.model.image.OverlayLocation _location;

    private static int HEIGHT_PADDING = 5;

    public TextOverlayLocation(ImageViewer image, JComponent component, au.org.ala.delta.model.image.OverlayLocation location) {
        _component = component;
        _image = image;
        _location = location;
    }

    @Override
    public int getX() {
        double scaledWidth = _image.getImageWidth();
        Point p = _image.getImageOrigin();
        return (int) Math.round(_location.X / 1000d * scaledWidth) + p.x;
    }

    @Override
    public int getY() {
        double scaledHeight = _image.getImageHeight();
        Point p = _image.getImageOrigin();
        return (int) Math.round(_location.Y / 1000d * scaledHeight) + p.y;
    }

    @Override
    public int getHeight() {
        if (_location.H <= Short.MIN_VALUE) {
            return _component.getPreferredSize().height;
        }

        int height = 0;
        if (_location.H < 0) {
            Font f = _component.getFont();
            FontMetrics m = _component.getFontMetrics(f);
            int lineHeight = m.getHeight();

            height = lineHeight * -_location.H;
        } else {
            // Fonts don't scale with the image so the height should use the
            // original image height.
            double scaledHeight = _image.getImageHeight();
            height = (int) (_location.H / 1000d * scaledHeight);
        }

        // Need to add some padding to the height, as the height being exactly
        // the height returned by
        // font metrics results in overlays unnecessarily being displayed inside
        // scroll panes.
        height = height + HEIGHT_PADDING;

        return height;
    }

    @Override
    public int getWidth() {
        if (_location.W <= 0) {
            return _component.getPreferredSize().width;
        }

        // Fonts don't scale with the image so the width should use the
        // original image width.
        double scaledWidth = _image.getImageWidth();

        int width = (int) (_location.W / 1000d * scaledWidth);

        return width;
    }

    @Override
    public void updateLocationFromBounds(Rectangle bounds) {
        double scaledWidth = _image.getImageWidth();
        Point p = _image.getImageOrigin();
        double toImageUnits = 1000d / scaledWidth;
        int x = (int) Math.round((bounds.x - p.x) * toImageUnits);

        _location.setX(x);
        _location.setW((int) (bounds.width * toImageUnits));

        double scaledHeight = _image.getImageHeight();
        toImageUnits = 1000d / scaledHeight;
        int y = (int) Math.round((bounds.y - p.y) * toImageUnits);
        _location.setY(y);
        _location.setH((int) (bounds.height * toImageUnits));
    }

}

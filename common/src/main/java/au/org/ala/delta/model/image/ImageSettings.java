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
package au.org.ala.delta.model.image;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import au.org.ala.delta.model.ResourceSettings;
import au.org.ala.delta.model.image.OverlayLocation.OLDrawType;
import au.org.ala.delta.util.Utils;

/**
 * The ImageSettings class maintains the defaults used when creating images and
 * image overlays.
 */
public class ImageSettings extends ResourceSettings {

    public static final int DEFAULT_FONT_SIZE = 10;

    /**
     * Fonts specified using delta's OVERLAY FONTS directive assume a DPI of
     * 120.
     */
    public static final int DELTA_DEFAULT_DPI_RESOLUTION = 120;

    private String _datasetName;

    public enum ButtonAlignment {
        NO_ALIGN, ALIGN_VERTICALLY, ALIGN_HORIZONTALLY
    };

    public enum OverlayFontType {
        OF_DEFAULT, OF_BUTTON, OF_FEATURE;
        // OL_FONT_LIST_END;

        public static OverlayFontType fromOrdinal(int ord) {
            return values()[ord];
        }

    };

    private static final ButtonAlignment DEFAULT_BUTTON_ALIGNMENT = ButtonAlignment.NO_ALIGN;

    public static class FontInfo {

        public FontInfo() {
            this(0, 0, false, 0, 0, 0, null);
            comment = null;
        }

        public FontInfo(int size, int weight, boolean italic, int pitch, int family, int charSet, String name) {
            this.size = size;
            this.weight = weight;
            this.italic = italic;
            this.pitch = pitch;
            this.family = family;
            this.charSet = charSet;
            this.name = name;
        }

        public int size;
        public int weight;
        public boolean italic;
        public int pitch;
        public int family;
        public int charSet;
        public String name;
        public String comment;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + charSet;
            result = prime * result + family;
            result = prime * result + (italic ? 1231 : 1237);
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + pitch;
            result = prime * result + size;
            result = prime * result + weight;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FontInfo other = (FontInfo) obj;
            if (charSet != other.charSet)
                return false;
            if (family != other.family)
                return false;
            if (italic != other.italic)
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (pitch != other.pitch)
                return false;
            if (size != other.size)
                return false;
            if (weight != other.weight)
                return false;
            return true;
        }

    }

    private FontInfo _defaultFontInfo;
    private FontInfo _defaultFeatureFontInfo;
    private FontInfo _defaultButtonFont;
    private boolean _centreInBox;
    private boolean _includeComments;
    private boolean _omitDescription;
    private boolean _useIntegralHeight;
    private boolean _hotspotsPopup;
    private boolean _useCustomPopupColour;
    private Color _customPopupColour;
    private ButtonAlignment _buttonAlignment;

    public ImageSettings() {
        _dataSetPath = null;
        _resourceLocations = new ArrayList<String>();
        _resourceLocations.add("images");

        _defaultFontInfo = new FontInfo(DEFAULT_FONT_SIZE, 4, false, 2, 2, 0, "MS Sans Serif");
        _defaultButtonFont = new FontInfo(DEFAULT_FONT_SIZE, 4, false, 2, 2, 0, "MS Sans Serif");
        _defaultFeatureFontInfo = new FontInfo(DEFAULT_FONT_SIZE, 7, false, 2, 2, 0, "MS Sans Serif");
    }

    public ImageSettings(String dataSetPath) {
        this();
        _dataSetPath = dataSetPath;
    }

    public FontInfo getDefaultFontInfo() {
        return _defaultFontInfo;
    }

    public void setDefaultFontInfo(FontInfo fontInfo) {
        _defaultFontInfo = fontInfo;
    }

    public Font getDefaultFont() {
        return fontInfoToFont(_defaultFontInfo);
    }

    public Font getDefaultFeatureFont() {
        return fontInfoToFont(_defaultFeatureFontInfo);
    }

    public FontInfo getDefaultFeatureFontInfo() {
        return _defaultFeatureFontInfo;
    }

    public void setDefaultFeatureFontInfo(FontInfo fontInfo) {
        _defaultFeatureFontInfo = fontInfo;
    }

    public Font getDefaultButtonFont() {
        return fontInfoToFont(_defaultButtonFont);
    }

    public FontInfo getDefaultButtonFontInfo() {
        return _defaultButtonFont;
    }

    public void setDefaultButtonFontInfo(FontInfo fontInfo) {
        _defaultButtonFont = fontInfo;
    }

    public int getFontCount() {
        int count = 0;
        if (_defaultButtonFont != null) {
            count++;
        }
        if (_defaultFontInfo != null) {
            count++;
        }
        if (_defaultFontInfo != null) {
            count++;
        }
        return count;
    }

    public boolean getCentreInBox() {
        return _centreInBox;
    }

    public void setCentreInBox(boolean centreInBox) {
        _centreInBox = centreInBox;
    }

    public boolean getIncludeComments() {
        return _includeComments;
    }

    public void setIncludeComments(boolean includeComments) {
        _includeComments = includeComments;
    }

    public boolean getOmitDescription() {
        return _omitDescription;
    }

    public void setOmitDescription(boolean omitDescription) {
        _omitDescription = omitDescription;
    }

    public boolean getUseIntegralHeight() {
        return _useIntegralHeight;
    }

    public void setUseIntegralHeight(boolean useIntegralHeight) {
        _useIntegralHeight = useIntegralHeight;
    }

    public boolean getHotspotsPopup() {
        return _hotspotsPopup;
    }

    public void setHotspotsPopup(boolean hotspotsPopup) {
        _hotspotsPopup = hotspotsPopup;
    }

    public boolean getUseCustomPopupColour() {
        return _useCustomPopupColour;
    }

    public void setUseCustomPopupColour(boolean useCustomPopupColour) {
        _useCustomPopupColour = useCustomPopupColour;
    }

    public Color getCustomPopupColour() {
        return _customPopupColour;
    }

    public void setCustomPopupColour(Color customPopupColour) {
        _customPopupColour = customPopupColour;
    }

    public ButtonAlignment getButtonAlignment() {
        if (_buttonAlignment == null) {
            return DEFAULT_BUTTON_ALIGNMENT;
        }
        return _buttonAlignment;
    }

    public void setButtonAlignment(ButtonAlignment buttonAlignment) {
        _buttonAlignment = buttonAlignment;
    }

    public FontInfo getFont(OverlayFontType type) {
        switch (type) {
        case OF_DEFAULT:
            return _defaultFontInfo;
        case OF_BUTTON:
            return _defaultButtonFont;
        case OF_FEATURE:
            return _defaultFeatureFontInfo;
        }
        throw new IllegalArgumentException("Unsupported font type: " + type);
    }

    public void setDefaultFont(Font font) {
        FontInfo fontInfo = getDefaultFontInfo();
        fontToFontInfo(font, fontInfo);
    }

    public void setDefaultFont(Font font, String comment) {
        setFont(font, comment, getDefaultFontInfo());
    }

    public void setDefaultFeatureFont(Font font) {
        FontInfo fontInfo = getDefaultFeatureFontInfo();
        fontToFontInfo(font, fontInfo);
    }

    public void setDefaultFeatureFont(Font font, String comment) {
        setFont(font, comment, getDefaultFeatureFontInfo());
    }

    public void setDefaultButtonFont(Font font) {
        FontInfo fontInfo = getDefaultButtonFontInfo();
        fontToFontInfo(font, fontInfo);
    }

    public void setDefaultButtonFont(Font font, String comment) {
        setFont(font, comment, getDefaultButtonFontInfo());
    }

    private void setFont(Font font, String comment, FontInfo fontInfo) {
        fontToFontInfo(font, fontInfo);
        fontInfo.comment = comment;
    }

    private void fontToFontInfo(Font font, FontInfo fontInfo) {
        fontInfo.name = font.getFamily();
        fontInfo.size = font.getSize();
        fontInfo.italic = font.isItalic();
        fontInfo.weight = font.isBold() ? 7 : 4;
    }

    private Font fontInfoToFont(FontInfo info) {
        int fontSize = info.size;
        if (fontSize == 0) {
            fontSize = DEFAULT_FONT_SIZE;
        }
        int style = info.italic ? Font.ITALIC : 0;
        style = style | (info.weight >= 5 ? Font.BOLD : 0);

        fontSize = Utils.adjustFontSize(fontSize, DELTA_DEFAULT_DPI_RESOLUTION);

        return new Font(info.name, style, fontSize);
    }

    public void configureHotSpotDefaults(OverlayLocation location) {
        location.drawType = OLDrawType.rectangle;
        location.setW(250);
        location.setH(250);
    }

    // The dataset name (or "heading") needs to be accessible via the image
    // settings so that the content of
    // "@heading" overlays for startup images can have their text set to be the
    // dataset name.

    public String getDatasetName() {
        return _datasetName;
    }

    public void setDatasetName(String datasetName) {
        this._datasetName = datasetName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ImageSettings))
            return false;

        ImageSettings that = (ImageSettings) o;

        if (_centreInBox != that._centreInBox)
            return false;
        if (_hotspotsPopup != that._hotspotsPopup)
            return false;
        if (_includeComments != that._includeComments)
            return false;
        if (_omitDescription != that._omitDescription)
            return false;
        if (_useCustomPopupColour != that._useCustomPopupColour)
            return false;
        if (_useIntegralHeight != that._useIntegralHeight)
            return false;
        if (getButtonAlignment() != that.getButtonAlignment())
            return false;
        if (_customPopupColour != null ? !_customPopupColour.equals(that._customPopupColour) : that._customPopupColour != null)
            return false;
        if (_defaultButtonFont != null ? !_defaultButtonFont.equals(that._defaultButtonFont) : that._defaultButtonFont != null)
            return false;
        if (_defaultFeatureFontInfo != null ? !_defaultFeatureFontInfo.equals(that._defaultFeatureFontInfo) : that._defaultFeatureFontInfo != null)
            return false;
        if (_defaultFontInfo != null ? !_defaultFontInfo.equals(that._defaultFontInfo) : that._defaultFontInfo != null)
            return false;
        if (!_resourceLocations.equals(that._resourceLocations))
            return false;
        if (!_remoteResourceLocations.equals(that._remoteResourceLocations))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _defaultFontInfo != null ? _defaultFontInfo.hashCode() : 0;
        result = 31 * result + (_defaultFeatureFontInfo != null ? _defaultFeatureFontInfo.hashCode() : 0);
        result = 31 * result + (_defaultButtonFont != null ? _defaultButtonFont.hashCode() : 0);
        result = 31 * result + (_centreInBox ? 1 : 0);
        result = 31 * result + (_includeComments ? 1 : 0);
        result = 31 * result + (_omitDescription ? 1 : 0);
        result = 31 * result + (_useIntegralHeight ? 1 : 0);
        result = 31 * result + (_hotspotsPopup ? 1 : 0);
        result = 31 * result + (_useCustomPopupColour ? 1 : 0);
        result = 31 * result + (_customPopupColour != null ? _customPopupColour.hashCode() : 0);
        result = 31 * result + (_buttonAlignment != null ? _buttonAlignment.hashCode() : 0);
        result = 31 * result + (_resourceLocations.hashCode());
        result = 31 * result + (_remoteResourceLocations.hashCode());

        return result;
    }
}

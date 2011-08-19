package au.org.ala.delta.model.image;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.image.OverlayLocation.OLDrawType;

/**
 * The ImageSettings class maintains the defaults used when creating images and
 * image overlays.
 */
public class ImageSettings {

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
    }

    private List<String> _imagePaths;
    private FontInfo _defaultFontInfo;
    private FontInfo _defaultFeatureFontInfo;
    private FontInfo _defaultButtonFont;
    private String _dataSetPath;
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
        _imagePaths = new ArrayList<String>();
        _imagePaths.add("images");

        _defaultFontInfo = new FontInfo(10, 4, false, 2, 2, 0, "MS Sans Serif");
        _defaultButtonFont = new FontInfo(10, 4, false, 2, 2, 0, "MS Sans Serif");
        _defaultFeatureFontInfo = new FontInfo(10, 7, false, 2, 2, 0, "MS Sans Serif");
    }

    public ImageSettings(String dataSetPath) {
        this();
        _dataSetPath = dataSetPath;
    }

    public void setDataSetPath(String path) {
        _dataSetPath = path;
    }

    // Convenience method for when there is only one image path
    public String getImagePath() {
        if (!_imagePaths.isEmpty()) {
            return getImagePaths().get(0);
        } else {
            return null;
        }
    }

    public List<String> getImagePaths() {
        List<String> retList = new ArrayList<String>();

        for (String imagePath : _imagePaths) {
            if (imagePath.startsWith("http") || new File(imagePath).isAbsolute() || StringUtils.isEmpty(_dataSetPath)) {
                retList.add(imagePath);
            } else {
                retList.add(_dataSetPath + File.separator + imagePath);
            }
        }

        return retList;
    }

    // Convenience method for when there is only one image path
    public void setImagePath(String imagePath) {
        _imagePaths = new ArrayList<String>();
        _imagePaths.add(imagePath);
    }

    public void setImagePaths(List<String> imagePaths) {
        _imagePaths = new ArrayList<String>(imagePaths);
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

    private Font fontInfoToFont(FontInfo info) {
        if (info.size == 0) {
            return null;
        }
        int style = info.italic ? Font.ITALIC : 0;
        style = style | (info.weight > 500 ? Font.BOLD : 0);
        return new Font(info.name, style, Math.abs(info.size));
    }

    public void configureHotSpotDefaults(OverlayLocation location) {
        location.drawType = OLDrawType.rectangle;
        location.setW(250);
        location.setH(250);
    }
    
    public URL findFileOnImagePath(String fileName) {
        URL fileLocation = null;
        for (String imagePath : getImagePaths()) {
            try {
                if (imagePath.toLowerCase().startsWith("http")) {
                    fileLocation = new URL(imagePath + fileName);

                    // Try opening a stream to the remote file. If no exceptions
                    // are thrown, the file
                    // was successfully found at that location. Unfortunately
                    // there is no better way to
                    // test existence of a remote file.
                    fileLocation.openStream();
                    break;
                } else {
                    File f = new File(imagePath + File.separator + fileName);
                    if (f.exists()) {
                        fileLocation = f.toURI().toURL();
                        break;
                    }

                }

            } catch (IOException ioexception) {
                // do nothing, keep searching on image path.
            }
        }

        return fileLocation;
    }

}

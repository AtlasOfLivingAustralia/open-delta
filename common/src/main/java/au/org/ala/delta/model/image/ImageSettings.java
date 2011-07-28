package au.org.ala.delta.model.image;

import java.awt.Color;
import java.awt.Font;

/**
 * The ImageSettings class maintains the defaults used when creating images and
 * image overlays.
 */
public class ImageSettings {

	public enum ButtonAlignment {NO_ALIGN, ALIGN_VERTICALLY, ALIGN_HORIZONTALLY};
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
	
	private String _imagePath = "images";
	private FontInfo _defaultFontInfo;
	private FontInfo _defaultFeatureFontInfo;
	private FontInfo _defaultButtonFont;
	
	public String getImagePath() {
		return _imagePath;
	}
	
	public void setImagePath(String imagePath) {
		_imagePath = imagePath;	
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
		return false;
	}
	
	public boolean getIncludeComments() {
		return false;
	}
	
	public boolean getOmitDescription() {
		return false;
	}
	
	public boolean getUseIntegralHeight() {
		return false;
	}
	
	public boolean getHotspotsPopUp() {
		return false;
	}
	
	public Color getCustomPopupColour() {
		return null;
	}
	
	public ButtonAlignment getButtonAlignment() {
		return ButtonAlignment.NO_ALIGN;
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
		throw new IllegalArgumentException("Unsupported font type: "+type);
	}
	
	private Font fontInfoToFont(FontInfo info) {
		if (info.size == 0) {
			return null;
		}
		int style = info.italic ? Font.ITALIC : 0;
		style = style | (info.weight > 500 ? Font.BOLD : 0); 
		return new Font(info.name, style, Math.abs(info.size));
	}

	
	
}

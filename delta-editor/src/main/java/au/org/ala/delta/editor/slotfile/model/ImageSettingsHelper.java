package au.org.ala.delta.editor.slotfile.model;

import java.awt.Color;

import au.org.ala.delta.editor.slotfile.VOImageInfoDesc;
import au.org.ala.delta.editor.slotfile.VOImageInfoDesc.LOGFONT;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.ImageSettings.FontInfo;
import au.org.ala.delta.model.image.ImageSettings.OverlayFontType;
import au.org.ala.delta.model.image.OverlayLocation;
import au.org.ala.delta.util.Pair;

/**
 * Helper class for converting between a VOImageInfoDesc and ImageSettings
 * object.
 */
public class ImageSettingsHelper {

	public static void copyToImageSettings(VOImageInfoDesc imageInfo, ImageSettings settings) {
		Pair<LOGFONT, String> pair = imageInfo.readOverlayFont(OverlayFontType.OF_DEFAULT);
		if (pair.getFirst()!= null && pair.getSecond() != null) {
			FontInfo fontInfo = toFontInfo(pair);
			settings.setDefaultFontInfo(fontInfo);
		}
		
		pair = imageInfo.readOverlayFont(OverlayFontType.OF_FEATURE);
		if (pair.getFirst()!= null && pair.getSecond() != null) {			
			FontInfo fontInfo = toFontInfo(pair);
			settings.setDefaultFeatureFontInfo(fontInfo);
		}
		
		pair = imageInfo.readOverlayFont(ImageSettings.OverlayFontType.OF_BUTTON);
		if (pair.getFirst()!= null && pair.getSecond() != null) {
			FontInfo fontInfo = toFontInfo(pair);
			settings.setDefaultButtonFontInfo(fontInfo);
		}
		
		settings.setResourcePath(imageInfo.readImagePath());
		
		ImageOverlay overlay = new ImageOverlay();
		OverlayLocation tmpLocation = new OverlayLocation();
		tmpLocation.flags = imageInfo.getOverlayDefaults();
		overlay.addLocation(tmpLocation);
		settings.setCentreInBox(overlay.centreText());
		settings.setIncludeComments(overlay.includeComments());
		settings.setOmitDescription(overlay.omitDescription());
		settings.setUseIntegralHeight(overlay.integralHeight());
		
		tmpLocation.flags = imageInfo.getHotspotDefaults();
		settings.setHotspotsPopup(tmpLocation.isPopup());
		settings.setUseCustomPopupColour(tmpLocation.getUseCustomColour());
		settings.setCustomPopupColour(new Color(tmpLocation.getColor()));
	}
	
	public static void copyFromImageSettings(VOImageInfoDesc imageInfo, ImageSettings settings) {
		Pair<LOGFONT, String> defaultFont = fromFontInfo(settings.getDefaultFontInfo());
		imageInfo.writeOverlayFont(OverlayFontType.OF_DEFAULT, defaultFont.getSecond(), defaultFont.getFirst());
		Pair<LOGFONT, String> featureFont = fromFontInfo(settings.getDefaultFeatureFontInfo());
		imageInfo.writeOverlayFont(OverlayFontType.OF_FEATURE, featureFont.getSecond(), featureFont.getFirst());
		Pair<LOGFONT, String> buttonFont = fromFontInfo(settings.getDefaultButtonFontInfo());
		imageInfo.writeOverlayFont(OverlayFontType.OF_BUTTON, buttonFont.getSecond(), buttonFont.getFirst());
	
		imageInfo.writeImagePath(settings.getResourcePath());
		
		ImageOverlay overlay = new ImageOverlay();
		OverlayLocation tmpLocation = new OverlayLocation();
		overlay.addLocation(tmpLocation);
		overlay.setCentreText(settings.getCentreInBox());
		overlay.setIncludeComments(settings.getIncludeComments());
		overlay.setOmitDescription(settings.getOmitDescription());
		overlay.setIntegralHeight(settings.getUseIntegralHeight());
		imageInfo.setOverlayDefaults((short)tmpLocation.flags);
		
		tmpLocation = new OverlayLocation();
		tmpLocation.setPopup(settings.getHotspotsPopup());
		tmpLocation.setUseCustomColour(settings.getUseCustomPopupColour());
		tmpLocation.setColor(settings.getCustomPopupColour().getRGB());
		imageInfo.setHotspotDefaults(tmpLocation.flags);
		
	}
	
	public static FontInfo toFontInfo(Pair<LOGFONT, String> values) {
		FontInfo info = new FontInfo();
		LOGFONT font = values.getFirst();
		info.size = -(font.lfHeight * 72 / 120);
		info.weight = font.lfWeight/100;
		info.italic = font.lfItalic == 0 ? false : true;
		info.pitch = font.lfPitchAndFamily & 3;
		info.family = (font.lfPitchAndFamily & 0xf0) >> 4;
		info.charSet = font.lfCharSet;
		info.name = font.getLfFaceName();
		
		info.comment = values.getSecond();
		
		return info;
	}
	
	public static Pair<LOGFONT, String> fromFontInfo(FontInfo fontInfo) {
		LOGFONT font = new LOGFONT();
		font.lfHeight = -(fontInfo.size * 120 / 72);
		font.lfWeight = fontInfo.weight * 100;
		font.lfItalic = (fontInfo.italic ? (byte)1 : (byte)0);
		font.lfPitchAndFamily |= fontInfo.pitch;
		font.lfPitchAndFamily |= fontInfo.family << 4;
		font.lfCharSet = (byte)fontInfo.charSet;
		font.setLfFaceName(fontInfo.name);
		
		return new Pair<LOGFONT, String>(font, fontInfo.comment);
	}
}

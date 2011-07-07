package au.org.ala.delta.editor.slotfile.model;

import au.org.ala.delta.editor.slotfile.VOImageInfoDesc;
import au.org.ala.delta.editor.slotfile.VOImageInfoDesc.LOGFONT;
import au.org.ala.delta.editor.slotfile.VOImageInfoDesc.OverlayFontType;
import au.org.ala.delta.io.BinFileEncoding;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.ImageSettings.FontInfo;
import au.org.ala.delta.util.Pair;

public class ImageSettingsHelper {

	public static void copyToImageSettings(VOImageInfoDesc imageInfo, ImageSettings settings) {
		FontInfo fontInfo = toFontInfo(imageInfo.readOverlayFont(OverlayFontType.OF_DEFAULT));
		settings.setDefaultFontInfo(fontInfo);
		
		fontInfo = toFontInfo(imageInfo.readOverlayFont(OverlayFontType.OF_FEATURE));
		settings.setDefaultFeatureFontInfo(fontInfo);
		
		fontInfo = toFontInfo(imageInfo.readOverlayFont(OverlayFontType.OF_BUTTON));
		settings.setDefaultButtonFontInfo(fontInfo);
		
		settings.setImagePath(imageInfo.readImagePath());
	}
	
	public static void copyFromImageSettings(VOImageInfoDesc imageInfo, ImageSettings settings) {
		
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
		info.name = BinFileEncoding.decode(font.lfFaceName);
		
		info.comment = values.getSecond();
		
		return info;
	}
}

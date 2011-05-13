package au.org.ala.delta.editor.model;

import java.awt.Color;
import java.awt.Font;

/**
 * The ImageSettings class maintains the defaults used when creating images and
 * image overlays.
 */
public class ImageSettings {

	public enum ButtonAlignment {NO_ALIGN, ALIGN_VERTICALLY, ALIGN_HORIZONTALLY};
	
	
	private String _imagePath = "images";
	
	public String getImagePath() {
		return _imagePath;
	}
	
	public Font getDefaultFont() {
		return null;
	}
	
	public Font getDefaultFeatureFont() {
		return null;
	}
	
	public Font getDefaultButtonFont() {
		return null;
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
	
}

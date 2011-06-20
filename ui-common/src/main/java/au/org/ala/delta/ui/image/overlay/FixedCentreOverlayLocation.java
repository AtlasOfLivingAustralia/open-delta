package au.org.ala.delta.ui.image.overlay;

import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.JComponent;

import au.org.ala.delta.ui.image.ImageViewer;

public class FixedCentreOverlayLocation implements OverlayLocation {

	private JComponent _component;
	private ImageViewer _image;
	private au.org.ala.delta.model.image.OverlayLocation _location;
	
	public FixedCentreOverlayLocation(ImageViewer image, JComponent component, au.org.ala.delta.model.image.OverlayLocation location) {
		_component = component;
		_image = image;
		_location = location;
	}
	
	@Override
	public int getX() {
		double scaledWidth = _image.getImageWidth();
		double width = _image.getPreferredImageWidth();
		
		double toPixels = width / 1000d;
		
		double halfComponentWidth = 0.5 * getWidth();
		double midPointXInPixels = _location.X*toPixels + halfComponentWidth;
		
		double imageScale = scaledWidth/width;
		
		return (int)Math.round(midPointXInPixels*imageScale - halfComponentWidth);
	}
	
	@Override
	public int getY() {
		double scaledHeight = _image.getImageHeight();
		double height = _image.getPreferredImageHeight();
		
		double toPixels = height / 1000d;
		
		double halfComponentHeight = 0.5 * getHeight();
		double midPointYInPixels = _location.Y*toPixels + halfComponentHeight;
		
		double imageScale = scaledHeight/height;
		
		
		return (int)Math.round(midPointYInPixels*imageScale - halfComponentHeight);
	}
	
	
	@Override
	public int getHeight() {
		if (_location.H == 0) {
			return _component.getPreferredSize().height;
		}
		if (_location.H < 0) {
			Font f = _component.getFont();
			FontMetrics m = _component.getFontMetrics(f);
			int lineHeight = m.getHeight();
			return lineHeight * -_location.H;
		}
		
		
		// Fonts don't scale with the image so the height should use the
		// original image height.
		double scaledHeight = _image.getPreferredImageHeight();
		
		return (int)(_location.H / 1000d * scaledHeight);
	}
	
	
	@Override
	public int getWidth() {
		if (_location.W == 0) {
			return _component.getPreferredSize().width;
		}
		
		// Fonts don't scale with the image so the width should use the
		// original image width.
		double scaledWidth = _image.getPreferredImageWidth();
		
		return (int)(_location.W / 1000d * scaledWidth);
	}
	
}

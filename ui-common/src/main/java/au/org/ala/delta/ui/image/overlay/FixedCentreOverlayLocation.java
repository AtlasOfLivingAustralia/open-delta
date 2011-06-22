package au.org.ala.delta.ui.image.overlay;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.border.Border;

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
		
		Point p = _image.getImageOrigin();
		return (int)Math.round(midPointXInPixels*imageScale - halfComponentWidth)+p.x;
	}
	
	@Override
	public int getY() {
		double scaledHeight = _image.getImageHeight();
		double height = _image.getPreferredImageHeight();
		
		double toPixels = height / 1000d;
		
		double halfComponentHeight = 0.5 * getHeight();
		double midPointYInPixels = _location.Y*toPixels + halfComponentHeight;
		
		double imageScale = scaledHeight/height;
		
		Point p = _image.getImageOrigin();
		return (int)Math.round(midPointYInPixels*imageScale - halfComponentHeight)+p.y;
	}
	
	
	@Override
	public int getHeight() {
		if (_location.H == 0) {
			return _component.getPreferredSize().height;
		}
		
		int height = 0;
		if (_location.H < 0) {
			Font f = _component.getFont();
			FontMetrics m = _component.getFontMetrics(f);
			int lineHeight = m.getHeight();
			
			height = lineHeight * -_location.H;
		}
		else {
			// Fonts don't scale with the image so the height should use the
			// original image height.
			double scaledHeight = _image.getPreferredImageHeight();
			height = (int)(_location.H / 1000d * scaledHeight);
		}
		
		// Add room for the border.
		Insets insets = borderInsets(_component);
		height += insets.top + insets.bottom;
		
		return height; 
	}
	
	
	@Override
	public int getWidth() {
		if (_location.W == 0) {
			return _component.getPreferredSize().width;
		}
		
		// Fonts don't scale with the image so the width should use the
		// original image width.
		double scaledWidth = _image.getPreferredImageWidth();
		
		int width = (int)(_location.W / 1000d * scaledWidth);
		
		// Add room for the border.
		Insets insets = borderInsets(_component);
		width += insets.left + insets.right;
		return width;
	}
	
	
	public Insets borderInsets(JComponent component) {
		Border b = _component.getBorder();
		Insets insets = null;
		if (b != null) {
			insets = b.getBorderInsets(_component);
		}
		else {
			insets = new Insets(0, 0, 0, 0);
		}
		return insets;
	}
}

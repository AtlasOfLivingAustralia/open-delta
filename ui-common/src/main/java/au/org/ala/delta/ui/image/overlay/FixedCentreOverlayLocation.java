package au.org.ala.delta.ui.image.overlay;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import au.org.ala.delta.ui.image.ImageViewer;

/**
 * The FixedCentreOverlayLocation adjusts the centre position of the overlay
 * component as the image resizes but does not modify the size.
 */
public class FixedCentreOverlayLocation implements OverlayLocation {

	protected JComponent _component;
	protected ImageViewer _image;
	protected au.org.ala.delta.model.image.OverlayLocation _location;
	
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
		if (_location.H <= Short.MIN_VALUE) {
			return _component.getPreferredSize().height;
		}
		
		int height = 0;
		if (_location.H < 0) {
			Font f = _component.getFont();
			FontMetrics m = _component.getFontMetrics(f);
			int lineHeight = m.getHeight();
			
			height = lineHeight * -_location.H;
			System.out.println("H="+height);
		}
		else {
			// Fonts don't scale with the image so the height should use the
			// original image height.
			double scaledHeight = _image.getPreferredImageHeight();
			height = (int)(_location.H / 1000d * scaledHeight);
		}
		
		return height; 
	}
	
	
	@Override
	public int getWidth() {
		if (_location.W <= 0) {
			return _component.getPreferredSize().width;
		}
		
		// Fonts don't scale with the image so the width should use the
		// original image width.
		double scaledWidth = _image.getPreferredImageWidth();
		
		int width = (int)(_location.W / 1000d * scaledWidth);
		
		return width;
	}

	@Override
	public void updateLocationFromBounds(Rectangle bounds) {
		double scaledWidth = _image.getImageWidth();
		double width = _image.getPreferredImageWidth();
		double imageScale = scaledWidth/width;
		
		double halfComponentWidth = 0.5 * bounds.width;
		double midPointXInPixels = bounds.x + halfComponentWidth;
		double toImageUnits = 1000/width;
		
		Point p = _image.getImageOrigin();
		
		int x = (int)Math.round((midPointXInPixels/imageScale-halfComponentWidth)*toImageUnits - p.x);
		_location.setX(x);
		_location.setW((int)(bounds.width*toImageUnits));
		
		double scaledHeight = _image.getImageHeight();
		double height = _image.getPreferredImageHeight();
		imageScale = scaledHeight/height;
		
		double halfComponentHeight = 0.5 * bounds.height;
		double midPointYInPixels = bounds.y + halfComponentHeight;
		toImageUnits = 1000/height;
		int y = (int)Math.round((midPointYInPixels/imageScale-halfComponentHeight)*toImageUnits - p.y);
		_location.setY(y);
		_location.setH((int)(bounds.height*toImageUnits));
		
	}
	
}

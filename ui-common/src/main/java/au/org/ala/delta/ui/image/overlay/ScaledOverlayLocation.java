package au.org.ala.delta.ui.image.overlay;

import java.awt.Point;
import java.awt.Rectangle;

import au.org.ala.delta.ui.image.ImageViewer;


/**
 * A ScaledOverlayLocation simply converts the location information of the
 * overlay into pixels using the actual size of the image.
 */
public class ScaledOverlayLocation implements OverlayLocation {

	private ImageViewer _image;
	private au.org.ala.delta.model.image.OverlayLocation _location;
	
	public ScaledOverlayLocation(ImageViewer image, au.org.ala.delta.model.image.OverlayLocation location) {
		_image = image;
		_location = location;
	}
	
	public int getX() {
		double scaledWidth = _image.getImageWidth();
		Point p = _image.getImageOrigin();
		return (int)Math.round(_location.X / 1000d * scaledWidth)+p.x;
	}
	
	public int getY() {
		double scaledHeight = _image.getImageHeight();
		Point p = _image.getImageOrigin();
		return (int)Math.round(_location.Y / 1000d * scaledHeight)+p.y;
	}
	
	public int getHeight() {

		double scaledHeight = _image.getImageHeight();
		
		return (int)(_location.H / 1000d * scaledHeight);		
	}
	
	public int getWidth() {
		
		double scaledWidth = _image.getImageWidth();
		
		return (int)Math.round(_location.W / 1000d * scaledWidth);
	}
	
	@Override
	public void updateLocationFromBounds(Rectangle bounds) {
		
		double scaledWidth = _image.getImageWidth();
		Point p = _image.getImageOrigin();
		double toImageUnits = 1000d/scaledWidth;
		int x = (int)Math.round((bounds.x-p.x)*toImageUnits);
		
		_location.setX(x);
		_location.setW((int)(bounds.width*toImageUnits));
		
		double scaledHeight = _image.getImageHeight();
		toImageUnits = 1000d/scaledHeight;
		int y = (int)Math.round((bounds.y-p.y)*toImageUnits);
		_location.setY(y);
		_location.setH((int)(bounds.height*toImageUnits));
	}
	
}

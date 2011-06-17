package au.org.ala.delta.ui.image.overlay;

import javax.swing.JComponent;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.ImageViewer;

public class OverlayLocation {

	private JComponent _component;
	private ImageOverlay _overlay;
	private ImageViewer _image;
	
	public OverlayLocation(ImageViewer image, JComponent component, ImageOverlay overlay) {
		_component = component;
		_overlay = overlay;
		_image = image;
	}
	
	public int getX() {
		double scaledWidth = _image.getImageWidth();
		double width = _image.getPreferredImageWidth();
		
		double toPixels = width / 1000d;
		
		double halfComponentWidth = 0.5 * preferredWidth();
		double midPointXInPixels = _overlay.getX()*toPixels + halfComponentWidth;
		
		double imageScale = scaledWidth/width;
		
		return (int)Math.round(midPointXInPixels*imageScale - halfComponentWidth);
	}
	
	public int getY() {
		double scaledHeight = _image.getImageHeight();
		double height = _image.getPreferredImageHeight();
		
		double toPixels = height / 1000d;
		
		double halfComponentHeight = 0.5 * preferredHeight();
		double midPointYInPixels = _overlay.getY()*toPixels + halfComponentHeight;
		
		double imageScale = scaledHeight/height;
		
		
		return (int)Math.round(midPointYInPixels*imageScale - halfComponentHeight);
	}
	
	public int preferredHeight() {
		return _component.getPreferredSize().height;
	}
	
	public int preferredWidth() {
		return _component.getPreferredSize().width;
	}
	
}

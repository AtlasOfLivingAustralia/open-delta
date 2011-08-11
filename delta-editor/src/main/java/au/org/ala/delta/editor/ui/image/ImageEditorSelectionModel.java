package au.org.ala.delta.editor.ui.image;

import java.awt.Point;

import javax.swing.JComponent;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;

/**
 * Tracks selections made in an ImageEditorPanel.
 */
public class ImageEditorSelectionModel {

	private Point _selectedPoint;
	private Image _selectedImage;
	private JComponent _selectedComponent;
	
	public Point getSelectedPoint() {
		return _selectedPoint;
	}
	public void setSelectedPoint(Point selectedPoint) {
		_selectedPoint = selectedPoint;
	}
	public Image getSelectedImage() {
		return _selectedImage;
	}
	public void setSelectedImage(Image selectedImage) {
		_selectedImage = selectedImage;
	}
	public ImageOverlay getSelectedOverlay() {
		return (ImageOverlay)_selectedComponent.getClientProperty("ImageOverlay");
	}
	public void setSelectedOverlayComponent(JComponent component) {
		_selectedComponent = component;
	}
	
}

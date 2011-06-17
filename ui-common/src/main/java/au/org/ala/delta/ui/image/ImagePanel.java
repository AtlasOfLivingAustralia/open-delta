package au.org.ala.delta.ui.image;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Displays a single image, scaled if necessary to fit this JPanel.
 */
public class ImagePanel extends JPanel {

	private static final long serialVersionUID = -1203009970081375666L;
	protected BufferedImage _image;
	protected Image _scaledImage;
	
	/**
	 * Displays this image in this panel.
	 * @param imageFileLocation the location on the file system of the image to display.
	 */
	public void displayImage(URL imageFileLocation) {
		
		try {
			_image = ImageIO.read(imageFileLocation);
			_scaledImage = _image;
			
			setPreferredSize(new Dimension(_image.getWidth(), _image.getHeight()));
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to load image: "+imageFileLocation);
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		if (_scaledImage == null || _scaledImage.getWidth(null) != getWidth() || _scaledImage.getHeight(null) != getHeight()) {
			scaleImage();
		}
		g.drawImage(_scaledImage, 0, 0, null);
	}
	
	/**
	 * Scales the image to the size of this panel and caches it.
	 */
	protected void scaleImage() {
		// TODO change to progressive bilinear scaling...
		_scaledImage = _image.getScaledInstance(getWidth(), getHeight(), 0);
	}
	
	public int getPreferredImageWidth() {
		return _image.getWidth();
	}
	
	public int getPreferredImageHeight() {
		return _image.getHeight();
	}
	
	public int getImageWidth() {
		if (_scaledImage == null) {
			return getPreferredImageWidth();
		}
		return _scaledImage.getWidth(null);
	}
	
	public int getImageHeight() {
		if (_scaledImage == null) {
			return getPreferredImageHeight();
		}
		return _scaledImage.getHeight(null);
	}
}

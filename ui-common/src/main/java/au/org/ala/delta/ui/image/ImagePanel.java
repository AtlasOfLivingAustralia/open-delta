package au.org.ala.delta.ui.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
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
	/** Ratio of width to height of the unscaled image */
	private double _unscaledRatio;
	
	public ImagePanel() {
		setBackground(Color.WHITE);
		setOpaque(true);
	}
	
	/**
	 * Displays this image in this panel.
	 * @param imageFileLocation the location on the file system of the image to display.
	 */
	public void displayImage(URL imageFileLocation) {
		
		try {
			_image = ImageIO.read(imageFileLocation);
			_scaledImage = _image;
			
			_unscaledRatio = (double)getPreferredImageWidth()/(double)getPreferredImageHeight();
			dumpImageDetails();
			setPreferredSize(new Dimension(_image.getWidth(null), _image.getHeight(null)));
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to load image: "+imageFileLocation);
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (_scaledImage == null || _scaledImage.getWidth(null) != getWidth() || _scaledImage.getHeight(null) != getHeight()) {
			scaleImage();
		}
		Point origin = getImageOrigin();
		
		g.drawImage(_scaledImage, origin.x, origin.y, null);
	}
	
	/**
	 * Scales the image to the size of this panel and caches it.
	 */
	protected void scaleImage() {
		
		if (widthLimited()) {
			_scaledImage = _image.getScaledInstance(getWidth(), -1, 0);
		}
		else {
			_scaledImage = _image.getScaledInstance(-1, getHeight(), 0);
		}
	}
	
	public int getPreferredImageWidth() {
		return _image.getWidth(null);
	}
	
	public int getPreferredImageHeight() {
		return _image.getHeight(null);
	}
	
	
	
	public Point getImageOrigin() {
		int x = 0;
		int y = 0;
		
		if (widthLimited()) {
			y = (getHeight() - getImageHeight())/2;
		}
		else {
			x = (getWidth() - getImageWidth())/2;	
		}
		
		return new Point(Math.abs(x), Math.abs(y));
	}
	
	public int getImageWidth() {
		
		if (widthLimited()) {
			return getWidth();
		}
		else {
			return (int)(getHeight() * _unscaledRatio);
		}
	}
	
	public int getImageHeight() {
		
		if (widthLimited()) {
			return (int)(getWidth() / _unscaledRatio);
		}
		else {
			return getHeight();
		}
	}
	
	private boolean widthLimited() {
		Rectangle bounds = getBounds();
		double actualRatio = (double)bounds.width/(double)bounds.height;
		
		return _unscaledRatio > actualRatio;
	}
	
	private void dumpImageDetails() {
		System.out.println(_image.getColorModel().getNumColorComponents());
		ColorModel mc = _image.getColorModel();
		System.out.println(mc);
	}
}

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
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * Displays a single image, scaled if necessary to fit this JPanel.
 * The scaling behaviour may be modified by means of the setScalingMode method.
 */
public class ImagePanel extends JPanel implements Scrollable {

	private int _maxScrollableUnitIncrement = 5;
	private static final long serialVersionUID = -1203009970081375666L;
	protected BufferedImage _image;
	protected Image _scaledImage;
	private ScalingMode _scalingMode;
	private ScalingStrategy _scalingStrategy;
	
	public enum ScalingMode {NO_SCALING, FIXED_ASPECT_RATIO, FILL_AVAILABLE_SPACE};
	
	public ImagePanel() {
		setBackground(Color.WHITE);
		setOpaque(true);
		_scalingMode = ScalingMode.FIXED_ASPECT_RATIO;
	}
	
	/**
	 * Displays this image in this panel.
	 * @param imageFileLocation the location on the file system of the image to display.
	 */
	public void displayImage(URL imageFileLocation) {
		
		try {
			_image = ImageIO.read(imageFileLocation);
			_scaledImage = _image;
			
			setScalingMode(_scalingMode);
			
			dumpImageDetails();
			setPreferredSize(new Dimension(_image.getWidth(null), _image.getHeight(null)));
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to load image: "+imageFileLocation);
		}
	}
	
	public void setScalingMode(ScalingMode mode) {
		_scalingMode = mode;
		
		switch (mode) {
		case NO_SCALING:
			_scalingStrategy = new NoScalingStrategy(_image);
			break;
		case FIXED_ASPECT_RATIO:
			_scalingStrategy = new FixedAspectRatioScalingStrategy(_image);
			break;
		case FILL_AVAILABLE_SPACE:
			_scalingStrategy = new FullSizeScalingStrategy();
			break;
		}
	}
	
	/**
	 * Draws the image (scaled if requested) onto the background of the
	 * panel.
	 */
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
		_scaledImage = _scalingStrategy.getScaledImage(_image);
	}
	
	public int getPreferredImageWidth() {
		return _image.getWidth(null);
	}
	
	public int getPreferredImageHeight() {
		return _image.getHeight(null);
	}
	
	
	/**
	 * Delegates to the current ScalingStrategy.
	 */
	public Point getImageOrigin() {
		return _scalingStrategy.getScaledImageOrigin();
	}
	
	/**
	 * Delegates to the current ScalingStrategy.
	 */
	public int getImageWidth() {
		return _scalingStrategy.getScaledImageSize().width;
	}
	
	/**
	 * Delegates to the current ScalingStrategy.
	 */
	public int getImageHeight() {
		return _scalingStrategy.getScaledImageSize().height;
	}
	
	
	
	private void dumpImageDetails() {
		ColorModel mc = _image.getColorModel();
		System.out.println(mc);
	}
	
	
	
	
	/**
	 * A ScalingStrategy is responsible for determining how the image
	 * is scaled as the available size to display the image changes. 
	 */
	interface ScalingStrategy {
		public Dimension getScaledImageSize();
		public Point getScaledImageOrigin();
		public Image getScaledImage(Image unscaledImage);
	}
	
	
	/**
	 * The FixedAspectRatioScalingStrategy scales the image as the size
	 * of the display area changes but will keep the aspect ratio of the 
	 * image the same as the size changes.  The image will be centred in the
	 * remaining space.
	 */
	class FixedAspectRatioScalingStrategy implements ScalingStrategy {
		
		/** The aspect ratio of width to height of the unscaled image */
		private double _unscaledAspectRatio;
		public FixedAspectRatioScalingStrategy(BufferedImage unscaledImage) {
			_unscaledAspectRatio = (double)unscaledImage.getWidth()/(double)unscaledImage.getHeight();
		}
		public Dimension getScaledImageSize() {

			int width = 0;
			int height = 0;
			
			if (widthLimited()) {
				width = getWidth();
				height = (int)(getWidth() / _unscaledAspectRatio);
			}
			else {
				width = (int)(getHeight() * _unscaledAspectRatio);
				height = getHeight();
			}
			
			return new Dimension(width, height);
		}
		
		public Point getScaledImageOrigin() {
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
	
		public Image getScaledImage(Image unscaledImage) {
			Image scaledImage = null;
			if (widthLimited()) {
				scaledImage = unscaledImage.getScaledInstance(getWidth(), -1, 0);
			}
			else {
				scaledImage = unscaledImage.getScaledInstance(-1, getHeight(), 0);
			}
			return scaledImage;
		}
		
		
		private boolean widthLimited() {
			Rectangle bounds = getBounds();
			double actualRatio = (double)bounds.width/(double)bounds.height;
			
			return _unscaledAspectRatio > actualRatio;
		}
	}
	

	/**
	 * The FullSizeScalingStrategy scales the image to fill the available
	 * display area.
	 */
	class FullSizeScalingStrategy implements ScalingStrategy {
		
		public Dimension getScaledImageSize() {
			return getSize();
		}
		
		public Point getScaledImageOrigin() {
			
			return new Point(0, 0);
		}
	
		public Image getScaledImage(Image unscaledImage) {
			
			return unscaledImage.getScaledInstance(getWidth(), getHeight(), 0);
		}
	}
	
	/**
	 * The NoScalingStrategy centres the image in the available space but
	 * does not scale it.
	 */
	class NoScalingStrategy implements ScalingStrategy {
		
		private Dimension _unscaledImageSize;
		private Image _unscaledImage;
		
		public NoScalingStrategy(Image unscaledImage) {
			_unscaledImage = unscaledImage;
			_unscaledImageSize = new Dimension(_image.getWidth(), _image.getHeight());
		}
		public Dimension getScaledImageSize() {
			return _unscaledImageSize;
		}
		
		public Point getScaledImageOrigin() {
			int dx = (getWidth()-_unscaledImageSize.width)/2;
			int dy = (getHeight() - _unscaledImageSize.height)/2;
			
			return new Point(Math.max(0, dx), Math.max(0, dy));
		}
	
		public Image getScaledImage(Image unscaledImage) {
			return _unscaledImage;
		}
	}
	
	/* The portion of code (below) that implements the Scrollable interface was
	 * taken from the Swing tutorial:
	 * http://download.oracle.com/javase/tutorial/uiswing/examples/components/ScrollDemoProject/src/components/ScrollablePicture.java
	 */
	/*
	 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
	 *
	 * Redistribution and use in source and binary forms, with or without
	 * modification, are permitted provided that the following conditions
	 * are met:
	 *
	 *   - Redistributions of source code must retain the above copyright
	 *     notice, this list of conditions and the following disclaimer.
	 *
	 *   - Redistributions in binary form must reproduce the above copyright
	 *     notice, this list of conditions and the following disclaimer in the
	 *     documentation and/or other materials provided with the distribution.
	 *
	 *   - Neither the name of Oracle or the names of its
	 *     contributors may be used to endorse or promote products derived
	 *     from this software without specific prior written permission.
	 *
	 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
	 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
	 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
	 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
	 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
	 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
	 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
	 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
	 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
	 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
	 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
	 */
	public Dimension getPreferredScrollableViewportSize() {
	     return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,  int orientation, int direction) {
	    //Get the current position.
	    int currentPosition = 0;
	    if (orientation == SwingConstants.HORIZONTAL) {
	        currentPosition = visibleRect.x;
	    } else {
	        currentPosition = visibleRect.y;
	    }

	    //Return the number of pixels between currentPosition
	    //and the nearest tick mark in the indicated direction.
	    if (direction < 0) {
	        int newPosition = currentPosition - (currentPosition / _maxScrollableUnitIncrement) * _maxScrollableUnitIncrement;
	        return (newPosition == 0) ? _maxScrollableUnitIncrement : newPosition;
	    } else {
	        return ((currentPosition / _maxScrollableUnitIncrement) + 1)  * _maxScrollableUnitIncrement  - currentPosition;
	    }
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,  int orientation, int direction) {
	    if (orientation == SwingConstants.HORIZONTAL) {
	        return visibleRect.width - _maxScrollableUnitIncrement;
	    } else {
	        return visibleRect.height - _maxScrollableUnitIncrement;
	    }
	}

	public boolean getScrollableTracksViewportWidth() {
	    return false;
	}

	public boolean getScrollableTracksViewportHeight() {
	    return false;
	}

	public void setMaxUnitIncrement(int pixels) {
	    _maxScrollableUnitIncrement = pixels;
	}
	
	
}

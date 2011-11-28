/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.ui.image.overlay;

import java.awt.Point;
import java.awt.Rectangle;
import java.beans.Beans;
import java.net.URL;

import org.jdesktop.application.Application;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import au.org.ala.delta.model.ResourceSettings;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.impl.DefaultImageData;
import au.org.ala.delta.ui.image.ImageViewer;

public class FixedCentreOverlayLocationTest extends TestCase {

	class ImageViewerStub extends ImageViewer {

		private static final long serialVersionUID = -2394301367905565140L;
		private int _w;
		private int _h;
		private int _pw;
		private int _ph;
		private Point _origin;
		
		public ImageViewerStub(int w, int h, int pw, int ph, Point origin, ImageSettings imageSettings) {
			super(new Image(new DefaultImageData("test")), imageSettings);
			_w = w;
			_h = h;
			_pw = pw;
			_ph = ph;
			_origin = origin;
		}
		@Override
		public void displayImage(URL imageFileLocation) { }
		@Override
		public int getPreferredImageWidth() {
			return _pw;
		}

        @Override
		public int getPreferredImageHeight() {
			return _ph;
		}

		@Override
		public Point getImageOrigin() {
			return _origin;
		}

		@Override
		public int getImageWidth() {
			return _w;
		}

		@Override
		public int getImageHeight() {
			return _h;
		}
		
	      @Override
	        protected URL findImageFile(String fileName, ImageSettings imageSettings) {
	            return null;
	        }
	}
	
	public static class ApplicationStub extends Application {

		@Override
		protected void startup() {}
	}
	
	private ImageSettings _imageSettings;
	
	@Before
	public void setUp() {
		Beans.setDesignTime(true);
		Application.launch(ApplicationStub.class, null);
		_imageSettings = new ImageSettings("images");
	}
	
	@Test
	public void testConversionNoScaling() {
		
		ImageViewerStub stub = new ImageViewerStub(1000, 1000, 1000, 1000, new Point(0,0), _imageSettings);
		
		au.org.ala.delta.model.image.OverlayLocation location = newLocation(100, 50, 100, 60);
		FixedCentreOverlayLocation imageLocation = new FixedCentreOverlayLocation(stub, null, location);
	
		assertEquals(100, imageLocation.getX());
		assertEquals(50, imageLocation.getY());
		assertEquals(100, imageLocation.getWidth());
		assertEquals(65, imageLocation.getHeight());
		
	}
	
	@Test
	public void testBoundsToLocationNoScaling() {
		ImageViewerStub stub = new ImageViewerStub(2000, 1000, 2000, 1000, new Point(0,0), _imageSettings);
		
		au.org.ala.delta.model.image.OverlayLocation location = newLocation(100, 50, 80, 60);
		FixedCentreOverlayLocation imageLocation = new FixedCentreOverlayLocation(stub, null, location);
	
		Rectangle bounds = new Rectangle(200, 100, 80, 60); 
		imageLocation.updateLocationFromBounds(bounds);
		
		assertEquals(200, imageLocation.getX());
		assertEquals(100, imageLocation.getY());
		assertEquals(80, imageLocation.getWidth());
		assertEquals(65, imageLocation.getHeight());
	}
	
	private au.org.ala.delta.model.image.OverlayLocation newLocation(int x, int y, int w, int h) {
		au.org.ala.delta.model.image.OverlayLocation location = new au.org.ala.delta.model.image.OverlayLocation();
		location.setX(x);
		location.setY(y);
		location.setW(w);
		location.setH(h);
		return location;
	}
}

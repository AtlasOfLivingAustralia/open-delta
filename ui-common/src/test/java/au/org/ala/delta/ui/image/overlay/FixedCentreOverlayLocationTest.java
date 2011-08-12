package au.org.ala.delta.ui.image.overlay;

import java.awt.Point;
import java.awt.Rectangle;
import java.net.URL;

import org.jdesktop.application.Application;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
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
		
		public ImageViewerStub(int w, int h, int pw, int ph, Point origin) {
			super(new Image(new DefaultImageData("test")), new ImageSettings());
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
	}
	
	public static class ApplicationStub extends Application {

		@Override
		protected void startup() {}
	}
	
	@Before
	public void setUp() {
		Application.launch(ApplicationStub.class, null);
	}
	
	@Test
	public void testConversionNoScaling() {
		
		ImageViewerStub stub = new ImageViewerStub(1000, 1000, 1000, 1000, new Point(0,0));
		
		au.org.ala.delta.model.image.OverlayLocation location = newLocation(100, 50, 100, 60);
		FixedCentreOverlayLocation imageLocation = new FixedCentreOverlayLocation(stub, null, location);
	
		assertEquals(100, imageLocation.getX());
		assertEquals(50, imageLocation.getY());
		assertEquals(100, imageLocation.getWidth());
		assertEquals(60, imageLocation.getHeight());
		
	}
	
	@Test
	public void testBoundsToLocationNoScaling() {
		ImageViewerStub stub = new ImageViewerStub(2000, 1000, 2000, 1000, new Point(0,0));
		
		au.org.ala.delta.model.image.OverlayLocation location = newLocation(100, 50, 80, 60);
		FixedCentreOverlayLocation imageLocation = new FixedCentreOverlayLocation(stub, null, location);
	
		Rectangle bounds = new Rectangle(200, 100, 80, 60); 
		imageLocation.updateLocationFromBounds(bounds);
		
		assertEquals(200, imageLocation.getX());
		assertEquals(100, imageLocation.getY());
		assertEquals(80, imageLocation.getWidth());
		assertEquals(60, imageLocation.getHeight());
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

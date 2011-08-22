package au.org.ala.delta.model.image;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Tests the ImageSettings class.
 */
public class ImageSettingsTest extends TestCase {

	/**
	 * Tests adding a subdirectory of the data set path to the image path.
	 */
	@Test
	public void testAddToImagePathSubDirectoryOfDataSet() {
		String dataSetPath = File.listRoots()[0].getAbsolutePath()+
			"test"+File.separatorChar+"path"+File.separatorChar;
		String newPath = dataSetPath+"moreimages";
		
		ImageSettings imageSettings = new ImageSettings(dataSetPath);
		
		imageSettings.addToResourcePath(new File(newPath));
	
		assertEquals("images;moreimages", imageSettings.getResourcePath());	
	}
	
	@Test
	public void testAddToImagePathNotSubDirectoryOfDataSet() {
		String dataSetPath = File.listRoots()[0].getAbsolutePath()+
		    "test"+File.separatorChar+"path"+File.separatorChar;
		String newPath = File.listRoots()[0].getAbsolutePath()+
			"test"+File.separatorChar+"moreimages";
		ImageSettings imageSettings = new ImageSettings(dataSetPath);
		
		imageSettings.addToResourcePath(new File(newPath));
	
		assertEquals("images;.."+File.separatorChar+"moreimages", imageSettings.getResourcePath());	
	
		newPath = File.listRoots()[0].getAbsolutePath()+"moreimages";
		imageSettings.addToResourcePath(new File(newPath));
		
		assertEquals("images;.."+File.separatorChar+"moreimages;.."+File.separatorChar+".."+File.separatorChar+"moreimages", imageSettings.getResourcePath());	
	}

}

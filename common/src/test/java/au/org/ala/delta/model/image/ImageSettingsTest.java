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

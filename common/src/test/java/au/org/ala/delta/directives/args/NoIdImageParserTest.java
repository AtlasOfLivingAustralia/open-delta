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
package au.org.ala.delta.directives.args;

import java.io.StringReader;
import java.text.ParseException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.image.ImageInfo;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageType;

/**
 * Tests the NoIdImageParser class.
 */
public class NoIdImageParserTest extends TestCase {

	
	private NoIdImageParser parserFor(String directiveArgs, int imageType) {
		DeltaContext context = new DeltaContext();
		
		StringReader reader = new StringReader(directiveArgs);
		
		return new NoIdImageParser(context, reader, imageType);
	}
	
	/**
	 * This test checks the parser can handle correctly formatted text.
	 */
	@Test
	public void testSingleImage() throws ParseException {
		
		NoIdImageParser parser = parserFor("test.jpg <@feature x=1 y=2 w=3 h=-1>", ImageType.IMAGE_CHARACTER);
		
		parser.parse();
		
		List<ImageInfo> imageInfoList = parser.getImageInfo();
		
		assertEquals(1, imageInfoList.size());
		
		ImageInfo imageInfo = imageInfoList.get(0);
		assertEquals(0, imageInfo.getId());
		assertEquals("test.jpg", imageInfo.getFileName());
		assertEquals(1, imageInfo.getOverlays().size());
		ImageOverlay overlay = imageInfo.getOverlays().get(0);
		assertEquals(1, overlay.getX());
		assertEquals(2, overlay.getY());
		assertEquals(3, overlay.getWidth(0));
		assertEquals(-1, overlay.getLocation(0).H);
		
	}
	
	@Test
	public void testMultipleImages() throws ParseException {
		
		ImageParser parser = parserFor("test.jpg <@feature x=1 y=2 w=3 h=-1>\n"+
				"test2.jpg <@feature x=6 y=7 w=8 h=-10>", ImageType.IMAGE_CHARACTER);
		
		parser.parse();
		
		List<ImageInfo> imageInfoList = parser.getImageInfo();
		
		assertEquals(2, imageInfoList.size());
		
		ImageInfo imageInfo = imageInfoList.get(0);
		assertEquals(0, imageInfo.getId());
		assertEquals("test.jpg", imageInfo.getFileName());
		assertEquals(1, imageInfo.getOverlays().size());
		ImageOverlay overlay = imageInfo.getOverlays().get(0);
		assertEquals(1, overlay.getX());
		assertEquals(2, overlay.getY());
		assertEquals(3, overlay.getWidth(0));
		assertEquals(-1, overlay.getLocation(0).H);
		
		imageInfo = imageInfoList.get(1);
		assertEquals(0, imageInfo.getId());
		assertEquals("test2.jpg", imageInfo.getFileName());
		assertEquals(1, imageInfo.getOverlays().size());
		overlay = imageInfo.getOverlays().get(0);
		assertEquals(6, overlay.getX());
		assertEquals(7, overlay.getY());
		assertEquals(8, overlay.getWidth(0));
		assertEquals(-10, overlay.getLocation(0).H);
		
	}
}

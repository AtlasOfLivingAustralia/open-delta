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
package au.org.ala.delta.editor.slotfile;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.editor.slotfile.VOImageInfoDesc.LOGFONT;
import au.org.ala.delta.model.image.ImageSettings.OverlayFontType;
import au.org.ala.delta.util.Pair;


/**
 * Tests the VOImageInfoDesc class.
 */ 
public class VOImageInfoDescTest extends DeltaTestCase {
 
	/** Holds the instance of the class we are testing */
	private DeltaVOP _vop;
	
	@Before
	public void setUp() throws Exception {
		File f = copyURLToFile("/SAMPLE.DLT");
			
		_vop = new DeltaVOP(f.getAbsolutePath(), false);
	}

	@After
	public void tearDown() throws Exception {
		if (_vop != null) {
			_vop.close();
		}
		super.tearDown();
	}
	
	/**
	 * Since we are working with the sample data set we know which taxa have images.
	 */
	@Test
	public void testReadWriteImageInfoDesc() {
		
		VOImageInfoDesc imageInfo = _vop.getImageInfo();
		
		
		imageInfo.writeImagePath("uhoh");
		String imagePath = imageInfo.readImagePath();
		assertEquals("uhoh", imagePath);
		
		Pair<LOGFONT, String> fontInfo = imageInfo.readOverlayFont(OverlayFontType.OF_DEFAULT);
		
		LOGFONT font = fontInfo.getFirst();
		font.lfHeight = -15;
		font.lfItalic = 1;
		imageInfo.writeOverlayFont(OverlayFontType.OF_DEFAULT, "Test", font);
		
		fontInfo = imageInfo.readOverlayFont(OverlayFontType.OF_DEFAULT);
		
		assertEquals("Test", fontInfo.getSecond());
		assertEquals(-15, fontInfo.getFirst().lfHeight);
		assertEquals(1, fontInfo.getFirst().lfItalic);
		
	}

}

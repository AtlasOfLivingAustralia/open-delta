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
package au.org.ala.delta.model;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.model.impl.ControllingInfo;

public class CharacterDependencyTest extends TestCase {

	private MutableDeltaDataSet _dataSet;
	
	@Before
	public void setUp() throws Exception {
		URL blah = getClass().getResource("/dataset/sample/toint");
		File file = new File(blah.toURI());
		
		_dataSet = DefaultDataSetFactory.load(file);
	}
	
	@Test
	public void testCharacterDependencies() {
		Character character = _dataSet.getCharacter(48);
		Item item = _dataSet.getItem(1);
		ControllingInfo info = _dataSet.checkApplicability(character, item);
		
		assertTrue(info.isInapplicable());
	}
	
}

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
package au.org.ala.delta.util;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Tests the ArrayUtils class.
 */
public class ArrayUtilsTest {

	/**
	 * Tests the deleteRange method.
	 */
	@Test public void testDeleteRange() throws Exception {

		String source = "Test Delete Range";
		
		byte[] result = ArrayUtils.deleteRange(source.getBytes("UTF-8"), 5, 12);
		
		String resultStr = new String(result, "UTF-8");
		
		assertEquals("Test Range", resultStr);
	}
	
	/**
	 * Tests the deleteRange method.
	 */
	@Test public void testInsert() throws Exception {

		byte[] source = "Test Range".getBytes("UTF-8");
		byte[] toInsert = "Insert ".getBytes("UTF-8");
		
		byte[] result = ArrayUtils.insert(source, 5, toInsert);
		
		String resultStr = new String(result, "UTF-8");
		
		assertEquals("Test Insert Range", resultStr);
	}

}

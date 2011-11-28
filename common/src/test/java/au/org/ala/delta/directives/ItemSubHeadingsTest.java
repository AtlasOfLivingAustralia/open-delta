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
package au.org.ala.delta.directives;

import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import junit.framework.TestCase;

/**
 * Tests the ItemSubHeadings class.
 */
public class ItemSubHeadingsTest extends TestCase {

	/**
	 * Tests processing of the directive with correct data.
	 */
	@Test
	public void testItemSubHeadingsProcessing() throws Exception {
		String data = "#87. Transverse section of lamina.\n" + "#96. Leaf epidermis.\n"
				+ "#124. Pollen ultrastructure.";

		ItemSubHeadings directive = new ItemSubHeadings();

		DeltaContext context = new DeltaContext();

		directive.parseAndProcess(context, data);

		assertEquals("Transverse section of lamina.", context.getItemSubheading(87));
		assertEquals("Leaf epidermis.", context.getItemSubheading(96));
		assertEquals("Pollen ultrastructure.", context.getItemSubheading(124));
		for (int i = 1; i <= 124; i++) {
			boolean expectedResult = (i == 87 || i == 96 || i == 124);
			assertEquals(Integer.toString(i), expectedResult, context.getItemSubheading(i) != null);
		}

	}

	/**
	 * Tests processing of the directive with correct data and a delimiter. Not
	 * actually sure how the delimiter is supposed to be used right now....
	 */
	@Test
	public void testItemSubHeadingsProcessingWithDelimiter() throws Exception {
		String data = "!\n" + "#87. !Transverse section of lamina.!\n" + "#96. !Leaf epidermis.!\n"
				+ "#124. !Pollen ultrastructure.!";

		ItemSubHeadings directive = new ItemSubHeadings();

		DeltaContext context = new DeltaContext();

		directive.parseAndProcess(context, data);

		assertEquals("Transverse section of lamina.", context.getItemSubheading(87));
		assertEquals("Leaf epidermis.", context.getItemSubheading(96));
		assertEquals("Pollen ultrastructure.", context.getItemSubheading(124));
		for (int i = 1; i <= 124; i++) {
			boolean expectedResult = (i == 87 || i == 96 || i == 124);
			assertEquals(Integer.toString(i), expectedResult, context.getItemSubheading(i) != null);
		}

	}

	/**
	 * Tests processing of the directive with correct data and an invalid
	 * delimiter.
	 */
	@Test
	public void testItemSubHeadingsProcessingWithInvalidDelimiter() throws Exception {

		String[] invalidDelimiters = new String[] { "*", "#", "<", ">" };

		for (String delimeter : invalidDelimiters) {
			String data = " " + delimeter + "\n #1. ";

			ItemSubHeadings directive = new ItemSubHeadings();

			DeltaContext context = new DeltaContext();

			try {
				directive.parseAndProcess(context, data);
				fail("Invalid delimeter should have caused an exception");
			} catch (Exception e) {
			}

		}

	}
}

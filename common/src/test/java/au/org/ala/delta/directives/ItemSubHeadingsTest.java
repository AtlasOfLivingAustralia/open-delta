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

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.impl.DefaultDataSet;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import junit.framework.TestCase;

/**
 * Tests the ItemSubHeadings class.
 */
public class ItemSubHeadingsTest extends TestCase {

    private DeltaContext _context;

    @Before
    public void setUp() {
        DefaultDataSetFactory factory = new DefaultDataSetFactory();
        DefaultDataSet dataSet = (DefaultDataSet)factory.createDataSet("test");

        // Type of character doesn't matter for this test.
        int charCount = 200;
        for (int i=0; i<charCount; i++) {
            dataSet.addCharacter(CharacterType.UnorderedMultiState);
        }

        int itemCount = 10;
        for (int i=0; i<itemCount; i++) {
            dataSet.addItem();
        }

        _context = new DeltaContext(dataSet);

    }


    /**
	 * Tests processing of the directive with correct data.
	 */
	@Test
	public void testItemSubHeadingsProcessing() throws Exception {
		String data = "#87. Transverse section of lamina.\n" + "#96. Leaf epidermis.\n"
				+ "#124. Pollen ultrastructure.";

		ItemSubHeadings directive = new ItemSubHeadings();

		directive.parseAndProcess(_context, data);

		assertEquals("Transverse section of lamina.", _context.getItemSubheading(87));
		assertEquals("Leaf epidermis.", _context.getItemSubheading(96));
		assertEquals("Pollen ultrastructure.", _context.getItemSubheading(124));
		for (int i = 1; i <= 124; i++) {
			boolean expectedResult = (i == 87 || i == 96 || i == 124);
			assertEquals(Integer.toString(i), expectedResult, _context.getItemSubheading(i) != null);
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

		directive.parseAndProcess(_context, data);

		assertEquals("Transverse section of lamina.", _context.getItemSubheading(87));
		assertEquals("Leaf epidermis.", _context.getItemSubheading(96));
		assertEquals("Pollen ultrastructure.", _context.getItemSubheading(124));
		for (int i = 1; i <= 124; i++) {
			boolean expectedResult = (i == 87 || i == 96 || i == 124);
			assertEquals(Integer.toString(i), expectedResult, _context.getItemSubheading(i) != null);
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

			try {
				directive.parseAndProcess(_context, data);
				fail("Invalid delimeter should have caused an exception");
			} catch (Exception e) {
			}

		}

	}
}

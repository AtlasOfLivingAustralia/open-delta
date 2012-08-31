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
package au.org.ala.delta.editor.directives;

import java.io.File;

import junit.framework.TestCase;

/**
 * Tests the ImportFileNameFilter class.
 */
public class ImportFileNameFilterTest extends TestCase {

	public void testFilterWithSinglePattern() {

		String exclusionPattern = "*.bak";

		check(exclusionPattern, "test.bak", false);
		check(exclusionPattern, "bak", true);
		check(exclusionPattern, ".bak", false);
		check(exclusionPattern, "test", true);
		check(exclusionPattern, "test.bak1", true);
	}

	public void testFilterWithMulitplePatterns() {

		String exclusionPattern = "*.bak;*.tmp;fred";

		check(exclusionPattern, "test.bak", false);
		check(exclusionPattern, "rem.tmp", false);
		check(exclusionPattern, "fred", false);
		check(exclusionPattern, "fred.fred", true);
		check(exclusionPattern, "fred.bak", false);
	}

	private void check(String exclusionPatterns, String input, boolean expectedResult) {
		ImportFileNameFilter filter = new ImportFileNameFilter(exclusionPatterns);

		assertEquals(expectedResult, filter.accept(new File(input)));
	}
}

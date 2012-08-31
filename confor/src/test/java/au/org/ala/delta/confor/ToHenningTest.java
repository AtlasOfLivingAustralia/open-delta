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
package au.org.ala.delta.confor;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

/**
 * Tests the TRANSLATE INTO PAUP FORAMT directive in the context of the sample
 * tonpau directives file.
 */
public class ToHenningTest extends ConforTestCase {

	@Test
	public void testSampleToHenning86() throws Exception {
		runConfor();
		
		File expectedFile = new File(FilenameUtils.concat(_samplePath, "expected_results/hendata"));
		String expected = FileUtils.readFileToString(expectedFile, "utf-8");

		System.out.println(expected);
		
		File actualFile = new File(FilenameUtils.concat(_samplePath, "hendata"));
		String actual = FileUtils.readFileToString(actualFile, "utf-8");

		System.out.print(actual);
		
		
		boolean dosEol = expected.contains("\r\n");
		String expectedLineSeparator = "\n";
		if (dosEol) {
			expectedLineSeparator = "\r\n";
		}
		
		if (!System.getProperty("line.separator").equals(expectedLineSeparator)) {
			expected = expected.replaceAll(expectedLineSeparator, System.getProperty("line.separator"));
		}
		// The heading contains the date so will be different.
		String heading = "Grass Genera 11:53 15-NOV-11"; // <Date>, eg. 11:32 05-OCT-11
		
		actual = actual.replaceAll("Grass Genera.*[0-9]{2}-[a-zA-Z]{3}-[0-9]{4}", heading);
		
		/*for (int i=0; i<expected.length(); i++) {
			if (expected.charAt(i) != actual.charAt(i)) {
				System.out.println("Difference @ char: "+i+" Expected: "+expected.charAt(i)+(int)expected.charAt(i)+", Actual: "+actual.charAt(i)+(int)actual.charAt(i));
				break;
			}
		}
		BufferedReader expectedReader = new BufferedReader(new StringReader(expected));
		BufferedReader actualReader = new BufferedReader(new StringReader(actual));
		String expectedLine = expectedReader.readLine();
		String actualLine = actualReader.readLine();
		while (expectedLine != null) {
			assertEquals(expectedLine.trim(), actualLine.trim());
			expectedLine = expectedReader.readLine();
			actualLine = actualReader.readLine();
			
		}*/
		
		assertEquals(expected.trim(), actual.trim());
	}

	@Override
	protected String directivesFileName() {
		return "tohen";
	}
	
	
}

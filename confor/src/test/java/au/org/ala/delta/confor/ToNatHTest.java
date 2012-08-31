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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

/**
 * Tests the PRINT CHARACTER LIST directive in the context of the sample
 * printc directives file.
 */
public class ToNatHTest extends ConforTestCase {

	@Test
	public void testSampleToNatH() throws Exception {
		runConfor();
		
		testFile("index.htm", "");
		
		String[] files = {
				"agrostis.htm", "andropog.htm", "anisopog.htm", "bambusa.htm",
				"chloris.htm", "cynodon.htm", "echinoch.htm", "eleusine.htm", 
				"festuca.htm", "implicit.htm", "oryza.htm", "panicum.htm",
				"phragmit.htm", "poa.htm", "zea.htm"};
		for (String file : files) {
			testFile(file, "www/");
		}
	}

	private void testFile(String fileName, String outputPath) throws IOException {
		File expectedFile = new File(FilenameUtils.concat(_samplePath, "expected_results/"+fileName));
		String expected = FileUtils.readFileToString(expectedFile, "cp1252");

		System.out.println(expected);
		
		File actualFile = new File(FilenameUtils.concat(_samplePath, outputPath+fileName));
		String actual = FileUtils.readFileToString(actualFile, "cp1252");

		System.out.print(actual);
		
		
		boolean dosEol = expected.contains("\r\n");
		String expectedLineSeparator = "\n";
		if (dosEol) {
			expectedLineSeparator = "\r\n";
		}
		
		if (!System.getProperty("line.separator").equals(expectedLineSeparator)) {
			expected = expected.replaceAll(expectedLineSeparator, System.getProperty("line.separator"));
			//actual = actual.replaceAll(expectedLineSeparator, System.getProperty("line.separator"));
		}
		// The heading contains the date so will be different.
		
		for (int i=0; i<expected.length(); i++) {
			if (expected.charAt(i) != actual.charAt(i)) {
				System.out.println("Difference @ char: "+i+" Expected: "+expected.charAt(i)+(int)expected.charAt(i)+", Actual: "+actual.charAt(i)+(int)actual.charAt(i));
				break;
			}
		}
		//assertEquals(expected, actual);
	}

	@Override
	protected String directivesFileName() {
		return "tonath";
	}
	
	
}

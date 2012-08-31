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
package au.org.ala.delta.dist;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * This is an integration test for the DIST program.
 */
public class DISTTest extends TestCase {

	private String _path;
	@Before
	public void setUp() throws Exception {
		File dataSetDirectory = urlToFile("/dataset");
		File dest = new File(System.getProperty("java.io.tmpdir"));
		FileUtils.copyDirectory(dataSetDirectory, dest);
		_path = dest.getAbsolutePath();
	}
	
	@Test
	public void testDISTWithSample() throws Exception {
		String path = FilenameUtils.concat(_path, "sample");
		
		runDIST(FilenameUtils.concat(path, "dist"));
		checkResults(path, "grass.nam", false);
		checkResults(path, "grass.dis", true);
	}
	
	/**
	 * Still uses the sample data as input, but tests the PHYLIP FORMAT,
	 * MATCH OVERLAP and EXCLUDE ITEMS directives.
	 */
	@Test
	public void testDISTPhylipFormatMatchOverlap() throws Exception {
		String path = FilenameUtils.concat(_path, "sample");
		
		runDIST(FilenameUtils.concat(path, "dist2"));
		checkResults(path, "grass2.dis", false);
	}
	
	private void checkResults(String path, String resultFileName, boolean compareAsFloats) throws Exception {
		
		java.io.File expectedFile = new File(FilenameUtils.concat(path, "expected_results/"+resultFileName));
		String expected = FileUtils.readFileToString(expectedFile, "cp1252");
	
		System.out.println(expected);
		
		File actualFile = new File(FilenameUtils.concat(path, resultFileName));
		String actual = FileUtils.readFileToString(actualFile, "cp1252");
	
		System.out.print(actual);
		expected = replaceNewLines(expected);
		
		if (compareAsFloats) {
			String[] actualFloats = actual.trim().split("\\s+");
			String[] expectedFloats = expected.trim().split("\\s+");
			for (int i=0; i<expectedFloats.length; i++) {
				float float1 = Float.valueOf(actualFloats[i]);
				float float2 = Float.valueOf(expectedFloats[i]);
				assertEquals("index "+i, float2, float1, 0.001f);
			}
		}
		
		assertEquals(expected, actual);
	}

	protected String replaceNewLines(String expected) {
		boolean dosEol = expected.contains("\r\n");
		String expectedLineSeparator = "\n";
		if (dosEol) {
			expectedLineSeparator = "\r\n";
		}
		
		if (!System.getProperty("line.separator").equals(expectedLineSeparator)) {
			expected = expected.replaceAll(expectedLineSeparator, System.getProperty("line.separator"));
		}
		return expected;
	}
	
	private void runDIST(String... args) throws Exception {
		DIST.main(args);
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = DISTTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}

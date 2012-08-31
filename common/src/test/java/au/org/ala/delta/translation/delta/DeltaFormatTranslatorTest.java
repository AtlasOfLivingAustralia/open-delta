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
package au.org.ala.delta.translation.delta;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.DataSetTranslatorFactory;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.TranslatorTest;
import junit.framework.TestResult;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Tests the DeltaFormatTranslator.
 */
public class DeltaFormatTranslatorTest extends TranslatorTest {

	protected static final String DEFAULT_DATASET_PATH = "/dataset/sample/fillin";
	
	protected DataSetTranslator _dataSetTranslator;
	protected PrintFile _printer;
	protected DataSetTranslatorFactory _factory;
	protected PrintFile _outputFile;
	
	@Before
	public void setUp() throws Exception {
		
		_bytes = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(_bytes, false, "UTF-8");
		
		_context = new DeltaContext();
		_outputFile = new PrintFile(pout, 80);
		_context.getOutputFileSelector().setOutputFile(_outputFile);
		_factory = new DataSetTranslatorFactory();
		
	}

    @After
    public void tearDown() {

        _outputFile.close();
        IOUtils.closeQuietly(_bytes);
    }

    @Test
	public void testItemsTranslation() throws Exception {
		_context.setOmitTypeSettingMarks(false);
		_context.setInsertImplicitValues(true);
		initialiseContext(DEFAULT_DATASET_PATH);
		
		checkResult("/dataset/sample/expected_results/deltaformatitems.txt");

	}

    @Test
	public void testItemsTranslationOmitTypeSettingMarks() throws Exception {
		_context.setInsertImplicitValues(true);
		_context.setOmitTypeSettingMarks(true);
		initialiseContext(DEFAULT_DATASET_PATH);

		checkResult("/dataset/sample/expected_results/deltaformatitem-omittypesettingmarks.txt");

	}

    @Test
	public void testItemsTranslationNoImplicitValues() throws Exception {
		_context.setInsertImplicitValues(false);

		initialiseContext(DEFAULT_DATASET_PATH);

		checkResult("/dataset/sample/expected_results/deltaformatitem-noimplicitvalues.txt");

	}

	/**
	 * Checks the result of the translation is identical to the contents of the supplied file.
	 *
	 * @param expectedResultsFileName the name of the file - the path "/dataset/simple/expected_results/" is 
	 * prepended before loading the file contents.
	 * @throws Exception if there is an error reading the file.
	 */
	protected void checkResult(String expectedResultsFileName) throws Exception {
		
		String expectedResults = classLoaderPathToString(expectedResultsFileName);
		
		boolean dosEol = expectedResults.contains("\r\n");
		String expectedLineSeparator = "\n";
		if (dosEol) {
			expectedLineSeparator = "\r\n";
		}
		String lineSeparator = System.getProperty("line.separator");
		if (!lineSeparator.equals(expectedLineSeparator)) {
			expectedResults = expectedResults.replaceAll(expectedLineSeparator, lineSeparator);
		}
		
		expectedResults = expectedResults.trim();
		String actualResults = actualResults().trim();
		
		// Replace windows code page chars
		
		actualResults = actualResults.replaceAll("\\\\emdash\\{\\}", "\u2014");
		actualResults = actualResults.replaceAll("\\\\endash\\{\\}", "\u2013");
		
		
		// This is here because I keep getting bitten by end of line issues and the test failure
		// comparison editor doesn't display them.
		for (int i=0; i<expectedResults.length() && i<actualResults.length(); i++) {
			if (expectedResults.charAt(i) != actualResults.charAt(i)) {
				System.out.println("First wrong character @ position "+i);
				System.out.println("Expected: "+Integer.toHexString((int)expectedResults.charAt(i))+", found: "+Integer.toHexString((int)actualResults.charAt(i)));
				break;
			}
		}
				
		assertEquals(expectedResults, actualResults);
		
		
	}
    public TestResult run() {
        System.out.println("Running....");
        return super.run();
    }
}

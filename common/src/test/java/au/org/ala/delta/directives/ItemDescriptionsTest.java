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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.MutableDeltaDataSet;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.StringReader;

public class ItemDescriptionsTest extends TestCase {

	private MutableDeltaDataSet _dataSet;
	private DeltaContext _context;

	@Before
	public void setUp() {
		DefaultDataSetFactory factory = new DefaultDataSetFactory();
		_dataSet = factory.createDataSet("test");
		_context = new DeltaContext(_dataSet);
		_context.setMaximumNumberOfItems(1);
	}
	
	private void runConforExpectingError(String scriptName, int expectedError) throws Exception {
		try {
            runCONFOR(scriptName);
			fail(String.format("Exception (error %d) expected", expectedError));
		} catch (DirectiveException ex) {
			assertEquals(expectedError, ex.getErrorNumber());
		}
		
	}

    private void runCONFOR(String scriptName) throws Exception {
        String directives = getConforResoure(scriptName);
        ConforDirectiveFileParser parser = ConforDirectiveFileParser.createInstance();
        ConforDirectiveParserObserver observer = new ConforDirectiveParserObserver(_context);
        parser.registerObserver(observer);
        parser.parse(new StringReader(directives), _context);
    }

    private String getConforResoure(String name) throws Exception {
		InputStream is = ItemDescriptionsTest.class.getResourceAsStream(String.format("/confor/%s", name));
		return StringUtils.join(IOUtils.readLines(is), "\n");
	}

	@Test
	public void testParsing() throws Exception {		
		runConforExpectingError("prereqtest", 36);
	}
	
	@Test
	public void test1() throws Exception {
		runConforExpectingError("test1", 12);
	}
	
	@Test
	public void test2() throws Exception {
		runConforExpectingError("test2", 46);
	}


    /**
     * Ensures the Item parser handles the ACCEPT DUPLICATE VALUES directive correctly.
     */
    @Test
    public void testDuplicateValues() throws Exception {

        runCONFOR("acceptDuplicateValues");

        // The context should have raised a warning by the ConforDirectiveParserObserver will have removed it
        // by this point.  Instead we just check the value has been overwritten.
        assertEquals("1", _context.getDataSet().getAttribute(3, 2).getValueAsString());
    }

}

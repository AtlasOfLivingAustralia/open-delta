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

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;

/**
 * Tests the TextFileDataSetRepository class.
 */
public class TextFileDataSetRepositoryTest extends TestCase {

    private TextFileDataSetRepository _repository = new TextFileDataSetRepository();

    /**
     * Tests that a Data Set can be created by specifying the directory containing the specs/chars and items files.
     * @throws Exception if there is an error during the test.
     */
    @Test
    public void testLoadFromDirectory() throws Exception {

        File dataSetDirectory = new File(getClass().getResource("/dataset/sample").toURI());

        MutableDeltaDataSet dataSet = _repository.findByName(dataSetDirectory.getAbsolutePath(), null);

        assertEquals(89, dataSet.getNumberOfCharacters());
        assertEquals(14, dataSet.getMaximumNumberOfItems());


    }

    /**
     * Tests that a Data Set can be created by specifying a directives file from the dataset that contains imports
     * of the specs/items/chars files.
     * @throws Exception if there is an error during the test.
     */
    @Test
    public void testLoadFromDirectivesFile() throws Exception {
        File toint = new File(getClass().getResource("/dataset/sample/toint").toURI());

        MutableDeltaDataSet dataSet = _repository.findByName(toint.getAbsolutePath(), null);

        assertEquals(89, dataSet.getNumberOfCharacters());
        assertEquals(14, dataSet.getMaximumNumberOfItems());
    }

}

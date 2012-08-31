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
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.MutableDeltaDataSet;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the DecimalPlaces directive class.
 */
public class DecimalPlacesTest extends TestCase {

    private DecimalPlaces _decimalPlaces = new DecimalPlaces();

    private MutableDeltaDataSet _dataSet;
    private DeltaContext _context;

    @Before
    public void setUp() throws Exception {
        DefaultDataSetFactory factory = new DefaultDataSetFactory();
        _dataSet = factory.createDataSet("test");
        _context = new DeltaContext(_dataSet);
        _context.setNumberOfCharacters(3);
        _context.setMaximumNumberOfItems(3);

        _dataSet.addCharacter(CharacterType.RealNumeric);
        _dataSet.addCharacter(CharacterType.IntegerNumeric);
        _dataSet.addCharacter(CharacterType.RealNumeric);

        _dataSet.addItem();
        _dataSet.addItem();

    }

    /**
     * Tests the simple case where the directive is correctly formatted.
     * @throws Exception if there is an error during the test.
     */
    @Test
    public void testParsingCorrectlyFormattedData() throws Exception {
        String data = "1,2 3,1";
        _decimalPlaces.parseAndProcess(_context, data);

        assertEquals(new Integer(2), _context.getDecimalPlaces(1));
        assertNull(_context.getDecimalPlaces(2));
        assertEquals(new Integer(1), _context.getDecimalPlaces(3));
    }



}

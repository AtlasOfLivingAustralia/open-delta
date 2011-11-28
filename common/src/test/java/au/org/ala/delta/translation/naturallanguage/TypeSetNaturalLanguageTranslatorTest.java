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
package au.org.ala.delta.translation.naturallanguage;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.AbstractDataSetTranslator;
import au.org.ala.delta.translation.DataSetFilter;
import au.org.ala.delta.translation.FormattedTextTypeSetter;
import au.org.ala.delta.translation.FormatterFactory;
import au.org.ala.delta.translation.IterativeTranslator;
import au.org.ala.delta.translation.TypeSettingAttributeFormatter;
import au.org.ala.delta.translation.TypeSettingItemFormatter;

/**
 * Tests the production of type set natural language. This test is more of an
 * integration test than a unit test - it relies on the collaboration of several
 * classes to produce the natural language output.
 */
public class TypeSetNaturalLanguageTranslatorTest extends NaturalLangaugeTranslatorTest {

    @Before
    public void setUp() throws Exception {

        _bytes = new ByteArrayOutputStream();
        _context = new DeltaContext();
       
    }

    @Override
    protected void initialiseContext(String path) throws Exception {
        super.initialiseContext(path);
        PrintStream pout = new PrintStream(_bytes, false, "UTF-8");
        
        _context.setPrintStream(pout);
        _context.getOutputFileSelector().setPrintWidth(0);
        _printer = _context.getOutputFileSelector().getPrintFile();

    }

    protected Map<Integer, TypeSettingMark> createMarks() {
        HashMap<Integer, TypeSettingMark> marks = new HashMap<Integer, TypeSettingMark>();

        for (MarkPosition position : MarkPosition.values()) {
        	_context.addTypeSettingMark(new TypeSettingMark(position.getId(), "mark " + position.getId(), false));
        }
       
        return marks;
    }

    /**
     * Tests the type setting mark insertion used in a simple data set.
     */
    @Test
    public void testSimpleDataSetWithTypesetting() throws Exception {
        initialiseContext(DEFAULT_DATASET_PATH);
        createMarks();
        _typeSetter = new FormattedTextTypeSetter(_context, _printer);
        ItemFormatter itemFormatter = new TypeSettingItemFormatter(_typeSetter);
        CharacterFormatter characterFormatter = new CharacterFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, false, false);
        AttributeFormatter attributeFormatter = new TypeSettingAttributeFormatter();
        DataSetFilter filter = new NaturalLanguageDataSetFilter(_context);
		IterativeTranslator translator = new NaturalLanguageTranslator(_context, _typeSetter, _printer, itemFormatter, characterFormatter, attributeFormatter);
        _dataSetTranslator = new AbstractDataSetTranslator(_context, filter, translator);
			
        _dataSetTranslator.translateItems();
        checkResult("typeset.txt");
    }

    /**
     * Tests the type setting mark insertion using the sample data set.
     */
    @Test
    public void testSampleDataSetWithTypesetting() throws Exception {
        initialiseContext("/dataset/sample/tonatr_simple");
        
        _typeSetter = new FormattedTextTypeSetter(_context, _printer);
        ItemFormatter itemFormatter = new TypeSettingItemFormatter(_typeSetter);
        CharacterFormatter characterFormatter = new CharacterFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, false, false);
        AttributeFormatter attributeFormatter = new FormatterFactory(_context).createAttributeFormatter();
        
        DataSetFilter filter = new NaturalLanguageDataSetFilter(_context);
        IterativeTranslator translator = new NaturalLanguageTranslator(_context, _typeSetter, _printer, itemFormatter, characterFormatter, attributeFormatter);
        _dataSetTranslator = new AbstractDataSetTranslator(_context, filter, translator);
		
        _dataSetTranslator.translateItems();
        checkResult("/dataset/sample/expected_results/withtypesetting.txt");
    }

}

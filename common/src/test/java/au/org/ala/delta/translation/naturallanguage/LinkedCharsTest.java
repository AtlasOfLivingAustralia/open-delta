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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.DataSetFilter;
import au.org.ala.delta.translation.DelegatingDataSetTranslator;
import au.org.ala.delta.translation.IterativeTranslator;
import au.org.ala.delta.translation.PlainTextTypeSetter;
import au.org.ala.delta.translation.PrintFile;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Tests the production of plain text natural language, specifically in regards to linked characters and
 * inapplicability.
 */
public class LinkedCharsTest extends NaturalLangaugeTranslatorTest {

	@Before
	public void setUp() throws Exception {
		
		_bytes = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(_bytes, false, "UTF-8");
		_printer = new PrintFile(pout, 80);
		_typeSetter = new PlainTextTypeSetter(_printer);
		_context = new DeltaContext();
		ItemFormatter itemFormatter = new ItemFormatter(false, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, false, false);
		CharacterFormatter characterFormatter = new CharacterFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, true, false);
		AttributeFormatter attributeFormatter = new AttributeFormatter(false, true, CommentStrippingMode.RETAIN);
		DataSetFilter filter = new NaturalLanguageDataSetFilter(_context);
		IterativeTranslator translator = new NaturalLanguageTranslator(_context, _typeSetter, _printer, itemFormatter, characterFormatter, attributeFormatter);
		_dataSetTranslator = new DelegatingDataSetTranslator(_context, filter, translator);
		
	}

    @Test
	public void testBasicTranslation() throws Exception {
		initialiseContext("/dataset/inapplicable/tonat");
		_dataSetTranslator.translateItems();
		checkResult("/dataset/inapplicable/expected_results/test.prt");
	}
	

}

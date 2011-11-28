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
import java.util.HashSet;
import java.util.Set;


import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.translation.AbstractDataSetTranslator;
import au.org.ala.delta.translation.DataSetFilter;
import au.org.ala.delta.translation.IterativeTranslator;
import au.org.ala.delta.translation.PlainTextTypeSetter;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.naturallanguage.NaturalLanguageTranslator;

/**
 * Tests the production of plain text natural language.  This test is more of an integration
 * test than a unit test - it relies on the collaboration of several classes to produce the
 * natural language output.
 */
public class PlainTextNaturalLanguageTranslatorTest extends NaturalLangaugeTranslatorTest {

	@Before
	public void setUp() throws Exception {
		
		_bytes = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(_bytes, false, "UTF-8");
		_printer = new PrintFile(pout, 78);
		_typeSetter = new PlainTextTypeSetter(_printer);
		_context = new DeltaContext();
		ItemFormatter itemFormatter = new ItemFormatter(false, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, false, false);
		CharacterFormatter characterFormatter = new CharacterFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, true, false);
		AttributeFormatter attributeFormatter = new AttributeFormatter(false, true, CommentStrippingMode.RETAIN);
		DataSetFilter filter = new NaturalLanguageDataSetFilter(_context);
		IterativeTranslator translator = new NaturalLanguageTranslator(_context, _typeSetter, _printer, itemFormatter, characterFormatter, attributeFormatter);
		_dataSetTranslator = new AbstractDataSetTranslator(_context, filter, translator);
		
	}
	
	public void testBasicTranslation() throws Exception {
		initialiseContext(DEFAULT_DATASET_PATH);
		_dataSetTranslator.translateItems();
		checkResult("default.txt");
	}
	
	@Test
	/**
	 * Tests the OMIT REDUNDANT VARIANT ATTRIBUTES directive works correctly.
	 */
	public void testOmitRedundantVariantAttributes() throws Exception {
		initialiseContext(DEFAULT_DATASET_PATH);
		_context.setOmitRedundantVariantAttributes(true);
		
		_dataSetTranslator.translateItems();
		checkResult("redundant_variant_attr_omitted.txt");
	}
	
	@Test
	/**
	 * Tests the INSERT REDUNDANT VARIANT ATTRIBUTES directive works correctly.
	 */
	public void testInsertRedundantVariantAttributes() throws Exception {
		initialiseContext(DEFAULT_DATASET_PATH);
		_context.setOmitRedundantVariantAttributes(false);
		
		_dataSetTranslator.translateItems();
		checkResult("redundant_variant_attr_included.txt");
	}
	
	@Test
	/**
	 * Tests the natural language output handles the LINK CHARACTERS directive correctly.
	 */
	public void testLinkedCharacters() throws Exception {
		initialiseContext(DEFAULT_DATASET_PATH);
		Set<Integer> linkedCharacters = new HashSet<Integer>();
		linkedCharacters.add(4);
		linkedCharacters.add(5);
		
		_context.linkCharacters(linkedCharacters);
		
		_dataSetTranslator.translateItems();
		checkResult("linked_characters.txt");
	}
	
	public void testSimpleSampleTranslation() throws Exception{
		initialiseContext(SAMPLE_DATASET_PATH);
		
		_dataSetTranslator.translateItems();
		checkResult("/dataset/sample/expected_results/default.txt");
	}
	
	public void testSampleTranslationWithImplictValues() throws Exception{
		initialiseContext(SAMPLE_DATASET_PATH);
		_context.setInsertImplicitValues(true);
		_dataSetTranslator.translateItems();
		checkResult("/dataset/sample/expected_results/withimplicitvalues.txt");
	}
	
	public void testSimplePoneriniTranslation() throws Exception {
		initialiseContext(PONERINI_DATASET_PATH);	
		
		_dataSetTranslator.translateItems();
		checkResult("/dataset/ponerini/expected_results/default.txt");
	}
	
	/**
	 * This test method actually takes quite a while so might not be suitable for the normal
	 * build as it slows down the test cycle a fair bit.
	 */
	public void zztestSimpleVideTranslation() throws Exception {
		initialiseContext(VIDE_DATASET_PATH);	
		
		_dataSetTranslator.translateItems();
		checkResult("/dataset/vide/expected_results/default.txt", true);
	}
	
	/**
	 * This test method actually takes quite a while so might not be suitable for the normal
	 * build as it slows down the test cycle a fair bit.
	 */
	public void zztestSimpleCFloraTranslation() throws Exception {
		initialiseContext(CFLORA_DATASET_PATH);	
		
		_dataSetTranslator.translateItems();
		checkResult("/dataset/cflora/expected_results/default.txt");
	}
	
}

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
package au.org.ala.delta.translation.attribute;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.translation.PlainTextTypeSetter;

/**
 * Tests the NumericAttributeTranslator class.
 */
public class NumericAttributeTranslatorTest extends TestCase {
	private RealCharacter _realCharacter;
	private DefaultDataSetFactory _factory;
	private NumericAttributeTranslator _translator;
	private PlainTextTypeSetter _typeSetter;
	private AttributeFormatter _attributeFormatter;
	
	@Before
	public void setUp() throws Exception {
		_factory = new DefaultDataSetFactory();
		_realCharacter = (RealCharacter)_factory.createCharacter(CharacterType.RealNumeric, 2);
		_realCharacter.setUnits("units");
		_typeSetter = new PlainTextTypeSetter(null);
		_attributeFormatter = new AttributeFormatter(false, true, CommentStrippingMode.RETAIN);
		
		_translator = new NumericAttributeTranslator(_realCharacter, _typeSetter, _attributeFormatter, false, false, false);
	}
	
	/**
	 * Tests a Real Numeric character is formatted correctly.
	 */
	@Test
	public void testRealAttributeNoComments() {
		String attributeValue = "1.3";
		String value = format(_realCharacter, attributeValue);
		
		assertEquals("1.3 units", value);
	}
	
	/**
	 * Tests a Real Numeric character with comments is formatted correctly.
	 */
	@Test
	public void testRealAttributeWithComments() {
		String attributeValue = "1.3<test>";
		String value = format(_realCharacter, attributeValue);
		
		assertEquals("1.3 units <test>", value);
	}
	
	@Test
	public void testMoreComplexAttributes() {

		String[] inputs = {
				"<comment 1>1.3<comment 2>", "<comment 1>1.3-2<comment 2>",
				"<comment 1>1&2<comment 2>", "<comment 1>1<comment 2>/2<comment 3>"};
		
		String[] expected = {
				"<comment 1> 1.3 units <comment 2>", "<comment 1> 1.3-2 units <comment 2>",
				"<comment 1> 1 and 2 units <comment 2>", "<comment 1> 1 units <comment 2>, or 2 units <comment 3>"};
		
		for (int i=0; i<inputs.length; i++) {
			String formattedValue = format(_realCharacter, inputs[i]);
			assertEquals(expected[i], formattedValue);
		}
	
	}
	
	@Test
	public void zztestOmitUpperForCharacter() {
		_translator = new NumericAttributeTranslator(_realCharacter, _typeSetter, _attributeFormatter, false, false, true);
	
		String[] inputs = {
				"<comment 1>1.3(-2)<comment 2>", "<comment 1>1.3-2<comment 2>",
				"<comment 1>1&2<comment 2>", "<comment 1>1<comment 2>/2<comment 3>"};
		
		String[] expected = {
				"<comment 1> 1.3 units <comment 2>", "<comment 1> 2 units <comment 2>",
				"<comment 1> 1 and 2 units <comment 2>", "<comment 1> 1 units <comment 2>, or 2 units <comment 3>"};
		
		for (int i=0; i<inputs.length; i++) {
			String formattedValue = format(_realCharacter, inputs[i]);
			assertEquals(expected[i], formattedValue);
		}
	}
	
	private String format(au.org.ala.delta.model.Character character, String value) {
		AttributeParser parser = new AttributeParser();
		
		return _translator.translate(parser.parse(value));
	}
}

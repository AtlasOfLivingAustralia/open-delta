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
package au.org.ala.delta.key;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DataSetBuilder;
import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.impl.DefaultDataSet;
import au.org.ala.delta.translation.attribute.AttributeTranslatorFactory;
import au.org.ala.delta.translation.key.KeyStateTranslator;

public class KeyStateTranslatorTest extends TestCase {

	private KeyStateTranslator _translator;
	private DeltaContext _context;
	private MutableDeltaDataSet _dataSet;
	
	@Before
	public void setUp() throws Exception {
		DefaultDataSetFactory factory = new DefaultDataSetFactory();
		_dataSet = (DefaultDataSet)factory.createDataSet("test");
		_context = new DeltaContext(_dataSet);
		_context.setNumberOfCharacters(4);
		DataSetBuilder.buildSimpleDataSet(_dataSet);
		MultiStateCharacter char5 = (MultiStateCharacter)_dataSet.addCharacter(CharacterType.OrderedMultiState);
		char5.setDescription("character 5 description");
		char5.setNumberOfStates(5);
		char5.setState(1, "state 1");
		char5.setState(2, "state 2");
		char5.setState(3, "state 3");
		char5.setState(4, "state 4");
		char5.setState(5, "state 5");
		
		DeltaContext context = new DeltaContext();
		AttributeTranslatorFactory attrFactory = new AttributeTranslatorFactory(context);
		_translator = new KeyStateTranslator(attrFactory);
	}
	
	@Test
	public void testTranslateUnorderedMultistateCharacter() {
		IdentificationKeyCharacter keyChar = new IdentificationKeyCharacter(_dataSet.getCharacter(1));
		List<Integer> originalStates = new ArrayList<Integer>();
		originalStates.add(2);
		originalStates.add(3);
		
		keyChar.addState(1, originalStates);
		
		originalStates = new ArrayList<Integer>();
		originalStates.add(1);
		keyChar.addState(2, originalStates);
		
		String result = _translator.translateState(keyChar, 1);
		//assertEquals("This is state 2 or 3", result);
		
		result = _translator.translateState(keyChar, 2);
		assertEquals("state 1", result);
		
	}

	@Test
	public void testTranslateOrderedMultistateCharacter() {
		IdentificationKeyCharacter keyChar = new IdentificationKeyCharacter(_dataSet.getCharacter(5));
		List<Integer> originalStates = new ArrayList<Integer>();
		originalStates.add(1);
		originalStates.add(2);
		originalStates.add(3);
		
		keyChar.addState(1, originalStates);
		originalStates = new ArrayList<Integer>();
		originalStates.add(5);
		
		keyChar.addState(2, originalStates);
		
		String result = _translator.translateState(keyChar, 1);
		assertEquals("state 1 to state 3", result);
		
		result = _translator.translateState(keyChar, 2);
		assertEquals("state 5", result);
	}

	@Test
	public void testTranslateNumericCharacter() {
		// Character 3 is a numeric character with units of mm.
		IdentificationKeyCharacter keyChar = new IdentificationKeyCharacter(_dataSet.getCharacter(3));
		
		BigDecimal min = new BigDecimal(-Float.MAX_VALUE);
		BigDecimal max = new BigDecimal("2");
		
		keyChar.addState(1, min, max);
		keyChar.addState(2, new BigDecimal("2"), new BigDecimal("4"));
		keyChar.addState(3, new BigDecimal("4"), new BigDecimal("10.1"));
		keyChar.addState(4, new BigDecimal("10.1"), new BigDecimal(Float.MAX_VALUE));
		
		
		String result = _translator.translateState(keyChar, 1);
		assertEquals("up to 2 mm", result);
		
		result = _translator.translateState(keyChar, 2);
		assertEquals("2 to 4 mm", result);
		
		result = _translator.translateState(keyChar, 3);
		assertEquals("4 to 10.1 mm", result);
		
		result = _translator.translateState(keyChar, 4);
		assertEquals("10.1 mm or more", result);
	}
	
	@Test
	public void testTranslateNumericCharacterSingleValues() {
		// Character 3 is an integer character with units of mm.
		IdentificationKeyCharacter keyChar = new IdentificationKeyCharacter(_dataSet.getCharacter(3));
		keyChar.addState(2, new BigDecimal("0"), new BigDecimal("0"));
		keyChar.addState(3, new BigDecimal("4"), new BigDecimal("4"));
		keyChar.addState(4, new BigDecimal("5"), new BigDecimal(Float.MAX_VALUE));
		
		String result = _translator.translateState(keyChar, 1);
		assertEquals("0 mm", result);
		
		result = _translator.translateState(keyChar, 2);
		assertEquals("4 mm", result);
		
		result = _translator.translateState(keyChar, 3);
		assertEquals("5 mm or more", result);
		
		
	}
	
}

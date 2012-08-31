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

import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.NumericCharacter;

/**
 * Tests the CharacterList class.
 */
public class CharacterListTest extends TestCase {

	
	private MutableDeltaDataSet _dataSet;
	private DeltaContext _context;
	private CharacterList _characterList = new CharacterList();
	
	@Before
	public void setUp() {
		DefaultDataSetFactory factory = new DefaultDataSetFactory();
		_dataSet = factory.createDataSet("test");
		_context = new DeltaContext(_dataSet);
		
		_characterList = new CharacterList();
	}
	
	@Test
	public void testCharacterList() throws Exception {
		MultiStateCharacter multiStateChar = addOrderedMultistateChar(4);
		
		String charDescription = 
			"#1. <adaxial> ligule <form; avoid seedlings>/\n"+
			"    1. an unfringed membrane <may be variously hairy or ciliolate>/\n"+
			"    2. a fringed membrane/\n"+
			"    3. a fringe of hairs/\n"+
			"    4. a rim of minute papillae/\n";

		_characterList.parseAndProcess(_context, charDescription);
		
		assertEquals(1, _dataSet.getNumberOfCharacters());
		assertEquals("<adaxial> ligule <form; avoid seedlings>", multiStateChar.getDescription());
		assertEquals(4, multiStateChar.getNumberOfStates());
		assertEquals("an unfringed membrane <may be variously hairy or ciliolate>", multiStateChar.getState(1));
		assertEquals("a fringed membrane", multiStateChar.getState(2));
		assertEquals("a fringe of hairs", multiStateChar.getState(3));
		assertEquals("a rim of minute papillae", multiStateChar.getState(4));
		
	}
	
	@Test
	public void testCharacterListTooManyStates() throws Exception {
		int numStates = 3;
		addOrderedMultistateChar(numStates);
		
		String charDescription = 
			"#1. <adaxial> ligule <form; avoid seedlings>/\n"+
			"    1. an unfringed membrane <may be variously hairy or ciliolate>/\n"+
			"    2. a fringed membrane/\n"+
			"    3. a fringe of hairs/\n"+
			"    4. a rim of minute papillae/\n";
		
		try {
			_characterList.parseAndProcess(_context, charDescription);
			checkError(138);
		}
		catch (DirectiveException e) {
			fail("Should have added error to context");
		}

	}
	
	@Test
	public void testCharacterListNotEnoughStates() throws Exception {
		int numStates = 3;
		addOrderedMultistateChar(numStates);
		
		String charDescription = 
			"#1. <adaxial> ligule <form; avoid seedlings>/\n"+
			"    1. an unfringed membrane <may be variously hairy or ciliolate>/\n"+
			"    2. a fringed membrane/\n";
		
		try {
			_characterList.parseAndProcess(_context, charDescription);
			checkError(16);
		}
		catch (DirectiveException e) {
			fail("Should have added error to context");
		}

	}
	
	@Test
	public void testTooManyUnits() throws Exception {
		addNumericCharacter();
		addNumericCharacter();
		
		
		String charDescription = 
			"#1. <adaxial> ligule <form; avoid seedlings>/\n"+
			"   mm/\n"+
			"   cm/\n" +
			"#2. Another one/\n";
		
		try {
			_characterList.parseAndProcess(_context, charDescription);
			checkError(18);
		}
		catch (DirectiveException e) {
			fail("Should have added error to context");
		}

	}
	
	@Test
	public void testOutOfOrderCharacters() throws Exception {
		addNumericCharacter();
		addNumericCharacter();
		String charDescription = 
			"#2. Another one/\n"+
			"#1. <adaxial> ligule <form; avoid seedlings>/\n"+
			"   mm/\n";
		try {
			_characterList.parseAndProcess(_context, charDescription);
			checkError(14, 14);
		}
		catch (DirectiveException e) {
			fail("Should have added error to context");
		}
	}
	
	@Test
	public void testNotEnoughCharacters() throws Exception {
		addNumericCharacter();
		addNumericCharacter();
		String charDescription = 
			"#1. <adaxial> ligule <form; avoid seedlings>/\n"+
			"   mm/\n";
		try {
			_characterList.parseAndProcess(_context, charDescription);
			checkError(135);
		}
		catch (DirectiveException e) {
			fail("Should have added error to context");
		}
	}
	
	
	private MultiStateCharacter addOrderedMultistateChar(int numStates) {
		_context.setNumberOfCharacters(_context.getNumberOfCharacters()+1);
		MultiStateCharacter character = (MultiStateCharacter)_dataSet.addCharacter(CharacterType.OrderedMultiState);
		character.setNumberOfStates(numStates);
		return character;
	}
	
	private NumericCharacter<?> addNumericCharacter() {
		_context.setNumberOfCharacters(_context.getNumberOfCharacters()+1);
		@SuppressWarnings("unchecked")
		NumericCharacter<Integer> character = (NumericCharacter<Integer>)_dataSet.addCharacter(CharacterType.IntegerNumeric);
		return character;
	}
	
	private void checkError(int... numbers) {
		List<DirectiveError> errors = _context.getErrors();
		assertEquals(numbers.length, errors.size());
		int i=0;
		for (DirectiveError error : errors) {
			assertEquals(numbers[i++], error.getErrorNumber());
		}
	}
	
}

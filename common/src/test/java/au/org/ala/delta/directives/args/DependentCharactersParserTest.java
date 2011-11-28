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
package au.org.ala.delta.directives.args;

import java.io.StringReader;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;

/**
 * Tests the DependentCharactersParser class.
 */
public class DependentCharactersParserTest extends TestCase {

	private MutableDeltaDataSet _dataSet;
	private DeltaContext _context;
	
	@Before
	public void setUp() {
		_dataSet = new DefaultDataSetFactory().createDataSet("test");
		for (int i=1; i<=35; i++) {
			MultiStateCharacter character = (MultiStateCharacter)_dataSet.addCharacter(CharacterType.UnorderedMultiState);
			character.setNumberOfStates(3);
		}
		_context = new DeltaContext(_dataSet);
	}
	
	@Test
	public void testDependentCharacters() throws Exception {
		
		String data = "4,2:16 10,1/3:12-13:20:30-35";
		DependentCharactersParser dependentCharacters = new DependentCharactersParser(_context, new StringReader(data));
		dependentCharacters.parse();
		
		List<CharacterDependency> dependencies = dependentCharacters.getCharacterDependencies();
		assertEquals(2, dependencies.size());
		
		CharacterDependency dependency = dependencies.get(0);
		assertEquals(4, dependency.getControllingCharacterId());
		Set<Integer> states = dependency.getStates();
		assertEquals(1, states.size());
		assertTrue(states.contains(2));
		Set<Integer> controlled = dependency.getDependentCharacterIds();
		assertEquals(1, controlled.size());
		
		dependency = dependencies.get(1);
		assertEquals(10, dependency.getControllingCharacterId());
		states = dependency.getStates();
		assertEquals(2, states.size());
		assertTrue(states.contains(1));
		assertTrue(states.contains(3));
		
		controlled = dependency.getDependentCharacterIds();
		assertEquals(9, controlled.size());
		int[] expected = {12, 13, 20, 30, 31, 32, 33, 34, 35};
		for (int i : expected) {
			assertTrue(controlled.contains(i));
		}
	}
	
	
	
}

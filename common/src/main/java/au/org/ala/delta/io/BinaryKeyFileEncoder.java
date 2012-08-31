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
package au.org.ala.delta.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.translation.FilteredCharacter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.FilteredItem;

/**
 * Knows how to encode a list of character dependencies in the format required
 * by Intkey and Key.
 */
public class BinaryKeyFileEncoder {

	public List<Integer> encodeCharacterDependencies(FilteredDataSet dataSet, boolean useFilteredCharNums) {
		int numChars;
		Iterator<IdentificationKeyCharacter> keyChars;
		if (useFilteredCharNums) {
			numChars = dataSet.getNumberOfFilteredCharacters();
			keyChars = dataSet.identificationKeyCharacterIterator();
		}	
		else {
			numChars = dataSet.getNumberOfCharacters();
			keyChars = dataSet.unfilteredIdentificationKeyCharacterIterator();
		}
		
		List<Integer> dependencyData = initialiseDependencyList(numChars);

		while (keyChars.hasNext()) {
			IdentificationKeyCharacter filteredChar = keyChars.next();
			if (filteredChar.getCharacterType().isMultistate()) {

				addDependencyData(dataSet, dependencyData, filteredChar, useFilteredCharNums);
			}
		}
		return dependencyData;
	}

	public List<Integer> encodeCharacterDependenciesInverted(FilteredDataSet dataSet) {
		int numChars = dataSet.getNumberOfFilteredCharacters();
		Iterator<IdentificationKeyCharacter> keyChars = dataSet.identificationKeyCharacterIterator();
		
		List<Integer> dependencyData = initialiseDependencyList(numChars);

		while (keyChars.hasNext()) {
			IdentificationKeyCharacter filteredChar = keyChars.next();
			au.org.ala.delta.model.Character character = filteredChar.getCharacter();

			addInvertedDependencyData(dataSet, filteredChar.getFilteredCharacterNumber(), dependencyData, character);

		}
		return dependencyData;
	}

	private List<Integer> initialiseDependencyList(int size) {
		Integer[] characters = new Integer[size];
		Arrays.fill(characters, 0);
		List<Integer> dependencyData = new ArrayList<Integer>(Arrays.asList(characters));
		return dependencyData;
	}

	private void addDependencyData(FilteredDataSet dataSet, List<Integer> dependencyData,
			IdentificationKeyCharacter multiStateCharacter, boolean useFilteredCharNumbers) {
		
		int charNumber = useFilteredCharNumbers ? multiStateCharacter.getFilteredCharacterNumber() : multiStateCharacter.getCharacterNumber();
		List<CharacterDependency> dependentCharacters = multiStateCharacter.getDependentCharacters();
		if (dependentCharacters != null && dependentCharacters.size() > 0) {
			// Any location specifications are "1" indexed because the
			// original code was FORTRAN. Intkey expects this and compensates.
			dependencyData.set(charNumber - 1, dependencyData.size() + 1);
			int numStates = multiStateCharacter.getNumberOfStates();
			int statesOffset = dependencyData.size();
			// Start off by adding zeros for each state, which makes the List
			// the
			// correct length to just start adding dependency data at the end.
			for (int state = 0; state < numStates; state++) {
				dependencyData.add(0);
			}
			for (CharacterDependency dependency : dependentCharacters) {
				Set<Integer> dependentChars = dependency.getDependentCharacterIds();
			
				if (useFilteredCharNumbers) {
					Set<Integer> filteredChars = new HashSet<Integer>();
					
					for (int originalCharNum : dependentChars) {
						filteredChars.add(dataSet.filteredCharacterNumber(originalCharNum));
					}
					dependentChars = filteredChars;
					
				}
				List<Integer> dependentCharacterNumbers = toRangeList(dependentChars);

				for (int state : dependency.getStates()) {
					int dataOffset = dependencyData.size() + 1; // Another case
																// of 1 based
																// indexing.

					dependencyData.set(statesOffset + state - 1, dataOffset);
					dependencyData.add(dependentCharacterNumbers.size() / 2);
					dependencyData.addAll(dependentCharacterNumbers);
				}

			}
		}
	}

	private void addInvertedDependencyData(FilteredDataSet dataSet, int filteredCharNumber, List<Integer> invertedDependencyData,
			au.org.ala.delta.model.Character character) {
		List<CharacterDependency> dependencies = character.getControllingCharacters();
		if (dependencies == null || dependencies.size() == 0) {
			return;
		}
		// FORTRAN 1-based array indexing...
		invertedDependencyData.set(filteredCharNumber - 1, invertedDependencyData.size() + 1);
		// Inverted data doesn't contain the number of controlling characters
		// invertedDependencyData.add(dependencies.size());
		Set<Integer> controllingChars = new HashSet<Integer>();
		for (CharacterDependency dependency : dependencies) {
			int controllingCharNumber = dependency.getControllingCharacterId();
			if (!controllingChars.contains(controllingCharNumber)) {
				invertedDependencyData.add(dataSet.filteredCharacterNumber(controllingCharNumber));
			}
			controllingChars.add(controllingCharNumber);
		}
	}

	private List<Integer> toRangeList(Set<Integer> values) {
		List<Integer> rangeList = new ArrayList<Integer>();
		List<Integer> list = new ArrayList<Integer>(values);
		if (list.size() == 1) {

			rangeList.add(list.get(0));
			rangeList.add(list.get(0));
			return rangeList;
		}

		Collections.sort(list);

		int first = 0;
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i) != list.get(i - 1) + 1) {
				rangeList.add(list.get(first));
				rangeList.add(list.get(i - 1));
				first = i;
			}
		}
		rangeList.add(list.get(first));
		rangeList.add(list.get(list.size() - 1));
		return rangeList;
	}

	public BitSet encodeAttributeStates(List<Integer> presentStates) {
		// Turn into bitset.
		BitSet bits = new BitSet();
		for (int state : presentStates) {
			bits.set(state - 1);
		}
		return bits;
	}
	
	public int typeToInt(CharacterType type) {
		switch (type) {
		case UnorderedMultiState:
			return 1;
		case OrderedMultiState:
			return 2;
		case IntegerNumeric:
			return 3;
		case RealNumeric:
			return 4;
		case Text:
			return 5;
		}
		
		throw new IllegalArgumentException("Invalid character type: "+type);
	}
	
	public CharacterType typeFromInt(int encodedType) {
		switch (encodedType) {
		case 1:
			return CharacterType.UnorderedMultiState;
		case 2:
			return CharacterType.OrderedMultiState;
		case 3:
			return CharacterType.IntegerNumeric;
		case 4:
			return CharacterType.RealNumeric;
		case 5:
			return CharacterType.Text;
		}
		
		throw new IllegalArgumentException("Invalid character type: "+encodedType);
	}
	
	public List<Boolean> encodeCharacterMasks(FilteredDataSet dataSet, boolean filterTextChars) {
		Boolean[] init = new Boolean[dataSet.getNumberOfCharacters()];
		Arrays.fill(init, Boolean.FALSE);
		List<Boolean> includedCharacters = new ArrayList<Boolean>(Arrays.asList(init));
		
		Iterator<FilteredCharacter> chars = dataSet.filteredCharacters();
		while (chars.hasNext()) {
			FilteredCharacter character = chars.next();
			if (!filterTextChars || !character.getCharacter().getCharacterType().isText()) {
				includedCharacters.set(character.getCharacter().getCharacterId()-1, Boolean.TRUE);
			}
		}
		return includedCharacters;
	}
	
	public List<Boolean> encodeItemMasks(FilteredDataSet dataSet) {
		Boolean[] init = new Boolean[dataSet.getMaximumNumberOfItems()];
		Arrays.fill(init, Boolean.FALSE);
		List<Boolean> includedItems = new ArrayList<Boolean>(Arrays.asList(init));
		
		Iterator<FilteredItem> items = dataSet.filteredItems();
		while (items.hasNext()) {
			FilteredItem item = items.next();
			includedItems.set(item.getItem().getItemNumber()-1, Boolean.TRUE);
		}
		return includedItems;
	}

}

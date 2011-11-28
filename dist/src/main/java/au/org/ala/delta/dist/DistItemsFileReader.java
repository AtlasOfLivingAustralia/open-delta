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
package au.org.ala.delta.dist;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import au.org.ala.delta.dist.io.DistItemsFile;
import au.org.ala.delta.io.BinaryKeyFileEncoder;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.util.Pair;

/**
 * Reads the DIST items file and constructs a DeltaDataSet from it.
 */
public class DistItemsFileReader {
	
	private DistItemsFile _itemsFile;
	private MutableDeltaDataSet _dataSet;
	private BinaryKeyFileEncoder _encoder;
	private DistContext _context;
	
	public DistItemsFileReader(MutableDeltaDataSet dataSet, DistItemsFile itemsFile, DistContext context) {
		_itemsFile = itemsFile;
		_dataSet = dataSet;
		_context = context;
		_encoder = new BinaryKeyFileEncoder();
	}
	
	public void readAll() {
		createCharacters();
		createItems();
	}
	
	private void createCharacters() {
		
		List<Integer> charTypes = _itemsFile.readCharacterTypes();
		List<Integer> states = _itemsFile.readNumbersOfStates();
		List<Float> weights = _itemsFile.readCharacterWeights();
		List<Boolean> charMask = _itemsFile.readCharacterMask();
		
		for (int i=0; i<_itemsFile.getNumberOfCharacters(); i++) {
			CharacterType type = _encoder.typeFromInt(charTypes.get(i));
			type = effectiveType(type);
			Character character = _dataSet.addCharacter(type);
			if (type.isMultistate()) {
				((MultiStateCharacter)character).setNumberOfStates(states.get(i));
			}
			character.setReliability(weights.get(i));
			if (!charMask.get(i)) {
				_context.excludeCharacter(i+1);
			}
		}
		
	}
	
	/**
	 * During the CONFOR TRANSLATE INTO DIST FORMAT operation, the attribute
	 * values for OrderedMultistate, Integer Numeric and Real Numeric characters
	 * are all converted to a single float.  
	 * This operation is a one way translation so when reading the data set
	 * back out, it is easiest to treat these character types as 
	 * real numerics.
	 * 
	 * @param type the actual character type.
	 * @return either UnorderedMulitstate or Real Numeric.
	 */
	private CharacterType effectiveType(CharacterType type) {
		if (type == CharacterType.UnorderedMultiState || type.isText()) {
			return type;
		}
		
		return CharacterType.RealNumeric;
	}
	
	
	private void createItems() {
		for (int i=1; i<=_itemsFile.getNumberOfItems(); i++) {
			
			Item item = _dataSet.addItem();
			Pair<String, ByteBuffer> itemData = _itemsFile.readItem(i);
			
			item.setDescription(itemData.getFirst());
			
			decodeAttributes(item, itemData.getSecond());
		}
	}
	
	private void decodeAttributes(Item item, ByteBuffer attributeData) {
		Pair<List<Integer>, List<Integer>> offsets = _itemsFile.getAttributeOffsets();
		List<Integer> wordOffsets = offsets.getFirst();
		List<Integer> bitOffsets = offsets.getSecond();
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			int wordOffset = wordOffsets.get(i-1);
			if (wordOffset == 0) {
				continue;
			}
			int bitOffset = bitOffsets.get(i-1);
			
			Character character = _dataSet.getCharacter(i);
			switch (character.getCharacterType()) {
			case UnorderedMultiState:
				decodeUnorderedMultiStateCharacter(item, attributeData, (MultiStateCharacter)character, wordOffset, bitOffset);
				break;
			case RealNumeric:
				decodeRealCharacter(item, attributeData, character, wordOffset);
				break;
			}
		}
	}

	private void decodeRealCharacter(Item item, ByteBuffer attributeData, Character character, int wordOffset) {
		FloatBuffer buffer = attributeData.asFloatBuffer();
		float value = buffer.get(wordOffset-1);
		Attribute attribute = _dataSet.addAttribute(item.getItemNumber(), character.getCharacterId());
		if (value == -9999f || Float.isNaN(value)) {
			return;
		}
		String valueStr = Float.toString(value);
		try {
			attribute.setValueFromString(valueStr);
			}
			catch (Exception e) {
				System.out.println("Breakpoint");
			}
	}

	private void decodeUnorderedMultiStateCharacter(Item item, ByteBuffer attributeData, MultiStateCharacter character, int wordOffset, int bitOffset) {
		IntBuffer buffer = attributeData.asIntBuffer();
		int numStates = character.getNumberOfStates();
		MultiStateAttribute attribute = (MultiStateAttribute)_dataSet.addAttribute(item.getItemNumber(), character.getCharacterId());
		
		int numInts = (numStates+bitOffset) / 32;
		if (numStates % 32 != 0) {
			numInts++;
		}
		
		wordOffset--;
		int[] data = new int[numInts];
		for (int i=wordOffset; i<wordOffset+numInts; i++) {
			data[i-wordOffset] = buffer.get(i);
		}
		
		BigInteger bits = BigInteger.valueOf(data[0]);
		StringBuilder attributeString = new StringBuilder();
		for (int i=bitOffset; i<bitOffset+numStates; i++) {
			if (i %32 == 0) {
				bits = BigInteger.valueOf(data[i/32]);
			}
			if (bits.testBit(i%32)) {
				if (attributeString.length() > 0) {
					attributeString.append("/");
				}
				attributeString.append(i-bitOffset+1);
			}
		}
		try {
		attribute.setValueFromString(attributeString.toString());
		}
		catch (Exception e) {
			System.out.println("Breakpoint");
		}
	}
	
}

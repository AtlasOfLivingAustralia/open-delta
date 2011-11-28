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
package au.org.ala.delta.translation.dist;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.dist.WriteOnceDistItemsFile;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.io.BinaryKeyFileEncoder;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.NumericAttribute;
import au.org.ala.delta.model.NumericRange;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.util.Pair;

/**
 * Writes the key items file using the data in a supplied DeltaContext and
 * associated data set.
 */
public class DistItemsFileWriter {

	public static final int INAPPLICABLE_BIT = 20;
	private WriteOnceDistItemsFile _itemsFile;
	private FilteredDataSet _dataSet;
	private DeltaContext _context;
	private ItemFormatter _itemFormatter;
	private BinaryKeyFileEncoder _encoder;
	
	
	public DistItemsFileWriter(
			DeltaContext context, 
			FilteredDataSet dataSet,
			ItemFormatter itemFormatter,
			WriteOnceDistItemsFile itemsFile) {
		_itemsFile = itemsFile;
		_dataSet = dataSet;
		_context = context;
		_itemFormatter = itemFormatter;
		_encoder = new BinaryKeyFileEncoder();
		
	}
	
	public void writeAll() {
		
		Pair<int[], int[]> offsets = calculateAttributeOffsets();
		 
		Pair<List<Integer>, List<Integer>> itemRecords = writeItems(offsets.getFirst(), offsets.getSecond());
		writeHeading();
		writeCharacterSpecs();
		writeCharacterMask();
		writeCharacterWeights();
		_itemsFile.writeAttributeOffsets(offsets.getFirst(), offsets.getSecond());
		writeItemMask();
		_itemsFile.writeItemRecordsAndNameLengths(itemRecords.getFirst(), itemRecords.getSecond());
		
		// Need to write the header last as it is updated as each section 
		// is written.
		_itemsFile.writeHeader();
	}
	
	protected Pair<List<Integer>, List<Integer>> writeItems(int[] wordOffsets, int[] bitOffsets) {
		 final int BYTES_IN_WORD = 4;
		 List<Integer> itemRecords = new ArrayList<Integer>();
		 List<Integer> nameLengths = new ArrayList<Integer>();
		 int size = BinaryKeyFile.RECORD_LENGTH_BYTES;
		 for (int offset : wordOffsets) {
			 size = Math.max(size, offset);
		 }
		 Iterator<Item> items = _dataSet.unfilteredItems();
		 while (items.hasNext()) {
			 Item item = items.next();
			 String description = _itemFormatter.formatItemDescription(item);
			 nameLengths.add(description.length());
			 byte[] bytes = new byte[(size+1)*BYTES_IN_WORD];
			 Arrays.fill(bytes, (byte)0);
			 
			 ByteBuffer work = ByteBuffer.wrap(bytes);
			 work.order(ByteOrder.LITTLE_ENDIAN);
			 
			 
			 Iterator<IdentificationKeyCharacter> chars = _dataSet.unfilteredIdentificationKeyCharacterIterator();
			 while (chars.hasNext()) {
				 IdentificationKeyCharacter keyChar = chars.next();
				 int charNum = keyChar.getCharacterNumber();
				 if (!keyChar.getCharacterType().isText()) {
					 int offset = wordOffsets[keyChar.getCharacterNumber()-1]-1;
					 if (!(keyChar.getCharacterType() == CharacterType.UnorderedMultiState)) {
						 work.putFloat(offset*BYTES_IN_WORD, -9999.0f);
					 }
					 Attribute attribute = item.getAttribute(keyChar.getCharacter());
					 if (attribute == null || attribute.isUnknown()) {
						 continue;
					 }
					 switch (keyChar.getCharacterType()) {
					 case UnorderedMultiState:
						 encodeUnorderedMultistateAttribute(
								 work, wordOffsets[charNum-1]-1, bitOffsets[charNum-1], 
								 keyChar, (MultiStateAttribute)attribute);
	
						 break;
					 case OrderedMultiState:
						 encodeOrderedMultistateAttribute(
								 work, wordOffsets[charNum-1]-1, keyChar, (MultiStateAttribute)attribute);
						 break;
					 case IntegerNumeric:
					 case RealNumeric:
						 encodeNumericAttribute(
								 work, wordOffsets[charNum-1]-1, keyChar, (NumericAttribute)attribute);
						
						 break;
					 }
				 }
				
			 }
			 itemRecords.add(_itemsFile.writeItem(description, work)); 
		 }
		 return new Pair<List<Integer>, List<Integer>>(itemRecords, nameLengths);
	}
	
	protected void writeHeading() {
		String heading = _context.getHeading(HeadingType.HEADING);
		if (StringUtils.isNotEmpty(heading)) {
			_itemsFile.writeHeading(heading);
		}
	}
	
	protected void writeCharacterSpecs() {
		List<Integer> states = new ArrayList<Integer>();
		List<Integer> types = new ArrayList<Integer>();
		Iterator<IdentificationKeyCharacter> keyChars = _dataSet.unfilteredIdentificationKeyCharacterIterator();
		while (keyChars.hasNext()) {
			IdentificationKeyCharacter keyChar = keyChars.next();
			states.add(keyChar.getNumberOfStates());
			types.add(_encoder.typeToInt(keyChar.getCharacterType()));
		}
		_itemsFile.writeCharacterTypes(types);
		_itemsFile.writeNumbersOfStates(states);
	}
	
	protected void writeCharacterMask() {
		_itemsFile.writeCharacterMask(_encoder.encodeCharacterMasks(_dataSet, false));
	}
	
	protected void writeCharacterWeights() {
		List<Float> weights = new ArrayList<Float>();
		Iterator<IdentificationKeyCharacter> keyChars = _dataSet.unfilteredIdentificationKeyCharacterIterator();
		while (keyChars.hasNext()) {
			IdentificationKeyCharacter keyChar = keyChars.next();
			weights.add(new Float(_context.getCharacterWeight(keyChar.getCharacterNumber())));
		}
		_itemsFile.writeCharacterWeights(weights);	
	}
	
	protected void writeItemMask() {
		_itemsFile.writeItemMask(_encoder.encodeItemMasks(_dataSet));
	}
	
	private void encodeUnorderedMultistateAttribute(ByteBuffer work, int wordOffset, int bitOffset, IdentificationKeyCharacter keyChar, MultiStateAttribute attribute) {
		List<Integer> states = keyChar.getPresentStates(attribute);
		
		int word = work.getInt(wordOffset*4);
		
		for (int state : states) {
			int bit = bitOffset + state-1;
			if (bit > 31) {
				work.putInt(wordOffset*4, word);
				
				wordOffset++;
				word = work.getInt(wordOffset*4);
				bitOffset -= 32;
				bit = bitOffset + state-1;
			}
			word |= (1 << bit);
		}
		work.putInt(wordOffset*4, word);
	}
	
	private void encodeOrderedMultistateAttribute(ByteBuffer work, int wordOffset, IdentificationKeyCharacter keyChar, MultiStateAttribute attribute) {
		List<Integer> states = keyChar.getPresentStates(attribute);
		double average = average(states);
		work.putFloat(wordOffset*4, new Float(average));
		
	}
	
	private void encodeNumericAttribute(ByteBuffer work, int wordOffset, IdentificationKeyCharacter keyChar, NumericAttribute attribute) {
		List<NumericRange> ranges = attribute.getNumericValue();
		List<Double> values = new ArrayList<Double>();
		
		for (NumericRange range : ranges) {
			values.add(range.middle());
			
		}
		double average = average(values);
		work.putFloat(wordOffset*4, new Float(average));
	}
	
	private double average(List<? extends Number> values) {
		int count = 0;
		double sum = 0;
		for (Number value : values) {
			count++;
			sum += value.doubleValue();
		}
		double average = sum/(double)count;
		return average;
	}
	
	private Pair<int[], int[]> calculateAttributeOffsets() {
		// FORTRAN 1 based indexing means our offset starts at 1.
		int wordOffset = 1;
        int bitOffset = 0;
        final int BITS_IN_WORD = 32;
        
        int[] wordOffsets = new int[_dataSet.getNumberOfCharacters()];
        int[] bitOffsets = new int[_dataSet.getNumberOfCharacters()];
        Iterator<IdentificationKeyCharacter> chars = _dataSet.unfilteredIdentificationKeyCharacterIterator();
        while (chars.hasNext()) {
        	IdentificationKeyCharacter character = chars.next();
        	int charNumber = character.getCharacterNumber();
        	bitOffsets[charNumber-1] = 0;
        	if (character.getCharacterType() == CharacterType.UnorderedMultiState) {
        		int numStates = character.getNumberOfStates();
        		wordOffsets[charNumber-1] = wordOffset;
        		bitOffsets[charNumber-1] = bitOffset;
        		bitOffset += numStates;
        		int word = bitOffset / BITS_IN_WORD;
        		wordOffset += word;
        		bitOffset -= word*BITS_IN_WORD;
        	}
        }
        
        if (bitOffset !=0) {
        	wordOffset++;
        }
 
        chars = _dataSet.unfilteredIdentificationKeyCharacterIterator();
        while (chars.hasNext()) {
        	IdentificationKeyCharacter character = chars.next();
        	int charNumber = character.getCharacterNumber();
        	if (character.getCharacterType() != CharacterType.UnorderedMultiState && 
        		character.getCharacterType() != CharacterType.Text) {
        		wordOffsets[charNumber-1] = wordOffset;
        		wordOffset++;
        	}
        }
        
        int itemLength = wordOffset-1;
        _itemsFile.setLengthOfAttributeLists(itemLength);


        return new Pair<int[], int[]>(wordOffsets, bitOffsets);
	}
}

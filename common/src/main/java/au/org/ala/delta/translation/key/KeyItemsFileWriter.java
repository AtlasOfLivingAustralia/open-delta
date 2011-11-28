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
package au.org.ala.delta.translation.key;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.io.BinaryKeyFileEncoder;
import au.org.ala.delta.key.WriteOnceKeyItemsFile;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.NumericAttribute;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.FilteredItem;
import au.org.ala.delta.util.Pair;

/**
 * Writes the key items file using the data in a supplied DeltaContext and
 * associated data set.
 */
public class KeyItemsFileWriter {

	public static final int INAPPLICABLE_BIT = 20;
	private WriteOnceKeyItemsFile _itemsFile;
	private FilteredDataSet _dataSet;
	private DeltaContext _context;
	private ItemFormatter _itemFormatter;
	private BinaryKeyFileEncoder _encoder;
	
	
	public KeyItemsFileWriter(
			DeltaContext context, 
			FilteredDataSet dataSet,
			ItemFormatter itemFormatter,
			WriteOnceKeyItemsFile itemsFile) {
		_itemsFile = itemsFile;
		_dataSet = dataSet;
		_context = context;
		_itemFormatter = itemFormatter;
		_encoder = new BinaryKeyFileEncoder();
		
	}
	
	public void writeAll() {
		
		writeItems();
		writeHeading();
		writeCharacterMask();
		writeNumbersOfStates();
		writeCharacterDependencies();
		writeCharacterReliabilities();
		writeTaxonMask();
		writeItemLengths();
		writeItemAbundances();
		
		// Need to write the header last as it is updated as each section 
		// is written.
		_itemsFile.writeHeader();
	}
	
	
	
	protected void writeItems() {
		
		List<Pair<String, List<BitSet>>> items = new ArrayList<Pair<String,List<BitSet>>>();
		for (int i=1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			items.add(writeItem(_dataSet.getItem(i)));
		}
		_itemsFile.writeItems(items);
	}
	
	protected Pair<String, List<BitSet>> writeItem(Item item) {
		
		String description = _itemFormatter.formatItemDescription(item, CommentStrippingMode.STRIP_ALL);
		List<BitSet> attributes = new ArrayList<BitSet>();
		Iterator<IdentificationKeyCharacter> keyChars = _dataSet.unfilteredIdentificationKeyCharacterIterator();
		while (keyChars.hasNext()) {
			IdentificationKeyCharacter keyChar = keyChars.next();
			Attribute attribute = item.getAttribute(keyChar.getCharacter());
			List<Integer> states = new ArrayList<Integer>();
			
			if (!attribute.isInapplicable()) {
				if (keyChar.getCharacterType().isMultistate()) {
					states = keyChar.getPresentStates((MultiStateAttribute)attribute);
				}
				else if (keyChar.getCharacterType().isNumeric()) {
					states = keyChar.getPresentStates((NumericAttribute)attribute);
					 
				}
			}
			BitSet bits = _encoder.encodeAttributeStates(states);
			if (attribute.isInapplicable()) {
				bits.set(INAPPLICABLE_BIT);
			}
			attributes.add(bits);
		}
		return new Pair<String, List<BitSet>>(description, attributes);
		
	}
	
	protected void writeCharacterDependencies() {
		List<Integer> dependencyData = _encoder.encodeCharacterDependencies(_dataSet, false);
		
		_itemsFile.writeCharacterDependencies(dependencyData);
	}
	
	protected void writeHeading() {
		String heading = _context.getHeading(HeadingType.HEADING);
		if (heading == null) {
			heading = " ";
		}
		_itemsFile.writeHeading(heading);

	}
	
	protected void writeCharacterMask() {
		List<Boolean> includedCharacters = _encoder.encodeCharacterMasks(_dataSet, true);
		_itemsFile.writeCharacterMask(includedCharacters);
	}
	
	protected void writeNumbersOfStates() {
		List<Integer> states = new ArrayList<Integer>();
		Iterator<IdentificationKeyCharacter> keyChars = _dataSet.unfilteredIdentificationKeyCharacterIterator();
		while (keyChars.hasNext()) {
			IdentificationKeyCharacter keyChar = keyChars.next();
			states.add(keyChar.getNumberOfStates());
		}
		_itemsFile.writeNumbersOfStates(states);
	}
	
	protected void writeCharacterReliabilities() {
		List<Float> reliabilities = new ArrayList<Float>();
		Iterator<IdentificationKeyCharacter> keyChars = _dataSet.unfilteredIdentificationKeyCharacterIterator();
		while (keyChars.hasNext()) {
			IdentificationKeyCharacter keyChar = keyChars.next();
			reliabilities.add(new Float(_context.getCharacterReliability(keyChar.getCharacterNumber())));
		}
		_itemsFile.writeCharacterReliabilities(reliabilities);	
	}
	
	protected void writeTaxonMask() {
		List<Boolean> includedItems = _encoder.encodeItemMasks(_dataSet);
		_itemsFile.writeTaxonMask(includedItems);
	}
	
	protected void writeItemLengths() {
		// It would have been easier to write this when the items were 
		// written but testing is easier if we maintain the same ordering
		// as CONFOR.
		List<Pair<String, List<BitSet>>> items = new ArrayList<Pair<String,List<BitSet>>>();
		for (int i=1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			items.add(writeItem(_dataSet.getItem(i)));
		}
		List<Integer> lengths = new ArrayList<Integer>();
		for (Pair<String, List<BitSet>> item : items) {
			lengths.add(item.getFirst().length());
		}
		_itemsFile.writeItemLengths(lengths);
	}
	
	protected void writeItemAbundances() {
		List<Float> abundances = new ArrayList<Float>();
		Iterator<FilteredItem> items = _dataSet.filteredItems();
		while (items.hasNext()) {
			FilteredItem item = items.next();
			double abundancy = _context.getItemAbundancy(item.getItem().getItemNumber());
			abundancy = Math.log(abundancy)/Math.log(2) +5;
			abundances.add(new Float(abundancy));
		}
		_itemsFile.writeItemAbundances(abundances);	
	}
}

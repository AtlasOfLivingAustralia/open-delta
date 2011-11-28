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
package au.org.ala.delta.translation.print;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.AbstractIterativeTranslator;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.Words;
import au.org.ala.delta.translation.Words.Word;
import au.org.ala.delta.translation.delta.DeltaWriter;

/**
 * Writes the character list to the print file.
 */
public class UncodedCharactersPrinter extends AbstractIterativeTranslator {
	
	private ItemListTypeSetter _typeSetter;
	private PrintFile _printFile;
	private ItemFormatter _itemFormatter;
	private DeltaContext _context;
	protected List<Character> _uncodedChars;
	private DeltaWriter _deltaWriter;
	private boolean _omitItemDescription;
	protected UncodedCharactersTypeSetter _charactersTypesetter;
	
	
	public UncodedCharactersPrinter(
			DeltaContext context, 
			PrintFile printFile, 
			ItemFormatter itemFormatter,
			ItemListTypeSetter typeSetter,
			UncodedCharactersTypeSetter charactersTypesetter,
			boolean omitItemDescription) {
		_typeSetter = typeSetter;
		_printFile = printFile;
		_itemFormatter = itemFormatter;
		_deltaWriter = new DeltaWriter();
		_context = context;
		_omitItemDescription = omitItemDescription;
		_charactersTypesetter = charactersTypesetter;
	}
	
	@Override
	public void beforeFirstItem() {
		
	}
	
	@Override
	public void beforeItem(Item item) {
		_charactersTypesetter.beforeUncodedCharacterList();
		if (!_omitItemDescription) {
			_typeSetter.beforeItem(item);
			_printFile.outputLine(_itemFormatter.formatItemDescription(item));
			_typeSetter.afterItemName();
		}
		_uncodedChars = new ArrayList<Character>();
		_charactersTypesetter.beforeNewParagraph();
	}

	
	@Override
	public void beforeAttribute(Attribute attribute) {
		Character character = attribute.getCharacter();
		Item item = attribute.getItem();
		
		if (_context.getDataSet().isUncoded(item, character)) {
			_uncodedChars.add(character);
		}
	}
	
	@Override
	public void afterItem(Item item) {
		
		StringBuilder uncoded = new StringBuilder();
		uncoded.append(Words.word(Word.NOT_CODED)).append(":");
		
		appendUncodedCharacters(uncoded);
		
		_printFile.setIndent(6);
		_printFile.capitaliseNextWord();
		_printFile.outputLine(uncoded.toString());
		_printFile.setIndent(0);
	}
	
	protected void appendUncodedCharacters(StringBuilder out) {
		

		out.append(" ");
		List<Integer> charNums = new ArrayList<Integer>();
		
		for (Character character : _uncodedChars) {
			int charNum = character.getCharacterId();
			charNums.add(charNum);
		}
		
		// Excluded characters still influence the output.  They cannot
		// appear explicitly but can determine whether a range is 
		// broken or not.
		Iterator<Integer> charNumIterator = charNums.iterator();
		while (charNumIterator.hasNext()) {
			int charNum = charNumIterator.next();
			if (_context.isCharacterExcluded(charNum)) {
				if (!charNums.contains(charNum-1) || !charNums.contains(charNum+1)) {
					charNumIterator.remove();
				}
			}
		}
		
		out.append(_deltaWriter.rangeToString(charNums, _charactersTypesetter.rangeSeparator()));

	}

	
	@Override
	public void afterLastItem() {
		_typeSetter.afterLastItem();
	}
}

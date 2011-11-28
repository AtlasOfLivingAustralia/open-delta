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

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.translation.ItemListTypeSetterAdapter;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.delta.DeltaFormatTranslator;

/**
 * Writes the character list to the print file.
 */
public class CharacterListPrinter extends DeltaFormatTranslator {
	
	private DeltaContext _context;
	
	public CharacterListPrinter(
			DeltaContext context, 
			PrintFile printer, 
			CharacterFormatter characterFormatter,
			CharacterListTypeSetter typeSetter) {
		super(context, printer, null, characterFormatter, null, typeSetter, new ItemListTypeSetterAdapter());
		_context = context;
	}
	
	@Override
	public void beforeFirstCharacter() {
		_typeSetter.beforeFirstCharacter();
	}

	@Override
	public void beforeCharacter(Character character) {
		
		printCharacterHeading(character);
		
		_typeSetter.beforeCharacter();
		super.beforeCharacter(character);
		
	}

	private void printCharacterHeading(Character character) {
		String heading = _context.getCharacterHeading(character.getCharacterId());
		if (StringUtils.isNotBlank(heading)) {
			
			_typeSetter.beforeCharacterHeading();
			_printer.outputLine(0, _characterFormatter.defaultFormat(heading), 1);
			_typeSetter.afterCharacterHeading();
		}
	}

	@Override
	public void afterCharacter(Character character) {
		if (character.hasNotes()) {
			_typeSetter.beforeCharacterNotes();
			_printer.setIndentOnLineWrap(false);
			String notes = _characterFormatter.formatNotes(character);
			_printer.outputLine(0, notes, 0);
			_printer.setIndentOnLineWrap(true);
		}
		_printer.writeBlankLines(1, 0);
	}
	
	@Override
	public void afterLastCharacter() {
		_typeSetter.afterCharacterList();
	}
	
	@Override
	public void beforeFirstItem() {}

	@Override
	public void beforeItem(Item item) {}

	@Override
	public void afterItem(Item item) {}

	@Override
	public void beforeAttribute(Attribute attribute) {}
	
	@Override
	public void translateOutputParameter(OutputParameter parameter) {}
		
}

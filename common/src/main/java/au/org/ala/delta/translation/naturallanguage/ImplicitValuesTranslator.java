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
package au.org.ala.delta.translation.naturallanguage;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.DataSetFilter;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;

/**
 * Writes the implicit attributes section during a natural language translation
 * if the TRANSLATE IMPLICIT VALUES directive is used.
 */
public class ImplicitValuesTranslator extends NaturalLanguageTranslator {

	private DataSetFilter _filter;
	
	public ImplicitValuesTranslator(DeltaContext context, DataSetFilter filter, ItemListTypeSetter typeSetter,
			PrintFile printer, ItemFormatter itemFormatter, CharacterFormatter characterFormatter,
			AttributeFormatter attributeFormatter) {
		super(context, typeSetter, printer, itemFormatter, characterFormatter, attributeFormatter);
		_filter = filter;
	}

	public void translateImplicitValues() {

		
		// The algorithm for translating implicit values is the same as
		// for translating an Item, so we are making a fake, stand-alone Item,
		// populating it with attributes that represent the implicit values
		// then translating it.
		DefaultDataSetFactory factory = new DefaultDataSetFactory();
		Item item = factory.createItem(0);

		for (int i = 1; i <= _dataSet.getNumberOfCharacters(); i++) {
			Character character = _dataSet.getCharacter(i);
			if (_filter.filter(character)) {
			
				if (character.getCharacterType().isMultistate()) {
					MultiStateCharacter multiStateChar = (MultiStateCharacter) character;
					int implicitState = multiStateChar.getUncodedImplicitState();
					if (implicitState > 0) {
						Attribute attribute = factory.createAttribute(character, item);
						try {
							attribute.setValueFromString(Integer.toString(implicitState));
						}
						catch (DirectiveException e) {
							// Since we aren't parsing user data here this shouldn't happen.
							throw new RuntimeException(e);
						}
						item.addAttribute(character, attribute);
						
						beforeAttribute(attribute);
	
					}
				}
			}
		}
		afterItem(item);
		_printer.printBufferLine();
		
	}

}

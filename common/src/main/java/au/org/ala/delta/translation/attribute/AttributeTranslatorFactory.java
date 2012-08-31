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
package au.org.ala.delta.translation.attribute;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.translation.FormatterFactory;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.TypeSetterFactory;

public class AttributeTranslatorFactory {

	private DeltaContext _context;
	private CharacterFormatter _characterFormatter;
	private AttributeFormatter _attributeFormatter;
	private ItemListTypeSetter _typeSetter;
	
	public AttributeTranslatorFactory(DeltaContext context) {
		FormatterFactory formatterFactory = new FormatterFactory(context);
		_context = context;
		_attributeFormatter = formatterFactory.createAttributeFormatter();
		_characterFormatter = formatterFactory.createCharacterFormatter();
		_typeSetter = new TypeSetterFactory().createTypeSetter(_context, null);
	}
	
	public AttributeTranslatorFactory(
			DeltaContext context, 
			CharacterFormatter charFormatter, 
			AttributeFormatter attributeFormatter,
			ItemListTypeSetter typeSetter) {
		_characterFormatter = charFormatter;
		_context = context;
		_attributeFormatter = attributeFormatter;
		_typeSetter = typeSetter;
	}

	public AttributeTranslator translatorFor(Character character) {
		boolean omitOr = _context.isOrOmmitedForCharacter(character.getCharacterId());

		AttributeTranslator translator;
		if (character instanceof MultiStateCharacter) {
			translator = new MultiStateAttributeTranslator(
					(MultiStateCharacter) character, 
					_characterFormatter,
					_attributeFormatter,
					omitOr);
		}
		else if (character instanceof NumericCharacter<?>) {
			translator = new NumericAttributeTranslator(
					(NumericCharacter<?>) character, 
					_typeSetter,
					_attributeFormatter,
					_context.getOmitSpaceBeforeUnits(),
					_context.getOmitLowerForCharacter(character.getCharacterId()),
					omitOr,
                    _context.getDecimalPlaces(character.getCharacterId()));
		}
		else {
			translator = new TextAttributeTranslator(_attributeFormatter, omitOr);
		}
		if (_context.getUseAlternateComma(character.getCharacterId())) {
			translator.useAlternateComma();
		}
		if (_context.getOmitInapplicables()) {
			translator.omitInapplicables();
		}
		if (_context.isFinalCommaOmmitedForCharacter(character.getCharacterId())) {
			translator.omitFinalComma();
		}
		if (!_context.getReplaceSemiColonWithComma(character.getCharacterId()).isEmpty()) {
			translator.omitAllCommas();
		}
		return translator;
	}
}

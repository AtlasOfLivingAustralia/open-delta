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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.Words;
import au.org.ala.delta.translation.Words.Word;

public class UncodedCharactersTranslator extends UncodedCharactersPrinter {
	private CharacterFormatter _characterFormatter;
	public UncodedCharactersTranslator(
			DeltaContext context, 
			PrintFile printFile, 
			ItemFormatter itemFormatter,
			CharacterFormatter characterFormatter,
			ItemListTypeSetter typeSetter,
			UncodedCharactersTypeSetter charactersTypesetter,
			boolean omitItemDescription) {
		super(context, printFile, itemFormatter, typeSetter, charactersTypesetter, omitItemDescription);
		_characterFormatter = characterFormatter;
	}
	
	
	protected void appendUncodedCharacters(StringBuilder out) {
		for (Character character : _uncodedChars) {
			out.append(" ");
			out.append(_characterFormatter.formatCharacterDescription(character));
			out.append(Words.word(Word.FULL_STOP));
		}
		
	}

}

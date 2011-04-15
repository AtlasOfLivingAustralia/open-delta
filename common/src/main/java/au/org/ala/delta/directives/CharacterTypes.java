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
package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.Logger;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;

public class CharacterTypes extends AbstractCharacterListDirective<DeltaContext, CharacterType> {

	public CharacterTypes() {
		super("character", "types");
	}

	@Override
	protected CharacterType interpretRHS(DeltaContext context, String rhs) {
		return CharacterType.parse(rhs);
	}

	@Override
	public void process(DeltaContext context, String data) throws Exception {
		super.process(context, data);
		
		createDefaultCharacters(context);
	}
	
	@Override
	protected void processCharacter(DeltaContext context, int charNumber, CharacterType type) {
		Logger.debug("Setting type for character %d to %s", charNumber, type);
		
		DeltaDataSet dataSet = context.getDataSet();
		dataSet.addCharacter(charNumber, type);
	}
	
	
	/**
	 * Characters not explicitly listed in the CHARACTER TYPES directive default to 
	 * unordered multistate characters.
	 * @param dataSet
	 */
	private void createDefaultCharacters(DeltaContext context) {
		
		DeltaDataSet dataSet = context.getDataSet();
		for (int i=1; i<=context.getNumberOfCharacters(); i++) {
			Logger.debug("Creating a default (unordered multistate) character with number %d", i);
			if (dataSet.getCharacter(i) == null) {
				dataSet.addCharacter(i, CharacterType.UnorderedMultiState);
			}
		}
	}
}

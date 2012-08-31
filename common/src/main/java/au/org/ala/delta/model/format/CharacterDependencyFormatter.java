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
package au.org.ala.delta.model.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;

/**
 * Formats CharacterDependencies in a consistent manner.
 */
public class CharacterDependencyFormatter {

	private MutableDeltaDataSet _dataSet;
	private CharacterFormatter _characterFormatter;
	
	public CharacterDependencyFormatter(MutableDeltaDataSet dataSet) {
		_dataSet = dataSet;
		_characterFormatter = new CharacterFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, true, false);
	}
	
	public String formatCharacterDependency(CharacterDependency characterDependency) {
		
		MultiStateCharacter controllingCharacter = (MultiStateCharacter)_dataSet.getCharacter(characterDependency.getControllingCharacterId());
		
		StringBuilder description = new StringBuilder();
		
		appendSummary(characterDependency, controllingCharacter, description);
		
		appendText(characterDependency.getStatesAsList(), controllingCharacter, description);
		
		return description.toString();
	}
	
	private void appendSummary(CharacterDependency characterDependency,
			MultiStateCharacter controllingCharacter, StringBuilder description) {
		description.append("[").append(controllingCharacter.getCharacterId());
		description.append(",");
		List<Integer> stateNumbers = characterDependency.getStatesAsList();
		for (int i=0; i<stateNumbers.size()-1; i++) {
			description.append(stateNumbers.get(i));
			description.append("/");
		}
		description.append(stateNumbers.get(stateNumbers.size()-1));
		description.append("] ");
	}

	private void appendText(List<Integer> stateNumbers,
			MultiStateCharacter controllingCharacter, StringBuilder description) {
		
		String charDescription = _characterFormatter.formatCharacterDescription(controllingCharacter);
		
		if (StringUtils.isEmpty(charDescription)) {
			charDescription = _characterFormatter.formatCharacterDescription(controllingCharacter, CommentStrippingMode.RETAIN);
		}
		description.append(charDescription);
		description.append(": ");
		
		for (int i=0; i<stateNumbers.size()-1; i++) {
			description.append(_characterFormatter.formatState(controllingCharacter, stateNumbers.get(i)));
			description.append(", or ");
		}
		description.append(_characterFormatter.formatState(controllingCharacter, stateNumbers.get(stateNumbers.size()-1)));
	}
	
	public String defaultLabelFor(MultiStateCharacter controllingCharacter, Collection<Integer> states) {
		StringBuilder builder = new StringBuilder();
		appendText(new ArrayList<Integer>(states), controllingCharacter, builder);
		return builder.toString();
	}
	
}

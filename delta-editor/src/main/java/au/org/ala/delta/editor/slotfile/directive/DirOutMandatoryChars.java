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
package au.org.ala.delta.editor.slotfile.directive;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MutableDeltaDataSet;

/**
 * Exports the MANDATORY CHARACTERS directive.
 */
public class DirOutMandatoryChars extends AbstractDirOutFunctor {

	@Override
	public void writeDirectiveArguments(DirectiveInOutState state) {
		MutableDeltaDataSet dataSet = state.getDataSet();
		
		List<Integer> mandatoryChars = new ArrayList<Integer>();
		
		for (int i=1; i<=dataSet.getNumberOfCharacters(); i++) {
			Character character = dataSet.getCharacter(i);
			if (character.isMandatory()) {
				mandatoryChars.add(i);
			}
		}
		writeLine(state, _deltaWriter.rangeToString(mandatoryChars));
	}

}

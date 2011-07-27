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
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.DeltaDataSet;

/**
 * Exports the DEPENDENT CHARACTERS directive.
 */
public class DirOutDependentChars extends AbstractDirOutFunctor {

	@Override
	public void writeDirectiveArguments(DirectiveInOutState state) {
		DeltaDataSet dataSet = state.getDataSet();

		List<CharacterDependency> characterDependencies = dataSet
				.getAllCharacterDependencies();

		List<CharacterDependency> controllingVector = checkDependencies(state,
				characterDependencies);
		Collections.sort(controllingVector);

		for (CharacterDependency dependency : controllingVector) {
			
			_textBuffer.append(' ');
			int charNo = dependency.getControllingCharacterId();
			_textBuffer.append(charNo).append(',');

			List<Integer> states = dependency.getStatesAsList();
			Collections.sort(states);

			for (int i = 0; i < states.size(); i++) {
				if (i != 0) {
					_textBuffer.append('/');
				}
				_textBuffer.append(states.get(i));
			}

			List<Integer> controlledCharacters = new ArrayList<Integer>(
					dependency.getDependentCharacterIds());
			Collections.sort(controlledCharacters);

			_textBuffer.append(':');
			_textBuffer.append(_deltaWriter.rangeToString(controlledCharacters, ':'));
			
		}
		writeLine(state, _textBuffer.toString());
	}

	private List<CharacterDependency> checkDependencies(
			DirectiveInOutState state,
			List<CharacterDependency> characterDependencies) {
		boolean contWarn = true;
		boolean labelWarn = true;

		List<CharacterDependency> controllingVector = new ArrayList<CharacterDependency>();
		for (CharacterDependency dependency : characterDependencies) {

			if (dependency.getDependentCharacterCount() > 0) {
				controllingVector.add(dependency);
			} else if (contWarn) {
				state.error("TDirInOutEx(ED_NO_CONTROLLED_CHARS)");
				contWarn = false;
			}
			if (!StringUtils.isEmpty(dependency.getDescription()) && labelWarn) {
				state.error("TDirInOutEx(ED_CONTATTR_HAS_LABEL)");
				labelWarn = false;

			}
		}
		return controllingVector;
	}

}

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;

/**
 * Exports the APPLICABLE CHARACTERS directive.
 */
public class DirOutApplicableChars extends AbstractDirOutFunctor {

	@Override
	public void writeDirectiveArguments(DirectiveInOutState state) {
		// YUCK! To get this to work, we must first gather together ALL the
		// controlling
		// attributes under the control of a given character, then accumulate a
		// list of
		// all the characters under the control of this controlling character,
		// via any
		// of its various controlling attributes. Then go through each state of
		// the controlling
		// character and determine which of the controlled characters are NOT
		// under it's
		// control...

		MutableDeltaDataSet dataSet = state.getDataSet();

		for (int i = 1; i <= dataSet.getNumberOfCharacters(); i++) {
			Character character = dataSet.getCharacter(i);

			if (character.getDependentCharacters().size() == 0)
				continue;

			MultiStateCharacter multiStateChar = (MultiStateCharacter) character;
			int nStates = multiStateChar.getNumberOfStates();
			if (nStates == 0)
				throw new RuntimeException(" TDirInOutEx(ED_INTERNAL_ERROR)");

			// std::vector<TUniIdSet> stateLists(nStates); // Creates a vector
			// of sets; one set for each state
			// std::vector<TUniIdVector> revStateLists(nStates); // And a
			// corresponding vector of vectors
			List<Set<Integer>> stateLists = new ArrayList<Set<Integer>>();
			for (int tmp = 0; tmp < nStates; tmp++) {
				stateLists.add(new HashSet<Integer>());
			}
			Set<Integer> allDeps = new HashSet<Integer>();

			List<CharacterDependency> contAttrs = character
					.getDependentCharacters();

			for (CharacterDependency dependency : contAttrs) {

				List<Integer> states = dependency.getStatesAsList();
				Set<Integer> controlled = dependency.getDependentCharacterIds();
				for (int contCharNo : controlled) {

					if (contCharNo == 0)
						throw new RuntimeException(
								"TDirInOutEx(ED_INTERNAL_ERROR)");
					allDeps.add(contCharNo);
					for (int stateNo : states) {

						if (stateNo == 0 || stateNo > nStates)
							throw new RuntimeException(
									"TDirInOutEx(ED_INTERNAL_ERROR)");
						stateLists.get(stateNo - 1).add(contCharNo);
					}
				}
			}
			// At this point, allDeps should contain the character numbers
			// of all characters under the control of the current one
			List<List<Integer>> revStateLists = new ArrayList<List<Integer>>();

			for (int m = 0; m < nStates; ++m) {
				Set<Integer> tmp = new HashSet<Integer>(allDeps);
				tmp.removeAll(stateLists.get(m));
				revStateLists.add(new ArrayList<Integer>(tmp));
				Collections.sort(revStateLists.get(m));

			}

			// And now we should have, for each state, a sorted list of the
			// character numbers of
			// of all characters which it makes applicable.
			for (int m = 0; m < nStates; ++m) {
				if (revStateLists.get(m).size() > 0) {
					_textBuffer.append(' ');
					_textBuffer.append(i);
					_textBuffer.append(',');
					_textBuffer.append(m + 1);

					// Search for any other states with the same set of
					// applicable characters
					for (int n = m + 1; n < nStates; ++n) {
						if (revStateLists.get(m).equals(revStateLists.get(n))) {
							_textBuffer.append('/');
							_textBuffer.append(n + 1);
							revStateLists.get(n).clear();
						}
					}
					_textBuffer.append(':');
					_textBuffer.append(_deltaWriter.rangeToString(revStateLists.get(m), ':', '-'));
				}
			}
		}
		writeLine(state, _textBuffer.toString());
	}
}

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;


/**
 * Processes the APPLICABLE CHARACTERS directive.
 */
public class ApplicableCharacters extends AbstractCharacterDependencyDirective {

	public static final String[] CONTROL_WORDS = {"applicable", "characters"};

	public ApplicableCharacters() {
		super(CONTROL_WORDS);
	}
	
	@Override
	protected void addCharacterDependencies(DeltaContext context, List<CharacterDependency> dependencies) {
		MutableDeltaDataSet dataSet = context.getDataSet();
		
		for (CharacterDependency dependency : dependencies) {
			MultiStateCharacter character = (MultiStateCharacter)dataSet.getCharacter(dependency.getControllingCharacterId());
			Set<Integer> states = invertStates(character, dependency.getStates());
			dataSet.addCharacterDependency(character, states, dependency.getDependentCharacterIds());
		}
	}
	
	private Set<Integer> invertStates(MultiStateCharacter character, Set<Integer> states) {
		Set<Integer> inverted = new HashSet<Integer>();
		for (int i=1; i<character.getNumberOfStates(); i++) {
			if (!states.contains(i)) {
				inverted.add(i);
			}
		}
		return inverted;
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
	
}

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
package au.org.ala.delta.dist;

import java.util.Set;

import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;

/**
 * Calculates the difference between two multistate attributes using a sum
 * of the probabilities that each state is present in the attribute.
 */
public class DefaultMultiStateDifferenceCalculator implements MultiStateDifferenceCalculator {

	public float computeMultiStateDifference(MultiStateAttribute attribute1, MultiStateAttribute attribute2) {
		MultiStateCharacter character = (MultiStateCharacter)attribute1.getCharacter();
		int numStates = character.getNumberOfStates();
		Set<Integer> states1 = attribute1.getPresentStates();
		Set<Integer> states2 = attribute2.getPresentStates();
		
		if ((states1.size() == 0) || (states2.size() ==0)) {
			throw new IllegalArgumentException("Zero states coded - attribute unknown!");
		}
		float distance = 0;
		for (int i=1; i<=numStates; i++) {
			float p1 = (states1.contains(i) ? 1f : 0f) / states1.size();
			float p2 = (states2.contains(i) ? 1f : 0f) / states2.size();
			distance += Math.abs(p1-p2);
		}
		
		return distance*0.5f;
	}

}

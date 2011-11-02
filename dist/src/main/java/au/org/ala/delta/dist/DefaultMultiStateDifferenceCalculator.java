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

package au.org.ala.delta.dist;

import java.util.HashSet;
import java.util.Set;

import au.org.ala.delta.model.MultiStateAttribute;

/**
 * If any of the states in attribute 1 are also present in attribute 2 the
 * difference is 0.  Else the difference is 1.
 */
public class MatchOverlapMultiStateDifferenceCalculator implements MultiStateDifferenceCalculator {

	public float computeMultiStateDifference(MultiStateAttribute attribute1, MultiStateAttribute attribute2) {
		Set<Integer> states1 = new HashSet<Integer>(attribute1.getPresentStates());
		Set<Integer> states2 = attribute2.getPresentStates();
		
		states1.retainAll(states2);
		
		return states1.isEmpty() ? 1f : 0f;
	}

}

package au.org.ala.delta.dist;

import au.org.ala.delta.model.MultiStateAttribute;

/**
 * Computes the difference between two MultiState attributes.
 */
public interface MultiStateDifferenceCalculator {

	public float computeMultiStateDifference(MultiStateAttribute attribute1, MultiStateAttribute attribute2);
		
}

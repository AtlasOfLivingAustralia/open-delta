package au.org.ala.delta.translation;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * Lets all Items, Characters and Attributes through the filter.
 */
public class AllPassFilter implements DataSetFilter {

	@Override
	public boolean filter(Item item) {
		return true;
	}

	@Override
	public boolean filter(Item item, Character character) {
		return true;
	}

	@Override
	public boolean filter(Character character) {
		return true;
	}

}

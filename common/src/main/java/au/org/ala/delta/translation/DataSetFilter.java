package au.org.ala.delta.translation;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

public interface DataSetFilter {

	/**
	 * Filters the supplied Item.
	 * @param item the Item to filter.
	 * @return true if the item should be included in the translation.
	 */
	public boolean filter(Item item);

	/**
	 * Filters the supplied Attribute.
	 * @param attribute the Attribute to filter.
	 * @return true if the attribute should be included in the translation.
	 */
	public boolean filter(Item item, Character character);
	
	/**
	 * Filters the supplied Character during a Character translation operation.
	 * @param character the character to filter.
	 * @return true if the character should be translated, false otherwise.
	 */
	public boolean filter(Character character);

}
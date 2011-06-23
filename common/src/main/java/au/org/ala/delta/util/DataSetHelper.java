package au.org.ala.delta.util;

import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * Contains helper methods for working with a DeltaDataSet. These methods could
 * belong in the dataset itself but is is already quite cluttered.
 */
public class DataSetHelper {

	private DeltaDataSet _dataSet;

	public DataSetHelper(DeltaDataSet dataSet) {
		assert (dataSet != null);
		_dataSet = dataSet;
	}

	/**
	 * Returns the next Item in the Data Set that has an Image associated 
	 * with it.
	 * @param startFrom the Item to start from when searching for the "next"
	 * Item.
	 * @return the first Item found with an item number greater than the
	 * starting Item which also has at least one image.  If no such Item
	 * exists, null will be returned.
	 */
	public Item getNextItemWithImage(Item startFrom) {

		int itemNumber = startFrom.getItemNumber();

		for (int i = itemNumber + 1; i <= _dataSet.getMaximumNumberOfItems(); i++) {
			Item next = _dataSet.getItem(i);
			if (next.getImageCount() > 0) {
				return next;
			}
		}
		return null;
	}

	/**
	 * Returns the next Item in the Data Set that has an Image associated 
	 * with it.
	  * @param startFrom the Item to start from when searching for the 
	 * "previous" Item.
	 * @return the first Item found with an item number less than the
	 * starting Item which also has at least one image.  If no such 
	 * Item exists, null will be returned.
	 */
	public Item getPreviousItemWithImage(Item startFrom) {

		int itemNumber = startFrom.getItemNumber();

		for (int i = itemNumber - 1; i > 0; i--) {
			Item next = _dataSet.getItem(i);
			if (next.getImageCount() > 0) {
				return next;
			}
		}
		return null;
	}

	/**
	 * Returns the next Character in the Data Set that has an Character 
	 * associated with it.
	 * @param startFrom the Character to start from when searching for the 
	 * "next" Character.
	 * @return the first Character found with an item number greater than the
	 * starting Character which also has at least one image.  If no such 
	 * Character exists, null will be returned.
	 */
	public Character getNextCharacterWithImage(Character startFrom) {

		int characterNumber = startFrom.getCharacterId();

		for (int i = characterNumber + 1; i <= _dataSet.getNumberOfCharacters(); i++) {
			Character next = _dataSet.getCharacter(i);
			if (next.getImageCount() > 0) {
				return next;
			}
		}
		return null;
	}

	/**
	 * Returns the previous Character in the Data Set that has an Character 
	 * associated with it.
	 * @param startFrom the Character to start from when searching for the 
	 * "previous" Character.
	 * @return the first Character found with an item number less than the
	 * starting Character which also has at least one image.  If no such 
	 * Character exists, null will be returned.
	 */
	public Character getPreviousCharacterWithImage(Character startFrom) {

		int characterNumber = startFrom.getCharacterId();

		for (int i = characterNumber - 1; i > 0; i--) {
			Character next = _dataSet.getCharacter(i);
			if (next.getImageCount() > 0) {
				return next;
			}
		}
		return null;
	}
}

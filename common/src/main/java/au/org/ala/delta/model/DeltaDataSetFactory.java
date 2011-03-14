package au.org.ala.delta.model;


/**
 * An abstract factory for creating DELTA model objects.
 */
public interface DeltaDataSetFactory {

	/**
	 * Creates a new DeltaDataSet with the supplied name.
	 */
	public DeltaDataSet createDataSet(String name);
	
	/**
	 * Creates an item with the supplied number.
	 * @param number the number of the item.
	 * @return a new Item.
	 */
	public Item createItem(int number);
	
	/**
	 * Creates a new Character of the specified type.
	 * @param type the type of Character to create.
	 * @param number the character number.
	 * @return a new Character of the specified type.
	 */
	public Character createCharacter(CharacterType type, int number);
}

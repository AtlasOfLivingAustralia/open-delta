package au.org.ala.delta.model.observer;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;

/**
 * Contains information about a change to a DeltaDataSet.
 */
public class DeltaDataSetChangeEvent {

	public Character _character;
	
	public Item _item;
	
	public DeltaDataSet _dataSet;
	
	public DeltaDataSetChangeEvent(DeltaDataSet source) {
		this(source, null, null);
	}
	
	public DeltaDataSetChangeEvent(DeltaDataSet source, Character character, Item item) {
		_dataSet = source;
		_character = character;
		_item = item;
	}
	
	public Character getCharacter() {
		return _character;
	}
	
	public Item getItem() {
		return _item;
	}
}

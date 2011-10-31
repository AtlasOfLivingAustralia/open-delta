package au.org.ala.delta.dist;

import java.nio.ByteBuffer;
import java.util.List;

import au.org.ala.delta.dist.io.DistItemsFile;
import au.org.ala.delta.io.BinaryKeyFileEncoder;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.util.Pair;

/**
 * Reads the DIST items file and constructs a DeltaDataSet from it.
 */
public class DistItemsFileReader {
	
	private DistItemsFile _itemsFile;
	private DeltaDataSet _dataSet;
	private BinaryKeyFileEncoder _encoder;
	
	public DistItemsFileReader(DeltaDataSet dataSet, DistItemsFile itemsFile) {
		_itemsFile = itemsFile;
		_dataSet = dataSet;
		_encoder = new BinaryKeyFileEncoder();
	}
	
	public void readAll() {
		createCharacters();
		createItems();
	}
	
	private void createCharacters() {
		
		List<Integer> charTypes = _itemsFile.readCharacterTypes();
		List<Integer> states = _itemsFile.readNumbersOfStates();
		for (int i=0; i<_itemsFile.getNumberOfCharacters(); i++) {
			CharacterType type = _encoder.typeFromInt(charTypes.get(i));
			Character character = _dataSet.addCharacter(type);
			if (type.isMultistate()) {
				((MultiStateCharacter)character).setNumberOfStates(states.get(i));
			}
		}
	}
	
	private void createItems() {
		for (int i=0; i<_itemsFile.getNumberOfItems(); i++) {
			
			Item item = _dataSet.addItem();
			Pair<String, ByteBuffer> itemData = _itemsFile.readItem(i);
			
			item.setDescription(itemData.getFirst());
			
			decodeAttributes(item, itemData.getSecond());
		}
	}
	
	private void decodeAttributes(Item item, ByteBuffer attributeData) {
		
	}
	
}

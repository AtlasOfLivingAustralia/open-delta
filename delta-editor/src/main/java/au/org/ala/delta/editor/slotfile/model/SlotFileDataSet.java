package au.org.ala.delta.editor.slotfile.model;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.TextType;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOItemDesc;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;

/**
 * Implementation of a DELTA DataSet that uses the random access slotfile to read data on demand rather
 * than storing it in memory.
 */
public class SlotFileDataSet implements DeltaDataSet {

	private DeltaVOP _vop;
	private SlotFileDataSetFactory _factory;

	public SlotFileDataSet(DeltaVOP vop, SlotFileDataSetFactory factory) {
		_vop = vop;
		_factory = factory;
	}
	
	public DeltaVOP getVOP() {
		return _vop;
	}
	
	@Override
	public Item getItem(int number) {
		synchronized (_vop) {
			return _factory.createItem(number);	
		}		
	}
	
	@Override
	public Character getCharacter(int number) {
		synchronized (_vop) {
			int charId = _vop.getDeltaMaster().uniIdFromCharNo(number);	
			VOCharBaseDesc characterDesc = (VOCharBaseDesc)_vop.getDescFromId(charId);
			return _factory.createCharacter(CharacterTypeConverter.fromCharType(characterDesc.getCharType()), number);
		}
	}

	@Override
	public String getAttributeAsString(int itemNumber, int characterNumber) {
		synchronized (_vop) {
			int itemId = _vop.getDeltaMaster().uniIdFromItemNo(itemNumber);
			VOItemDesc itemDesc = (VOItemDesc) _vop.getDescFromId(itemId);
			
			int charId = _vop.getDeltaMaster().uniIdFromCharNo(characterNumber);			
			return itemDesc.readAttributeAsText(charId, TextType.UTF8, 1);
		}
		
	}
	
	@Override
	public String getName() {
		return _vop.getFilename();
	}
	
	/**
	 * Doesn't do anything - the name is always the filename of the slot file.
	 */
	@Override
	public void setName(String name) {}

	@Override
	public int getNumberOfCharacters() {
		
		synchronized (_vop) {
			if (_vop.getDeltaMaster() == null) {
				return 0;
			}
			return _vop.getDeltaMaster().getNChars();
		}
		
	}

	@Override
	public int getMaximumNumberOfItems() {
		synchronized (_vop) {
			if (_vop.getDeltaMaster() == null) {
				return 0;
			}
			return _vop.getDeltaMaster().getNItems();
		}
	}
	
	@Override
	public void close() {
		_vop.close();
	}
	
	@Override
	public Character addCharacter(CharacterType type) {
		synchronized (_vop) {
			Character character = _factory.createCharacter(type, getNumberOfCharacters()+1);
			return character;
		}
	}

	@Override
	public Item addItem() {
		synchronized (_vop) {
			Item item = addItem(getMaximumNumberOfItems()+1);
			return item;
		}
	}
	
	@Override
	public Character addCharacter(int characterNumber, CharacterType type) {
		synchronized (_vop) {
			
			return _factory.createCharacter(type, characterNumber);
		}
	}
	
	@Override
	public Item addItem(int itemNumber) {
		synchronized (_vop) {
			return _factory.createItem(itemNumber);
		}
	}
	
}

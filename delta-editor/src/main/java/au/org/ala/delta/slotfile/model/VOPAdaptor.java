package au.org.ala.delta.slotfile.model;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterFactory;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.slotfile.Attribute;
import au.org.ala.delta.slotfile.CharType;
import au.org.ala.delta.slotfile.DeltaVOP;
import au.org.ala.delta.slotfile.VOCharBaseDesc;
import au.org.ala.delta.slotfile.VOCharTextDesc;
import au.org.ala.delta.slotfile.VOItemDesc;
import au.org.ala.delta.slotfile.VOCharBaseDesc.CharTextInfo;

/**
 * Implementation of a DELTA DataSet that uses the random access slotfile to read data on demand rather
 * than storing it in memory.
 */
public class VOPAdaptor implements DeltaDataSet {

	private DeltaVOP _vop;

	public VOPAdaptor(DeltaVOP vop) {
		_vop = vop;
	}
	
	public DeltaVOP getVOP() {
		return _vop;
	}
	
	@Override
	public Item getItem(int number) {
		int itemId = _vop.getDeltaMaster().uniIdFromItemNo(number);
		VOItemDesc itemDesc = (VOItemDesc) _vop.getDescFromId(itemId);
		VOItemAdaptor adaptor = new VOItemAdaptor(itemDesc, number);
		return new Item(adaptor, number);
	}
	
	@Override
	public Character getCharacter(int number) {
		int charId = _vop.getDeltaMaster().uniIdFromCharNo(number);	
		VOCharBaseDesc characterDesc = (VOCharBaseDesc)_vop.getDescFromId(charId);
		CharTextInfo txtInfo = characterDesc.readCharTextInfo(0, (short) 0);
		VOCharTextDesc textDesc = (VOCharTextDesc) _vop.getDescFromId(txtInfo.charDesc);
		
		VOCharacterAdaptor characterAdaptor = new VOCharacterAdaptor(characterDesc, textDesc);
		au.org.ala.delta.model.Character character = CharacterFactory.newCharacter(fromCharType(characterDesc.getCharType()), number);
		character.setImpl(characterAdaptor);
		
		return character;
	}

	@Override
	public String getAttributeAsString(int itemNumber, int characterNumber) {
		int itemId = _vop.getDeltaMaster().uniIdFromItemNo(itemNumber);
		VOItemDesc itemDesc = (VOItemDesc) _vop.getDescFromId(itemId);
		
		int charId = _vop.getDeltaMaster().uniIdFromCharNo(characterNumber);			
		Attribute attr = itemDesc.readAttribute(charId);
		if (attr != null) {
			return attr.getAsText(0, _vop);
		}
		return "-";
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
		return _vop.getDeltaMaster().getNChars();
		
	}

	@Override
	public int getMaximumNumberOfItems() {
		return _vop.getDeltaMaster().getNItems();
	}
	
	@Override
	public void close() {
		_vop.close();
	}

	/**
	 * Converts a slotfile CharType int into a model class CharacterType enum.
	 * @param charType the slotfile character type.
	 * @return the appropriate matching CharacterType for the supplied char type.
	 */
	public CharacterType fromCharType(int charType) {
		switch (charType) {
		case CharType.TEXT:
			return CharacterType.Text;
		case CharType.INTEGER:
			return CharacterType.IntegerNumeric;
		case CharType.REAL:
			return CharacterType.RealNumeric;
		case CharType.ORDERED:
			return CharacterType.OrderedMultiState;
		case CharType.UNORDERED:
			return CharacterType.UnorderedMultiState;
		default:
			throw new RuntimeException("Unregognised character type: " + charType);
		}
	}
	
}

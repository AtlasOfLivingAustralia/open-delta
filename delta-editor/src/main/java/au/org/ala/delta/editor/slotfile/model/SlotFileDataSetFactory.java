package au.org.ala.delta.editor.slotfile.model;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOCharTextDesc;
import au.org.ala.delta.editor.slotfile.VOItemDesc;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc.CharTextInfo;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterFactory;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.DeltaDataSetFactory;
import au.org.ala.delta.model.Item;


/**
 * Creates instances of the DELTA model classes backed by slotfile virtual objects.
 */
public class SlotFileDataSetFactory implements DeltaDataSetFactory {

	/** The Virtual Object that represents the whole data set */
	private DeltaVOP _vop;
	
	/**
	 * Creates a new instance of the SlotFileDataSetFactory that can create instances of the model
	 * classes associated backed by the supplied VOP.
	 * @param vop the Virtual Object that represents the whole data set and provides access to the slot file.
	 */
	public SlotFileDataSetFactory(DeltaVOP vop) {
		_vop = vop;
	}
	
	/**
	 * Creates a new DeltaDataSet backed by our VOP.
	 * @param name ignored in this case as the VOP already has a file name associated with it.
	 */
	@Override
	public DeltaDataSet createDataSet(String name) {

		DeltaDataSet dataSet = new VOPAdaptor(_vop, this);
		return dataSet;
	}

	/**
	 * Creates a new Item backed by a VOItemAdaptor.
	 * @param number identifies the item. Items in a DeltaDataSet must have unique numbers.
	 */
	@Override
	public Item createItem(int number) {
		int itemId = _vop.getDeltaMaster().uniIdFromItemNo(number);
		VOItemDesc itemDesc = (VOItemDesc) _vop.getDescFromId(itemId);
		VOItemAdaptor adaptor = new VOItemAdaptor(itemDesc, number);
		return new Item(adaptor, number);
	}

	/**
	 * Creates a new Character of the specified type backed by a VOCharacterAdaptor.
	 * @param number identifies the character. Characters in a DeltaDataSet must have unique numbers.
	 */
	@Override
	public Character createCharacter(CharacterType type, int number) {
		
		Character character = CharacterFactory.newCharacter(type, number);
		
		int charId = _vop.getDeltaMaster().uniIdFromCharNo(number);	
		VOCharBaseDesc characterDesc = (VOCharBaseDesc)_vop.getDescFromId(charId);
		CharTextInfo txtInfo = characterDesc.readCharTextInfo(0, (short) 0);
		VOCharTextDesc textDesc = (VOCharTextDesc) _vop.getDescFromId(txtInfo.charDesc);
		
		VOCharacterAdaptor characterAdaptor = new VOCharacterAdaptor(characterDesc, textDesc);
		
		character.setImpl(characterAdaptor);
		
		return character;
	}
}

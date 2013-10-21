/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.editor.slotfile.model;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOCharTextDesc;
import au.org.ala.delta.editor.slotfile.VOControllingDesc;
import au.org.ala.delta.editor.slotfile.VOItemDesc;
import au.org.ala.delta.editor.slotfile.VOItemDesc.ItemFixedData;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.AttributeFactory;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.CharacterFactory;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSetFactory;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.VariantItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Creates instances of the DELTA model classes backed by slotfile virtual objects.
 */
public class SlotFileDataSetFactory implements DeltaDataSetFactory {

	/** The Virtual Object that represents the whole data set */
	private DeltaVOP _vop;
	
	
	/**
	 * Creates a new instance of the SlotFileDataSetFactory without an existing DeltaVOP.
	 * A SlotFileDataSetFactory created in this way will create and initialise a new DeltaVOP.
	 */
	public SlotFileDataSetFactory() {
		_vop =  new DeltaVOP();
	}
	
	/**
	 * Creates a new instance of the SlotFileDataSetFactory that can create instances of the model
	 * classes associated backed by the supplied VOP.
	 * @param vop the Virtual Object that represents the whole data set and provides access to the slot file.
	 */
	public SlotFileDataSetFactory(DeltaVOP vop) {
		_vop = vop;
	}
	
	public DeltaVOP getVOP() {
		return _vop;
	}
	
	/**
	 * Creates a new DeltaDataSet backed by our VOP.
	 * @param name ignored in this case as the VOP already has a file name associated with it.
	 */
	@Override
	public MutableDeltaDataSet createDataSet(String name) {

		MutableDeltaDataSet dataSet = new SlotFileDataSet(_vop, this);
		return dataSet;
	}

	/**
	 * Creates a new Item backed by a VOItemAdaptor.
	 * @param number identifies the item. Items in a DeltaDataSet must have unique numbers.
	 */
	@Override
	public Item createItem(int number) {		
		VOItemAdaptor adaptor = createSlotFileItem(number);
		return new Item(adaptor);
	}

	private VOItemAdaptor createSlotFileItem(int number) {
		VOItemDesc itemDesc = null;
		
		VOItemDesc.ItemFixedData itemFixedData = new VOItemDesc.ItemFixedData();
		itemDesc = (VOItemDesc)_vop.insertObject(itemFixedData, ItemFixedData.SIZE, null, 0, 100);
		int itemId = itemDesc.getUniId();
		_vop.getDeltaMaster().insertItem(itemId, number);

		VOItemAdaptor adaptor = new VOItemAdaptor(_vop, itemDesc);
		return adaptor;
	}
	
	@Override
	public Item createVariantItem(Item parent, int number) {
		VOItemAdaptor adaptor = createSlotFileItem(number);
		return new VariantItem(parent, adaptor);
	}

	/**
	 * Creates a new Character of the specified type backed by a new VOCharBaseDesc in the
	 * SlotFile.
	 * 
	 * @param type the type of character to create.
	 * @param number identifies the character. Characters in a DeltaDataSet must have unique numbers.
	 */
	@Override
	public Character createCharacter(CharacterType type, int number) {
		
		VOCharBaseDesc characterDesc = null;
		
		characterDesc = newVOCharDesc(type, number);
		
		return wrapCharacter(characterDesc, number);
	}
	
	
	private VOCharBaseDesc newVOCharDesc(CharacterType type, int characterNumber) {
		VOCharBaseDesc.CharBaseFixedData characterFixedData = new VOCharBaseDesc.CharBaseFixedData();
		VOCharBaseDesc characterBase = (VOCharBaseDesc)_vop.insertObject(characterFixedData, VOCharBaseDesc.CharBaseFixedData.SIZE, null, 0, 0);
		int charId = characterBase.getUniId();
		_vop.getDeltaMaster().insertCharacter(charId, characterNumber);
		
		characterBase.setCharType((short)CharacterTypeConverter.toCharType(type));
		
		return characterBase;
	}
	
	/**
	 * Creates an instance of the appropriate model Character class that delegates to the 
	 * supplied VOCharBaseDesc.
	 * @param characterDesc the slot file character descriptor to wrap.
	 * @param number the character number of the new character.
	 * @return a new Character that delegates to the supplied VOCharBaseDesc.
	 */
	public Character wrapCharacter(VOCharBaseDesc characterDesc, int number) {		
		CharacterType type = CharacterTypeConverter.fromCharType(characterDesc.getCharType());
		VOCharTextDesc textDesc = characterDesc.readCharTextInfo(0, (short) 0);
		Character character = CharacterFactory.newCharacter(type, new VOCharacterAdaptor(_vop, characterDesc, textDesc));
		return character;
	}


    @Override
    public Attribute createAttribute(Character character, Item item) {
        
        VOCharacterAdaptor characterAdaptor = (VOCharacterAdaptor) character.getImpl();
        VOItemAdaptor itemAdaptor = (VOItemAdaptor) item.getItemData();
        
        VOAttributeAdaptor attrAdaptor = new VOAttributeAdaptor(itemAdaptor.getItemDesc(), characterAdaptor.getCharBaseDesc());
        
        Attribute attribute = AttributeFactory.newAttribute(character, attrAdaptor);
        attribute.setItem(item);
        return attribute;
    }

	@Override
	public CharacterDependency createCharacterDependency(
			MultiStateCharacter owningCharacter, Set<Integer> states,
			Set<Integer> dependentCharacters) {
		
		VOControllingDesc.ControllingFixedData controllingData = new VOControllingDesc.ControllingFixedData();
		VOControllingDesc controllingDesc = (VOControllingDesc)getVOP().insertObject(controllingData, VOControllingDesc.ControllingFixedData.SIZE, null, 0, 0);
		
		int charId = getVOP().getDeltaMaster().uniIdFromCharNo(owningCharacter.getCharacterId());
		VOCharBaseDesc charDesc = (VOCharBaseDesc)getVOP().getDescFromId(charId);
		
		List<Integer> stateIds = new ArrayList<Integer>(states.size());
		for (int stateNum : states) {
			stateIds.add(charDesc.uniIdFromStateNo(stateNum));
		}
		
		
		
		controllingDesc.setControllingInfo(charId, stateIds, "");
		charDesc.addDependentContAttr(controllingDesc.getUniId());
        _vop.getDeltaMaster().insertContAttr(controllingDesc.getUniId(), 0);

		CharacterDependency charDependency = new CharacterDependency(new VOControllingAdapter(getVOP(), controllingDesc));
		
		return charDependency;
		
	}
    
    
}

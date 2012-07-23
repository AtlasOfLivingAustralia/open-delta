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
package au.org.ala.delta.model;

import java.io.File;
import java.util.Set;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.ConforDirectiveFileParser;
import au.org.ala.delta.model.impl.DefaultAttributeData;
import au.org.ala.delta.model.impl.DefaultCharacterData;
import au.org.ala.delta.model.impl.DefaultCharacterDependencyData;
import au.org.ala.delta.model.impl.DefaultDataSet;
import au.org.ala.delta.model.impl.DefaultItemData;
import au.org.ala.delta.model.impl.ItemData;


/**
 * Creates DeltaDataSets backed by in-memory model objects.
 */
public class DefaultDataSetFactory implements DeltaDataSetFactory {

	public static MutableDeltaDataSet load(File file) throws Exception {
		DeltaContext context = new DeltaContext();
		ConforDirectiveFileParser parser = ConforDirectiveFileParser.createInstance();
		parser.parse(file, context);
		return context.getDataSet();
	}
	
	@Override
	public MutableDeltaDataSet createDataSet(String name) {
		return new DefaultDataSet(this);
	}

	@Override
	public Item createItem(int number) {
		ItemData defaultData = new DefaultItemData(number);
		Item item = new Item(defaultData);
		
		return item;
	}
	
	@Override
	public Item createVariantItem(Item parent, int itemNumber) {
		ItemData defaultData = new DefaultItemData(itemNumber);
		Item item = new VariantItem(parent, defaultData);
		
		return item;
	}

	@Override
	public Character createCharacter(CharacterType type, int number) {
		Character character = CharacterFactory.newCharacter(type, new DefaultCharacterData(number));		
		return character;
	}

    @Override
    public Attribute createAttribute(Character character, Item item) {
        Attribute attribute = AttributeFactory.newAttribute(character, new DefaultAttributeData(character));
        attribute.setItem(item);
        return attribute;
    }

	@Override
	public CharacterDependency createCharacterDependency(
			MultiStateCharacter owningCharacter, Set<Integer> states,
			Set<Integer> dependentCharacters) {

		DefaultCharacterDependencyData impl = new DefaultCharacterDependencyData(
				owningCharacter.getCharacterId(), states, dependentCharacters);
		
		return new CharacterDependency(impl);
	}
    
    
    

}

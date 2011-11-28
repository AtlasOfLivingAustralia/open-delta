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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOControllingDesc;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.impl.CharacterDependencyData;

/**
 * Implements the CharacterDependencyData interface using the VOControllingDesc
 * read / write methods.
 */
public class VOControllingAdapter implements CharacterDependencyData {

	private VOControllingDesc _controllingDesc;
	private DeltaVOP _vop;
	
	public VOControllingAdapter(DeltaVOP vop, VOControllingDesc controllingDesc) {
		_vop = vop;
		_controllingDesc = controllingDesc;
	}
	
	public int getId() {
		synchronized (_vop) {
			return _controllingDesc.getUniId();
		}
	}
	
	@Override
	public void addDependentCharacter(Character character) {
		synchronized (_vop) {
			int charId = getCharacterId(character);
			_controllingDesc.addControlledChar(charId);
		}
	}

	@Override
	public void removeDependentCharacter(Character character) {
		synchronized (_vop) {
			int charId = getCharacterId(character);
			_controllingDesc.removeControlledChar(charId);
		}
	}

	@Override
	public void setDescription(String description) {
		synchronized (_vop) {
			_controllingDesc.writeLabel(description);
		}
	}

	@Override
	public String getDescription() {
		synchronized (_vop) {
			return _controllingDesc.readLabel();
		}
	}

	@Override
	public Set<Integer> getStates() {
		synchronized (_vop) {
			List<Integer> stateIds = _controllingDesc.readStateIds();
			Set<Integer> stateNumbers = new HashSet<Integer>(stateIds.size());
			VOCharBaseDesc charBase = charBaseForId(_controllingDesc.getCharId());
			for (int id : stateIds) {
				stateNumbers.add(charBase.stateNoFromUniId(id));
			}
			
			return stateNumbers;
		}
	}
	
	@Override
	public void setStates(Set<Integer> states) {
		synchronized (_vop) {
			List<Integer> stateIds = new ArrayList<Integer>();
			VOCharBaseDesc charBase = charBaseForId(_controllingDesc.getCharId());
			for (int stateNumber : states) {
				stateIds.add(charBase.uniIdFromStateNo(stateNumber));
			}
			Collections.sort(stateIds);
			
			_controllingDesc.writeStateIds(stateIds);
		}
	}
	
	@Override
	public void addState(int state) {
		synchronized (_vop) {
			List<Integer> states = _controllingDesc.readStateIds();
			if (!states.contains(state)) {
				states.add(state);
				_controllingDesc.writeStateIds(states);
			}
		}
	}

	@Override
	public Set<Integer> getDependentCharacterIds() {
		synchronized (_vop) {
			List<Integer> charIds = _controllingDesc.readControlledChars();
			Set<Integer> characterNumbers = new HashSet<Integer>(charIds.size());
			
			for (int id : charIds) {
				characterNumbers.add(_vop.getDeltaMaster().charNoFromUniId(id));
			}
			
			return characterNumbers;
		}
	}

	public int getControllingCharacterId() {
		synchronized (_vop) {
			int charId =_controllingDesc.getCharId();
			return _vop.getDeltaMaster().charNoFromUniId(charId);
		}
	}
	
	private VOCharBaseDesc charBaseForId(int id) {
		synchronized (_vop) {
			return (VOCharBaseDesc)_vop.getDescFromId(id);
		}
	}
	
	private int getCharacterId(Character character) {
		synchronized (_vop) {
			return _vop.getDeltaMaster().uniIdFromCharNo(character.getCharacterId());
		}
	}

	/**
	 * Returns the slot file unique id of the VOControlingDesc that is backing
	 * the supplied CharacterDependency object.
	 * @param dependency the object to get the id of.
	 * @return the unique id of the backing VOControllingDesc.
	 */
	public static int getId(CharacterDependency dependency) {
		VOControllingAdapter impl = (VOControllingAdapter)dependency.getImpl();
		return impl.getId();
	}
}

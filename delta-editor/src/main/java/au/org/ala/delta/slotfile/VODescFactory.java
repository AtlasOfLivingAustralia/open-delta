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
package au.org.ala.delta.slotfile;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import au.org.ala.delta.Logger;

public class VODescFactory {
	
	public static final int VOAnyDesc_TypeId = VOAnyDesc.VOTID_USER_BASE;
	public static final int VONoteDesc_TypeId = VOAnyDesc_TypeId + 1;
	public static final int VODeltaMasterDesc_TypeId = VOAnyDesc.VOTID_DELTA_BASE;
	public static final int VOControllingDesc_TypeId = VODeltaMasterDesc_TypeId + 1;
	public static final int VOCharBaseDesc_TypeId = VOControllingDesc_TypeId + 1;
	public static final int VOCharTextDesc_TypeId = VOCharBaseDesc_TypeId + 1;
	public static final int VOItemDesc_TypeId = VOCharTextDesc_TypeId + 1;
	public static final int VODirFileDesc_TypeId = VOItemDesc_TypeId + 1;
	public static final int VOImageDesc_TypeId = VODirFileDesc_TypeId + 1;
	public static final int VOImageInfoDesc_TypeId = VOImageDesc_TypeId + 1;
	
	private static HashMap<Integer, Class<? extends VOAnyDesc>> _map = new HashMap<Integer, Class<? extends VOAnyDesc>>();
	
	static {
		_map.put(VOAnyDesc_TypeId, VOAnyDesc.class);
		_map.put(VONoteDesc_TypeId, VONoteDesc.class);
		_map.put(VODeltaMasterDesc_TypeId, VODeltaMasterDesc.class);		
		_map.put(VOControllingDesc_TypeId, VOControllingDesc.class);
		_map.put(VOCharBaseDesc_TypeId, VOCharBaseDesc.class);
		_map.put(VOCharTextDesc_TypeId, VOCharTextDesc.class);
		_map.put(VOItemDesc_TypeId, VOItemDesc.class);
		_map.put(VODirFileDesc_TypeId, VODirFileDesc.class);
		_map.put(VOImageDesc_TypeId, VOImageDesc.class);
		_map.put(VOImageInfoDesc_TypeId, VOImageInfoDesc.class);		
	}
		
	public static VOAnyDesc getDescFromTypeId(int typeid, SlotFile slotFile, VOP vop) {
		if (_map.containsKey(typeid)) {
			Class<? extends VOAnyDesc> c = _map.get(typeid);
			try {
				Constructor<? extends VOAnyDesc> ctor = c.getConstructor(SlotFile.class, VOP.class);
				return (VOAnyDesc) ctor.newInstance(slotFile, vop);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		} else {
			throw new RuntimeException("Unrecognized VO Type: " + typeid);
		}
	}

}

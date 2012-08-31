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
package au.org.ala.delta.delfor.format;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;

/**
 * The StateReorderer class is responsible for reordering the states of
 * a single Character.
 */
public class StateReorderer implements FormattingAction {

	private List<Integer> _newOrder;
	private int _charNumber;
	
	public StateReorderer(int charNumber, List<Integer> newOrder) {
		_newOrder = newOrder;
		_charNumber = charNumber;
	}
	
	@Override
	public void format(DelforContext context, SlotFileDataSet dataSet) {
		
		DeltaVOP vop = dataSet.getVOP();
		VOCharBaseDesc character = (VOCharBaseDesc)vop.getDescFromId(vop.getDeltaMaster().uniIdFromCharNo(_charNumber));
		// The reordering is much easier if we do it by id, as once we 
		// move the first state the rest are renumbered.
		List<Integer> ids = new ArrayList<Integer>();
		for (int i=1; i<=character.getNStatesUsed(); i++) {
			ids.add(character.uniIdFromStateNo(i));
		}
		
		for (int i=0; i<_newOrder.size(); i++) {
			
			int toMove = _newOrder.get(i);
			int toPosition = i + 1;
			
			int stateId = ids.get(toMove-1);	
			int number = character.stateNoFromUniId(stateId);
			
			character.moveState(number, toPosition);
		}
	}

}

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
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.model.Character;

public class CharacterReorderer implements FormattingAction {

	private List<Integer> _newOrder;
	
	public CharacterReorderer(List<Integer> newOrder) {
		_newOrder = newOrder;
	}
	
	
	@Override
	public void format(DelforContext context, SlotFileDataSet dataSet) {
		
		DeltaVOP vop = dataSet.getVOP();
		
		// The reordering is much easier if we do it by id, as once we 
		// move the first character the rest are renumbered.
		List<Integer> ids = new ArrayList<Integer>();
		for (int i=1; i<=dataSet.getNumberOfCharacters(); i++) {
			ids.add(vop.getDeltaMaster().uniIdFromCharNo(i));
		}
		
		for (int i=0; i<_newOrder.size(); i++) {
			
			int toMove = _newOrder.get(i);
			int toPosition = i + 1;
			
			int charId = ids.get(toMove-1);
			int charNumToMove = vop.getDeltaMaster().charNoFromUniId(charId);
			Character character = dataSet.getCharacter(charNumToMove);
			dataSet.moveCharacter(character, toPosition);
		}
	}

}

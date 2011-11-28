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
import java.util.Collections;
import java.util.List;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;


/**
 * The CharacterExcluder will delete any Characters that have been excluded by 
 * an INCLUDE CHARACTERS or EXCLUDE CHARACTERS directive.
 */
public class CharacterExcluder implements FormattingAction {

	private List<Integer> _toExclude;
	
	public CharacterExcluder(List<Integer> toExclude) {
		_toExclude = new ArrayList<Integer>(toExclude);
	}
	@Override
	public void format(DelforContext context, SlotFileDataSet dataSet) {
		
		Collections.sort(_toExclude);
		// Delete characters in reverse order so the reordering will not
		// change the numbers of the characters we are deleting.
		for (int i=_toExclude.size()-1; i>=0; i--) {
			dataSet.deleteCharacter(dataSet.getCharacter(_toExclude.get(i)));
		}
	}

}

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

public class StateValueMatrix {

	private int _characterCount;
	private int _itemCount;
	private StateValue _matrix[][];

	public StateValueMatrix(int characterCount, int itemCount) {
		_characterCount = characterCount;
		_itemCount = itemCount;
		_matrix = new StateValue[characterCount][itemCount];
	}

	public void setValue(int charIndx, int itemIndex, StateValue value) {
		_matrix[charIndx - 1][itemIndex - 1] = value;
	}

	public StateValue getValue(int charIndex, int itemIndex) {
		return _matrix[charIndex - 1][itemIndex - 1];
	}
	
	public int getCharacterCount() {
		return _characterCount;
	}
	
	public int getItemCount() {
		return _itemCount;
	}

}

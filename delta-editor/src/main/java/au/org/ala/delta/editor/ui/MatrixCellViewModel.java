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
package au.org.ala.delta.editor.ui;

public class MatrixCellViewModel {

	private String _text;
	private boolean _implicit;
	private boolean _inapplicable;
	private boolean _uncodedMandatory;

	public MatrixCellViewModel() {
	}

	public MatrixCellViewModel(String text) {
		_text = text;
	}

	public MatrixCellViewModel(String text, boolean implicit) {
		_text = text;
		_implicit = implicit;
	}

	public void setImplicit(boolean implicit) {
		_implicit = implicit;
	}

	public boolean isImplicit() {
		return _implicit;
	}

	public String getText() {
		return _text;
	}

	public void setText(String text) {
		_text = text;
	}

	public void setInapplicable(boolean inapplicable) {
		_inapplicable = inapplicable;
	}

	public boolean isInapplicable() {
		return _inapplicable;
	}
	
	public void setUncodedMandatory(boolean uncodedMandatory) {
		_uncodedMandatory = uncodedMandatory;
	}
	
	public boolean isUncodedMandatory() {
		return _uncodedMandatory;
	}

	@Override
	public String toString() {
		return String.format("%s [Implicit: %b]", _text, _implicit);
	}

}

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

import javax.swing.table.AbstractTableModel;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;

public class TableHeaderModel extends AbstractTableModel {

	private static final long serialVersionUID = 777956252484757022L;

	private EditorViewModel _model;
	private CharacterFormatter _formatter;
	
	public TableHeaderModel(EditorViewModel model) {
		_model = model;
		_formatter = new CharacterFormatter(false, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, true);
	}
	
	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public int getColumnCount() {
		return _model.getNumberOfCharacters();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		au.org.ala.delta.model.Character ch = _model.getCharacter(columnIndex + 1);
		return _formatter.formatCharacterDescription(ch);
	}

}

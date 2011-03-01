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
package au.org.ala.delta;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;

public class MatrixTableModel implements TableModel {

	private DeltaDataSet _dataSet;

	public MatrixTableModel(DeltaDataSet dataSet) {
		_dataSet = dataSet;
	}
		
	@Override
	public int getColumnCount() {
		return _dataSet.getNumberOfCharacters();
	}

	@Override
	public int getRowCount() {
		return _dataSet.getMaximumNumberOfItems();
	}

	@Override
	public String getColumnName(int column) {
		return (column+1)+ ". "+ _dataSet.getCharacter(column + 1).getDescription();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Character.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String attributeValue = _dataSet.getAttributeAsString(rowIndex+1, columnIndex+1);
		if (attributeValue == null) {
			attributeValue = "-";
		}
		return attributeValue;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}

}

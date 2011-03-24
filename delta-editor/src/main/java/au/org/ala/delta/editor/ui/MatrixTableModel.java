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

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.rtf.RTFUtils;

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
		Character ch = _dataSet.getCharacter(column + 1);
		return RTFUtils.stripFormatting(ch.getDescription());
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Character.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		Character character = _dataSet.getCharacter(columnIndex+1);
		if (character instanceof TextCharacter) {
			return true;
		}
		Attribute attr = _dataSet.getItem(rowIndex+1).getAttribute(character); 
		return attr == null || attr.isSimple();
	}
	
	public Integer getImplicitStateNo(int rowIndex, int columnIndex) {
		Character character = _dataSet.getCharacter(columnIndex+1);				
		if (character instanceof MultiStateCharacter) {
			MultiStateCharacter msc = (MultiStateCharacter) character;					
			Attribute attr = _dataSet.getItem(rowIndex + 1).getAttribute(character);
			if (attr == null) {
				int implicit = msc.getUncodedImplicitState();
				if (implicit > 0) {
					return implicit;
				}
			}
		}
		
		return null;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String tmp = _dataSet.getAttributeAsString(rowIndex+1, columnIndex+1);
		MatrixCellViewModel vm = new MatrixCellViewModel();
		if (StringUtils.isEmpty(tmp)) {
			Integer implicit = getImplicitStateNo(rowIndex, columnIndex);
			if (implicit != null) {
				vm.setText(String.format("%d", implicit));
				vm.setImplicit(true);			
			} else {
				vm.setText("");
			}						
		} else {
			vm.setText(RTFUtils.stripFormatting(tmp));
		}
		
		return vm;
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

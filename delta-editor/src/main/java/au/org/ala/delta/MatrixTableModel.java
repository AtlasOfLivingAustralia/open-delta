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
import au.org.ala.delta.slotfile.Attribute;
import au.org.ala.delta.slotfile.VOItemDesc;

public class MatrixTableModel implements TableModel {

	private DeltaContext _context;

	public MatrixTableModel(DeltaContext context) {
		_context = context;
	}
		

	@Override
	public int getColumnCount() {
		return _context.getNumberOfCharacters();
	}

	@Override
	public int getRowCount() {
		return _context.getMaximumNumberOfItems();
	}

	@Override
	public String getColumnName(int column) {
		return _context.getCharacter(column + 1).getDescription();
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
		
		if (_context.VOP != null) {
			int itemId = _context.VOP.getDeltaMaster().uniIdFromItemNo(rowIndex + 1);
			VOItemDesc itemDesc = (VOItemDesc) _context.VOP.getDescFromId(itemId);
			int charId = _context.VOP.getDeltaMaster().uniIdFromCharNo(columnIndex);			
			Attribute attr = itemDesc.readAttribute(charId);
			if (attr != null) {
				return attr.getAsText(0, _context.VOP);
			}
			return "-";
		}
		
		if (_context != null) {
			return _context.getMatrix().getValue(columnIndex + 1, rowIndex + 1);
		}
		
		return "X";
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

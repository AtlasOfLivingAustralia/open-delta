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
package au.org.ala.delta.editor.ui.dnd;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import au.org.ala.delta.editor.ui.DropIndicationTableHeader;

/**
 * The DropIndicationTable is able to provide an indication of the of the drop location of a
 * row or column being dragged, despite the drag and drop operation occurring on a different
 * table instance.  In the DELTA case, the drag and drop operation occurs on the row header but
 * is simulated on the main content table to provide a better visual indication of the drop 
 * location to the user.
 */
public class DropIndicationTable extends JTable {

	private static final long serialVersionUID = -3467217705935460965L;
	
	/** The row the drop will occur at */
	private int _dropRow = -1;
	
	/** The column the drop will occur at */
	private int _dropColumn = -1;
	
	/** The index of the drop row the last time we painted a drop location */
	public int _lastPaintedRow = -1;
	
	/** The index of the drop column the last time we painted a drop location */
	public int _lastPaintedColumn = -1;
	
	/** The JTable that the drag and drop operation is actually occurring on */
	private JTable _dropEventSource;
	
	/**
	 * Creates a new DropIndicationTable with the supplied model using the supplied JTable as
	 * the source of drop events to respond to.
	 * @param model the model for this table.
	 * @param dropEventSource this table will react to drop target events from this table.
	 */
	public DropIndicationTable(TableModel model, JTable dropEventSource) {
		super();
		DropIndicationTableHeader header = new DropIndicationTableHeader(columnModel);
		setTableHeader(header);
		header.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("dropColumn".equals(evt.getPropertyName())) {
					updateColumnDropIndication();
				}
			}
		});
		setModel(model);
		_dropEventSource = dropEventSource;
		_dropEventSource.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("dropLocation".equals(evt.getPropertyName())) {
					updateRowDropIndication();
				}
			}
		});
	}
	
	
	/**
	 * Overrides paintComponent to paint the drop location indication if necessary.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		// Draw drop lines.
		if (_dropRow >= 0) {
			
			Rectangle rect = getCellRect(_dropRow, 0, true);
			if (g.getClip().intersects(rect.x, rect.y-2, getWidth(), 3)) {
				g.fillRect(rect.x, rect.y-2, getWidth(), 3);
				_lastPaintedRow = _dropRow;
			}
		}
		if (_dropColumn >= 0) {
			Rectangle rect = getCellRect(0, _dropColumn, true);
			if (g.getClip().intersects(rect.x, rect.y-2, 3, getHeight())) {
				g.fillRect(rect.x, rect.y-2, 3, getHeight());
				_lastPaintedColumn = _dropColumn;
			}
		}
	}
	
	private void updateRowDropIndication() {
		JTable.DropLocation dl = _dropEventSource.getDropLocation();
		// The drop location will be null if the drag was initiated by the column
		// header but dragged over the row header.
		if (dl != null) {
			_dropRow = dl.getRow();
			paintImmediately(getRowDropIndicationBounds());
		}
		else {
			_dropRow = -1;
			repaint();
		}
	}
	
	/**
	 * Works out the bounds in which our customized painting should occur.
	 * @return a rectangle containing the clip bounds for our paint.
	 */
	private Rectangle getRowDropIndicationBounds() {
		
		Rectangle previousRec = getCellRect(_lastPaintedRow, 0, true);
		Rectangle rect = getCellRect(_dropRow, 0, true);
		
		int minY = Math.min(previousRec.y, rect.y);
		int maxY = Math.max(previousRec.y, rect.y);
		
		int height = maxY-minY+3;
		
		return new Rectangle(rect.x, minY-2, getWidth(), height); 
	}
	
	private void updateColumnDropIndication() {
		_dropColumn = getHeader().getDropColumn();
		if (_dropColumn != -1) {
			repaint(getColumnDropIndicationBounds());
		}
		else {
			repaint();
		}
	}
	
	/**
	 * Works out the bounds in which our customized column painting should occur.
	 * @return a rectangle containing the clip bounds for our paint.
	 */
	private Rectangle getColumnDropIndicationBounds() {
		
		Rectangle previousRec = getCellRect(0, _lastPaintedColumn, true);
		Rectangle rect = getCellRect(0, _dropColumn, true);
		
		int minX = Math.min(previousRec.x, rect.x);
		int maxX = Math.max(previousRec.x, rect.x);
		
		int width = maxX-minX+3;
		
		return new Rectangle(minX-1, rect.y, width, getHeight()); 
	}
	
	
	private DropIndicationTableHeader getHeader() {
		return (DropIndicationTableHeader)getTableHeader();
	}

}

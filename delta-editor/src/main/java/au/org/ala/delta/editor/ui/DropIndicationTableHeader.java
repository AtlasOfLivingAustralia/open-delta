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

import javax.swing.*;
import javax.swing.TransferHandler.DropLocation;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TooManyListenersException;

/**
 * Overrides JTableHeader to provide column reordering via drag and drop in 
 * a manner visually consistent with the row reordering. (unlike the default 
 * behaviour).  It also provides a visual indication of column selection which is 
 * synchronized with the table selection.
 */
public class DropIndicationTableHeader extends JTableHeader
implements DragGestureListener, ListSelectionListener, ReorderableList {

	private static final long serialVersionUID = 6903527328137944112L;

	private static final String SELECTION_ACTION_NAME = "selectionAction";
	
	private boolean _dragEnabled;
	private int _selectedColumn;
	private int _dropLocation;
	
	/** Updates the drop location during a drag and drop operation */
	private DropWatcher _dropWatcher;
	
	public DropIndicationTableHeader(TableColumnModel model) {
		super(model);
		_dragEnabled = true;
		_selectedColumn = -1;
		_dropLocation = -1;
		_dropWatcher = new DropWatcher();
		
		DragSource dragSource = DragSource.getDefaultDragSource();
         
        dragSource.createDefaultDragGestureRecognizer(this,DnDConstants.ACTION_COPY_OR_MOVE,this);
         
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int selection = columnAtPoint(e.getPoint());
				// Updating the table selection will cause our selection to be updated also via
				// the selection listener we installed.
				updateTableColumnSelection(selection);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					
					Action action = getActionMap().get(SELECTION_ACTION_NAME);
		
					if (action != null) {
						ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
						action.actionPerformed(event);
					}
				}
			}
		});
		
	}
	
	private void updateTableColumnSelection(int column) {
		MatrixTableModel model = (MatrixTableModel) getTable().getModel();
		if (model.getDataSet().getNumberOfCharacters() == 0) {
			return;
		}
		
		getTable().getColumnModel().getSelectionModel().setSelectionInterval(column, column);
	}
	
	/**
	 * Overrides paintComponent to paint the current selection and drop location
	 * indication.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		if (_dropLocation >=0) {
			Rectangle r = getHeaderRect(_dropLocation);
			g.setColor(Color.BLACK);
			g.fillRect(r.x, r.y, 3, r.height);
			
		}
		if (_selectedColumn >= 0) {
			Rectangle r = getHeaderRect(_selectedColumn);
			g.setColor(SystemColor.controlShadow);
			g.drawRect(r.x, r.y, r.width-1, getParent().getHeight()-2);
		}
	}
	
	/**
	 * Overrides setTable to add a column selection listener.
	 */
	@Override
	public void setTable(JTable table) {
		super.setTable(table);
		table.getColumnModel().getSelectionModel().addListSelectionListener(this);
	}

	/**
	 * Enables or disables drag indication.  This is so prevent a drag and drop operation
	 * being initiated during a table header resize.
	 * @param dragEnabled true to enable drag and drop, false to disable it.
	 */
	public void setDragEnabled(boolean dragEnabled) {
		_dragEnabled = dragEnabled;
	}
	
	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		if (_dragEnabled && getResizingColumn() == null) {
			getTransferHandler().exportAsDrag(DropIndicationTableHeader.this, dge.getTriggerEvent(), TransferHandler.COPY);
		}
	}
	
	/**
	 * Updates the drop location.
	 * @param column the column in which the drop will occur, -1 if no drag and 
	 * drop operation is in progress.
	 */
	public void setDropColumn(int column) {
		if (column != _dropLocation) {
			int oldLocation = _dropLocation;
			_dropLocation = column;
			firePropertyChange("dropColumn", oldLocation, column);
			repaint();
		}
	}
	
	public int getDropColumn() {
		return _dropLocation;
	}

	/**
	 * Adds a drop target listener to the supplied DropTarget so we can track
	 * the drop location.
	 */
	@Override
	public synchronized void setDropTarget(DropTarget dt) {
		
		DropTarget dropTarget = getDropTarget();
		if (dropTarget != null) {
			dropTarget.removeDropTargetListener(_dropWatcher);
		}
		super.setDropTarget(dt);

		if (dt != null) {
			try {
			    dt.addDropTargetListener(_dropWatcher);
			
			}catch (TooManyListenersException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Keeps the column selection in sync with the selected column in the
	 * table.
	 * @param e the event raised by the table selection model on table selection.
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		setSelectedColumn(getTable().getSelectedColumn());
	}
	
	/**
	 * Updates the current column selection.
	 * @param column the new selection.
	 */
	public void setSelectedColumn(int column) {
		int oldSelection = _selectedColumn;
		_selectedColumn = column;
		repaint(getHeaderRect(_selectedColumn));
		repaint(getHeaderRect(oldSelection));
		
	}
	
	@Override
	public int getSelectedIndex() {
		return _selectedColumn;
	}
	
	@Override
	public void setSelectedIndex(int index) {
		updateTableColumnSelection(index);
	}

	@Override
	public int getDropLocationIndex(DropLocation dropLocation) {
		return _dropLocation;
	}

    @Override
    public JComponent getListViewComponent() {
        return this;
    }

	/**
	 * Registers the action to take when a selection (double click or Enter key) has been made on
	 * this list.
	 * @param action the action that will be invoked on selection.
	 */
	public void setSelectionAction(Action action) {
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECTION_ACTION_NAME);
		getActionMap().put(SELECTION_ACTION_NAME, action);
	}

	private void scrollTo(int column) {
		getTable().scrollRectToVisible(getTable().getCellRect(0, column, true));
	}
	
	class DropWatcher extends DropTargetAdapter {
		
		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			Point p = dtde.getLocation();
			int column = columnAtPoint(dtde.getLocation());
			Rectangle rect = getHeaderRect(column);
			if (p.x > rect.x + rect.width/2) {
				setDropColumn(column+1);
			}
			else {
				setDropColumn(column);
			}
			p = SwingUtilities.convertPoint(DropIndicationTableHeader.this, p, getParent());
			if (p.x > getParent().getWidth()-5) {
				scrollTo(column+1);
			}
			else if (p.x < 5) {
				scrollTo(column-1);
			}
		}
		
		@Override
		public void drop(DropTargetDropEvent dtde) {
			setDropColumn(-1);
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			setDropColumn(-1);
		}
	}
}

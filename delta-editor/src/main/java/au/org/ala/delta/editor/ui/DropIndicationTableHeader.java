package au.org.ala.delta.editor.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TooManyListenersException;

import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * Overrides JTableHeader to provide column reordering via drag and drop in 
 * a manner visually consistent with the row reordering. (unlike the default 
 * behaviour).  It also provides a visual indication of column selection which is 
 * synchronized with the table selection.
 */
public class DropIndicationTableHeader extends JTableHeader
implements DragGestureListener, ListSelectionListener, ReorderableList<au.org.ala.delta.model.Character> {

	private static final long serialVersionUID = 6903527328137944112L;

	private boolean _dragEnabled = true;
	private int _selectedColumn = -1;
	private int _dropLocation = -1;
	
	/** Updates the drop location during a drag and drop operation */
	private DropWatcher _dropWatcher;
	
	public DropIndicationTableHeader(TableColumnModel model) {
		super(model);
		
		
		DragSource dragSource = DragSource.getDefaultDragSource();
         
        dragSource.createDefaultDragGestureRecognizer(this,DnDConstants.ACTION_COPY_OR_MOVE,this);
         
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selection = columnAtPoint(e.getPoint());
				// Updating the table selection will cause our selection to be updated also via
				// the selection listener we installed.
				updateTableColumnSelection(selection);
			}
		});
		
	}
	
	private void updateTableColumnSelection(int column) {
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
		if (_dragEnabled) {
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
	public int getDropLocationIndex() {
		return _dropLocation;
	}

	@Override
	public void setSelectionAction(Action action) { }


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

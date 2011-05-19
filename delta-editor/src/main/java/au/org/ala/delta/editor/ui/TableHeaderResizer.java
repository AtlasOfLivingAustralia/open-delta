package au.org.ala.delta.editor.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.JTableHeader;

public class TableHeaderResizer extends MouseInputAdapter {

	public static Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);

	private int mouseYOffset;
	boolean resizing = false;
	private Cursor otherCursor = resizeCursor;
	private DropIndicationTableHeader _fixedColumnsTable;
	private JTable _mainTable;
	private JScrollPane _scrollPane;
	private JScrollPane _fixedScrollPane;

	public TableHeaderResizer(DropIndicationTableHeader fixedTable, JTable mainTable, JScrollPane scrollPane, JScrollPane fixedScrollPane) {
		this._fixedColumnsTable = fixedTable;
		_fixedColumnsTable.addMouseListener(this);
		_fixedColumnsTable.addMouseMotionListener(this);
		_mainTable = mainTable;
		_scrollPane = scrollPane;
		_fixedScrollPane = fixedScrollPane;
	}


	public void mousePressed(MouseEvent e) {
		if (inResizeZone(e)) {
			resizing = true;
			_fixedColumnsTable.setDragEnabled(false);
			Point p = e.getPoint();
			mouseYOffset = p.y - _fixedColumnsTable.getPreferredSize().height;
		}
		
	}

	private void swapCursor() {
		Cursor tmp = _fixedColumnsTable.getCursor();
		_fixedColumnsTable.setCursor(otherCursor);
		otherCursor = tmp;
	}

	public void mouseMoved(MouseEvent e) {
		
		if (inResizeZone(e)) {
			if (!(_fixedColumnsTable.getCursor() == resizeCursor)) {
				swapCursor();
				_fixedColumnsTable.setDragEnabled(false);
			}
		}
		else {
			if (_fixedColumnsTable.getCursor() == resizeCursor) {
				swapCursor();
				_fixedColumnsTable.setDragEnabled(true);
			}
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		resizing = false;
		_fixedColumnsTable.setDragEnabled(true);
		
	}
	
	private boolean inResizeZone(MouseEvent e) {
		if (e.getSource() == _fixedColumnsTable) {
			return e.getY() >= _scrollPane.getColumnHeader().getHeight()-3;
		}
		else {
			return false;
		}
	}

	public void mouseDragged(MouseEvent e) {
		int mouseY = e.getY();

		if (resizing) {
			
			int newHeight = mouseY - mouseYOffset;
			if (newHeight > 0) {
				
				_fixedColumnsTable.setPreferredSize(new Dimension(1000, newHeight));
				_scrollPane.getColumnHeader().setPreferredSize(new Dimension(1000, newHeight));
				_fixedScrollPane.getColumnHeader().setPreferredSize(new Dimension(10000, newHeight));
				_scrollPane.revalidate();
				_fixedScrollPane.revalidate();
			}

		}
	}
}
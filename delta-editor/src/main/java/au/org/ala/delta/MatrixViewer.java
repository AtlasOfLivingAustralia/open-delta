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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import au.org.ala.delta.model.Item;

public class MatrixViewer extends JInternalFrame implements IContextHolder {

	private static final long serialVersionUID = 1L;

	private DeltaContext _context;
	private JTable _table;
	private JTable _fixedColumns;
	private MatrixTableModel _model;
	private StateEditor _stateEditor;

	public MatrixViewer(DeltaContext context) {
		super("Matrix Viewer - " + context.getVariable("HEADING", ""));
		_context = context;
		_model = new MatrixTableModel(context);

		this.setSize(new Dimension(600, 500));

		_table = new JTable(_model);
		_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		_table.getTableHeader().setSize(new Dimension(_table.getColumnModel().getTotalColumnWidth(), 100));
		_table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		_table.setRowSelectionAllowed(false);

		_fixedColumns = new JTable(new ItemColumnModel(context));
		_fixedColumns.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		_fixedColumns.getTableHeader().setPreferredSize(new Dimension(_table.getColumnModel().getTotalColumnWidth(), 100));
		_fixedColumns.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_fixedColumns.setBackground(SystemColor.control);

		_fixedColumns.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row = _fixedColumns.getSelectedRow();
				_table.getSelectionModel().setSelectionInterval(row, row);
			}
		});
		
		ListSelectionListener listener = new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row = _table.getSelectedRow();
				_fixedColumns.getSelectionModel().setSelectionInterval(row, row);
				int charId = _table.getSelectedColumn() + 1;
				int itemId = _table.getSelectedRow() + 1;
			
				if (charId > 0 && itemId > 0) {
					au.org.ala.delta.model.Character selectedCharacter = _context.getCharacter(charId);
					Item selectedItem = _context.getItem(itemId);				
					_stateEditor.bind(selectedCharacter, selectedItem);
				}
				
			}
		};

		
		_table.getSelectionModel().addListSelectionListener(listener);
		_table.getColumnModel().getSelectionModel().addListSelectionListener(listener);
		
		_table.getTableHeader().setDefaultRenderer(new MyCellRenderer());

		final JScrollPane scrollpane = new JScrollPane(_table);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		final JScrollPane fixedScrollPane = new JScrollPane(_fixedColumns);
		fixedScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		fixedScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		fixedScrollPane.setPreferredSize(new Dimension(120, 200));

		scrollpane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				fixedScrollPane.getViewport().setViewPosition(scrollpane.getViewport().getViewPosition());
				fixedScrollPane.invalidate();
				fixedScrollPane.updateUI();
			}
		});

		JSplitPane content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		// JPanel content = new JPanel(new BorderLayout());
		content.add(scrollpane, JSplitPane.RIGHT);
		content.add(fixedScrollPane, JSplitPane.LEFT);
		content.setDividerSize(4);
		content.setDividerLocation(180);
		
		_stateEditor = new StateEditor(_context);
		
		JSplitPane divider =new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		divider.setDividerLocation(getHeight() - 200);
		divider.setResizeWeight(1);
		
		divider.setTopComponent(content);
		divider.setBottomComponent(_stateEditor);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(divider, BorderLayout.CENTER);

	}

	public DeltaContext getContext() {
		return _context;
	}

}

class BottomLineBorder extends LineBorder {
	public BottomLineBorder(Color color) {
		super(color);
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		Color oldColor = g.getColor();
		g.setColor(lineColor);
		g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
		g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
		g.setColor(oldColor);
	}
}

class MyCellRenderer extends JTextArea implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();
	/** map from table to map of rows to map of column heights */
	private final Map cellSizes = new HashMap();

	public MyCellRenderer() {
		setLineWrap(true);
		setWrapStyleWord(true);
		setPreferredSize(new Dimension(150, 100));
		setBackground(SystemColor.control);
		setBorder(new BottomLineBorder(SystemColor.controlShadow));
	}

	public Component getTableCellRendererComponent(//
			JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
		// set the colours, etc. using the standard for that platform
		adaptee.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);
		// setForeground(adaptee.getForeground());
		// setBackground(adaptee.getBackground());
		// setBorder(adaptee.getBorder());
		setFont(adaptee.getFont());
		setText(adaptee.getText());

		// This line was very important to get it working with JDK1.4
		TableColumnModel columnModel = table.getColumnModel();

		setSize(columnModel.getColumn(column).getWidth(), 100000);

		int height_wanted = (int) getPreferredSize().getHeight();
		addSize(table, row, column, height_wanted);
		height_wanted = findTotalMaximumRowSize(table, row);
		if (row >= 0 && height_wanted != table.getRowHeight(row)) {
			table.setRowHeight(row, height_wanted);
		}
		return this;
	}

	private void addSize(JTable table, int row, int column, int height) {
		Map rows = (Map) cellSizes.get(table);
		if (rows == null) {
			cellSizes.put(table, rows = new HashMap());
		}
		Map rowheights = (Map) rows.get(new Integer(row));
		if (rowheights == null) {
			rows.put(new Integer(row), rowheights = new HashMap());
		}
		rowheights.put(new Integer(column), new Integer(height));
	}

	/**
	 * Look through all columns and get the renderer. If it is also a TextAreaRenderer, we look at the maximum height in its hash table for this row.
	 */
	private int findTotalMaximumRowSize(JTable table, int row) {
		int maximum_height = 0;
		Enumeration columns = table.getColumnModel().getColumns();

		// Enumeration columns = table.getColumnModel().getColumns();

		while (columns.hasMoreElements()) {

			TableColumn tc = ((TableColumn) columns.nextElement());

			TableCellRenderer cellRenderer = tc.getCellRenderer();
			if (cellRenderer instanceof MyCellRenderer) {
				MyCellRenderer tar = (MyCellRenderer) cellRenderer;
				maximum_height = Math.max(maximum_height, tar.findMaximumRowSize(table, row));
			}
		}
		return maximum_height;
	}

	// TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();
	// if (defaultRenderer instanceof MyCellRender) {
	// this.tableHeader.setDefaultRenderer(((SortableHeaderRenderer) defaultRenderer).tableCellRenderer);
	//
	//

	private int findMaximumRowSize(JTable table, int row) {
		Map rows = (Map) cellSizes.get(table);
		if (rows == null)
			return 0;
		Map rowheights = (Map) rows.get(new Integer(row));
		if (rowheights == null)
			return 0;
		int maximum_height = 0;
		for (Iterator it = rowheights.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			int cellHeight = ((Integer) entry.getValue()).intValue();
			maximum_height = Math.max(maximum_height, cellHeight);
		}
		return maximum_height;
	}
}

class ItemColumnModel implements TableModel {

	private DeltaContext _context;

	public ItemColumnModel(DeltaContext context) {
		_context = context;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int column) {
		return "Item description";
	}

	@Override
	public int getRowCount() {
		return _context.getMaximumNumberOfItems();
	}

	@Override
	public Object getValueAt(int row, int column) {
		Item item = _context.getItem(row + 1);
		return item.getDescription();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Item.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub

	}

}

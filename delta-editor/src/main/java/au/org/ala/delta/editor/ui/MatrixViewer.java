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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;

import au.org.ala.delta.editor.DeltaView;
import au.org.ala.delta.editor.EditorPreferences;
import au.org.ala.delta.editor.ItemController;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.dnd.DropIndicationTable;
import au.org.ala.delta.editor.ui.dnd.SimpleTransferHandler;
import au.org.ala.delta.model.Item;

/**
 * The MatrixViewer presents the attributes of the data set in a tabular format.
 */
public class MatrixViewer extends JInternalFrame implements DeltaView {

	private static final long serialVersionUID = 1L;

	private EditorViewModel _dataSet;
	private DropIndicationTable _table;
	private TableRowHeader _fixedColumns;
	private MatrixTableModel _model;
	private AttributeEditor _attributeEditor;

	
	@Resource
	String windowTitle;

	public MatrixViewer(EditorViewModel dataSet) {
		super();
		setName(dataSet.getShortName()+"-grid");
		ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(MatrixViewer.class);
		resourceMap.injectFields(this);

		this.setTitle(String.format(windowTitle, dataSet.getName()));

		_dataSet = dataSet;
		
		_model = new MatrixTableModel(dataSet);

		this.setSize(new Dimension(600, 500));

		_fixedColumns = new TableRowHeader(dataSet);
		_fixedColumns.setDragEnabled(true);
		_fixedColumns.setDropMode(DropMode.INSERT_ROWS);
		_fixedColumns.setFillsViewportHeight(true);
		new ItemController(_fixedColumns, dataSet);
		
		_table = new DropIndicationTable(_model, _fixedColumns);
		_fixedColumns.setTable(_table);
		
		_fixedColumns.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row = _fixedColumns.getSelectedRow();
				_table.getSelectionModel().setSelectionInterval(row, row);
			}
		});
				
		new TableRowResizer(_fixedColumns, _table);
		
		_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		_table.getTableHeader().setSize(new Dimension(_table.getColumnModel().getTotalColumnWidth(), 100));
		_table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		_table.setCellSelectionEnabled(true);
		_table.setDefaultRenderer(Object.class, new AttributeCellRenderer());
		_table.getTableHeader().setReorderingAllowed(false);
		_table.getTableHeader().setTransferHandler(new TransferHandler("columnModel"));
		
		ListSelectionListener listener = new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int row = _table.getSelectedRow();
				_fixedColumns.getSelectionModel().setSelectionInterval(row, row);
				int charId = _table.getSelectedColumn() + 1;
				int itemId = _table.getSelectedRow() + 1;
				
				if (itemId > 0) {
					Item selectedItem = _dataSet.getItem(itemId);
					_dataSet.setSelectedItem(selectedItem);
				
					if (charId > 0) {
						au.org.ala.delta.model.Character selectedCharacter = _dataSet.getCharacter(charId);
						_attributeEditor.bind(selectedCharacter, selectedItem);
					}
				}
			}
		};

		_table.getSelectionModel().addListSelectionListener(listener);
		_table.getColumnModel().getSelectionModel().addListSelectionListener(listener);
		
		_table.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());

		final JScrollPane scrollpane = new JScrollPane(_table);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		final JScrollPane fixedScrollPane = new JScrollPane(_fixedColumns);
		fixedScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		fixedScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		fixedScrollPane.setPreferredSize(new Dimension(120, 200));
		new TableHeaderResizer((DropIndicationTableHeader)_table.getTableHeader(), _fixedColumns, scrollpane, fixedScrollPane);
		scrollpane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				fixedScrollPane.getViewport().setViewPosition(scrollpane.getViewport().getViewPosition());
			}
		});
		
		// Even though the vertical scrollbar policy is "never" the scroll position can be
		// adjusted while dragging during a drag and drop operation.
		fixedScrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				scrollpane.getViewport().setViewPosition(fixedScrollPane.getViewport().getViewPosition());
				
			}
		});
		
		// This enables mouse wheeling scrolling works over the table row header
		// (it won't by default because the vertical scroll bar policy is never).
		fixedScrollPane.setWheelScrollingEnabled(false);
		fixedScrollPane.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				e.setSource(scrollpane);
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
			}
		});
		
		JSplitPane content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		// JPanel content = new JPanel(new BorderLayout());
		content.add(scrollpane, JSplitPane.RIGHT);
		content.add(fixedScrollPane, JSplitPane.LEFT);
		content.setDividerSize(4);
		content.setDividerLocation(180);

		_attributeEditor = new AttributeEditor(_dataSet);

		JSplitPane divider = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		divider.setDividerLocation(getHeight() - 200);
		divider.setResizeWeight(1);

		divider.setTopComponent(content);
		divider.setBottomComponent(_attributeEditor);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(divider, BorderLayout.CENTER);
		
		_attributeEditor.add(new AttributeEditorListener() {
			
			@Override
			public void advance() {
				updateSelection(1);
			}
			@Override
			public void reverse() {
				updateSelection(-1);
			}
		});

		_table.getActionMap().getParent().remove("paste");

		ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
		javax.swing.Action copyAll = actionMap.get("copyAll");
		if (copyAll != null) {
			_table.getActionMap().put("copyAll", copyAll);
		}

		javax.swing.Action copySelection = actionMap.get("copy");
		if (copySelection != null) {
			_table.getActionMap().put("copy", copySelection);
		}
		if ((_dataSet.getMaximumNumberOfItems() > 0) && (_dataSet.getNumberOfCharacters() > 0)) {
			selectCell(0, 0);
		}
		configureDefaultRowHeight();
		
		_table.addKeyListener(new KeyProxy());
	}	
	
	/**
	 * Updates the current table selection index.  
	 * @param selectionModifier the amount to add to the current selection index.
	 */
	private void updateSelection(int selectionModifier) {
		switch (EditorPreferences.getEditorAdvanceMode()) {
		case Character:
			int candidateCharIndex = _table.getSelectedColumn() + selectionModifier;
			if (candidateCharIndex >= 0 && candidateCharIndex < _table.getModel().getColumnCount()) {
				_table.setColumnSelectionInterval(candidateCharIndex, candidateCharIndex);							
			}
			break;
		case Item:
			int candidateRowIndex = _table.getSelectedRow() + selectionModifier;;
			if (candidateRowIndex >= 0 && candidateRowIndex < _table.getModel().getRowCount()) {
				_table.setRowSelectionInterval(candidateRowIndex, candidateRowIndex);							
			}						
			break;
		}
		scrollCellToVisible(_table.getSelectedRow(), _table.getSelectedColumn());
	}
	
	public void scrollCellToVisible(int row, int column) {
		_table.scrollRectToVisible(_table.getCellRect(row, column, true));
	}	
	
	private void selectCell(int rowIndex, int colIndex) {
		_table.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
		_table.getColumnModel().getSelectionModel().setSelectionInterval(colIndex, colIndex);		
	}
	
	private void configureDefaultRowHeight() {
		TableCellRenderer renderer = _fixedColumns.getDefaultRenderer(Object.class);
		Component comp = renderer.getTableCellRendererComponent(_fixedColumns, "Example Text", true, true, 0, 0);
		_fixedColumns.setRowHeight(comp.getPreferredSize().height);
		_table.setRowHeight(comp.getPreferredSize().height);
	}
	
	@Action(block = BlockingScope.APPLICATION)
	public Task<Void, Void> copy() {
		return new CopySelectedTask(Application.getInstance());
	}

	@Action(block = BlockingScope.APPLICATION)
	public Task<Void, Void> copyAll() {
		return new CopyAllTask(Application.getInstance());
	}
	
	
	@Override
	public void open() {}

	@Override
	public boolean editsValid() {
		return _attributeEditor.isAttributeValid();
	}

	@Override
	public String getViewTitle() {
		return windowTitle;
	}

	abstract class ClipboardCopyTask extends Task<Void, Void> {

		protected String CELL_SEPERATOR = "\t";
		protected String EOL = "\n";
		private long _totalCells = 0;
		private long _cellsCopied = 0;
		private int _lastPercent = 0;

		public ClipboardCopyTask(Application application) {
			super(application);
		}

		protected void setTotalCells(long total) {
			_totalCells = total;
		}

		protected void incrementCellsCopied() {
			_cellsCopied++;
			int newPercent = (int) (((double) _cellsCopied) / ((double) _totalCells) * 100);
			if (newPercent != _lastPercent) {
				_lastPercent = newPercent;
				setProgress(_lastPercent);
			}
		}

	}

	class CopySelectedTask extends ClipboardCopyTask {

		public CopySelectedTask(Application application) {
			super(application);
		}

		@Override
		protected Void doInBackground() throws Exception {

			message("copying");

			StringBuilder b = new StringBuilder();

			int[] cols = _table.getSelectedColumns();
			int[] rows = _table.getSelectedRows();

			setTotalCells(cols.length * rows.length);

			// Now for each row...
			for (int i = 0; i < rows.length; ++i) {
				int row = rows[i];

				for (int j = 0; j < cols.length; ++j) {
					int col = cols[j];
					String value = ((MatrixCellViewModel) _model.getValueAt(row, col)).getText();
					if (col > 0) {
						b.append(CELL_SEPERATOR);
					}
					b.append(value);
					incrementCellsCopied();
				}
				b.append(EOL);
			}

			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			cb.setContents(new StringSelection(b.toString()), null);

			return null;
		}

	}

	class CopyAllTask extends CopySelectedTask {

		public CopyAllTask(Application application) {
			super(application);
		}

		@Override
		protected Void doInBackground() throws Exception {

			message("copying");

			StringBuilder b = new StringBuilder();

			setTotalCells(_model.getColumnCount() * _model.getRowCount());

			// First do row headers, which are item descriptions
			b.append("(Items)");
			for (int i = 0; i < _model.getColumnCount(); i++) {
				b.append(CELL_SEPERATOR).append(_model.getColumnName(i));
			}
			b.append(EOL);

			// Now for each row...

			for (int row = 0; row < _model.getRowCount(); ++row) {
				b.append(_fixedColumns.getModel().getValueAt(row, 0));
				// and for each data item (column)
				for (int col = 0; col < _model.getColumnCount(); ++col) {
					String value = ((MatrixCellViewModel) _model.getValueAt(row, col)).getText();
					b.append(CELL_SEPERATOR).append(value);
					incrementCellsCopied();
				}
				b.append(EOL);
			}

			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			cb.setContents(new StringSelection(b.toString()), null);

			return null;
		}

	}

	/**
	 * This listener class is responsible for transferring focus to the 
	 * AttributeEditor when the user starts typing in a focused cell.
	 * Command type events (e.g. cut/copy/paste) will not initiate a focus transfer
	 * but any other key events will.	 *
	 */
	class KeyProxy extends KeyAdapter {

		@Override
		public void keyTyped(KeyEvent e) {
			checkAndForwardKey(e);
		}
		
		private void checkAndForwardKey(KeyEvent e) {
			if (shouldForwardKey(e)) {
				_attributeEditor.acceptKeyEvent(e);
			}
		}
		
		/**
		 * Checks a key event and determines whether it represents a command
		 * (e.g. cut/copy/paste).
		 * @param e the KeyEvent to check.
		 * @return true if the KeyEvent is a command.
		 */
		private boolean isCommand(KeyEvent e) {
			int commandKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
            return ((e.getModifiers() & commandKeyMask) != 0);
		}
		
		/** 
		 * @return true if the table has a only one selected cell.
		 */
		private boolean isSingleCellSelected() {
			return (_table.getSelectedColumnCount() == 1 && _table.getSelectedRowCount() == 1);
		}
		
		/**
		 * A key will be forwarded if: 
		 * a) it's not a command (cut/copy/paste)
		 * b) there is a single selected cell in the table (so it is clear
		 * which attribute is being edited).
		 * @param e the KeyEvent that was generated.
		 * @return true if the KeyEvent should be forwarded to the AttributeEditor for
		 * processing.
		 */
		private boolean shouldForwardKey(KeyEvent e) {
			return isSingleCellSelected() && !isCommand(e);
		}
	}
	
}

class BottomLineBorder extends LineBorder {

	private static final long serialVersionUID = 1L;

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


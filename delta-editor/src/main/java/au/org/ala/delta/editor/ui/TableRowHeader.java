package au.org.ala.delta.editor.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.observer.AbstractDataSetObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.ui.AboutBox;


/**
 * The TableRowHeader is a JTable suitable for use as a row header for another table.
 */
public class TableRowHeader extends JTable implements ReorderableList<Item> {

	private static final long serialVersionUID = 4631242294243331000L;
	private static final String SELECTION_ACTION_NAME = "selectionAction";

	
	public TableRowHeader(EditorViewModel dataModel) {
		super(new ItemColumnModel(dataModel));
		((ItemColumnModel)getModel()).setTable(this);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		// These lines are required to enable initiating a drag and drop operation
		// on an unselected cell.
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		setBackground(SystemColor.control);
		FixedColumnRenderer renderer = new FixedColumnRenderer();
		setDefaultRenderer(Object.class, renderer);
	}
	
	public void setTable(JTable table) {
		getTableHeader().setPreferredSize(new Dimension(table.getColumnModel().getTotalColumnWidth(), 100));
		getTableHeader().setDefaultRenderer(new HeaderRenderer());
	}
	@Override
	public int getSelectedIndex() {
		int selectedRow = getSelectionModel().getMinSelectionIndex();
		return selectedRow;
	}

	@Override
	public void setSelectedIndex(int index) {
		getSelectionModel().setSelectionInterval(index, index);
		scrollRectToVisible(getCellRect(index, 0, true));
		
	}
	
	@Override
	public int getDropLocationIndex() {
		return getDropLocation().getRow();
	}
	
	/**
	 * Registers the action to take when a selection (double click or Enter key) has been made on
	 * this list.
	 * @param action the action that will be invoked on selection.
	 */
	public void setSelectionAction(Action action) {
		addMouseListener(new DoubleClickToAction());
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECTION_ACTION_NAME);
		getActionMap().put(SELECTION_ACTION_NAME, action);
	}
	
	/**
	 * Detects double clicks and treats them as a different type of selection event.
	 */
	public class DoubleClickToAction extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
				int index = rowAtPoint(e.getPoint());
				getSelectionModel().setSelectionInterval(index, index);
				Action action = getActionMap().get(SELECTION_ACTION_NAME);
	
				if (action != null) {
					ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
					action.actionPerformed(event);
				}
			}
		}
	}

	/**
	 * Renderer that renders the header component like the cells.
	 */
	class HeaderRenderer extends DefaultTableCellRenderer {
		
		private static final long serialVersionUID = 2624649501223462614L;

		public HeaderRenderer() {
			setBackground(UIManager.getColor("Panel.background"));
			setText("");
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			g.setColor(SystemColor.controlShadow);
			g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
		}
	}
	
	static class ItemColumnModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private JTable _table;
		private EditorViewModel _dataSet;
		private ItemFormatter _formatter;

		@Resource
		String columnName;

		public ItemColumnModel(EditorViewModel dataSet) {
			ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(AboutBox.class);
			resourceMap.injectFields(this);
			_dataSet = dataSet;
			_dataSet.addDeltaDataSetObserver(new ItemAddedListener());
			boolean includeNumber = false;
			boolean stripComments = false;
			boolean replaceAngleBrackets = false;
			boolean stripRtf = true;
			boolean useShortFormOfVariant = true;
			_formatter = new ItemFormatter(includeNumber, stripComments, replaceAngleBrackets, stripRtf, useShortFormOfVariant);
		}
		
		public void setTable(JTable table) {
			_table = table;
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public String getColumnName(int column) {
			return columnName;
		}

		@Override
		public int getRowCount() {
			return _dataSet.getMaximumNumberOfItems();
		}

		@Override
		public Object getValueAt(int row, int column) {
			Item item = _dataSet.getItem(row + 1);
			return _formatter.formatItemDescription(item);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return Item.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
		
		/**
		 * Keeps this model in sync with changes to the DataSet.
		 */
		class ItemAddedListener extends AbstractDataSetObserver {
			@Override
			public void itemAdded(DeltaDataSetChangeEvent event) {
				fireTableRowsInserted(getRowIndex(event), getRowIndex(event));
			}
			@Override
			public void itemEdited(DeltaDataSetChangeEvent event) {
				fireTableCellUpdated(getRowIndex(event), 0);
			}
			@Override
			public void itemDeleted(DeltaDataSetChangeEvent event) {
				int rowIndex = getRowIndex(event);
				int selection = _table.getSelectedRow();
				fireTableRowsDeleted(rowIndex, rowIndex);
				if (selection == rowIndex) {
					selection = Math.min(selection, getRowCount()-1);
					_table.getSelectionModel().setSelectionInterval(selection, selection);
				}
			}
			
			private int getRowIndex(DeltaDataSetChangeEvent event) {
				return event.getItem().getItemNumber()-1;
			}
			
		}

	}

	
}

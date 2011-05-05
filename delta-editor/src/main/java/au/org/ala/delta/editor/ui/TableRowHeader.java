package au.org.ala.delta.editor.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.observer.AbstractDataSetObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.ui.AboutBox;


/**
 * The TableRowHeader is a JTable suitable for use as a row header for another table.
 */
public class TableRowHeader extends JTable implements ReorderableItemList {

	private static final long serialVersionUID = 4631242294243331000L;
	private EditorDataModel _dataModel;
	
	public TableRowHeader(EditorDataModel dataModel) {
		super(new ItemColumnModel(dataModel));
		
		_dataModel = dataModel;
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
	public Item getSelectedItem() {
		int selectedRow = getSelectionModel().getMinSelectionIndex();
		return _dataModel.getItem(selectedRow+1);
	}

	@Override
	public void setSelectedItem(int itemNumber) {
		getSelectionModel().setSelectionInterval(itemNumber-1, itemNumber-1);
		
	}
	
	@Override
	public int getDropLocationIndex() {
		return getDropLocation().getRow();
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

		private EditorDataModel _dataSet;
		private ItemFormatter _formatter;

		@Resource
		String columnName;

		public ItemColumnModel(EditorDataModel dataSet) {
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
				fireTableRowsDeleted(getRowIndex(event), getRowIndex(event));
			}
			
			private int getRowIndex(DeltaDataSetChangeEvent event) {
				return event.getItem().getItemNumber()-1;
			}
			
		}

	}

	
}

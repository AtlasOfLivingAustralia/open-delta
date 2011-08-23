package au.org.ala.delta.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;

/**
 * Allows the user to see and execute CONFOR / DIST / KEY directives files.
 */
public class ActionSetsDialog extends JDialog {
	
	private static final long serialVersionUID = -3525771335064005800L;
	
	private EditorViewModel _model;
	private ResourceMap _resources;
	
	private JLabel actionSetDetailsLabel;
	private JButton runButton;
	private JButton doneButton;
	private JButton editButton;
	private JButton deleteButton;
	private JTable conforTable;
	private JTable intkeyTable;
	private JTable distTable;
	private JTable keyTable;
	private JTabbedPane tabbedPane;
	
	
	public ActionSetsDialog(Window parent, EditorViewModel model) {
		super(parent);
		_model = model;
		_resources = Application.getInstance().getContext().getResourceMap();
		createUI();
		addEventHandlers();
		updateGUI();
	}
	
	
	private void addEventHandlers() {
		ActionMap actions = Application.getInstance().getContext().getActionMap(this);
		runButton.setAction(actions.get("runDirectiveFile"));
		editButton.setAction(actions.get("editDirectiveFile"));
		deleteButton.setAction(actions.get("deleteDirectiveFile"));
		doneButton.setAction(actions.get("doneWithActionSets"));
		

	}
	@Action
	public void runDirectiveFile() {
		DirectiveFile file = getSelectedFile();
		System.out.println("Run: "+file.getShortFileName());
		
	}
	@Action
	public void editDirectiveFile() {
		DirectiveFile file = getSelectedFile();
		System.out.println("Edit: "+file.getShortFileName());
		
	}
	@Action
	public void deleteDirectiveFile() {
		DirectiveFile file = getSelectedFile();
		System.out.println("Delete: "+file.getShortFileName());
		// TODO are you sure?
		//_model.deleteDirectiveFile(file);
	}
	@Action
	public void doneWithActionSets() {
		setVisible(false);
	}
	
	private DirectiveFile getSelectedFile() {
		JTable selectedTable = (JTable)((JScrollPane)tabbedPane.getSelectedComponent()).getViewport().getView();
		
		int selected = selectedTable.getSelectedRow();
		return ((DirectiveFileTableModel)selectedTable.getModel()).getFileAt(selected);
	}
	
	private void createUI() {
		setName("actionSetsDialog");
		setTitle(_resources.getString("actionSetsDialog.title"));
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		conforTable = new JTable();
		tabbedPane.addTab(_resources.getString("directiveTypeConfor.text"), new JScrollPane(conforTable));
		
		intkeyTable = new JTable();
		tabbedPane.addTab(_resources.getString("directiveTypeIntkey.text"), new JScrollPane(intkeyTable));
		
		distTable = new JTable();
		tabbedPane.addTab(_resources.getString("directiveTypeDist.text"), new JScrollPane(distTable));
		
		keyTable = new JTable();
		tabbedPane.addTab(_resources.getString("directiveTypeKey.text"), new JScrollPane(keyTable));
		
		JPanel buttonPanel = new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.EAST);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		
		runButton = new JButton("Run");
		buttonPanel.add(runButton);
		
		doneButton = new JButton("Done");
		buttonPanel.add(doneButton);
		
		editButton = new JButton("Edit");
		buttonPanel.add(editButton);
		
		deleteButton = new JButton("Delete");
		buttonPanel.add(deleteButton);
		
		JPanel labelPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) labelPanel.getLayout();
		flowLayout.setHgap(20);
		flowLayout.setAlignment(FlowLayout.LEFT);
		getContentPane().add(labelPanel, BorderLayout.NORTH);
		
		JLabel actionSetLabel = new JLabel("Action Set:");
		actionSetLabel.setName("actionSetsActionSetsLabel");
		labelPanel.add(actionSetLabel);
		
		actionSetDetailsLabel = new JLabel("");
		labelPanel.add(actionSetDetailsLabel);
	}

	private void updateGUI() {
		Map<DirectiveType, List<DirectiveFile>> files = new HashMap<DirectiveFile.DirectiveType, List<DirectiveFile>>();
		
		for (DirectiveType type : DirectiveType.values()) {
			files.put(type, new ArrayList<DirectiveFile>());
		}
		int numFiles = _model.getDirectiveFileCount();
		for (int i=1; i<=numFiles; i++) {
			DirectiveFile file = _model.getDirectiveFile(i);
			files.get(file.getType()).add(file);
		}
		conforTable.setModel(new DirectiveFileTableModel(files.get(DirectiveType.CONFOR)));
		intkeyTable.setModel(new DirectiveFileTableModel(files.get(DirectiveType.INTKEY)));
		distTable.setModel(new DirectiveFileTableModel(files.get(DirectiveType.DIST)));
		keyTable.setModel(new DirectiveFileTableModel(files.get(DirectiveType.KEY)));	
	}
	
	private class DirectiveFileTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 6176954693803171069L;
		private List<DirectiveFile> _files;
		private String[] columnKeys = {"actionSetsActionColumn", "actionSetsFileNameColumn", "actionSetsImportExportColumn"};
		private String[] columnNames;
		private DateFormat _displayFormat;
		
		public DirectiveFileTableModel(List<DirectiveFile> files) {
			_files = files;
			columnNames = new String[columnKeys.length];
			for (int i=0; i<columnNames.length; i++) {
				columnNames[i] = _resources.getString(columnKeys[i]);
			}
			_displayFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		}
		
		@Override
		public int getRowCount() {
			return _files.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return getDescription(rowIndex);
			}
			else if (columnIndex == 1) {
				return _files.get(rowIndex).getShortFileName();
			}
			else {
				long lastModified = _files.get(rowIndex).getLastModifiedTime();
				return _displayFormat.format(new Date(lastModified));
			}
			
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		private String getDescription(int fileNum) {
			String description = _files.get(fileNum).getDescription();
			if (StringUtils.isEmpty(description)) {
				description = _resources.getString("actionSetsNoDescription");
			}
			return description;
		}
		
		public DirectiveFile getFileAt(int rowIndex) {
			return _files.get(rowIndex);
		}
		
	}
}

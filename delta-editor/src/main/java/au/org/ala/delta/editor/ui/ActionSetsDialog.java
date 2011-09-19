package au.org.ala.delta.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.editor.ui.util.MessageDialogHelper;

/**
 * Allows the user to see and execute CONFOR / DIST / KEY directives files.
 */
public class ActionSetsDialog extends AbstractDeltaView {
	
	private static final long serialVersionUID = -3525771335064005800L;
	
	private static final int TIME_COLUMN_WIDTH = 150;
	private static final int FILE_NAME_COLUMN_WIDTH = 150;
	private static final int FILE_DESCRIPTION_COLUMN_WIDTH = 300;
	
	private EditorViewModel _model;
	private ResourceMap _resources;
	private ActionMap _actions;
	private MessageDialogHelper _messageHelper;
	
	private JLabel actionSetDetailsLabel;
	private JButton runButton;
	private JButton addButton;
	private JButton doneButton;
	private JButton editButton;
	private JButton deleteButton;
	private JTable conforTable;
	private JTable intkeyTable;
	private JTable distTable;
	private JTable keyTable;
	private JTabbedPane tabbedPane;
	
	
	public ActionSetsDialog(EditorViewModel model) {
		
		_model = model;
		_resources = Application.getInstance().getContext().getResourceMap();
		_actions = Application.getInstance().getContext().getActionMap(this);
		ActionMap editorActions = Application.getInstance().getContext().getActionMap();
		_actions.put("viewDirectivesEditor", editorActions.get("viewDirectivesEditor"));
		_messageHelper = new MessageDialogHelper();
		createUI();
		addEventHandlers();
		updateGUI();
	}
	
	
	private void addEventHandlers() {
		runButton.setAction(_actions.get("runDirectiveFile"));
		addButton.setAction(_actions.get("addDirectiveFile"));
		editButton.setAction(_actions.get("editDirectiveFile"));
		deleteButton.setAction(_actions.get("deleteDirectiveFile"));
		doneButton.setAction(_actions.get("doneWithActionSets"));
		
		JTable[] tables = {conforTable, intkeyTable, distTable, keyTable};
		for (JTable table : tables) {
			table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent e) {
					updateAction();
				}
			});
		}
		tabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				updateAction();
			}
		});
	}
	
	private void configureWidths(JTable table) {
		table.getColumnModel().getColumn(0).setPreferredWidth(FILE_DESCRIPTION_COLUMN_WIDTH);
		table.getColumnModel().getColumn(1).setPreferredWidth(FILE_NAME_COLUMN_WIDTH);
		table.getColumnModel().getColumn(2).setPreferredWidth(TIME_COLUMN_WIDTH);		
	}
	
	public void updateAction() {
		DirectiveFile file = getSelectedFile();
		if (file == null) {
			enableActions(false);
			actionSetDetailsLabel.setText("");
		}
		else {
			enableActions(true);
			actionSetDetailsLabel.setText(getActionText(file));
		}
	}
	
	private String getActionText(DirectiveFile file) {
		String action = file.getDefiningDirective();
		if (file.isSpecsFile()) {
			action = _resources.getString("actionSetsSpecsFileAction");
		}
		else if (file.isCharsFile()) {
			action = _resources.getString("actionSetsCharsFileAction");
		}
		else if (file.isItemsFile()) {
			action = _resources.getString("actionSetsItemsFileAction");
		}
		
		if (StringUtils.isEmpty(action)) {
			switch (file.getType()) {
			case CONFOR:
			    action = _resources.getString("actionSetsDefaultConforAction");
				break;
			case INTKEY:
				action = _resources.getString("actionSetsDefaultIntkeyAction");
				break;
			case DIST:
				action = _resources.getString("actionSetsDefaultDistAction");
				break;
			case KEY:
				action = _resources.getString("actionSetsDefaultKeyAction");
				break;
			}
		}
		return action;
	}
	
	private void enableActions(boolean enable) {
		String[] actions = {"runDirectiveFile", "editDirectiveFile","deleteDirectiveFile"};
		for (String action : actions) {
			_actions.get(action).setEnabled(enable);
		}
	}
	
	@Action
	public void runDirectiveFile() {
		DirectiveFile file = getSelectedFile();	
		if (file == null) {
			return;
		}
		String program = null;
		switch (file.getType()) {
		case CONFOR:
			program = "CONFORQW";
			break;
		case INTKEY:
			program = "INTKEY5";
			break;
		case DIST:
			program = "DISTQW";
			break;
		case KEY:
			program = "KEYQW";
			break;
		}
		try {
			String name = file.getShortFileName();
			String fileName = FilenameUtils.concat(_model.getDataSetPath(), name);
			Runtime.getRuntime().exec(new String[]{program, "\""+fileName+"\""});
		}
		catch (Exception e) {
			_messageHelper.errorRunningDirectiveFile(file.getShortFileName());
		}
	}
	
	@Action
	public void addDirectiveFile() {
		
		String name = _messageHelper.promptForDirectiveFileName();
		if (name != null) {
			int fileCount = _model.getDirectiveFileCount();
			DirectiveFile file = _model.addDirectiveFile(fileCount, name, selectedDirectiveType());
			updateGUI();
			
			JTable selectedTable = (JTable)((JScrollPane)tabbedPane.getSelectedComponent()).getViewport().getView();
			DirectiveFileTableModel tm = (DirectiveFileTableModel)selectedTable.getModel();
			int newFileIndex = tm.indexOf(file);
			selectedTable.getSelectionModel().setSelectionInterval(newFileIndex, newFileIndex);
			
			editDirectiveFile();
		}
	}
	
	@Action
	public void editDirectiveFile() {
		DirectiveFile file = getSelectedFile();
		_model.setSelectedDirectiveFile(file);
		
		_actions.get("viewDirectivesEditor").actionPerformed(null);
	}
	@Action
	public void deleteDirectiveFile() {
		DirectiveFile file = getSelectedFile();
		if (file == null) {
			return;
		}
		if(_messageHelper.confirmDeleteDirectiveFile(file.getShortFileName())) {
			_model.deleteDirectiveFile(file);
			updateGUI();
		}
	}
	@Action
	public void doneWithActionSets() {
		setVisible(false);
	}
	
	private DirectiveFile getSelectedFile() {
		JTable selectedTable = (JTable)((JScrollPane)tabbedPane.getSelectedComponent()).getViewport().getView();
		
		int selected = selectedTable.getSelectedRow();
		DirectiveFile file = null;
		if (selected >= 0) {
			file = ((DirectiveFileTableModel)selectedTable.getModel()).getFileAt(selected); 
		}
		return file;
	}
	
	private void createUI() {
		setName("actionSetsDialog");
		
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
		
		runButton = new JButton("Run");
		
		addButton = new JButton("Add");
		
		editButton = new JButton("Edit");
		
		deleteButton = new JButton("Delete");
		
		doneButton = new JButton("Done");
		GroupLayout gl_buttonPanel = new GroupLayout(buttonPanel);
		gl_buttonPanel.setHorizontalGroup(
			gl_buttonPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(runButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(addButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(editButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(deleteButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(doneButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		gl_buttonPanel.setVerticalGroup(
			gl_buttonPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_buttonPanel.createSequentialGroup()
					.addComponent(runButton)
					.addComponent(addButton)
					.addComponent(editButton)
					.addComponent(deleteButton)
					.addComponent(doneButton))
		);
		buttonPanel.setLayout(gl_buttonPanel);
		
		
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
		configureWidths(conforTable);
		intkeyTable.setModel(new DirectiveFileTableModel(files.get(DirectiveType.INTKEY)));
		configureWidths(intkeyTable);
		distTable.setModel(new DirectiveFileTableModel(files.get(DirectiveType.DIST)));
		configureWidths(distTable);
		keyTable.setModel(new DirectiveFileTableModel(files.get(DirectiveType.KEY)));
		configureWidths(keyTable);
		
		updateAction();
	}
	
	private DirectiveType selectedDirectiveType() {
		JTable selectedTable = (JTable)((JScrollPane)tabbedPane.getSelectedComponent()).getViewport().getView();
		if (selectedTable == conforTable) {
			return DirectiveType.CONFOR;
		}
		else if (selectedTable == intkeyTable) {
			return DirectiveType.INTKEY;
		}
		else if (selectedTable == keyTable) {
			return DirectiveType.KEY;
		}
		else  {
			return DirectiveType.DIST;
		}
	}
	
	@Override
	public String getViewTitle() {
		return _resources.getString("actionSetsDialog.title");
	}

	/**
	 * Presents a List of DirectiveFiles in a form suitable for display
	 * in the Action Sets Dialog.
	 */
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
				if (lastModified == 0) {
					return "";
				}
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
		
		public int indexOf(DirectiveFile file) {
			for (int i=0; i<_files.size(); i++) {
				DirectiveFile tmpFile = _files.get(i);
				if (file.getShortFileName().equals(tmpFile.getShortFileName())) {
					return i;
				}
			}
			return -1;
		}
		
	}
}

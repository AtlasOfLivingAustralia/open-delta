package au.org.ala.delta.editor.directives.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.EditorPreferences;
import au.org.ala.delta.editor.directives.ImportFileNameFilter;
import au.org.ala.delta.ui.MessageDialogHelper;
import au.org.ala.delta.ui.util.IconHelper;

/**
 * The ImportExportDialog is the user interface component that allows the user to:
 * 1) In import mode: select a set of DELTA directive files to import into the DELTA Editor.
 * 2) In export mode: export the DELTA Editor data set to DELTA directives files.
 */
public class ImportExportDialog extends JDialog {
	
	private static final String DEFAULT_ITEMS_DIRECTIVE_FILE = "items";
	private static final String DEFAULT_CHARS_DIRECTIVE_FILE = "chars";
	private static final String DEFAULT_SPECS_DIRECTIVE_FILE = "specs";

	public static enum DirectiveType {
		CONFOR("C"), INTKEY("I"), DIST("D"), KEY("K"); 
		private String _abbreviation;
		private DirectiveType(String abbreviation) {
			_abbreviation = abbreviation;
		}
		public String getAbbreviation(){return _abbreviation;}
	};
	
	public static class DirectiveFile {
		
		public DirectiveFile(String name, DirectiveType type) {
			_fileName = name;
			_type = type;
		}
		public String _fileName;
		public DirectiveType _type;
		
		public String toString() {
			return _fileName+" ("+_type.getAbbreviation()+")";
		}
	}
	
	private DirectiveType _selectedDirectiveType = DirectiveType.CONFOR;
	private File _currentDirectory;
	private String _specsFile;
	private String _charactersFile;
	private String _itemsFile;
	private List<DirectiveFile> _otherDirectivesFiles;
	private List<String> _possibleDirectiveFiles;
	private boolean _okPressed;
	private String _currentFilter;
	
	private static final long serialVersionUID = 8695641918190503720L;
	private JTextField currentDirectoryTextField;
	private JTextField charactersFileTextField;
	private JTextField itemsFileTextField;
	private JList possibleDirectivesList;
	private JButton btnChange;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JList otherDirectivesList;
	private JRadioButton rdbtnConfor;
	private JRadioButton rdbtnIntkey;
	private JRadioButton rdbtnDist;
	private JRadioButton rdbtnKey;
	private JButton btnOk;
	private JButton btnCancel;
	private JButton moveToOtherButton;
	private JButton moveToPossibleButton;
	
	@Resource
	private String filterDialogTitle;
	@Resource
	private String filterDialogMessage;
	@Resource
	private String directoryChooserTitle;
	@Resource
	private String dialogTitle;
	@Resource
	private String specificationsPanelTitle;
	@Resource
	private String charsPanelTitle;
	@Resource
	private String itemsPanelTitle;
	@Resource
	private String otherPanelTitle;
	@Resource
	private String possiblePanelTitle;
	@Resource
	private String directiveTypePanelTitle;
	private JButton moveToSpecsButton;
	private JButton moveToCharsButton;
	private JButton moveToItemsButton;
	private JTextField specificationsFileTextField;
	private JButton excludeFilterButton;
	private JTextField currentImportFilterTextField;
	
	public ImportExportDialog() {
		setName("ImportExportDialogBox");
		ResourceMap resources = Application.getInstance().getContext().getResourceMap(ImportExportDialog.class);
		resources.injectFields(this);
		
		_specsFile = DEFAULT_SPECS_DIRECTIVE_FILE;
		_otherDirectivesFiles = new ArrayList<DirectiveFile>();
		_possibleDirectiveFiles = new ArrayList<String>();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		createUI();
		
		_currentFilter = EditorPreferences.getImportFileFilter();
		
		addEventListeners();
		
		// This is necessary as if we bring up the chooser dialog before this dialog is visible
		// the JFileChooser cannot be shown relative to this dialog and hence will always appear
		// on the default monitor in a multi monitor configuration.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				directorySelected();
			}
		});
	}

	private void addEventListeners() {
		ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
		btnChange.setAction(actionMap.get("directorySelected"));
		btnOk.setAction(actionMap.get("okPressed"));
		btnOk.setEnabled(false);
		btnCancel.setAction(actionMap.get("cancelPressed"));
		excludeFilterButton.setAction(actionMap.get("updateFilter"));
		
		ActionListener typeSelectionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSelectedDirectiveType((JRadioButton)e.getSource());
			}
		};
		moveToOtherButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				moveFromPossibleToOther();
			}
		});
		moveToPossibleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				moveFromOtherToPossible();
			}
		});
		moveToSpecsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				moveToSpecs();
			}
		});
		moveToCharsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				moveToChars();
			}
		});
		moveToItemsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				moveToItems();
			}
		});
		
		rdbtnConfor.putClientProperty(DirectiveType.class, DirectiveType.CONFOR);
		rdbtnConfor.addActionListener(typeSelectionListener);
		rdbtnIntkey.putClientProperty(DirectiveType.class, DirectiveType.INTKEY);
		rdbtnIntkey.addActionListener(typeSelectionListener);
		rdbtnDist.putClientProperty(DirectiveType.class, DirectiveType.DIST);
		rdbtnDist.addActionListener(typeSelectionListener);
		rdbtnKey.putClientProperty(DirectiveType.class, DirectiveType.KEY);
		rdbtnKey.addActionListener(typeSelectionListener);
		
	}
	
	/**
	 * Creates and lays out the UI components for this dialog.
	 */
	private void createUI() {
		setTitle(dialogTitle);
		setIconImages(IconHelper.getBlueIconList());
		JPanel leftPanel = new JPanel();
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, specificationsPanelTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, charsPanelTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, itemsPanelTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_3 = new JPanel();
		panel_3.setPreferredSize(new Dimension(150, 10));
		panel_3.setBorder(new TitledBorder(null, otherPanelTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout gl_leftPanel = new GroupLayout(leftPanel);
		gl_leftPanel.setHorizontalGroup(
			gl_leftPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_leftPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_leftPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
						.addComponent(panel_3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
						.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_leftPanel.setVerticalGroup(
			gl_leftPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_leftPanel.createSequentialGroup()
					.addGap(5)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(5)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_3, GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE))
		);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		otherDirectivesList = new JList();
		panel_3.add(new JScrollPane(otherDirectivesList));
		
		JPanel panel_7 = new JPanel();
		panel_3.add(panel_7, BorderLayout.EAST);
		
		moveToOtherButton = new JButton("<<");
		moveToPossibleButton = new JButton(">>");
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, directiveTypePanelTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		rdbtnConfor = new JRadioButton();
		rdbtnConfor.setName("directiveTypeConfor");
		buttonGroup.add(rdbtnConfor);
		rdbtnConfor.setSelected(true);
		
		rdbtnIntkey = new JRadioButton();
		rdbtnIntkey.setName("directiveTypeIntkey");
		buttonGroup.add(rdbtnIntkey);
		
		rdbtnDist = new JRadioButton();
		rdbtnDist.setName("directiveTypeDist");
		buttonGroup.add(rdbtnDist);
		
		rdbtnKey = new JRadioButton();
		rdbtnKey.setName("directiveTypeKey");
		buttonGroup.add(rdbtnKey);
		
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
						.addComponent(rdbtnConfor)
						.addComponent(rdbtnIntkey)
						.addComponent(rdbtnDist)
						.addComponent(rdbtnKey))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_4.setVerticalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addComponent(rdbtnConfor)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnIntkey)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnDist)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(rdbtnKey))
		);
		panel_4.setLayout(gl_panel_4);
		GroupLayout gl_panel_7 = new GroupLayout(panel_7);
		gl_panel_7.setHorizontalGroup(
			gl_panel_7.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_7.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_panel_7.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_panel_7.createSequentialGroup()
							.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(Alignment.TRAILING, gl_panel_7.createSequentialGroup()
							.addGroup(gl_panel_7.createParallelGroup(Alignment.LEADING)
								.addComponent(moveToPossibleButton)
								.addComponent(moveToOtherButton))
							.addGap(50))))
		);
		gl_panel_7.setVerticalGroup(
			gl_panel_7.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_7.createSequentialGroup()
					.addComponent(moveToOtherButton)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(moveToPossibleButton)
					.addPreferredGap(ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
					.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		panel_7.setLayout(gl_panel_7);
		
		itemsFileTextField = new JTextField();
		itemsFileTextField.setColumns(10);
		
		moveToItemsButton = new JButton("<<");
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addComponent(itemsFileTextField, GroupLayout.PREFERRED_SIZE, 217, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
					.addComponent(moveToItemsButton)
					.addGap(50))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(itemsFileTextField, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(moveToItemsButton))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		
		charactersFileTextField = new JTextField();
		charactersFileTextField.setColumns(10);
		
		moveToCharsButton = new JButton("<<");
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
					.addComponent(charactersFileTextField, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
					.addComponent(moveToCharsButton)
					.addGap(50))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(charactersFileTextField, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(moveToCharsButton))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		
		specificationsFileTextField = new JTextField();
		specificationsFileTextField.setColumns(10);
		
		moveToSpecsButton = new JButton("<<");
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addComponent(specificationsFileTextField, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
					.addComponent(moveToSpecsButton)
					.addGap(50))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(specificationsFileTextField, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(moveToSpecsButton))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		leftPanel.setLayout(gl_leftPanel);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setName("directoryPanel");
		rightPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, possiblePanelTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		rightPanel.add(panel_5, BorderLayout.CENTER);
		panel_5.setLayout(new BorderLayout(0, 0));
		
		possibleDirectivesList = new JList();
		panel_5.add(new JScrollPane(possibleDirectivesList), BorderLayout.CENTER);
		
		JPanel panel_6 = new JPanel();
		panel_5.add(panel_6, BorderLayout.SOUTH);
		
		JLabel lblExclude = new JLabel("Exclude");
		
		currentImportFilterTextField = new JTextField();
		currentImportFilterTextField.setEditable(false);
		currentImportFilterTextField.setColumns(10);
		
		excludeFilterButton = new JButton("...");
		GroupLayout gl_panel_6 = new GroupLayout(panel_6);
		gl_panel_6.setHorizontalGroup(
			gl_panel_6.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_6.createSequentialGroup()
					.addGap(12)
					.addComponent(lblExclude)
					.addGap(5)
					.addComponent(currentImportFilterTextField)
					.addGap(5)
					.addComponent(excludeFilterButton)
					.addGap(12))
		);
		gl_panel_6.setVerticalGroup(
			gl_panel_6.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_6.createSequentialGroup()
					.addGap(9)
					.addComponent(lblExclude))
				.addGroup(gl_panel_6.createSequentialGroup()
					.addGap(6)
					.addComponent(currentImportFilterTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panel_6.createSequentialGroup()
					.addGap(5)
					.addComponent(excludeFilterButton))
		);
		panel_6.setLayout(gl_panel_6);
		
		JPanel topPanel = new JPanel();
		
		JLabel lblImportDirectory = new JLabel("Import directory:");
		
		currentDirectoryTextField = new JTextField();
		currentDirectoryTextField.setColumns(10);
		currentDirectoryTextField.setEditable(false);
		
		btnChange = new JButton("Change...");
		
		JPanel buttonBar = new JPanel();
		buttonBar.setBorder(new EmptyBorder(5, 0, 5, 0));
		
		btnOk = new JButton("OK");
		
		btnCancel = new JButton("Cancel");
		
		JButton btnHelp = new JButton("Help");
		GroupLayout gl_buttonBar = new GroupLayout(buttonBar);
		gl_buttonBar.setHorizontalGroup(
			gl_buttonBar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_buttonBar.createSequentialGroup()
					.addGap(96)
					.addComponent(btnOk)
					.addGap(79)
					.addComponent(btnCancel)
					.addGap(82)
					.addComponent(btnHelp)
					.addGap(94))
		);
		gl_buttonBar.setVerticalGroup(
			gl_buttonBar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_buttonBar.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_buttonBar.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCancel)
						.addComponent(btnOk)
						.addComponent(btnHelp)))
					.addGap(5)
		);
		buttonBar.setLayout(gl_buttonBar);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(topPanel, GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(leftPanel, GroupLayout.PREFERRED_SIZE, 411, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rightPanel, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
					.addGap(10))
				.addComponent(buttonBar, GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(topPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(rightPanel, GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
						.addComponent(leftPanel, GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE))
					.addComponent(buttonBar, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))
		);
		GroupLayout gl_topPanel = new GroupLayout(topPanel);
		gl_topPanel.setHorizontalGroup(
			gl_topPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_topPanel.createSequentialGroup()
					.addGap(10)
					.addComponent(lblImportDirectory)
					.addGap(5)
					.addComponent(currentDirectoryTextField, GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
					.addGap(12)
					.addComponent(btnChange)
					.addContainerGap())
		);
		gl_topPanel.setVerticalGroup(
			gl_topPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(lblImportDirectory, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
				.addGroup(gl_topPanel.createParallelGroup(Alignment.BASELINE)
					.addComponent(currentDirectoryTextField, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
					.addComponent(btnChange))
		);
		topPanel.setLayout(gl_topPanel);
		getContentPane().setLayout(groupLayout);
		setPreferredSize(new Dimension(600,600));
	}
	
	@Action
	public void directorySelected() {
		JFileChooser directorySelector = new JFileChooser(_currentDirectory);
		directorySelector.setDialogTitle(directoryChooserTitle);
		directorySelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		directorySelector.setAcceptAllFileFilterUsed(false);
		
		int result = directorySelector.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			_currentDirectory = directorySelector.getSelectedFile();
			updateModelForNewCurrentDirectory();
		}
	}
	
	@Action
	public void okPressed() {
		_okPressed = true;
		setVisible(false);
	}
	
	@Action
	public void cancelPressed() {
		_okPressed = false;
		setVisible(false);
	}
	
	public boolean proceed() {
		return _okPressed;
	}
	
	public void updateSelectedDirectiveType(JRadioButton selected) {
		if (selected.isSelected()) {
			_selectedDirectiveType = (DirectiveType)selected.getClientProperty(DirectiveType.class);
		}
	}
	
	public void moveFromPossibleToOther() {
		
		for (Object file : possibleDirectivesList.getSelectedValues()) {
			DirectiveFile directiveFile = new DirectiveFile((String)file, _selectedDirectiveType);
			
			_otherDirectivesFiles.add(directiveFile);
			_possibleDirectiveFiles.remove(file);
		}
		updateUI();
	}
	
	public void moveFromOtherToPossible() {
		
		for (Object file : otherDirectivesList.getSelectedValues()) {
			DirectiveFile directiveFile = (DirectiveFile)file;
			_possibleDirectiveFiles.add(directiveFile._fileName);
			_otherDirectivesFiles.remove(file);
		}
		updateUI();
	}
	
	public void moveToSpecs() {
		String file = (String)possibleDirectivesList.getSelectedValue();
		if (StringUtils.isNotEmpty(_specsFile)) {
			_possibleDirectiveFiles.add(_specsFile);
		}
		_possibleDirectiveFiles.remove(file);
		_specsFile = file;
		
		updateUI();
	}
	
	public void moveToChars() {
		String file = (String)possibleDirectivesList.getSelectedValue();
		if (StringUtils.isNotEmpty(_charactersFile)) {
			_possibleDirectiveFiles.add(_charactersFile);
		}
		_possibleDirectiveFiles.remove(file);
		_charactersFile = file;
		updateUI();
	}
	
	public void moveToItems() {
		String file = (String)possibleDirectivesList.getSelectedValue();
		if (StringUtils.isNotEmpty(_itemsFile)) {
			_possibleDirectiveFiles.add(_itemsFile);
		}
		_possibleDirectiveFiles.remove(file);
		_itemsFile = file;
		updateUI();
	}
	
	private void updateModelForNewCurrentDirectory() {
		_otherDirectivesFiles = new ArrayList<DirectiveFile>();
		_possibleDirectiveFiles = new ArrayList<String>();
		
		for (File file : _currentDirectory.listFiles(new ImportFileNameFilter(_currentFilter))) {
			_possibleDirectiveFiles.add(file.getName());
			
		}
		if (_possibleDirectiveFiles.contains(DEFAULT_SPECS_DIRECTIVE_FILE)) {
			_specsFile = DEFAULT_SPECS_DIRECTIVE_FILE;
			_possibleDirectiveFiles.remove(DEFAULT_SPECS_DIRECTIVE_FILE);
		}
		if (_possibleDirectiveFiles.contains(DEFAULT_CHARS_DIRECTIVE_FILE)) {
			_charactersFile = DEFAULT_CHARS_DIRECTIVE_FILE;
			_possibleDirectiveFiles.remove(DEFAULT_CHARS_DIRECTIVE_FILE);
		}
		if (_possibleDirectiveFiles.contains(DEFAULT_ITEMS_DIRECTIVE_FILE)) {
			_itemsFile = DEFAULT_ITEMS_DIRECTIVE_FILE;
			_possibleDirectiveFiles.remove(DEFAULT_ITEMS_DIRECTIVE_FILE);
		}
		
		updateUI();
		
	}
	
	private void updateUI() {
		currentDirectoryTextField.setText(_currentDirectory.getAbsolutePath());
		charactersFileTextField.setText(_charactersFile);
		itemsFileTextField.setText(_itemsFile);
		specificationsFileTextField.setText(_specsFile);
		currentImportFilterTextField.setText(_currentFilter);
		FilteredListModel possibleDirectivesModel = new FilteredListModel(_currentFilter);
		possibleDirectivesList.setModel(possibleDirectivesModel);
		
		DefaultListModel otherDirectivesModel = new DefaultListModel();
		for (DirectiveFile file : _otherDirectivesFiles) {
			otherDirectivesModel.addElement(file);
		}
		otherDirectivesList.setModel(otherDirectivesModel);
		
		
		btnOk.setEnabled((_currentDirectory != null) && 
			(!_otherDirectivesFiles.isEmpty() || StringUtils.isNotEmpty(_charactersFile) || StringUtils.isNotEmpty(_itemsFile)));
	}
	
	public List<DirectiveFile> getSelectedFiles() {
		DirectiveFile specsFile = new DirectiveFile(_specsFile, DirectiveType.CONFOR);
		DirectiveFile charsFile = new DirectiveFile(_charactersFile, DirectiveType.CONFOR);
		DirectiveFile itemsFile = new DirectiveFile(_itemsFile, DirectiveType.CONFOR);
		
		List<DirectiveFile> files = new ArrayList<DirectiveFile>();
		files.add(specsFile);
		files.add(charsFile);
		files.add(itemsFile);
		files.addAll(_otherDirectivesFiles);
		
		return files;
	}
	
	public File getSelectedDirectory() {
		return _currentDirectory;
	}
	
	@Action
	public void updateFilter() {
		
		String filter = MessageDialogHelper.showInputDialog(this, filterDialogTitle, filterDialogMessage, 35, _currentFilter);
		if (filter != null) {
			setCurrentFilter(filter);
		}
	}
	
	public void setCurrentFilter(String filter) {
		_currentFilter = filter;
		EditorPreferences.setImportFileFilter(filter);
		currentImportFilterTextField.setText(_currentFilter);
		
		updateUI();
	}
	
	private class FilteredListModel extends AbstractListModel {

		private static final long serialVersionUID = -2432156074008941418L;
		private List<String> _filteredList = new ArrayList<String>();
		
		public FilteredListModel(String filter) {
			ImportFileNameFilter importFilter = new ImportFileNameFilter(_currentFilter);
			for (String file : _possibleDirectiveFiles) {
				if (importFilter.accept(new File(file))) {
					_filteredList.add(file);
				}
			}
		}

		@Override
		public Object getElementAt(int index) {			
			return _filteredList.get(index);
		}

		@Override
		public int getSize() {
			return _filteredList.size();
		}
		
	}
}

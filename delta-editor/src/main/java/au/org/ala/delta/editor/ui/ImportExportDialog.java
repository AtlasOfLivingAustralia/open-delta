package au.org.ala.delta.editor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

/**
 * The ImportExportDialog is the user interface component that allows the user to:
 * 1) In import mode: select a set of DELTA directive files to import into the DELTA Editor.
 * 2) In export mode: export the DELTA Editor data set to DELTA directives files.
 */
public class ImportExportDialog extends JDialog {
	
	private static final String DEFAULT_ITEMS_DIRECTIVE_FILE = "items";
	private static final String DEFAULT_CHARS_DIRECTIVE_FILE = "chars";
	private static final String DEFAULT_SPECS_DIRECTIVE_FILE = "specs";

	private enum DirectiveType {
		CONFOR("C"), INTKEY("I"), DIST("D"), KEY("K"); 
		private String _abbreviation;
		private DirectiveType(String abbreviation) {
			_abbreviation = abbreviation;
		}
		public String getAbbreviation(){return _abbreviation;}
	};
	
	private class DirectiveFile {
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
	
	private static final long serialVersionUID = 8695641918190503720L;
	private JTextField currentDirectoryTextField;
	private JTextField specificationsFileTextField;
	private JTextField charactersFileTextField;
	private JTextField itemsFileTextField;
	private JTextField textField_4;
	private JList possibleDirectivesList;
	private JButton btnChange;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JList otherDirectivesList;
	
	public ImportExportDialog() {
		
		_specsFile = DEFAULT_SPECS_DIRECTIVE_FILE;
		_otherDirectivesFiles = new ArrayList<DirectiveFile>();
		_possibleDirectiveFiles = new ArrayList<String>();
		createUI();
		ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
		btnChange.setAction(actionMap.get("directorySelected"));
	}

	/**
	 * Creates and lays out the UI components for this dialog.
	 */
	private void createUI() {
		JPanel leftPanel = new JPanel();
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Specifications file", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Characters file", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Items file", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panel_3 = new JPanel();
		panel_3.setPreferredSize(new Dimension(150, 10));
		panel_3.setBorder(new TitledBorder(null, "Other directives files", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
		
		JButton button_3 = new JButton("<<");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				moveFromPossibleToOther();
			}
		});
		
		JButton button_4 = new JButton(">>");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Directive type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JRadioButton rdbtnConfor = new JRadioButton("Confor");
		buttonGroup.add(rdbtnConfor);
		
		JRadioButton rdbtnIntkey = new JRadioButton("Intkey");
		buttonGroup.add(rdbtnIntkey);
		
		JRadioButton rdbtnDist = new JRadioButton("Dist");
		buttonGroup.add(rdbtnDist);
		
		JRadioButton rdbtnKey = new JRadioButton("Key");
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
			gl_panel_7.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_7.createSequentialGroup()
					.addGap(30)
					.addGroup(gl_panel_7.createParallelGroup(Alignment.TRAILING)
						.addComponent(button_4)
						.addComponent(button_3))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_panel_7.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_panel_7.setVerticalGroup(
			gl_panel_7.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_7.createSequentialGroup()
					.addComponent(button_3)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(button_4)
					.addPreferredGap(ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
					.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		panel_7.setLayout(gl_panel_7);
		
		itemsFileTextField = new JTextField();
		itemsFileTextField.setColumns(10);
		
		JButton button_2 = new JButton("<<");
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addComponent(itemsFileTextField, GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
					.addGap(34)
					.addComponent(button_2)
					.addGap(29))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(itemsFileTextField, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(button_2))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		
		charactersFileTextField = new JTextField();
		charactersFileTextField.setColumns(10);
		
		JButton button_1 = new JButton("<<");
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addComponent(charactersFileTextField, GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
					.addGap(35)
					.addComponent(button_1)
					.addGap(26))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(charactersFileTextField, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(button_1))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		
		specificationsFileTextField = new JTextField();
		specificationsFileTextField.setColumns(10);
		
		JButton button = new JButton("<<");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(specificationsFileTextField, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
					.addGap(33)
					.addComponent(button)
					.addGap(26))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(specificationsFileTextField, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(button))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		leftPanel.setLayout(gl_leftPanel);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setName("directoryPanel");
		rightPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "Possible directive files", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		rightPanel.add(panel_5, BorderLayout.CENTER);
		panel_5.setLayout(new BorderLayout(0, 0));
		
		possibleDirectivesList = new JList();
		panel_5.add(new JScrollPane(possibleDirectivesList), BorderLayout.CENTER);
		
		JPanel panel_6 = new JPanel();
		panel_5.add(panel_6, BorderLayout.SOUTH);
		
		JLabel lblExclude = new JLabel("Exclude");
		
		textField_4 = new JTextField();
		textField_4.setColumns(10);
		
		JButton button_5 = new JButton("...");
		GroupLayout gl_panel_6 = new GroupLayout(panel_6);
		gl_panel_6.setHorizontalGroup(
			gl_panel_6.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_6.createSequentialGroup()
					.addGap(12)
					.addComponent(lblExclude)
					.addGap(5)
					.addComponent(textField_4)
					.addGap(5)
					.addComponent(button_5)
					.addGap(12))
		);
		gl_panel_6.setVerticalGroup(
			gl_panel_6.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_6.createSequentialGroup()
					.addGap(9)
					.addComponent(lblExclude))
				.addGroup(gl_panel_6.createSequentialGroup()
					.addGap(6)
					.addComponent(textField_4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panel_6.createSequentialGroup()
					.addGap(5)
					.addComponent(button_5))
		);
		panel_6.setLayout(gl_panel_6);
		
		JPanel topPanel = new JPanel();
		
		JLabel lblImportDirectory = new JLabel("Import directory:");
		
		currentDirectoryTextField = new JTextField();
		currentDirectoryTextField.setColumns(10);
		
		btnChange = new JButton("Change...");
		
		JPanel buttonBar = new JPanel();
		buttonBar.setBorder(new EmptyBorder(5, 0, 5, 0));
		
		JButton btnOk = new JButton("OK");
		
		JButton btnCnacel = new JButton("Cancel");
		
		JButton btnHelp = new JButton("Help");
		GroupLayout gl_buttonBar = new GroupLayout(buttonBar);
		gl_buttonBar.setHorizontalGroup(
			gl_buttonBar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_buttonBar.createSequentialGroup()
					.addGap(96)
					.addComponent(btnOk)
					.addGap(79)
					.addComponent(btnCnacel)
					.addGap(82)
					.addComponent(btnHelp)
					.addGap(94))
		);
		gl_buttonBar.setVerticalGroup(
			gl_buttonBar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_buttonBar.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_buttonBar.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCnacel)
						.addComponent(btnOk)
						.addComponent(btnHelp)))
		);
		buttonBar.setLayout(gl_buttonBar);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(topPanel, GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(leftPanel, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
					.addComponent(rightPanel, GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
					.addGap(10))
				.addComponent(buttonBar, GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
				    .addGap(5)
					.addComponent(topPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(leftPanel, GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
						.addComponent(rightPanel, GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE))
					.addComponent(buttonBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
	}
	
	@Action
	public void directorySelected() {
		JFileChooser directorySelector = new JFileChooser(_currentDirectory);
		directorySelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		directorySelector.setAcceptAllFileFilterUsed(false);
		int result = directorySelector.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			_currentDirectory = directorySelector.getSelectedFile();
			updateModelForNewCurrentDirectory();
		}
	}
	
	public void moveFromPossibleToOther() {
		
		DefaultListModel model = (DefaultListModel)possibleDirectivesList.getModel();
		for (Object file : possibleDirectivesList.getSelectedValues()) {
			DirectiveFile directiveFile = new DirectiveFile();
			directiveFile._fileName = (String)file;
			directiveFile._type = _selectedDirectiveType;
			
			_otherDirectivesFiles.add(directiveFile);
			model.removeElement(file);
		}
		updateUI();
	}
	
	private void updateModelForNewCurrentDirectory() {
		_otherDirectivesFiles = new ArrayList<DirectiveFile>();
		_possibleDirectiveFiles = new ArrayList<String>();
		for (File file : _currentDirectory.listFiles()) {
			_possibleDirectiveFiles.add(file.getName());
			
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
		DefaultListModel possibleDirectivesModel = new DefaultListModel();
		for (String file : _possibleDirectiveFiles) {
			possibleDirectivesModel.addElement(file);
		}
		possibleDirectivesList.setModel(possibleDirectivesModel);
		
		DefaultListModel otherDirectivesModel = new DefaultListModel();
		for (DirectiveFile file : _otherDirectivesFiles) {
			otherDirectivesModel.addElement(file);
		}
		otherDirectivesList.setModel(otherDirectivesModel);
	}
}

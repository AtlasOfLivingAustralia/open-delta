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
package au.org.ala.delta.editor.directives.ui;

import au.org.ala.delta.editor.EditorPreferences;
import au.org.ala.delta.editor.directives.DirectiveFileInfo;
import au.org.ala.delta.editor.directives.ImportFileNameFilter;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.ui.MessageDialogHelper;
import au.org.ala.delta.ui.util.IconHelper;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The ImportExportDialog is the user interface component that allows the user to: 1) In import mode: select a set of DELTA directive files to import into the DELTA Editor. 2) In export mode: export
 * the DELTA Editor data set to DELTA directives files.
 */
public class ImportExportDialog extends JDialog {

	private ImportExportViewModel _model;
	private boolean _okPressed;
	private String _currentFilter;
	private ResourceMap _resources;
	private ActionMap _actionMap;

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

	private String filterDialogTitle;
	private String filterDialogMessage;
	private String dialogTitle;
	private String specificationsPanelTitle;
	private String charsPanelTitle;
	private String itemsPanelTitle;
	private String otherPanelTitle;
	private String possiblePanelTitle;
	private String directiveTypePanelTitle;
	private String directoryLabelText;

	private JButton moveToSpecsButton;
	private JButton moveToCharsButton;
	private JButton moveToItemsButton;
	private JTextField specificationsFileTextField;
	private JButton excludeFilterButton;
	private JTextField currentImportFilterTextField;
	private JLabel lblNewLabel;
	private JComboBox cmbLineSeparator;
	private JPanel pnlLineSeparator;

    private boolean _importMode;

	public ImportExportDialog(Window parent, ImportExportViewModel model, String resourcePrefix) {

		super(parent);
		_model = model;
		setName("ImportExportDialogBox");
        _importMode = resourcePrefix.contains("Import");

        _resources = Application.getInstance().getContext().getResourceMap(ImportExportDialog.class);
		loadResources(resourcePrefix);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		createUI();


        if (_importMode) {
			pnlLineSeparator.setVisible(false);
		} else {
			// Line endings...
			DefaultComboBoxModel lsmodel = new DefaultComboBoxModel(new LineSeparatorViewModel[] { 
					new LineSeparatorViewModel(System.getProperty("line.separator"), _resources.getString(resourcePrefix + ".lineSeparator.system.default")),
					new LineSeparatorViewModel("\n", _resources.getString(resourcePrefix + ".lineSeparator.unix")), 
					new LineSeparatorViewModel("\r\n", _resources.getString(resourcePrefix + ".lineSeparator.windows")) 
			});

			cmbLineSeparator.setModel(lsmodel);

			cmbLineSeparator.setSelectedIndex(0);
		}

		_currentFilter = EditorPreferences.getImportFileFilter();

		addEventListeners();

		updateUI();
	}

	private void loadResources(String resourcePrefix) {
		filterDialogTitle = _resources.getString(resourcePrefix + ".filterDialogTitle");
		filterDialogMessage = _resources.getString(resourcePrefix + ".filterDialogMessage");
		dialogTitle = _resources.getString(resourcePrefix + ".dialogTitle");
		specificationsPanelTitle = _resources.getString(resourcePrefix + ".specificationsPanelTitle");
		charsPanelTitle = _resources.getString(resourcePrefix + ".charsPanelTitle");
		itemsPanelTitle = _resources.getString(resourcePrefix + ".itemsPanelTitle");
		otherPanelTitle = _resources.getString(resourcePrefix + ".otherPanelTitle");
		possiblePanelTitle = _resources.getString(resourcePrefix + ".possiblePanelTitle");
		directiveTypePanelTitle = _resources.getString(resourcePrefix + ".directiveTypePanelTitle");
		directoryLabelText = _resources.getString(resourcePrefix + ".directoryLabel");
	}

	private void addEventListeners() {
		_actionMap = Application.getInstance().getContext().getActionMap(this);
		btnChange.setAction(_actionMap.get("directorySelected"));
		btnOk.setAction(_actionMap.get("okPressed"));
		btnOk.setEnabled(false);
		btnCancel.setAction(_actionMap.get("cancelPressed"));
		excludeFilterButton.setAction(_actionMap.get("updateFilter"));

		ActionListener typeSelectionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSelectedDirectiveType((JRadioButton) e.getSource());
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

		pnlLineSeparator = new JPanel();
		pnlLineSeparator.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout gl_leftPanel = new GroupLayout(leftPanel);
		gl_leftPanel.setHorizontalGroup(gl_leftPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_leftPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_leftPanel
										.createParallelGroup(Alignment.LEADING)
										.addComponent(pnlLineSeparator, GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
										.addComponent(panel_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
										.addGroup(
												gl_leftPanel
														.createSequentialGroup()
														.addGroup(
																gl_leftPanel.createParallelGroup(Alignment.TRAILING)
																		.addComponent(panel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
																		.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
																		.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)).addGap(1)))));
		gl_leftPanel.setVerticalGroup(gl_leftPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_leftPanel.createSequentialGroup().addGap(5).addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(6)
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_3, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(pnlLineSeparator, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)));

		lblNewLabel = new JLabel();
        lblNewLabel.setName("exportLineSeparator");

		cmbLineSeparator = new JComboBox();
		GroupLayout gl_pnlLineSeparator = new GroupLayout(pnlLineSeparator);
		gl_pnlLineSeparator.setHorizontalGroup(gl_pnlLineSeparator.createParallelGroup(Alignment.LEADING).addGroup(
				gl_pnlLineSeparator.createSequentialGroup().addContainerGap().addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(cmbLineSeparator, 0, 271, Short.MAX_VALUE).addContainerGap()));
		gl_pnlLineSeparator.setVerticalGroup(gl_pnlLineSeparator.createParallelGroup(Alignment.TRAILING).addGroup(
				gl_pnlLineSeparator
						.createSequentialGroup()
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(
								gl_pnlLineSeparator.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel)
										.addComponent(cmbLineSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addContainerGap()));
		pnlLineSeparator.setLayout(gl_pnlLineSeparator);
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
		gl_panel_4.setHorizontalGroup(gl_panel_4.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel_4.createSequentialGroup()
						.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING).addComponent(rdbtnConfor).addComponent(rdbtnIntkey).addComponent(rdbtnDist).addComponent(rdbtnKey))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		gl_panel_4.setVerticalGroup(gl_panel_4.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel_4.createSequentialGroup().addComponent(rdbtnConfor).addPreferredGap(ComponentPlacement.RELATED).addComponent(rdbtnIntkey).addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(rdbtnDist).addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(rdbtnKey)));
		panel_4.setLayout(gl_panel_4);
		GroupLayout gl_panel_7 = new GroupLayout(panel_7);
		gl_panel_7.setHorizontalGroup(gl_panel_7.createParallelGroup(Alignment.TRAILING).addGroup(
				gl_panel_7
						.createSequentialGroup()
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(
								gl_panel_7
										.createParallelGroup(Alignment.TRAILING)
										.addGroup(gl_panel_7.createSequentialGroup().addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE).addGap(5))
										.addGroup(
												gl_panel_7.createSequentialGroup()
														.addGroup(gl_panel_7.createParallelGroup(Alignment.LEADING).addComponent(moveToPossibleButton).addComponent(moveToOtherButton)).addGap(50)))));
		gl_panel_7.setVerticalGroup(gl_panel_7.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel_7.createSequentialGroup().addComponent(moveToOtherButton).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(moveToPossibleButton)
						.addPreferredGap(ComponentPlacement.RELATED, 114, Short.MAX_VALUE).addComponent(panel_4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		panel_7.setLayout(gl_panel_7);

		itemsFileTextField = new JTextField();
		itemsFileTextField.setColumns(10);
		itemsFileTextField.setEditable(false);

        String moveSpecsCharsItemsButtonText = _importMode ? "<<" : ">>";
		moveToItemsButton = new JButton(moveSpecsCharsItemsButtonText);
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(gl_panel_2.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel_2.createSequentialGroup().addComponent(itemsFileTextField, GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE).addGap(59).addComponent(moveToItemsButton).addGap(50)));
		gl_panel_2.setVerticalGroup(gl_panel_2.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel_2
						.createSequentialGroup()
						.addGroup(
								gl_panel_2.createParallelGroup(Alignment.BASELINE).addComponent(itemsFileTextField, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
										.addComponent(moveToItemsButton)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		panel_2.setLayout(gl_panel_2);

		charactersFileTextField = new JTextField();
		charactersFileTextField.setColumns(10);
		charactersFileTextField.setEditable(false);

		moveToCharsButton = new JButton(moveSpecsCharsItemsButtonText);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING).addGroup(
				gl_panel_1.createSequentialGroup().addComponent(charactersFileTextField, GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE).addGap(58).addComponent(moveToCharsButton).addGap(50)));
		gl_panel_1.setVerticalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel_1
						.createSequentialGroup()
						.addGroup(
								gl_panel_1.createParallelGroup(Alignment.BASELINE).addComponent(charactersFileTextField, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
										.addComponent(moveToCharsButton)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		panel_1.setLayout(gl_panel_1);

		specificationsFileTextField = new JTextField();
		specificationsFileTextField.setColumns(10);
		specificationsFileTextField.setEditable(false);

		moveToSpecsButton = new JButton(moveSpecsCharsItemsButtonText);

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(
				gl_panel.createSequentialGroup().addComponent(specificationsFileTextField, GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE).addGap(57).addComponent(moveToSpecsButton).addGap(50)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup()
						.addGroup(
								gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(specificationsFileTextField, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
										.addComponent(moveToSpecsButton)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);
		leftPanel.setLayout(gl_leftPanel);

		JPanel rightPanel = new JPanel();
		rightPanel.setName("directoryPanel");

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, possiblePanelTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_5.setLayout(new BorderLayout(0, 0));

		possibleDirectivesList = new JList();
		panel_5.add(new JScrollPane(possibleDirectivesList), BorderLayout.CENTER);

		JPanel panel_6 = new JPanel();
		panel_5.add(panel_6, BorderLayout.SOUTH);

		JLabel lblExclude = new JLabel();
        lblExclude.setName("importExportExcludeLabel");

		currentImportFilterTextField = new JTextField();
		currentImportFilterTextField.setEditable(false);
		currentImportFilterTextField.setColumns(10);

		excludeFilterButton = new JButton("...");
		GroupLayout gl_panel_6 = new GroupLayout(panel_6);
		gl_panel_6.setHorizontalGroup(gl_panel_6.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel_6.createSequentialGroup().addGap(2).addComponent(lblExclude).addGap(5).addComponent(currentImportFilterTextField, GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE).addGap(5)
						.addComponent(excludeFilterButton).addGap(2)));
		gl_panel_6.setVerticalGroup(gl_panel_6.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_6.createSequentialGroup().addGap(9).addComponent(lblExclude))
				.addGroup(gl_panel_6.createSequentialGroup().addGap(6).addComponent(currentImportFilterTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panel_6.createSequentialGroup().addGap(5).addComponent(excludeFilterButton)));
		panel_6.setLayout(gl_panel_6);

		JPanel topPanel = new JPanel();

		JLabel lblImportDirectory = new JLabel(directoryLabelText);

		currentDirectoryTextField = new JTextField();
		currentDirectoryTextField.setColumns(10);
		currentDirectoryTextField.setEditable(false);

		btnChange = new JButton();

		JPanel buttonBar = new JPanel();
		buttonBar.setBorder(new EmptyBorder(5, 0, 5, 0));

		btnOk = new JButton();

		btnCancel = new JButton();

		GroupLayout gl_buttonBar = new GroupLayout(buttonBar);
		gl_buttonBar.setHorizontalGroup(gl_buttonBar.createParallelGroup(Alignment.TRAILING).addGroup(
				gl_buttonBar.createSequentialGroup().addContainerGap(398, Short.MAX_VALUE).addComponent(btnOk).addGap(5).addComponent(btnCancel).addContainerGap()));
		gl_buttonBar.setVerticalGroup(gl_buttonBar.createParallelGroup(Alignment.TRAILING).addGroup(
				gl_buttonBar.createSequentialGroup().addGap(5).addGroup(gl_buttonBar.createParallelGroup(Alignment.BASELINE).addComponent(btnCancel).addComponent(btnOk))));
		buttonBar.setLayout(gl_buttonBar);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout
				.createParallelGroup(Alignment.LEADING)
				.addComponent(topPanel, GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
				.addGroup(
						groupLayout.createSequentialGroup().addComponent(leftPanel, GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(rightPanel, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE).addContainerGap()).addComponent(buttonBar, GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE));
		groupLayout
				.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
						groupLayout
								.createSequentialGroup()
								.addGap(5)
								.addComponent(topPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGroup(
										groupLayout.createParallelGroup(Alignment.LEADING).addComponent(leftPanel, GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
												.addComponent(rightPanel, GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE))
								.addComponent(buttonBar, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)));
		GroupLayout gl_rightPanel = new GroupLayout(rightPanel);
		gl_rightPanel.setHorizontalGroup(gl_rightPanel.createParallelGroup(Alignment.LEADING).addComponent(panel_5, GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE));
		gl_rightPanel.setVerticalGroup(gl_rightPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_rightPanel.createSequentialGroup().addGap(5).addComponent(panel_5, GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)));
		rightPanel.setLayout(gl_rightPanel);
		GroupLayout gl_topPanel = new GroupLayout(topPanel);
		gl_topPanel.setHorizontalGroup(gl_topPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_topPanel.createSequentialGroup().addGap(10).addComponent(lblImportDirectory).addGap(5).addComponent(currentDirectoryTextField, GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
						.addGap(12).addComponent(btnChange).addContainerGap()));
		gl_topPanel
				.setVerticalGroup(gl_topPanel
						.createParallelGroup(Alignment.LEADING)
						.addComponent(lblImportDirectory, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addGroup(
								gl_topPanel.createParallelGroup(Alignment.BASELINE).addComponent(currentDirectoryTextField, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
										.addComponent(btnChange)));
		topPanel.setLayout(gl_topPanel);
		getContentPane().setLayout(groupLayout);
		setPreferredSize(new Dimension(600, 600));
	}

	public void setDirectorySelectionAction(javax.swing.Action action) {
		btnChange.setAction(action);
	}
	
	public String getLineSeparator() {
		if (cmbLineSeparator.getSelectedItem() == null) {
			return System.getProperty("line.separator");
		}
		
		LineSeparatorViewModel vm = (LineSeparatorViewModel) cmbLineSeparator.getSelectedItem();
		return vm.getLineSeparator();	
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
			_model.setSelectedDirectiveType((DirectiveType) selected.getClientProperty(DirectiveType.class));
		}
	}

	public void moveFromPossibleToOther() {

		for (Object file : possibleDirectivesList.getSelectedValues()) {
			_model.include((DirectiveFileInfo) file);
		}
		updateUI();
	}

	public void moveFromOtherToPossible() {

		for (Object file : otherDirectivesList.getSelectedValues()) {
			_model.exclude((DirectiveFileInfo) file);
		}
		updateUI();
	}

	public void moveToSpecs() {
        DirectiveFileInfo file = null;
        if (_importMode) {
		    file = (DirectiveFileInfo) possibleDirectivesList.getSelectedValue();
        }
        _model.moveToSpecs(file);
		updateUI();
	}

	public void moveToChars() {
        DirectiveFileInfo file = null;

        if (_importMode) {
		    file = (DirectiveFileInfo) possibleDirectivesList.getSelectedValue();
        }
        _model.moveToChars(file);
		updateUI();
	}

	public void moveToItems() {
        DirectiveFileInfo file = null;

        if (_importMode) {
            file = (DirectiveFileInfo) possibleDirectivesList.getSelectedValue();

        }
        _model.moveToItems(file);
		updateUI();
	}

	public void updateUI() {
		File directory = _model.getCurrentDirectory();
		String path = "";
		if (directory != null) {
			path = directory.getAbsolutePath();
		}
		currentDirectoryTextField.setText(path);
		DirectiveFileInfo charsFile = _model.getCharactersFile();
		if (charsFile != null) {
			charactersFileTextField.setText(charsFile.getFileName());
		}
        else {
            charactersFileTextField.setText("");
        }
		DirectiveFileInfo itemsFile = _model.getItemsFile();
		if (itemsFile != null) {
			itemsFileTextField.setText(itemsFile.getFileName());
		}
        else {
            itemsFileTextField.setText("");
        }
		DirectiveFileInfo specsFile = _model.getSpecsFile();
		if (specsFile != null) {
			specificationsFileTextField.setText(specsFile.getFileName());
		}
        else {
            specificationsFileTextField.setText("");
        }
		if (_model.isSpecsDisabled()) {
			charactersFileTextField.setEnabled(false);
			specificationsFileTextField.setEnabled(false);
			moveToSpecsButton.setEnabled(false);
			moveToCharsButton.setEnabled(false);
		}

		currentImportFilterTextField.setText(_currentFilter);
		FilteredListModel possibleDirectivesModel = new FilteredListModel(_currentFilter);
		possibleDirectivesList.setModel(possibleDirectivesModel);

		DefaultListModel otherDirectivesModel = new DefaultListModel();
		for (DirectiveFileInfo file : _model.getIncludedDirectivesFiles()) {
			otherDirectivesModel.addElement(file);
		}
		otherDirectivesList.setModel(otherDirectivesModel);

		btnOk.setEnabled(_model.isImportable());
	}

	@Override
	public void setVisible(boolean visible) {
		if (!isVisible() && visible == true) {
			if (_model.getCurrentDirectory() == null) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						btnChange.getAction().actionPerformed(new ActionEvent(this, 0, ""));
					}
				});
			}
		}
		super.setVisible(visible);

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
		private List<DirectiveFileInfo> _filteredList = new ArrayList<DirectiveFileInfo>();

		public FilteredListModel(String filter) {
			ImportFileNameFilter importFilter = new ImportFileNameFilter(_currentFilter);
			for (DirectiveFileInfo file : _model.getExcludedDirectiveFiles()) {
				if (importFilter.accept(new File(file.getFileName()))) {
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

	class LineSeparatorViewModel {

		private String _lineEnding;
		private String _description;

		public LineSeparatorViewModel(String lineEnding, String description) {
			_lineEnding = lineEnding;
			_description = description;
		}

		public String getDescription() {
			return _description;
		}

		public String getLineSeparator() {
			return _lineEnding;
		}

		@Override
		public String toString() {
			return _description + " (" + describeLineSeparator() + ")";
		}

		private String describeLineSeparator() {
			StringBuilder b = new StringBuilder();

			for (int i = 0; i < _lineEnding.length(); ++i) {
				char ch = _lineEnding.charAt(i);
				switch (ch) {
				case '\n':
					b.append("\\n");
					break;
				case '\r':
					b.append("\\r");
					break;
				default:
					b.append(String.format("0x%02x", (int) ch));
				}

			}

			return b.toString();
		}
	}
}

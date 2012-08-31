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
package au.org.ala.delta.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.model.SearchDirection;
import au.org.ala.delta.ui.util.UIUtils;

public class SearchReplaceDialog extends BaseDialog {

	private static final long serialVersionUID = 1L;

	private javax.swing.JRadioButton backwardRadioButton;
	private JCheckBox caseSensitiveCheck;
	private JButton closeButton;
	private javax.swing.ButtonGroup directionButtonGroup;
	private JPanel directionPanel;
	private JButton findButton;
	private JComboBox findCombo;
	private JLabel findLabel;
	private javax.swing.JRadioButton forwardRadioButton;
	private JPanel aaPanel1;
	private JPanel mainPanel;
	private JPanel optionsPanel;
	private JButton replaceAllButton;
	private JButton replaceButton;
	private JButton replaceFindButton;
	private JLabel replaceLabel;
	private JComboBox replaceWithCombo;
	private JCheckBox wrapSearchCheck;
	private SearchAndReplaceController searchableComponent;
	private ResourceMap _messages;

	/**
	 * Constructs a SearchReplaceDialog instance with specific attributes.
	 * 
	 * @param searchableComponent
	 *            The text area.
	 */
	public SearchReplaceDialog(SearchAndReplaceController searchableComponent) {
		super(UIUtils.getParentFrame(searchableComponent.getEditorComponent()));
		
		SingleFrameApplication application = (SingleFrameApplication) Application.getInstance();
		_messages = application.getContext().getResourceMap();
		
		setTitle(_messages.getString("searchReplaceDialog.title"));
		
		setName("searchReplaceDialog");		
		setResizable(false);	
		hookInternalFrame(searchableComponent.getEditorComponent());
		this.searchableComponent = searchableComponent;
		initComponents();
		forwardRadioButton.setSelected(true);
		wrapSearchCheck.setSelected(true);

		// defining key bindings
		InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");

		ActionMap actionMap = this.getRootPane().getActionMap();
		actionMap.put("escape", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt) {
				closeButtonActionPerformed(evt);
			}
		});
		actionMap.put("enter", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt) {
				findButtonActionPerformed(evt);
			}
		});

		// centering dialog over text area
		UIUtils.centerDialog(this, searchableComponent.getEditorComponent());
	}

	public SearchAndReplaceController getSearchableComponent() {
		return searchableComponent;
	}

	/**
	 * Gets the text to find.
	 * 
	 * @return The text to find.
	 */
	public String getFindText() {
		String findText = findCombo.getEditor().getItem().toString();
		return findText;
	}

	/**
	 * Sets the text to find.
	 * 
	 * @param findText
	 *            The new text to find.
	 */
	public void setFindText(String findText) {
		updateComboBox(findCombo, findText);
	}

	/**
	 * Gets the find direction.
	 * 
	 * @return The current find direction.
	 */
	public SearchDirection getFindDirection() {
		if (backwardRadioButton.isSelected()) {
			return SearchDirection.Backward;
		}
		return SearchDirection.Forward;
	}

	/**
	 * Gets the case sensitive search flag.
	 * 
	 * @return The case sensitive flag specified through the find dialog.
	 */
	public boolean isCaseSensitiveSearch() {
		return caseSensitiveCheck.isSelected();
	}

	/**
	 * Gets the wrapped search flag.
	 * 
	 * @return The wrapped search flag specified through the find dialog.
	 */
	public boolean isWrappedSearch() {
		return wrapSearchCheck.isSelected();
	}

	/**
	 * @see java.awt.Dialog#show()
	 */
	@SuppressWarnings("deprecation")
	public void show() {
		super.show();
		findCombo.requestFocus();
	}

	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;
		directionButtonGroup = new javax.swing.ButtonGroup();
		aaPanel1 = new JPanel();
		findButton = new JButton();
		replaceFindButton = new JButton();
		replaceButton = new JButton();
		replaceAllButton = new JButton();
		closeButton = new JButton();
		mainPanel = new JPanel();
		findLabel = new JLabel();
		replaceLabel = new JLabel();
		findCombo = new JComboBox();
		replaceWithCombo = new JComboBox();
		directionPanel = new JPanel();
		forwardRadioButton = new javax.swing.JRadioButton();
		backwardRadioButton = new javax.swing.JRadioButton();
		optionsPanel = new JPanel();
		caseSensitiveCheck = new JCheckBox();
		wrapSearchCheck = new JCheckBox();
		aaPanel1.setLayout(new java.awt.GridBagLayout());

		findButton.setMnemonic('n');
		findButton.setText(_messages.getString("searchReplaceDialog.findButton"));
		findButton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent evt) {
				findButtonActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
		aaPanel1.add(findButton, gridBagConstraints);

		replaceFindButton.setMnemonic('d');
		replaceFindButton.setText(_messages.getString("searchReplaceDialog.replaceFindButton"));
		replaceFindButton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent evt) {
				replaceFindButtonActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
		aaPanel1.add(replaceFindButton, gridBagConstraints);

		replaceButton.setMnemonic('R');
		replaceButton.setText(_messages.getString("searchReplaceDialog.replaceButton"));
		replaceButton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent evt) {
				replaceButtonActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
		aaPanel1.add(replaceButton, gridBagConstraints);

		replaceAllButton.setMnemonic('A');
		replaceAllButton.setText(_messages.getString("searchReplaceDialog.replaceAllButton"));
		replaceAllButton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent evt) {
				replaceAllButtonActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
		aaPanel1.add(replaceAllButton, gridBagConstraints);

		closeButton.setText(_messages.getString("searchReplaceDialog.closeButton"));
		closeButton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeButtonActionPerformed(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.ipadx = 5;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
		gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
		aaPanel1.add(closeButton, gridBagConstraints);

		getContentPane().add(aaPanel1, java.awt.BorderLayout.SOUTH);

		mainPanel.setLayout(new java.awt.GridBagLayout());

		findLabel.setText(_messages.getString("searchReplaceDialog.findLabel"));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
		mainPanel.add(findLabel, gridBagConstraints);

		replaceLabel.setText(_messages.getString("searchReplaceDialog.replaceLabel"));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
		mainPanel.add(replaceLabel, gridBagConstraints);

		findCombo.setEditable(true);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 5);
		mainPanel.add(findCombo, gridBagConstraints);

		replaceWithCombo.setEditable(true);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 5);
		mainPanel.add(replaceWithCombo, gridBagConstraints);

		directionPanel.setLayout(new java.awt.GridBagLayout());

		directionPanel.setBorder(new javax.swing.border.TitledBorder(_messages.getString("searchReplaceDialog.directionPanelTitle")));
		forwardRadioButton.setText(_messages.getString("searchReplaceDialog.forwardRadioButton"));
		directionButtonGroup.add(forwardRadioButton);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.5;
		directionPanel.add(forwardRadioButton, gridBagConstraints);

		backwardRadioButton.setText(_messages.getString("searchReplaceDialog.backwardRadioButton"));
		directionButtonGroup.add(backwardRadioButton);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 0.5;
		directionPanel.add(backwardRadioButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
		mainPanel.add(directionPanel, gridBagConstraints);

		optionsPanel.setLayout(new java.awt.GridBagLayout());

		optionsPanel.setBorder(new javax.swing.border.TitledBorder(_messages.getString("searchReplaceDialog.optionsPanelTitle")));
		caseSensitiveCheck.setText(_messages.getString("searchReplaceDialog.caseSensitiveCheck"));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.5;
		optionsPanel.add(caseSensitiveCheck, gridBagConstraints);

		wrapSearchCheck.setText(_messages.getString("searchReplaceDialog.wrapSearchCheck"));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.weightx = 0.5;
		optionsPanel.add(wrapSearchCheck, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
		mainPanel.add(optionsPanel, gridBagConstraints);

		getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

		getRootPane().setDefaultButton(this.findButton);
		pack();
	}

	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.setVisible(false);
	}

	private SearchOptions createSearchOptions() {
		return new SearchOptions(getFindText(), getFindDirection(), isCaseSensitiveSearch(), isWrappedSearch());
	}

	private void replaceAllButtonActionPerformed(java.awt.event.ActionEvent evt) {
		updateComboBox(findCombo, getFindText());
		String textToReplaceWith = (String) replaceWithCombo.getSelectedItem();
		updateComboBox(replaceWithCombo, textToReplaceWith);
		searchableComponent.replaceAll(createSearchOptions(), textToReplaceWith);
	}

	private void replaceButtonActionPerformed(java.awt.event.ActionEvent evt) {
		String textToReplaceWith = (String) replaceWithCombo.getSelectedItem();
		updateComboBox(replaceWithCombo, textToReplaceWith);
		searchableComponent.replaceSelected(textToReplaceWith);
	}

	private void findButtonActionPerformed(java.awt.event.ActionEvent evt) {
		updateComboBox(findCombo, getFindText());
		searchableComponent.find(createSearchOptions());
	}

	private void replaceFindButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// replace step
		String textToReplaceWith = (String) replaceWithCombo.getSelectedItem();
		updateComboBox(replaceWithCombo, textToReplaceWith);
		searchableComponent.replaceSelected(textToReplaceWith);
		// find step
		String textToFind = getFindText();
		updateComboBox(findCombo, textToFind);
		searchableComponent.find(createSearchOptions());
	}

	/**
	 * Updates the list of the given combo box, puts the given latestEntry item on top of the list.
	 * 
	 * @param combo
	 *            The combo box to update.
	 * @param latestEntry
	 *            The lates entry to put on top of the selection list.
	 */
	private void updateComboBox(JComboBox combo, String latestEntry) {
		int numItems = combo.getItemCount();
		List<String> items = new ArrayList<String>();
		for (int i = 0; i < numItems; i++) {
			String current = (String) combo.getItemAt(i);
			if ((current != null) && !current.equals(latestEntry)) {
				items.add(current);
			}
		}
		numItems = items.size();
		combo.removeAllItems();
		combo.addItem(latestEntry);
		for (int i = 0; i < numItems; i++) {
			combo.addItem(items.get(i));
		}
	}

	public SearchOptions getSearchOptions() {
		return createSearchOptions();
	}

}

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

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * The DuplicateItemWarningDialog displays a dialog when a duplicate Item description is encountered during
 * a directives file import.
 */
public class DuplicateItemWarningDialog extends JDialog {
	
	private static final long serialVersionUID = -7426897304302709849L;
	private JTextField itemDescriptionField;
	private JButton overwriteButton;
	private JButton retainButton;
	private JCheckBox applyToAllCheckBox;
	
	private boolean _overwrite;
	private boolean _applyToAllItems;

	public DuplicateItemWarningDialog(String itemDescription) {
		super(((SingleFrameApplication)Application.getInstance()).getMainFrame());
		setModal(true);
        setName("duplicateItemWarningDialog");
        _overwrite = false;
        _applyToAllItems = false;
		createUI();
		itemDescriptionField.setText(itemDescription);
		addActionListeners();
	}
	
	private void addActionListeners() {
		ActionMap actions = Application.getInstance().getContext().getActionMap(this);
		overwriteButton.setAction(actions.get("overwriteItem"));
		retainButton.setAction(actions.get("retainItem"));
	}
	
	private void createUI() {
		JPanel topPanel = new JPanel();
		topPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(topPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
						.addComponent(bottomPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(topPanel, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
					.addGap(6)
					.addComponent(bottomPanel, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		bottomPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		bottomPanel.add(panel, BorderLayout.CENTER);
		
		retainButton = new JButton("");
		retainButton.setName("retainButton");
		
		overwriteButton = new JButton("");
		overwriteButton.setName("overwriteButton");
		
		JLabel retainButtonLabel = new JLabel("");
		retainButtonLabel.setName("retainButtonLabel");
		
		JLabel overwriteButtonLabel = new JLabel("");
		overwriteButtonLabel.setName("overwriteButtonLabel");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(retainButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(retainButtonLabel))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(overwriteButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(overwriteButtonLabel)))
					.addContainerGap(347, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                                .addComponent(retainButton)
                                .addComponent(retainButtonLabel))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
                                .addComponent(overwriteButton)
                                .addComponent(overwriteButtonLabel))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		bottomPanel.add(panel_1, BorderLayout.SOUTH);
		
		applyToAllCheckBox = new JCheckBox();
		applyToAllCheckBox.setName("applyToAllCheckBox");
		panel_1.add(applyToAllCheckBox);
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		bottomPanel.add(panel_2, BorderLayout.NORTH);
		
		JLabel actionDescriptionLabel = new JLabel("");
		panel_2.add(actionDescriptionLabel);
		actionDescriptionLabel.setName("actionDescriptionLabel");
		topPanel.setLayout(new BorderLayout(0, 0));
		
		itemDescriptionField = new JTextField();
		itemDescriptionField.setEditable(false);
		topPanel.add(itemDescriptionField, BorderLayout.NORTH);

		
		JTextArea textArea = new JTextArea();
		textArea.setColumns(0);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
		textArea.setName("duplicateItemWarningDescription");
		textArea.setOpaque(false);
		textArea.setEditable(false);
		topPanel.add(textArea, BorderLayout.CENTER);
		getContentPane().setLayout(groupLayout);
	}

    /**
     * Called when the user presses the "Overwrite" button.
     */
	@Action
	public void overwriteItem() {
		_overwrite = true;
		_applyToAllItems = applyToAllCheckBox.isSelected();
		setVisible(false);
	}

    /**
     * Called when the user presses the "Retain" button.
     */
	@Action
	public void retainItem() {
		_overwrite = false;
		_applyToAllItems = applyToAllCheckBox.isSelected();
		setVisible(false);
	}

    /**
     *
     * @return true if the user selected to overwrite the item description.
     */
	public boolean getOverwriteItem() {
		return _overwrite;
	}

    /**
     *
     * @return true if the user selected the "apply to all" checkbox.
     */
	public boolean getApplyToAll() {
		return _applyToAllItems;
	}
	
}

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
package au.org.ala.delta.ui.codeeditor;

import au.org.ala.delta.ui.BaseDialog;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;

/**
 * Displays a dialog asking the user which line they want to go to.
 */
public class GotoLineDialog extends BaseDialog {

	private static final long serialVersionUID = 1L;
	/** The text area. */
	private CodeTextArea textArea;

    private ResourceMap _resources;

	/**
	 * Sets the current line number.
	 * 
	 * @param lineNumber
	 *            The line number to set.
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumberField.setText(Integer.toString(lineNumber + 1));
	}

	/**
	 * Constructs a GotoLineDialog instance with specific attributes.
	 * 
	 * @param textArea
	 *            The text area.
	 */
	public GotoLineDialog(CodeTextArea textArea) {
		super(textArea.getFrame(), false);
        setName("gotoLineDialog");
        _resources = Application.getInstance().getContext().getResourceMap();
		this.textArea = textArea;
		setTitle(_resources.getString("gotoLineDialog.title"));
		initComponents();

		// defining key bindings
		InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");

		ActionMap actionMap = this.getRootPane().getActionMap();
		actionMap.put("escape", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});
		actionMap.put("enter", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});

		// centering dialog over text area
		centerDialog();
	}

	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		mainPanel = new JPanel();
		lineNumberLabel = new JLabel();
		lineNumberField = new JTextField();
		buttonPanel = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		addWindowListener(new WindowAdapter() {

			public void windowClosing(java.awt.event.WindowEvent evt) {
				closeDialog(evt);
			}
		});

		mainPanel.setLayout(new java.awt.GridBagLayout());

		lineNumberLabel.setText(_resources.getString("gotoLineDialog.lineNumberLabel.text"));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
		mainPanel.add(lineNumberLabel, gridBagConstraints);

		lineNumberField.addKeyListener(new java.awt.event.KeyAdapter() {

			public void keyReleased(java.awt.event.KeyEvent evt) {
				lineNumberFieldKeyReleased(evt);
			}
		});

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
		mainPanel.add(lineNumberField, gridBagConstraints);

		getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

		buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

		okButton.setText(_resources.getString("gotoLineDialog.okButton.text"));
		okButton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});

		buttonPanel.add(okButton);

		cancelButton.setText(_resources.getString("gotoLineDialog.cancelButton.text"));
		cancelButton.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		buttonPanel.add(cancelButton);

		getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

		pack();
	}

	private void lineNumberFieldKeyReleased(java.awt.event.KeyEvent evt) {
		checkLineNumberInput();
	}

	/**
	 * Checks if the input for the line number is valid.
	 * 
	 * @return true if the input is currently valid, else false.
	 */
	private boolean checkLineNumberInput() {
		String line = lineNumberField.getText();
		if (line.length() == 0) {
			lineNumberField.setForeground(Color.black);
			return true;
		}
		try {
			int lineNumber = Integer.parseInt(line);
			if ((lineNumber < 1) || (lineNumber > textArea.getLineCount())) {
				lineNumberField.setForeground(Color.red);
				return false;
			}
			lineNumberField.setForeground(Color.black);
		} catch (NumberFormatException ex) {
			lineNumberField.setForeground(Color.red);
			return false;
		}
		return true;
	}

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if (!checkLineNumberInput()) {
			return;
		}
		String line = lineNumberField.getText();
		try {
			int lineNumber = Integer.parseInt(line);
			textArea.gotoLine(lineNumber - 1);
			this.setVisible(false);
		} catch (NumberFormatException ex) {
			lineNumberField.setForeground(Color.red);
		}
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		this.setVisible(false);
	}

	private JPanel buttonPanel;
	private JButton cancelButton;
	private JTextField lineNumberField;
	private JLabel lineNumberLabel;
	private JPanel mainPanel;
	private JButton okButton;

}

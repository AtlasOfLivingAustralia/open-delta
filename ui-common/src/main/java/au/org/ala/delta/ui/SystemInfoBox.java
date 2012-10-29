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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.util.Utils;

public class SystemInfoBox extends JDialog {

	private static final long serialVersionUID = 1L;

	private String configDetails;
	
	@Resource
	String windowTitle;
	
	public SystemInfoBox(Dialog owner, String applicationName) {
		super(owner, true);
		
		ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
		ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(AboutBox.class);
		resourceMap.injectFields(this);
		
		this.configDetails = Utils.generateSystemInfo(applicationName);
		
		setTitle(windowTitle);
		this.setMinimumSize(new Dimension(800, 400));
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel pnlButton = new JPanel();
		pnlButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(pnlButton, BorderLayout.SOUTH);
		pnlButton.setLayout(new BorderLayout(0, 0));
		
		JButton btnCopyToClipboard = new JButton();
		btnCopyToClipboard.setAction(actionMap.get("copySystemInfoToClipboard"));
		pnlButton.add(btnCopyToClipboard, BorderLayout.WEST);
		
		JButton btnOK = new JButton();
		btnOK.setAction(actionMap.get("closeSystemInfoBox"));
		btnOK.setAlignmentX(Component.RIGHT_ALIGNMENT);
		pnlButton.add(btnOK, BorderLayout.EAST);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JTextArea textArea = new JTextArea();
		textArea.setText(this.configDetails);
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		//center the dialog on screen
		this.setLocationRelativeTo(owner);
	}
	
	@Action
	public void copySystemInfoToClipboard() {
		StringSelection selection = new StringSelection(this.configDetails);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
	}
	
	@Action
	public void closeSystemInfoBox() {
		this.dispose();
	}
	
}

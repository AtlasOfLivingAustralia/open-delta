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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.Logger;

public class LicenseInfoBox extends JDialog {
	
	private static final long serialVersionUID = 1L;

	@Resource
	String windowTitle;
	
	@Resource
	String licenseAttribution;
	
	@Resource
	String sourceCodeLocation;

	public LicenseInfoBox(Dialog owner) {
		super(owner, true);
		
		ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
		ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(AboutBox.class);
		resourceMap.injectFields(this);
		
		this.setTitle(windowTitle);
		
		this.setMinimumSize(new Dimension(800, 800));
		
		StringBuilder labelTextBuilder = new StringBuilder();
		labelTextBuilder.append("<html><center>");
		labelTextBuilder.append(licenseAttribution);
		labelTextBuilder.append("<br>");
		labelTextBuilder.append(sourceCodeLocation);
		labelTextBuilder.append("</center></html>");
		
		JLabel topLabel = new JLabel(labelTextBuilder.toString());
		topLabel.setFont(new Font(topLabel.getFont().getName(), topLabel.getFont().getStyle(), 14));
		topLabel.setBorder(BorderFactory.createEmptyBorder(20,0,20,0));

		JPanel pnlTop = new JPanel();
		pnlTop.add(topLabel, BorderLayout.CENTER);
		
		JTextArea textArea = new JTextArea(loadLicenseText());
		textArea.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
		
		
		JButton btnOK = new JButton();
		btnOK.setAction(actionMap.get("closeLicenseInfoBox"));
		
		JPanel pnlBottom = new JPanel();
		pnlBottom.setLayout(new BoxLayout(pnlBottom, BoxLayout.PAGE_AXIS));
		btnOK.setAlignmentX(RIGHT_ALIGNMENT);
		pnlBottom.add(btnOK, BorderLayout.WEST);
		pnlBottom.setBorder(BorderFactory.createEmptyBorder(20,0,20,20));
		
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		
		//center the dialog on screen
		this.setLocationRelativeTo(owner);
	}
	
	public String loadLicenseText() {
		
		StringBuilder licenseText = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(LicenseInfoBox.class.getResourceAsStream("/au/org/ala/delta/resources/MPL-1.1.txt")));
		String line;
		
		try {
			while ((line = in.readLine()) != null) {
				licenseText.append(line);
				licenseText.append("\n");
			}
		} catch (Exception ex) {
			Logger.log("Error while reading Mozilla License text from resource file: %s", ex.getMessage());
		}
		
		return licenseText.toString();
	}
	
	@Action
	public void closeLicenseInfoBox() {
		this.dispose();
	}
}

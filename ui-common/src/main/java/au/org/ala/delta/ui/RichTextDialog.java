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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import au.org.ala.delta.ui.rtf.RtfEditorPane;

/**
 * A dialog containing a read only RtfEditorPane in a scroll pane that 
 * can be used for displaying information to the user.
 */
public class RichTextDialog extends JDialog {

	private static final long serialVersionUID = 9109806191571551508L;

	private RtfEditorPane editor;
	private ActionMap _actionMap;
	
	public RichTextDialog(Window owner, String text) {
		super(owner);
		_actionMap = Application.getInstance().getContext().getActionMap(this);
		setLayout(new BorderLayout());
		editor = new RtfEditorPane();
		
		setPreferredSize(new Dimension(300, 300));
		
		editor.setEditable(false);
		editor.setBackground(Color.WHITE);
		editor.setBorder(null);
		editor.setOpaque(true);
		editor.setFont(UIManager.getFont("Label.font"));	
		editor.setText(text);	
	
		JScrollPane scroller = new JScrollPane(editor);
		scroller.setViewportBorder(null);
		scroller.setBorder(null);
		add(scroller, BorderLayout.CENTER);
		
		JPanel btnBar = new JPanel();
		btnBar.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JButton btnClose = new JButton();
		btnClose.setAction(_actionMap.get("closePressed"));
		btnBar.add(btnClose);
		add(btnBar, BorderLayout.SOUTH);
		
		setName("RichTextDialog");
	}
	
	@Action
	public void closePressed() {
		this.setVisible(false);
	}
	
	public void setText(String text) {
		editor.setText(text);
	}
}

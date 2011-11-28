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
import java.awt.Window;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;

/**
 * A dialog containing a read only EditorPane in a scroll pane that 
 * can be used for displaying plain text information to the user.
 */
public class TextFileViewer extends JDialog {

	private static final long serialVersionUID = 9109806191571551508L;

	private JTextPane viewer;
	public TextFileViewer(Window owner, File file) throws IOException {
		super(owner);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		viewer = new JTextPane();
		
		viewer.setEditable(false);
		viewer.setBackground(Color.WHITE);
		viewer.setBorder(null);
		viewer.setOpaque(true);
		viewer.setFont(UIManager.getFont("Label.font"));	
		
		JScrollPane scroller = new JScrollPane(viewer);
		getContentPane().add(scroller, BorderLayout.CENTER);
		
		setTitle(file.getAbsolutePath());
		displayFileContents(file);
		pack();
	}
	
	private void displayFileContents(File file) throws IOException {
		String text = FileUtils.readFileToString(file);
		viewer.setText(text);
	}
}

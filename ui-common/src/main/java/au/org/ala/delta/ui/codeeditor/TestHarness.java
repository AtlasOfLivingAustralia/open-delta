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

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.commons.io.FileUtils;

public class TestHarness extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws IOException {

		TestHarness frm = new TestHarness();

		frm.setVisible(true);
		frm.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public TestHarness() throws IOException {
		super();		
		this.setSize(new Dimension(400, 400));
		CodeTextArea editor = new CodeTextArea("text/confor");
		
		String d = FileUtils.readFileToString(new File("J:/grasses/items"));
		
		editor.setText(d);

		editor.setEOLMarkersPainted(false);
		editor.setShowLineNumbers(true);

		JScrollPane scroll = new JScrollPane(editor);
		this.getContentPane().add(scroll);
	}
}

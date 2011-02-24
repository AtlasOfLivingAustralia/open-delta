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
package au.org.ala.delta.editor.controller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.help.CSH;
import javax.help.DefaultHelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JRootPane;

public class HelpController {

	private DefaultHelpBroker helpBroker;

	
	public static final String GRID_VIEW_HELP_KEY = "gridview";
	public static final String TREE_VIEW_HELP_KEY = "treeview";
	
	
	public HelpController() {
		try {
			initialiseHelpSet("help/delta_editor/DeltaEditor");
			
		}
		catch (HelpSetException h) {
			throw new RuntimeException(h);
		}
	
	}
	
	public void setHelpKeyForComponent(JComponent component, String helpMapKey) {
		CSH.setHelpIDString(component, helpMapKey);
	}
	
	public void initialiseHelpSet(String name) throws HelpSetException {
		URL url = HelpSet.findHelpSet(getClass().getClassLoader(), name);
		HelpSet editorHelpSet = new HelpSet(getClass().getClassLoader(), url);
		
		helpBroker = (DefaultHelpBroker)editorHelpSet.createHelpBroker();	
		helpBroker.initPresentation();
		
	}
	
	public void enableHelpKey(JFrame frame) {
		final JRootPane rootPane = frame.getRootPane();
		
		helpBroker.enableHelpKey(rootPane, "top", helpBroker.getHelpSet());
		
		// This is a little dodgy, but we need to respond to help requests when the
		// Frame has focus, but JavaHelp by default creates a new help viewer for this 
		// situation which both impacts performance and makes it difficult to configure things
		// like the frame size.
		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				int code = e.getKeyCode();
				if (code == KeyEvent.VK_F1 || code == KeyEvent.VK_HELP) {
					new CSH.DisplayHelpFromSource(helpBroker).actionPerformed(
							new ActionEvent(rootPane, 0, ""));
				}
			}
			
		});
		
	}
	
	public ActionListener helpAction() {
		return new CSH.DisplayHelpFromSource(helpBroker);
	}
	
	public ActionListener helpOnSelectionAction() {
		return new CSH.DisplayHelpAfterTracking(helpBroker);
	}
}

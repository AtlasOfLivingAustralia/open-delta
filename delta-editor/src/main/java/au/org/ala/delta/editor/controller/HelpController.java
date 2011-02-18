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

import java.awt.event.ActionListener;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;

public class HelpController {

	private HelpBroker helpBroker;
	
	public HelpController() {
		try {
			initialiseHelpSet("help/delta_editor/DeltaEditor");
		}
		catch (HelpSetException h) {
			throw new RuntimeException(h);
		}
	}
	
	
	public void initialiseHelpSet(String name) throws HelpSetException {
		URL url = HelpSet.findHelpSet(getClass().getClassLoader(), name);
		HelpSet editorHelpSet = new HelpSet(getClass().getClassLoader(), url);
		
		helpBroker = editorHelpSet.createHelpBroker();
		
	}
	
	public ActionListener helpAction() {
		return new CSH.DisplayHelpFromSource(helpBroker);
	}
	
}

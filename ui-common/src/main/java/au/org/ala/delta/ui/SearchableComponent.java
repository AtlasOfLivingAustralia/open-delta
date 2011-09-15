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

import javax.swing.JComponent;

/**
 * If your component implements this interface than you can use the SearchReplaceDiaglog to coordinate searching! Neat!
 * 
 * @author baird
 *
 */
public interface SearchableComponent {
	
	/**
	 * The parent frame for the component
	 * @return
	 */
	JComponent getEditorComponent();
	
	/**
	 * Find the specified text 
	 * @param text
	 * @param options
	 */
	boolean find(String text, SearchOptions options);	
	
	/**
	 * Replace the currently selected text with the supplied text
	 * @param replacementText
	 * @param options
	 */
	void replaceSelected(String replacementText);
	
	/**
	 * Replace all instances of textToReplace with replacment text
	 * @param textToReplace
	 * @param replacementText
	 * @param options
	 */
	int replaceAll(String textToReplace, String replacementText, SearchOptions options);

}

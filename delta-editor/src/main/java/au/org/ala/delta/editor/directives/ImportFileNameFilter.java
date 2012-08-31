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
package au.org.ala.delta.editor.directives;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * A FileNameFilter that takes an exclusion pattern of the form:
 * pattern1;pattern2;pattern3...
 * The patterns are treated as literals except for the * character with is treated as a wildcard.
 */
public class ImportFileNameFilter extends FileFilter implements java.io.FileFilter {

	private String _description;
	private String[] _exclusionPatterns;
	
	public ImportFileNameFilter(String exclusionPatterns) {
		
		_description = exclusionPatterns;
		// Convert simple wildcard * into a regexp - convert '.' to a literal (as it will be 
		// a file name extension) and '*' into ".*"
		String tmp = exclusionPatterns.replace(".", "\\.");
		tmp = tmp.replace("*", ".*");
		_exclusionPatterns = tmp.split(";");
	}
	@Override
	public boolean accept(File file) {
		
		for (String pattern : _exclusionPatterns) {
			if (file.getName().matches("^"+pattern+"$")) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String getDescription() {
		return _description;
	}
	
	

	
}

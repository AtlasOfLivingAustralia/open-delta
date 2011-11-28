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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.model.MutableDeltaDataSet;


/**
 * Provides context for the import operation.  Keeps track of the current
 * directive and directive file.
 */
public class ImportContext extends DeltaContext {

	private DirectiveFile _currentFile;
	private Directive _currentDirective;
	
	public ImportContext(MutableDeltaDataSet dataSet) {
		super(dataSet);
	}
	
	public DirectiveFile getDirectiveFile() {
		return _currentFile;
	}
	
	public void setDirectiveFile(DirectiveFile file) {
		_currentFile = file;
	}
	
	public void setDirective(Directive directive) {
		_currentDirective = directive;
	}
	
	public Directive getDirective() {
		return _currentDirective;
	}
	
	
	
	
}

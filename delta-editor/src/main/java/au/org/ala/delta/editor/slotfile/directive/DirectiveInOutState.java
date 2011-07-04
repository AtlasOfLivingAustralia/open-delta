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
package au.org.ala.delta.editor.slotfile.directive;

import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.translation.Printer;

public class DirectiveInOutState {

	private DirectiveInstance _currentDirective;
	private Printer _printer;
	private DeltaDataSet _dataSet;
	
	public void setCurrentDirective(DirectiveInstance directive) {
		_currentDirective = directive;
	}
	
	public DirectiveInstance getCurrentDirective() {
		return _currentDirective;
	}
	
	public void setPrinter(Printer printer) {
		_printer = printer;
	}
	
	public Printer getPrinter() {
		return _printer;
	}
	
	public DeltaDataSet getDataSet() {
		return _dataSet;
	}
	
	public void setDataSet(DeltaDataSet dataSet) {
		_dataSet = dataSet;
	}
}

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

import java.io.PrintStream;

import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.translation.Printer;

public class DirectiveInOutState {

	private DirectiveInstance _currentDirective;
	private Printer _printer;
	private DeltaDataSet _dataSet;
	
	public DirectiveInOutState(DeltaDataSet dataSet) {
		_dataSet = dataSet;
	}
	
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
	
	/**
	 * Convenience method that creates a Printer from a PrintStream.
	 * @param out the print stream to export to.
	 */
	public void setPrintStream(PrintStream out) {
		Printer printer = new Printer(out, 80);
		printer.setIndentOnLineWrap(true);
		printer.setSoftWrap(true);
		printer.setIndent(2);
		printer.useParagraphIndentOnLineWrap();
		setPrinter(printer);
	}
	
	public DeltaDataSet getDataSet() {
		return _dataSet;
	}
	
	public void setDataSet(DeltaDataSet dataSet) {
		_dataSet = dataSet;
	}
	
	public void error(String messageKey) {
		
	}
	
	public void warning(String messageKey) {
		
	}
	
}

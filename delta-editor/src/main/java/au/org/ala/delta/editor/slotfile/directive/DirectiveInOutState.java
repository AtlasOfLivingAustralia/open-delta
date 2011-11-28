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

import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.translation.PrintFile;

public class DirectiveInOutState {

	public static final int DEFAULT_OUTPUT_WIDTH = 80;
	
	private DirectiveInstance _currentDirective;
	private PrintFile _printer;
	private MutableDeltaDataSet _dataSet;
	private boolean _newLineAfterAttributes;
	
	public DirectiveInOutState(MutableDeltaDataSet dataSet) {
		_dataSet = dataSet;
	}
	
	public void setCurrentDirective(DirectiveInstance directive) {
		_currentDirective = directive;
	}
	
	public DirectiveInstance getCurrentDirective() {
		return _currentDirective;
	}
	
	public void setPrinter(PrintFile printer) {
		_printer = printer;
	}
	
	public PrintFile getPrinter() {
		return _printer;
	}
	
	/**
	 * Convenience method that creates a Printer from a PrintStream.
	 * @param out the print stream to export to.
	 */
	public void setPrintStream(PrintStream out) {
		PrintFile printer = new PrintFile(out, DEFAULT_OUTPUT_WIDTH);
		printer.setIndentOnLineWrap(true);
		printer.setSoftWrap(true);
		printer.setIndent(2);
		printer.setUseParagraphIndentOnLineWrap(true);
	
		setPrinter(printer);
	}
	
	public MutableDeltaDataSet getDataSet() {
		return _dataSet;
	}
	
	public void setDataSet(MutableDeltaDataSet dataSet) {
		_dataSet = dataSet;
	}
	
	public void setNewLineAfterAttributes(boolean newLineAfterAttributes) {
		_newLineAfterAttributes = newLineAfterAttributes;
	}
	
	public boolean getNewLineAfterAttributes() {
		return _newLineAfterAttributes;
	}
	
	public void error(DirectiveException e) {
		
	}
	
	public void error(String messageKey) {
		
	}
	
	public void warning(String messageKey) {
		
	}
	
}

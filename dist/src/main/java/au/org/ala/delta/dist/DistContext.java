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
package au.org.ala.delta.dist;

import java.io.PrintStream;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.dist.io.DistOutputFileManager;

/**
 * A placeholder for state information provided by DIST specific directives.
 */
public class DistContext extends DeltaContext {

	private static final String DEFAULT_ITEMS_FILE_NAME = "ditems";
	
	private boolean _phylipFormat;
	private boolean _matchOverlap;
	private int _minimumNumberOfComparisons;
	private String _itemsFileName;
	
	public DistContext(PrintStream out, PrintStream err) {
    	super(out, err);
    	
    	_phylipFormat = false;
		_matchOverlap = false;
		_itemsFileName = DEFAULT_ITEMS_FILE_NAME;
    }
	
	public DistContext() {
		this(System.out, System.err);
	}

	public DistOutputFileManager getOutputFileManager() {
		return (DistOutputFileManager)_outputFileSelector;
	}

	public boolean isPhylipFormat() {
		return _phylipFormat;
	}
	
	public void usePhylipFormat() {
		_phylipFormat = true;
	}
	
	protected void createOutputFileManager() {
		_outputFileSelector = new DistOutputFileManager();
	}

	public void matchOverlap() {
		_matchOverlap = true;
	}
	
	public boolean getMatchOverlap() {
		return _matchOverlap;
	}
	
	public void setMinimumNumberOfComparisons(int minimumNumberOfComparisons) {
		_minimumNumberOfComparisons = minimumNumberOfComparisons;
	}
	
	public int getMinimumNumberOfComparisons() {
		return _minimumNumberOfComparisons;
	}
	
	public String getItemsFileName() {
		return _itemsFileName;
	}
	
	public void setItemsFileName(String fileName) {
		_itemsFileName = fileName;
	}
 }

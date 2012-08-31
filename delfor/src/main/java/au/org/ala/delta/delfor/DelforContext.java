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
package au.org.ala.delta.delfor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.delfor.format.CharacterExcluder;
import au.org.ala.delta.delfor.format.FormattingAction;
import au.org.ala.delta.delfor.format.ItemExcluder;
import au.org.ala.delta.editor.directives.ImportContext;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.util.Pair;

public class DelforContext extends ImportContext {

	private boolean _newLineForAttributes;
	private List<FormattingAction> _actions;
	private List<Pair<File, String>> _files;
	private boolean _exclusionsAdded;
	private String _nextOutputFileName;
	
	public DelforContext(MutableDeltaDataSet dataSet) {
		super(dataSet);
		
		_newLineForAttributes = false;
		_actions = new ArrayList<FormattingAction>();
		_files = new ArrayList<Pair<File, String>>();
		_exclusionsAdded = false;
	}

	public void newLineForAttributes() {
		_newLineForAttributes = true;
	}

	public boolean getNewLineForAttributes() {
		return _newLineForAttributes;
	}

	public void addFormattingAction(FormattingAction action) {
		_actions.add(action);
	}
	
	public List<FormattingAction> getFormattingActions() {
		if (!_exclusionsAdded) {
			addExclusions();
		}
		return _actions;
	}
	
	public void addReformatFile(File toReformat) {
		String outputFileName = toReformat.getName()+".new";
		if (StringUtils.isNotBlank(_nextOutputFileName)) {
			outputFileName = _nextOutputFileName;
			_nextOutputFileName = null;
		}
		_files.add(new Pair<File, String>(toReformat, outputFileName));
	}
	
	public List<Pair<File, String>> getFilesToReformat() {
		return _files;
	}
	
	public int getOutputWidth() {
		return getOutputFileSelector().getOutputWidth();
	}
	
	private void addExclusions() {
		if (!_excludedCharacters.isEmpty()) {
			_actions.add(new CharacterExcluder(new ArrayList<Integer>(_excludedCharacters)));
		}
		if (!_excludedItems.isEmpty()) {
			_actions.add(new ItemExcluder(new ArrayList<Integer>(_excludedItems)));
		}
		_exclusionsAdded = true;
	}
	

	public void setNextOutputFile(String filename) {
		_nextOutputFileName = filename;
	}
}

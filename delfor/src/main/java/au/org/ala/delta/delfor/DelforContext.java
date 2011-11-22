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

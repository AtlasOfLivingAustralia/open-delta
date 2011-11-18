package au.org.ala.delta.delfor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.delfor.format.FormattingAction;
import au.org.ala.delta.editor.directives.ImportContext;
import au.org.ala.delta.model.MutableDeltaDataSet;

public class DelforContext extends ImportContext {

	private boolean _newLineForAttributes;
	private List<FormattingAction> _actions;
	private List<File> _files;
	
	public DelforContext(MutableDeltaDataSet dataSet) {
		super(dataSet);
		
		_newLineForAttributes = false;
		_actions = new ArrayList<FormattingAction>();
		_files = new ArrayList<File>();
	}
	
	@Override
	public void setMaximumNumberOfItems(int items) {
		super.setMaximumNumberOfItems(items);
		
		for (int i=1; i<=items; i++) {
			getDataSet().addItem();
		}
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
		return _actions;
	}
	
	public void addReformatFile(File toReformat) {
		_files.add(toReformat);
	}
	
	public List<File> getFilesToReformat() {
		return _files;
	}
}

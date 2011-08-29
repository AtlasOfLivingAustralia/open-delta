package au.org.ala.delta.editor.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.model.DeltaDataSet;


/**
 * Provides context for the import operation.  Keeps track of the current
 * directive and directive file.
 */
public class ImportContext extends DeltaContext {

	private DirectiveFile _currentFile;
	private Directive _currentDirective;
	
	public ImportContext(DeltaDataSet dataSet) {
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

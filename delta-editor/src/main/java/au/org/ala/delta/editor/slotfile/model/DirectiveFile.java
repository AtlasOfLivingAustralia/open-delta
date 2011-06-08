package au.org.ala.delta.editor.slotfile.model;

import java.util.List;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.editor.directives.DirectiveArgConverter;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VODirFileDesc;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;

/**
 * Represents a file containing a list of DELTA directives.  DeltaDataSets can have
 * a set of directive files associated with them that 
 */
public class DirectiveFile {


	public static enum DirectiveType {
		CONFOR("C"), INTKEY("I"), DIST("D"), KEY("K"); 
		private String _abbreviation;
		private DirectiveType(String abbreviation) {
			_abbreviation = abbreviation;
		}
		public String getAbbreviation(){return _abbreviation;}
	};
	
	private VODirFileDesc _dirFileDesc;
	
	public DirectiveFile(VODirFileDesc dirFileDesc) {
		_dirFileDesc = dirFileDesc;
		
	}
	
	public DirectiveType getType() {
		return DirectiveType.CONFOR;
	}
	
	public String getFileName() {
		return _dirFileDesc.getFileName();
	}
	
	public void setFileType(short type) {
		_dirFileDesc.setFileType(type);
	}
	
	public void setProgramType(int type) {
		
	}
	
	public int getDirectiveCount() {
		return _dirFileDesc.getNDirectives();
	}
	
	public void add(AbstractDirective<? extends AbstractDeltaContext> directive) {
		DirectiveArgConverter converter = new DirectiveArgConverter((DeltaVOP)_dirFileDesc.getVOP());			
		Dir dir = converter.fromDirective(directive);
		
		List<Dir> directives = _dirFileDesc.readAllDirectives();
		directives.add(dir);
		_dirFileDesc.writeAllDirectives(directives);
	}
	
	public void execute() {
		
	}
}

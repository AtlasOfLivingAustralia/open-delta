package au.org.ala.delta.editor.slotfile.model;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.editor.directives.DirectiveArgConverter;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.editor.slotfile.VODirFileDesc;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.directive.DistDirType;
import au.org.ala.delta.editor.slotfile.directive.IntkeyDirType;
import au.org.ala.delta.editor.slotfile.directive.KeyDirType;

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
	private DirectiveArgConverter _converter;
	
	public DirectiveFile(VODirFileDesc dirFileDesc) {
		_dirFileDesc = dirFileDesc;
		_converter = new DirectiveArgConverter((DeltaVOP)_dirFileDesc.getVOP());
	}
	
	public DirectiveType getType() {
		short progType = _dirFileDesc.getProgType();
		DirectiveType type;
		switch (progType) {
		case VODirFileDesc.PROGTYPE_CONFOR:
			type = DirectiveType.CONFOR;
			break;
		case VODirFileDesc.PROGTYPE_INTKEY:
			type = DirectiveType.INTKEY;
			break;
		case VODirFileDesc.PROGTYPE_DIST:
			type = DirectiveType.DIST;
			break;
		case VODirFileDesc.PROGTYPE_KEY:
			type = DirectiveType.KEY;
			break;
		default:
			throw new IllegalStateException("This file has an unknown type! "+progType);
		}
		return type;
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
		Dir dir = _converter.fromDirective(directive);
		
		List<Dir> directives = _dirFileDesc.readAllDirectives();
		directives.add(dir);
		_dirFileDesc.writeAllDirectives(directives);
	}
	
	public void execute() {
		
	}
	
	public List<DirectiveInstance> getDirectives() {
		List<Dir> directives = _dirFileDesc.readAllDirectives();
		List<DirectiveInstance> toReturn = new ArrayList<DirectiveInstance>(directives.size());
		for (Dir dir : directives) {
			System.out.println(dir.getDirType());
			
			Directive directive = getDirective(dir);
			DirectiveArguments args = _converter.convertArgs(
					dir, directive.getArgType());
			toReturn.add(new DirectiveInstance(directive, args));
		}
		return toReturn;
	}
	
	public Directive getDirective(Dir dir) {
		DirectiveType progType = getType();
		int type = dir.getDirType();
		type &= VODirFileDesc.DIRARG_DIRTYPE_MASK;
		
		Directive directive = null;
		switch (progType) {
		case CONFOR:
			directive = ConforDirType.ConforDirArray[type];
			break;
		case DIST:
			directive = DistDirType.DistDirArray[type];
			break;
		case KEY:
			directive = KeyDirType.KeyDirArray[type];
			break;
		case INTKEY:
			directive = IntkeyDirType.IntkeyDirArray[type];
			break;
	
		default:
		throw new IllegalStateException("This file has an unknown type! "+progType);
		}
		return directive;
	}
	

	public String toString() {
		return getFileName()+" ("+getType().getAbbreviation()+")";
	}
}

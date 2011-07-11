package au.org.ala.delta.editor.slotfile.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

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
	
	public boolean isSpecsFile() {
		return (_dirFileDesc.getFileFlags() & VODirFileDesc.FILEFLAG_SPECS) > 0;
	}
	
	public boolean isItemsFile() {
		return (_dirFileDesc.getFileFlags() & VODirFileDesc.FILEFLAG_ITEMS) > 0;
	}
	
	public boolean isCharsFile() {
		return (_dirFileDesc.getFileFlags() & VODirFileDesc.FILEFLAG_CHARS) > 0;
	}
	
	public String getFileName() {
		return _dirFileDesc.getFileName();
	}
	
	public String getShortFileName() {
		String fileName = getFileName();
		File file = new File(FilenameUtils.separatorsToSystem(fileName));
		return file.getName();
	}
	
	public void setFileType(short type) {
		_dirFileDesc.setFileType(type);
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
		DirectiveType type = getType();
		for (Dir dir : directives) {
			
			Directive directive = getDirective(dir);
			DirectiveArguments args = _converter.convertArgs(
					dir, directive.getArgType());
			DirectiveInstance dirInstance = new DirectiveInstance(directive, args);
			dirInstance.setCommented((dir.getDirType() & VODirFileDesc.DIRARG_COMMENT_FLAG) != 0);
			dirInstance.setDirectiveType(type);
			toReturn.add( dirInstance);
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

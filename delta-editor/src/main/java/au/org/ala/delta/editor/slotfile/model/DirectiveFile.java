package au.org.ala.delta.editor.slotfile.model;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.editor.directives.DirectiveArgConverter;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.editor.slotfile.VODirFileDesc;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.DirArgs;
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
	
	public void setSpecsFile(boolean isSpecsFile) {
		int flags = _dirFileDesc.getFileFlags();
		if (isSpecsFile) {
			_dirFileDesc.setFileFlags(flags | VODirFileDesc.FILEFLAG_SPECS);
		}
		else {
			_dirFileDesc.setFileFlags(flags & ~VODirFileDesc.FILEFLAG_SPECS);
		}
	}
	
	public boolean isItemsFile() {
		return (_dirFileDesc.getFileFlags() & VODirFileDesc.FILEFLAG_ITEMS) > 0;
	}
	
	public void setItemsFile(boolean isItemsFile) {
		int flags = _dirFileDesc.getFileFlags();
		if (isItemsFile) {
			_dirFileDesc.setFileFlags(flags | VODirFileDesc.FILEFLAG_ITEMS);
		}
		else {
			_dirFileDesc.setFileFlags(flags & ~VODirFileDesc.FILEFLAG_ITEMS);
		}
	}
	
	public boolean isCharsFile() {
		return (_dirFileDesc.getFileFlags() & VODirFileDesc.FILEFLAG_CHARS) > 0;
	}
	
	public void setCharsFile(boolean isCharsFile) {
		int flags = _dirFileDesc.getFileFlags();
		if (isCharsFile) {
			_dirFileDesc.setFileFlags(flags | VODirFileDesc.FILEFLAG_CHARS);
		}
		else {
			_dirFileDesc.setFileFlags(flags & ~VODirFileDesc.FILEFLAG_CHARS);
		}
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
	
	public void add(DirectiveInstance directive) {
		add(getDirectiveCount(), directive);
	}
	
	public void add(int index, DirectiveInstance directive) {
		Dir dir = _converter.fromDirective(directive);
		
		List<Dir> directives = _dirFileDesc.readAllDirectives();
		System.out.println(directives);
		directives.add(index, dir);
		System.out.println("Adding directive "+directive.getDirective().joinNameComponents());
		_dirFileDesc.writeAllDirectives(directives);
	}
	
	public void deleteDirective(int directiveNum) {
		_dirFileDesc.deleteDirective(directiveNum);
	}
	
	public DirectiveInstance addTextDirective(int index, Directive directiveType, String text) {
		DirectiveArguments args = new DirectiveArguments();
		args.addTextArgument(text);
		DirectiveInstance directive = new DirectiveInstance(directiveType, args);
		add(index, directive);
		return directive;
	}
	
	public DirectiveInstance addIntegerDirective(int index, Directive directiveType, int value) {
		DirectiveArguments args = new DirectiveArguments();
		args.addValueArgument(new BigDecimal(value));
		DirectiveInstance directive = new DirectiveInstance(directiveType, args);
		add(index, directive);
		return directive;
	}
	
	public DirectiveInstance addNoArgDirective(int index, Directive directiveType) {
		DirectiveArguments args = new DirectiveArguments();
		DirectiveInstance directive = new DirectiveInstance(directiveType, args);
		add(index, directive);
		return directive;
	}
	
	public void execute() {
		
	}
	
	/**
	 * Returns the the text from the first SHOW or COMMENT directive in
	 * this directive file.  If none exist, an empty string will be returned.
	 */
	public String getDescription() {
		List<Integer> directiveTypes = new ArrayList<Integer>();		
		
		switch (getType()) {
		case CONFOR:
			directiveTypes.add(ConforDirType.SHOW);
			directiveTypes.add(ConforDirType.COMMENT);
			break;
		case INTKEY:
			directiveTypes.add(IntkeyDirType.SHOW);
			directiveTypes.add(IntkeyDirType.COMMENT);
			break;
		case DIST:
			directiveTypes.add(DistDirType.COMMENT);
			break;
		case KEY:
			directiveTypes.add(KeyDirType.COMMENT);
			break;
		}
		
		List<Dir> directives = _dirFileDesc.readAllDirectives(directiveTypes);
		
		String description = "";
		if (!directives.isEmpty()) {
			List<DirArgs> args = directives.get(0).args;
			if (args.size() > 0) {
				description = args.get(0).text;
			}		
		}
		
		return description;
	}
	
	public List<DirectiveInstance> getDirectives() {
		List<Dir> directives = _dirFileDesc.readAllDirectives();
		List<DirectiveInstance> toReturn = new ArrayList<DirectiveInstance>(directives.size());
		DirectiveType type = getType();
		for (Dir dir : directives) {
			
			Directive directive = getDirective(dir);
			DirectiveArguments args = _converter.convertArgs(dir, directive.getArgType());
			DirectiveInstance dirInstance = new DirectiveInstance(directive, args);
			dirInstance.setCommented((dir.getDirType() & VODirFileDesc.DIRARG_COMMENT_FLAG) != 0);
			dirInstance.setDirectiveType(type);
			toReturn.add( dirInstance);
		}
		return toReturn;
	}
	
	private Directive getDirective(Dir dir) {
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

	public long getLastModifiedTime() {
		return _dirFileDesc.getFileModifyTime();
	}
	

	public String toString() {
		return getFileName()+" ("+getType().getAbbreviation()+")";
	}
	
	/**
	 * Attempts to return the name of the directive in this file which is 
	 * the main purpose of the directive file.
	 * It currently only will return something for CONFOR directive files.
	 */
	public String getDefiningDirective() {
		if (getType() == DirectiveType.CONFOR) {
			int directiveType = _dirFileDesc.getPrincipleConforAction();
			if (directiveType != 0) {
				return ConforDirType.ConforDirArray[directiveType].joinNameComponents();
			}
		}
		return "";
	}

	public int getFileNumber() {
		DeltaVOP vop = (DeltaVOP)_dirFileDesc.getVOP();
		return vop.getDeltaMaster().dirFileNoFromUniId(_dirFileDesc.getUniId());
	}
}

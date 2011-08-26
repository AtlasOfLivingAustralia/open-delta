package au.org.ala.delta.editor.directives;

import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;

/**
 * A DirectiveFileInfo provides information about a directive file that is
 * used by the import/export process.  It is separate from the
 * DirectiveFile model class because the import process needs this information
 * but will not necessarily have a slotfile backed DirectiveFiles available for
 * use.
 */
public class DirectiveFileInfo {

	private String _fileName;
	private DirectiveType _type;
	private DirectiveFile _directiveFile;
	private String _name;
	
	public DirectiveFileInfo(String fileName) {
		this(fileName, null, null, null);
	}
	
	public DirectiveFileInfo(String fileName, DirectiveType type) {
		this(fileName, fileName, type, null);
	}
	
	public DirectiveFileInfo(String name, String fileName, DirectiveType type) {
		this(name, fileName, type, null);
	}
	
	public DirectiveFileInfo(String fileName, DirectiveType type, DirectiveFile file) {
		this(fileName, fileName, type, file);
	}
	
	public DirectiveFileInfo(String name, String fileName, DirectiveType type, DirectiveFile file) {
		_name = name;
		setFileName(fileName);
		setType(type);
		setDirectiveFile(file);
	}
	
	/**
	 * @return the desired name of the DirectiveFile, which may be different
	 * to the fileName (in the case of importing template directive files
	 * when creating a new dataset.
	 */
	public String getName() {
		return _name;
	}
	
	public String getFileName() {
		return _fileName;
	}
	public void setFileName(String fileName) {
		_fileName = fileName;
	}
	public DirectiveType getType() {
		return _type;
	}
	public void setType(DirectiveType type) {
		_type = type;
	}
	public DirectiveFile getDirectiveFile() {
		return _directiveFile;
	}
	public void setDirectiveFile(DirectiveFile directiveFile) {
		_directiveFile = directiveFile;
	}
	
	public String toString() {
		return _fileName + " (" + _type.getAbbreviation() + ")";
	}
	
}

package au.org.ala.delta.editor.directives;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * A FileNameFilter that takes an exclusion pattern of the form:
 * pattern1;pattern2;pattern3...
 * The patterns are treated as literals except for the * character with is treated as a wildcard.
 */
public class ImportFileNameFilter extends FileFilter implements java.io.FileFilter {

	private String _description;
	private String[] _exclusionPatterns;
	
	public ImportFileNameFilter(String exclusionPatterns) {
		
		_description = exclusionPatterns;
		// Convert simple wildcard * into a regexp - convert '.' to a literal (as it will be 
		// a file name extension) and '*' into ".*"
		String tmp = exclusionPatterns.replace(".", "\\.");
		tmp = tmp.replace("*", ".*");
		_exclusionPatterns = tmp.split(";");
	}
	@Override
	public boolean accept(File file) {
		
		for (String pattern : _exclusionPatterns) {
			if (file.getName().matches("^"+pattern+"$")) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String getDescription() {
		return _description;
	}
	
	

	
}

package au.org.ala.delta.editor.directives;

/**
 * The status of the current import or export operation. Used as a model for the ImportExportStatusDialog.
 */
public class ImportExportStatus {

	private String heading;
	private String importDirectory;
	private String currentFile;
	private String currentDirective;
	
	private int totalLines;
	private int totalErrors;
	
	private int lineInCurentFile;
	private int errorsInCurrentFile;
	
	private String textFromLastShowDirective;

	/**
	 * @return the heading
	 */
	public String getHeading() {
		return heading;
	}

	/**
	 * @param heading the heading to set
	 */
	public void setHeading(String heading) {
		this.heading = heading;
	}

	/**
	 * @return the importDirectory
	 */
	public String getImportDirectory() {
		return importDirectory;
	}

	/**
	 * @param importDirectory the importDirectory to set
	 */
	public void setImportDirectory(String importDirectory) {
		this.importDirectory = importDirectory;
	}

	/**
	 * @return the currentFile
	 */
	public String getCurrentFile() {
		return currentFile;
	}

	/**
	 * @param currentFile the currentFile to set
	 */
	public void setCurrentFile(String currentFile) {
		this.currentFile = currentFile;
	}

	/**
	 * @return the currentDirective
	 */
	public String getCurrentDirective() {
		return currentDirective;
	}

	/**
	 * @param currentDirective the currentDirective to set
	 */
	public void setCurrentDirective(String currentDirective) {
		this.currentDirective = currentDirective;
	}

	/**
	 * @return the totalLines
	 */
	public int getTotalLines() {
		return totalLines;
	}

	/**
	 * @param totalLines the totalLines to set
	 */
	public void setTotalLines(int totalLines) {
		this.totalLines = totalLines;
	}

	/**
	 * @return the totalErrors
	 */
	public int getTotalErrors() {
		return totalErrors;
	}

	/**
	 * @param totalErrors the totalErrors to set
	 */
	public void setTotalErrors(int totalErrors) {
		this.totalErrors = totalErrors;
	}

	/**
	 * @return the lineInCurentFile
	 */
	public int getLineInCurentFile() {
		return lineInCurentFile;
	}

	/**
	 * @param lineInCurentFile the lineInCurentFile to set
	 */
	public void setLineInCurentFile(int lineInCurentFile) {
		this.lineInCurentFile = lineInCurentFile;
	}

	/**
	 * @return the errorsInCurrentFile
	 */
	public int getErrorsInCurrentFile() {
		return errorsInCurrentFile;
	}

	/**
	 * @param errorsInCurrentFile the errorsInCurrentFile to set
	 */
	public void setErrorsInCurrentFile(int errorsInCurrentFile) {
		this.errorsInCurrentFile = errorsInCurrentFile;
	}

	/**
	 * @return the textFromLastShowDirective
	 */
	public String getTextFromLastShowDirective() {
		return textFromLastShowDirective;
	}

	/**
	 * @param textFromLastShowDirective the textFromLastShowDirective to set
	 */
	public void setTextFromLastShowDirective(String textFromLastShowDirective) {
		this.textFromLastShowDirective = textFromLastShowDirective;
	}
	
	
}

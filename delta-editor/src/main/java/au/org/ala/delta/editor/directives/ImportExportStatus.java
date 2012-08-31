/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.editor.directives;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.Heading;
import au.org.ala.delta.directives.Show;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.rtf.RTFAlignment;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.util.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.ResourceMap;

import java.text.DateFormat;
import java.util.Date;


/**
 * The status of the current import or export operation. Used as a model for the ImportExportStatusDialog.
 */
public class ImportExportStatus  {

	private String heading;
	private String importDirectory;
	private String currentFile;
	private String currentDirective;
	
	private int totalDirectives;
	private int totalErrors;
	
	private int directivesInCurentFile;
	private int errorsInCurrentFile;
	
	private String textFromLastShowDirective;

	private RTFBuilder _logBuilder;
	
	private boolean _error;
	
	private volatile boolean _cancelled;
	private volatile boolean _paused;
	private volatile boolean _finished;
	private volatile boolean _pauseOnError;
	
	private String _resourcePrefix;
	private ResourceMap _resources;
	
	public ImportExportStatus(ResourceMap resources, String resourcePrefix) {
		_cancelled = false;
		_finished = false;
		_paused = false;
		_pauseOnError = true;
		_resourcePrefix = resourcePrefix;
		_resources = resources;
		_logBuilder = new RTFBuilder();
		_logBuilder.startDocument();
		
	}

	/**
	 * @param heading the heading to set
	 */
	public void setHeading(String heading) {
		this.heading = heading;
		_logBuilder.setAlignment(RTFAlignment.CENTER);
		_logBuilder.appendText(_resources.getString(_resourcePrefix+".heading"));
		_logBuilder.appendText(_resources.getString("importExportReport.dataSetLabel",heading));
	}

	/**
	 * @return the importDirectory
	 */
	public String getImportDirectory() {
		return importDirectory;
	}

	public String getHeading() {
		return this.heading;
	}
	
	/**
	 * @param importDirectory the importDirectory to set
	 */
	public void setImportDirectory(String importDirectory) {
		
		this.importDirectory = importDirectory;
		_logBuilder.appendText(_resources.getString(_resourcePrefix+".directoryLabel", importDirectory));
		_logBuilder.appendText(_resources.getString(_resourcePrefix+".timeLabel", currentTime()));
		
	}
	
	private String currentTime() {
		DateFormat dateFormat = DateFormat.getDateTimeInstance();
		return dateFormat.format(new Date());
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
	public void setCurrentFile(DirectiveFileInfo currentFile) {
		
		finishPreviousDirective();
		_error = false;
		
		this.currentFile = currentFile.getFileName();
		errorsInCurrentFile = 0;
		directivesInCurentFile = 0;
		
		_logBuilder.setAlignment(RTFAlignment.LEFT);
		_logBuilder.appendText("");
		_logBuilder.appendText(_resources.getString("importExportReport.directivesFileLabel", currentFile));
		_logBuilder.increaseIndent();
		_logBuilder.appendText(_resources.getString("importExportReport.fileTypeLabel", currentFile.getType()));
		
		_logBuilder.decreaseIndent();
	}

	private void finishPreviousDirective() {
		if (StringUtils.isNotEmpty(this.currentFile)) {
			_logBuilder.increaseIndent();
			if (_error) {
				_logBuilder.appendText(_resources.getString(_resourcePrefix+".failure"));
			}
			else {
				_logBuilder.appendText(_resources.getString(_resourcePrefix+".success"));
			}
			_logBuilder.decreaseIndent();
		}
	}

	/**
	 * @return the currentDirective
	 */
	public String getCurrentDirective() {
		return currentDirective;
	}

	/**
	 * @param directive the current directive being processed
     * @param data the data supplied to the directive being processed.
     *
	 */
	public void setCurrentDirective(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {
		directivesInCurentFile++;
		totalDirectives++;
		currentDirective = directive.getName();
		
		if (ArrayUtils.equalsIgnoreCase(Show.CONTROL_WORDS, directive.getControlWords())) {
			_logBuilder.increaseIndent();
			_logBuilder.appendText("*"+directive.getName()+" "+data);
			_logBuilder.decreaseIndent();
			textFromLastShowDirective = data;
		}
		else if (ArrayUtils.equalsIgnoreCase(Heading.CONTROL_WORDS, directive.getControlWords())) {
			heading = data;
		}
	}
	
	public void setCurrentDirective(DirectiveInstance directive) {
		directivesInCurentFile++;
		totalDirectives++;
		String name = directive.getDirective().joinNameComponents();
		currentDirective = name;
		
		if (directive.getDirective().getNumber() == ConforDirType.SHOW) {
			_logBuilder.increaseIndent();
			String data = "";
			DirectiveArguments args = directive.getDirectiveArguments();
			if (args != null) {
				data = args.getFirstArgumentText();
			}
			_logBuilder.appendText("*"+name+" "+data);
			_logBuilder.decreaseIndent();
			textFromLastShowDirective = data;
		}
		else if (directive.getDirective().getNumber() == ConforDirType.HEADING) {
			String data = "";
			DirectiveArguments args = directive.getDirectiveArguments();
			if (args != null) {
				data = args.getFirstArgumentText();
			}
			heading = data;
		}
	}

	/**
	 * @return the totalLines
	 */
	public int getTotalDirectives() {
		return totalDirectives;
	}

	public void error(String message) {
		incrementErrors();
		_error = true;
		_logBuilder.increaseIndent();
		_logBuilder.appendText(message);
		_logBuilder.decreaseIndent();
		
	}
	
	private void incrementErrors() {
		totalErrors++;
		errorsInCurrentFile++;
	}
	
	/**
	 * @return the totalErrors
	 */
	public int getTotalErrors() {
		return totalErrors;
	}


	/**
	 * @return the lineInCurentFile
	 */
	public int getDirectivesInCurentFile() {
		return directivesInCurentFile;
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
	
	public String getImportLog() {
		return _logBuilder.toString() + "}\n";
	}

	public boolean getPauseOnError() {
		return _pauseOnError;
	}
	
	/**
	 * Pauses the execution of the calling Thread until such time as some
	 * other thread calls resume().
	 * In the intended use case, this object becomes the synchronization
	 * point between the ImportController.DoImportTask and the ImportExportStatusDialog.
	 */
	public void pause() {
		_paused = true;
		synchronized (this) {
			try {
				this.wait();
			}
			catch (InterruptedException e){}
		}
	}
	
	/**
	 * Combined with the pause() method, this is used by the
	 * ImportController.DoImportTask to pause and resume the import operation.
	 */
	public void resume() {
		_paused = false;
		synchronized(this) {
			this.notify();
		}
	}

	public void cancel() {
		_cancelled = true;
		synchronized(this) {
			this.notify();
		}
	}
	
	public boolean isCancelled() {
		return _cancelled;
	}
	
	public boolean isPaused() {
		return _paused;
	}

	public void finish() {
		_finished = true;
		finishPreviousDirective();
		writeReportFooter();
		
	}

	private void writeReportFooter() {
		_logBuilder.setAlignment(RTFAlignment.CENTER);
		_logBuilder.appendText(_resources.getString(_resourcePrefix+".finished", currentTime()));
		if (totalErrors > 0) {
			_logBuilder.appendText(_resources.getString(_resourcePrefix+".failureMessage",totalErrors));
		}
	}
	
	public boolean isFinished() {
		return _finished;
	}

	public void setPauseOnError(boolean pauseOnError) {
		_pauseOnError = pauseOnError;
		
	}
}

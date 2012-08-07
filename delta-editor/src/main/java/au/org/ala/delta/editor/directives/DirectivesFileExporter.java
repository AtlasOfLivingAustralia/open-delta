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

import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.editor.slotfile.directive.DirectiveInOutState;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.util.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.List;

/**
 * The DirectivesFileExporter is responsible exporting the contents of a DirectivesFile to the file system.
 */
public class DirectivesFileExporter {

    /**
     * Exports the directives in the supplied DirectiveFile.
     * @param file the DirectiveFile to export.
     * @param state tracks the current directive and export location.
     */
	public void writeDirectivesFile(DirectiveFile file, DirectiveInOutState state) {
		try {
			List<DirectiveInstance> directives = file.getDirectives();

			for (int i = 0; i < directives.size(); i++) {
				writeDirective(directives.get(i), state);
				if (i != directives.size() - 1) {
					state.getPrinter().writeBlankLines(1, 0);
				}
			}
			state.getPrinter().printBufferLine();
			file.setLastModifiedTime(System.currentTimeMillis());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (state.getPrinter() != null) {
				state.getPrinter().close();
			}
		}
	}

    /**
     * Creates a file in the supplied path on the file system to export the contents of the supplied
     * DirectivesFile.  If there is an existing file with the same name, it will be backed up and deleted.
     * @param file the DirectiveFile to be exported.
     * @param directoryPath the directory in which the file should be created.
     * @return a File to which the directives can be written.
     */
	public File createExportFile(DirectiveFile file, String directoryPath) {
		String fileName = file.getShortFileName();
		FileUtils.backupAndDelete(fileName, directoryPath);
		
		FilenameUtils.concat(directoryPath, fileName);
		File directivesFile = new File(directoryPath + fileName);
		
		return directivesFile;
	}

	protected void writeDirective(DirectiveInstance directive,
			DirectiveInOutState state) {
		
		state.setCurrentDirective(directive);
		Directive directiveInfo = directive.getDirective();

		directiveInfo.getOutFunc().process(state);
	}
}

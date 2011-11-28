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
package au.org.ala.delta.editor.directives.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.editor.directives.DirectiveFileInfo;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;

/**
 * Manages the data displayed in the ImportExportDialog during an export 
 * operation.
 */
public class ExportViewModel extends ImportExportViewModel {

	/**
	 * Builds the model for the export dialog from the directive files
	 * currently a part of the model.
	 * @return a new ImportExportViewModel initialised from the data set.
	 */
	@Override
	public void populate(EditorViewModel model) {

		int directiveFileCount = model.getDirectiveFileCount();

		List<DirectiveFileInfo> files = new ArrayList<DirectiveFileInfo>(
				directiveFileCount);

		for (int i = 1; i <= directiveFileCount; i++) {
			DirectiveFile dirFile = model.getDirectiveFile(i);

			DirectiveFileInfo info = new DirectiveFileInfo(
					dirFile.getShortFileName(), dirFile.getType());
			info.setDirectiveFile(dirFile);

			if (dirFile.isSpecsFile()) {
				setSpecsFile(info);
			} else if (dirFile.isItemsFile()) {
				setItemsFile(info);
			} else if (dirFile.isCharsFile()) {
				setCharactersFile(info);
			} else {
				files.add(info);
			}
		}
		setIncludedDirectivesFiles(files);
		String exportPath = model.getExportPath();
		if (StringUtils.isEmpty(exportPath)) {
			exportPath = model.getDataSetPath();
		}
		setCurrentDirectory(new File(exportPath));
	}
}

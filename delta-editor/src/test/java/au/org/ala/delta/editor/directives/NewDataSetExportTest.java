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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;

/**
 * Tests the chars / specs / items directives files are created and exported
 * correctly from a new dataset.
 */
public class NewDataSetExportTest extends AbstractImportExportTest {
	
	@Override
	protected void createDataSet() throws Exception {
		File tmpDataSet = copyURLToFile("/dataset/simple/test1.dlt");
		SlotFileRepository repo = new SlotFileRepository();
		_dataSet = (SlotFileDataSet)repo.findByName(tmpDataSet.getAbsolutePath(), null);
		
	}

	@Test
	public void testExport() throws Exception {
		ExportController controller = new ExportController(_helper);
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		List<DirectiveFileInfo> files = new ArrayList<DirectiveFileInfo>();
		DirectiveFileInfo specs = new DirectiveFileInfo("specs", DirectiveType.CONFOR);
		files.add(specs);
		DirectiveFileInfo chars = new DirectiveFileInfo("chars", DirectiveType.CONFOR);
		files.add(chars);
		DirectiveFileInfo items = new DirectiveFileInfo("items", DirectiveType.CONFOR);
		files.add(items);
		
		controller.new DoExportTask(tmpDir, files, true).doInBackground();
		
		File exportedSpecs = new File(FilenameUtils.concat(tmpDir.getAbsolutePath(), "specs"));
		String specsText = FileUtils.readFileToString(exportedSpecs);
		assertTrue(specsText.contains("*NUMBER OF CHARACTERS 3"));
		assertTrue(specsText.contains("*MAXIMUM NUMBER OF STATES 4"));
		assertTrue(specsText.contains("*MAXIMUM NUMBER OF ITEMS 4"));
		assertTrue(specsText.contains("*CHARACTER TYPES"));
		assertTrue(specsText.contains("*NUMBERS OF STATES 1,4 3,3"));
		
		
		File exportedChars = new File(FilenameUtils.concat(tmpDir.getAbsolutePath(), "chars"));
		String charsText = FileUtils.readFileToString(exportedChars);
		assertTrue(charsText.contains("CHARACTER LIST"));
		
		File exportedItems = new File(FilenameUtils.concat(tmpDir.getAbsolutePath(), "items"));
		String itemsText = FileUtils.readFileToString(exportedItems);
		assertTrue(itemsText.contains("ITEM DESCRIPTIONS"));
	}
	
}

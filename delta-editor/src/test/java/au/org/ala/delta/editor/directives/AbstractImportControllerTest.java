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



import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;

/**
 * Base class for test cases testing the import controller. Each subclass
 * works with a different data set.
 */
public abstract class AbstractImportControllerTest extends AbstractImportExportTest {

	
	/** The instance of the class we are testing */
	protected ImportController importer;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		importer = new ImportController(_helper, _model);
	}
	
	protected void createDataSet() throws Exception {
		_dataSet = (SlotFileDataSet)_repository.newDataSet();
	}	
}

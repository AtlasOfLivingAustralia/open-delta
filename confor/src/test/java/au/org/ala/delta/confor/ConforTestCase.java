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
package au.org.ala.delta.confor;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;

/**
 * Base class for CONFOR integration tests.
 */
public abstract class ConforTestCase extends TestCase {

	protected String _directivesFilePath;
	protected String _samplePath;
	
	@Before
	public void setUp() throws Exception {
		File directory = urlToFile("/dataset/");
		File dest = new File(System.getProperty("java.io.tmpdir"));
		FileUtils.copyDirectory(directory, dest);
	
		_samplePath = FilenameUtils.concat(dest.getAbsolutePath(), getDataSet());
		_directivesFilePath = FilenameUtils.concat(_samplePath, directivesFileName());
	
		
	}
	
	protected void runConfor() throws Exception {
		CONFOR.main(new String[] { _directivesFilePath });
	}
	
	protected abstract String directivesFileName();
	
	protected String getDataSet() {
		return "sample";
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = ToDistTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
}

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
package au.org.ala.delta.delfor;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.model.AbstractObservableDataSet;

/**
 * Tests the DirectivesFileFormatter class.
 */
public class DirectivesFileFormatterTest extends TestCase {

	private DirectivesFileFormatter _formatter;
	private DelforContext _context;
	
	@Before
	public void setUp() throws Exception {
		SlotFileRepository dataSetRepository = new SlotFileRepository();
		AbstractObservableDataSet dataSet = (AbstractObservableDataSet) dataSetRepository.newDataSet();
		
		_context = new DelforContext(dataSet);
		
		DelforDirectiveFileParser parser = DelforDirectiveFileParser.createInstance();
		File specs = urlToFile("/dataset/sample/specs");
		parser.parse(specs, _context);
		
		
		_formatter = new DirectivesFileFormatter(_context);
	}
	
	
	@Test
	public void testReformatSpecs() throws Exception {
		File specs = urlToFile("/dataset/sample/specs");
		_context.addReformatFile(specs);
		_formatter.reformat();
	}
	
	private File urlToFile(String urlString) throws Exception {
		URL url = DirectivesFileFormatterTest.class.getResource(urlString);
		File file = new File(url.toURI());
		return file;
	}
	
}

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
package au.org.ala.delta.translation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.ConforDirectiveFileParser;
import au.org.ala.delta.directives.ConforDirectiveParserObserver;

public abstract class TranslatorTest extends TestCase {

	protected ByteArrayOutputStream _bytes;
	protected DeltaContext _context;

	public TranslatorTest() {

	}

	public TranslatorTest(String name) {
		super(name);
	}

	/**
	 * Reads in specs/chars/items from the simple test data set but no other
	 * configuration. Test cases can manually configure the DeltaContext before
	 * doing the translation.
	 * 
	 * @throws Exception
	 *             if there was an error reading the input files.
	 */
	protected void initialiseContext(String path) throws Exception {

		File specs = classloaderPathToFile(path);

		ConforDirectiveFileParser parser = ConforDirectiveFileParser
				.createInstance();
		ConforDirectiveParserObserver conforObserver = new ConforDirectiveParserObserver(_context);
		parser.registerObserver(conforObserver);
		parser.parse(specs, _context);
		
		conforObserver.finishedProcessing();
	}

	protected File classloaderPathToFile(String path) throws URISyntaxException {
		URL resource = getClass().getResource(path);
		return new File(resource.toURI());
	}

	protected String classLoaderPathToString(String path) throws Exception {
		File file = classloaderPathToFile(path);

		return FileUtils.readFileToString(file, "Cp1252");
	}

	protected String actualResults() throws IOException {
		_bytes.flush();
		return new String(_bytes.toByteArray(), Charset.forName("UTF-8"));
	}
}

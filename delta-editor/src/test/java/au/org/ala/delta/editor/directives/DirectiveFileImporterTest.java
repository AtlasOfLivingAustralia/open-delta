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
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.directives.DirectiveSearchResult;
import au.org.ala.delta.directives.DirectiveSearchResult.ResultType;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.directive.DistDirType;
import au.org.ala.delta.editor.slotfile.directive.IntkeyDirType;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSetFactory;
import au.org.ala.delta.model.CharacterType;

/**
 * Tests the DirectiveFileImporter class. 
 */
public class DirectiveFileImporterTest extends TestCase {

	/** The instance of the class we are testing */
	private DirectiveFileImporter _importer;
	
	/** The data set we are importing into */
	private SlotFileDataSet _dataSet;
	
	private ImportContext _context;
	
	private DirectiveImportHandlerAdapter _importHandler;
	
	@Before
	public void setUp() throws Exception {
		
		_dataSet = (SlotFileDataSet)new SlotFileDataSetFactory().createDataSet("test");
		for (int i=0; i<89; i++) {
			_dataSet.addCharacter(CharacterType.Text);
		}
		_context = new ImportContext(_dataSet);
		_importHandler = new DirectiveImportHandlerAdapter();
		
	}
	
	@Test
	public void testToIntImport() throws Exception {
	
		_importer = new DirectiveFileImporter(_importHandler, ConforDirType.ConforDirArray);
		DirectiveFile file = importFile("toint", DirectiveType.CONFOR);
		assertEquals(1, _dataSet.getDirectiveFileCount());
		
		file = _dataSet.getDirectiveFile(1);
		
		assertEquals(24, file.getDirectiveCount());
		
		List<DirectiveInstance> directives = file.getDirectives();
		
		DirectiveInstance directive = directives.get(0);
		assertEquals("SHOW", directive.getDirective().joinNameComponents());
		assertEquals("Translate into INTKEY format.", directive.getDirectiveArguments().getFirstArgumentText().trim());
		
		directive = directives.get(1);
		assertEquals("LISTING FILE", directive.getDirective().joinNameComponents());
		assertEquals("toint.lst", directive.getDirectiveArguments().getFirstArgumentText().trim());
		
		directive = directives.get(2);
		assertEquals("HEADING", directive.getDirective().joinNameComponents());
		assertEquals("DELTA Sample Data", directive.getDirectiveArguments().getFirstArgumentText());
		
		directive = directives.get(3);
		assertEquals("REGISTRATION SUBHEADING", directive.getDirective().joinNameComponents());
		assertEquals("Version: 21st September 2000.", directive.getDirectiveArguments().getFirstArgumentText());
		
		directive = directives.get(4);
		assertEquals("INPUT FILE", directive.getDirective().joinNameComponents());
		assertEquals("specs", directive.getDirectiveArguments().getFirstArgumentText());
		
		directive = directives.get(5);
		assertEquals("TRANSLATE INTO INTKEY FORMAT", directive.getDirective().joinNameComponents());
		assertEquals(0, directive.getDirectiveArguments().size());
		
		directive = directives.get(6);
		assertEquals("CHARACTERS FOR SYNONYMY", directive.getDirective().joinNameComponents());
		assertEquals(1, directive.getDirectiveArguments().getFirstArgumentIdAsInt());
		
		directive = directives.get(7);
		assertEquals("OMIT PERIOD FOR CHARACTERS", directive.getDirective().joinNameComponents());
		assertEquals(1, directive.getDirectiveArguments().getFirstArgumentIdAsInt());
		
		directive = directives.get(8);
		assertEquals("OMIT OR FOR CHARACTERS", directive.getDirective().joinNameComponents());
		assertEquals(86, directive.getDirectiveArguments().getFirstArgumentIdAsInt());
		
		directive = directives.get(9);
		assertEquals("OMIT INNER COMMENTS", directive.getDirective().joinNameComponents());
		assertEquals(0, directive.getDirectiveArguments().size());
		
		directive = directives.get(10);
		assertEquals("EXCLUDE CHARACTERS", directive.getDirective().joinNameComponents());
		assertEquals(2, directive.getDirectiveArguments().size());
		assertEquals(Integer.valueOf(88), directive.getDirectiveArguments().get(0).getId());
		assertEquals(Integer.valueOf(89), directive.getDirectiveArguments().get(1).getId());
		
		directive = directives.get(11);
		assertEquals("CHARACTER RELIABILITIES", directive.getDirective().joinNameComponents());
		assertEquals(87, directive.getDirectiveArguments().size());
		assertEquals(8, directive.getDirectiveArguments().get(43).getValueAsInt());
		assertEquals(new BigDecimal("7.1"), directive.getDirectiveArguments().get(76).getValue());
	
		directive = directives.get(12);
		assertEquals("NEW PARAGRAPHS AT CHARACTERS", directive.getDirective().joinNameComponents());
		assertEquals(11, directive.getDirectiveArguments().size());
		int[] ids = {1, 4, 12, 25, 26, 68, 77, 78, 87, 88, 89};
		for (int i=0; i<ids.length; i++) {
			assertEquals(Integer.valueOf(ids[i]), (Integer)directive.getDirectiveArguments().get(i).getId());
		}
	
		directive = directives.get(13);
		assertEquals("ITEM SUBHEADINGS", directive.getDirective().joinNameComponents());
		assertEquals(11, directive.getDirectiveArguments().size());
		
		ids = new int[] {0, 1, 4, 12, 25, 26, 68, 77, 78, 87, 88};
		// First arg is the delimiter, if present.
		for (int i=1; i<ids.length; i++) {
			assertEquals(Integer.valueOf(ids[i]), (Integer)directive.getDirectiveArguments().get(i).getId());
			assertTrue(StringUtils.isNotEmpty(directive.getDirectiveArguments().get(i).getText()));
		}
		assertEquals("\\pard\\li0\\fi340\\b{}Taxonomy, distribution.\\b0{} ", directive.getDirectiveArguments().get(8).getText());
		
		// Next 5 are "INPUT FILE".
		String[] files = {"cnotes", "ofonts", "cimages", "timages", "ofiles"};
		for (int i=0; i<files.length; i++) {
			directive = directives.get(14+i);
			assertEquals("INPUT FILE", directive.getDirective().joinNameComponents());
			assertEquals(files[i], directive.getDirectiveArguments().getFirstArgumentText());
		}
		
	}
	
	
	@Test
	public void testDistImport() throws Exception {
		_importer = new DirectiveFileImporter(_importHandler, DistDirType.DistDirArray);
		importFile("dist", DirectiveType.DIST);
		DirectiveFile file = _dataSet.getDirectiveFile(1);
		
		assertEquals(1, _dataSet.getDirectiveFileCount());
		
		List<DirectiveInstance> directives = file.getDirectives();
		
		assertEquals(5, directives.size());
		
		DirectiveInstance directive = directives.get(0);
		assertEquals("COMMENT", directive.getDirective().joinNameComponents());
		assertEquals("Generate distance matrix.", directive.getDirectiveArguments().getFirstArgumentText().trim());
		
		directive = directives.get(1);
		assertEquals("LISTING FILE", directive.getDirective().joinNameComponents());
		assertEquals("dist.lst", directive.getDirectiveArguments().getFirstArgumentText());
		
		directive = directives.get(2);
		assertEquals("ITEMS FILE", directive.getDirective().joinNameComponents());
		assertEquals("ditems", directive.getDirectiveArguments().getFirstArgumentText());
		
		directive = directives.get(3);
		assertEquals("OUTPUT FILE", directive.getDirective().joinNameComponents());
		assertEquals("grass.dis", directive.getDirectiveArguments().getFirstArgumentText());
		
		directive = directives.get(4);
		assertEquals("MINIMUM NUMBER OF COMPARISONS", directive.getDirective().joinNameComponents());
		assertEquals(7, directive.getDirectiveArguments().getFirstArgumentValue());
	}

	private DirectiveFile importFile(String fileName, DirectiveType type) throws URISyntaxException, IOException {
		String path = "/au/org/ala/delta/editor/directives/expected_results/"+fileName;
		File toint = new File(getClass().getResource(path).toURI());
		
		DirectiveFile file = _dataSet.addDirectiveFile(1, fileName, type);
		
		_context.setDirectiveFile(file);
		try {
			_importer.parse(toint, _context);
		}
		catch (DirectiveException e) {}
		return file;
	}
	
	@Test
	public void testIntkeyFileImport() throws Exception {
		
		_importer = new DirectiveFileImporter(_importHandler, IntkeyDirType.IntkeyDirArray);
		List<String> directiveControlWords = Arrays.asList(IntkeyDirType.IntkeyDirArray[IntkeyDirType.DEFINE_BUTTON].getName());
		DirectiveSearchResult result = _importer.getDirectiveRegistry().findDirective(directiveControlWords);
		assertEquals(ResultType.Found, result.getResultType());
		assertEquals(directiveControlWords, Arrays.asList(result.getMatches().get(0).getControlWords()));
		
		directiveControlWords = Arrays.asList(IntkeyDirType.IntkeyDirArray[IntkeyDirType.COMMENT].getName());
		result = _importer.getDirectiveRegistry().findDirective(directiveControlWords);
		assertEquals(ResultType.Found, result.getResultType());
		assertEquals(directiveControlWords, Arrays.asList(result.getMatches().get(0).getControlWords()));
		
	}
}

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
package au.org.ala.delta.editor.slotfile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;

/**
 * Tests the VODirFileDesc class.
 */
public class VODirFileDescTest extends DeltaTestCase {

	/** Holds the data set we obtain the data from to back our test */
	protected DeltaVOP _vop;

	@Before
	public void setUp() throws Exception {
		File f = copyURLToFile("/dataset/simple.DLT");

		_vop = new DeltaVOP(f.getAbsolutePath(), false);
	}

	@After
	public void tearDown() throws Exception {
		if (_vop != null) {
			_vop.close();
		}
		super.tearDown();
	}

	@Test
	/**
	 * The Specs file looks like:
	 * *SHOW ~ Grass Genera - specifications.
	 *
	 * *NUMBER OF CHARACTERS 5
	 * *MAXIMUM NUMBER OF STATES 3
	 * *MAXIMUM NUMBER OF ITEMS 6
	 * *CHARACTER TYPES 1,TE 2,IN 3,RN 4,UM 5,OM 
	 * *NUMBERS OF STATES 4,2 5,3
	 * *IMPLICIT VALUES 5,1:3
	 */
	public void testReadSpecsDirectiveFile() {

		VODirFileDesc specs = getDirFileDesc("Z:\\simple data set\\specs");

		assertEquals(7, specs.getNDirectives());

		List<Dir> directives = specs.readAllDirectives();

		Dir directive = directives.get(0);
		assertEquals(Arrays.asList(new String[] { "SHOW"}), getDirName(directive, specs));
		assertEquals("~ Grass Genera - specifications.", directive.args.get(0).text);

		// These are all "internal" directives so don't have args as their data
		// is
		// a part of the data set.
		directive = directives.get(1);
		assertEquals(Arrays.asList(new String[] { "NUMBER", "OF", "CHARACTERS"}), getDirName(directive, specs));
		assertEquals(0, directive.args.size());

		directive = directives.get(2);
		assertEquals(Arrays.asList(new String[] { "MAXIMUM", "NUMBER", "OF", "STATES" }), getDirName(directive, specs));
		assertEquals(0, directive.args.size());

		directive = directives.get(3);
		assertEquals(Arrays.asList(new String[] { "MAXIMUM", "NUMBER", "OF", "ITEMS" }), getDirName(directive, specs));
		assertEquals(0, directive.args.size());

		directive = directives.get(4);
		assertEquals(Arrays.asList(new String[] { "CHARACTER", "TYPES",}), getDirName(directive, specs));
		assertEquals(0, directive.args.size());

		directive = directives.get(5);
		assertEquals(Arrays.asList(new String[] { "NUMBERS", "OF", "STATES"}), getDirName(directive, specs));
		assertEquals(0, directive.args.size());

		directive = directives.get(6);
		assertEquals(Arrays.asList(new String[] { "IMPLICIT", "VALUES"}), getDirName(directive, specs));
		assertEquals(0, directive.args.size());

	}

	/**
	 * 
	 * The layout file looks like:
	 * 
	 * *COMMENT ~ Layout for natural-language descriptions.
	 * *REPLACE ANGLE BRACKETS 
	 * *OMIT CHARACTER NUMBERS 
	 * *OMIT INNER COMMENTS 
	 * *OMIT INAPPLICABLES
	 * 
	 * COMMENT Specify slot in which image links will appear in HTML
	 * descriptions. There must be a corresponding character in the character
	 * list, and no data should be recorded against this character in the item
	 * descriptions (the data come from the TAXON IMAGES directive in the file
	 * 'timages').
	 * 
	 * COMMENT : CHARACTER FOR TAXON IMAGES 88
	 * 
	 * COMMENT : EXCLUDE CHARACTER 89
	 * 
	 * COMMENT : NEW PARAGRAPHS AT CHARACTERS 1-2 12 25-26 68 77-78 87-89
	 * 
	 * COMMENT Group characters into sentences.
	 * 
	 * COMMENT : LINK CHARACTERS 3-5 7-9 10-11 13-15 16-17 18-21 26-29 32-37
	 * 41-42 45-47:52-54 48-51 55-56 57-59 62-63 64-65 68-70 71-72 73-75 78-84
	 * 
	 * COMMENT Headings within in taxon descriptions. The numbers specify the
	 * character before which the heading is placed.
	 * 
	 * COMMENT : ITEM SUBHEADINGS #2. \b{}Habit, vegetative morphology\b0{}.
	 * #12. \b{}Inflorescence\b0{}. #25. \b{}Female-sterile spikelets\b0{}. #26.
	 * \b{}Female-fertile spikelets, florets, fruit\b0{}. #68.
	 * \b{}Photosynthetic pathway, leaf blade anatomy\b0{}. #77. \b{}Special
	 * diagnostic feature.\b0{} #78. \b{}Taxonomy, distribution\b0{}. #87.
	 * \b{}Anatomical references\b0{}. #88. \b{}Illustrations\b0{}.
	 */
	public void testReadSomething() {

		VODirFileDesc layout = getDirFileDesc("layout");
		assertEquals(13, layout.getNDirectives());
		
		List<Dir> directives = layout.readAllDirectives();
		Dir directive = directives.get(0);
		assertEquals(Arrays.asList(new String[] { "COMMENT"}), getDirName(directive, layout));
		assertEquals("~ Layout for natural-language descriptions.", directive.args.get(0).text);

		directive = directives.get(1);
		assertEquals(Arrays.asList(new String[] {"REPLACE", "ANGLE", "BRACKETS" }), getDirName(directive, layout));
		assertEquals(0, directive.args.size());

		directive = directives.get(2);
		assertEquals(Arrays.asList(new String[] {"OMIT", "CHARACTER", "NUMBERS" }), getDirName(directive, layout));
		assertEquals(0, directive.args.size());

		directive = directives.get(3);
		assertEquals(Arrays.asList(new String[] {"OMIT", "INNER", "COMMENTS"}), getDirName(directive, layout));
		assertEquals(0, directive.args.size());

		directive = directives.get(4);
		assertEquals(Arrays.asList(new String[] {"OMIT", "INAPPLICABLES"}), getDirName(directive, layout));
		assertEquals(0, directive.args.size());
	}

	protected List<String> getDirName(Dir directive, VODirFileDesc fileDesc) {
		List<Directive> allDirectives = fileDesc.getDirArray();
		return Arrays.asList(allDirectives.get(directive.getDirType()).getName());
	}

	protected VODirFileDesc getDirFileDesc(int dirFileNum) {
		int id = _vop.getDeltaMaster().uniIdFromDirFileNo(dirFileNum);
		return (VODirFileDesc) _vop.getDescFromId(id);
	}

	protected VODirFileDesc getDirFileDesc(String fileName) {
		for (int i = 1; i <= _vop.getDeltaMaster().getNDirFiles(); i++) {
			VODirFileDesc dirFile = getDirFileDesc(i);
			System.out.println(dirFile.getFileName());
			if (fileName.equalsIgnoreCase(dirFile.getFileName())) {
				return dirFile;
			}
		}
		throw new RuntimeException("No directive file exists with name: " + fileName);
	}

}

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
package au.org.ala.delta.directives.validation;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.directives.ExcludeCharacters;
import au.org.ala.delta.directives.ExcludeItems;
import au.org.ala.delta.directives.IncludeCharacters;
import au.org.ala.delta.directives.IncludeItems;
import au.org.ala.delta.directives.IndexHeadings;
import au.org.ala.delta.directives.PrintWidth;

/**
 * Tests the IncompatibleDirectivesValidator class.
 */
public class IncompatibleDirectivesValidatorTest extends TestCase {

	private IncompatibleDirectivesValidator _validator;
	
	@Before
	public void setUp() {
		_validator = new IncompatibleDirectivesValidator();
	}
	@Test
	public void testDuplicateDirectives() {
		IndexHeadings indexHeadings = new IndexHeadings();
		try {
			_validator.validate(indexHeadings);
			_validator.validate(indexHeadings);
			fail("Validation exception expected");
		}
		catch (DirectiveException e) {}
		
		// Try again with a different instance of the directive.
		try {
			_validator.validate(new IndexHeadings());
			fail("Validation exception expected");
		}
		catch (DirectiveException e) {}
		
	}
	
	@Test
	public void testLevel0DirectivesCanBeDuplicated() {
		PrintWidth printWidth = new PrintWidth();
		
		try {
			_validator.validate(printWidth);
			_validator.validate(printWidth);
		}
		catch (DirectiveException e) {
			fail("Level 0 directives should be allowed to appear twice.");
		}
	}
	
	@Test
	public void testIncompatibleDirectives() {
		IncludeCharacters includeChars = new IncludeCharacters();
		ExcludeCharacters excludeChars = new ExcludeCharacters();
		IncludeItems includeItems = new IncludeItems();
		ExcludeItems excludeItems = new ExcludeItems();
		
		try {
			_validator.validate(includeChars);
			_validator.validate(includeItems);
		}
		catch (DirectiveException e) {
			fail("No error expected");
		}
		
		try {
			_validator.validate(excludeChars);
			fail("Error expected.");
		}	
		catch (DirectiveException e) {
		}
		
		try {
			_validator.validate(excludeItems);
			fail("Error expected.");
		}
		catch (DirectiveException e) {}
		
		
	}
}

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
package au.org.ala.delta.directives.args;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.IntegerValidator;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.StringReader;
import java.text.ParseException;

/**
 * Tests the IdListParser class.
 */
public class IdListParserTest extends TestCase {

	
	private IdListParser parserFor(String directiveArgs, IntegerValidator validator) {
		DeltaContext context = new DeltaContext();
		
		StringReader reader = new StringReader(directiveArgs);
		
		return new IdListParser(context, reader, validator);
	}
	
	/**
	 * This test checks the parser can handle correctly formatted text.
	 * It includes a single valued id and a range as well as a real value 
	 * and integer value.
	 */
	@Test
	public void testCorrectlyFormattedValue() throws ParseException {
		
		IdListParser parser = parserFor("1-3 4 5", null);
		
		parser.parse();
		
		DirectiveArguments args = parser.getDirectiveArgs();
		
		assertEquals(5, args.size());
		
		for (int i=1; i<=3; i++) {
			DirectiveArgument<?> arg = (DirectiveArgument<?>)args.get(i-1);
			
			assertEquals(Integer.valueOf(i), (Integer)arg.getId());
		}
	}
	
	@Test
	public void testIncorrectlyFormattedId() {
		IdListParser parser = parserFor("1a", null);
		
		try {
			parser.parse();
			fail("An exception should have been thrown");
		}
		catch (ParseException e) {
			assertEquals(1, e.getErrorOffset());
		}
	}
	
	@Test
	public void testIncorrectlyFormattedRange() {
		IdListParser parser = parserFor("12=13 14", null);
		
		try {
			parser.parse();
			fail("An exception should have been thrown");
		}
		catch (ParseException e) {
			assertEquals(2, e.getErrorOffset());
		}
	}
	
	@Test
	public void testIncorrectlyFormattedSeparator() {
		IdListParser parser = parserFor("12-13,14", null);
		
		try {
			parser.parse();
			fail("An exception should have been thrown");
		}
		catch (ParseException e) {
			assertEquals(5, e.getErrorOffset());
		}
	}
	
	
}

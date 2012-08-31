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
import au.org.ala.delta.directives.validation.IntegerRangeValidator;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.StringReader;
import java.text.ParseException;

/**
 * Tests the IntegerIdArgParser class.
 */
public class IntegerIdArgParserTest extends TestCase {

	
	private IntegerIdArgParser parserFor(String directiveArgs) {
		DeltaContext context = new DeltaContext();
        context.newParsingContext();
		
		StringReader reader = new StringReader(directiveArgs);
		
		return new IntegerIdArgParser(context, reader, new IntegerRangeValidator(-1000, 1000));
	}
	
	/**
	 * This test checks the parser can handle correctly formatted text.
	 * It includes a single valued id and a range as well as a real value 
	 * and integer value.
	 */
	@Test
	public void testCorrectlyFormattedValue() throws ParseException {
		
		IntegerIdArgParser parser = parserFor("1");
		
		parser.parse();
		
		DirectiveArguments args = parser.getDirectiveArgs();
		
		assertEquals(1, args.size());
		
		
		DirectiveArgument<?> arg = (DirectiveArgument<?>)args.get(0);
			
		assertEquals(Integer.valueOf(1), (Integer)arg.getId());
	}
	
	@Test
	public void testIncorrectlyFormattedId() {
		IntegerIdArgParser parser = parserFor("1a");
		
		try {
			parser.parse();
			fail("An exception should have been thrown");
		}
		catch (ParseException e) {
			assertEquals(0, e.getErrorOffset());
		}
	}
	
	@Test
	public void testIncorrectlyFormattedRange() {
		IntegerIdArgParser parser = parserFor("12 13 14");
		
		try {
			parser.parse();
			fail("An exception should have been thrown");
		}
		catch (ParseException e) {
			assertEquals(0, e.getErrorOffset());
		}
	}
}

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
package au.org.ala.delta.translation.attribute;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.translation.attribute.CommentedValueList.CommentedValues;
import au.org.ala.delta.translation.attribute.CommentedValueList.Values;

import junit.framework.TestCase;

/**
 * Tests the AttributeParser class.
 */
public class AttributeParserTest extends TestCase {

	private AttributeParser _parser;
	
	@Before
	public void setUp() {
		_parser = new AttributeParser();
	}
	
	@Test
	public void testParse() {
		String value = "1/2";
		
		CommentedValueList attribute = _parser.parse(value);
		
		assertEquals("", attribute.getCharacterComment());
		
		List<CommentedValues> commentedValues = attribute.getCommentedValues();
		
		assertEquals(2, commentedValues.size());
		
		assertEquals("", commentedValues.get(0).getComment());
		
		Values values = commentedValues.get(0).getValues();
		assertEquals("1", values.getValues().get(0));
		assertEquals("", values.getSeparator());
	}
	
	@Test
	public void testParseWithComments() {
		String value = "<character comment>1&2<comment>";

		CommentedValueList attribute = _parser.parse(value);
		
		assertEquals("<character comment>", attribute.getCharacterComment());
		
		List<CommentedValues> commentedValues = attribute.getCommentedValues();
		
		assertEquals(1, commentedValues.size());
		
		assertEquals("<comment>", commentedValues.get(0).getComment());
		Values values = commentedValues.get(0).getValues();
		assertEquals("1", values.getValues().get(0));
		assertEquals("2", values.getValues().get(1));
		assertEquals("&", values.getSeparator());
	}
	
	@Test
	public void testParseComplexAttributeWithComments() {
		String value = "<character comment>1<comment 1>/1&2<comment 2<nested comment>>/3-4";

		CommentedValueList attribute = _parser.parse(value);
		
		assertEquals("<character comment>", attribute.getCharacterComment());
		
		List<CommentedValues> commentedValues = attribute.getCommentedValues();
		
		assertEquals(3, commentedValues.size());
		
		assertEquals("<comment 1>", commentedValues.get(0).getComment());
		Values values = commentedValues.get(0).getValues();
		assertEquals("1", values.getValues().get(0));
		
		assertEquals("<comment 2<nested comment>>", commentedValues.get(1).getComment());
		values = commentedValues.get(1).getValues();
		assertEquals("1", values.getValues().get(0));
		assertEquals("2", values.getValues().get(1));
		assertEquals("&", values.getSeparator());
		
		assertEquals("", commentedValues.get(2).getComment());
		values = commentedValues.get(2).getValues();
		assertEquals("3", values.getValues().get(0));
		assertEquals("4", values.getValues().get(1));
		assertEquals("-", values.getSeparator());
	}
	
	@Test
	public void testParseComplexAttribute() {
		String attributeValue = "<comment>1&2&3<comment 2>/1&3<comment 3>";
		CommentedValueList attribute = _parser.parse(attributeValue);
		assertEquals("<comment>", attribute.getCharacterComment());
		
		List<CommentedValues> commentedValues = attribute.getCommentedValues();
		
		assertEquals(2, commentedValues.size());
		
		assertEquals("<comment 2>", commentedValues.get(0).getComment());
		Values values = commentedValues.get(0).getValues();
		assertEquals("1", values.getValues().get(0));
		assertEquals("2", values.getValues().get(1));
		assertEquals("3", values.getValues().get(2));
		assertEquals("&", values.getSeparator());
		
		
		assertEquals("<comment 3>", commentedValues.get(1).getComment());
		values = commentedValues.get(1).getValues();
		assertEquals("1", values.getValues().get(0));
		assertEquals("3", values.getValues().get(1));
		assertEquals("&", values.getSeparator());
	}
	
	@Test
	public void testSpecialCharacterInComment() {
		String attributeValue = "2<1/1>/7<3/3>";
		CommentedValueList attribute = _parser.parse(attributeValue);
		
		assertEquals("", attribute.getCharacterComment());
		
		List<CommentedValues> commentedValues = attribute.getCommentedValues();
		assertEquals(2, commentedValues.size());
		assertEquals(1, commentedValues.get(0).getNumValues());
		assertEquals("2", commentedValues.get(0).getValue(0));
		assertEquals("<1/1>", commentedValues.get(0).getComment());
		
		assertEquals(1, commentedValues.get(1).getNumValues());
		assertEquals("7", commentedValues.get(1).getValue(0));
		assertEquals("<3/3>", commentedValues.get(1).getComment());
	
	}
}

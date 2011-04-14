package au.org.ala.delta.translation.attribute;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.translation.attribute.ParsedAttribute.CommentedValues;
import au.org.ala.delta.translation.attribute.ParsedAttribute.Values;

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
		
		ParsedAttribute attribute = _parser.parse(value);
		
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

		ParsedAttribute attribute = _parser.parse(value);
		
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

		ParsedAttribute attribute = _parser.parse(value);
		
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
		ParsedAttribute attribute = _parser.parse(attributeValue);
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
	
}

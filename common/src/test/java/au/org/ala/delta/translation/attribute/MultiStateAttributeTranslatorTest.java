package au.org.ala.delta.translation.attribute;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;

/**
 * Tests the MultiStateAttributeTranslator class.
 */
public class MultiStateAttributeTranslatorTest extends TestCase {

	
	private DefaultDataSetFactory _factory;
	private MultiStateCharacter _multiStateCharacter;
	private MultiStateAttributeTranslator _translator;
	
	
	@Before
	public void setUp() throws Exception {
		_factory = new DefaultDataSetFactory();
		_multiStateCharacter = (MultiStateCharacter)_factory.createCharacter(CharacterType.UnorderedMultiState, 1);
		_multiStateCharacter.setNumberOfStates(6);
		_multiStateCharacter.setDescription("A multistate character");
		_multiStateCharacter.setState(1, "State 1");
		_multiStateCharacter.setState(2, "State 2");
		_multiStateCharacter.setState(3, "State 3");
		_multiStateCharacter.setState(4, "<State 4>");
		_multiStateCharacter.setState(5, "State 5");
		_multiStateCharacter.setState(6, "State 6");
		AttributeFormatter attributeFormatter = new AttributeFormatter(false, true);
		CharacterFormatter characterFormatter = new CharacterFormatter(false, true, false, true);
		_translator = new MultiStateAttributeTranslator(_multiStateCharacter, characterFormatter, attributeFormatter);
	}
	
	/**
	 * Tests MultiStateAttributes can be formatted correctly.
	 */
	@Test
	public void testFormatMultistateAttribute() {
		
		String attributeValue = "1/2";
		String value = format(_multiStateCharacter, attributeValue);
		
		assertEquals("State 1, or State 2", value);
	}
	
	
	/**
	 * Tests MultiStateAttributes can be formatted correctly.
	 */
	@Test
	public void testFormatMultistateAttributeWithTrailingCommentOnly() {
		
		String attributeValue = "1/2<comment>";
		String value = format(_multiStateCharacter, attributeValue);
		
		assertEquals("State 1, or State 2 <comment>", value);
	}

	/**
	 * Tests MultiStateAttributes with comments can be formatted correctly.
	 */
	@Test
	public void testFormatMultistateAttributeWithComments() {
		
		String attributeValue = "<character comment>1<state comment 1>/2<state comment 2>";
		String value = format(_multiStateCharacter, attributeValue);
		
		assertEquals("<character comment> State 1 <state comment 1>, or State 2 <state comment 2>", value);
	}
	
	/**
	 * Tests a MultiState character attribute is formatted correctly when it contains only
	 * a comment but no state values.
	 */
	@Test
	public void testFormatMultistateAttributeWithCommentOnly() {
		String attributeValue = "<just a comment>";
		String value = format(_multiStateCharacter, attributeValue);
		
		assertEquals("<just a comment>", value);
	}

	/**
	 * Tests a MultiState character attribute containing the "&" separator is formatted correctly.
	 */
	@Test
	public void testFormatMultistateAttributeWithAnd() {
		String attributeValue = "<comment>1&2<comment 2>";
		String value = format(_multiStateCharacter, attributeValue);
		
		assertEquals("<comment> State 1 and State 2 <comment 2>", value);
		
		attributeValue = "<comment>1&2&3<comment 2>/1&3<comment 3>";
		value = format(_multiStateCharacter, attributeValue);
		
		assertEquals("<comment> State 1, State 2, and State 3 <comment 2>, or State 1 and State 3 <comment 3>", value);
	}
	
	/**
	 * Tests a MultiState character attribute containing the "-" separator is formatted correctly.
	 */
	@Test
	public void testFormatMultistateAttributeWithRange() {
		String attributeValue = "<comment>1-2<comment 2>";
		String value = format(_multiStateCharacter, attributeValue);
		
		assertEquals("<comment> State 1 to State 2 <comment 2>", value);
		
		attributeValue = "<comment>1-2-3<comment 2>/1&3<comment 3>";
		value = format(_multiStateCharacter, attributeValue);
		
		assertEquals("<comment> State 1 to State 2 to State 3 <comment 2>, or State 1 and State 3 <comment 3>", value);
	}
	
	/**
	 * To allow tidy formatting of attributes in a DELTA file, comments can be used to escape
	 * a newline character that would otherwise signify the end of the attribute.
	 * These comments don't appear in the natural language output.
	 */
	@Test
	public void testOmitEmptyComments() {
		
		String attributeValue = "1<\n>/2<>/3<test>";
		String value = format(_multiStateCharacter, attributeValue);
		
		assertEquals("State 1, or State 2, or State 3 <test>", value);
		
		attributeValue = "1<\r\n>/2< >/3<test>";
		value = format(_multiStateCharacter, attributeValue);
		
		assertEquals("State 1, or State 2, or State 3 <test>", value);
		
		attributeValue = "1<\t>/2<	>/3<test>";
		value = format(_multiStateCharacter, attributeValue);
		
		assertEquals("State 1, or State 2, or State 3 <test>", value);
	}
	
	@Test
	public void testStateValueOnlyContainComments() {
		
		String attributeValue = "1/2/3/4";
		String value = format(_multiStateCharacter, attributeValue);
		
		assertEquals("State 1, or State 2, or State 3", value);
		
		attributeValue = "3/4/5";
		value = format(_multiStateCharacter, attributeValue);
			
		assertEquals("State 3, or State 5", value);
		
		attributeValue = "4/5";
		value = format(_multiStateCharacter, attributeValue);
			
		assertEquals("State 5", value);
		
	}
	
	
	private String format(au.org.ala.delta.model.Character character, String value) {
		AttributeParser parser = new AttributeParser();
		
		return _translator.translate(parser.parse(value));
	}

}

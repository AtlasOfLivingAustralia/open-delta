package au.org.ala.delta.directives.args;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.DeltaContext;

/**
 * Tests the IdValueListParser class.
 */
public class IdValueListParserTest extends TestCase {

	
	private IdValueListParser parserFor(String directiveArgs) {
		DeltaContext context = new DeltaContext();
		
		StringReader reader = new StringReader(directiveArgs);
		
		return new IdValueListParser(context, reader);
	}
	
	/**
	 * This test checks the parser can handle correctly formatted text.
	 * It includes a single valued id and a range as well as a real value 
	 * and integer value.
	 */
	@Test
	public void testCorrectlyFormattedValue() throws ParseException {
		
		IdValueListParser parser = parserFor("1-3,4.1 5,2");
		
		parser.parse();
		
		DirectiveArguments args = parser.getDirectiveArgs();
		
		assertEquals(4, args.size());
		
		for (int i=1; i<=3; i++) {
			DirectiveArgument<?> arg = (DirectiveArgument<?>)args.get(i-1);
			
			assertEquals(Integer.valueOf(i), (Integer)arg.getId());
			assertEquals(new BigDecimal("4.1"), arg.getValue());
		}
		DirectiveArgument<?> arg = (DirectiveArgument<?>)args.get(3);
		assertEquals(Integer.valueOf(5), (Integer)arg.getId());
		assertEquals(new BigDecimal("2"), arg.getValue());
	}
	
	@Test
	public void testIncorrectlyFormattedId() {
		IdValueListParser parser = parserFor("a,1");
		
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
		IdValueListParser parser = parserFor("12-13a,1");
		
		try {
			parser.parse();
			fail("An exception should have been thrown");
		}
		catch (ParseException e) {
			assertEquals(5, e.getErrorOffset());
		}
	}
	
	@Test
	public void testIncorrectlyFormattedValue() {
		IdValueListParser parser = parserFor("12-13,a");
		
		try {
			parser.parse();
			fail("An exception should have been thrown");
		}
		catch (ParseException e) {
			assertEquals(6, e.getErrorOffset());
		}
	}
	
	@Test
	public void testIncorrectlyFormattedSeparator() {
		IdValueListParser parser = parserFor("12-13 1");
		
		try {
			parser.parse();
			fail("An exception should have been thrown");
		}
		catch (ParseException e) {
			assertEquals(5, e.getErrorOffset());
		}
	}
	
}

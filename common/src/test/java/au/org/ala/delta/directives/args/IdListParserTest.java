package au.org.ala.delta.directives.args;

import java.io.StringReader;
import java.text.ParseException;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.DeltaContext;

/**
 * Tests the IdListParser class.
 */
public class IdListParserTest extends TestCase {

	
	private IdListParser parserFor(String directiveArgs) {
		DeltaContext context = new DeltaContext();
		
		StringReader reader = new StringReader(directiveArgs);
		
		return new IdListParser(context, reader);
	}
	
	/**
	 * This test checks the parser can handle correctly formatted text.
	 * It includes a single valued id and a range as well as a real value 
	 * and integer value.
	 */
	@Test
	public void testCorrectlyFormattedValue() throws ParseException {
		
		IdListParser parser = parserFor("1-3 4 5");
		
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
		IdListParser parser = parserFor("1a");
		
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
		IdListParser parser = parserFor("12=13 14");
		
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
		IdListParser parser = parserFor("12-13,14");
		
		try {
			parser.parse();
			fail("An exception should have been thrown");
		}
		catch (ParseException e) {
			assertEquals(5, e.getErrorOffset());
		}
	}
	
	
}

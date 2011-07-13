package au.org.ala.delta.directives.args;

import java.io.StringReader;
import java.text.ParseException;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.DeltaContext;

/**
 * Tests the IntegerIdArgParser class.
 */
public class IntegerIdArgParserTest extends TestCase {

	
	private IntegerIdArgParser parserFor(String directiveArgs) {
		DeltaContext context = new DeltaContext();
		
		StringReader reader = new StringReader(directiveArgs);
		
		return new IntegerIdArgParser(context, reader);
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

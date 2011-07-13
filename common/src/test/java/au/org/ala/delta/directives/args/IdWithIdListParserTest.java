package au.org.ala.delta.directives.args;

import java.io.StringReader;
import java.text.ParseException;

import junit.framework.TestCase;

import org.junit.Test;

import au.org.ala.delta.DeltaContext;

/**
 * Tests the IdWithIdListParser class.
 */
public class IdWithIdListParserTest extends TestCase {

	
	private IdWithIdListParser parserFor(String directiveArgs) {
		DeltaContext context = new DeltaContext();
		
		StringReader reader = new StringReader(directiveArgs);
		
		return new IdWithIdListParser(context, reader);
	}
	
	/**
	 * This test checks the parser can handle correctly formatted text.
	 */
	@Test
	public void testSingleArgWithIntegerId() throws ParseException {
		
		IdWithIdListParser parser = parserFor("#1. 2-3 1");
		
		parser.parse();
		
		DirectiveArguments args = parser.getDirectiveArgs();
		
		assertEquals(1, args.size());
		
		
		DirectiveArgument<?> arg = (DirectiveArgument<?>)args.get(0);
			
		assertEquals(Integer.valueOf(1), (Integer)arg.getId());
		assertEquals(3, arg.getDataList().size());
		int[] expected = new int[] {2,3,1};
		
		for (int i=0; i<expected.length; i++) {
			assertEquals(Integer.valueOf(expected[i]), arg.getDataList().get(i));
		}
	}
	
	/**
	 * This test checks the parser can handle correctly formatted text.
	 */
	@Test
	public void testMultipleArgsWithStringId() throws ParseException {
		
		IdWithIdListParser parser = parserFor("# Item 1/ 1 13\n#Item 2/ 2-4");
		
		parser.parse();
		
		DirectiveArguments args = parser.getDirectiveArgs();
		
		assertEquals(2, args.size());
		
		
		DirectiveArgument<?> arg = (DirectiveArgument<?>)args.get(0);
			
		assertEquals("Item 1", (String)arg.getId());
		assertEquals(2, arg.getDataList().size());
		int[] expected = new int[] {1, 13};
		
		for (int i=0; i<expected.length; i++) {
			assertEquals(Integer.valueOf(expected[i]), arg.getDataList().get(i));
		}
		
		arg = (DirectiveArgument<?>)args.get(1);
		
		assertEquals("Item 2", (String)arg.getId());
		assertEquals(3, arg.getDataList().size());
		expected = new int[] {2, 3, 4};
		
		for (int i=0; i<expected.length; i++) {
			assertEquals(Integer.valueOf(expected[i]), arg.getDataList().get(i));
		}
	}
	
}

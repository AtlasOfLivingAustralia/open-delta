package au.org.ala.delta.directives.args;

import java.io.StringReader;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.impl.DefaultDataSet;


/**
 * Tests the KeyStateParser class.
 */
public class KeyStateParserTest extends TestCase {

	
	private DeltaContext _context;
	private DeltaDataSet _dataSet;
	
	@Before
	public void setUp() {
		DefaultDataSetFactory factory = new DefaultDataSetFactory();
		_dataSet = (DefaultDataSet)factory.createDataSet("test");
		_context = new DeltaContext(_dataSet);
		
		_dataSet.addCharacter(CharacterType.UnorderedMultiState);
		_dataSet.addCharacter(CharacterType.OrderedMultiState);
		_dataSet.addCharacter(CharacterType.Text);
		_dataSet.addCharacter(CharacterType.IntegerNumeric);
		_dataSet.addCharacter(CharacterType.RealNumeric);
		_dataSet.addCharacter(CharacterType.UnorderedMultiState);
		_dataSet.addCharacter(CharacterType.UnorderedMultiState);
		
		
		_context = new DeltaContext(_dataSet);
	}
	
	private KeyStateParser parserFor(String data) {
		return new KeyStateParser(_context, new StringReader(data));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testParseUnorderedMultiStateChar() throws Exception {
		
		KeyStateParser parser = parserFor("1,1&3/2/4");
		parser.parse();
		
		DirectiveArguments args = parser.getDirectiveArgs();
		assertEquals(3, args.size());
		
		DirectiveArgument<Integer> arg = (DirectiveArgument<Integer>)args.get(0);
		checkArg(arg, 1, 1, 1, 3);
		arg = (DirectiveArgument<Integer>)args.get(1);
		checkArg(arg, 1, 2, 2);
		arg = (DirectiveArgument<Integer>)args.get(2);
		checkArg(arg, 1, 3, 4);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testParseOrderedMultiStateChar() throws Exception {
		
		KeyStateParser parser = parserFor("2,1/2-3/4");
		parser.parse();
		
		DirectiveArguments args = parser.getDirectiveArgs();
		assertEquals(3, args.size());
		
		DirectiveArgument<Integer> arg = (DirectiveArgument<Integer>)args.get(0);
		checkArg(arg, 2, 1, 1);
		arg = (DirectiveArgument<Integer>)args.get(1);
		checkArg(arg, 2, 2, 2, 3);
		arg = (DirectiveArgument<Integer>)args.get(2);
		checkArg(arg, 2, 3, 4);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testParseNumericChar() throws Exception {
		
		KeyStateParser parser = parserFor("5,~1.1/1.1-3/3~");
		parser.parse();
		
		DirectiveArguments args = parser.getDirectiveArgs();
		assertEquals(3, args.size());
		
		DirectiveArgument<Integer> arg = (DirectiveArgument<Integer>)args.get(0);
		checkFloatArg(arg, 5, 1, -Float.MAX_VALUE, 1.1f);
		arg = (DirectiveArgument<Integer>)args.get(1);
		checkFloatArg(arg, 5, 2, 1.1f, 3f);
		arg = (DirectiveArgument<Integer>)args.get(2);
		checkFloatArg(arg, 5, 3, 3f, Float.MAX_VALUE);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testMultipleChars() throws Exception {
		
		KeyStateParser parser = parserFor("2,1/2/3 4,~1.3/1.3~ 5,~1.1/1.1-3/3~");
		parser.parse();
		
		DirectiveArguments args = parser.getDirectiveArgs();
		assertEquals(8, args.size());
		
		DirectiveArgument<Integer> arg = (DirectiveArgument<Integer>)args.get(0);
		checkArg(arg, 2, 1, 1);
		arg = (DirectiveArgument<Integer>)args.get(1);
		checkArg(arg, 2, 2, 2);
		arg = (DirectiveArgument<Integer>)args.get(2);
		checkArg(arg, 2, 3, 3);
		
		arg = (DirectiveArgument<Integer>)args.get(3);
		checkFloatArg(arg, 4, 1, -Float.MAX_VALUE, 1.3f);
		arg = (DirectiveArgument<Integer>)args.get(4);
		checkFloatArg(arg, 4, 2, 1.3f, Float.MAX_VALUE);
		
		arg = (DirectiveArgument<Integer>)args.get(5);
		checkFloatArg(arg, 5, 1, -Float.MAX_VALUE, 1.1f);
		arg = (DirectiveArgument<Integer>)args.get(6);
		checkFloatArg(arg, 5, 2, 1.1f, 3f);
		arg = (DirectiveArgument<Integer>)args.get(7);
		checkFloatArg(arg, 5, 3, 3f, Float.MAX_VALUE);
		
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testMultipleIds() throws Exception {
		
		KeyStateParser parser = parserFor("6-7,1/2&3");
		parser.parse();
		
		DirectiveArguments args = parser.getDirectiveArgs();
		assertEquals(4, args.size());
		
		DirectiveArgument<Integer> arg = (DirectiveArgument<Integer>)args.get(0);
		checkArg(arg, 6, 1, 1);
		arg = (DirectiveArgument<Integer>)args.get(1);
		checkArg(arg, 7, 1, 1);
		
		arg = (DirectiveArgument<Integer>)args.get(2);
		checkArg(arg, 6, 2, 2, 3);
		
		arg = (DirectiveArgument<Integer>)args.get(3);
		checkArg(arg, 7, 2, 2, 3);
	}
	
	private void checkArg(DirectiveArgument<Integer> arg, int id, int value, int... values) {
		assertEquals((Integer)id, arg.getId());
		assertEquals(value, arg.getValueAsInt());
		for (int i=0; i<values.length; i++) {
			assertEquals((Integer)values[i], arg.getDataList().get(i));
		}
	}
	
	private void checkFloatArg(DirectiveArgument<Integer> arg, int id, int value, float... values) {
		assertEquals((Integer)id, arg.getId());
		assertEquals(value, arg.getValueAsInt());
		for (int i=0; i<values.length; i++) {
			assertEquals((Float)values[i], arg.getData().get(i).floatValue());
		}
	}
	
}

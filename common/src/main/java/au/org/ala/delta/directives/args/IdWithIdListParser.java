package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext;

/**
 * The IdWithIdListParser parses directive arguments in the form:
 * 
 * #firstid1 id1 id2 idn
 * #firstid2 id1 id2 idn
 * 
 * The firstid can be numeric or an item description.
 * 
 * This argument format is used by directives such as: EMPHASIZE CHARACTERS and
 * ADD CHARACTERS.
 * 
 */
public class IdWithIdListParser extends DirectiveArgsParser {

	
	public IdWithIdListParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	public void parse() throws ParseException {
		
		_args = new DirectiveArguments();
		readNext();
		skipWhitespace();
		while (_currentInt > 0) {
			
			expect('#');
			
			readIdAndIdList();
		}
	}
	
	
	private void readIdAndIdList() throws ParseException {
		
		Object id = readId();
		skipWhitespace();
		
		DirectiveArgument<?> arg;
		if (id instanceof String) {
			arg = new DirectiveArgument<String>((String)id);
		}
		else {
			arg = new DirectiveArgument<Integer>((Integer)id);
		}
		
		while (_currentInt >=0 && _currentChar != '#') {
			
			IntRange ids = readIds();
			for (int tmpId : ids.toArray()) {
				arg.add(tmpId);
			}
			
			skipWhitespace();
		}
		
		_args.add(arg);
		
	}
	
	protected Object readId() throws ParseException {
		expect('#');
		
		mark();
		readNext();
		if (Character.isDigit(_currentChar)) {
			reset();
			
			return readListId();
		}
		else {
			reset();
			
			return readItemDescription();
		}
	}
}

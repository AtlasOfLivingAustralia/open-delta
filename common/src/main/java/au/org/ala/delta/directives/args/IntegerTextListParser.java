package au.org.ala.delta.directives.args;
import java.io.Reader;
import java.text.ParseException;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext;


public class IntegerTextListParser extends TextListParser<IntRange> {

	public IntegerTextListParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	protected void readSingle() throws ParseException {
		
		IntRange ids = readId();
		String comment = readOptionalComment();
		String value = readText();
		for (int id : ids.toArray()) {
			_args.addDirectiveArgument(id, comment, value);
		}
	}
	
	@Override
	protected IntRange readId() throws ParseException {
		expect(MARK_IDENTIFIER);
		
		readNext();
		IntRange ids = readIds();
		expect('.');
	    readNext();  // consume the . character.
	    return ids;
		
	}

}

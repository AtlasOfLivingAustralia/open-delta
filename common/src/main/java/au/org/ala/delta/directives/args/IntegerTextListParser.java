package au.org.ala.delta.directives.args;
import java.io.Reader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;


public class IntegerTextListParser extends TextListParser<Integer> {

	public IntegerTextListParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	protected Integer readId() throws ParseException {
		return readListId();
	}

}

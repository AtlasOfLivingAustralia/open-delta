package au.org.ala.delta.directives.args;
import java.io.Reader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;


public class StringTextListParser extends TextListParser<String> {

	public StringTextListParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	protected String readId() throws ParseException {
		return readItemDescription();
	}

}

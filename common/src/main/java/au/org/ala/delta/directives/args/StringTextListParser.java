package au.org.ala.delta.directives.args;
import java.io.Reader;

import au.org.ala.delta.DeltaContext;


public class StringTextListParser extends TextListParser<String> {

	public StringTextListParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	protected String readId() throws Exception {
		return readItemDescription();
	}

}

package au.org.ala.delta.directives;
import java.io.Reader;

import au.org.ala.delta.DeltaContext;


public class StringTextListParser extends TextListParser<String> {

	public StringTextListParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	protected String readId() throws Exception {
		expect(MARK_IDENTIFIER);
		
		readNext();
		
		StringBuilder id = new StringBuilder();
		while (_currentChar != '/') {
			id.append(_currentChar);
		}
		
		expect('/');
	    readNext();  // consume the / character.
	    return id.toString();
	}

}

package au.org.ala.delta.directives;
import java.io.Reader;

import au.org.ala.delta.DeltaContext;


public class IntegerTextListParser extends TextListParser<Integer> {

	public IntegerTextListParser(DeltaContext context, Reader reader) {
		super(context, reader);
	}
	
	@Override
	protected Integer readId() throws Exception {
		expect(MARK_IDENTIFIER);
		
		readNext();
		
		int id = readInteger();
		
		expect('.');
	    readNext();  // consume the . character.
	    return id;
	}

}

package au.org.ala.delta.delfor.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.delfor.DelforDirectiveFileParser;
import au.org.ala.delta.directives.DirectiveParser;

public class InputFile extends au.org.ala.delta.directives.InputFile {

	@Override
	protected DirectiveParser<DeltaContext> createParser() {
		return DelforDirectiveFileParser.createInstance();
	}

	
}

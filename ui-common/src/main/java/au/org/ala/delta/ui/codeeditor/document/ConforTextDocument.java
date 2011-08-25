package au.org.ala.delta.ui.codeeditor.document;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.ConforDirectiveFileParser;
import au.org.ala.delta.directives.DirectiveParser;

public class ConforTextDocument extends DirectiveTextDocument<DeltaContext> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected DirectiveParser<DeltaContext> getDirectiveParser() {
		return ConforDirectiveFileParser.createInstance();
	}
	
	@Override
	public String getMimeType() {
		return "text/confor";
	}

}

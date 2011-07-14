package au.org.ala.delta.editor.directives;

import java.util.List;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParserObserver;

/**
 * Handler to allow callbacks during the directive import process.
 */
public interface DirectiveImportHandler extends DirectiveParserObserver {
	
	public void handleUnrecognizedDirective(ImportContext context, List<String> controlWords);

	public void handleDirectiveProcessingException(
			ImportContext context, AbstractDirective<ImportContext> d, Exception ex);
    
}

package au.org.ala.delta.editor.directives;

import java.util.List;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;

/**
 * Provides an empty implementation of the DirectiveImportHandler.
 */
public class DirectiveImportHandlerAdapter implements DirectiveImportHandler {

	@Override
	public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {
	}

	@Override
	public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {
	}

	@Override
	public void handleUnrecognizedDirective(ImportContext context, List<String> controlWords) {
	}

	@Override
	public void handleDirectiveProcessingException(ImportContext context, AbstractDirective<ImportContext> d,
			Exception ex) {
	}

}

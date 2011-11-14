package au.org.ala.delta.directives;

import au.org.ala.delta.directives.validation.DirectiveException;

public interface DirectiveParserObserver {

    void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) throws DirectiveException;
    void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive);
	void finishedProcessing();
	public void handleDirectiveProcessingException(AbstractDeltaContext context, AbstractDirective<? extends AbstractDeltaContext> directive,Exception ex) throws DirectiveException;
    
}

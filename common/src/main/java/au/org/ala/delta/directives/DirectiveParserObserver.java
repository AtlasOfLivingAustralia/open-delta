package au.org.ala.delta.directives;

public interface DirectiveParserObserver {

    void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data);
    void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive);
	void finishedProcessing();
	public void handleDirectiveProcessingException(AbstractDeltaContext context, AbstractDirective<? extends AbstractDeltaContext> directive,Exception ex);
    
}

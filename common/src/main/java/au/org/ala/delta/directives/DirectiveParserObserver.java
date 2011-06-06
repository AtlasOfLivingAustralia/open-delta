package au.org.ala.delta.directives;

public interface DirectiveParserObserver {

    void preProcess(String data);
    void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive);
    
    
}

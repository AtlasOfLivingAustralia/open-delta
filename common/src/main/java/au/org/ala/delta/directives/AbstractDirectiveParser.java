package au.org.ala.delta.directives;

public class AbstractDirectiveParser<T extends AbstractDeltaContext> {
    
    protected void register(AbstractDirective<T> dir) {
        
    }
    
    public AbstractDirectiveParser(T context) {        
    }
    
    public void parse() {
        
    }
    
    protected void processDirective(Directive d, T context) {
        
    }

}

package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;

public class ConforDirectiveParserObserver implements DirectiveParserObserver {

    private DeltaContext _context; 
    
    public ConforDirectiveParserObserver(DeltaContext context) {
        _context = context;
        _context.setDirectiveParserObserver(this);
    }
    
    @Override
    public void preProcess(String data) {
        _context.ListMessage(data);
    }

    @Override
    public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {
        // do nothing
    }
    

}

package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.directives.DirectiveParser;

public class IntkeyDirectiveParser extends DirectiveParser<IntkeyContext> {
    
    //private constructor - use factory method to get an instance.
    private IntkeyDirectiveParser() {}
    
    public static IntkeyDirectiveParser createInstance() {
        IntkeyDirectiveParser instance = new IntkeyDirectiveParser();
        
        instance.register(new FileTaxaDirective());
        
        return instance;
    }
    
    
}

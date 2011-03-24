package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.directives.AbstractDirectiveParser;

public class IntkeyDirectiveParser extends AbstractDirectiveParser<IntkeyContext> {
    
    public IntkeyDirectiveParser() {
        super(new IntkeyContext());
        //register(new MyIntKeyDir());
    }   
}

package au.org.ala.delta.intkey.directives;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.directives.ParsingContext;

public class IntkeyDirectiveParser extends DirectiveParser<IntkeyContext> {
    
    //private constructor - use factory method to get an instance.
    private IntkeyDirectiveParser() {}
    
    public static IntkeyDirectiveParser createInstance() {
        IntkeyDirectiveParser instance = new IntkeyDirectiveParser();
        
        instance.registerDirective(new FileCharactersDirective());
        instance.registerDirective(new FileTaxaDirective());
        instance.registerDirective(new NewDatasetDirective());
        
        return instance;
    }

    @Override
    protected void handleUnrecognizedDirective(ParsingContext pc, List<String> controlWords) {
        System.out.println("Ignoring unrecognized directive: " + StringUtils.join(controlWords, " "));
    }
    
    
}

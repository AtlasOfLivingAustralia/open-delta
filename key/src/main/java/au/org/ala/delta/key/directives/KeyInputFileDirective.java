package au.org.ala.delta.key.directives;

import java.io.File;

import au.org.ala.delta.directives.DirectiveParserObserver;
import au.org.ala.delta.key.KeyContext;

public class KeyInputFileDirective extends AbstractKeyInputFileDirective {
    
    public KeyInputFileDirective() {
        super("input", "file");
    }

    @Override
    void parseFile(File file, KeyContext context) throws Exception {
        KeyDirectiveParser parser = KeyDirectiveParser.createInstance();
        DirectiveParserObserver observer = context.getDirectiveParserObserver();
        if (observer != null) {
            parser.registerObserver(observer);
        }
        parser.parse(file, context);  
    }

}

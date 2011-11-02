package au.org.ala.delta.key.directives;

import java.util.List;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.key.KeyContext;

public class KeyDirectiveFileParser extends DirectiveParser<KeyContext> {

    // Private constructor, must use factory method to get an instance
    private KeyDirectiveFileParser() {

    }

    public static KeyDirectiveFileParser createInstance() {
        KeyDirectiveFileParser instance = new KeyDirectiveFileParser();

        // instance.registerDirective(new CharacterWeights());

        return instance;
    }

    @Override
    protected void handleUnrecognizedDirective(KeyContext context, List<String> controlWords) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void handleDirectiveProcessingException(KeyContext context, AbstractDirective<KeyContext> d, Exception ex) {
        // TODO Auto-generated method stub

    }

}

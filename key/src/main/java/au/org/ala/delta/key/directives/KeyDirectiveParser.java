package au.org.ala.delta.key.directives;

import java.util.List;

import au.org.ala.delta.directives.CharacterReliabilities;
import au.org.ala.delta.directives.Comment;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.directives.ExcludeCharacters;
import au.org.ala.delta.directives.ExcludeItems;
import au.org.ala.delta.directives.Heading;
import au.org.ala.delta.directives.IncludeCharacters;
import au.org.ala.delta.directives.IncludeItems;
import au.org.ala.delta.directives.ItemAbundances;
import au.org.ala.delta.key.KeyContext;

public class KeyDirectiveParser extends DirectiveParser<KeyContext> {

    // Private constructor, must use factory method to get an instance
    private KeyDirectiveParser() {

    }

    public static KeyDirectiveParser createInstance() {
        KeyDirectiveParser instance = new KeyDirectiveParser();
        instance.registerDirective(new ABaseDirective());
        instance.registerDirective(new AddCharacterNumbersDirective());
        instance.registerDirective(new CharactersFileDirective());
        instance.registerDirective(new CharacterReliabilities());
        instance.registerDirective(new Comment());
        instance.registerDirective(new ExcludeCharacters());
        instance.registerDirective(new ExcludeItems());
        instance.registerDirective(new Heading());
        instance.registerDirective(new IncludeCharacters());
        instance.registerDirective(new IncludeItems());
        instance.registerDirective(new KeyInputFileDirective());
        instance.registerDirective(new ItemAbundances());
        instance.registerDirective(new ItemsFileDirective());
        instance.registerDirective(new RBaseDirective());
        instance.registerDirective(new ReuseDirective());
        instance.registerDirective(new VaryWtDirective());

        return instance;
    }

    @Override
    protected void handleUnrecognizedDirective(KeyContext context, List<String> controlWords) {
        //System.err.println("Unrecognized directive " + StringUtils.join(controlWords, " "));
    }

}

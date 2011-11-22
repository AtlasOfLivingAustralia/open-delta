package au.org.ala.delta.key.directives;

import java.io.File;

import au.org.ala.delta.key.KeyContext;

public class CharactersFileDirective extends AbstractKeyInputFileDirective {
    
    public CharactersFileDirective() {
        super("characters", "file");
    }

    @Override
    void parseFile(File file, KeyContext context) throws Exception {
        context.setCharactersFile(file);
    }

}

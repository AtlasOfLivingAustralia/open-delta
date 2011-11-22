package au.org.ala.delta.key.directives;

import java.io.File;

import au.org.ala.delta.key.KeyContext;

public class ItemsFileDirective extends AbstractKeyInputFileDirective {
    
    public ItemsFileDirective() {
        super("items", "file");
    }

    @Override
    void parseFile(File file, KeyContext context) throws Exception {
        context.setItemsFile(file);
    }

}

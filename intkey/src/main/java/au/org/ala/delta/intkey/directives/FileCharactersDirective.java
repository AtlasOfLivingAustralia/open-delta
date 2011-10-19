package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.FileCharactersDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

/**
 * The FILE CHARACTERS directive - specifies the name of the intkey characters
 * file. It is normally only used in the initialization file - intkey.ini.
 * 
 * @author ChrisF
 * 
 */
public class FileCharactersDirective extends NewIntkeyDirective {

    public FileCharactersDirective() {
        super(false, "file", "characters");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new FileArgument("file", "Files (ichars*)", null, Arrays.asList(new String[] { "ichars" }), false));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new FileCharactersDirectiveInvocation();
    }

}

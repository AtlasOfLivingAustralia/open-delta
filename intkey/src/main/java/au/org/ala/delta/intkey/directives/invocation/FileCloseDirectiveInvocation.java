package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class FileCloseDirectiveInvocation extends IntkeyDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) {
        // Do nothing. This directive is no longer needed as content is now
        // written out to output files
        // Immediately. It is implemented here as a NO OP purely for backwards
        // compatbility.
        return false;
    }

}

package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayWindowingDirectiveInvocation extends IntkeyDirectiveInvocation {
    @Override
    public boolean execute(IntkeyContext context) {
        // Do nothing. This directive appears to be left over from an older
        // version of Intkey. It is implemented here as a NO OP purely for
        // backwards compatbility.
        return false;
    }
}

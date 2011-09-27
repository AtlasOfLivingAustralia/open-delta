package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayLogDirectiveInvocation extends IntkeyDirectiveInvocation {

    private boolean value;
    
    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.getUI().setLogVisible(value);
        return true;
    }
    
}

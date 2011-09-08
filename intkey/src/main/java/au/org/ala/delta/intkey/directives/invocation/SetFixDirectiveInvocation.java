package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetFixDirectiveInvocation implements IntkeyDirectiveInvocation {

    private boolean value;

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        System.out.println(value);
        return true;
    }

}

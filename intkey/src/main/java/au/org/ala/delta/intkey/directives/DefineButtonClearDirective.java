package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.DefineButtonClearDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineButtonClearDirective extends IntkeyDirective {

    public DefineButtonClearDirective() {
        super("define", "button", "clear");
    }
    
    @Override
    public int getArgType() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new DefineButtonClearDirectiveInvocation();
    }

}

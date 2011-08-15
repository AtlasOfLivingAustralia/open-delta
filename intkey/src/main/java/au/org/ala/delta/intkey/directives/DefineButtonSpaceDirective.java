package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.intkey.directives.invocation.DefineButtonSpaceDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineButtonSpaceDirective extends IntkeyDirective {

    public DefineButtonSpaceDirective() {
        super("define", "button", "space");
    }
    
    @Override
    public int getArgType() {
        // TODO Auto-generated method stub
        return DirectiveArgType.DIRARG_NONE;
    }
    
    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new DefineButtonSpaceDirectiveInvocation();
    }

}

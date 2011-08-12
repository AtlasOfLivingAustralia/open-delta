package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetImagePathDirective extends IntkeyDirective {

    @Override
    public int getArgType() {
        // TODO Auto-generated method stub
        return DirectiveArgType.DIRARG_TEXT;
    }
    
    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}

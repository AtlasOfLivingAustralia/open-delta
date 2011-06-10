package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class ChangeDirective extends IntkeyDirective {
    
    public ChangeDirective() {
        super("change");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new UseDirective().doProcess(context, data, true);
    }


    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES;
    }

}

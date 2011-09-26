package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.DiagType;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetDiagTypeSpecimensDirectiveInvocation extends IntkeyDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) {
        context.setDiagType(DiagType.SPECIMENS);
        return false;
    }

}

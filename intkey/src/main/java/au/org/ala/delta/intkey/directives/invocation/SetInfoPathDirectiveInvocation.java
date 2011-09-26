package au.org.ala.delta.intkey.directives.invocation;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetInfoPathDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<String> _infoPaths;
    
    public SetInfoPathDirectiveInvocation(List<String> infoPaths) {
        _infoPaths = new ArrayList<String>(infoPaths);
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setInfoPaths(_infoPaths);
        return true;
    }

}

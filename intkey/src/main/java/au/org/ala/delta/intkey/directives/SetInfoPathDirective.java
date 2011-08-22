package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetInfoPathDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetInfoPathDirective extends IntkeyDirective {
    
    public SetInfoPathDirective() {
        super("set", "infopath");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        List<String> infoPaths = new ArrayList<String>();
        for (String path: data.split(";")) {
            infoPaths.add(path);
        }
        return new SetInfoPathDirectiveInvocation(infoPaths);
    }

}

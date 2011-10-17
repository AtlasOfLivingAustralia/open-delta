package au.org.ala.delta.intkey.directives.invocation;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetInfoPathDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<String> _infoPaths;

    public void setInfoPaths(String infoPathsAsString) {
        _infoPaths = new ArrayList<String>();
        for (String path : infoPathsAsString.split(";")) {
            _infoPaths.add(path);
        }
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setInfoPaths(_infoPaths);
        return true;
    }

}

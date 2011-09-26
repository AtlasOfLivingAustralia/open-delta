package au.org.ala.delta.intkey.directives.invocation;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetImagePathDirectiveInvocation extends IntkeyDirectiveInvocation {
    
    private List<String> _imagePaths;
    
    public SetImagePathDirectiveInvocation(List<String> imagePaths) {
        _imagePaths = new ArrayList<String>(imagePaths);
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setImagePaths(_imagePaths);
        return true;
    }

}

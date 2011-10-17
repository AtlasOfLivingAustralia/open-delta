package au.org.ala.delta.intkey.directives.invocation;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetImagePathDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<String> _imagePaths;

    public void setImagePaths(String imagePathsAsString) {
        _imagePaths = new ArrayList<String>();
        for (String path : imagePathsAsString.split(";")) {
            _imagePaths.add(path);
        }
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setImagePaths(_imagePaths);
        return true;
    }

}

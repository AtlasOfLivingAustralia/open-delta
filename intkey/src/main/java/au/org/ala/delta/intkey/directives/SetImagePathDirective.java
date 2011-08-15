package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetImagePathDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetImagePathDirective extends IntkeyDirective {

    public SetImagePathDirective() {
        super("set", "imagepath");
    }
    
    @Override
    public int getArgType() {
        // TODO Auto-generated method stub
        return DirectiveArgType.DIRARG_TEXT;
    }
    
    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        List<String> imagePaths = new ArrayList<String>();
        for (String path: data.split(";")) {
            imagePaths.add(path);
        }
        return new SetImagePathDirectiveInvocation(imagePaths);
    }

}

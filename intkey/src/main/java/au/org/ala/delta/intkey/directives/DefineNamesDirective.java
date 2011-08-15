package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.intkey.directives.invocation.DefineNamesDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineNamesDirective extends IntkeyDirective {

    public DefineNamesDirective() {
        super("define", "names");
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_INTKEY_ITEMLIST;
    }
    
    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);
        
        String keyword = null;
        
        List<String> names = null;
        
        if (!tokens.isEmpty()) {
            keyword = ParsingUtils.removeEnclosingQuotes(tokens.get(0));
        }
        
        if (tokens.size() > 1) {
            names = new ArrayList<String>();
            for (int i=1; i < tokens.size(); i++) {
                String name = ParsingUtils.removeEnclosingQuotes(tokens.get(i));
                
                // Names are separated by commas, strip trailing comma
                // if it is present
                if (name.endsWith(",")) {
                    name = name.substring(0, name.length() - 1);
                }
                
                names.add(name);
            }
        }
        
        if (keyword == null) {
            //TODO prompt for keyword
        }
        
        if (names == null) {
            //TODO prompt for names
        }
        
        return new DefineNamesDirectiveInvocation(keyword, names);
    }
}

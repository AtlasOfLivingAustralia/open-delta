package au.org.ala.delta.intkey.directives;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class IncludeCharactersDirective extends IntkeyDirective {
    
    public IncludeCharactersDirective() {
        super("include", "characters");
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_INTKEY_CHARLIST;
    }
    
    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        List<au.org.ala.delta.model.Character> characters = null;
        
        if (StringUtils.isBlank(data)) {
            characters = context.getDirectivePopulator().promptForCharacters("INCLUDE CHARACTERS");
        } else {
            
        }
        
        
        
        return null;
    }



}

package au.org.ala.delta.intkey.directives;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.intkey.directives.invocation.IncludeTaxaDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class IncludeTaxaDirective extends IntkeyDirective {
    public IncludeTaxaDirective() {
        super("include", "taxa");
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_INTKEY_CHARLIST;
    }
    
    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        Set<Integer> includeTaxaNumbers = new HashSet<Integer>();
        
        if (StringUtils.isBlank(data)) {
            List<Item> selectedTaxa = context.getDirectivePopulator().promptForTaxa("INCLUDE TAXA");
            for (Item taxon: selectedTaxa) {
                includeTaxaNumbers.add(taxon.getItemNumber());
            }
        } else {
            throw new NotImplementedException();
        }
        
        if (includeTaxaNumbers.size() == 0) {
            return null;
        }
        
        return new IncludeTaxaDirectiveInvocation(includeTaxaNumbers);
    }
}

package au.org.ala.delta.intkey.directives;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.intkey.directives.invocation.ExcludeTaxaDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class ExcludeTaxaDirective extends IntkeyDirective {
    public ExcludeTaxaDirective() {
        super("exclude", "taxa");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        Set<Integer> excludeTaxaNumbers = new HashSet<Integer>();
        
        List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);
        
        if (tokens.isEmpty()) {
            List<Item> selectedTaxa = context.getDirectivePopulator().promptForTaxa("EXCLUDE TAXA", false);
            for (Item taxon: selectedTaxa) {
                excludeTaxaNumbers.add(taxon.getItemNumber());
            }
        } else {
            for (String token: tokens) {
                List<Item> tokenTaxa = ParsingUtils.parseTaxonToken(token, context);
                for (Item taxon: tokenTaxa) {
                    excludeTaxaNumbers.add(taxon.getItemNumber());
                }
            }
        }
        
        if (excludeTaxaNumbers.size() == 0) {
            return null;
        }
        
        return new ExcludeTaxaDirectiveInvocation(excludeTaxaNumbers);
    }
}

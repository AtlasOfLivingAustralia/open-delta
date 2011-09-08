package au.org.ala.delta.intkey.directives;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.intkey.directives.invocation.IncludeTaxaDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class IncludeTaxaDirective extends IntkeyDirective {
    public IncludeTaxaDirective() {
        super("include", "taxa");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        Set<Integer> includeTaxaNumbers = new HashSet<Integer>();
        
        List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);
        
        if (tokens.isEmpty()) {
            List<Item> selectedTaxa = context.getDirectivePopulator().promptForTaxaByKeyword("INCLUDE TAXA", false);
            for (Item taxon: selectedTaxa) {
                includeTaxaNumbers.add(taxon.getItemNumber());
            }
        } else {
            for (String token: tokens) {
                List<Item> tokenTaxa = ParsingUtils.parseTaxonToken(token, context);
                for (Item taxon: tokenTaxa) {
                    includeTaxaNumbers.add(taxon.getItemNumber());
                }
            }
        }
        
        if (includeTaxaNumbers.size() == 0) {
            return null;
        }
        
        return new IncludeTaxaDirectiveInvocation(includeTaxaNumbers);
    }
}

package au.org.ala.delta.intkey.directives.invocation;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.IntkeyUI;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineTaxaDirectiveInvocation implements IntkeyDirectiveInvocation {

    public static final String LABEL = "DEFINE TAXA";
    
    private String _keyword;
    private Set<Integer> _taxaNumbers;

    public DefineTaxaDirectiveInvocation(String keyword, Set<Integer> characterNumbers) {
        _keyword = keyword;
        _taxaNumbers = characterNumbers;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.addCharacterKeyword(_keyword, _taxaNumbers);
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", LABEL, _keyword, StringUtils.join(_taxaNumbers, " "));
    }
    

}

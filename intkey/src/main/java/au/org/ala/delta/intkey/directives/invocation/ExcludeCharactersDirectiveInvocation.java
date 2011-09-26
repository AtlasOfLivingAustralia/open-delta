package au.org.ala.delta.intkey.directives.invocation;

import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class ExcludeCharactersDirectiveInvocation extends IntkeyDirectiveInvocation {

    private Set<Integer> _excludedCharNumbers;
    
    public ExcludeCharactersDirectiveInvocation(Set<Integer> excludedCharNumbers) {
        _excludedCharNumbers = excludedCharNumbers;
    }
    
    @Override
    public boolean execute(IntkeyContext context) {
        context.setExcludedCharacters(_excludedCharNumbers);
        return true;
    }

}

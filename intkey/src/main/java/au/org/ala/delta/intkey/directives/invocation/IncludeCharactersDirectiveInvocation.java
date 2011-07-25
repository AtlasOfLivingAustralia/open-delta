package au.org.ala.delta.intkey.directives.invocation;

import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class IncludeCharactersDirectiveInvocation implements IntkeyDirectiveInvocation {

    private Set<Integer> _includedCharNumbers;
    
    public IncludeCharactersDirectiveInvocation(Set<Integer> includedCharNumbers) {
        _includedCharNumbers = includedCharNumbers;
    }
    
    @Override
    public boolean execute(IntkeyContext context) {
        context.setIncludedCharacters(_includedCharNumbers);
        return true;
    }

}

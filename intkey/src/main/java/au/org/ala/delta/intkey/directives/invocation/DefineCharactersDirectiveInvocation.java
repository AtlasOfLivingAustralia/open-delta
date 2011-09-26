package au.org.ala.delta.intkey.directives.invocation;

import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineCharactersDirectiveInvocation extends IntkeyDirectiveInvocation { 
    String _keyword;
    Set<Integer> _characterNumbers;

    public DefineCharactersDirectiveInvocation(String keyword, Set<Integer> characterNumbers) {
        _keyword = keyword;
        _characterNumbers = characterNumbers;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.addCharacterKeyword(_keyword, _characterNumbers);
        return true;
    }

}

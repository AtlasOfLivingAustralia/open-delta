package au.org.ala.delta.intkey.directives.invocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;

public class ExcludeCharactersDirectiveInvocation extends IntkeyDirectiveInvocation {

    private Set<Integer> _excludedCharNumbers;

    public void setCharacters(List<Character> characters) {
        _excludedCharNumbers = new HashSet<Integer>();
        for (Character ch: characters) {
            _excludedCharNumbers.add(ch.getCharacterId());
        }
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setExcludedCharacters(_excludedCharNumbers);
        return true;
    }

}

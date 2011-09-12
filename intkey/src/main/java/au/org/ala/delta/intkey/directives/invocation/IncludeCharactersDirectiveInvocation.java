package au.org.ala.delta.intkey.directives.invocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;

public class IncludeCharactersDirectiveInvocation implements IntkeyDirectiveInvocation {

    private List<Character> _characters;

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }
    
    @Override
    public boolean execute(IntkeyContext context) {
        Set<Integer> includedCharNumbers = new HashSet<Integer>();
        for (Character ch: _characters) {
            includedCharNumbers.add(ch.getCharacterId());
        }
        
        context.setIncludedCharacters(includedCharNumbers);
        return true;
    }

}

package au.org.ala.delta.intkey.directives.invocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;

public class SetExactDirectiveInvocation implements IntkeyDirectiveInvocation {

    private List<au.org.ala.delta.model.Character> characters;
    
    public void setCharacters(List<au.org.ala.delta.model.Character> characters) {
        this.characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        Set<Integer> characterNumbers = new HashSet<Integer>();
        for (Character ch: characters) {
            characterNumbers.add(ch.getCharacterId());
        }
        
        context.setExactCharacters(characterNumbers);
        return true;
    }

}

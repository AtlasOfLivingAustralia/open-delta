package au.org.ala.delta.intkey.directives.invocation;

import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;

public class CharactersDirectiveInvocation implements IntkeyDirectiveInvocation {
    
    private List<Character> _characters;

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        System.out.println(_characters.toString());
        
        //TODO generate and show report
        return true;
    }

}

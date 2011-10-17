package au.org.ala.delta.intkey.directives.invocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineCharactersDirectiveInvocation extends IntkeyDirectiveInvocation {
    String _keyword;
    List<au.org.ala.delta.model.Character> _characters;

    public void setKeyword(String keyword) {
        this._keyword = keyword;
    }

    public void setCharacters(List<au.org.ala.delta.model.Character> characters) {
        this._characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        Set<Integer> characterNumbers = new HashSet<Integer>();
        for (au.org.ala.delta.model.Character ch : _characters) {
            characterNumbers.add(ch.getCharacterId());
        }

        context.addCharacterKeyword(_keyword, characterNumbers);
        return true;
    }

}

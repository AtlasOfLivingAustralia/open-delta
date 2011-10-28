package au.org.ala.delta.intkey.directives.invocation;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.util.Utils;

public class OutputCharactersDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<au.org.ala.delta.model.Character> _characters;

    public void setCharacters(List<au.org.ala.delta.model.Character> characters) {
        this._characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        List<Integer> characterNumbers = new ArrayList<Integer>();
        for (au.org.ala.delta.model.Character ch : _characters) {
            characterNumbers.add(ch.getCharacterId());
        }

        try {
            if (context.getLastOutputLineWasComment()) {
                context.setLastOutputLineWasComment(false);
                context.appendToOutputFile(Utils.formatIntegersAsListOfRanges(characterNumbers));
            } else {
                context.appendToOutputFile(String.format("OUTPUT CHARACTERS %s", Utils.formatIntegersAsListOfRanges(characterNumbers)));
            }
        } catch (IllegalStateException ex) {
            throw new IntkeyDirectiveInvocationException("NoOutputFileOpen.error");
        }

        return true;
    }
}

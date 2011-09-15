package au.org.ala.delta.intkey.directives.invocation;

import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.rtf.RTFBuilder;

public class CharactersDirectiveInvocation implements IntkeyDirectiveInvocation {

    private List<Character> _characters;

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        CharacterFormatter characterFormatter = new CharacterFormatter(true, false, false, true, true);

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        for (Character ch : _characters) {
            builder.appendText(characterFormatter.formatCharacterDescription(ch));
            builder.increaseIndent();

            if (ch instanceof MultiStateCharacter) {
                MultiStateCharacter msChar = (MultiStateCharacter) ch;
                for (int i = 0; i < msChar.getNumberOfStates(); i++) {
                    builder.appendText(characterFormatter.formatState(msChar, i + 1));
                }
            } else if (ch instanceof NumericCharacter<?>) {
                NumericCharacter<?> numChar = (NumericCharacter<?>) ch;
                builder.appendText(characterFormatter.formatUnits(numChar));
            }

            builder.decreaseIndent();
            builder.appendText("");
        }
        
        builder.endDocument();

        context.getUI().displayRTFReport(builder.toString(), "Characters");

        return true;
    }

}

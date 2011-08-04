package au.org.ala.delta.ui.util;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.ui.RichTextDialog;

public class UIUtils {

    public static RichTextDialog createCharacterDetailsDialog(Character character) {
        CharacterFormatter formatter = new CharacterFormatter();
        StringBuilder text = new StringBuilder();
        text.append(formatter.formatCharacterDescription(character));
        
        if (character instanceof MultiStateCharacter) {
            MultiStateCharacter multiStateChar = (MultiStateCharacter)character;
            for (int i=1; i<=multiStateChar.getNumberOfStates(); i++) {
                text.append("\\par ");
                text.append(formatter.formatState(multiStateChar, i));
            }
        }
        else if (character instanceof NumericCharacter<?>) {
            NumericCharacter<?> numericChar = (NumericCharacter<?>)character;
            text.append("\\par ");
            text.append(numericChar.getUnits());
        }
        RichTextDialog dialog = new RichTextDialog(text.toString());
        return dialog;
    }
}

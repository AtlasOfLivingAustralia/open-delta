package au.org.ala.delta.intkey.directives.invocation;

import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;

public class DeleteDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<Character> _characters;
    private boolean suppressUnusedCharacterWarning;

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    public void setSuppressUnusedCharacterWarning(boolean suppressUnusedCharacterWarning) {
        this.suppressUnusedCharacterWarning = suppressUnusedCharacterWarning;
    }

    @Override
    public boolean execute(IntkeyContext context) {

        if (context.getUsedCharacters().isEmpty() && !suppressUnusedCharacterWarning) {
            // TODO this warning really should be displayed BEFORE the user is
            // prompted to select characters
            // to delete.
            context.getUI().displayErrorMessage("No character values have been used to describe the specimen");
            return false;
        }

        for (Character ch : _characters) {
            context.removeValueForCharacter(ch);
        }

        context.specimenUpdateComplete();

        return true;
    }

}

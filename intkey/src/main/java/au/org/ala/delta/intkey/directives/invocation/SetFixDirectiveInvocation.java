package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetFixDirectiveInvocation extends OnOffDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) {

        if (_value == true && context.charactersFixed()) {
            context.getUI().displayErrorMessage("Characters already fixed.");
            return false;
        }

        if (_value == true && context.getUsedCharacters().isEmpty()) {
            context.getUI().displayErrorMessage("No characters in specimen description to fix.");
            return false;
        }

        context.setCharactersFixed(_value);
        return true;
    }

}

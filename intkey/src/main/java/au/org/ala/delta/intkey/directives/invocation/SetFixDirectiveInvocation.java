package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetFixDirectiveInvocation implements IntkeyDirectiveInvocation {

    private boolean value;

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        
        if (value == true && context.charactersFixed()) {
            context.getUI().displayErrorMessage("Characters already fixed.");
            return false;
        }        
        
        if (value == true && context.getUsedCharacters().isEmpty()) {
            context.getUI().displayErrorMessage("No characters in specimen description to fix.");
            return false;
        }
        
        context.setCharactersFixed(value);
        return true;
    }

}

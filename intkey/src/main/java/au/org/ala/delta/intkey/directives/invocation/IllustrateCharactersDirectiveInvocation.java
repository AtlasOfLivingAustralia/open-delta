package au.org.ala.delta.intkey.directives.invocation;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;

public class IllustrateCharactersDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<au.org.ala.delta.model.Character> _characters;
    
    public void setCharacters(List<au.org.ala.delta.model.Character> characters) {
        this._characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        //filter out any taxa that do not have images
        List<Character> charsWithoutImages = new ArrayList<Character>();
        for (Character ch : _characters) {
            if (ch.getImageCount() == 0) {
                charsWithoutImages.add(ch);
            }
        }
        
        _characters.removeAll(charsWithoutImages);
        
        if (_characters.isEmpty()) {
            context.getUI().displayErrorMessage("No images for the specified characters");
            return false;
        }
        
        context.getUI().IllustrateCharacters(_characters);
        return true;
    }

}

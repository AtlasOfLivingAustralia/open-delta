package au.org.ala.delta.intkey.directives.invocation;

import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.util.Pair;

public class SummaryDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<Item> _taxa;

    private List<Character> _characters;

    public void setSelectedTaxaSpecimen(Pair<List<Item>, Boolean> pair) {
        this._taxa = pair.getFirst();
        // The specimen has no relevance here. Simply ignore it if it is
        // specified.
    }

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        RTFBuilder builder = new RTFBuilder();
        
        for (Character ch: _characters) {
            for (Item taxon: _taxa) {
                if (ch instanceof MultiStateCharacter) {
                    
                } else if (ch instanceof IntegerCharacter) {
                    
                } else if (ch instanceof RealCharacter) {
                    
                } else if (ch instanceof TextCharacter) {
                    
                }
            }
        }
        
        return true;
    }

}

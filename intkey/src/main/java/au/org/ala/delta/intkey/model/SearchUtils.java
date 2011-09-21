package au.org.ala.delta.intkey.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.TextAttribute;

/**
 * Utility methods for searching for text in characters and taxa
 * @author ChrisF
 *
 */
public class SearchUtils {
    
    public static boolean characterMatches(Character ch, String searchText, boolean searchStates) {
        boolean result = false;

        String searchTextLowerCase = searchText.toLowerCase();

        if (ch.getDescription().toLowerCase().contains(searchTextLowerCase)) {
            result = true;
        }

        if (!result && searchStates) {
            if (ch instanceof MultiStateCharacter) {
                MultiStateCharacter msChar = (MultiStateCharacter) ch;
                for (String state : msChar.getStates()) {
                    if (state.toLowerCase().contains(searchTextLowerCase)) {
                        result = true;
                        break;
                    }
                }
            } else if (ch instanceof NumericCharacter<?>) {
                NumericCharacter<?> numChar = (NumericCharacter<?>) ch;
                if (numChar.getUnits() != null && numChar.getUnits().toLowerCase().contains(searchTextLowerCase)) {
                    result = true;
                }
            }
        }

        return result;
    }
    
    public static boolean taxonMatches(String searchText, Item taxon, List<String> synonymStrings) {
        String searchTextLowerCase = searchText.toLowerCase();

        if (taxon.getDescription().toLowerCase().contains(searchTextLowerCase)) {
            return true;
        }

        if (synonymStrings != null) {
            for (String synonymString : synonymStrings) {
                if (synonymString.toLowerCase().contains(searchTextLowerCase)) {
                    return true;
                }
            }
        }

        return false;
    }
    
    public static List<String> getSynonymyStringsForTaxon(Item taxon, Map<Item, List<TextAttribute>> taxaSynonymyAttributes) {
        List<TextAttribute> taxonSynonymyAttributes = taxaSynonymyAttributes.get(taxon);
        List<String> synonymyStrings = new ArrayList<String>();

        for (TextAttribute attr : taxonSynonymyAttributes) {
            synonymyStrings.add(attr.getText());
        }
        
        return synonymyStrings;
    }
}

/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
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

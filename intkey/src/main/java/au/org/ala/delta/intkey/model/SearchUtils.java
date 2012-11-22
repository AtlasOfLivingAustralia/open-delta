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
 * 
 * @author ChrisF
 * 
 */
public class SearchUtils {

    /**
     * Returns true if the supplied text is contained in the character
     * description and optionally its states.
     * 
     * @param ch
     *            The character
     * @param searchText
     *            The search text
     * @param searchStates
     *            If true, the characters states (in the case of a multistate)
     *            or the character's units (in the case of a numeric character)
     *            will also be searched for the supplied text
     * @return True if the supplied text is contained within the character's
     *         description, or optionally the character's states or units.
     */
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

    /**
     * Returns true if the supplied text matches the taxon description, or any
     * of the taxon's synonym strings if provided.
     * 
     * @param searchText
     *            The search text
     * @param taxon
     *            The taxon
     * @param synonymStrings
     *            The synonym strings for the taxon that should also be searched
     *            for the search text. Supply null if not applicable.
     * @return true if the supplied text matches the taxon description, or any
     *         of the taxon's synonym strings if provided.
     */
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

    /**
     * Returns the synonymy strings for a taxon
     * 
     * @param taxon
     *            The taxon
     * @param taxaSynonymyAttributes
     *            The map of taxon to synonymy attributes for all taxa in the
     *            dataset.
     * @return The synonymy strings for the taxon, or an empty list if there
     *         aren't any.
     */
    public static List<String> getSynonymyStringsForTaxon(Item taxon, Map<Item, List<TextAttribute>> taxaSynonymyAttributes) {
        List<TextAttribute> taxonSynonymyAttributes = taxaSynonymyAttributes.get(taxon);
        List<String> synonymyStrings = new ArrayList<String>();

        for (TextAttribute attr : taxonSynonymyAttributes) {
            synonymyStrings.add(attr.getText());
        }

        return synonymyStrings;
    }
}

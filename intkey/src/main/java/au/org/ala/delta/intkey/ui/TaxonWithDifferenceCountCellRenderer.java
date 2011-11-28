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
package au.org.ala.delta.intkey.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import au.org.ala.delta.model.Item;

public class TaxonWithDifferenceCountCellRenderer extends TaxonCellRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = -1387340931499472805L;

    private Map<Item, Set<au.org.ala.delta.model.Character>> _taxaDifferingCharacters;

    public TaxonWithDifferenceCountCellRenderer(Map<Item, Set<au.org.ala.delta.model.Character>> taxaDifferingCharacters, boolean displayNumbering, boolean displayComments) {
        super(displayNumbering, displayComments);
        _taxaDifferingCharacters = new HashMap<Item, Set<au.org.ala.delta.model.Character>>();
        if (taxaDifferingCharacters != null) {
            _taxaDifferingCharacters.putAll(taxaDifferingCharacters);
        }
    }

    @Override
    protected String getTextForValue(Object value) {
        if (value instanceof Item) {
            Item taxon = (Item) value;
            int differenceCount = 0;

            if (_taxaDifferingCharacters != null) {
                if (_taxaDifferingCharacters.containsKey(taxon)) {
                    differenceCount = _taxaDifferingCharacters.get(taxon).size();
                }
            }

            return String.format("(%s) %s", differenceCount, _formatter.formatItemDescription(taxon));
        } else {
            return value.toString();
        }
    }

}

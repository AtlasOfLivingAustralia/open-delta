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
package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

public class TabularKey {
    
    private List<TabularKeyRow> _rows;
    
    private Set<au.org.ala.delta.model.Character> _usedCharacters;
    private Set<Item> _usedItems;
    
    /**
     * True if the key is incomplete - if there are insufficient characters to identify one or more taxa.
     */
    private boolean _keyIncomplete = false;
    
    public TabularKey() {
        _rows = new ArrayList<TabularKeyRow>();
        _usedCharacters = new HashSet<au.org.ala.delta.model.Character>();
        _usedItems = new HashSet<Item>();
    }
    
    public void addRow(TabularKeyRow row) {
        _rows.add(row);
        for (Attribute attr: row.getAllAttributes()) {
            _usedCharacters.add(attr.getCharacter());
        }
        
        _usedItems.add(row.getItem());
    }
    
    public boolean isCharacterUsedInKey(au.org.ala.delta.model.Character ch) {
        return _usedCharacters.contains(ch);
    }
    
    public Set<Character> getCharactersUsedInKey() {
        return new HashSet<Character>(_usedCharacters);
    }
    
    public Set<Item> getItemsUsedInKey() {
        return new HashSet<Item>(_usedItems);
    }
    
    public int getNumberOfRows() {
        return _rows.size();
    }
    
    public TabularKeyRow getRowAt(int idx) {
        return _rows.get(idx);
    }
    
    public List<TabularKeyRow> getRows() {
        return _rows;
    }
    
    public double getAverageLength() {
        double sumLengths = 0;
        
        for (TabularKeyRow row: _rows) {
            sumLengths += row.getNumberOfColumns();
        }
        
        return sumLengths / _rows.size();
    }
    
    public double getMaximumLength() {
        double maxLength = 0;
        
        for (TabularKeyRow row: _rows) {
            if (row.getNumberOfColumns() > maxLength) {
                maxLength = row.getNumberOfColumns();
            }
        }
        
        return maxLength;
    }
    
    public double getAverageCost() {
        double sumCosts = 0;
        
        for (TabularKeyRow row: _rows) {
            sumCosts += row.getCost();
        }
        
        return sumCosts / _rows.size();
    }
    
    public double getMaximumCost() {
        double maxCost = 0;
        
        for (TabularKeyRow row: _rows) {
            if (row.getNumberOfColumns() > maxCost) {
                maxCost = row.getCost();
            }
        }
        
        return maxCost;
    }
    
    public boolean isKeyIncomplete() {
        return _keyIncomplete;
    }

    public void setKeyIncomplete(boolean keyIncomplete) {
        this._keyIncomplete = keyIncomplete;
    }
}

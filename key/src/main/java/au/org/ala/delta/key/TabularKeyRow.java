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
import java.util.List;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;

public class TabularKeyRow {

    private Item _item;

    private List<MultiStateAttribute> _mainAttributes;
    private List<List<MultiStateAttribute>> _confirmatoryCharacterAttributeLists;
    
    private double _cost;

    public TabularKeyRow() {
        _mainAttributes = new ArrayList<MultiStateAttribute>();
        _confirmatoryCharacterAttributeLists = new ArrayList<List<MultiStateAttribute>>();
        _cost = 0;
    }

    public Item getItem() {
        return _item;
    }

    public void setItem(Item item) {
        this._item = item;
    }

    public void addAttribute(MultiStateAttribute mainAttribute, List<MultiStateAttribute> confirmatoryCharacterAttributes, double cost) {
        if (mainAttribute == null) {
            throw new IllegalArgumentException("Main character value cannot be null");
        }

        _mainAttributes.add(mainAttribute);

        // record absence of confirmatory character values as null
        if (confirmatoryCharacterAttributes != null && confirmatoryCharacterAttributes.isEmpty()) {
            confirmatoryCharacterAttributes = null;
        }
        _confirmatoryCharacterAttributeLists.add(confirmatoryCharacterAttributes);
        
        _cost += cost;
    }

    public int getNumberOfColumns() {
        return _mainAttributes.size();
    }

    public MultiStateAttribute getMainAttributeForColumn(int columnNumber) {
        if (columnNumber <= 0 || columnNumber > getNumberOfColumns()) {
            throw new IllegalArgumentException("Invalid column index");
        }
        
        return _mainAttributes.get(columnNumber - 1);
    }

    public List<MultiStateAttribute> getConfirmatoryCharacterAttributesForColumn(int columnNumber) {
        if (columnNumber <= 0 || columnNumber > getNumberOfColumns()) {
            throw new IllegalArgumentException("Invalid column index");
        }
        
        return _confirmatoryCharacterAttributeLists.get(columnNumber - 1);
    }

    public List<MultiStateAttribute> getMainAttributes() {
        return _mainAttributes;
    }

    public List<MultiStateAttribute> getAllAttributesForColumn(int columnNumber) {
        if (columnNumber <= 0 || columnNumber > getNumberOfColumns()) {
            throw new IllegalArgumentException("Invalid column index");
        }
        
        List<MultiStateAttribute> retList = new ArrayList<MultiStateAttribute>();
        retList.add(getMainAttributeForColumn(columnNumber));
        if (getConfirmatoryCharacterAttributesForColumn(columnNumber) != null) {
            retList.addAll(getConfirmatoryCharacterAttributesForColumn(columnNumber));
        }
        return retList;
    }

    /**
     * Get all character values in the row for the columns up to and including the 
     * specified column number. If the column number supplied is greater than the number of
     * columns in the row, all available column values will be returned.
     * @param columnNumber
     * @return
     */
    public List<MultiStateAttribute> getAllCharacterValuesUpToColumn(int columnNumber) {
        if (columnNumber <= 0) {
            throw new IllegalArgumentException("Invalid column index");
        }
        
        List<MultiStateAttribute> retList = new ArrayList<MultiStateAttribute>();
        for (int i=1; i <= columnNumber && i <= getNumberOfColumns(); i++) {
            retList.addAll(getAllAttributesForColumn(i));
        }
        
        return retList;
    }

    public List<MultiStateAttribute> getAllAttributes() {
        List<MultiStateAttribute> allValuesList = new ArrayList<MultiStateAttribute>();

        for (int i = 0; i < _mainAttributes.size(); i++) {
            allValuesList.add(_mainAttributes.get(i));

            List<MultiStateAttribute> confirmatoryCharacterValues = _confirmatoryCharacterAttributeLists.get(i);
            if (confirmatoryCharacterValues != null) {
                allValuesList.addAll(confirmatoryCharacterValues);
            }
        }

        return allValuesList;
    }
    
    /**
     * @return The summed cost of using each of the main characters in the row, in order
     */
    public double getCost() {
        return _cost;
    }

}

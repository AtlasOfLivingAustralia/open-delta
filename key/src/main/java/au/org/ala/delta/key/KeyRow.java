package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;

public class KeyRow {

    private Item _item;

    private List<MultiStateAttribute> _mainCharacterValues;
    private List<List<MultiStateAttribute>> _confirmatoryCharacterValuesLists;

    public KeyRow() {
        _mainCharacterValues = new ArrayList<MultiStateAttribute>();
        _confirmatoryCharacterValuesLists = new ArrayList<List<MultiStateAttribute>>();
    }

    public Item getItem() {
        return _item;
    }

    public void setItem(Item item) {
        this._item = item;
    }

    public void addColumnValue(MultiStateAttribute mainCharacterValue, List<MultiStateAttribute> confirmatoryCharacterValues) {
        if (mainCharacterValue == null) {
            throw new IllegalArgumentException("Main character value cannot be null");
        }

        _mainCharacterValues.add(mainCharacterValue);

        // record absence of confirmatory character values as null
        if (confirmatoryCharacterValues != null && confirmatoryCharacterValues.isEmpty()) {
            confirmatoryCharacterValues = null;
        }
        _confirmatoryCharacterValuesLists.add(confirmatoryCharacterValues);
    }

    public int getNumberOfColumnValues() {
        return _mainCharacterValues.size();
    }

    public MultiStateAttribute getMainCharacterValueForColumn(int columnNumber) {
        if (columnNumber <= 0 || columnNumber > getNumberOfColumnValues()) {
            throw new IllegalArgumentException("Invalid column index");
        }
        
        return _mainCharacterValues.get(columnNumber - 1);
    }

    public List<MultiStateAttribute> getConfirmatoryCharacterValuesForColumn(int columnNumber) {
        if (columnNumber <= 0 || columnNumber > getNumberOfColumnValues()) {
            throw new IllegalArgumentException("Invalid column index");
        }
        
        return _confirmatoryCharacterValuesLists.get(columnNumber - 1);
    }

    public List<MultiStateAttribute> getMainCharacterValues() {
        return _mainCharacterValues;
    }

    public List<MultiStateAttribute> getAllCharacterValuesForColumn(int columnNumber) {
        if (columnNumber <= 0 || columnNumber > getNumberOfColumnValues()) {
            throw new IllegalArgumentException("Invalid column index");
        }
        
        List<MultiStateAttribute> retList = new ArrayList<MultiStateAttribute>();
        retList.add(getMainCharacterValueForColumn(columnNumber));
        if (getConfirmatoryCharacterValuesForColumn(columnNumber) != null) {
            retList.addAll(getConfirmatoryCharacterValuesForColumn(columnNumber));
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
        for (int i=1; i <= columnNumber && i <= getNumberOfColumnValues(); i++) {
            retList.addAll(getAllCharacterValuesForColumn(i));
        }
        
        return retList;
    }

    public List<MultiStateAttribute> getAllCharacterValues() {
        List<MultiStateAttribute> allValuesList = new ArrayList<MultiStateAttribute>();

        for (int i = 0; i < _mainCharacterValues.size(); i++) {
            allValuesList.add(_mainCharacterValues.get(i));

            List<MultiStateAttribute> confirmatoryCharacterValues = _confirmatoryCharacterValuesLists.get(i);
            if (confirmatoryCharacterValues != null) {
                allValuesList.addAll(confirmatoryCharacterValues);
            }
        }

        return allValuesList;
    }

}

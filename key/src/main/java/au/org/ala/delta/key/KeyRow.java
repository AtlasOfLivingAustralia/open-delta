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
    
    public MultiStateAttribute getMainCharacterValueAt(int idx) {
        return _mainCharacterValues.get(idx);
    }
    
    public List<MultiStateAttribute> getConfirmatoryCharacterValuesAt(int idx) {
        return _confirmatoryCharacterValuesLists.get(idx);
    }
    
    public List<MultiStateAttribute> getMainCharacterValues() {
        return _mainCharacterValues;
    }
    
    public List<List<MultiStateAttribute>> getRowAsList() {
        List<List<MultiStateAttribute>> rowList = new ArrayList<List<MultiStateAttribute>>();
        
        for (int i=0; i < _mainCharacterValues.size(); i++) {
            List<MultiStateAttribute> columnValueList = new ArrayList<MultiStateAttribute>();
            columnValueList.add(_mainCharacterValues.get(i));
            
            List<MultiStateAttribute> confirmatoryCharacterValues = _confirmatoryCharacterValuesLists.get(i);
            if (confirmatoryCharacterValues != null) {
                columnValueList.addAll(confirmatoryCharacterValues);
            }
            
            rowList.add(columnValueList);
        }
        
        return rowList;
    }
    
}

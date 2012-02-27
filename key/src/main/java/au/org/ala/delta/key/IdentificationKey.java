package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

public class IdentificationKey {
    
    private List<KeyRow> _rows;
    
    private Set<au.org.ala.delta.model.Character> _usedCharacters;
    private Set<Item> _usedItems;
    
    public IdentificationKey() {
        _rows = new ArrayList<KeyRow>();
        _usedCharacters = new HashSet<au.org.ala.delta.model.Character>();
        _usedItems = new HashSet<Item>();
    }
    
    public void addRow(KeyRow row) {
        _rows.add(row);
        for (Attribute attr: row.getAllCharacterValues()) {
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
    
    public KeyRow getRowAt(int idx) {
        return _rows.get(idx);
    }
    
    public List<KeyRow> getRows() {
        return _rows;
    }
}

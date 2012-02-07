package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.MultiStateCharacter;

public class IdentificationKey {
    
    private List<KeyRow> _rows;
    
    private Map<MultiStateCharacter, Integer> characterOccurrences;
    
    private Set<au.org.ala.delta.model.Character> _usedCharacters;
    
    public IdentificationKey() {
        _rows = new ArrayList<KeyRow>();
        _usedCharacters = new HashSet<au.org.ala.delta.model.Character>();
    }
    
    public void addRow(KeyRow row) {
        _rows.add(row);
        for (Attribute attr: row.getAllCharacterValues()) {
            _usedCharacters.add(attr.getCharacter());
        }
    }
    
    public boolean isCharacterUsedInKey(au.org.ala.delta.model.Character ch) {
        return _usedCharacters.contains(ch);
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

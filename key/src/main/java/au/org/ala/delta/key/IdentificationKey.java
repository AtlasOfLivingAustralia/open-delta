package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.model.MultiStateCharacter;

public class IdentificationKey {
    
    private List<KeyRow> _rows;
    
    private Map<MultiStateCharacter, Integer> characterOccurrences;
    
    public IdentificationKey() {
        _rows = new ArrayList<KeyRow>();
    }
    
    public void addRow(KeyRow row) {
        _rows.add(row);
        
        // Add occurrences to map - this is used to modify character costs
        // using the value set by the REUSE directive
        //for (MultiStateAttribute attr: )
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

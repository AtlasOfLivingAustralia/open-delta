package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.List;

public class IdentificationKey {
    
    private List<KeyRow> _rows;
    
    public IdentificationKey() {
        _rows = new ArrayList<KeyRow>();
    }
    
    public void addRow(KeyRow row) {
        _rows.add(row);
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

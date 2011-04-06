package au.org.ala.delta.intkey.model;

import java.util.ArrayList;
import java.util.List;

public class Character {

    private String _description;
    private IntkeyCharacterType _type;
    private List<String> _states;
    
    public String getDescription() {
        return _description;
    }
    
    public IntkeyCharacterType getType() {
        return _type;
    }
    
    public List<String> getStates() {
        //return defensive copy
        return new ArrayList<String>(_states);
    }
    
    void setDescription(String description) {
        this._description = description;
    }
    
    void setType(IntkeyCharacterType type) {
        this._type = type;
    }
    
    void setStates(List<String> states) {
        this._states = states;
    }

    @Override
    public String toString() {
        /*StringBuilder builder = new StringBuilder();
        builder.append(_description);
        builder.append("\n");
        
        for (String state: _states) {
            builder.append("\t-> ");
            builder.append(state);
            builder.append("\n");
        }
        
        return builder.toString();*/
        return _description;
    }
    
    
}

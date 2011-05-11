package au.org.ala.delta.intkey.model.specimen;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.MultiStateCharacter;

public class MultiStateValue extends CharacterValue {
    
    private MultiStateCharacter _character;
    private List<Integer> _stateValues;
    
    public MultiStateValue(MultiStateCharacter character, List<Integer> stateValues) {
        _character = character;
        _stateValues = new ArrayList<Integer>(stateValues);
    }

    @Override
    public MultiStateCharacter getCharacter() {
        return _character;
    }
    
    public List<Integer> getStateValues() {
        return new ArrayList<Integer>(_stateValues);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(_formatter.formatCharacterDescription(_character));
        builder.append(" ");
        
        for (int i=0; i < _stateValues.size(); i++) {
            if (i > 0) {
                builder.append("; or ");
            }
            
            builder.append(_formatter.formatState(_character, _stateValues.get(i)));
        }
        
        return builder.toString().trim();
    }

}

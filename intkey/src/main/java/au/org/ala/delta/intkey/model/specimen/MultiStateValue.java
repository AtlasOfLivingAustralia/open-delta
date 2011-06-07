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

    @Override
    public String toShortString() {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i < _stateValues.size(); i++) {
            if (i > 0) {
                builder.append("/");
            }
            
            builder.append(_stateValues.get(i));
        }
        
        return builder.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_character == null) ? 0 : _character.hashCode());
        result = prime * result + ((_stateValues == null) ? 0 : _stateValues.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MultiStateValue other = (MultiStateValue) obj;
        if (_character == null) {
            if (other._character != null)
                return false;
        } else if (!_character.equals(other._character))
            return false;
        if (_stateValues == null) {
            if (other._stateValues != null)
                return false;
        } else if (!_stateValues.equals(other._stateValues))
            return false;
        return true;
    }

}

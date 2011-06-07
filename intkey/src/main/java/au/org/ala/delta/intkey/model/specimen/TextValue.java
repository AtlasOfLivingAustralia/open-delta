package au.org.ala.delta.intkey.model.specimen;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.TextCharacter;

public class TextValue extends CharacterValue {
    
    private TextCharacter _character;
    private List<String> _values;

    public TextValue(TextCharacter character, List<String> values) {
        _character = character;
        _values = new ArrayList<String>(values);
    }
    
    @Override
    public TextCharacter getCharacter() {
        return _character;
    }
    
    public List<String> getValues() {
        return new ArrayList<String>(_values);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(_formatter.formatCharacterDescription(_character));
        builder.append(" ");
        
        for (int i=0; i < _values.size(); i++) {
            if (i > 0) {
                builder.append(" or ");
            }
            
            builder.append("\"");
            builder.append(_values.get(i));
            builder.append("\"");
        }
        
        return builder.toString();
    }

    @Override
    public String toShortString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\"");
        for (int i=0; i < _values.size(); i++) {
            if (i > 0) {
                builder.append("/");
            }
            
            builder.append(_values.get(i));
        }
        builder.append("\"");
        
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_character == null) ? 0 : _character.hashCode());
        result = prime * result + ((_values == null) ? 0 : _values.hashCode());
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
        TextValue other = (TextValue) obj;
        if (_character == null) {
            if (other._character != null)
                return false;
        } else if (!_character.equals(other._character))
            return false;
        if (_values == null) {
            if (other._values != null)
                return false;
        } else if (!_values.equals(other._values))
            return false;
        return true;
    }
    
}

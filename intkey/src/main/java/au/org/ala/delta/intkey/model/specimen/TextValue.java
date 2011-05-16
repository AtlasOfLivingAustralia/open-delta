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
    
}

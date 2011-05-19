package au.org.ala.delta.intkey.model.specimen;

import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.RealCharacter;

public class RealValue extends CharacterValue {
    
    private RealCharacter _character;
    private FloatRange _range;

    public RealValue(RealCharacter character, FloatRange range) {
        _character = character;
        _range = range;
    }
    
    @Override
    public Character getCharacter() {
        return _character;
    }
    
    public FloatRange getRange() {
        return _range;
    }

    @Override
    public String toString() {
        //TODO - possibly??? need to take character maximum and minimum value into account here
        //output should be foo less than x or foo greater than y
        StringBuilder builder = new StringBuilder();
        builder.append(_formatter.formatCharacterDescription(_character));
        builder.append(" ");
        
        builder.append(this.toShortString());
        
        if (_character.hasUnits()) {
            builder.append(" ");
            builder.append(_character.getUnits());
        }
        
        return builder.toString();
    }

    @Override
    public String toShortString() {
        StringBuilder builder = new StringBuilder();
        float minimumValue = _range.getMinimumFloat();
        float maximumValue = _range.getMaximumFloat();
        if (minimumValue == maximumValue) {
            builder.append(minimumValue);
        } else {
            builder.append(minimumValue);
            builder.append("-");
            builder.append(maximumValue);
        }
        
        return builder.toString();
    }
    
    

}

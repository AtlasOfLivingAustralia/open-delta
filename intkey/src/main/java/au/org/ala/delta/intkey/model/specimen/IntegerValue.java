package au.org.ala.delta.intkey.model.specimen;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.model.IntegerCharacter;

public class IntegerValue extends CharacterValue {

    private IntRange _range;
    private IntegerCharacter _character;
    
    public IntegerValue(IntegerCharacter character, IntRange range) {
        _character = character;
        _range = range;
    }
    
    public IntRange getRange() {
        return _range;
    }

    @Override
    public IntegerCharacter getCharacter() {
        return _character;
    }
    
    @Override
    public String toString() {
        //TODO - need to take character maximum and minimum value into account here
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
        int minimumValue = _range.getMinimumInteger();
        int maximumValue = _range.getMaximumInteger();
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

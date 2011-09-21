package au.org.ala.delta.intkey.model.specimen;

import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.format.Formatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;

public class RealValue extends CharacterValue {
    
    private RealCharacter _character;
    private FloatRange _range;
    private Formatter _unitsFormatter;

    public RealValue(RealCharacter character, FloatRange range) {
        _character = character;
        _range = range;
        _unitsFormatter = new Formatter(true, AngleBracketHandlingMode.RETAIN, true);
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
        StringBuilder builder = new StringBuilder();
        builder.append(_formatter.formatCharacterDescription(_character));
        builder.append(" ");
        
        builder.append(this.toShortString());
        
        if (_character.hasUnits()) {
            builder.append(" ");
            builder.append(_unitsFormatter.defaultFormat(_character.getUnits()));
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_character == null) ? 0 : _character.hashCode());
        result = prime * result + ((_range == null) ? 0 : _range.hashCode());
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
        RealValue other = (RealValue) obj;
        if (_character == null) {
            if (other._character != null)
                return false;
        } else if (!_character.equals(other._character))
            return false;
        if (_range == null) {
            if (other._range != null)
                return false;
        } else if (!_range.equals(other._range))
            return false;
        return true;
    }
    
}

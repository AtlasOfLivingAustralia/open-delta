package au.org.ala.delta.intkey.model.specimen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.format.Formatter;

public class IntegerValue extends CharacterValue {

    private IntegerCharacter _character;
    private List<Integer> _values;
    private Formatter _unitsFormatter;

    public IntegerValue(IntegerCharacter character, Set<Integer> values) {
        _character = character;
        _unitsFormatter = new Formatter(true, false, false, true);

        int charMinimum = _character.getMinimumValue();
        int charMaximum = _character.getMaximumValue();

        // All values below the character minimum are treated as the
        // same value by intkey. Similarly, all values above the character
        // maximum are
        // treated as the same value. This leads to descriptions ".... or less"
        // and "... or more".
        // Use the values character minimum - 1 and character maximum + 1 the
        // indicate the presence
        // of values that fall into either of the aforementioned groups.
        boolean belowMinimumPresent = false;
        boolean aboveMaximumPresent = false;

        _values = new ArrayList<Integer>();
        for (int i : values) {
            if (i < charMinimum) {
                belowMinimumPresent = true;
            } else if (i > charMaximum) {
                aboveMaximumPresent = true;
            } else {
                _values.add(i);
            }
        }

        if (belowMinimumPresent) {
            _values.add(0, charMinimum - 1);
        }

        if (aboveMaximumPresent) {
            _values.add(charMaximum + 1);
        }

        Collections.sort(_values);
    }

    @Override
    public IntegerCharacter getCharacter() {
        return _character;
    }

    public List<Integer> getValues() {
        return new ArrayList<Integer>(_values);
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
        IntegerValue other = (IntegerValue) obj;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(_formatter.formatCharacterDescription(_character));
        builder.append(" ");

        builder.append(buildValuesString(false));

        if (_character.hasUnits()) {
            builder.append(" ");
            builder.append(_unitsFormatter.defaultFormat(_character.getUnits()));
        }

        return builder.toString();
    }

    @Override
    public String toShortString() {
        return buildValuesString(true);
    }

    /**
     * Build a string listing the integer values in sets of ranges
     * 
     * @param shortVersion
     *            use a shorter, more concise format
     * @return
     */
    public String buildValuesString(boolean shortVersion) {
        StringBuilder builder = new StringBuilder();

        int belowMinimum = _character.getMinimumValue() - 1;
        int aboveMaximum = _character.getMaximumValue() + 1;

        boolean belowMinimumPresent = false;
        boolean aboveMaximumPresent = false;

        List<Integer> valuesCopy = new ArrayList<Integer>(_values);

        // One below the character minimum and one above the character
        // maximum are special cases. They should always be written out
        // on their own.
        if (valuesCopy.contains(belowMinimum)) {
            belowMinimumPresent = true;
            valuesCopy.remove((Integer) belowMinimum);
        }

        if (valuesCopy.contains(aboveMaximum)) {
            aboveMaximumPresent = true;
            valuesCopy.remove((Integer) aboveMaximum);
        }

        List<IntRange> intRanges = new ArrayList<IntRange>();

        if (valuesCopy.size() == 1) {
            intRanges.add(new IntRange(valuesCopy.get(0), valuesCopy.get(0)));
        } else {
            int startCurrentRange = 0;
            for (int i = 0; i < valuesCopy.size(); i++) {
                int num = valuesCopy.get(i);
                if (i > 0) {
                    int prevNum = valuesCopy.get(i - 1);

                    if (num != prevNum + 1) {
                        intRanges.add(new IntRange(startCurrentRange, prevNum));
                        startCurrentRange = num;
                    }

                    if (i == valuesCopy.size() - 1) {
                        intRanges.add(new IntRange(startCurrentRange, num));
                    }
                } else {
                    startCurrentRange = num;
                }
            }
        } 

        String orSeparator;
        if (shortVersion) {
            orSeparator = "/";
        } else {
            orSeparator = "; or ";
        }

        if (belowMinimumPresent) {
            builder.append(Integer.toString(belowMinimum));
            if (!shortVersion) {
                builder.append(" or less");
            }
            if (intRanges.size() > 0 || aboveMaximumPresent) {
                builder.append(orSeparator);
            }
        }

        for (int i = 0; i < intRanges.size(); i++) {
            IntRange range = intRanges.get(i);

            if (range.getMinimumInteger() == range.getMaximumInteger()) {
                builder.append(Integer.toString(range.getMinimumInteger()));
            } else {
                builder.append(Integer.toString(range.getMinimumInteger()) + "-" + Integer.toString(range.getMaximumInteger()));
            }

            if (i != intRanges.size() - 1 || aboveMaximumPresent) {
                builder.append(orSeparator);
            }
        }

        if (aboveMaximumPresent) {
            builder.append(Integer.toString(aboveMaximum));
            if (!shortVersion) {
                builder.append(" or more");
            }
        }

        return builder.toString();
    }

}

package au.org.ala.delta.model.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.RealAttribute;
import au.org.ala.delta.model.TextAttribute;

/**
 * Formats an attribute.
 */
public class AttributeFormatter extends CharacterFormatter {

    public AttributeFormatter(boolean includeNumber, boolean stripFormatting, boolean stripComments) {
        super(includeNumber, stripComments, false, stripFormatting);
    }

    public AttributeFormatter(boolean includeNumber, boolean stripFormatting, boolean stripComments, boolean replaceBrackets) {
        super(includeNumber, stripComments, replaceBrackets, stripFormatting);
    }

    /**
     * Attribute formatting differs from Character and Item formatting in that
     * by default attribute comments are not removed.
     * 
     * @param attribute
     *            the attribute to format.
     * @return the formatted attribute value.
     */
    public String formatComment(String comment) {

        if (StringUtils.isEmpty(comment) || EMPTY_COMMENT_PATTERN.matcher(comment).matches()) {
            return "";
        }
        return defaultFormat(comment);
    }

    public String formatAttribute(Attribute attribute) {
        if (attribute instanceof MultiStateAttribute) {
            return formatMultiStateAttribute((MultiStateAttribute) attribute);
        } else if (attribute instanceof IntegerAttribute) {
            return formatIntegerAttribute((IntegerAttribute) attribute);
        } else if (attribute instanceof RealAttribute) {
            return formatRealAttribute((RealAttribute) attribute);
        } else if (attribute instanceof TextAttribute) {
            return formatTextAttribute((TextAttribute) attribute);
        } else {
            throw new IllegalArgumentException("Unrecognised attribute type");
        }
    }

    private String formatMultiStateAttribute(MultiStateAttribute attribute) {
        StringBuilder builder = new StringBuilder();

        List<Integer> values = new ArrayList<Integer>(attribute.getPresentStates());

        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                // TODO "or" needs to be internationalized
                builder.append("; or ");
            }

            builder.append(formatState(attribute.getCharacter(), values.get(i)));
        }

        return builder.toString().trim();
    }

    private String formatIntegerAttribute(IntegerAttribute attribute) {
        StringBuilder builder = new StringBuilder();

        int belowMinimum = attribute.getCharacter().getMinimumValue() - 1;
        int aboveMaximum = attribute.getCharacter().getMaximumValue() + 1;

        boolean belowMinimumPresent = false;
        boolean aboveMaximumPresent = false;

        List<Integer> valuesCopy = new ArrayList<Integer>(attribute.getPresentValues());
        Collections.sort(valuesCopy);

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

        String orSeparator = "; or ";

        if (belowMinimumPresent) {
            builder.append(Integer.toString(belowMinimum));
            builder.append(" or less");
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
            builder.append(" or more");
        }
        
        String units = attribute.getCharacter().getUnits();
        if (!StringUtils.isBlank(units)) {
            builder.append(" ");
            builder.append(units);
        }

        return builder.toString();
    }

    private String formatRealAttribute(RealAttribute attribute) {
        FloatRange range = attribute.getPresentRange();
        if (range != null) {
            StringBuilder builder = new StringBuilder();
            float minimumValue = range.getMinimumFloat();
            float maximumValue = range.getMaximumFloat();
            if (minimumValue == maximumValue) {
                builder.append(minimumValue);
            } else {
                builder.append(minimumValue);
                builder.append("-");
                builder.append(maximumValue);
            }

            builder.append(" ");
            builder.append(attribute.getCharacter().getUnits());

            return builder.toString();
        } else {
            return null;
        }
    }

    private String formatTextAttribute(TextAttribute attribute) {
        return defaultFormat(attribute.getText());
    }

}

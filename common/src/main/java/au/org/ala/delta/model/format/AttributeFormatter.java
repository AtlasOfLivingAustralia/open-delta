package au.org.ala.delta.model.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealAttribute;
import au.org.ala.delta.model.TextAttribute;

/**
 * Formats an attribute.
 */
public class AttributeFormatter extends Formatter {

    private boolean _includeNumber;

    private String _orMoreCaption;
    private String _orLessCaption;
    private String _notRecordedCaption;
    private String _notApplicableCaption;
    private String _orWord;

    public AttributeFormatter(boolean includeNumber, boolean stripFormatting, CommentStrippingMode commentStrippingMode) {
        super(commentStrippingMode, AngleBracketHandlingMode.RETAIN, stripFormatting, false);
        _includeNumber = includeNumber;
        initCaptions();
    }

    public AttributeFormatter(boolean includeNumber, boolean stripFormatting, CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleBracketHandlingMode,
            boolean capitaliseFirstWord, String orWord) {
        super(commentStrippingMode, angleBracketHandlingMode, stripFormatting, capitaliseFirstWord);
        _includeNumber = includeNumber;
        initCaptions();
        if (!StringUtils.isEmpty(orWord)) {
            _orWord = orWord;
        }
    }

    private void initCaptions() {
        ResourceBundle bundle = ResourceBundle.getBundle("au/org/ala/delta/resources/delta-common");
        _orMoreCaption = bundle.getString("AttributeFormatter.OrMore");
        _orLessCaption = bundle.getString("AttributeFormatter.OrLess");
        _notRecordedCaption = bundle.getString("AttributeFormatter.NotRecorded");
        _notApplicableCaption = bundle.getString("AttributeFormatter.NotApplicable");
        _orWord = bundle.getString("AttributeFormatter.DefaultOrWord");
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

    /**
     * Format the supplied attribute
     * 
     * @param attribute
     *            the attribute to format
     * @return A formatted string describing the attribute
     */
    public String formatAttribute(Attribute attribute) {
        if (attribute.isInapplicable() && attribute.isUnknown()) {
            return _notApplicableCaption;
        } else if (attribute.isUnknown()) {
            return _notRecordedCaption;
        } else {
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
    }

    private String formatMultiStateAttribute(MultiStateAttribute attribute) {
        StringBuilder builder = new StringBuilder();

        MultiStateCharacter character = attribute.getCharacter();
        List<Integer> values = new ArrayList<Integer>(attribute.getPresentStates());

        for (int i = 0; i < values.size(); i++) {
            int stateNumber = values.get(i);

            if (i > 0) {
                String orSeparator = null;
                if (attribute.getCharacter().getOmitOr() == true) {
                    orSeparator = "; " + _orWord + " ";
                } else {
                    orSeparator = "; ";
                }

                builder.append(orSeparator);
            }

            if (_includeNumber) {
                builder.append("(");
                builder.append(stateNumber);
                builder.append(") ");
            }
            String stateText = character.getState(stateNumber);
            builder.append(defaultFormat(stateText));
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

        String orSeparator = null;
        if (attribute.getCharacter().getOmitOr() == true) {
            orSeparator = "; " + _orWord + " ";
        } else {
            orSeparator = "; ";
        }

        if (belowMinimumPresent) {
            builder.append(String.format(_orLessCaption, Integer.toString(belowMinimum)));
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
            builder.append(String.format(_orMoreCaption, Integer.toString(aboveMaximum)));
        }

        String units = attribute.getCharacter().getUnits();
        if (!StringUtils.isBlank(units)) {
            builder.append(" ");
            builder.append(units);
        }

        return defaultFormat(builder.toString());
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

            return defaultFormat(builder.toString());
        } else {
            return null;
        }
    }

    private String formatTextAttribute(TextAttribute attribute) {
        return defaultFormat(attribute.getText());
    }

}

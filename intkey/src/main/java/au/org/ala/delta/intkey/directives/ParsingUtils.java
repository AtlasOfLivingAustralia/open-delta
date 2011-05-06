package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

public class ParsingUtils {
    private static Pattern INT_RANGE_PATTERN = Pattern.compile("^(\\d+)-(\\d+)$");
    private static Pattern FLOAT_RANGE_PATTERN = Pattern.compile("^(\\d+(\\.\\d+)?)-(\\d+(\\.\\d+)?)$");
    private static Pattern INT_LIST_PATTERN = Pattern.compile("^\\d+(/\\d+)+$");
    
    //private static Pattern FLOAT_LIST_PATTERN = Pattern.compile("^(\\d+(\\.\\d+)?)-(\\d+(\\.\\d+)?)$");

    public static List<Integer> parseMultiStateCharacterValue(String charValue) {
        List<Integer> retList = parseIntList(charValue);
        
        if (retList == null) {
            IntRange range = parseIntRange(charValue);
            retList = new ArrayList<Integer>();
            if (range != null) {
                for (int i: range.toArray()) {
                    retList.add(i);
                }
            }
        }
        
        if (retList == null) {
            throw new IllegalArgumentException("Invalid multistate value");
        }
        
        return retList;
    }

    public static IntRange parseIntegerCharacterValue(String charValue) {
        IntRange r = parseIntRange(charValue);

        if (r == null) {
            List<Integer> list = parseIntList(charValue);
            if (list != null) {
                Collections.sort(list);
                r = new IntRange(list.get(0), list.get(list.size() - 1));
            }
        }
        
        if (r == null) {
            throw new IllegalArgumentException("Invalid integer value");
        }

        return r;
    }

    public static FloatRange parseRealCharacterValue(String charValue) {
        FloatRange r = parseFloatRange(charValue);

        //TODO - make it so that floats can be parsed using the "/" separator
        
        if (r == null) {
            throw new IllegalArgumentException("Invalid integer value");
        }

        return r;

    }

    public static List<String> parseTextCharacterValue(String charValue) {
        // Remove surrounding quotes if they are present
        if (charValue.charAt(0) == '"' && charValue.charAt(charValue.length() - 1) == '"') {
            charValue = charValue.substring(1, charValue.length() - 2);
        }

        List<String> retList = new ArrayList<String>();
        for (String s : charValue.split("/")) {
            retList.add(s);
        }

        return retList;
    }

    public static IntRange parseIntRange(String text) {
        try {
            Matcher m = INT_RANGE_PATTERN.matcher(text);
            if (m.matches()) {
                int lhs = Integer.parseInt(m.group(1));
                int rhs = Integer.parseInt(m.group(2));
                return new IntRange(lhs, rhs);
            } else {
                return new IntRange(Integer.parseInt(text));
            }
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static FloatRange parseFloatRange(String text) {
        try {
            Matcher m = FLOAT_RANGE_PATTERN.matcher(text);
            if (m.matches()) {
                float lhs = Float.parseFloat(m.group(1));
                float rhs = Float.parseFloat(m.group(3));
                return new FloatRange(lhs, rhs);
            } else {
                return new FloatRange(Float.parseFloat(text));
            }
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static List<Integer> parseIntList(String text) {
        List<Integer> retList = null;

        try {
            Matcher m = INT_LIST_PATTERN.matcher(text);
            if (m.matches()) {
                retList = new ArrayList<Integer>();
                String[] tokens = text.split("/");
                for (String token : tokens) {
                    retList.add(Integer.parseInt(token));
                }
            }
        } catch (NumberFormatException ex) {
            return null;
        }

        return retList;
    }
}

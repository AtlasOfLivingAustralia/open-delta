package au.org.ala.delta.intkey.directives;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.ui.IntegerInputDialog;
import au.org.ala.delta.intkey.ui.MultiStateInputDialog;
import au.org.ala.delta.intkey.ui.RealInputDialog;
import au.org.ala.delta.intkey.ui.TextInputDialog;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;

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
            throw new IllegalArgumentException("Invalid real value");
        }

        return r;

    }

    public static List<String> parseTextCharacterValue(String charValue) {
        // Remove surrounding quotes if they are present
        charValue = removeEnclosingQuotes(charValue);

        List<String> retList = new ArrayList<String>();
        for (String s : charValue.split("/")) {
            retList.add(s);
        }

        return retList;
    }

    //TODO this method is the same as a method on the AbstractDirective
    //class. Need to refactor that one out to avoid duplication here.
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
    
    public static List<String> splitDataIntoSubCommands(String data) {
        List<String> subCommands = new ArrayList<String>();

        boolean inQuotedString = false;
        int endLastSubcommand = 0;
        for (int i = 0; i < data.length(); i++) {
            boolean isEndSubcommand = false;

            char c = data.charAt(i);

            if (c == '"') {
                // TODO ignore quote if it is in the middle of a string
                // don't throw error for unmatched quotes.
                // this is the behaviour in the legacy intkey - may change this
                // later.

                if (i == 0) {
                    inQuotedString = true;
                } else if (i != data.length() - 1) {
                    char preceedingChar = data.charAt(i - 1);
                    char followingChar = data.charAt(i + 1);
                    if (inQuotedString && (followingChar == ' ' || followingChar == ',')) {
                        inQuotedString = false;
                    } else if (!inQuotedString && (preceedingChar == ' ' || preceedingChar == ',')) {
                        inQuotedString = true;
                    }
                }
            } else if (c == ' ' && !inQuotedString) {
                // if we're not inside a quoted string, then a space designates
                // the end of a subcommand
                isEndSubcommand = true;
            }

            if (i == (data.length() - 1)) {
                // end of data string always designates the end of a subcommand
                isEndSubcommand = true;
            }

            if (isEndSubcommand) {
                String subCommand = null;
                if (endLastSubcommand == 0) {
                    subCommand = data.substring(endLastSubcommand, i + 1);
                } else {
                    subCommand = data.substring(endLastSubcommand + 1, i + 1);
                }
                
                //use trim to remove spaces
                subCommands.add(subCommand.trim());
                endLastSubcommand = i;
            }
        }

        return subCommands;
    }
    
    public static String removeEnclosingQuotes(String str) {
        if (str.charAt(0) == '"' && str.charAt(str.length() - 1) == '"') {
            return(str.substring(1, str.length() - 1));
        }
        return str;
    }

}

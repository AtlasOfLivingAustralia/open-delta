package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

public class ParsingUtils {
    private static Pattern INT_RANGE_PATTERN = Pattern.compile("^(-?\\d+)-(-?\\d+)$");
    private static Pattern FLOAT_RANGE_PATTERN = Pattern.compile("^(-?\\d+(\\.\\d+)?)-(-?\\d+(\\.\\d+)?)$");

    public static final String KEYWORD_SPECIMEN = "specimen";

    // Take an individual number, number range or keyword and return the
    // corresponding characters
    public static List<Character> parseCharacterToken(String characterToken, IntkeyContext context) {
        List<Character> characters = new ArrayList<Character>();

        IntRange range = ParsingUtils.parseIntRange(characterToken);
        if (range != null) {
            for (int i : range.toArray()) {
                Character c = context.getDataset().getCharacter(i);
                characters.add(c);
            }
        } else {
            List<Character> keywordCharacters = context.getCharactersForKeyword(removeEnclosingQuotes(characterToken));
            characters.addAll(keywordCharacters);
        }

        return characters;
    }

    // Take an individual number, number range or keyword and return the
    // corresponding taxa
    public static List<Item> parseTaxonToken(String taxonToken, IntkeyContext context) {
        List<Item> taxa = new ArrayList<Item>();

        IntRange range = ParsingUtils.parseIntRange(taxonToken);

        if (range != null) {
            for (int i : range.toArray()) {
                Item t = context.getDataset().getTaxon(i);
                taxa.add(t);
            }
        } else {
            List<Item> keywordTaxa = context.getTaxaForKeyword(removeEnclosingQuotes(taxonToken));
            taxa.addAll(keywordTaxa);
        }

        return taxa;
    }

    public static Set<Integer> parseMultistateOrIntegerCharacterValue(String charValue) {
        Set<Integer> selectedStates = new HashSet<Integer>();

        // split on "/" character to get a list of ranges
        String[] tokens = charValue.split("/");

        for (String token : tokens) {
            IntRange r = parseIntRange(token);

            if (r == null) {
                throw new IllegalArgumentException("Invalid integer value");
            }

            for (int i : r.toArray()) {
                selectedStates.add(i);
            }
        }

        return selectedStates;
    }

    public static FloatRange parseRealCharacterValue(String charValue) {
        // The "/" character is interpreted as the range separator when
        // parsing a real value.
        charValue = charValue.replace("/", "-");

        FloatRange r = parseFloatRange(charValue);

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

    // TODO this method is the same as a method on the AbstractDirective
    // class. Need to refactor that one out to avoid duplication here.
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

    public static List<String> tokenizeDirectiveCall(String data) {
        List<String> tokens = new ArrayList<String>();
        if (data == null) {
            return tokens;
        }

        boolean inQuotedString = false;
        int endLastToken = -1;
        for (int i = 0; i < data.length(); i++) {
            boolean isEndToken = false;

            char c = data.charAt(i);

            char prevChar = 0;
            if (i > 0) {
                prevChar = data.charAt(i - 1);
            }

            char nextChar = 0;
            if (i < data.length() - 1) {
                nextChar = data.charAt(i + 1);
            }

            // open and close parentheses are tokens on their own (provided we
            // are not inside a quoted string)
            if ((c == '(' || c == ')') && !inQuotedString) {
                isEndToken = true;
                // If the next character is an open or end parenthesis, we are
                // at the end of the curent token
                // (provided we are not inside a quoted string)
            } else if ((nextChar == '(' || nextChar == ')') && !inQuotedString) {
                isEndToken = true;
            } else if (c == '"') {
                // Ignore quote if it is in the middle of a string -
                // don't throw error for unmatched quotes.
                // this is the behaviour in the legacy intkey - may change this
                // later.

                if (i == 0) {
                    inQuotedString = true;
                } else if (i != data.length() - 1) {
                    if (inQuotedString && (nextChar == ' ' || nextChar == ',' || nextChar == '\n' || nextChar == '\r')) {
                        inQuotedString = false;
                    } else if (!inQuotedString && (prevChar == ' ' || prevChar == ',' || prevChar == '\n' || prevChar == '\r')) {
                        inQuotedString = true;
                    }
                }
            } else if ((c == ' ' || c == '\n' || c == '\r') && !inQuotedString) {
                // if we're not inside a quoted string, then a space or newline
                // designates
                // the end of a token
                isEndToken = true;
            }

            if (i == (data.length() - 1)) {
                // end of data string always designates the end of a token
                isEndToken = true;
            }

            if (isEndToken) {
                String token = null;
                if (endLastToken == -1) {
                    token = data.substring(0, i + 1);
                } else {
                    token = data.substring(endLastToken + 1, i + 1);
                }

                // use trim to remove any remaining whitespace. Tokens that
                // consist solely of whitespace should be completely omitted.
                String trimmedToken = token.trim();
                if (trimmedToken.length() > 0) {
                    tokens.add(token.trim());
                }
                endLastToken = i;
            }
        }

        return tokens;
    }

    public static String removeEnclosingQuotes(String str) {
        if (str.charAt(0) == '"' && str.charAt(str.length() - 1) == '"') {
            return (str.substring(1, str.length() - 1));
        }
        return str;
    }

}

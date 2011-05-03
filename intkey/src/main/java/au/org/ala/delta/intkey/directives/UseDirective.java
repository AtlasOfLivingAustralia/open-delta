package au.org.ala.delta.intkey.directives;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.ui.TextInputDialog;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;

public class UseDirective extends IntkeyDirective {

    private static Pattern COMMA_SEPARATED_VALUE_PATTERN = Pattern.compile("^.+,.*$");

    private static Pattern RANGE_VALUE_PATTERN = Pattern.compile("^\\d+-\\d+$");
    private static Pattern INT_VALUE_PATTERN = Pattern.compile("^\\d+$");

    public UseDirective() {
        super("use");
    }

    @Override
    public IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        if (context.getDataset() != null) {

            List<String> subCommands = splitDataIntoSubCommands(data);
            System.out.println(subCommands);

            boolean suppressAlreadySetWarning = false;
            
            List<Integer> characterNumbers = new ArrayList<Integer>();
            List<String> specifiedValues = new ArrayList<String>();

            for (String subCmd : subCommands) {
                if (subCmd.equalsIgnoreCase("/M")) {
                    suppressAlreadySetWarning = true;
                } else {
                    parseSubcommands(subCmd, characterNumbers, specifiedValues);
                }
            }

            for (int i = 0; i < characterNumbers.size(); i++) {
                int charNum = characterNumbers.get(i);
                au.org.ala.delta.model.Character ch = context.getDataset().getCharacter(charNum);

                String charValue = specifiedValues.get(i);

                Object parsedCharValue = null;

                if (charValue != null) {
                    if (ch instanceof MultiStateCharacter) {

                    } else if (ch instanceof IntegerCharacter) {

                    } else if (ch instanceof RealCharacter) {

                    } else if (ch instanceof TextCharacter) {
                        parsedCharValue = parseTextValue(charValue);
                    } else {
                        throw new IllegalArgumentException("Unrecognized character type");
                    }
                } else {
                    if (ch instanceof MultiStateCharacter) {

                    } else if (ch instanceof IntegerCharacter) {

                    } else if (ch instanceof RealCharacter) {

                    } else if (ch instanceof TextCharacter) {
                        parsedCharValue = promptForTextValue(context.getMainFrame(), (TextCharacter)ch);
                        System.out.print("Parsed Value: " + parsedCharValue.toString());
                    } else {
                        throw new IllegalArgumentException("Unrecognized character type");
                    }
                }
            }
        } else {
            throw new RuntimeException("Need to have a dataset loaded before USE can be called.");
        }

        // TODO Auto-generated method stub

        // INITALIZE

        // PROCESS CHARACTERS WITH ATTRIBUTES FIRST
        // for each character specified
        // process controlling characters of the character (dataset.cc_process)
        // use character

        // PROCESS CHARACTERS WITHOUT ATTRIBUTES NEXT

        return null;
    }

    private List<String> splitDataIntoSubCommands(String data) {
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
                    } else if (!inQuotedString && (preceedingChar == ' ' || preceedingChar == ',' )) {
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
                subCommands.add(subCommand);
                endLastSubcommand = i;
            }
        }

        return subCommands;
    }

    private void parseSubcommands(String subCmd, List<Integer> characterNumbers, List<String> specifiedValues) throws Exception {

        List<Integer> parsedCharacterNumbers;

        // switch
        if (COMMA_SEPARATED_VALUE_PATTERN.matcher(subCmd).matches()) {
            // comma separated value: c,v

            String lhs = null;
            String rhs = null;

            String[] innerPieces = subCmd.split(",");
            if (innerPieces.length > 2) {
                throw new Exception("Bad Syntax");
            }

            lhs = innerPieces[0];

            if (innerPieces.length == 2) {
                rhs = innerPieces[1];
            }

            parsedCharacterNumbers = parseLHS(lhs);
            for (int c : parsedCharacterNumbers) {
                specifiedValues.add(rhs);
            }
        } else {
            parsedCharacterNumbers = parseLHS(subCmd);
            for (int c : parsedCharacterNumbers) {
                specifiedValues.add(null);
            }
        }
        characterNumbers.addAll(parsedCharacterNumbers);
    }

    private List<Integer> parseLHS(String lhs) {
        List<Integer> retList = new ArrayList<Integer>();

        if (INT_VALUE_PATTERN.matcher(lhs).matches()) {
            System.out.println("Int: " + lhs);
            int val = Integer.parseInt(lhs);
            retList.add(val);
        } else if (RANGE_VALUE_PATTERN.matcher(lhs).matches()) {
            IntRange range = parseRange(lhs);
            for (int i : range.toArray()) {
                retList.add(i);
            }
        } else {
            System.out.println("Keyword: " + lhs);
        }

        return retList;
    }

    private List<Integer> parseMultiStateValue(String charValue) {
        return null;
    }

    private IntRange parseIntegerValue(String charValue) {
        return null;
    }

    private FloatRange parseRealValue(String charValue) {
        return null;
    }

    private List<String> parseTextValue(String charValue) {
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

    private List<Integer> promptForMultiStateValue(MultiStateCharacter ch) {
        return null;
    }

    private IntRange promptForIntegerValue(IntegerCharacter ch) {
        return null;
    }

    private FloatRange promptForRealValue(RealCharacter ch) {
        return null;
    }

    private List<String> promptForTextValue(Frame frame, TextCharacter ch) {
        TextInputDialog dlg = new TextInputDialog(frame, ch);
        dlg.setVisible(true);
        return dlg.getInputData();
    }

    class UseDirectiveInvocation implements IntkeyDirectiveInvocation {

        // List<String>

        @Override
        public void execute(IntkeyContext context) {
            // TODO Auto-generated method stub

        }

    }

}

package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;

public class UseDirective extends IntkeyDirective {

    // TODO complete parsing for non-text character values
    // TODO do "toString()" for invocation class
    // TODO show message box when try to set a character value but it fails due
    // to it not
    // available - need to do anything else when this happens?

    private static Pattern COMMA_SEPARATED_VALUE_PATTERN = Pattern.compile("^.+,.*$");

    public UseDirective() {
        super("use");
    }

    @Override
    public IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        if (context.getDataset() != null) {

            List<String> subCommands = ParsingUtils.splitDataIntoSubCommands(data);

            boolean suppressAlreadySetWarning = false;

            List<Integer> characterNumbers = new ArrayList<Integer>();
            List<String> specifiedValues = new ArrayList<String>();

            for (String subCmd : subCommands) {
                if (subCmd.equalsIgnoreCase("/M")) {
                    suppressAlreadySetWarning = true;
                } else {
                    parseSubcommands(subCmd, characterNumbers, specifiedValues, context);
                }
            }

            if (characterNumbers.size() == 0) {
                // If no character numbers (or keywords) were specified, then
                // the user needs to
                // be prompted to select which character(s) they want to use.

                // set characterNumbers list in here.
            }

            UseDirectiveInvocation invoc = new UseDirectiveInvocation(suppressAlreadySetWarning);

            Specimen specimen = context.getSpecimen();
            for (int i = 0; i < characterNumbers.size(); i++) {
                int charNum = characterNumbers.get(i);
                au.org.ala.delta.model.Character ch = context.getDataset().getCharacter(charNum);

                if (!suppressAlreadySetWarning) {
                    if (specimen.hasValueFor(ch)) {
                        String msg = String.format("Character %d has already been used. Do you want to change the value(s) you entered?", ch.getCharacterId());
                        int dlgSelection = JOptionPane.showConfirmDialog(context.getMainFrame(), msg, "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        if (dlgSelection == JOptionPane.NO_OPTION) {
                            continue;
                        } else {
                            // Remove the value that is already set in the
                            // specimen for this character. This will stop the
                            // same prompt being
                            // shown when the UseDirectiveInvocation is
                            // executed. The check needs to be done in two
                            // places because commands can be re-executed.
                            specimen.removeValueForCharacter(ch);
                        }
                    }
                }

                // Parse the supplied value for each character, or prompt for
                // one if no value was supplied

                String charValue = specifiedValues.get(i);

                Object parsedCharValue = null;

                if (charValue != null) {
                    if (ch instanceof MultiStateCharacter) {
                        parsedCharValue = ParsingUtils.parseMultiStateCharacterValue(charValue);
                    } else if (ch instanceof IntegerCharacter) {
                        parsedCharValue = ParsingUtils.parseIntegerCharacterValue(charValue);
                    } else if (ch instanceof RealCharacter) {
                        parsedCharValue = ParsingUtils.parseRealCharacterValue(charValue);
                    } else if (ch instanceof TextCharacter) {
                        parsedCharValue = ParsingUtils.parseTextCharacterValue(charValue);
                    } else {
                        throw new IllegalArgumentException("Unrecognized character type");
                    }
                } else {
                    if (ch instanceof MultiStateCharacter) {
                        parsedCharValue = ParsingUtils.promptForMultiStateValue(context.getMainFrame(), (MultiStateCharacter) ch);
                    } else if (ch instanceof IntegerCharacter) {
                        parsedCharValue = ParsingUtils.promptForIntegerValue(context.getMainFrame(), (IntegerCharacter) ch);
                    } else if (ch instanceof RealCharacter) {
                        parsedCharValue = ParsingUtils.promptForRealValue(context.getMainFrame(), (RealCharacter) ch);
                    } else if (ch instanceof TextCharacter) {
                        parsedCharValue = ParsingUtils.promptForTextValue(context.getMainFrame(), (TextCharacter) ch);
                    } else {
                        throw new IllegalArgumentException("Unrecognized character type");
                    }
                }

                invoc.addCharacterValue(ch, parsedCharValue);
            }
            return invoc;
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
    }

    public UseDirectiveInvocation createUseDirectiveInvocation(boolean suppessAlreadyUsedWarning, List<Character> characters, List<Object> charValues) {
        UseDirectiveInvocation invoc = new UseDirectiveInvocation(suppessAlreadyUsedWarning);
        for (int i = 0; i < characters.size(); i++) {
            invoc.addCharacterValue(characters.get(i), charValues.get(i));
        }
        return invoc;
    }

    private void parseSubcommands(String subCmd, List<Integer> characterNumbers, List<String> specifiedValues, IntkeyContext context) throws Exception {

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

            parsedCharacterNumbers = parseLHS(lhs, context);
            for (int c : parsedCharacterNumbers) {
                specifiedValues.add(rhs);
            }
        } else {
            parsedCharacterNumbers = parseLHS(subCmd, context);
            for (int c : parsedCharacterNumbers) {
                specifiedValues.add(null);
            }
        }
        characterNumbers.addAll(parsedCharacterNumbers);
    }

    private List<Integer> parseLHS(String lhs, IntkeyContext context) {
        List<Integer> retList = new ArrayList<Integer>();

        IntRange range = ParsingUtils.parseIntRange(lhs);
        if (range != null) {
            for (int i : range.toArray()) {
                retList.add(i);
            }
        } else {
            // TODO handle exception if not valid keyword passed.
            retList = context.getCharacterNumbersForKeyword(lhs);
        }

        return retList;
    }

    class UseDirectiveInvocation implements IntkeyDirectiveInvocation {

        private Map<au.org.ala.delta.model.Character, Object> _characterValues;
        private boolean _suppressAlreadySetWarning;

        public UseDirectiveInvocation(boolean suppressAlreadySetWarning) {
            _suppressAlreadySetWarning = suppressAlreadySetWarning;
            _characterValues = new HashMap<Character, Object>();
        }

        @Override
        public void execute(IntkeyContext context) {
            Specimen specimen = context.getSpecimen();

            for (Character ch : _characterValues.keySet()) {
                Object characterVal = _characterValues.get(ch);

                if (!_suppressAlreadySetWarning) {
                    if (specimen.hasValueFor(ch)) {
                        String msg = String.format("Character %d has already been used. Do you want to change the value(s) you entered?", ch.getCharacterId());
                        int dlgSelection = JOptionPane.showConfirmDialog(context.getMainFrame(), msg, "Information", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        if (dlgSelection == JOptionPane.NO_OPTION) {
                            continue;
                        }
                    }
                }

                if (ch instanceof MultiStateCharacter) {
                    specimen.setMultiStateValue((MultiStateCharacter) ch, (List<Integer>) characterVal);
                } else if (ch instanceof IntegerCharacter) {
                    specimen.setIntegerValue((IntegerCharacter) ch, (IntRange) characterVal);
                } else if (ch instanceof RealCharacter) {
                    specimen.setRealValue((RealCharacter) ch, (FloatRange) characterVal);
                } else if (ch instanceof TextCharacter) {
                    specimen.setTextValue((TextCharacter) ch, (List<String>) characterVal);
                } else {
                    throw new RuntimeException("Unrecognized character type");
                }
            }

        }

        void addCharacterValue(au.org.ala.delta.model.Character ch, Object val) {
            _characterValues.put(ch, val);
        }
    }

}

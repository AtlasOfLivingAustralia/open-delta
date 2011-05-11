package au.org.ala.delta.intkey.directives;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.model.specimen.CharacterValue;
import au.org.ala.delta.intkey.model.specimen.IntegerValue;
import au.org.ala.delta.intkey.model.specimen.MultiStateValue;
import au.org.ala.delta.intkey.model.specimen.RealValue;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.intkey.model.specimen.TextValue;
import au.org.ala.delta.intkey.ui.CharacterKeywordSelectionDialog;
import au.org.ala.delta.intkey.ui.IntegerInputDialog;
import au.org.ala.delta.intkey.ui.MultiStateInputDialog;
import au.org.ala.delta.intkey.ui.RealInputDialog;
import au.org.ala.delta.intkey.ui.TextInputDialog;
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
            boolean suppressAlreadySetWarning = false;

            List<Integer> characterNumbers = new ArrayList<Integer>();
            List<String> specifiedValues = new ArrayList<String>();

            if (data != null && data.trim().length() > 0) {
                List<String> subCommands = ParsingUtils.splitDataIntoSubCommands(data);

                for (String subCmd : subCommands) {
                    // TODO need to handle additional undocumented flags
                    if (subCmd.equalsIgnoreCase("/M")) {
                        suppressAlreadySetWarning = true;
                    } else {
                        parseSubcommands(subCmd, characterNumbers, specifiedValues, context);
                    }
                }

            } else {
                // No characters specified, prompt the user to select characters
                CharacterKeywordSelectionDialog dlg = new CharacterKeywordSelectionDialog(context.getMainFrame(), context);
                dlg.setVisible(true);
                if (dlg.getOkButtonPressed()) {
                    List<Character> selectedCharacters = dlg.getSelectedCharacters();
                    for (Character ch : selectedCharacters) {
                        characterNumbers.add(ch.getCharacterId());
                        specifiedValues.add(null);
                    }
                }
            }

            UseDirectiveInvocation invoc = new UseDirectiveInvocation(suppressAlreadySetWarning);

            for (int i = 0; i < characterNumbers.size(); i++) {
                int charNum = characterNumbers.get(i);
                au.org.ala.delta.model.Character ch = context.getDataset().getCharacter(charNum);

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
            List<Character> charList = context.getCharactersForKeyword(lhs);
            for (Character c : charList) {
                retList.add(c.getCharacterId());
            }
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
        public boolean execute(IntkeyContext context) {

            // First pass - perform validation, and prompt for any character values that were
            // not specified by the user
            for (Character ch : _characterValues.keySet()) {
                Object characterVal = _characterValues.get(ch);

                if (!checkCharacterUsable(ch, context)) {
                    continue;
                }

                if (characterVal == null) {
                    if (ch instanceof MultiStateCharacter) {
                        characterVal = promptForMultiStateValue(context.getMainFrame(), (MultiStateCharacter) ch);
                    } else if (ch instanceof IntegerCharacter) {
                        characterVal = promptForIntegerValue(context.getMainFrame(), (IntegerCharacter) ch);
                    } else if (ch instanceof RealCharacter) {
                        characterVal = promptForRealValue(context.getMainFrame(), (RealCharacter) ch);
                    } else if (ch instanceof TextCharacter) {
                        characterVal = promptForTextValue(context.getMainFrame(), (TextCharacter) ch);
                    } else {
                        throw new IllegalArgumentException("Unrecognized character type");
                    }

                    if (characterVal == null) {
                        //User did not enter a value in prompt dialog, or hit "cancel"
                        // - do not set a value for this character
                        _characterValues.remove(ch);
                    } else {
                        // store this value so that the prompt does not need to
                        // be done for subsequent invocations
                        _characterValues.put(ch, characterVal);
                    }
                }
            }
            
            
            for (Character ch : _characterValues.keySet()) {
                Object parsedCharacterVal = _characterValues.get(ch);
                
                CharacterValue value;
                if (ch instanceof MultiStateCharacter) {
                    value = new MultiStateValue((MultiStateCharacter) ch, (List<Integer>) parsedCharacterVal);
                } else if (ch instanceof IntegerCharacter) {
                    value = new IntegerValue((IntegerCharacter) ch, (IntRange) parsedCharacterVal);
                } else if (ch instanceof RealCharacter) {
                    value = new RealValue((RealCharacter) ch, (FloatRange) parsedCharacterVal);
                } else if (ch instanceof TextCharacter) {
                    value = new TextValue((TextCharacter) ch, (List<String>) parsedCharacterVal);
                } else {
                    throw new RuntimeException("Unrecognized character type");
                }
                
                context.setValueForCharacter(ch, value);
            }

            return true;
        }

        void addCharacterValue(au.org.ala.delta.model.Character ch, Object val) {
            _characterValues.put(ch, val);
        }

        private boolean checkCharacterUsable(Character ch, IntkeyContext context) {
            // is character fixed?

            // is character already used?

            // is character unavailable?
            // is character excluded?

            return true;
        }

        private List<Integer> promptForMultiStateValue(Frame frame, MultiStateCharacter ch) {
            MultiStateInputDialog dlg = new MultiStateInputDialog(frame, ch);
            dlg.setVisible(true);
            return dlg.getInputData();
        }

        private IntRange promptForIntegerValue(Frame frame, IntegerCharacter ch) {
            IntegerInputDialog dlg = new IntegerInputDialog(frame, ch);
            dlg.setVisible(true);
            return dlg.getInputData();
        }

        private FloatRange promptForRealValue(Frame frame, RealCharacter ch) {
            RealInputDialog dlg = new RealInputDialog(frame, ch);
            dlg.setVisible(true);
            return dlg.getInputData();
        }

        private List<String> promptForTextValue(Frame frame, TextCharacter ch) {
            TextInputDialog dlg = new TextInputDialog(frame, ch);
            dlg.setVisible(true);
            return dlg.getInputData();
        }

        @Override
        public String toString() {
            return String.format("USE %s", _characterValues.toString());
        }

    }

}

package au.org.ala.delta.intkey.directives;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.CharacterValue;
import au.org.ala.delta.intkey.model.specimen.IntegerValue;
import au.org.ala.delta.intkey.model.specimen.MultiStateValue;
import au.org.ala.delta.intkey.model.specimen.RealValue;
import au.org.ala.delta.intkey.model.specimen.TextValue;
import au.org.ala.delta.intkey.ui.CharacterKeywordSelectionDialog;
import au.org.ala.delta.intkey.ui.CharacterSelectionDialog;
import au.org.ala.delta.intkey.ui.IntegerInputDialog;
import au.org.ala.delta.intkey.ui.MultiStateInputDialog;
import au.org.ala.delta.intkey.ui.RealInputDialog;
import au.org.ala.delta.intkey.ui.TextInputDialog;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;

public class UseDirective extends IntkeyDirective {

    // TODO show message box when try to set a character value but it fails due
    // to it not
    // available - need to do anything else when this happens?

    // TODO UI should only update once at the end - should not update for each
    // controlling
    // character that is prompted for.

    private static Pattern COMMA_SEPARATED_VALUE_PATTERN = Pattern.compile("^.+,.*$");

    private CharacterFormatter _charFormatter;

    public UseDirective() {
        super("use");
        _charFormatter = new CharacterFormatter(false, false, true, true);
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES;
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return doProcess(context, data, false);
    }

    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data, boolean change) throws Exception {
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
                String directiveName = change ? directiveName = StringUtils.join(new ChangeDirective().getControlWords(), " ").toUpperCase() : StringUtils.join(_controlWords, " ").toUpperCase();
                CharacterKeywordSelectionDialog dlg = new CharacterKeywordSelectionDialog(UIUtils.getMainFrame(), context, directiveName);
                dlg.setVisible(true);
                List<Character> selectedCharacters = dlg.getSelectedCharacters();
                if (selectedCharacters.size() > 0) {
                    for (Character ch : selectedCharacters) {
                        characterNumbers.add(ch.getCharacterId());
                        specifiedValues.add(null);
                    }
                } else {
                    // User has hit cancel, nothing to execute
                    return null;
                }
            }

            UseDirectiveInvocation invoc = new UseDirectiveInvocation(change, suppressAlreadySetWarning);

            for (int i = 0; i < characterNumbers.size(); i++) {
                int charNum = characterNumbers.get(i);
                au.org.ala.delta.model.Character ch;
                try {
                    ch = context.getDataset().getCharacter(charNum);
                } catch (IllegalArgumentException ex) {
                    throw new IntkeyDirectiveParseException(String.format(UIUtils.getResourceString("UseDirective.InvalidCharacterNumber"), charNum, context.getDataset().getNumberOfCharacters()), ex);
                }

                // Parse the supplied value for each character, or prompt for
                // one if no value was supplied

                String charValue = specifiedValues.get(i);

                if (charValue != null) {
                    try {
                        if (ch instanceof MultiStateCharacter) {
                            MultiStateCharacter msCh = (MultiStateCharacter) ch;

                            try {
                                Set<Integer> setStateValues = ParsingUtils.parseMultistateOrIntegerCharacterValue(charValue);

                                for (int val : setStateValues) {
                                    if (val < 0 || val > msCh.getNumberOfStates()) {
                                        throw new IntkeyDirectiveParseException(String.format(UIUtils.getResourceString("UseDirective.InvalidStateValue"), charValue,
                                                _charFormatter.formatCharacterDescription(ch), Integer.toString(ch.getCharacterId(), msCh.getNumberOfStates())));
                                    }
                                }

                                invoc.addCharacterValue((MultiStateCharacter) ch, new MultiStateValue((MultiStateCharacter) ch, setStateValues));
                            } catch (IllegalArgumentException ex) {
                                throw new IntkeyDirectiveParseException(String.format(UIUtils.getResourceString("UseDirective.InvalidStateValue"), charValue, Integer.toString(ch.getCharacterId()),
                                        _charFormatter.formatCharacterDescription(ch), msCh.getNumberOfStates()), ex);
                            }
                        } else if (ch instanceof IntegerCharacter) {

                            try {
                                Set<Integer> intValues = ParsingUtils.parseMultistateOrIntegerCharacterValue(charValue);
                                invoc.addCharacterValue((IntegerCharacter) ch, new IntegerValue((IntegerCharacter) ch, intValues));
                            } catch (IllegalArgumentException ex) {
                                throw new IntkeyDirectiveParseException(String.format(UIUtils.getResourceString("UseDirective.InvalidIntegerValue"), charValue, Integer.toString(ch.getCharacterId()),
                                        _charFormatter.formatCharacterDescription(ch)), ex);
                            }
                        } else if (ch instanceof RealCharacter) {

                            try {
                                FloatRange floatRange = ParsingUtils.parseRealCharacterValue(charValue);
                                invoc.addCharacterValue((RealCharacter) ch, new RealValue((RealCharacter) ch, floatRange));
                            } catch (IllegalArgumentException ex) {
                                throw new IntkeyDirectiveParseException(String.format(UIUtils.getResourceString("UseDirective.InvalidRealValue"), charValue, Integer.toString(ch.getCharacterId()),
                                        _charFormatter.formatCharacterDescription(ch)), ex);
                            }
                        } else if (ch instanceof TextCharacter) {
                            List<String> stringList = ParsingUtils.parseTextCharacterValue(charValue);
                            invoc.addCharacterValue((TextCharacter) ch, new TextValue((TextCharacter) ch, stringList));
                        } else {
                            throw new RuntimeException("Unrecognized character type");
                        }
                    } catch (IllegalArgumentException ex) {
                        throw new IntkeyDirectiveParseException(ex.getMessage());
                    }
                } else {
                    invoc.addCharacterValue(ch, null);
                }
            }
            return invoc;
        } else {
            throw new IntkeyDirectiveParseException(UIUtils.getResourceString("UseDirective.NoDataSetMsg"));
        }
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

    public class UseDirectiveInvocation implements IntkeyDirectiveInvocation {

        private Map<au.org.ala.delta.model.Character, CharacterValue> _characterValues;
        private boolean _change;
        private boolean _suppressAlreadySetWarning;

        public UseDirectiveInvocation(boolean change, boolean suppressAlreadySetWarning) {
            _change = change;
            _suppressAlreadySetWarning = suppressAlreadySetWarning;

            // Use LinkedHashMap so that keys can be iterated over in the order
            // that they
            // were inserted.
            _characterValues = new LinkedHashMap<Character, CharacterValue>();
        }

        @Override
        public boolean execute(IntkeyContext context) {

            // Split up characters that have had their values specified and
            // those that
            // haven't. They need to be processed differently.
            List<Character> charsWithValues = new ArrayList<Character>();
            List<Character> charsNoValues = new ArrayList<Character>();

            for (Character ch : _characterValues.keySet()) {
                if (_characterValues.get(ch) == null) {
                    charsNoValues.add(ch);
                } else {
                    charsWithValues.add(ch);
                }
            }

            // Process characters with values specified first
            for (Character ch : charsWithValues) {
                if (checkCharacterUsable(ch, context, !_suppressAlreadySetWarning && !_change)) {
                    // halt execution if values not sucessfully set for all
                    // controlling characters
                    if (!processControllingCharacters(ch, context)) {
                        return false;
                    }
                    
                    // second call to checkCharacterUsable() to ensure that
                    // character has not been
                    // made inapplicable by the value given to one or more of
                    // its controlling
                    // characters
                    if (checkCharacterUsable(ch, context, false)) {
                        CharacterValue characterVal = _characterValues.get(ch);
                        context.setValueForCharacter(ch, characterVal);
                    }
                }
            }

            if (charsNoValues.size() == 1) {
                Character ch = charsNoValues.get(0);
                if (checkCharacterUsable(ch, context, !_suppressAlreadySetWarning && !_change)) {
                    
                    // halt execution if values not sucessfully set for all
                    // controlling characters
                    if (!processControllingCharacters(ch, context)) {
                        return false;
                    }
                    
                    
                    // second call to checkCharacterUsable() to ensure that
                    // character has not been
                    // made inapplicable by the value given to one or more of
                    // its controlling
                    // characters
                    if (checkCharacterUsable(ch, context, false)) {
                        CharacterValue characterVal = promptForCharacterValue(UIUtils.getMainFrame(), ch);
                        if (characterVal != null) {
                            // store this value so that the prompt does not need
                            // to
                            // be done for subsequent invocations
                            _characterValues.put(ch, characterVal);
                            context.setValueForCharacter(ch, characterVal);
                        } else {
                            // User hit cancel or did not enter a value when
                            // prompted.
                            // Abort execution when this happens
                            return false;
                        }
                    } else {
                        // remove this value so that the user will not be
                        // prompted
                        // about it when the command is
                        // run additional times.
                        _characterValues.remove(ch);
                    }
                }
            } else {
                Collections.sort(charsNoValues);
                while (!charsNoValues.isEmpty()) {
                    String directiveName = _change ? directiveName = StringUtils.join(new ChangeDirective().getControlWords(), " ").toUpperCase() : StringUtils.join(_controlWords, " ").toUpperCase();

                    CharacterSelectionDialog selectDlg = new CharacterSelectionDialog(UIUtils.getMainFrame(), charsNoValues, directiveName);
                    selectDlg.setVisible(true);

                    List<Character> selectedCharacters = selectDlg.getSelectedCharacters();

                    if (selectedCharacters.isEmpty()) {
                        // User hit cancel or did not select any characters.
                        // Abort
                        // execution when this happens.
                        // Directive should not be stored in
                        // Execution history
                        return false;
                    }

                    for (Character ch : selectedCharacters) {

                        CharacterValue characterVal = null;

                        if (checkCharacterUsable(ch, context, !_suppressAlreadySetWarning && !_change)) {
                            
                            // halt execution if values not sucessfully set for all
                            // controlling characters
                            if (!processControllingCharacters(ch, context)) {
                                return false;
                            }
                            
                            // As the character's controlling characters have been successfully
                            // set, remove all of them from the list of characters needing to have
                            // their values set.
                            IntkeyDataset dataset = context.getDataset();
                            List<CharacterDependency> charDeps = getFullControllingCharacterDependenciesList(ch, dataset);
                            for (CharacterDependency cd : charDeps) {
                                Character controllingChar = dataset.getCharacter(cd.getControllingCharacterId());
                                charsNoValues.remove(controllingChar);
                            }
                            
                            // second call to checkCharacterUsable() to ensure
                            // that character has not been
                            // made inapplicable by the value given to one or
                            // more of its controlling
                            // characters
                            if (checkCharacterUsable(ch, context, false)) {
                                characterVal = promptForCharacterValue(UIUtils.getMainFrame(), ch);
                            } else {
                                // remove this value so that the user will not
                                // be
                                // prompted about it when the command is
                                // run additional times.
                                _characterValues.remove(ch);
                            }

                            if (characterVal != null) {
                                // store this value so that the prompt does not
                                // need
                                // to
                                // be done for subsequent invocations
                                _characterValues.put(ch, characterVal);
                                context.setValueForCharacter(ch, characterVal);
                                charsNoValues.remove(ch);
                            }
                        }
                    }
                }
            }

            context.specimenUpdateComplete();
            return true;
        }

        void addCharacterValue(au.org.ala.delta.model.Character ch, CharacterValue val) {
            _characterValues.put(ch, val);
        }

        private boolean checkCharacterUsable(Character ch, IntkeyContext context, boolean warnAlreadySet) {
            CharacterFormatter formatter = new CharacterFormatter(false, false, true, true);

            // TODO is character fixed?

            // is character already used?
            if (warnAlreadySet) {
                if (context.getSpecimen().hasValueFor(ch)) {
                    if (context.isProcessingInputFile()) {
                        return true;
                    } else {
                        String msg = String.format(UIUtils.getResourceString("UseDirective.CharacterAlreadyUsed"), formatter.formatCharacterDescription(ch));
                        String title = String.format(UIUtils.getResourceString("Intkey.informationDlgTitle"), formatter.formatCharacterDescription(ch));
                        int choice = JOptionPane.showConfirmDialog(UIUtils.getMainFrame(), msg, title, JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }

            // is character unavailable?
            if (context.getSpecimen().isCharacterInapplicable(ch)) {
                if (!context.isProcessingInputFile()) {
                    String msg = String.format(UIUtils.getResourceString("UseDirective.CharacterUnavailable"), formatter.formatCharacterDescription(ch));
                    String title = String.format(UIUtils.getResourceString("Intkey.informationDlgTitle"), formatter.formatCharacterDescription(ch));
                    JOptionPane.showMessageDialog(UIUtils.getMainFrame(), msg, title, JOptionPane.ERROR_MESSAGE);
                }
                return false;
            }

            // TODO is character excluded?

            return true;
        }

        /**
         * Set values for all controlling characters for the specified character, prompting if necessary
         * @param ch 
         * @param context
         * @return true if values were set successfully for all controlling characters
         */
        private boolean processControllingCharacters(Character ch, IntkeyContext context) {
            List<CharacterDependency> allControllingChars = getFullControllingCharacterDependenciesList(ch, context.getDataset());

            // Used a linked hashmap as need to maintain the ordering returned
            // by getFullControllingCharacterDependenciesList()
            Map<MultiStateCharacter, Set<Integer>> controllingCharInapplicableStates = new LinkedHashMap<MultiStateCharacter, Set<Integer>>();

            // Look through all the CharacterDependency objects for the
            // character and for each controlling character, collect the list of
            // all states for the controlling character that will make the
            // dependent character inapplicable
            for (CharacterDependency cd : allControllingChars) {
                MultiStateCharacter cc = (MultiStateCharacter) context.getDataset().getCharacter(cd.getControllingCharacterId());

                if (context.getSpecimen().hasValueFor(cc)) {
                    continue;
                }

                // states for the controlling character that will make dependent
                // characters inapplicable
                Set<Integer> inapplicableStates = cd.getStates();

                if (controllingCharInapplicableStates.containsKey(cc)) {
                    controllingCharInapplicableStates.get(cc).addAll(inapplicableStates);
                } else {
                    controllingCharInapplicableStates.put(cc, new HashSet<Integer>(inapplicableStates));
                }
            }

            // For each controlling character, set its value or prompt the user
            // for its value as appropriate
            for (MultiStateCharacter cc : controllingCharInapplicableStates.keySet()) {
                Set<Integer> inapplicableStates = controllingCharInapplicableStates.get(cc);

                // states for the controlling character that will make dependent
                // characters applicable. At least one of these states needs to
                // be
                // set on the controlling character.
                Set<Integer> applicableStates = new HashSet<Integer>();

                for (int i = 1; i < cc.getStates().length + 1; i++) {
                    if (!inapplicableStates.contains(i)) {
                        applicableStates.add(i);
                    }
                }

                if (applicableStates.size() == cc.getStates().length) {
                    throw new RuntimeException(String.format("There are no states for character %s that will make character %s applicable", cc.getCharacterId(), ch.getCharacterId()));
                }

                // If not processing an input file, prompt the user to set the
                // value of the controlling character if it has been supplied as
                // an argument to the
                // NON AUTOMATIC CONTROLLING CHARACTERS confor directive, if the
                // dependent character has been supplied as an argument
                // to the USE CONTROLLING CHARACTERS FIRST confor directive, or
                // if there are multiple states that the controlling character
                // can be set to for which the dependent character will be
                // inapplicable.
                if (!context.isProcessingInputFile() && (cc.getNonAutoCc() || ch.getUseCc() || !cc.getNonAutoCc() && !cc.getUseCc() && applicableStates.size() > 1)) {
                    CharacterValue val = promptForCharacterValue(UIUtils.getMainFrame(), cc);
                    if (val != null) {
                        context.setValueForCharacter(cc, val);
                    } else {
                        // No values selected or cancel pressed. Return as values have not been set for all
                        // controlling characters
                        return false;
                    }
                } else {
                    // let intkey automatically use the character
                    MultiStateValue val = new MultiStateValue((MultiStateCharacter) cc, new HashSet<Integer>(applicableStates));
                    context.setValueForCharacter(cc, val);
                }

                // TODO output USEd controlling characters directly to the log
                // window
                // set the "used type" - by user or auto for the controlling
                // character

            }
            
            return true;
        }

        // For the given character, recursively build a list of
        // CharacterDependency objects describing
        // all characters that control it, directly and indirectly.
        private List<CharacterDependency> getFullControllingCharacterDependenciesList(Character ch, IntkeyDataset ds) {
            List<CharacterDependency> retList = new ArrayList<CharacterDependency>();

            List<CharacterDependency> directControllingChars = ch.getControllingCharacters();
            if (directControllingChars != null) {
                for (CharacterDependency cd : directControllingChars) {
                    Character controllingChar = ds.getCharacter(cd.getControllingCharacterId());
                    retList.add(cd);

                    // Add all the indirect or "ancestor" character
                    // dependencies.
                    List<CharacterDependency> ancestorCharacterDependencies = getFullControllingCharacterDependenciesList(controllingChar, ds);
                    for (CharacterDependency ancestorCd : ancestorCharacterDependencies) {
                        // If an "ancestor" dependency is already in the list,
                        // remove it and reinsert it at
                        // the front of the list. Need to ensure that values for
                        // the furthermost ancestors
                        // are set first, otherwise the Specimen will throw
                        // IllegalStateExceptions...
                        if (retList.contains(ancestorCd)) {
                            retList.remove(ancestorCd);
                        }

                        retList.add(0, ancestorCd);
                    }
                }
            }

            return retList;
        }

        private CharacterValue promptForCharacterValue(Frame frame, Character ch) {
            CharacterValue characterVal = null;

            if (ch instanceof MultiStateCharacter) {
                Set<Integer> stateValues = promptForMultiStateValue(frame, (MultiStateCharacter) ch);
                if (stateValues.size() > 0) {
                    characterVal = new MultiStateValue((MultiStateCharacter) ch, stateValues);
                }
            } else if (ch instanceof IntegerCharacter) {
                Set<Integer> intValue = promptForIntegerValue(frame, (IntegerCharacter) ch);
                if (intValue != null) {
                    characterVal = new IntegerValue((IntegerCharacter) ch, intValue);
                }
            } else if (ch instanceof RealCharacter) {
                FloatRange floatRange = promptForRealValue(frame, (RealCharacter) ch);
                if (floatRange != null) {
                    characterVal = new RealValue((RealCharacter) ch, floatRange);
                }
            } else if (ch instanceof TextCharacter) {
                List<String> stringList = promptForTextValue(frame, (TextCharacter) ch);
                if (stringList.size() > 0) {
                    characterVal = new TextValue((TextCharacter) ch, stringList);
                }
            } else {
                throw new IllegalArgumentException("Unrecognized character type");
            }

            return characterVal;
        }

        private Set<Integer> promptForMultiStateValue(Frame frame, MultiStateCharacter ch) {
            MultiStateInputDialog dlg = new MultiStateInputDialog(frame, ch);
            UIUtils.showDialog(dlg);
            return dlg.getInputData();
        }

        private Set<Integer> promptForIntegerValue(Frame frame, IntegerCharacter ch) {
            IntegerInputDialog dlg = new IntegerInputDialog(frame, ch);
            UIUtils.showDialog(dlg);
            return dlg.getInputData();
        }

        private FloatRange promptForRealValue(Frame frame, RealCharacter ch) {
            RealInputDialog dlg = new RealInputDialog(frame, ch);
            UIUtils.showDialog(dlg);
            return dlg.getInputData();
        }

        private List<String> promptForTextValue(Frame frame, TextCharacter ch) {
            TextInputDialog dlg = new TextInputDialog(frame, ch);
            UIUtils.showDialog(dlg);
            return dlg.getInputData();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (_change) {
                builder.append(StringUtils.join(new ChangeDirective().getControlWords(), "").toUpperCase());
            } else {
                builder.append(StringUtils.join(_controlWords, " ").toUpperCase());
            }
            builder.append(" ");
            for (Character ch : _characterValues.keySet()) {
                CharacterValue val = _characterValues.get(ch);
                builder.append(" ");
                builder.append(ch.getCharacterId());
                builder.append(",");
                builder.append(val.toShortString());
            }
            return builder.toString();
        }
    }
}

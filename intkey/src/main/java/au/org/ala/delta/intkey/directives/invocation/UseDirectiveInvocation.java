package au.org.ala.delta.intkey.directives.invocation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.intkey.directives.DirectivePopulator;
import au.org.ala.delta.intkey.model.IntkeyCharacterOrder;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.SpecimenValue;
import au.org.ala.delta.intkey.model.specimen.IntegerSpecimenValue;
import au.org.ala.delta.intkey.model.specimen.MultiStateSpecimenValue;
import au.org.ala.delta.intkey.model.specimen.RealSpecimenValue;
import au.org.ala.delta.intkey.model.specimen.TextSpecimenValue;
import au.org.ala.delta.intkey.ui.CharacterSelectionDialog;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;

public class UseDirectiveInvocation extends IntkeyDirectiveInvocation {

    private Map<au.org.ala.delta.model.Character, SpecimenValue> _characterValues;
    private boolean _change;
    private boolean _suppressAlreadySetWarning;
    private CharacterFormatter _charFormatter;
    private ItemFormatter _taxonFormatter;

    public UseDirectiveInvocation(boolean change, boolean suppressAlreadySetWarning) {
        _change = change;
        _suppressAlreadySetWarning = suppressAlreadySetWarning;

        // Use LinkedHashMap so that keys can be iterated over in the order
        // that they
        // were inserted.
        _characterValues = new LinkedHashMap<au.org.ala.delta.model.Character, SpecimenValue>();

        _charFormatter = new CharacterFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REPLACE, true, false);
        _taxonFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, false);
    }

    @Override
    public boolean execute(IntkeyContext context) {

        // Split up characters that have had their values specified and
        // those that
        // haven't. They need to be processed differently.
        List<au.org.ala.delta.model.Character> charsWithValues = new ArrayList<au.org.ala.delta.model.Character>();
        List<au.org.ala.delta.model.Character> charsNoValues = new ArrayList<au.org.ala.delta.model.Character>();

        for (au.org.ala.delta.model.Character ch : _characterValues.keySet()) {
            if (_characterValues.get(ch) == null) {
                charsNoValues.add(ch);
            } else {
                charsWithValues.add(ch);
            }
        }

        // Process characters with values specified first
        for (au.org.ala.delta.model.Character ch : charsWithValues) {
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
                    SpecimenValue characterVal = _characterValues.get(ch);
                    setValueForCharacter(ch, characterVal, context);
                }
            }
        }

        if (charsNoValues.size() == 1) {
            au.org.ala.delta.model.Character ch = charsNoValues.get(0);
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
                    SpecimenValue characterVal = promptForCharacterValue(ch, context.getDirectivePopulator());
                    if (characterVal != null) {
                        // store this value so that the prompt does not need
                        // to
                        // be done for subsequent invocations
                        _characterValues.put(ch, characterVal);
                        setValueForCharacter(ch, characterVal, context);
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
                // String directiveName = _change ? directiveName =
                // StringUtils.join(new ChangeDirective().getControlWords(),
                // " ").toUpperCase() : StringUtils.join(_controlWords,
                // " ").toUpperCase();
                String directiveName = "USE";

                CharacterSelectionDialog selectDlg = new CharacterSelectionDialog(UIUtils.getMainFrame(), charsNoValues, directiveName, context.getImageSettings(), context.displayNumbering());
                selectDlg.setVisible(true);

                List<au.org.ala.delta.model.Character> selectedCharacters = selectDlg.getSelectedCharacters();

                if (selectedCharacters.isEmpty()) {
                    // User hit cancel or did not select any characters.
                    // Abort
                    // execution when this happens.
                    // Directive should not be stored in
                    // Execution history
                    return false;
                }

                for (au.org.ala.delta.model.Character ch : selectedCharacters) {

                    SpecimenValue characterVal = null;

                    if (checkCharacterUsable(ch, context, !_suppressAlreadySetWarning && !_change)) {

                        // halt execution if values not sucessfully set for
                        // all
                        // controlling characters
                        if (!processControllingCharacters(ch, context)) {
                            return false;
                        }

                        // As the character's controlling characters have
                        // been successfully
                        // set, remove all of them from the list of
                        // characters needing to have
                        // their values set.
                        IntkeyDataset dataset = context.getDataset();
                        Map<MultiStateCharacter, Set<Integer>> charDeps = getAllControllingCharacterDependencies(ch, dataset);
                        for (MultiStateCharacter controllingChar : charDeps.keySet()) {
                            charsNoValues.remove(controllingChar);
                        }

                        // second call to checkCharacterUsable() to ensure
                        // that character has not been
                        // made inapplicable by the value given to one or
                        // more of its controlling
                        // characters
                        if (checkCharacterUsable(ch, context, false)) {
                            characterVal = promptForCharacterValue(ch, context.getDirectivePopulator());
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
                            setValueForCharacter(ch, characterVal, context);

                            charsNoValues.remove(ch);
                        }
                    }
                }
            }
        }

        context.specimenUpdateComplete();
        return true;
    }

    public void addCharacterValue(au.org.ala.delta.model.Character ch, SpecimenValue val) {
        _characterValues.put(ch, val);
    }

    private void setValueForCharacter(au.org.ala.delta.model.Character ch, SpecimenValue val, IntkeyContext context) {
        context.setValueForCharacter(ch, val);

        // If using the SEPARATE character order, give the user an opportunity
        // to change the value if it results in
        // the elimination of the taxon being separated.
        if (context.getCharacterOrder() == IntkeyCharacterOrder.SEPARATE) {
            Item taxonToSeparate = context.getDataset().getItem(context.getTaxonToSeparate());
            if (!context.getAvailableTaxa().contains(taxonToSeparate)) {
                boolean changeValue = context.getDirectivePopulator().promptForYesNoOption(
                        UIUtils.getResourceString("UseDirective.TaxonToSeparateEliminatedMsg", _charFormatter.formatCharacterDescription(ch), _taxonFormatter.formatItemDescription(taxonToSeparate)));

                if (changeValue) {
                    SpecimenValue newVal = promptForCharacterValue(ch, context.getDirectivePopulator());
                    _characterValues.put(ch, newVal);
                    setValueForCharacter(ch, newVal, context);
                }

            }
        }
    }

    private boolean checkCharacterUsable(au.org.ala.delta.model.Character ch, IntkeyContext context, boolean warnAlreadySet) {
        CharacterFormatter formatter = new CharacterFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REPLACE, true, false);

        // is character fixed?
        if (context.charactersFixed()) {
            if (context.getFixedCharactersList().contains(ch.getCharacterId())) {
                if (!context.isProcessingDirectivesFile()) {
                    String msg = UIUtils.getResourceString("UseDirective.CharacterFixed", formatter.formatCharacterDescription(ch));
                    String title = UIUtils.getResourceString("Intkey.informationDlgTitle");
                    JOptionPane.showMessageDialog(UIUtils.getMainFrame(), msg, title, JOptionPane.ERROR_MESSAGE);
                }
                return false;
            }
        }

        // is character already used?
        if (warnAlreadySet) {
            if (context.getSpecimen().hasValueFor(ch)) {
                if (context.isProcessingDirectivesFile()) {
                    return true;
                } else {
                    String msg = UIUtils.getResourceString("UseDirective.CharacterAlreadyUsed", formatter.formatCharacterDescription(ch));
                    String title = UIUtils.getResourceString("Intkey.informationDlgTitle");
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
            if (!context.isProcessingDirectivesFile()) {
                String msg = UIUtils.getResourceString("UseDirective.CharacterUnavailable", formatter.formatCharacterDescription(ch));
                String title = UIUtils.getResourceString("Intkey.informationDlgTitle", formatter.formatCharacterDescription(ch));
                JOptionPane.showMessageDialog(UIUtils.getMainFrame(), msg, title, JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }

        // is character excluded?
        if (!context.getIncludedCharacters().contains(ch)) {
            if (!context.isProcessingDirectivesFile()) {
                String msg = UIUtils.getResourceString("UseDirective.CharacterExcluded", formatter.formatCharacterDescription(ch));
                String title = UIUtils.getResourceString("Intkey.informationDlgTitle", formatter.formatCharacterDescription(ch));
                JOptionPane.showMessageDialog(UIUtils.getMainFrame(), msg, title, JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }

        return true;
    }

    /**
     * Set values for all controlling characters for the specified character,
     * prompting if necessary
     * 
     * @param ch
     * @param context
     * @return true if values were set successfully for all controlling
     *         characters
     */
    private boolean processControllingCharacters(au.org.ala.delta.model.Character ch, IntkeyContext context) {

        // Map of controlling characters to states of these controlling
        // characters that will cause this character to be inapplicable
        Map<MultiStateCharacter, Set<Integer>> controllingCharInapplicableStates = getAllControllingCharacterDependencies(ch, context.getDataset());

        // Sort the controlling characters such that any character in the list
        // is after all characters in the list that control it.
        List<MultiStateCharacter> controllingCharsList = new ArrayList<MultiStateCharacter>(controllingCharInapplicableStates.keySet());
        Collections.sort(controllingCharsList, new ControllingCharacterComparator());

        // For each controlling character, set its value or prompt the user
        // for its value as appropriate
        for (MultiStateCharacter cc : controllingCharsList) {
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
                throw new RuntimeException(MessageFormat.format("There are no states for character %s that will make character {0} applicable", cc.getCharacterId(), ch.getCharacterId()));
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
            if (!context.isProcessingDirectivesFile() && (cc.getNonAutoCc() || ch.getUseCc() || !cc.getNonAutoCc() && !cc.getUseCc() && applicableStates.size() > 1)) {
                SpecimenValue val = promptForCharacterValue(cc, context.getDirectivePopulator());
                if (val != null) {
                    context.setValueForCharacter(cc, val);
                } else {
                    // No values selected or cancel pressed. Return as
                    // values have not been set for all
                    // controlling characters
                    return false;
                }
            } else {
                // let intkey automatically use the character
                MultiStateSpecimenValue val = new MultiStateSpecimenValue((MultiStateCharacter) cc, new HashSet<Integer>(applicableStates));
                context.setValueForCharacter(cc, val);
            }

            // TODO output USEd controlling characters directly to the log
            // window
            // set the "used type" - by user or auto for the controlling
            // character

        }

        return true;
    }

    /**
     * For the given character, build a map of all its controlling characters,
     * and the states of these controlling characters that will make the
     * supplied character inapplicable.
     * 
     * @param ch
     * @param ds
     * @return
     */
    private Map<MultiStateCharacter, Set<Integer>> getAllControllingCharacterDependencies(au.org.ala.delta.model.Character ch, IntkeyDataset ds) {
        HashMap<MultiStateCharacter, Set<Integer>> retMap = new HashMap<MultiStateCharacter, Set<Integer>>();

        List<CharacterDependency> controllingDependencies = ch.getControllingCharacters();

        for (CharacterDependency cd : controllingDependencies) {

            // A controlling character must be a multistate character
            MultiStateCharacter cc = (MultiStateCharacter) ds.getCharacter(cd.getControllingCharacterId());

            // states for the controlling character that will make dependent
            // characters inapplicable
            Set<Integer> inapplicableStates = cd.getStates();

            if (retMap.containsKey(cc)) {
                retMap.get(cc).addAll(inapplicableStates);
            } else {
                retMap.put(cc, new HashSet<Integer>(inapplicableStates));
            }

            retMap.putAll(getAllControllingCharacterDependencies(cc, ds));
        }

        return retMap;
    }

    private SpecimenValue promptForCharacterValue(au.org.ala.delta.model.Character ch, DirectivePopulator populator) {
        SpecimenValue characterVal = null;

        if (ch instanceof MultiStateCharacter) {
            Set<Integer> stateValues = populator.promptForMultiStateValue((MultiStateCharacter) ch);
            if (stateValues != null && stateValues.size() > 0) {
                characterVal = new MultiStateSpecimenValue((MultiStateCharacter) ch, stateValues);
            }
        } else if (ch instanceof IntegerCharacter) {
            Set<Integer> intValue = populator.promptForIntegerValue((IntegerCharacter) ch);
            if (intValue != null && intValue.size() > 0) {
                characterVal = new IntegerSpecimenValue((IntegerCharacter) ch, intValue);
            }
        } else if (ch instanceof RealCharacter) {
            FloatRange floatRange = populator.promptForRealValue((RealCharacter) ch);
            if (floatRange != null) {
                characterVal = new RealSpecimenValue((RealCharacter) ch, floatRange);
            }
        } else if (ch instanceof TextCharacter) {
            List<String> stringList = populator.promptForTextValue((TextCharacter) ch);
            if (stringList != null && stringList.size() > 0) {
                characterVal = new TextSpecimenValue((TextCharacter) ch, stringList);
            }
        } else {
            throw new IllegalArgumentException("Unrecognized character type");
        }

        return characterVal;
    }

    @Override
    public String toString() {
        // StringBuilder builder = new StringBuilder();
        // if (_change) {
        // builder.append(StringUtils.join(new
        // ChangeDirective().getControlWords(), "").toUpperCase());
        // } else {
        // builder.append(StringUtils.join(_controlWords, " ").toUpperCase());
        // }
        // builder.append(" ");
        // for (Character ch : _characterValues.keySet()) {
        // CharacterValue val = _characterValues.get(ch);
        // builder.append(" ");
        // builder.append(ch.getCharacterId());
        // builder.append(",");
        // builder.append(val.toShortString());
        // }
        // return builder.toString();
        return "USE";
    }

    /**
     * Comparator used to sort a list of characters such that all of a
     * character's controlling characters will appear before it in the sorted
     * order.
     * 
     * @author ChrisF
     * 
     */
    private static class ControllingCharacterComparator implements Comparator<au.org.ala.delta.model.Character> {
        @Override
        public int compare(au.org.ala.delta.model.Character c1, au.org.ala.delta.model.Character c2) {
            if (isControlledBy(c1, c2)) {
                return 1;
            } else if (isControlledBy(c2, c2)) {
                return -1;
            } else {
                return 0;
            }
        }

        private boolean isControlledBy(au.org.ala.delta.model.Character c1, au.org.ala.delta.model.Character c2) {
            boolean result = false;

            List<CharacterDependency> controllingDependencies = c1.getControllingCharacters();

            for (CharacterDependency cd : controllingDependencies) {
                if (cd.getControllingCharacterId() == c2.getCharacterId()) {
                    result = true;
                    break;
                }
            }

            return result;
        }
    }
}

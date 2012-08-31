/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey.directives.invocation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.intkey.directives.DirectivePopulator;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.intkey.model.IntkeyCharacterOrder;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.ui.CharacterSelectionDialog;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.AttributeFactory;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealAttribute;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.TextAttribute;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.impl.SimpleAttributeData;
import au.org.ala.delta.util.Utils;

public class UseDirectiveInvocation extends BasicIntkeyDirectiveInvocation {

    private static final String USE_DIRECTIVE_NAME = "USE";
    private static final String CHANGE_DIRECTIVE_NAME = "CHANGE";

    private Map<au.org.ala.delta.model.Character, Attribute> _characterAttributes;
    private boolean _change;
    private boolean _suppressAlreadySetWarning;
    private CharacterFormatter _charFormatter;
    private ItemFormatter _taxonFormatter;

    private StringBuilder _stringRepresentationBuilder;
    private List<String> _stringRepresentationParts;

    // True if any updates were made to the specimen.
    private boolean _specimenUpdated = false;

    public UseDirectiveInvocation(boolean change, boolean suppressAlreadySetWarning, List<String> stringRepresentationParts) {
        _change = change;
        _suppressAlreadySetWarning = suppressAlreadySetWarning;

        // Use LinkedHashMap so that keys can be iterated over in the order
        // that they
        // were inserted.
        _characterAttributes = new LinkedHashMap<au.org.ala.delta.model.Character, Attribute>();

        _charFormatter = new CharacterFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REPLACE, true, false);
        _taxonFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, false);

        _stringRepresentationParts = stringRepresentationParts;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        _stringRepresentationBuilder = new StringBuilder();

        String directiveName = _change ? CHANGE_DIRECTIVE_NAME : USE_DIRECTIVE_NAME;

        _stringRepresentationBuilder.append(directiveName);
        _stringRepresentationBuilder.append(" ");

        if (_suppressAlreadySetWarning) {
            _stringRepresentationBuilder.append(UseDirective.SUPPRESS_ALREADY_SET_WARNING_FLAG + " ");
        }

        // Split up characters that have had their values specified and
        // those that
        // haven't. They need to be processed differently.
        List<au.org.ala.delta.model.Character> charsWithValuesSpecified = new ArrayList<au.org.ala.delta.model.Character>();
        List<au.org.ala.delta.model.Character> charsNoValuesSpecified = new ArrayList<au.org.ala.delta.model.Character>();

        for (au.org.ala.delta.model.Character ch : _characterAttributes.keySet()) {
            if (_characterAttributes.get(ch) == null) {
                charsNoValuesSpecified.add(ch);
            } else {
                charsWithValuesSpecified.add(ch);
            }
        }

        // Process characters with values specified first
        for (au.org.ala.delta.model.Character ch : charsWithValuesSpecified) {
            if (checkCharacterUsable(ch, context, !_suppressAlreadySetWarning && !_change)) {
                // Not interested in what controlling characters were
                // set/manually set here. Only need this when prompting for
                // character values (as the user can hit cancel, or hit OK with
                // no values input)
                if (!processControllingCharacters(ch, context, new ArrayList<Character>(), new ArrayList<Character>())) {
                    // halt execution if values not sucessfully set for all
                    // controlling characters
                    if (_specimenUpdated) {
                        context.specimenUpdateComplete();
                    }
                    return false;
                }

                // second call to checkCharacterUsable() to ensure that
                // character has not been
                // made inapplicable by the value given to one or more of
                // its controlling
                // characters
                if (checkCharacterUsable(ch, context, false)) {
                    Attribute attr = _characterAttributes.get(ch);
                    setValueForCharacter(ch, attr, context);
                } else {
                    if (_specimenUpdated) {
                        context.specimenUpdateComplete();
                    }
                    return false;
                }
            } else {
                if (_specimenUpdated) {
                    context.specimenUpdateComplete();
                }
                return false;
            }
        }

        if (charsNoValuesSpecified.size() == 1) {
            au.org.ala.delta.model.Character ch = charsNoValuesSpecified.get(0);
            if (checkCharacterUsable(ch, context, !_suppressAlreadySetWarning && !_change)) {

                List<Character> setControllingCharacters = new ArrayList<Character>();
                List<Character> manuallySetControllingCharacters = new ArrayList<Character>();
                if (!processControllingCharacters(ch, context, setControllingCharacters, manuallySetControllingCharacters)) {
                    // halt execution if values not sucessfully set for all
                    // controlling characters
                    if (_specimenUpdated) {
                        context.specimenUpdateComplete();
                    }
                    return false;
                }

                // second call to checkCharacterUsable() to ensure that
                // character has not been
                // made inapplicable by the value given to one or more of
                // its controlling
                // characters
                if (checkCharacterUsable(ch, context, false)) {
                    Attribute attr = promptForCharacterValue(ch, null, null, context.getDirectivePopulator(), context.getSpecimen(), context);
                    if (attr != null) {
                        // store this value so that the prompt does not need
                        // to
                        // be done for subsequent invocations
                        _characterAttributes.put(ch, attr);
                        setValueForCharacter(ch, attr, context);
                    } else {
                        // User hit cancel or did not enter a value when
                        // prompted.
                        // Abort execution when this happens
                        cleanupSpecimenAfterCancel(setControllingCharacters, manuallySetControllingCharacters, context.getSpecimen());
                        if (_specimenUpdated) {
                            context.specimenUpdateComplete();
                        }
                        return false;
                    }
                } else {
                    cleanupSpecimenAfterCancel(setControllingCharacters, manuallySetControllingCharacters, context.getSpecimen());
                    if (_specimenUpdated) {
                        context.specimenUpdateComplete();
                    }
                    return false;
                }
            } else {
                if (_specimenUpdated) {
                    context.specimenUpdateComplete();
                }
                return false;
            }
        } else {
            List<Character> charsNoValuesCopy = new ArrayList<Character>(charsNoValuesSpecified);
            Collections.sort(charsNoValuesCopy);
            while (!charsNoValuesCopy.isEmpty()) {
                CharacterSelectionDialog selectDlg = new CharacterSelectionDialog(UIUtils.getMainFrame(), charsNoValuesCopy, directiveName, context.getImageSettings(), context.displayNumbering(),
                        context);
                selectDlg.setVisible(true);

                List<au.org.ala.delta.model.Character> selectedCharacters = selectDlg.getSelectedCharacters();

                if (selectedCharacters.isEmpty()) {
                    // User hit cancel or did not select any characters.
                    // Abort
                    // execution when this happens.
                    // Directive should not be stored in
                    // Execution history
                    if (_specimenUpdated) {
                        context.specimenUpdateComplete();
                    }
                    return false;
                }

                for (au.org.ala.delta.model.Character ch : selectedCharacters) {

                    Attribute attr = null;

                    if (checkCharacterUsable(ch, context, !_suppressAlreadySetWarning && !_change)) {

                        List<Character> setControllingCharacters = new ArrayList<Character>();
                        List<Character> manuallySetControllingCharacters = new ArrayList<Character>();
                        if (!processControllingCharacters(ch, context, setControllingCharacters, manuallySetControllingCharacters)) {
                            // halt execution if values not sucessfully set for
                            // all
                            // controlling characters
                            if (_specimenUpdated) {
                                context.specimenUpdateComplete();
                            }
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
                            charsNoValuesCopy.remove(controllingChar);
                        }

                        // second call to checkCharacterUsable() to ensure
                        // that character has not been
                        // made inapplicable by the value given to one or
                        // more of its controlling
                        // characters
                        if (checkCharacterUsable(ch, context, false)) {
                            attr = promptForCharacterValue(ch, null, null, context.getDirectivePopulator(), context.getSpecimen(), context);
                        } else {
                            cleanupSpecimenAfterCancel(setControllingCharacters, manuallySetControllingCharacters, context.getSpecimen());
                            if (_specimenUpdated) {
                                context.specimenUpdateComplete();
                            }
                            return false;
                        }

                        if (attr != null) {
                            // store this value so that the prompt does not
                            // need
                            // to
                            // be done for subsequent invocations
                            _characterAttributes.put(ch, attr);
                            setValueForCharacter(ch, attr, context);

                            charsNoValuesCopy.remove(ch);
                        } else {
                            cleanupSpecimenAfterCancel(setControllingCharacters, manuallySetControllingCharacters, context.getSpecimen());
                            if (_specimenUpdated) {
                                context.specimenUpdateComplete();
                            }
                        }
                    } else {
                        if (_specimenUpdated) {
                            context.specimenUpdateComplete();
                        }
                        return false;
                    }
                }
            }
        }

        // Finish building string representation of directive call
        for (Character ch : charsNoValuesSpecified) {
            // Generate a string representation of the value set for the
            // character

            Attribute attr = _characterAttributes.get(ch);
            int strRepIndex = _stringRepresentationParts.indexOf(Integer.toString(ch.getCharacterId()));
            _stringRepresentationParts.remove(strRepIndex);
            String strRepresentation = Integer.toString(ch.getCharacterId()) + "," + formatAttributeValueForLog(attr);
            _stringRepresentationParts.add(strRepIndex, strRepresentation);
        }

        _stringRepresentationBuilder.append(StringUtils.join(_stringRepresentationParts, " "));

        context.appendToLog("*" + _stringRepresentationBuilder.toString());
        if (_specimenUpdated) {
            context.specimenUpdateComplete();
        }
        return true;
    }

    private String formatAttributeValueForLog(Attribute attr) {
        if (attr instanceof TextAttribute) {
            return ((TextAttribute) attr).getText();
        } else if (attr instanceof MultiStateAttribute) {
            return Utils.formatIntegersAsListOfRanges(((MultiStateAttribute) attr).getPresentStatesAsList(), "/", "-");
        } else if (attr instanceof IntegerAttribute) {
            Set<Integer> presentValues = ((IntegerAttribute) attr).getPresentValues();
            List<Integer> presentValuesList = new ArrayList<Integer>(presentValues);
            Collections.sort(presentValuesList);
            return Utils.formatIntegersAsListOfRanges(presentValuesList, "/", "-");
        } else if (attr instanceof RealAttribute) {
            FloatRange range = ((RealAttribute) attr).getPresentRange();
            StringBuilder builder = new StringBuilder();
            float min = range.getMinimumFloat();
            float max = range.getMaximumFloat();

            if (min == max) {
                builder.append(min);
            } else {
                builder.append(min);
                builder.append("-");
                builder.append(max);
            }
            return builder.toString();
        } else {
            throw new IllegalArgumentException("Unrecognised attribute type!");
        }
    }

    public void addCharacterValue(au.org.ala.delta.model.Character ch, Attribute attr) {
        _characterAttributes.put(ch, attr);
    }

    private void setValueForCharacter(au.org.ala.delta.model.Character ch, Attribute attr, IntkeyContext context) {
        context.setSpecimenAttributeForCharacter(ch, attr);

        // If using the SEPARATE character order, give the user an opportunity
        // to change the value if it results in
        // the elimination of the taxon being separated.
        if (context.getCharacterOrder() == IntkeyCharacterOrder.SEPARATE) {
            Item taxonToSeparate = context.getDataset().getItem(context.getTaxonToSeparate());
            if (!context.getAvailableTaxa().contains(taxonToSeparate)) {
                boolean changeValue = context.getDirectivePopulator().promptForYesNoOption(
                        UIUtils.getResourceString("UseDirective.TaxonToSeparateEliminatedMsg", _charFormatter.formatCharacterDescription(ch), _taxonFormatter.formatItemDescription(taxonToSeparate)));

                if (changeValue) {
                    Attribute newAttr = promptForCharacterValue(ch, null, null, context.getDirectivePopulator(), context.getSpecimen(), context);
                    if (newAttr != null) {
                        _characterAttributes.put(ch, newAttr);
                        setValueForCharacter(ch, newAttr, context);
                    }
                }

            }
        }

        _specimenUpdated = true;
    }

    private boolean checkCharacterUsable(au.org.ala.delta.model.Character ch, IntkeyContext context, boolean warnAlreadySetOrMaybeInapplicable) {
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
        if (warnAlreadySetOrMaybeInapplicable) {
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

        // is character maybe inapplicable?
        if (warnAlreadySetOrMaybeInapplicable) {
            if (context.getSpecimen().isCharacterMaybeInapplicable(ch)) {
                if (context.isProcessingDirectivesFile()) {
                    return true;
                } else {
                    String msg = UIUtils.getResourceString("UseDirective.CharacterMaybeInapplicable", formatter.formatCharacterDescription(ch));
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
     * @param controllingCharAttributes
     *            - should be an empty list. Will be populated with the
     *            attribute values set in the specimen for the controlling
     *            characters.
     * @return true if values were set successfully for all controlling
     *         characters
     */
    private boolean processControllingCharacters(au.org.ala.delta.model.Character ch, IntkeyContext context, List<Character> setControllingCharacters, List<Character> manuallySetControllingCharacters) {

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
            // If the controlling character already has a value set for it in
            // the specimen, do not modify it.
            if (context.getSpecimen().hasValueFor(cc)) {
                continue;
            }

            // Need to check that the character is still applicable, as the
            // value set by the user for one of
            // the controlling characters may have made it inapplicable
            if (!checkCharacterUsable(ch, context, false)) {
                return false;
            }

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
            // applicable.
            if (!context.isProcessingDirectivesFile() && (cc.getNonAutoCc() || ch.getUseCc() || !cc.getNonAutoCc() && !cc.getUseCc() && applicableStates.size() > 1)) {
                Attribute attr = promptForCharacterValue(cc, ch, applicableStates, context.getDirectivePopulator(), context.getSpecimen(), context);
                if (attr != null) {
                    context.setSpecimenAttributeForCharacter(cc, attr);
                    _specimenUpdated = true;
                    setControllingCharacters.add(cc);
                    manuallySetControllingCharacters.add(cc);
                } else {
                    // No values selected or cancel pressed. Return as
                    // values have not been set for all
                    // controlling characters
                    return false;
                }
            } else {
                // let intkey automatically use the character
                SimpleAttributeData impl = new SimpleAttributeData(false, false);
                impl.setPresentStateOrIntegerValues(applicableStates);
                Attribute attr = AttributeFactory.newAttribute(cc, impl);
                attr.setSpecimenAttribute(true);
                context.setSpecimenAttributeForCharacter(cc, attr);
                _specimenUpdated = true;
                setControllingCharacters.add(cc);
            }

            // output USEd controlling characters directly to the log
            // window

            context.appendToLog(MessageFormat.format("    *USE {0},{1}", cc.getCharacterId(), StringUtils.join(applicableStates, "/")));
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

    private Attribute promptForCharacterValue(au.org.ala.delta.model.Character ch, au.org.ala.delta.model.Character dependentChar, Set<Integer> applicableStates, DirectivePopulator populator,
            Specimen specimen, IntkeyContext context) {
        SimpleAttributeData impl = new SimpleAttributeData(false, false);

        if (ch instanceof MultiStateCharacter) {
            Set<Integer> stateValues;
            if (dependentChar != null) {
                // We are setting a value for a controlling character. Ensure
                // that the states that make the dependent character applicable
                // are pre-selected.
                stateValues = populator.promptForMultiStateValue((MultiStateCharacter) ch, applicableStates, dependentChar);
            } else {
                // Get the current value of the character as set in the
                // specimen. If no
                // value is set for the character, an attribute with
                // unknown = true is returned.
                MultiStateAttribute currentAttribute = (MultiStateAttribute) specimen.getAttributeForCharacter(ch);
                stateValues = populator.promptForMultiStateValue((MultiStateCharacter) ch, currentAttribute.isUnknown() ? null : currentAttribute.getPresentStates(), null);
            }
            if (stateValues != null && stateValues.isEmpty() && dependentChar == null) {
                // User hit OK but did not select any states. Delete any value
                // set
                // for the character.
                if (specimen.hasValueFor(ch)) {
                    deleteCharacter(ch, context);
                }
                return null;
            } else if (stateValues != null && stateValues.size() > 0) {
                impl.setPresentStateOrIntegerValues(stateValues);
            } else {
                return null;
            }
        } else if (ch instanceof IntegerCharacter) {
            // Get the current value of the character as set in the specimen. If
            // no
            // value is set for the character, an attribute with
            // unknown = true is returned.
            IntegerAttribute currentAttribute = (IntegerAttribute) specimen.getAttributeForCharacter(ch);

            Set<Integer> intValue = populator.promptForIntegerValue((IntegerCharacter) ch, currentAttribute.isUnknown() ? null : currentAttribute.getPresentValues());

            if (intValue != null && intValue.isEmpty()) {
                // User hit OK but did not input any values. Delete any value
                // set
                // for the character
                if (specimen.hasValueFor(ch)) {
                    deleteCharacter(ch, context);
                }
                return null;
            } else if (intValue != null && intValue.size() > 0) {
                impl.setPresentStateOrIntegerValues(intValue);
            } else {
                return null;
            }
        } else if (ch instanceof RealCharacter) {
            // Get the current value of the character as set in the specimen. If
            // no
            // value is set for the character, an attribute with
            // unknown = true is returned.
            RealAttribute currentAttribute = (RealAttribute) specimen.getAttributeForCharacter(ch);

            FloatRange floatRange = populator.promptForRealValue((RealCharacter) ch, currentAttribute.isUnknown() ? null : currentAttribute.getPresentRange());

            if (floatRange != null) {
                // Float range with minimum and maximum value as negative
                // infinity indicates that the user hit OK without inputting
                // values.
                if (floatRange.getMaximumFloat() == Float.NEGATIVE_INFINITY && floatRange.getMinimumFloat() == Float.NEGATIVE_INFINITY) {
                    // User hit OK but did not input any values. Delete any
                    // value set
                    // for the character
                    if (specimen.hasValueFor(ch)) {
                        deleteCharacter(ch, context);
                    }
                    return null;
                }
                impl.setRealRange(floatRange);
            } else {
                return null;
            }
        } else if (ch instanceof TextCharacter) {
            // Get the current value of the character as set in the specimen. If
            // no
            // value is set for the character, an attribute with
            // unknown = true is returned.
            TextAttribute currentAttribute = (TextAttribute) specimen.getAttributeForCharacter(ch);

            List<String> stringList = populator.promptForTextValue((TextCharacter) ch, currentAttribute.isUnknown() ? null : Arrays.asList(currentAttribute.getValueAsString().split("/")));

            if (stringList != null && stringList.isEmpty()) {
                // User hit OK but did not input any values. Delete any
                // value set
                // for the character
                if (specimen.hasValueFor(ch)) {
                    deleteCharacter(ch, context);
                }
                return null;
            } else if (stringList != null && stringList.size() > 0) {
                impl.setValueFromString(StringUtils.join(stringList, '/'));
            } else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("Unrecognized character type");
        }

        Attribute attr = AttributeFactory.newAttribute(ch, impl);
        attr.setSpecimenAttribute(true);

        return attr;
    }

    private void deleteCharacter(Character ch, IntkeyContext context) {
        List<Character> charInList = new ArrayList<Character>();
        charInList.add(ch);
        DeleteDirectiveInvocation deleteInvoc = new DeleteDirectiveInvocation();
        deleteInvoc.setCharacters(charInList);
        deleteInvoc.setStringRepresentation("DELETE " + ch.getCharacterId());

        context.executeDirective(deleteInvoc);
    }

    /**
     * Cleans up the specimen after the prompt for a character value is
     * cancelled, or returns successfully but with no values specified. The
     * values for any automatically-set controlling characters are removed from
     * the specimen, unless they are higher in the dependency hierarchy than a
     * manually set controlling character.
     * 
     * @param setControllingCharacters
     *            A list of all controlling characters that were set, in the
     *            order that they were set.
     * @param manuallySetControllingCharacters
     *            A list of all manually set controlling characters, in the
     *            order that they were set
     * @param specimen
     *            the specimen.
     */
    private void cleanupSpecimenAfterCancel(List<Character> setControllingCharacters, List<Character> manuallySetControllingCharacters, Specimen specimen) {
        List<Character> charsToRemoveFromSpecimen = new ArrayList<Character>();

        if (manuallySetControllingCharacters.isEmpty()) {
            charsToRemoveFromSpecimen.addAll(setControllingCharacters);
        } else {
            Character lastManuallySetCharacter = manuallySetControllingCharacters.get(manuallySetControllingCharacters.size() - 1);
            charsToRemoveFromSpecimen.addAll(setControllingCharacters.subList(setControllingCharacters.indexOf(lastManuallySetCharacter) + 1, setControllingCharacters.size()));
        }

        for (Character ch : charsToRemoveFromSpecimen) {
            specimen.removeValueForCharacter(ch);
        }

        // If all values set for controlling characters have been removed, the
        // net change to the specimen is zero. Hence it does not need to be
        // considered updated.
        if (charsToRemoveFromSpecimen.equals(setControllingCharacters)) {
            _specimenUpdated = false;
        }
    }

    @Override
    public String toString() {
        return _stringRepresentationBuilder.toString();
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

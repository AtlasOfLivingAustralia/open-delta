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
package au.org.ala.delta.intkey.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.mutable.MutableBoolean;

import au.org.ala.delta.intkey.model.DisplayImagesReportType;
import au.org.ala.delta.intkey.model.ImageDisplayMode;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.util.Pair;

public interface DirectivePopulator {

    /**
     * Prompt the user to select characters via a list of character keywords
     * 
     * @param directiveName
     *            the name of the directive being processed
     * @param permitSelectionFromIncludedCharactersOnly
     *            if true, the prompt will include the option to select only
     *            from the currently included characters - any excluded
     *            characters will be filtered out.
     * @param noneKeywordAvailable
     *            if true, the NONE keyword, representing an empty set of
     *            characters will be available as an option
     * @param returnSelectedKeywords
     *            returns a list of selected keywords, if applicable
     * @return the list of selected characters, or null if the user cancelled
     *         the operation
     */
    List<au.org.ala.delta.model.Character> promptForCharactersByKeyword(String directiveName, boolean permitSelectionFromIncludedCharactersOnly, boolean noneKeywordAvailable,
            List<String> returnSelectedKeywords);

    /**
     * Prompt the user to select characters from a list
     * 
     * @param directiveName
     *            the name of the directive being processed
     * @param selectFromRemainingCharactersOnly
     *            if true, only the available characters (those not eliminated
     *            or excluded) will be available for selection
     * @param returnSelectedKeywords
     *            returns a list of selected keywords, if applicable
     * @return the list of selected characters, or null if the user cancelled
     *         the operation
     */
    List<au.org.ala.delta.model.Character> promptForCharactersByList(String directiveName, boolean selectFromAvailableCharactersOnly, List<String> returnSelectedKeywords);

    /**
     * 
     * @param directiveName
     *            the name of the directive being processed
     * @param permitSelectionFromIncludedTaxaOnly
     *            if true, the prompt will include the option to select only
     *            from the currently included taxa - any excluded taxa will be
     *            filtered out.
     * @param noneKeywordAvailable
     *            if true, the NONE keyword, representing an empty set of
     *            characters will be available as an option
     * @param includeSpecimenAsOption
     *            if true, the specimen will be included as an available option
     * @param returnSpecimenSelected
     *            Mutable boolean, if true after the method has returned, the
     *            specimen was chosen as one of the options. Mutable boolean
     *            used here as java does not allow pass by reference.
     * @param returnSelectedKeywords
     *            returns a list of selected keywords, if applicable
     * @return the list of selected taxa, or null if the user cancelled the
     *         operation
     */
    List<Item> promptForTaxaByKeyword(String directiveName, boolean permitSelectionFromIncludedTaxaOnly, boolean noneKeywordAvailable, boolean includeSpecimenAsOption,
            MutableBoolean returnSpecimenSelected, List<String> returnSelectedKeywords);

    /**
     * 
     * @param directiveName
     * @param selectFromRemainingTaxaOnly
     *            if true, only the remaining taxa (those not eliminated or
     *            excluded) will be available for selection
     * 
     * @param autoSelectSingleValue
     *            if true, no prompt will be shown if only a single taxon is
     *            available for selection - that taxon will be selected
     *            automatically
     * @param includeSpecimenAsOption
     *            if true, the specimen will be included as an available option
     * @param returnSpecimenSelected
     *            Mutable boolean, if true after the method has returned, the
     *            specimen was chosen as one of the options. Mutable boolean
     *            used here as java does not allow pass by reference.
     * @param if true, the user will only be permitted to select a single taxon
     * @param returnSelectedKeywords
     *            returns a list of selected keywords, if applicable
     * @return the list of selected taxa, or null if the user cancelled the
     *         operation
     */
    List<Item> promptForTaxaByList(String directiveName, boolean selectFromIncludedTaxaOnly, boolean autoSelectSingleValue, boolean singleSelect, boolean includeSpecimenAsOption,
            MutableBoolean returnSpecimenSelected, List<String> returnSelectedKeywords);

    /**
     * Prompts the user with a yes/no question
     * 
     * @param message
     *            the question to display
     * @return true if the user selected "yes". False otherwise.
     */
    Boolean promptForYesNoOption(String message);

    /**
     * Prompt the user to enter a string
     * 
     * @param message
     *            the prompt message
     * @param initialSelectionValue
     *            initial value to be shown in the prompt
     * @param directiveName
     *            the name of the directive being processed
     * @return the string entered by the user, or null if the user cancelled the
     *         operation
     */
    String promptForString(String message, String initialSelectionValue, String directiveName);

    /**
     * Prompt the user to enter value(s) for a text character
     * 
     * @param ch
     *            the text character
     * @param currentValue
     *            The current value(s) set for the text character. Supply null
     *            if no value is currently set.
     * @return A list of the supplied values for the character, or null if the
     *         user cancelled the operation
     */
    List<String> promptForTextValue(TextCharacter ch, List<String> currentValue);

    /**
     * Prompt the user to enter value(s) for an integer character
     * 
     * @param ch
     *            the integer character
     * @param currentValue
     *            The current value(s) set for the integer character. Supply
     *            null if no value is currently set.
     * @return A list of the supplied values for the character, or null if the
     *         user cancelled the operation
     */
    Set<Integer> promptForIntegerValue(IntegerCharacter ch, Set<Integer> currentValue);

    /**
     * Prompt the user to enter a value for a real character
     * 
     * @param ch
     *            the real character
     * @param currentValue
     *            The current value(s) set for the real character. Supply null
     *            if no value is currently set.
     * @return The supplied value for the character, or null if the user
     *         cancelled the operation
     */
    FloatRange promptForRealValue(RealCharacter ch, FloatRange currentValue);

    /**
     * Prompt the user to enter value(s) for a multistate character
     * 
     * @param ch
     *            the multistate character
     * @param currentSelectedStates
     *            The current state(s) set for the multistate character. Supply
     *            null if no value is currently set. In the case that the
     *            character is a controlling character that must be set before
     *            its dependent character can be set, the current selected
     *            states should be all the states which make the dependent
     *            character applicable.
     * @param dependentCharacter
     *            if ch is a controlling character whose value must be set
     *            before its dependent character, this is a reference to the
     *            dependent character. The user should be shown a message
     *            informing them that the controlling character's value must be
     *            set before the controlling character must be set.
     * @return The supplied value for the character, or null if the user
     *         cancelled the operation
     */
    Set<Integer> promptForMultiStateValue(MultiStateCharacter ch, Set<Integer> currentSelectedStates, Character dependentCharacter);

    /**
     * Prompt the user to select a file
     * 
     * @param fileExtensions
     *            permitted file extensions
     * @param description
     *            description of the file type
     * @param createFileIfNonExistant
     *            if true, file will be created if it does not exist
     * @return The selected file, or null if the user cancelled the operation
     * @throws IOException
     */
    File promptForFile(List<String> fileExtensions, String description, boolean createFileIfNonExistant) throws IOException;

    /**
     * Prompt the user to select an on/off value for an intkey option
     * 
     * @param directiveName
     *            the name of the directive being processed
     * @param initialValue
     *            the initial value of the option
     * @return true if the option is set to ON, false if it is set to OFF, null
     *         if the user cancelled the operation
     */
    Boolean promptForOnOffValue(String directiveName, boolean initialValue);

    /**
     * Prompts the user to enter match settings used with the SET MATCH
     * directive.
     * 
     * @return A list of objects with three values - 1. A boolean indicating
     *         whether or not inapplicables should be matched, 2. A boolean
     *         indicating whether or not unknowns should be matched, 3. A
     *         MatchType enum value representing the match type (subset, overlap
     *         or exact). A null list indicates that the user cancelled the
     *         operation.
     */
    List<Object> promptForMatchSettings();

    List<Object> promptForButtonDefinition();

    Pair<ImageDisplayMode, DisplayImagesReportType> promptForImageDisplaySettings();
    
    String promptForDataset();

}

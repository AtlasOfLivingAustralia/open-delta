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
package au.org.ala.delta.key;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.ConforDirectiveParserObserver;
import au.org.ala.delta.directives.DirectiveParserObserver;
import au.org.ala.delta.directives.ExcludeCharacters;
import au.org.ala.delta.directives.ExcludeItems;
import au.org.ala.delta.directives.IncludeCharacters;
import au.org.ala.delta.directives.IncludeItems;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.key.directives.KeyDirectiveParser;
import au.org.ala.delta.key.directives.io.KeyCharactersFileReader;
import au.org.ala.delta.key.directives.io.KeyItemsFileReader;
import au.org.ala.delta.key.directives.io.KeyOutputFileManager;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.AttributeFactory;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MatchType;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.impl.SimpleAttributeData;
import au.org.ala.delta.translation.FilteredCharacter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.FilteredItem;
import au.org.ala.delta.translation.IncludeExcludeDataSetFilter;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.util.Utils;

public class Key implements DirectiveParserObserver {

    private static final String DEFAULT_TYPESETTING_FILE_EXTENSION = ".rtf";
    private static final String DEFAULT_OUTPUT_FILE_EXTENSION = ".prt";

    private KeyContext _context;
    private boolean _inputFilesRead = false;

    private SimpleDateFormat _timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat _dateFormat = new SimpleDateFormat("d-MMM-yyyy");

    private ConforDirectiveParserObserver _nestedObserver;

    private CharacterFormatter _charFormatter = new CharacterFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false);
    private ItemFormatter _itemFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, false);

    /**
     * @param args
     *            specifies the name of the input file to use.
     */
    public static void main(String[] args) throws Exception {

        System.out.println(generateCreditsString());

        File f = handleArgs(args);
        if (!f.exists()) {
            Logger.log("File %s does not exist!", f.getName());
            return;
        }

        new Key(f).calculateKey(f);
    }

    // The character(s) used for newline is system-dependent, so need to get it
    // from the
    // system properties
    private static String getNewLine() {
        return System.getProperty("line.separator");
    }

    private static String generateCreditsString() {
        StringBuilder credits = new StringBuilder("KEY version 2.12 (Java)");
        credits.append(getNewLine());
        credits.append("M.J. Dallwitz and T.A. Paine");
        credits.append(getNewLine());
        credits.append("Java edition ported by the Atlas of Living Australia, 2011.");
        credits.append(getNewLine());
        credits.append("CSIRO Division of Entomology, GPO Box 1700, Canberra, ACT 2601, Australia");
        credits.append(getNewLine());
        credits.append("Phone +61 2 6246 4075. Fax +61 2 6246 4000. Email delta@ento.csiro.au");
        credits.append(getNewLine());
        return credits.toString();
    }

    private static File handleArgs(String[] args) throws Exception {
        String fileName;
        if (args.length == 0) {
            fileName = askForFileName();
        } else {
            fileName = args[0];
        }

        return new File(fileName);
    }

    private static String askForFileName() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println();
        System.out.print("Enter the full pathname of the directives file: ");
        String fileName = in.readLine();

        return fileName;
    }

    public Key(File directivesFile) {
        _context = new KeyContext(directivesFile.getParentFile());
    }

    public Key(KeyContext context) {
        _context = context;
    }

    public List<File> getOutputFiles() {
        return _context.getOutputFileSelector().getOutputFiles();
    }

    public void calculateKey(File directivesFile) {
        _nestedObserver = new ConforDirectiveParserObserver(_context);

        boolean parseSuccessful = true;
        try {
            processDirectivesFile(directivesFile, _context);
        } catch (DirectiveException ex) {
            // Error message will be output by the _nestedObserver. Simply stop
            // termination here.
            parseSuccessful = false;
        } catch (Exception ex) {
            throw new RuntimeException("Fatal error occurred while processing directives file", ex);
        }

        if (parseSuccessful) {
            readInputFiles();

            boolean displayTabularKey = _context.getDisplayTabularKey();
            boolean displayBracketedKey = _context.getDisplayBracketedKey();

            if (displayTabularKey || displayBracketedKey) { // Don't bother
                                                            // calculating the
                                                            // key if we don't
                                                            // want one!
                Specimen specimen = new Specimen(_context.getDataSet(), true, false, false, MatchType.OVERLAP);
                // List<Pair<Item, List<Attribute>>> keyList = new
                // ArrayList<Pair<Item, List<Attribute>>>();
                IdentificationKey key = new IdentificationKey();

                FilteredDataSet dataset = new FilteredDataSet(_context, new IncludeExcludeDataSetFilter(_context));

                List<Character> includedCharacters = new ArrayList<Character>();
                Iterator<FilteredCharacter> iterFilteredCharacters = dataset.filteredCharacters();
                while (iterFilteredCharacters.hasNext()) {
                    includedCharacters.add(iterFilteredCharacters.next().getCharacter());
                }

                List<Item> includedItems = new ArrayList<Item>();
                Iterator<FilteredItem> iterFilteredItems = dataset.filteredItems();
                while (iterFilteredItems.hasNext()) {
                    includedItems.add(iterFilteredItems.next().getItem());
                }

                doCalculateKey(key, dataset, includedCharacters, includedItems, specimen, null);
                System.out.println("Key generation completed");

                Map<Integer, TypeSettingMark> typesettingMarksMap = _context.getTypeSettingMarks();
                boolean typsettingMarksSpecified = !(typesettingMarksMap == null || typesettingMarksMap.isEmpty());

                // Set default name for key output file if the name was not
                // specified in the directives file
                KeyOutputFileManager outputFileManager = _context.getOutputFileManager();
                if (outputFileManager.getOutputFile() == null) {
                    try {
                        outputFileManager.setOutputFileName(FilenameUtils.getBaseName(directivesFile.getName()) + DEFAULT_OUTPUT_FILE_EXTENSION);
                    } catch (Exception ex) {
                        throw new RuntimeException("Error creating output file", ex);
                    }
                }
                generateKeyOutput(key, includedCharacters, includedItems, _context.getDisplayTabularKey(), _context.getDisplayBracketedKey() && !typsettingMarksSpecified);

                if (_context.getDisplayBracketedKey() && typsettingMarksSpecified) {
                    // Set default name for typesetting output file if the name
                    // was not
                    // specified in the directives file
                    if (outputFileManager.getTypesettingFile() == null) {
                        try {
                            outputFileManager.setTypesettingFileName(FilenameUtils.getBaseName(directivesFile.getName()) + DEFAULT_TYPESETTING_FILE_EXTENSION);
                        } catch (Exception ex) {
                            throw new RuntimeException("Error creating typesetting file", ex);
                        }
                    }
                    generateTypesettingOutput();
                }

                generateListingOutput(includedCharacters, includedItems, true);
            } else {
                generateListingOutput(null, null, false);
            }
        }

        _nestedObserver.finishedProcessing();
    }

    private void readInputFiles() {
        if (!_inputFilesRead) {
            File charactersFile = _context.getCharactersFile();
            File itemsFile = _context.getItemsFile();

            BinaryKeyFile keyCharactersFile = new BinaryKeyFile(charactersFile.getAbsolutePath(), BinFileMode.FM_READONLY);
            BinaryKeyFile keyItemsFile = new BinaryKeyFile(itemsFile.getAbsolutePath(), BinFileMode.FM_READONLY);

            KeyCharactersFileReader keyCharactersFileReader = new KeyCharactersFileReader(_context, _context.getDataSet(), keyCharactersFile);
            keyCharactersFileReader.createCharacters();

            KeyItemsFileReader keyItemsFileReader = new KeyItemsFileReader(_context, _context.getDataSet(), keyItemsFile);
            keyItemsFileReader.readAll();
            _inputFilesRead = true;

            System.out.println("Data input completed.");
        }
    }

    private void doCalculateKey(IdentificationKey key, FilteredDataSet dataset, List<Character> includedCharacters, List<Item> includedItems, Specimen specimen,
            Map<Character, List<MultiStateAttribute>> confirmatoryCharacterValues) {

        if (confirmatoryCharacterValues == null) {
            confirmatoryCharacterValues = new HashMap<Character, List<MultiStateAttribute>>();
        }

        Set<Item> specimenAvailableTaxa = getSpecimenAvailableTaxa(specimen, includedItems);
        Set<Character> specimenAvailableCharacters = getSpecimenAvailableCharacters(specimen, includedCharacters);

        if (specimenAvailableTaxa.size() == 0) {
            return;
        } else if (specimenAvailableTaxa.size() == 1 || (_context.getStopAfterColumn() != -1 && specimen.getUsedCharacters().size() == _context.getStopAfterColumn())) {
            // Add a row to the table if a taxon has been identified (only 1
            // taxon remains available)

            // if the column limit set using the STOP AFTER COLUMN directive has
            // been reached, add a row for each remaining taxon
            // with the used characters

            for (Item taxon : specimenAvailableTaxa) {
                KeyRow row = new KeyRow();
                row.setItem(taxon);

                List<Attribute> attrList = new ArrayList<Attribute>();
                for (Character ch : specimen.getUsedCharacters()) {
                    MultiStateAttribute mainCharacterValue = (MultiStateAttribute) specimen.getAttributeForCharacter(ch);
                    row.addColumnValue(mainCharacterValue, confirmatoryCharacterValues.get(ch));

                    attrList.add(specimen.getAttributeForCharacter(ch));
                }

                key.addRow(row);
            }
        } else {
            // These won't be in order but that doesn't matter - best orders
            // stuff itself
            List<Integer> specimenAvailableCharacterNumbers = new ArrayList<Integer>();
            for (Character ch : specimenAvailableCharacters) {
                specimenAvailableCharacterNumbers.add(ch.getCharacterId());
            }

            List<Integer> specimenAvailableTaxaNumbers = new ArrayList<Integer>();
            for (Item item : specimenAvailableTaxa) {
                specimenAvailableTaxaNumbers.add(item.getItemNumber());
            }

            MultiStateCharacter bestCharacter;

            // Find preset character for this column/group, if there is one
            int currentColumn = specimen.getUsedCharacters().size() + 1;
            int currentGroup = getGroupCountForColumn(currentColumn - 1, key) + 1;

            int presetCharacterNumber = _context.getPresetCharacter(currentColumn, currentGroup);

            LinkedHashMap<Character, Double> bestMap = null;

            // -1 indicates no preset character for the column/group
            if (presetCharacterNumber > 0) {
                Character presetCharacter = _context.getDataSet().getCharacter(presetCharacterNumber);
                if (checkPresetCharacter(presetCharacter, specimen, includedItems)) {
                    System.out.println(MessageFormat.format("Using preset character {0},{1}:{2}", presetCharacterNumber, currentColumn, currentGroup));
                    bestCharacter = (MultiStateCharacter) presetCharacter;
                } else {
                    throw new RuntimeException(MessageFormat.format("Character {0} is not suitable for use at column {1} group {2}", presetCharacterNumber, currentColumn, currentGroup));
                }
            } else {
                bestMap = KeyBest.orderBest(_context, specimenAvailableCharacterNumbers, specimenAvailableTaxaNumbers, _context.getRBase(), _context.getABase(), _context.getReuse(), _context.getVaryWt());
//                for (Character ch: specimen.getUsedCharacters()) {
//                    System.out.println(specimen.getAttributeForCharacter(ch));
//                }
//                System.out.println("------");
//                System.out.println(bestMap);
//                System.out.println("#####");
                List<Character> bestOrderCharacters = new ArrayList<Character>(bestMap.keySet());
                if (bestOrderCharacters.isEmpty()) {
                    return;
                } else {
                    // KEY only uses multi state characters
                    bestCharacter = (MultiStateCharacter) bestOrderCharacters.get(0);
                }
            }

            List<ConfirmatoryCharacter> confirmatoryCharacters = null;
            int numberOfConfirmatoryCharacters = _context.getNumberOfConfirmatoryCharacters();
            if (numberOfConfirmatoryCharacters > 0) {
                // generated best characters if this has not already been done
                if (bestMap == null) {
                    bestMap = KeyBest.orderBest(_context, specimenAvailableCharacterNumbers, specimenAvailableTaxaNumbers, _context.getRBase(), _context.getABase(), _context.getReuse(), _context.getVaryWt());
                }
                List<Character> bestOrderCharacters = new ArrayList<Character>(bestMap.keySet());
                confirmatoryCharacters = getConfirmatoryCharacters(specimen, includedItems, bestOrderCharacters, bestCharacter, numberOfConfirmatoryCharacters);
            }

            for (int i = 0; i < bestCharacter.getNumberOfStates(); i++) {
                int stateNumber = i + 1;
                MultiStateAttribute attr = createMultiStateAttribute(bestCharacter, stateNumber);
                specimen.setAttributeForCharacter(bestCharacter, attr);

                if (confirmatoryCharacters != null && !confirmatoryCharacters.isEmpty()) {
                    List<MultiStateAttribute> confirmatoryAttributes = new ArrayList<MultiStateAttribute>();
                    for (ConfirmatoryCharacter confChar : confirmatoryCharacters) {
                        int confCharNumber = confChar.getConfirmatoryCharacterNumber();
                        int confStateNumber = confChar.getConfirmatoryStateNumber(stateNumber);
                        if (confStateNumber == -1) {
                            // No matching state in the confirmatory character.
                            // Should only be the case when using
                            // the main state's character eliminates all
                            // remaining taxa. Simply ignore this state.
                            continue;
                        }
                        MultiStateAttribute confAttr = createMultiStateAttribute((MultiStateCharacter) _context.getDataSet().getCharacter(confCharNumber), confStateNumber);
                        confirmatoryAttributes.add(confAttr);
                    }
                    confirmatoryCharacterValues.put(bestCharacter, confirmatoryAttributes);
                }

                doCalculateKey(key, dataset, includedCharacters, includedItems, specimen, confirmatoryCharacterValues);

                specimen.removeValueForCharacter(bestCharacter);
                confirmatoryCharacterValues.remove(bestCharacter);
            }
        }
    }

    private MultiStateAttribute createMultiStateAttribute(MultiStateCharacter msChar, int stateNumber) {
        SimpleAttributeData impl = new SimpleAttributeData(false, false);
        MultiStateAttribute attr = (MultiStateAttribute) AttributeFactory.newAttribute(msChar, impl);
        Set<Integer> presentStatesSet = new HashSet<Integer>();
        presentStatesSet.add(stateNumber);
        attr.setPresentStates(presentStatesSet);
        return attr;
    }

    private int getGroupCountForColumn(int columnNumber, IdentificationKey key) {
        MultiStateAttribute currentAttribute = null;
        int countForCurrentAttribute = 0;
        int groupCount = 0;
        for (KeyRow row : key.getRows()) {
            List<MultiStateAttribute> attributes = row.getMainCharacterValues();
            if (attributes.size() >= columnNumber) {
                MultiStateAttribute attr = attributes.get(columnNumber - 1);

                if (currentAttribute == null) {
                    currentAttribute = attr;
                    countForCurrentAttribute = 1;
                } else {
                    if (attr.equals(currentAttribute)) {
                        countForCurrentAttribute++;
                    } else {
                        // Do not count groups of only one attribute
                        if (countForCurrentAttribute > 1) {
                            groupCount++;
                        }
                        countForCurrentAttribute = 0;
                        currentAttribute = attr;
                        countForCurrentAttribute = 1;
                    }
                }
            }
        }

        // handle the last group in the key
        // Do not count groups of only one attribute
        if (countForCurrentAttribute > 1) {
            groupCount++;
        }

        return groupCount;
    }

    private boolean checkPresetCharacter(Character presetCharacter, Specimen specimen, List<Item> includedItems) {
        // Preset characters must be multistate
        if (!(presetCharacter instanceof MultiStateCharacter)) {
            return false;
        }

        // Used characters or ones that have been made inapplicable cannot be
        // used as
        // presets
        if (specimen.getInapplicableCharacters().contains(presetCharacter)) {
            return false;
        }

        if (specimen.getUsedCharacters().contains(presetCharacter)) {
            return false;
        }

        // A preset character cannot completely eliminate any of the available
        // taxa. Each taxon must be
        // available after at least one of the character states is used in the
        // specimen
        Set<Item> availableTaxa = getSpecimenAvailableTaxa(specimen, includedItems);
        Set<Item> availableTaxaAfterPresetUsed = new HashSet<Item>();

        MultiStateCharacter msPreset = (MultiStateCharacter) presetCharacter;
        for (int i = 0; i < msPreset.getNumberOfStates(); i++) {
            int stateNumber = i + 1;
            MultiStateAttribute attr = createMultiStateAttribute(msPreset, stateNumber);
            specimen.setAttributeForCharacter(msPreset, attr);
            availableTaxaAfterPresetUsed.addAll(getSpecimenAvailableTaxa(specimen, includedItems));
            specimen.removeValueForCharacter(msPreset);
        }

        return availableTaxaAfterPresetUsed.equals(availableTaxa);
    }

    private List<ConfirmatoryCharacter> getConfirmatoryCharacters(Specimen specimen, List<Item> includedItems, List<Character> bestCharacters, MultiStateCharacter mainCharacter,
            int numberOfConfirmatoryCharacters) {
        int foundConfirmatoryCharacters = 0;
        List<ConfirmatoryCharacter> confirmatoryCharacters = new ArrayList<ConfirmatoryCharacter>();

        List<Set<Item>> mainCharacterStateDistributions = new ArrayList<Set<Item>>();
        for (int i = 0; i < mainCharacter.getNumberOfStates(); i++) {
            int stateNumber = i + 1;
            MultiStateAttribute attr = createMultiStateAttribute(mainCharacter, stateNumber);
            specimen.setAttributeForCharacter(mainCharacter, attr);
            mainCharacterStateDistributions.add(getSpecimenAvailableTaxa(specimen, includedItems));
            specimen.removeValueForCharacter(mainCharacter);
        }

        for (Character ch : bestCharacters) {
            MultiStateCharacter multiStateChar = (MultiStateCharacter) ch;

            if (!multiStateChar.equals(mainCharacter)) {

                List<Set<Item>> confirmatoryCharacterStateDistributions = new ArrayList<Set<Item>>();

                for (int i = 0; i < multiStateChar.getNumberOfStates(); i++) {
                    int stateNumber = i + 1;
                    MultiStateAttribute attr = createMultiStateAttribute(multiStateChar, stateNumber);
                    specimen.setAttributeForCharacter(multiStateChar, attr);
                    confirmatoryCharacterStateDistributions.add(getSpecimenAvailableTaxa(specimen, includedItems));
                    specimen.removeValueForCharacter(multiStateChar);
                }

                if (compareStateDistributions(mainCharacterStateDistributions, confirmatoryCharacterStateDistributions)) {
                    //System.out.println(MessageFormat.format("Confirmatory character {0}:{1}", mainCharacter.getCharacterId(), multiStateChar.getCharacterId()));

                    Map<Integer, Integer> mainToConfirmatoryStateMap = new HashMap<Integer, Integer>();

                    for (int i = 0; i < mainCharacterStateDistributions.size(); i++) {
                        Set<Item> distribution = mainCharacterStateDistributions.get(i);
                        if (!distribution.isEmpty()) {
                            int indexInConfirmatoryDistributions = confirmatoryCharacterStateDistributions.indexOf(distribution);
                            if (indexInConfirmatoryDistributions > -1) {
                                mainToConfirmatoryStateMap.put(i + 1, indexInConfirmatoryDistributions + 1);
                            }
                        }
                    }

                    confirmatoryCharacters.add(new ConfirmatoryCharacter(multiStateChar.getCharacterId(), mainCharacter.getCharacterId(), mainToConfirmatoryStateMap));

                    foundConfirmatoryCharacters++;
                }

                if (foundConfirmatoryCharacters == numberOfConfirmatoryCharacters) {
                    break;
                }
            }
        }

        return confirmatoryCharacters;
    }

    private boolean compareStateDistributions(List<Set<Item>> dist1, List<Set<Item>> dist2) {

        for (Set<Item> dist1Item : dist1) {
            if (dist1Item.isEmpty()) {
                continue;
            }

            if (Collections.frequency(dist1, dist1Item) != Collections.frequency(dist2, dist1Item)) {
                return false;
            }
        }

        return true;
    }

    private Set<Item> getSpecimenAvailableTaxa(Specimen specimen, List<Item> includedItems) {
        Set<Item> availableTaxa = new HashSet<Item>(includedItems);

        for (Item item : specimen.getTaxonDifferences().keySet()) {
            Set<Character> differingCharacters = specimen.getTaxonDifferences().get(item);
            if (!differingCharacters.isEmpty()) {
                availableTaxa.remove(item);
            }
        }

        return availableTaxa;
    }

    private Set<Character> getSpecimenAvailableCharacters(Specimen specimen, List<Character> includedCharacters) {
        Set<Character> availableChars = new HashSet<Character>(includedCharacters);

        availableChars.removeAll(specimen.getUsedCharacters());

        availableChars.removeAll(specimen.getInapplicableCharacters());

        return availableChars;
    }

    private void processDirectivesFile(File input, KeyContext context) throws IOException, DirectiveException {
        KeyDirectiveParser parser = KeyDirectiveParser.createInstance();
        parser.registerObserver(this);
        parser.parse(input, context);
    }

    public KeyContext getContext() {
        return _context;
    }

    private void generateKeyOutput(IdentificationKey key, List<Character> includedCharacters, List<Item> includedItems, boolean outputTabularKey, boolean outputBracketedKey) {
        PrintFile printFile = _context.getOutputFileSelector().getOutputFile();

        printFile.outputLine(_context.getHeading(HeadingType.HEADING));
        printFile.outputLine(StringUtils.repeat("*", _context.getOutputFileSelector().getOutputWidth()));

        printFile.outputLine(generateCreditsString());
        printFile.writeBlankLines(1, 0);

        Date currentDate = Calendar.getInstance().getTime();

        printFile.outputLine(MessageFormat.format("Run at {0} on {1}", _timeFormat.format(currentDate), _dateFormat.format(currentDate)));
        printFile.writeBlankLines(1, 0);

        // Traverse the key to count the number of characters and items used in
        // it.
        Set<Character> charactersInKey = new HashSet<Character>();
        Set<Item> itemsInKey = new HashSet<Item>();

        for (KeyRow row : key.getRows()) {
            Item it = row.getItem();
            itemsInKey.add(it);
            for (int i = 0; i < row.getNumberOfColumnValues(); i++) {
                MultiStateAttribute mainCharacterValue = row.getMainCharacterValueAt(i);
                charactersInKey.add(mainCharacterValue.getCharacter());

                List<MultiStateAttribute> confirmatoryCharacterValues = row.getConfirmatoryCharacterValuesAt(i);
                if (confirmatoryCharacterValues != null) {
                    for (MultiStateAttribute confCharVal : confirmatoryCharacterValues) {
                        charactersInKey.add(confCharVal.getCharacter());
                    }
                }
            }
        }

        printFile.outputLine(MessageFormat.format("Characters - {0} in data, {1} included, {2} in key.", _context.getDataSet().getNumberOfCharacters(), includedCharacters.size(),
                charactersInKey.size()));
        printFile.outputLine(MessageFormat.format("Items - {0} in data, {1} included, {2} in key.", _context.getDataSet().getMaximumNumberOfItems(), includedItems.size(), itemsInKey.size()));
        printFile.writeBlankLines(1, 0);
        printFile.outputLine(MessageFormat.format("RBASE = {0} ABASE = {1} REUSE = {2} VARYWT = {3}", formatDouble(_context.getRBase()), formatDouble(_context.getABase()),
                formatDouble(_context.getReuse()), formatDouble(_context.getVaryWt())));
        // printFile.outputLine(MessageFormat.format("Number of confirmatory characters = {0}",
        // "TODO"));
        printFile.writeBlankLines(1, 0);
        // printFile.outputLine(MessageFormat.format("Average length of key = {0} Average cost of key = {1}",
        // "TODO", "TODO"));
        // printFile.outputLine(MessageFormat.format("Maximum length of key = {0} Maximum cost of key = {1}",
        // "TODO", "TODO"));

        printFile.writeBlankLines(1, 0);
        // printFile.outputLine(MessageFormat.format("Preset characters (character,column:group) 666,2:1",
        // "TODO");
        List<Integer> includedCharacterNumbers = new ArrayList<Integer>();
        for (Character ch : includedCharacters) {
            includedCharacterNumbers.add(ch.getCharacterId());
        }
        printFile.outputLine(MessageFormat.format("Characters included {0}", Utils.formatIntegersAsListOfRanges(includedCharacterNumbers)));

        List<Integer> includedItemNumbers = new ArrayList<Integer>();
        for (Item it : includedItems) {
            includedItemNumbers.add(it.getItemNumber());
        }
        printFile.outputLine(MessageFormat.format("Items included {0}", Utils.formatIntegersAsListOfRanges(includedItemNumbers)));
        // printFile.outputLine(MessageFormat.format("Items abundances {0}",
        // "TODO"));
        printFile.writeBlankLines(1, 0);

        if (outputTabularKey) {
            printTabularKey(key, printFile);
            System.out.println("Tabular key completed");
        }

        if (outputBracketedKey) {
            if (_context.getDisplayTabularKey()) {
                printFile.writeBlankLines(2, 0);
            }

            printBracketedKey(key, _context.getAddCharacterNumbers(), printFile);
            System.out.println("Bracketed key completed");
        }

    }

    // NOTE: In addition to the output lines generated here, any errors and a
    // list of output files are also written to the listing file.
    // These pieces of output are inserted by the KeyOutputFileManager.
    private void generateListingOutput(List<Character> includedCharacters, List<Item> includedItems, boolean outputKeyConfiguration) {
        KeyOutputFileManager outputFileManager = _context.getOutputFileManager();
        int outputWidth = outputFileManager.getOutputWidth();

        PrintFile listingPrintFile = outputFileManager.getKeyListingFile();
        if (listingPrintFile == null) {
            listingPrintFile = new PrintFile(System.out, outputWidth);
        } else {
            // Only output the credits when writing to a listing file. They have
            // already been written to
            // standard out.
            listingPrintFile.outputLine(generateCreditsString());
            listingPrintFile.writeBlankLines(1, 0);
        }

        if (outputKeyConfiguration) {
            DeltaDataSet dataset = _context.getDataSet();
            listingPrintFile.outputLine(MessageFormat.format("Number of characters = {0} Number of items = {1}", dataset.getNumberOfCharacters(), dataset.getMaximumNumberOfItems()));
            listingPrintFile.outputLine(MessageFormat.format("RBASE = {0} ABASE = {1} REUSE = {2} VARYWT = {3}", formatDouble(_context.getRBase()), formatDouble(_context.getABase()),
                    formatDouble(_context.getReuse()), formatDouble(_context.getVaryWt())));
            // TODO Number of confirmatory characers, number of preset
            // characters

            List<Integer> includedCharacterNumbers = new ArrayList<Integer>();
            for (Character ch : includedCharacters) {
                includedCharacterNumbers.add(ch.getCharacterId());
            }
            listingPrintFile.outputLine(MessageFormat.format("Characters included {0}", Utils.formatIntegersAsListOfRanges(includedCharacterNumbers)));

            listingPrintFile.outputLine(MessageFormat.format("{0} characters included.", includedCharacters.size()));
            listingPrintFile.outputLine(MessageFormat.format("{0} items included.", includedItems.size()));
            listingPrintFile.writeBlankLines(1, 0);
        }
    }

    private void generateTypesettingOutput() {
        String headerText = _context.getTypeSettingFileHeaderText();

        if (headerText != null) {
            _context.getOutputFileManager().getTypesettingFile().outputLine(headerText);
        }
        _context.getOutputFileManager().getTypesettingFile().outputLine("TODO - IMPLEMENT TYPESETTING OF BRACKETED KEY!");
    }

    private void printTabularKey(IdentificationKey key, PrintFile printFile) {
        ItemFormatter itemFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, false);

        // Do a first pass of the data structure to get the counts for the
        // number of times a taxon appears in the key, and to work out how wide
        // the cells need to be
        Map<Item, Integer> itemOccurrences = new HashMap<Item, Integer>();
        int cellWidth = 0;

        for (KeyRow row : key.getRows()) {
            Item it = row.getItem();

            if (itemOccurrences.containsKey(it)) {
                int currentItemCount = itemOccurrences.get(it);
                itemOccurrences.put(it, currentItemCount + 1);
            } else {
                itemOccurrences.put(it, 1);
            }

            for (MultiStateAttribute characterValue : row.getAllCharacterValues()) {

                int characterNumber = characterValue.getCharacter().getCharacterId();
                int numberOfDigits = Integer.toString(characterNumber).length();

                // Cell width needs to be at least as wide as the number of
                // digits, plus one extra character for the state value
                // associated with the attribute
                if (cellWidth < numberOfDigits + 1) {
                    cellWidth = numberOfDigits + 1;
                }
            }
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < key.getNumberOfRows(); i++) {
            KeyRow row = key.getRowAt(i);
            Item it = row.getItem();
            List<MultiStateAttribute> rowCharacterValues = row.getAllCharacterValues();

            List<MultiStateAttribute> previousRowCharacterValues = null;

            if (i > 0) {
                previousRowCharacterValues = key.getRowAt(i - 1).getAllCharacterValues();
            }

            // Output the dividing line between the previous row and the current
            // row
            builder.append("+---------------------------+");
            for (int j = 0; j < rowCharacterValues.size(); j++) {
                Attribute currentRowAttribute = rowCharacterValues.get(j);

                if (previousRowCharacterValues != null && previousRowCharacterValues.size() >= j + 1) {
                    Attribute previousRowAttribute = previousRowCharacterValues.get(j);
                    if (currentRowAttribute.equals(previousRowAttribute)) {
                        builder.append(StringUtils.repeat(" ", cellWidth));
                        builder.append("|");
                    } else {
                        builder.append(StringUtils.repeat("-", cellWidth));
                        builder.append("+");
                    }

                } else {
                    builder.append(StringUtils.repeat("-", cellWidth));
                    builder.append("+");
                }
            }

            if (previousRowCharacterValues != null) {
                int diffPrevRowAttributes = previousRowCharacterValues.size() - rowCharacterValues.size();
                for (int k = 0; k < diffPrevRowAttributes; k++) {
                    builder.append(StringUtils.repeat("-", cellWidth));
                    builder.append("+");
                }
            }

            builder.append("\n");

            // Output the item name and the number of occurences of the item in
            // the key, if it appears more than once
            builder.append("|");
            String formattedItemName = itemFormatter.formatItemDescription(it);
            builder.append(formattedItemName);

            int numItemOccurrences = itemOccurrences.get(it);

            if (numItemOccurrences > 1) {
                builder.append(StringUtils.repeat(" ", 27 - formattedItemName.length() - Integer.toString(numItemOccurrences).length()));
                builder.append(numItemOccurrences);
            } else {
                builder.append(StringUtils.repeat(" ", 27 - formattedItemName.length()));
            }

            builder.append("|");

            // Output the values character values used in the. Include values
            // for confirmatory characters if they are present
            for (int j = 0; j < row.getNumberOfColumnValues(); j++) {
                List<MultiStateAttribute> cellCharacterValues = row.getAllCharacterValuesAt(j);
                for (int k = 0; k < cellCharacterValues.size(); k++) {
                    MultiStateAttribute cellCharacterValue = cellCharacterValues.get(k);
                    int characterId = cellCharacterValue.getCharacter().getCharacterId();

                    // Insert spaces to pad out the cell if the character id +
                    // state
                    // value are not as wide as the cell width
                    builder.append(StringUtils.repeat(" ", cellWidth - (Integer.toString(characterId).length() + 1)));

                    builder.append(characterId);

                    // Only 1 state will be ever set - the key generation
                    // algorithm
                    // only sets
                    // Individual states for characters
                    int stateNumber = cellCharacterValue.getPresentStates().iterator().next();
                    // Convert state numbers to "A", "B", "C" etc
                    builder.append((char) (64 + stateNumber));

                    if (cellCharacterValues.size() > 1 && k < cellCharacterValues.size() - 1) {
                        builder.append(" ");
                    }
                }

                builder.append("|");
            }

            builder.append("\n");

            // If this is the last row, need to print the bottom edge of the
            // table
            if (i == key.getNumberOfRows() - 1) {
                builder.append("+---------------------------+");
                for (int l = 0; l < rowCharacterValues.size(); l++) {
                    builder.append(StringUtils.repeat("-", cellWidth));
                    builder.append("+");
                }
            }
        }

        printFile.outputLine(builder.toString());
    }

    private void printBracketedKey(IdentificationKey key, boolean displayCharacterNumbers, PrintFile printFile) {
        // List<List<MultiStateCharacter>> indexCharacters = new
        // ArrayList<List<MultiStateCharacter>>();
        List<Map<List<MultiStateAttribute>, Object>> indexInfoMaps = new ArrayList<Map<List<MultiStateAttribute>, Object>>();

        Map<List<MultiStateCharacter>, Integer> latestIndexForCharacterGroupMap = new HashMap<List<MultiStateCharacter>, Integer>();
        Map<Integer, Integer> indexBackReferences = new HashMap<Integer, Integer>();

        // int currentCharacterIndex = 1;

        for (int i = 0; i < key.getNumberOfRows(); i++) {
            KeyRow row = key.getRowAt(i);
            for (int j = 0; j < row.getNumberOfColumnValues(); j++) {
                List<MultiStateAttribute> columnAttrs = row.getAllCharacterValuesAt(j);
                List<MultiStateCharacter> columnChars = getCharactersFromAttributes(columnAttrs);

                KeyRow previousRow = null;

                // If the corresponding column in the previous row has the same
                // characters, use the latest existing index for that set of
                // characters.
                // Otherwise we need to create a new index
                boolean newIndex = false;
                if (i > 0) {
                    previousRow = key.getRowAt(i - 1);
                    if (previousRow.getNumberOfColumnValues() >= j + 1) {
                        if (!rowsMatchCharactersAtColumn(row, previousRow, j)) {
                            newIndex = true;
                        }
                    } else {
                        newIndex = true;
                    }
                }

                int indexForColumn;
                Map<List<MultiStateAttribute>, Object> indexInfo;

                if (newIndex || !latestIndexForCharacterGroupMap.containsKey(columnChars)) {
                    indexForColumn = indexInfoMaps.size();
                    // indexCharacters.add(columnChars);
                    indexInfo = new HashMap<List<MultiStateAttribute>, Object>();
                    indexInfoMaps.add(indexInfo);
                    latestIndexForCharacterGroupMap.put(columnChars, indexForColumn);
                } else {
                    indexForColumn = latestIndexForCharacterGroupMap.get(columnChars);
                    indexInfo = indexInfoMaps.get(indexForColumn);
                }

                if (j == row.getNumberOfColumnValues() - 1) {
                    if (indexInfo.containsKey(columnAttrs)) {
                        List<Item> taxaList = (List<Item>) indexInfo.get(columnAttrs);
                        taxaList.add(row.getItem());
                    } else {
                        List<Item> taxaList = new ArrayList<Item>();
                        taxaList.add(row.getItem());
                        indexInfo.put(columnAttrs, taxaList);
                    }
                } else {
                    List<MultiStateAttribute> nextColumnAttrs = row.getAllCharacterValuesAt(j + 1);
                    List<MultiStateCharacter> nextColumnChars = getCharactersFromAttributes(nextColumnAttrs);

                    // Get the index for the next column. If no index has been
                    // recorded for the group of characters, or
                    // the group of characters is different to the corresponding
                    // column in the previous row, then we
                    // need to create a new
                    int indexForNextColumn;
                    if (latestIndexForCharacterGroupMap.containsKey(nextColumnChars)) {
                        indexForNextColumn = latestIndexForCharacterGroupMap.get(nextColumnChars);

                        if (i > 0) {
                            if (previousRow.getNumberOfColumnValues() >= j + 2) {
                                if (!rowsMatchCharactersAtColumn(row, previousRow, j + 1)) {
                                    indexForNextColumn = indexInfoMaps.size();
                                }
                            } else {
                                indexForNextColumn = indexInfoMaps.size();
                            }
                        }
                    } else {
                        indexForNextColumn = indexInfoMaps.size();
                    }

                    indexInfo.put(columnAttrs, indexForNextColumn);
                    indexBackReferences.put(indexForNextColumn, indexForColumn);
                }
            }
        }

        System.out.println("TODO - print bracketed key");

        for (int i = 0; i < indexInfoMaps.size(); i++) {
            Map<List<MultiStateAttribute>, Object> indexInfoMap = indexInfoMaps.get(i);

            int backReference;
            if (i == 0) {
                backReference = 0;
            } else {
                backReference = indexBackReferences.get(i) + 1;
            }

            outputBrackedKeyIndex(printFile, i + 1, backReference, indexInfoMap);
        }
    }

    private void outputBrackedKeyIndex(PrintFile printFile, int indexNumber, int backReference, Map<List<MultiStateAttribute>, Object> attributeLinks) {
        StringBuilder builder = new StringBuilder();

        builder.append(indexNumber);
        builder.append(" ");
        builder.append("(").append(backReference).append(")").append(".");

        // Sort attribute lists by the first state values of the first attribute - any other attributes in the list are for
        // confirmatory characters, these will not effect the sort order.
        List<List<MultiStateAttribute>> sortedAttributeLists = new ArrayList<List<MultiStateAttribute>>(attributeLinks.keySet());
        Collections.sort(sortedAttributeLists, new Comparator<List<MultiStateAttribute>>() {

            @Override
            public int compare(List<MultiStateAttribute> l1, List<MultiStateAttribute> l2) {
                MultiStateAttribute l1FirstAttr = l1.get(0);
                MultiStateAttribute l2FirstAttr = l2.get(0);

                int l1FirstAttrStateNumber = l1FirstAttr.getPresentStates().iterator().next();
                int l2FirstAttrStateNumber = l2FirstAttr.getPresentStates().iterator().next();

                return Integer.valueOf(l1FirstAttrStateNumber).compareTo(l2FirstAttrStateNumber);
            }

        });

        for (List<MultiStateAttribute> attrs : sortedAttributeLists) {
            for (MultiStateAttribute attr : attrs) {
                builder.append(_charFormatter.formatCharacterDescription(attr.getCharacter()));
                builder.append(" ");
                builder.append(_charFormatter.formatState(attr.getCharacter(), attr.getPresentStates().iterator().next()));
                builder.append(" ");
            }
            builder.append(".....");

            Object itemListOrIndexNumber = attributeLinks.get(attrs);

            if (itemListOrIndexNumber instanceof List<?>) {
                List<Item> taxa = (List<Item>) itemListOrIndexNumber;
                for (Item taxon : taxa) {
                    builder.append(_itemFormatter.formatItemDescription(taxon));
                    builder.append(" ");
                }
            } else {
                int forwardReference = (Integer) itemListOrIndexNumber;
                builder.append(forwardReference + 1);
            }

            builder.append("\n");
        }

        System.out.println(builder.toString());
    }

    private List<MultiStateCharacter> getCharactersFromAttributes(List<MultiStateAttribute> attrs) {
        List<MultiStateCharacter> chars = new ArrayList<MultiStateCharacter>();
        for (MultiStateAttribute attr : attrs) {
            chars.add(attr.getCharacter());
        }

        return chars;
    }

    private boolean rowsMatchCharactersAtColumn(KeyRow row1, KeyRow row2, int columnIndex) {
        List<MultiStateAttribute> row1ColumnAttrs = row1.getAllCharacterValuesAt(columnIndex);
        List<MultiStateCharacter> row1ColumnChars = getCharactersFromAttributes(row1ColumnAttrs);

        List<MultiStateAttribute> row2ColumnAttrs = row2.getAllCharacterValuesAt(columnIndex);
        List<MultiStateCharacter> row2ColumnChars = getCharactersFromAttributes(row2ColumnAttrs);

        return row1ColumnChars.equals(row2ColumnChars);
    }

    private String formatDouble(double d) {
        return String.format("%.2f", d);
    }

    @Override
    public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) throws DirectiveException {
        if (directive instanceof IncludeCharacters || directive instanceof ExcludeCharacters || directive instanceof IncludeItems || directive instanceof ExcludeItems) {
            readInputFiles();
        }

        _nestedObserver.preProcess(directive, data);
    }

    @Override
    public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {
        _nestedObserver.postProcess(directive);
    }

    @Override
    public void finishedProcessing() {
        _nestedObserver.finishedProcessing();
    }

    @Override
    public void handleDirectiveProcessingException(AbstractDeltaContext context, AbstractDirective<? extends AbstractDeltaContext> directive, Exception ex) throws DirectiveException {
        _nestedObserver.handleDirectiveProcessingException(context, directive, ex);
    }

}

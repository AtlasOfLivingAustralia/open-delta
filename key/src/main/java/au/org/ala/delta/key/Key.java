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
import java.io.PrintStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

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
import au.org.ala.delta.key.directives.KeyDirectiveParser;
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
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;
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

    private KeyContext _context;
    private boolean _inputFilesRead = false;

    private SimpleDateFormat _timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat _dateFormat = new SimpleDateFormat("d-MMM-yyyy");

    private ConforDirectiveParserObserver _nestedObserver;

    private CharacterFormatter _charFormatter = new CharacterFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false);
    private ItemFormatter _itemFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, false);

    private PrintStream _defaultOutputStream;
    
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

        new Key(f).calculateKey();
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
        _context = new KeyContext(directivesFile);
        _defaultOutputStream = _context.getOutputFileManager().getDefaultOutputStream();
    }

    public Key(KeyContext context) {
        _context = context;
        _defaultOutputStream = _context.getOutputFileManager().getDefaultOutputStream();
    }

    public List<File> getOutputFiles() {
        return _context.getOutputFileSelector().getOutputFiles();
    }

    public void calculateKey() {
        try {
            File directivesFile = _context.getDirectivesFile();
            _nestedObserver = new ConforDirectiveParserObserver(_context);

            boolean parseSuccessful = true;
            try {
                processDirectivesFile(directivesFile, _context);
            } catch (DirectiveException ex) {
                // Error message will be output by the _nestedObserver. Simply
                // stop
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
                                                                // calculating
                                                                // the
                                                                // key if we
                                                                // don't
                                                                // want one!
                    Specimen specimen = new Specimen(_context.getDataSet(), true, false, false, MatchType.OVERLAP);
                    // List<Pair<Item, List<Attribute>>> keyList = new
                    // ArrayList<Pair<Item, List<Attribute>>>();
                    TabularKey key = new TabularKey();

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

                    doCalculateKey(key, dataset, includedCharacters, includedItems, specimen, null, null);
                    _defaultOutputStream.println("Key generation completed");

                    Map<Integer, TypeSettingMark> typesettingMarksMap = _context.getTypeSettingMarks();
                    boolean typsettingMarksSpecified = !(typesettingMarksMap == null || typesettingMarksMap.isEmpty());

                    generateKeyOutput(key, includedCharacters, includedItems, _context.getDisplayTabularKey(), _context.getDisplayBracketedKey(), typsettingMarksSpecified);

                    generateListingOutput(includedCharacters, includedItems, true);
                } else {
                    generateListingOutput(null, null, false);
                }
            }

            _nestedObserver.finishedProcessing();
        } catch (Exception ex) {
            ex.printStackTrace();
            _defaultOutputStream.println(MessageFormat.format("FATAL ERROR: {0}", ex.getMessage().toString()));
            _defaultOutputStream.println("Execution terminated");
            _defaultOutputStream.println("ABNORMAL TERMINATION");
        }
    }

    private void readInputFiles() {
        if (!_inputFilesRead) {
            KeyUtils.loadDataset(_context);
            _inputFilesRead = true;
        }
    }

    private void doCalculateKey(TabularKey key, FilteredDataSet dataset, List<Character> includedCharacters, List<Item> includedItems, Specimen specimen,
            Map<Character, List<MultiStateAttribute>> confirmatoryCharacterValues, Map<Character, Double> usedCharacterCosts) {

        if (confirmatoryCharacterValues == null) {
            confirmatoryCharacterValues = new HashMap<Character, List<MultiStateAttribute>>();
        }

        if (usedCharacterCosts == null) {
            usedCharacterCosts = new HashMap<Character, Double>();
        }

        Set<Item> specimenAvailableTaxa = getSpecimenAvailableTaxa(specimen, includedItems);
        Set<Character> specimenAvailableCharacters = getSpecimenAvailableCharacters(specimen, includedCharacters);

        if (specimenAvailableTaxa.size() == 0) {
            return;
        } else if (specimenAvailableTaxa.size() == 1 || (_context.getStopAfterColumnNumber() != -1 && specimen.getUsedCharacters().size() == _context.getStopAfterColumnNumber())) {
            // Add a row to the table if a taxon has been identified (only 1
            // taxon remains available)

            // if the column limit set using the STOP AFTER COLUMN directive has
            // been reached, add a row for each remaining taxon
            // with the used characters

            for (Item taxon : specimenAvailableTaxa) {
                TabularKeyRow row = new TabularKeyRow();
                row.setItem(taxon);

                for (Character ch : specimen.getUsedCharacters()) {
                    // Add row to key
                    MultiStateAttribute mainCharacterValue = (MultiStateAttribute) specimen.getAttributeForCharacter(ch);
                    row.addAttribute(mainCharacterValue, confirmatoryCharacterValues.get(ch), usedCharacterCosts.get(ch));

                    // If character has not already been used in key, update its
                    // cost using the REUSE setting to increase the
                    // probability of reuse
                    if (!key.isCharacterUsedInKey(ch)) {
                        double newCost = _context.getCharacterCost(ch.getCharacterId()) / _context.getReuse();
                        _context.setCharacterCost(ch.getCharacterId(), newCost);
                    }
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

            LinkedHashMap<Character, Double> bestMap = KeyBest.orderBest(_context.getDataSet(), _context.getCharacterCostsAsArray(), _context.getCalculatedItemAbundanceValuesAsArray(),
                    specimenAvailableCharacterNumbers, specimenAvailableTaxaNumbers, _context.getRBase(), _context.getABase(), _context.getReuse(), _context.getVaryWt(),
                    _context.getAllowImproperSubgroups());

            // -1 indicates no preset character for the column/group
            if (presetCharacterNumber > 0) {
                Character presetCharacter = _context.getDataSet().getCharacter(presetCharacterNumber);
                if (checkPresetCharacter(presetCharacter, specimen, includedItems)) {
                    _defaultOutputStream.println(MessageFormat.format("Using preset character {0},{1}:{2}", presetCharacterNumber, currentColumn, currentGroup));
                    bestCharacter = (MultiStateCharacter) presetCharacter;
                } else {
                    throw new RuntimeException(MessageFormat.format("Character {0} is not suitable for use at column {1} group {2}", presetCharacterNumber, currentColumn, currentGroup));
                }
            } else {
                List<Character> bestOrderCharacters = new ArrayList<Character>(bestMap.keySet());
                if (bestOrderCharacters.isEmpty()) {
                    return;
                } else {
                    // KEY only uses multi state characters
                    bestCharacter = (MultiStateCharacter) bestOrderCharacters.get(0);
                }
            }

            double bestCharacterCost = _context.getCharacterCost(bestCharacter.getCharacterId());

            List<ConfirmatoryCharacter> confirmatoryCharacters = null;
            int numberOfConfirmatoryCharacters = _context.getNumberOfConfirmatoryCharacters();
            if (numberOfConfirmatoryCharacters > 0) {
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

                usedCharacterCosts.put(bestCharacter, bestCharacterCost);

                doCalculateKey(key, dataset, includedCharacters, includedItems, specimen, confirmatoryCharacterValues, usedCharacterCosts);

                specimen.removeValueForCharacter(bestCharacter);
                confirmatoryCharacterValues.remove(bestCharacter);
                usedCharacterCosts.remove(bestCharacter);
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

    private int getGroupCountForColumn(int columnNumber, TabularKey key) {
        MultiStateAttribute currentAttribute = null;
        int countForCurrentAttribute = 0;
        int groupCount = 0;
        for (TabularKeyRow row : key.getRows()) {
            List<MultiStateAttribute> attributes = row.getMainAttributes();
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

    private void generateKeyOutput(TabularKey tabularKey, List<Character> includedCharacters, List<Item> includedItems, boolean outputTabularKey, boolean outputBracketedKey,
            boolean typesettingMarksSpecified) {
        PrintFile printFile = _context.getOutputFileManager().getOutputFile();

        if (outputTabularKey) {
            generateKeyHeader(printFile, tabularKey, includedCharacters, includedItems, outputTabularKey, outputBracketedKey);
            printTabularKey(tabularKey, printFile);
            _defaultOutputStream.println("Tabular key completed");
        }

        if (outputBracketedKey) {
            BracketedKey bracketedKey = KeyUtils.convertTabularKeyToBracketedKey(tabularKey);
            if (typesettingMarksSpecified) {
                PrintFile typesetFile = _context.getOutputFileManager().getTypesettingFile();
                generateTypesetBracketedKey(bracketedKey, includedCharacters, includedItems, typesetFile, _context.getAddCharacterNumbers(), _context.getOutputHtml(), tabularKey
                        .getCharactersUsedInKey().size(), tabularKey.getItemsUsedInKey().size(), tabularKey.getAverageLength(), tabularKey.getAverageCost(), tabularKey.getMaximumLength(),
                        tabularKey.getMaximumCost());
            } else {
                if (_context.getDisplayTabularKey()) {
                    printFile.writeBlankLines(2, 0);
                }

                generateKeyHeader(printFile, tabularKey, includedCharacters, includedItems, outputTabularKey, outputBracketedKey);
                printBracketedKey(bracketedKey, _context.getAddCharacterNumbers(), printFile);
            }
            _defaultOutputStream.println("Bracketed key completed");
        }
    }

    private void generateKeyHeader(PrintFile printFile, TabularKey key, List<Character> includedCharacters, List<Item> includedItems, boolean outputTabularKey, boolean outputBracketedKey) {
        printFile.outputLine(_context.getHeading(HeadingType.HEADING));
        printFile.outputLine(StringUtils.repeat("*", _context.getOutputFileSelector().getOutputWidth()));
        printFile.writeBlankLines(1, 0);

        printFile.outputLine(generateCreditsString());
        printFile.writeBlankLines(1, 0);

        Date currentDate = Calendar.getInstance().getTime();

        printFile.outputLine(MessageFormat.format("Run at {0} on {1}", _timeFormat.format(currentDate), _dateFormat.format(currentDate)));
        printFile.writeBlankLines(1, 0);

        printFile.outputLine(MessageFormat.format("Characters - {0} in data, {1} included, {2} in key.", _context.getDataSet().getNumberOfCharacters(), includedCharacters.size(), key
                .getCharactersUsedInKey().size()));
        printFile.outputLine(MessageFormat.format("Items - {0} in data, {1} included, {2} in key.", _context.getDataSet().getMaximumNumberOfItems(), includedItems.size(), key.getNumberOfRows()));
        printFile.writeBlankLines(1, 0);
        printFile.outputLine(MessageFormat.format("RBASE = {0} ABASE = {1} REUSE = {2} VARYWT = {3}", formatDouble(_context.getRBase()), formatDouble(_context.getABase()),
                formatDouble(_context.getReuse()), formatDouble(_context.getVaryWt())));
        printFile.outputLine(MessageFormat.format("Number of confirmatory characters = {0}", _context.getNumberOfConfirmatoryCharacters()));
        printFile.writeBlankLines(1, 0);
        printFile.outputLine(MessageFormat.format("Average length of key = {0} Average cost of key = {1}", String.format("%.1f", key.getAverageLength()), String.format("%.1f", key.getAverageCost())));
        printFile.outputLine(MessageFormat.format("Maximum length of key = {0} Maximum cost of key = {1}", String.format("%.1f", key.getMaximumLength()), String.format("%.1f", key.getMaximumCost())));

        printFile.writeBlankLines(1, 0);

        if (_context.getPresetCharacters().size() > 0) {
            printFile.outputLine(MessageFormat.format("Preset characters (character,column:group) {0}", KeyUtils.formatPresetCharacters(_context)));
            printFile.writeBlankLines(1, 0);
        }

        List<Integer> includedCharacterNumbers = new ArrayList<Integer>();
        for (Character ch : includedCharacters) {
            includedCharacterNumbers.add(ch.getCharacterId());
        }
        printFile.outputLine(MessageFormat.format("Characters included {0}", Utils.formatIntegersAsListOfRanges(includedCharacterNumbers)));

        List<Integer> includedItemNumbers = new ArrayList<Integer>();
        for (Item it : includedItems) {
            includedItemNumbers.add(it.getItemNumber());
        }

        printFile.outputLine(MessageFormat.format("Character reliabilities {0}", KeyUtils.formatCharacterReliabilities(_context, ",", "-")));

        printFile.writeBlankLines(1, 0);
        printFile.outputLine(MessageFormat.format("Items included {0}", Utils.formatIntegersAsListOfRanges(includedItemNumbers)));
        printFile.outputLine(MessageFormat.format("Item abundances {0}", KeyUtils.formatTaxonAbunances(_context, ",", "-")));

        printFile.writeBlankLines(1, 0);
    }

    // NOTE: In addition to the output lines generated here, any errors and a
    // list of output files are also written to the listing file.
    // These pieces of output are inserted by the KeyOutputFileManager.
    private void generateListingOutput(List<Character> includedCharacters, List<Item> includedItems, boolean outputKeyConfiguration) {
        PrintFile listingPrintFile = getListingPrintFile();

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

    // Get PrintFile for handling listing output. If the LISTING FILE directive
    // is not used, the PrintFile instance will point to
    // stdout.
    private PrintFile getListingPrintFile() {
        KeyOutputFileManager outputFileManager = _context.getOutputFileManager();
        int outputWidth = outputFileManager.getOutputWidth();
        int pageLength = outputFileManager.getOutputPageLength();

        PrintFile listingPrintFile = outputFileManager.getKeyListingFile();
        if (listingPrintFile == null) {
            listingPrintFile = new PrintFile(outputFileManager.getDefaultOutputStream(), outputWidth, pageLength);
        } else {
            // Only append the credits if we are not outputting to stdout.
            // The credits are always output to stdout when the application is
            // started.
            listingPrintFile.outputLine(generateCreditsString());
            listingPrintFile.writeBlankLines(1, 0);
        }

        return listingPrintFile;
    }

    private void printTabularKey(TabularKey key, PrintFile printFile) {
        ItemFormatter itemFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, false);

        // Do a first pass of the data structure to get the counts for the
        // number of times a taxon appears in the key, and to work out how wide
        // the cells need to be
        Map<Item, Integer> itemOccurrences = new HashMap<Item, Integer>();
        int cellWidth = 0;

        for (TabularKeyRow row : key.getRows()) {
            Item it = row.getItem();

            if (itemOccurrences.containsKey(it)) {
                int currentItemCount = itemOccurrences.get(it);
                itemOccurrences.put(it, currentItemCount + 1);
            } else {
                itemOccurrences.put(it, 1);
            }

            // If TRUNCATE TABULAR KEY AT directive has been used, only
            // traverse up to the relevant column.
            int columnLimit = row.getNumberOfColumns();
            if (_context.getTruncateTabularKeyAtColumnNumber() != -1) {
                columnLimit = _context.getTruncateTabularKeyAtColumnNumber();
            }

            for (int i = 0; i < columnLimit; i++) {
                int columnNumber = i + 1;

                for (MultiStateAttribute attr : row.getAllAttributesForColumn(columnNumber)) {
                    int characterNumber = attr.getCharacter().getCharacterId();
                    int numberOfDigits = Integer.toString(characterNumber).length();

                    // Cell width needs to be at least as wide as the number of
                    // digits, plus one extra character for the state value
                    // associated with the attribute
                    if (cellWidth < numberOfDigits + 1) {
                        cellWidth = numberOfDigits + 1;
                    }
                }
            }
        }

        StringBuilder builder = new StringBuilder();

        // Second pass - output the key
        for (int i = 0; i < key.getNumberOfRows(); i++) {
            TabularKeyRow row = key.getRowAt(i);
            Item it = row.getItem();

            List<MultiStateAttribute> rowAttributes;
            List<MultiStateAttribute> previousRowAttributes = null;

            // If TRUNCATE TABULAR KEY AT directive has been used, only
            // traverse up to the relevant column.
            int columnLimit = row.getNumberOfColumns();

            if (_context.getTruncateTabularKeyAtColumnNumber() == -1) {
                rowAttributes = row.getAllAttributes();
                if (i > 0) {
                    previousRowAttributes = key.getRowAt(i - 1).getAllAttributes();
                }
            } else {
                columnLimit = _context.getTruncateTabularKeyAtColumnNumber();
                rowAttributes = row.getAllCharacterValuesUpToColumn(columnLimit);
                if (i > 0) {
                    previousRowAttributes = key.getRowAt(i - 1).getAllCharacterValuesUpToColumn(columnLimit);
                }
            }

            // Output the dividing line between the previous row and the current
            // row
            builder.append("+---------------------------+");

            for (int j = 0; j < rowAttributes.size(); j++) {
                Attribute currentRowAttribute = rowAttributes.get(j);

                if (previousRowAttributes != null && previousRowAttributes.size() >= j + 1) {
                    Attribute previousRowAttribute = previousRowAttributes.get(j);
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

            if (previousRowAttributes != null) {
                int diffPrevRowAttributes = previousRowAttributes.size() - rowAttributes.size();
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
            for (int j = 0; j < columnLimit; j++) {
                int columnNumber = j + 1;
                List<MultiStateAttribute> cellCharacterValues = row.getAllAttributesForColumn(columnNumber);
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
                for (int l = 0; l < rowAttributes.size(); l++) {
                    builder.append(StringUtils.repeat("-", cellWidth));
                    builder.append("+");
                }
            }
        }

        printFile.outputLine(builder.toString());
    }

    private void printBracketedKey(BracketedKey bracketedKey, boolean displayCharacterNumbers, PrintFile printFile) {

        int numberOfIndicies = bracketedKey.getNumberOfNodes();
        // Amount that lines in the bracketed key need to be indented so that
        // all lines for an indent line up, given the amount that the
        // first line is pushed out due to the index number and back reference.
        // Need space for the index number and backreference, plus
        // one each for the open and close bracket, and the full stop, plus one
        // extra space
        int indexNumberIndent = Integer.toString(numberOfIndicies).length() * 2 + 4;

        // If a line wraps,
        // add an extra 1
        // space on top of
        // the indent from
        // the index and
        // backreference
        // numbers
        int indexLineWrapIndent = indexNumberIndent + 1;
        printFile.setLineWrapIndent(indexLineWrapIndent);

        // allow print file to wrap lines on '.' as well as spaces
        List<java.lang.Character> lineWrapChars = new ArrayList<java.lang.Character>();
        lineWrapChars.add(' ');
        lineWrapChars.add('.');

        printFile.setIndentOnLineWrap(true);
        printFile.setTrimInput(false, false);

        for (BracketedKeyNode node : bracketedKey) {
            outputBrackedKeyNode(printFile, node, displayCharacterNumbers, indexNumberIndent, indexLineWrapIndent);
            printFile.writeBlankLines(1, 0);
        }

    }

    private void outputBrackedKeyNode(PrintFile printFile, BracketedKeyNode node, boolean displayCharacterNumbers, int indexNumberIndent, int indexLineWrapIndent) {
        int outputWidth = _context.getOutputFileManager().getOutputWidth();

        // An index contains an "entry" for each group of attributes
        for (int i = 0; i < node.getNumberOfLines(); i++) {
            StringBuilder entryBuilder = new StringBuilder();

            if (i == 0) {
                entryBuilder.append(node.getNodeNumber());
                entryBuilder.append("(").append(node.getBackReference()).append(")").append(".");
                entryBuilder.append(" ");
            } else {
                entryBuilder.append(StringUtils.repeat(" ", indexNumberIndent));
            }

            List<MultiStateAttribute> attrs = node.getAttributesForLine(i);

            for (int j = 0; j < attrs.size(); j++) {
                MultiStateAttribute attr = attrs.get(j);
                if (displayCharacterNumbers) {
                    entryBuilder.append("(");
                    entryBuilder.append(attr.getCharacter().getCharacterId());
                    entryBuilder.append(") ");
                }

                entryBuilder.append(_charFormatter.formatCharacterDescription(attr.getCharacter()));
                entryBuilder.append(" ");
                entryBuilder.append(_charFormatter.formatState(attr.getCharacter(), attr.getPresentStates().iterator().next()));

                // Don't put a space after the last attribute description
                if (j < attrs.size() - 1) {
                    entryBuilder.append(" ");
                }
            }

            Object itemListOrIndexNumber = node.getDestinationForLine(i);

            if (itemListOrIndexNumber instanceof List<?>) {
                // Index references one or more taxa
                StringBuilder taxonListBuilder = new StringBuilder();
                List<Item> taxa = (List<Item>) itemListOrIndexNumber;

                for (int k = 0; k < taxa.size(); k++) {
                    Item taxon = taxa.get(k);
                    String taxonDescription = _itemFormatter.formatItemDescription(taxon);

                    if (k == 0) {
                        printFile.outputStringPairWithPaddingCharacter(entryBuilder.toString(), " " + taxonDescription, '.');
                    } else {
                        printFile.outputLine(StringUtils.repeat(" ", outputWidth - taxonDescription.length()) + taxonDescription);

                    }

                }

            } else {
                // Index references another index
                int forwardReference = (Integer) itemListOrIndexNumber;
                String forwardReferenceAsString = Integer.toString(forwardReference);
                printFile.outputStringPairWithPaddingCharacter(entryBuilder.toString(), " " + forwardReferenceAsString, '.');
            }
        }
    }

    private void generateTypesetBracketedKey(BracketedKey bracketedKey, List<Character> includedCharacters, List<Item> includedItems, PrintFile typesetFile, boolean displayCharacterNumbers,
            boolean outputHtml, int numCharactersUsedInKey, int numTaxaUsedInKey, double avgLenKey, double avgCostKey, double maxLenKey, double maxCostKey) {
        StringBuilder typesetTextBuilder = new StringBuilder();

        // Output start of file
        TypeSettingMark startFileMark = _context.getTypeSettingMark(MarkPosition.START_OF_FILE);
        typesetTextBuilder.append(startFileMark.getMarkText());

        // Output any file heading text set using the PRINT COMMENT directive
        String headerText = _context.getTypeSettingFileHeaderText();

        if (headerText != null) {
            typesetTextBuilder.append(_context.getTypeSettingFileHeaderText());
        }

        // Output key parameters
        TypeSettingMark parametersMark = _context.getTypeSettingMark(MarkPosition.PARAMETERS);
        String parametersText = parametersMark.getMarkText();

        typesetTextBuilder.append(parametersText);

        for (BracketedKeyNode node : bracketedKey) {
            typesetTextBuilder.append(generateTypesetTextForBracketedKeyNode(node, outputHtml, displayCharacterNumbers));
        }

        TypeSettingMark endKeyMark = _context.getTypeSettingMark(MarkPosition.END_OF_KEY);
        typesetTextBuilder.append(endKeyMark.getMarkText());

        TypeSettingMark endFileMark = _context.getTypeSettingMark(MarkPosition.END_OF_FILE);
        typesetTextBuilder.append(endFileMark.getMarkText());

        String typesetText = typesetTextBuilder.toString();

        typesetText = typesetText.replaceAll("@nchar", Integer.toString(_context.getDataSet().getNumberOfCharacters()));
        typesetText = typesetText.replaceAll("@ncincl", Integer.toString(includedCharacters.size()));
        typesetText = typesetText.replaceAll("@ncinkey", Integer.toString(numCharactersUsedInKey));

        typesetText = typesetText.replaceAll("@ntaxa", Integer.toString(_context.getDataSet().getMaximumNumberOfItems()));
        typesetText = typesetText.replaceAll("@ntincl", Integer.toString(includedItems.size()));
        typesetText = typesetText.replaceAll("@ntinkey", Integer.toString(numTaxaUsedInKey));

        typesetText = typesetText.replaceAll("@rbase", formatDouble(_context.getRBase()));
        typesetText = typesetText.replaceAll("@abase", formatDouble(_context.getABase()));
        typesetText = typesetText.replaceAll("@reuse", formatDouble(_context.getReuse()));
        typesetText = typesetText.replaceAll("@varywt", formatDouble(_context.getVaryWt()));

        typesetText = typesetText.replaceAll("@nconf", Integer.toString(_context.getNumberOfConfirmatoryCharacters()));
        typesetText = typesetText.replaceAll("@avglen", formatDouble(avgLenKey));
        typesetText = typesetText.replaceAll("@avgcost", formatDouble(avgCostKey));
        typesetText = typesetText.replaceAll("@maxlen", formatDouble(maxLenKey));
        typesetText = typesetText.replaceAll("@maxcost", formatDouble(maxCostKey));

        List<Integer> includedCharacterNumbers = new ArrayList<Integer>();
        for (Character ch : includedCharacters) {
            includedCharacterNumbers.add(ch.getCharacterId());
        }
        List<Integer> includedItemNumbers = new ArrayList<Integer>();
        for (Item it : includedItems) {
            includedItemNumbers.add(it.getItemNumber());
        }

        // any backslashes that may occur in rangeSymbol need to be escaped
        // otherwise they will be omitted when we do
        // a String.replaceAll
        String rangeSymbol = Matcher.quoteReplacement(_context.getTypeSettingMark(MarkPosition.RANGE_SYMBOL).getMarkText());

        typesetText = typesetText.replaceAll("@cmask", Utils.formatIntegersAsListOfRanges(includedCharacterNumbers, rangeSymbol));
        typesetText = typesetText.replaceAll("@rel", KeyUtils.formatCharacterReliabilities(_context, ",", rangeSymbol));
        typesetText = typesetText.replaceAll("@tmask", Utils.formatIntegersAsListOfRanges(includedItemNumbers, rangeSymbol));
        typesetText = typesetText.replaceAll("@tabund", KeyUtils.formatCharacterReliabilities(_context, ",", rangeSymbol));
        typesetText = typesetText.replaceAll("@preset", KeyUtils.formatPresetCharacters(_context));
        // @preset - Preset characters.

        typesetFile.outputLine(typesetText);
    }

    private String generateTypesetTextForBracketedKeyNode(BracketedKeyNode node, boolean outputHtml, boolean displayCharacterNumbers) {
        // @node - Node number.
        // @from - Previous node.
        // @to - Next node.
        // @state - feature/state text.
        // @nrow - number of "destinations" for the current node.
        CharacterFormatter typesetCharFormatter = new CharacterFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, false, false);
        typesetCharFormatter.setRtfToHtml(outputHtml);
        ItemFormatter typesetItemFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, false, false, false);
        typesetItemFormatter.setRtfToHtml(outputHtml);

        // Need to count the number of destinations, as multiple taxa identified
        // by the one line are counted as distinct destinations.
        int numDestinations = 0;

        StringBuilder nodeTextBuilder = new StringBuilder();

        TypeSettingMark firstLineMark;

        if (node.getNodeNumber() == 1) {
            firstLineMark = _context.getTypeSettingMark(MarkPosition.FIRST_LEAD_OF_FIRST_NODE);
        } else {
            firstLineMark = _context.getTypeSettingMark(MarkPosition.FIRST_LEAD_OF_NODE);
        }

        TypeSettingMark subsequentLineMark = _context.getTypeSettingMark(MarkPosition.SUBSEQUENT_LEAD_OF_NODE);

        TypeSettingMark firstTaxonDestinationMark = _context.getTypeSettingMark(MarkPosition.FIRST_DESTINATION_OF_LEAD);
        TypeSettingMark subsequentTaxonDestinationMark = _context.getTypeSettingMark(MarkPosition.SUBSEQUENT_DESTINATION_OF_LEAD);
        TypeSettingMark afterTaxonNamesMark = _context.getTypeSettingMark(MarkPosition.AFTER_TAXON_NAME);

        TypeSettingMark nodeNumberDestinationMark = _context.getTypeSettingMark(MarkPosition.DESTINATION_OF_LEAD_NODE);

        TypeSettingMark afterNodeMark = _context.getTypeSettingMark(MarkPosition.AFTER_NODE);

        for (int i = 0; i < node.getNumberOfLines(); i++) {
            StringBuilder lineTextBuilder = new StringBuilder();
            List<MultiStateAttribute> attributesForLine = node.getAttributesForLine(i);
            Object destinationForLine = node.getDestinationForLine(i);

            StringBuilder attributesTextBuilder = new StringBuilder();
            for (int j = 0; j < attributesForLine.size(); j++) {
                MultiStateAttribute attr = attributesForLine.get(j);
                if (displayCharacterNumbers) {
                    attributesTextBuilder.append("(");
                    attributesTextBuilder.append(attr.getCharacter().getCharacterId());
                    attributesTextBuilder.append(") ");
                }

                attributesTextBuilder.append(_charFormatter.formatCharacterDescription(attr.getCharacter()));
                attributesTextBuilder.append(" ");
                attributesTextBuilder.append(_charFormatter.formatState(attr.getCharacter(), attr.getPresentStates().iterator().next()));

                // Don't put a semicolon/space after the last attribute
                // description
                if (j < attributesForLine.size() - 1) {
                    attributesTextBuilder.append("; ");
                }
            }

            if (i == 0) {
                lineTextBuilder.append(firstLineMark.getMarkText());
            } else {
                lineTextBuilder.append(subsequentLineMark.getMarkText());
            }

            if (destinationForLine instanceof Integer) {
                int intDestination = (Integer) destinationForLine;

                String destinationText = nodeNumberDestinationMark.getMarkText();
                destinationText = destinationText.replaceAll("@to", Integer.toString(intDestination));
                lineTextBuilder.append(destinationText);
                numDestinations++;
            } else {
                List<Item> destinationTaxa = (List<Item>) destinationForLine;

                for (int j = 0; j < destinationTaxa.size(); j++) {
                    numDestinations++;
                    Item taxon = destinationTaxa.get(j);
                    String formattedTaxonDescription = typesetItemFormatter.formatItemDescription(taxon);

                    // Any backslashes in taxon description (from RTF
                    // formatting) need to be escaped,
                    // otherwise they will be omitted when we do a
                    // String.replaceAll
                    formattedTaxonDescription = Matcher.quoteReplacement(formattedTaxonDescription);

                    String destinationText;
                    if (j == 0) {
                        destinationText = firstTaxonDestinationMark.getMarkText();
                    } else {
                        destinationText = subsequentTaxonDestinationMark.getMarkText();
                    }
                    destinationText = destinationText.replaceAll("@to", formattedTaxonDescription);

                    lineTextBuilder.append(destinationText);
                }
                lineTextBuilder.append(afterTaxonNamesMark.getMarkText());
            }

            String lineText = lineTextBuilder.toString();
            lineText = lineText.replaceAll("@state", attributesTextBuilder.toString());
            nodeTextBuilder.append(lineText);
        }

        nodeTextBuilder.append(afterNodeMark.getMarkText());

        String nodeText = nodeTextBuilder.toString();
        nodeText = nodeText.replaceAll("@node", Integer.toString(node.getNodeNumber()));
        nodeText = nodeText.replaceAll("@from", Integer.toString(node.getBackReference()));
        nodeText = nodeText.replaceAll("@nrow", Integer.toString(numDestinations));

        return nodeText;
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
    public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) throws DirectiveException {
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

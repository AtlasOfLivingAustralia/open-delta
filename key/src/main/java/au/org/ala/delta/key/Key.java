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
import java.util.Calendar;
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
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

public class Key implements DirectiveParserObserver {

    private static final String DEFAULT_TYPESETTING_FILE_EXTENSION = ".rtf";
    private static final String DEFAULT_OUTPUT_FILE_EXTENSION = ".prt";

    private KeyContext _context;
    private boolean _inputFilesRead = false;
    private PrintFile _listingPrintFile;
    
    private SimpleDateFormat _timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat _dateFormat = new SimpleDateFormat("d-MMM-yyyy");

    private ConforDirectiveParserObserver _nestedObserver;

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

        // Set default names for output files if their names were not set in the
        // directives file
        KeyOutputFileManager outputFileManager = _context.getOutputFileManager();
        if (outputFileManager.getOutputFile() == null) {
            try {
                outputFileManager.setOutputFileName(FilenameUtils.getBaseName(directivesFile.getName()) + DEFAULT_OUTPUT_FILE_EXTENSION);
            } catch (Exception ex) {
                throw new RuntimeException("Error creating output file", ex);
            }
        }

        if (outputFileManager.getTypesettingFile() == null) {
            try {
                outputFileManager.setTypesettingFileName(FilenameUtils.getBaseName(directivesFile.getName()) + DEFAULT_TYPESETTING_FILE_EXTENSION);
            } catch (Exception ex) {
                throw new RuntimeException("Error creating typesetting file", ex);
            }
        }

        initializeListingPrintFile();

        if (parseSuccessful) {
            readInputFiles();

            Specimen specimen = new Specimen(_context.getDataSet(), true, true, MatchType.OVERLAP);
            List<Pair<Item, List<Attribute>>> keyList = new ArrayList<Pair<Item, List<Attribute>>>();

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

            doCalculateKey(dataset, includedCharacters, includedItems, specimen, keyList);
            System.out.println("Key generation completed");
            printKey(includedCharacters, includedItems, keyList);
        }
        
        _nestedObserver.finishedProcessing();
    }

    private void printKeyDataAndKeyDetails(List<Character> includedCharacters, List<Item> includedItems, List<Pair<Item, List<Attribute>>> keyList) {

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

    private void doCalculateKey(FilteredDataSet dataset, List<Character> includedCharacters, List<Item> includedItems, Specimen specimen, List<Pair<Item, List<Attribute>>> keyList) {

        Set<Item> specimenAvailableTaxa = getSpecimenAvailableTaxa(specimen, includedItems);
        Set<Character> specimenAvailableCharacters = getSpecimenAvailableCharacters(specimen, includedCharacters);

        if (specimenAvailableTaxa.size() == 0) {
            return;
        } else if (specimenAvailableTaxa.size() == 1) {
            List<Attribute> attrList = new ArrayList<Attribute>();
            for (Character ch : specimen.getUsedCharacters()) {
                attrList.add(specimen.getAttributeForCharacter(ch));
            }
            Pair<Item, List<Attribute>> pair = new Pair<Item, List<Attribute>>(specimenAvailableTaxa.iterator().next(), attrList);
            keyList.add(pair);
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

            LinkedHashMap<Character, Double> bestMap = KeyBest.orderBest(_context.getDataSet(), specimenAvailableCharacterNumbers, specimenAvailableTaxaNumbers, _context.getRBase(),
                    _context.getVaryWt());

            List<Character> bestOrderCharacters = new ArrayList<Character>(bestMap.keySet());

            if (!bestOrderCharacters.isEmpty()) {
                // KEY only uses multi state characters
                MultiStateCharacter bestCharacter = (MultiStateCharacter) bestOrderCharacters.get(0);

                // System.out.println(String.format("%s %s",
                // bestMap.get(bestCharacter), bestCharacter.getCharacterId()));
                // System.out.println("Available characters: " +
                // specimenAvailableCharacterNumbers.size());
                // System.out.println("Available taxa: " +
                // specimenAvailableTaxaNumbers.size());
                // System.out.println();
                // for (au.org.ala.delta.model.Character ch : bestMap.keySet())
                // {
                // double sepPower = bestMap.get(ch);
                // System.out.println(String.format("%s %s (%s)", sepPower, ch,
                // ch.getReliability()));
                // }
                // System.out.println();

                for (int i = 0; i < bestCharacter.getNumberOfStates(); i++) {
                    int stateNumber = i + 1;

                    SimpleAttributeData impl = new SimpleAttributeData(false, false);
                    MultiStateAttribute attr = (MultiStateAttribute) AttributeFactory.newAttribute(bestCharacter, impl);
                    Set<Integer> presentStatesSet = new HashSet<Integer>();
                    presentStatesSet.add(stateNumber);
                    attr.setPresentStates(presentStatesSet);

                    // System.out.println("Setting attribute " +
                    // attr.toString());
                    specimen.setAttributeForCharacter(bestCharacter, attr);

                    // System.out.println("Used characters: ");
                    // List<Attribute> attrList = new ArrayList<Attribute>();
                    // for (Character ch : specimen.getUsedCharacters()) {
                    // System.out.println(specimen.getAttributeForCharacter(ch));
                    // }
                    // System.out.println("Remaining taxa: " +
                    // getSpecimenAvailableTaxa(specimen).toString());

                    doCalculateKey(dataset, includedCharacters, includedItems, specimen, keyList);

                    specimen.removeValueForCharacter(bestCharacter);
                }
            }
        }
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

    private void initializeListingPrintFile() {
        KeyOutputFileManager outputFileManager = _context.getOutputFileManager();
        int outputWidth = outputFileManager.getOutputWidth();

        _listingPrintFile = outputFileManager.getPrintFile();
        if (_listingPrintFile == null) {
            _listingPrintFile = new PrintFile(System.out, outputWidth);
        } else {
            // Only output the credits when writing to a listing file. They have
            // already been written to
            // standard out.
            _listingPrintFile.outputLine(generateCreditsString());
            _listingPrintFile.writeBlankLines(1, 0);
        }

    }
    
    private void generateKeyOutput(List<Character> includedCharacters, List<Item> includedItems, List<Pair<Item, List<Attribute>>> keyList, PrintFile printFile) {
        DeltaDataSet dataset = _context.getDataSet();
        long datasetGenerationDate = _context.getCharactersFile().lastModified();
        printFile.outputLine(MessageFormat.format("{0}. Data converted {1} {2}", dataset.getName(), _timeFormat.format(datasetGenerationDate), _dateFormat.format(datasetGenerationDate)));
        printFile.outputLine(StringUtils.repeat("*", _context.getOutputFileSelector().getOutputWidth()));
    }
    
    private void generateListingOutput() {
        
    }
    
    private void generateTypesettingOutput() {
        
    }

    private void printOutputFileHeader(List<Character> includedCharacters, List<Item> includedItems, List<Pair<Item, List<Attribute>>> keyList, PrintFile printFile) {

        printFile.outputLine(generateCreditsString());
        printFile.writeBlankLines(1, 0);



        Date currentDate = Calendar.getInstance().getTime();

        printFile.outputLine(MessageFormat.format("Run at {0} on {1}", _timeFormat.format(currentDate), _dateFormat.format(currentDate)));
        printFile.writeBlankLines(1, 0);

        Set<Character> charactersInKey = new HashSet<Character>();
        Set<Item> itemsInKey = new HashSet<Item>();

        for (Pair<Item, List<Attribute>> pair : keyList) {
            Item it = pair.getFirst();
            itemsInKey.add(it);
            List<Attribute> attrs = pair.getSecond();
            for (Attribute attr : attrs) {
                charactersInKey.add(attr.getCharacter());
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
    }

    private void printTabularKey(List<Pair<Item, List<Attribute>>> keyList, PrintFile printFile) {
        ItemFormatter itemFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, false);

        // Do a first pass of the data structure to get the counts for the
        // number of times a taxon appears in the key, and to work out how wide
        // the cells need to be

        Map<Item, Integer> itemOccurrences = new HashMap<Item, Integer>();
        int cellWidth = 0;
        for (Pair<Item, List<Attribute>> pair : keyList) {
            Item it = pair.getFirst();
            List<Attribute> attrs = pair.getSecond();

            if (itemOccurrences.containsKey(it)) {
                int currentItemCount = itemOccurrences.get(it);
                itemOccurrences.put(it, currentItemCount + 1);
            } else {
                itemOccurrences.put(it, 1);
            }

            for (Attribute attr : attrs) {
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

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < keyList.size(); i++) {
            List<Attribute> previousRowAttributes = null;

            if (i > 0) {
                previousRowAttributes = keyList.get(i - 1).getSecond();
            }

            Pair<Item, List<Attribute>> currentRow = keyList.get(i);

            Item rowItem = currentRow.getFirst();
            List<Attribute> rowAttributes = currentRow.getSecond();

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

            builder.append("|");
            String formattedItemName = itemFormatter.formatItemDescription(rowItem);
            builder.append(formattedItemName);

            int numItemOccurrences = itemOccurrences.get(rowItem);

            if (numItemOccurrences > 1) {
                builder.append(StringUtils.repeat(" ", 27 - formattedItemName.length() - Integer.toString(numItemOccurrences).length()));
                builder.append(numItemOccurrences);
            } else {
                builder.append(StringUtils.repeat(" ", 27 - formattedItemName.length()));
            }

            builder.append("|");

            for (Attribute attr : rowAttributes) {
                MultiStateAttribute msAttr = (MultiStateAttribute) attr;
                int characterId = msAttr.getCharacter().getCharacterId();

                // Insert spaces to pad out the cell if the character id + state
                // value are not as wide as the cell width
                builder.append(StringUtils.repeat(" ", cellWidth - (Integer.toString(characterId).length() + 1)));

                builder.append(characterId);

                // Only 1 state will be ever set - the key generation algorithm
                // only sets
                // Individual states for characters
                int stateNumber = msAttr.getPresentStates().iterator().next();
                // Convert state numbers to "A", "B", "C" etc
                builder.append((char) (64 + stateNumber));
                builder.append("|");
            }

            builder.append("\n");

            // If this is the last row, need to print the bottom edge of the
            // table
            if (i == keyList.size() - 1) {
                builder.append("+---------------------------+");
                for (int l = 0; l < rowAttributes.size(); l++) {
                    builder.append(StringUtils.repeat("-", cellWidth));
                    builder.append("+");
                }
            }
        }

        printFile.outputLine(builder.toString());
    }

    private void printBracketedKey(List<Pair<Item, List<Attribute>>> keyList, boolean displayCharacterNumbers, PrintFile printFile) {
        CharacterFormatter charFormatter = new CharacterFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false);
        ItemFormatter itemFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, false);

        List<MultiStateCharacter> orderedCharacters = new ArrayList<MultiStateCharacter>();
        Map<MultiStateCharacter, Integer> characterIndices = new HashMap<MultiStateCharacter, Integer>();

        int currentCharacterIndex = 1;

        for (Pair<Item, List<Attribute>> pair : keyList) {
            List<Attribute> attrs = pair.getSecond();
            for (Attribute attr : attrs) {
                MultiStateCharacter ch = (MultiStateCharacter) attr.getCharacter();
                if (!characterIndices.containsKey(ch)) {
                    characterIndices.put(ch, currentCharacterIndex);
                    orderedCharacters.add(ch);
                    currentCharacterIndex++;
                }
            }
        }

        Map<Pair<Character, Integer>, Object> keyMap = new HashMap<Pair<Character, Integer>, Object>();
        for (Pair<Item, List<Attribute>> itemAttrsPair : keyList) {
            Item it = itemAttrsPair.getFirst();
            List<Attribute> attrs = itemAttrsPair.getSecond();

            for (int i = 0; i < attrs.size(); i++) {
                MultiStateAttribute currentAttr = (MultiStateAttribute) attrs.get(i);
                MultiStateAttribute nextAttr = null;

                if (i < attrs.size() - 1) {
                    nextAttr = (MultiStateAttribute) attrs.get(i + 1);
                }

                Pair<Character, Integer> charStateNumberPair = new Pair<Character, Integer>(currentAttr.getCharacter(), currentAttr.getPresentStatesAsList().get(0));

                if (nextAttr == null) {
                    keyMap.put(charStateNumberPair, it);
                } else {
                    keyMap.put(charStateNumberPair, nextAttr.getCharacter());
                }
            }
        }

        int orderedCharacterNumber = 1;

        Map<MultiStateCharacter, Integer> sourceNodeNumbers = new HashMap<MultiStateCharacter, Integer>();
        for (MultiStateCharacter ch : orderedCharacters) {
            boolean nodeNumberingDisplayed = false;
            for (int j = 1; j <= ch.getNumberOfStates(); j++) {
                StringBuilder builder = new StringBuilder();

                Pair<Character, Integer> charStateNumberPair = new Pair<Character, Integer>(ch, j);
                Object charOrItem = keyMap.get(charStateNumberPair);
                if (charOrItem != null) {
                    if (!nodeNumberingDisplayed) {
                        builder.append(orderedCharacterNumber);
                        builder.append("(");
                        builder.append(sourceNodeNumbers.containsKey(ch) ? sourceNodeNumbers.get(ch) : 0);
                        builder.append(").");
                        nodeNumberingDisplayed = true;
                    }

                    builder.append(StringUtils.repeat(" ", 10 - builder.toString().trim().length()));

                    String descriptionText;
                    if (displayCharacterNumbers) {
                        descriptionText = String.format("(%d) %s %s", ch.getCharacterId(), charFormatter.formatCharacterDescription(ch), charFormatter.formatState(ch, j));
                    } else {
                        descriptionText = String.format("%s %s", charFormatter.formatCharacterDescription(ch), charFormatter.formatState(ch, j));
                    }

                    builder.append(Utils.capitaliseFirstWord(descriptionText));

                    if (charOrItem instanceof Item) {
                        String itemDescription = itemFormatter.formatItemDescription((Item) charOrItem);
                        builder.append(StringUtils.repeat(".", 78 - builder.toString().length() - itemDescription.length() - 1));
                        builder.append(" ");
                        builder.append(itemDescription);
                    } else {
                        MultiStateCharacter nextNodeCharacter = (MultiStateCharacter) charOrItem;
                        int nextNodeCharacterIndex = characterIndices.get((nextNodeCharacter));
                        builder.append(StringUtils.repeat(".", 78 - builder.toString().length() - Integer.toString(nextNodeCharacterIndex).length() - 1));
                        builder.append(" ");
                        builder.append(nextNodeCharacterIndex);
                        sourceNodeNumbers.put(nextNodeCharacter, orderedCharacterNumber);
                    }

                    printFile.outputLine(builder.toString());
                }
            }
            orderedCharacterNumber++;

            printFile.outputLine(getNewLine());
        }
    }

    private void printKey(List<Character> includedCharacters, List<Item> includedItems, List<Pair<Item, List<Attribute>>> keyList) {
        if (_context.getDisplayTabularKey()) {
            PrintFile printFile = _context.getOutputFileSelector().getOutputFile();
            generateKeyOutput(includedCharacters, includedItems, keyList, printFile);
            printOutputFileHeader(includedCharacters, includedItems, keyList, printFile);
            printTabularKey(keyList, printFile);
            System.out.println("Tabular key completed");
        }

        if (_context.getDisplayBracketedKey()) {
            PrintFile printFile;
            Map<Integer, TypeSettingMark> typesettingMarksMap = _context.getTypeSettingMarks();
            if (typesettingMarksMap == null || typesettingMarksMap.isEmpty()) {
                // TYPESETTING MARKS directive has not been called, so bracketed
                // key
                // should be output to the same file as the tabular key
                printFile = _context.getOutputFileSelector().getOutputFile();
                if (_context.getDisplayTabularKey()) {
                    printFile.writeBlankLines(2, 0);
                }
            } else {
                // TYPESETTING MARKS directive has not been called, so bracketed
                // key
                // should be output to the file specified by the
                // KEY TYPESETTING FILE directive
                printFile = _context.getOutputFileManager().getTypesettingFile();

                // should header go here???
                // printHeader(includedCharacters, includedItems, keyList,
                // printFile);
            }

            printBracketedKey(keyList, _context.getAddCharacterNumbers(), printFile);
            System.out.println("Bracketed key completed");
        }
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

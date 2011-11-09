package au.org.ala.delta.key;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.Logger;
import au.org.ala.delta.best.Best;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.key.directives.KeyDirectiveFileParser;
import au.org.ala.delta.key.directives.io.KeyCharactersFileReader;
import au.org.ala.delta.key.directives.io.KeyItemsFileReader;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.AttributeFactory;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.impl.SimpleAttributeData;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

public class Key {

    private KeyContext _context;

    /**
     * @param args
     *            specifies the name of the input file to use.
     */
    public static void main(String[] args) throws Exception {

        StringBuilder credits = new StringBuilder("KEY version 2.12 (Java)");
        credits.append("\n\nM. J. Dallwitz, T.A. Paine");
        credits.append("\n\nCSIRO Division of Entomology, GPO Box 1700, Canberra, ACT 2601, Australia\nPhone +61 2 6246 4075. Fax +61 2 6246 4000. Email delta@ento.csiro.au");
        credits.append("\n\nJava edition ported by the Atlas of Living Australia, 2011.\n");

        System.out.println(credits);

        File f = handleArgs(args);
        if (!f.exists()) {
            Logger.log("File %s does not exist!", f.getName());
            return;
        }

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

    public void calculateKey(File directivesFile) {
        _context = new KeyContext();
        _context.setDataDirectory(directivesFile.getParentFile());

        try {
            processDirectivesFile(directivesFile, _context);
        } catch (Exception ex) {
            System.out.println("Error parsing directive file");
            ex.printStackTrace();
        }

        File charactersFile = Utils.createFileFromPath(_context.getCharactersFilePath(), _context.getDataDirectory());
        File itemsFile = Utils.createFileFromPath(_context.getItemsFilePath(), _context.getDataDirectory());

        BinaryKeyFile keyCharactersFile = new BinaryKeyFile(charactersFile.getAbsolutePath(), BinFileMode.FM_READONLY);
        BinaryKeyFile keyItemsFile = new BinaryKeyFile(itemsFile.getAbsolutePath(), BinFileMode.FM_READONLY);

        KeyCharactersFileReader keyCharactersFileReader = new KeyCharactersFileReader(_context.getDataSet(), keyCharactersFile);
        keyCharactersFileReader.createCharacters();

        KeyItemsFileReader keyItemsFileReader = new KeyItemsFileReader(_context, _context.getDataSet(), keyItemsFile);
        keyItemsFileReader.readAll();

//        Specimen specimen = new Specimen(_context.getDataSet(), false, false, MatchType.EXACT);
//        List<Pair<Item, List<Attribute>>> keyList = new ArrayList<Pair<Item, List<Attribute>>>();
//
//        doCalculateKey(specimen, keyList);
//
//        for (Pair<Item, List<Attribute>> pair : keyList) {
//            System.out.println(pair);
//        }
    }

    private void doCalculateKey(Specimen specimen, List<Pair<Item, List<Attribute>>> keyList) {

        List<Item> specimenAvailableTaxa = getSpecimenAvailableTaxa(specimen);
        List<Character> specimenAvailableCharacters = getSpecimenAvailableCharacters(specimen);

        if (specimenAvailableTaxa.size() == 0) {
            return;
        } else if (specimenAvailableTaxa.size() == 1) {
            List<Attribute> attrList = new ArrayList<Attribute>();
            for (Character ch : specimen.getUsedCharacters()) {
                attrList.add(specimen.getAttributeForCharacter(ch));
            }

            Pair<Item, List<Attribute>> pair = new Pair<Item, List<Attribute>>(specimenAvailableTaxa.get(0), attrList);
            keyList.add(pair);
            System.out.println(pair);
        } else {
            List<Integer> specimenAvailableCharacterNumbers = new ArrayList<Integer>();
            for (Character ch: specimenAvailableCharacters) {
                specimenAvailableCharacterNumbers.add(ch.getCharacterId());
            }
            
            List<Integer> specimenAvailableTaxaNumbers = new ArrayList<Integer>();
            for (Item item: specimenAvailableTaxa) {
                specimenAvailableTaxaNumbers.add(item.getItemNumber());
            }
            
            LinkedHashMap<Character, Double> bestMap = Best.orderBest(_context.getDataSet(), specimenAvailableCharacterNumbers, specimenAvailableTaxaNumbers, _context.getRBase(), _context.getVaryWt());

            List<Character> bestOrderCharacters = new ArrayList<Character>(bestMap.keySet());

            if (!bestOrderCharacters.isEmpty()) {
                // KEY only uses multi state characters
                MultiStateCharacter bestCharacter = (MultiStateCharacter) bestOrderCharacters.get(0);

                for (int i = 0; i < bestCharacter.getNumberOfStates(); i++) {
                    int stateNumber = i + 1;

                    SimpleAttributeData impl = new SimpleAttributeData(false, false);
                    MultiStateAttribute attr = (MultiStateAttribute) AttributeFactory.newAttribute(bestCharacter, impl);
                    Set<Integer> presentStatesSet = new HashSet<Integer>();
                    presentStatesSet.add(stateNumber);
                    attr.setPresentStates(presentStatesSet);

                    specimen.setAttributeForCharacter(bestCharacter, attr);

                    doCalculateKey(specimen, keyList);

                    specimen.removeValueForCharacter(bestCharacter);
                }
            }
        }
    }

    private List<Item> getSpecimenAvailableTaxa(Specimen specimen) {
        List<Integer> includedItemNumbers = _context.getIncludedItems();
        List<Item> availableTaxa = _context.getDataSet().getItemsAsList();

        List<Item> includedTaxa = new ArrayList<Item>();
        for (int includedItemNumber : includedItemNumbers) {
            includedTaxa.add(_context.getDataSet().getItem(includedItemNumber));
        }

        availableTaxa.retainAll(includedTaxa);

        for (Item item : specimen.getTaxonDifferences().keySet()) {
            Set<Character> differingCharacters = specimen.getTaxonDifferences().get(item);
            if (!differingCharacters.isEmpty()) {
                availableTaxa.remove(item);
            }
        }

        return availableTaxa;
    }
    
    private List<Character> getSpecimenAvailableCharacters(Specimen specimen) {
        List<Integer> includedCharNumbers = _context.getIncludedCharacters();
        List<Character> availableChars = _context.getDataSet().getCharactersAsList();

        List<Character> includedChars = new ArrayList<Character>();
        for (int includedCharNumber : includedCharNumbers) {
            includedChars.add(_context.getDataSet().getCharacter(includedCharNumber));
        }

        availableChars.retainAll(includedChars);

        availableChars.removeAll(specimen.getUsedCharacters());

        return availableChars;
    }

    private void processDirectivesFile(File input, KeyContext context) throws Exception {
        KeyDirectiveFileParser parser = KeyDirectiveFileParser.createInstance();
        parser.parse(input, context);
    }

    public KeyContext getContext() {
        return _context;
    }

}

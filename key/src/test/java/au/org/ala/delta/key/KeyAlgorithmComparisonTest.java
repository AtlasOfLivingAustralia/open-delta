package au.org.ala.delta.key;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.lang.math.IntRange;
import org.junit.Test;

import au.org.ala.delta.best.Best;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.key.directives.io.KeyCharactersFileReader;
import au.org.ala.delta.key.directives.io.KeyItemsFileReader;
import au.org.ala.delta.model.AttributeFactory;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MatchType;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.model.impl.SimpleAttributeData;
import au.org.ala.delta.translation.FilteredCharacter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.IncludeExcludeDataSetFilter;

public class KeyAlgorithmComparisonTest extends TestCase {

    @Test
    public void testDoTest() throws Exception {
        KeyContext context = new KeyContext(File.createTempFile("foo", "bar"));
        context.setABase(1.0);
        context.setVaryWt(1.0);
        context.setRBase(1.0);
        context.setReuse(5.0);
        
//        BinaryKeyFile keyCharactersFile = new BinaryKeyFile("C:\\Users\\ChrisF\\Virtualbox Shared Folder\\Robin2\\kchars", BinFileMode.FM_READONLY);
//        BinaryKeyFile keyItemsFile = new BinaryKeyFile("C:\\Users\\ChrisF\\Virtualbox Shared Folder\\Robin2\\kitems", BinFileMode.FM_READONLY);

        
        BinaryKeyFile keyCharactersFile = new BinaryKeyFile("C:\\Users\\ChrisF\\eclipse-workspace\\delta-REACTOR\\key\\src\\test\\resources\\sample\\kchars", BinFileMode.FM_READONLY);
        BinaryKeyFile keyItemsFile = new BinaryKeyFile("C:\\Users\\ChrisF\\eclipse-workspace\\delta-REACTOR\\key\\src\\test\\resources\\sample\\kitems", BinFileMode.FM_READONLY);
//        
        KeyCharactersFileReader keyCharactersFileReader = new KeyCharactersFileReader(context, context.getDataSet(), keyCharactersFile);
        keyCharactersFileReader.createCharacters();

        KeyItemsFileReader keyItemsFileReader = new KeyItemsFileReader(context, context.getDataSet(), keyItemsFile);
        keyItemsFileReader.readAll();
        
        // Calculate character costs and item abundance values
        
        DeltaDataSet deltaDataset = context.getDataSet();
        
        for (int i = 0; i < deltaDataset.getNumberOfCharacters(); i++) {
            Character ch = deltaDataset.getCharacter(i + 1);
            double charCost = Math.pow(context.getRBase(), 5.0 - Math.min(10.0, ch.getReliability()));
            context.setCharacterCost(ch.getCharacterId(), charCost);
        }
        
        for (int i = 0; i < deltaDataset.getMaximumNumberOfItems(); i++) {
            Item taxon = deltaDataset.getItem(i + 1);
            double itemAbundanceValue = Math.pow(context.getABase(), context.getItemAbundancy(i + 1) - 5.0);
            context.setCalculatedItemAbundanceValue(taxon.getItemNumber(), itemAbundanceValue);
        }
        
        //Specimen specimen = new Specimen(context.getDataSet(), true, false, false, MatchType.OVERLAP);
//        MultiStateCharacter char260 = (MultiStateCharacter) context.getDataSet().getCharacter(260);
//        MultiStateCharacter char17 = (MultiStateCharacter) context.getDataSet().getCharacter(17);
//        
//        specimen.setAttributeForCharacter(char260, createMultiStateAttribute(char260, 2));
//        specimen.setAttributeForCharacter(char17, createMultiStateAttribute(char17, 3));
        
        int[] arrAvailableTaxa = new int[] {9, 13};
        List<Integer> availableTaxaNumbers = new ArrayList<Integer>();
        for (int taxNum: arrAvailableTaxa) {
            availableTaxaNumbers.add(taxNum);
        }
        
        FilteredDataSet dataset = new FilteredDataSet(context, new IncludeExcludeDataSetFilter(context));
        
        List<Integer> availableCharacterNumbers = new ArrayList<Integer>();
        Iterator<FilteredCharacter> iterFilteredCharacters = dataset.filteredCharacters();
        while (iterFilteredCharacters.hasNext()) {
            availableCharacterNumbers.add(iterFilteredCharacters.next().getCharacter().getCharacterId());
        }
        
        availableCharacterNumbers.remove(Integer.valueOf(78));
        availableCharacterNumbers.remove(Integer.valueOf(35));
//        availableCharacterNumbers.remove(Integer.valueOf(260));
////        availableTaxaNumbers.remove(Integer.valueOf(1));
////        availableTaxaNumbers.remove(Integer.valueOf(2));
////        availableTaxaNumbers.remove(Integer.valueOf(3));
////        availableTaxaNumbers.remove(Integer.valueOf(8));
        
        context.setCharacterCost(78, context.getCharacterCost(78) / context.getReuse());
        context.setCharacterCost(35, context.getCharacterCost(35) / context.getReuse());
        context.setCharacterCost(47, context.getCharacterCost(47) / context.getReuse());
        context.setCharacterCost(52, context.getCharacterCost(52) / context.getReuse());
        
        
        Map<Character, Double> bestMap = KeyBest.orderBest(context.getDataSet(), context.getCharacterCostsAsArray(), context.getCalculatedItemAbundanceValuesAsArray(), availableCharacterNumbers, availableTaxaNumbers, context.getRBase(), context.getABase(), context.getReuse(), context.getVaryWt());
        for(Character ch: bestMap.keySet()) {
            System.out.println(bestMap.get(ch) + " " + ch);
        }
        
        System.out.println("##################");
        
        Map<Character, Double> otherBestMap = OldKeyBest.orderBest(context.getDataSet(), availableCharacterNumbers, availableTaxaNumbers, 1.0, 1.0);
        for(Character ch: otherBestMap.keySet()) {
            System.out.println(otherBestMap.get(ch) + " " + ch);
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

}

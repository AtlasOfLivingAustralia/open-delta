package au.org.ala.delta.intkey.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;

import au.org.ala.delta.best.Best;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.util.Pair;

public class DiagnoseDirectiveTest extends IntkeyDatasetTestCase {

    @Test
    public void testDiagnose() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        
        //Item taxon1 = context.getDataset().getItem(1);
        Pair<List<Integer>, List<Integer>> availableStuff = getCharacterAndTaxonNumbersForBest(context);
        
        
        LinkedHashMap<Character, Double> bestMap = Best.orderDiagnose(1, context.getDiagType(), context.getStopBest(), context.getDataset(), availableStuff.getFirst(), availableStuff.getSecond(), context.getRBase(), context.getVaryWeight());
        
        for (Character ch : bestMap.keySet()) {
            System.out.println(String.format("%s  %s  %s", bestMap.get(ch), ch.getCharacterId(), ch.getDescription()));
        }
    }
    
    
    private Pair<List<Integer>, List<Integer>> getCharacterAndTaxonNumbersForBest(IntkeyContext context) {
        List<Integer> characterNumbers = new ArrayList<Integer>();
        List<Integer> taxonNumbers = new ArrayList<Integer>();

        List<Character> availableCharacters = context.getAvailableCharacters();
        availableCharacters.removeAll(context.getDataset().getCharactersToIgnoreForBest());

        for (Character ch : availableCharacters) {
            characterNumbers.add(ch.getCharacterId());
        }

        for (Item taxon : context.getAvailableTaxa()) {
            taxonNumbers.add(taxon.getItemNumber());
        }

        return new Pair<List<Integer>, List<Integer>>(characterNumbers, taxonNumbers);
    }
}

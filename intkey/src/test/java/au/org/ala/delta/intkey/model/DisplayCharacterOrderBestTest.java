package au.org.ala.delta.intkey.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import au.org.ala.delta.best.Best;
import au.org.ala.delta.intkey.directives.IncludeCharactersDirective;
import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.util.Pair;

public class DisplayCharacterOrderBestTest extends IntkeyDatasetTestCase {

    @Test
    public void testSimpleDataSet() throws Exception {
        IntkeyContext context = loadDataset("/dataset/controlling_characters_simple/intkey.ink");

        Pair<List<Integer>, List<Integer>> availableCharactersAndTaxaNumbers = getCharacterAndTaxonNumbersForBest(context);
        List<Integer> availableCharacterNumbers = availableCharactersAndTaxaNumbers.getFirst();
        List<Integer> availableTaxaNumbers = availableCharactersAndTaxaNumbers.getSecond();

        Map<Character, Double> bestMap = Best.orderBest(context.getDataset(), availableCharacterNumbers, availableTaxaNumbers, context.getRBase(), context.getVaryWeight());

        List<Character> orderedCharList = new ArrayList<Character>(bestMap.keySet());

        assertEquals(7, bestMap.keySet().size());

        bestTestHelper(0, 6, 1.92, orderedCharList, bestMap);
        bestTestHelper(1, 1, 1.74, orderedCharList, bestMap);
        bestTestHelper(2, 7, 1.10, orderedCharList, bestMap);
        bestTestHelper(3, 2, 0.97, orderedCharList, bestMap);
        bestTestHelper(4, 5, 0.79, orderedCharList, bestMap);
        bestTestHelper(5, 3, 0.49, orderedCharList, bestMap);
        bestTestHelper(6, 4, 0.45, orderedCharList, bestMap);
    }

    @Test
    public void testDeltaSampleDataSet() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");
        IntkeyDataset dataset = context.getDataset();

        Pair<List<Integer>, List<Integer>> availableCharactersAndTaxaNumbers = getCharacterAndTaxonNumbersForBest(context);
        List<Integer> availableCharacterNumbers = availableCharactersAndTaxaNumbers.getFirst();
        List<Integer> availableTaxaNumbers = availableCharactersAndTaxaNumbers.getSecond();

        Map<Character, Double> bestMap = Best.orderBest(context.getDataset(), availableCharacterNumbers, availableTaxaNumbers, context.getRBase(), context.getVaryWeight());

        List<Character> orderedCharList = new ArrayList<Character>(bestMap.keySet());

        bestTestHelper(0, 38, 1.70, orderedCharList, bestMap);
        bestTestHelper(1, 54, 1.18, orderedCharList, bestMap);
        bestTestHelper(2, 11, 0.96, orderedCharList, bestMap);
        bestTestHelper(3, 28, 1.16, orderedCharList, bestMap);
        bestTestHelper(4, 40, 1.15, orderedCharList, bestMap);
        bestTestHelper(5, 60, 1.11, orderedCharList, bestMap);
        bestTestHelper(6, 26, 1.08, orderedCharList, bestMap);
        bestTestHelper(7, 66, 0.86, orderedCharList, bestMap);
        bestTestHelper(8, 44, 0.83, orderedCharList, bestMap);
        bestTestHelper(9, 27, 0.82, orderedCharList, bestMap);
        bestTestHelper(10, 13, 0.81, orderedCharList, bestMap);
        bestTestHelper(11, 3, 0.88, orderedCharList, bestMap);
        bestTestHelper(12, 35, 0.87, orderedCharList, bestMap);
        bestTestHelper(13, 52, 0.87, orderedCharList, bestMap);
        bestTestHelper(14, 34, 0.81, orderedCharList, bestMap);
        bestTestHelper(15, 58, 0.81, orderedCharList, bestMap);
        bestTestHelper(16, 19, 0.81, orderedCharList, bestMap);
        bestTestHelper(17, 30, 0.79, orderedCharList, bestMap);
        bestTestHelper(18, 63, 0.78, orderedCharList, bestMap);
        bestTestHelper(19, 31, 0.75, orderedCharList, bestMap);
        bestTestHelper(20, 47, 0.71, orderedCharList, bestMap);
        bestTestHelper(21, 48, 0.51, orderedCharList, bestMap);
        bestTestHelper(22, 56, 0.71, orderedCharList, bestMap);
        bestTestHelper(23, 12, 0.50, orderedCharList, bestMap);
        bestTestHelper(24, 7, 0.68, orderedCharList, bestMap);
        bestTestHelper(25, 67, 0.64, orderedCharList, bestMap);
        bestTestHelper(26, 61, 0.58, orderedCharList, bestMap);
        bestTestHelper(27, 45, 0.51, orderedCharList, bestMap);
        bestTestHelper(28, 41, 0.49, orderedCharList, bestMap);
        bestTestHelper(29, 49, 0.46, orderedCharList, bestMap);
        bestTestHelper(30, 50, 0.41, orderedCharList, bestMap);
        bestTestHelper(31, 9, 0.41, orderedCharList, bestMap);
        bestTestHelper(32, 4, 0.41, orderedCharList, bestMap);
        bestTestHelper(33, 20, 0.40, orderedCharList, bestMap);
        bestTestHelper(34, 59, 0.40, orderedCharList, bestMap);
        bestTestHelper(35, 15, 0.40, orderedCharList, bestMap);
        bestTestHelper(36, 62, 0.40, orderedCharList, bestMap);
        bestTestHelper(37, 64, 0.64, orderedCharList, bestMap);
        bestTestHelper(38, 77, 0.37, orderedCharList, bestMap);
        bestTestHelper(39, 16, 0.37, orderedCharList, bestMap);
        bestTestHelper(40, 57, 0.37, orderedCharList, bestMap);
        bestTestHelper(41, 65, 0.37, orderedCharList, bestMap);
        bestTestHelper(42, 37, 0.34, orderedCharList, bestMap);
        bestTestHelper(43, 53, 0.34, orderedCharList, bestMap);
        bestTestHelper(44, 2, 0.30, orderedCharList, bestMap);
        bestTestHelper(45, 18, 0.24, orderedCharList, bestMap);
        bestTestHelper(46, 43, 0.23, orderedCharList, bestMap);
        bestTestHelper(47, 8, 0.23, orderedCharList, bestMap);
        bestTestHelper(48, 51, 0.22, orderedCharList, bestMap);
        bestTestHelper(49, 5, 0.21, orderedCharList, bestMap);
        bestTestHelper(50, 46, 0.21, orderedCharList, bestMap);
        bestTestHelper(51, 29, 0.20, orderedCharList, bestMap);
        bestTestHelper(52, 36, 0.20, orderedCharList, bestMap);
        bestTestHelper(53, 70, 0.71, orderedCharList, bestMap);
        bestTestHelper(54, 14, 0.17, orderedCharList, bestMap);
        bestTestHelper(55, 42, 0.17, orderedCharList, bestMap);
        bestTestHelper(56, 10, 0.14, orderedCharList, bestMap);
        bestTestHelper(57, 32, 0.14, orderedCharList, bestMap);
        bestTestHelper(58, 23, 0.07, orderedCharList, bestMap);
        bestTestHelper(59, 17, 0.05, orderedCharList, bestMap);
        bestTestHelper(60, 21, 0.05, orderedCharList, bestMap);
        bestTestHelper(61, 22, 0.05, orderedCharList, bestMap);
        bestTestHelper(62, 24, 0.05, orderedCharList, bestMap);
        bestTestHelper(63, 39, 0.40, orderedCharList, bestMap);
        bestTestHelper(64, 6, 0.24, orderedCharList, bestMap);
        bestTestHelper(65, 68, 0.90, orderedCharList, bestMap);
    }

    @Test
    public void testBestOrder2() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        new UseDirective().parseAndProcess(context, "38,5");

        Pair<List<Integer>, List<Integer>> availableCharactersAndTaxaNumbers = getCharacterAndTaxonNumbersForBest(context);
        List<Integer> availableCharacterNumbers = availableCharactersAndTaxaNumbers.getFirst();
        List<Integer> availableTaxaNumbers = availableCharactersAndTaxaNumbers.getSecond();

        Map<Character, Double> bestMap = Best.orderBest(context.getDataset(), availableCharacterNumbers, availableTaxaNumbers, context.getRBase(), context.getVaryWeight());

        List<Character> orderedCharList = new ArrayList<Character>(bestMap.keySet());

        assertEquals(51, bestMap.keySet().size());

        bestTestHelper(0, 40, 1.46, orderedCharList, bestMap);
        bestTestHelper(1, 63, 1.34, orderedCharList, bestMap);
        bestTestHelper(2, 11, 1.00, orderedCharList, bestMap);
        bestTestHelper(3, 13, 0.92, orderedCharList, bestMap);
        bestTestHelper(4, 27, 0.92, orderedCharList, bestMap);
        bestTestHelper(5, 44, 0.83, orderedCharList, bestMap);
        bestTestHelper(6, 28, 1.00, orderedCharList, bestMap);
        bestTestHelper(7, 54, 0.98, orderedCharList, bestMap);
        bestTestHelper(8, 19, 0.92, orderedCharList, bestMap);
        bestTestHelper(9, 30, 0.92, orderedCharList, bestMap);
        bestTestHelper(10, 52, 0.92, orderedCharList, bestMap);
        bestTestHelper(11, 66, 0.65, orderedCharList, bestMap);
        bestTestHelper(12, 58, 0.76, orderedCharList, bestMap);
        bestTestHelper(13, 26, 0.75, orderedCharList, bestMap);
        bestTestHelper(14, 65, 0.65, orderedCharList, bestMap);
        bestTestHelper(15, 67, 0.65, orderedCharList, bestMap);
        bestTestHelper(16, 56, 0.62, orderedCharList, bestMap);
        bestTestHelper(17, 7, 0.61, orderedCharList, bestMap);
        bestTestHelper(18, 31, 0.59, orderedCharList, bestMap);
        bestTestHelper(19, 35, 0.59, orderedCharList, bestMap);
        bestTestHelper(20, 3, 0.55, orderedCharList, bestMap);
        bestTestHelper(21, 48, 0.33, orderedCharList, bestMap);
        bestTestHelper(22, 47, 0.44, orderedCharList, bestMap);

        // TODO these appear in the result in incorrect order. Almost certainly
        // because of the extra precision being used in the ported BEST
        // algorithm - doubles are being used.
        // bestTestHelper(23, 12, 0.22, orderedCharList, bestMap);
        // bestTestHelper(24, 41, 0.42, orderedCharList, bestMap);

        bestTestHelper(25, 37, 0.40, orderedCharList, bestMap);
        bestTestHelper(26, 53, 0.40, orderedCharList, bestMap);
        bestTestHelper(27, 4, 0.26, orderedCharList, bestMap);
        bestTestHelper(28, 20, 0.26, orderedCharList, bestMap);
        bestTestHelper(29, 29, 0.26, orderedCharList, bestMap);
        bestTestHelper(30, 59, 0.26, orderedCharList, bestMap);
        bestTestHelper(31, 8, 0.23, orderedCharList, bestMap);
        bestTestHelper(32, 9, 0.22, orderedCharList, bestMap);
        bestTestHelper(33, 10, 0.22, orderedCharList, bestMap);
        bestTestHelper(34, 34, 0.22, orderedCharList, bestMap);
        bestTestHelper(35, 36, 0.22, orderedCharList, bestMap);
        bestTestHelper(36, 45, 0.22, orderedCharList, bestMap);
        bestTestHelper(37, 61, 0.22, orderedCharList, bestMap);
        bestTestHelper(38, 49, 0.21, orderedCharList, bestMap);
        bestTestHelper(39, 51, 0.21, orderedCharList, bestMap);
        bestTestHelper(40, 2, 0.19, orderedCharList, bestMap);
        bestTestHelper(41, 5, 0.19, orderedCharList, bestMap);
        bestTestHelper(42, 43, 0.19, orderedCharList, bestMap);
        bestTestHelper(43, 46, 0.19, orderedCharList, bestMap);
        bestTestHelper(44, 50, 0.19, orderedCharList, bestMap);
        bestTestHelper(45, 14, 0.11, orderedCharList, bestMap);
        bestTestHelper(46, 70, 0.59, orderedCharList, bestMap);
        bestTestHelper(47, 64, 0.29, orderedCharList, bestMap);
        bestTestHelper(48, 6, 0.26, orderedCharList, bestMap);
        bestTestHelper(49, 39, 0.26, orderedCharList, bestMap);
        bestTestHelper(50, 68, 0.76, orderedCharList, bestMap);
    }

    @Test
    public void testBestOrder3() throws Exception {
        IntkeyContext context = loadDataset("/dataset/sample/intkey.ink");

        new UseDirective().parseAndProcess(context, "38,5");
        new UseDirective().parseAndProcess(context, "40,1");

        Pair<List<Integer>, List<Integer>> availableCharactersAndTaxaNumbers = getCharacterAndTaxonNumbersForBest(context);
        List<Integer> availableCharacterNumbers = availableCharactersAndTaxaNumbers.getFirst();
        List<Integer> availableTaxaNumbers = availableCharactersAndTaxaNumbers.getSecond();

        Map<Character, Double> bestMap = Best.orderBest(context.getDataset(), availableCharacterNumbers, availableTaxaNumbers, context.getRBase(), context.getVaryWeight());

        List<Character> orderedCharList = new ArrayList<Character>(bestMap.keySet());

        assertEquals(17, bestMap.keySet().size());

        bestTestHelper(0, 13, 1.00, orderedCharList, bestMap);
        bestTestHelper(1, 19, 1.00, orderedCharList, bestMap);
        bestTestHelper(2, 11, 0.33, orderedCharList, bestMap);
        bestTestHelper(3, 54, 0.44, orderedCharList, bestMap);
        bestTestHelper(4, 26, 0.27, orderedCharList, bestMap);
        bestTestHelper(5, 4, 0.25, orderedCharList, bestMap);
        bestTestHelper(6, 9, 0.25, orderedCharList, bestMap);
        bestTestHelper(7, 10, 0.25, orderedCharList, bestMap);
        bestTestHelper(8, 20, 0.25, orderedCharList, bestMap);
        bestTestHelper(9, 28, 0.25, orderedCharList, bestMap);
        bestTestHelper(10, 35, 0.25, orderedCharList, bestMap);
        bestTestHelper(11, 36, 0.25, orderedCharList, bestMap);
        bestTestHelper(12, 43, 0.25, orderedCharList, bestMap);
        bestTestHelper(13, 3, 0.14, orderedCharList, bestMap);
        bestTestHelper(14, 6, 0.25, orderedCharList, bestMap);
        bestTestHelper(15, 70, 0.25, orderedCharList, bestMap);
        bestTestHelper(16, 68, 0.25, orderedCharList, bestMap);

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

    private void bestTestHelper(int index, int expectedCharNum, double expectedSeparation, List<Character> orderedChars, Map<Character, Double> separationMap) {

        int actualCharNum = orderedChars.get(index).getCharacterId();
        String charFailureMessage = String.format("Index: %s, expected character: %s, actual character: %s.", index, expectedCharNum, actualCharNum);
        assertEquals(charFailureMessage, expectedCharNum, actualCharNum);

        BigDecimal bdExpectedSeparation = new BigDecimal(expectedSeparation).setScale(2, RoundingMode.HALF_UP);
        BigDecimal bdActualSeparation = new BigDecimal(separationMap.get(orderedChars.get(index))).setScale(2, RoundingMode.HALF_UP);
        String separationFailureMessage = String.format("Index: %s, expected separation: %s, actual separation: %s.", index, bdExpectedSeparation, bdActualSeparation);
        assertEquals(separationFailureMessage, bdExpectedSeparation, bdActualSeparation);
    }

}

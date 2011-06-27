package au.org.ala.delta.inkey.model;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;
import org.junit.Ignore;
import org.junit.Test;

import au.org.ala.delta.intkey.directives.UseDirective;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealAttribute;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;

public class DisplayCharacterOrderBestTest extends TestCase {

//    @Test
//    public void testSimpleDataSet() throws Exception {
//        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
//
//        IntkeyContext context = new IntkeyContext(null);
//        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
//
//        Map<Character, Double> bestMap = orderBestOther(context);
//        List<Character> orderedCharList = new ArrayList<Character>(bestMap.keySet());
//        
//        assertEquals(7, bestMap.keySet().size());
//        
//        bestTestHelper(0, 6, 1.92, orderedCharList, bestMap);
//        bestTestHelper(1, 1, 1.74, orderedCharList, bestMap);
//        bestTestHelper(2, 7, 1.10, orderedCharList, bestMap);
//        bestTestHelper(3, 2, 0.97, orderedCharList, bestMap);
//        bestTestHelper(4, 5, 0.79, orderedCharList, bestMap);
//        bestTestHelper(5, 3, 0.49, orderedCharList, bestMap);
//        bestTestHelper(6, 4, 0.45, orderedCharList, bestMap);
//    }
    
    @Test
    public void testDeltaSampleDataSet() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");

        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());

        Map<Character, Double> bestMap = orderBestOther(context);
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
        bestTestHelper(42, 37, 0.37, orderedCharList, bestMap);
        bestTestHelper(43, 53, 0.37, orderedCharList, bestMap);
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
    
    private void bestTestHelper(int index, int expectedCharNum, double expectedSeparation, List<Character> orderedChars, Map<Character, Double> separationMap) {
        
        int actualCharNum = orderedChars.get(index).getCharacterId();
        String charFailureMessage = String.format("Index: %s, expected character: %s, actual character: %s.", index, expectedCharNum, actualCharNum);  
        assertEquals(charFailureMessage, expectedCharNum, actualCharNum);
        
        BigDecimal bdExpectedSeparation =  new BigDecimal(expectedSeparation).setScale(2, RoundingMode.HALF_UP);
        BigDecimal bdActualSeparation = new BigDecimal(separationMap.get(orderedChars.get(index))).setScale(2, RoundingMode.HALF_UP);
        String separationFailureMessage = String.format("Index: %s, expected separation: %s, actual separation: %s.", index, bdExpectedSeparation, bdActualSeparation);
        assertEquals(separationFailureMessage, bdExpectedSeparation, bdActualSeparation);
    }
    
//    @Test
//    public void testSampleDataset() throws Exception {
//        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
//        //URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
//
//        IntkeyContext context = new IntkeyContext(null);
//        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
//        // context.newDataSetFile(new
//        // File("C:\\Users\\ChrisF\\Documents\\grasses\\intkey.ini").getAbsolutePath());
//        // context.newDataSetFile(new
//        // File("C:\\Users\\ChrisF\\Documents\\salix\\intkey.ini").getAbsolutePath());
//
//    }    
//    
//    @Test
//    public void testGrassesDataset() throws Exception {
//        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
//        //URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
//
//        IntkeyContext context = new IntkeyContext(null);
//        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
//        // context.newDataSetFile(new
//        // File("C:\\Users\\ChrisF\\Documents\\grasses\\intkey.ini").getAbsolutePath());
//        // context.newDataSetFile(new
//        // File("C:\\Users\\ChrisF\\Documents\\salix\\intkey.ini").getAbsolutePath());
//
//    }        

    // @Test
    // public void testBestOrder2() throws Exception {
    // URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
    //
    // IntkeyContext context = new IntkeyContext(null);
    // context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
    //
    // new UseDirective().parseAndProcess(context, "38,5");
    //
    // List<Character> bestChars = orderBestOther(context);
    // }

    private LinkedHashMap<au.org.ala.delta.model.Character, Double> orderBestOther(IntkeyContext context) {
        IntkeyDataset dataset = context.getDataset();
        Specimen specimen = context.getSpecimen();

        // final Map<Character, Double> suMap = new HashMap<Character,
        // Double>();
        // Map<Character, Double> sepMap = new HashMap<Character, Double>();
        List<Character> allCharacters = dataset.getCharacters();
        final double[] suVals = new double[allCharacters.size()];
        double[] sepVals = new double[allCharacters.size()];

        // Calculate costs of characters
        // Map<Character, Double> costMap = new HashMap<Character, Double>();
        double[] charCosts = new double[allCharacters.size()];

        for (Character ch : dataset.getCharacters()) {
            double charCost = Math.pow(context.getRBase(), 5.0 - Math.min(10.0, ch.getReliability()));
            charCosts[ch.getCharacterId() - 1] = charCost;
        }

        double varw = (1 - context.getVaryWeight()) / context.getVaryWeight();

        // Build list of available characters
        List<Character> availableCharacters = new ArrayList<Character>(specimen.getAvailableCharacters());
        List<Character> ignoredCharacters = new ArrayList<Character>();
        for (Character ch : availableCharacters) {

            // TODO ignore EXACT characters that have been eliminated
            // TODO ignore characters not "masked in" - excluded characters?

            // Ignore character if its reliability is zero
            if (ch.getReliability() == 0) {
                ignoredCharacters.add(ch);
            }

            // Ignore character if it is a text character
            if (ch instanceof TextCharacter) {
                ignoredCharacters.add(ch);
            }

            // Ignore real characters if there are no key states
            // for real characters
            if (ch instanceof RealCharacter && !context.getDataset().realCharacterKeyStateBoundariesPresent()) {
                ignoredCharacters.add(ch);
            }
        }
        availableCharacters.removeAll(ignoredCharacters);

        // Build list of remaining taxa
        int numAvailableTaxa = 0;
        Map<Item, Boolean> taxaAvailability = new HashMap<Item, Boolean>();

        // TODO this line throws exception if no characters have been USEd yet
        // Map<Item, Integer> taxonDifferences = specimen.getTaxonDifferences();
        for (Item taxon : dataset.getTaxa()) {

            boolean ignore = false;
            // if (taxonDifferences.get(taxon) > context.getTolerance()) {
            // continue;
            // }

            // TODO skip if taxon is not included

            // TODO skip if there are EXACT characters and this taxon has
            // been eliminated

            if (ignore) {
                taxaAvailability.put(taxon, false);
            } else {
                numAvailableTaxa++;
                taxaAvailability.put(taxon, true);
            }

        }

        // sort available characters by reliability (descending)
        Collections.sort(availableCharacters, new ReliabilityComparator());

        double cmin = charCosts[availableCharacters.get(0).getCharacterId() - 1]; // minimum
        // cost -
        // this will
        // always be
        // the
        // cost of
        // the available
        // character
        // with
        // the
        // greatest
        // reliability

        List<Character> unsuitableCharacters = new ArrayList<Character>();

        for (Character ch : availableCharacters) {

            // Map<Integer, Integer> subgroupsNumTaxa = new HashMap<Integer,
            // Integer>();
            // Map<Integer, Double> subgroupFrequencies = new HashMap<Integer,
            // Double>();

            int sumNumTaxaInSubgroups = 0;
            double sumSubgroupsFrequencies = 0;
            int numSubgroupsSameSizeAsOriginalGroup = 0;
            double sup0 = 0; // THEORETICAL PARTITION COMPONENT OF SUP.
            double dupf = 0; // ARBITRARY INTRA-TAXON VARIABILITY COMPONENT OF
                             // SUP.
            double sep = 0; // SEPARATION????
            double sup = 0; // SUP: TOTAL PARTITION COMPONENT OF SU. SUP = SUP0
                            // +
                            // DUPF.

            double su = 0; // character suitability

            int totalNumStates = 0;
            if (ch instanceof MultiStateCharacter) {
                totalNumStates = ((MultiStateCharacter) ch).getNumberOfStates();
            } else if (ch instanceof IntegerCharacter) {
                IntegerCharacter intChar = (IntegerCharacter) ch;
                totalNumStates = intChar.getMaximumValue() - intChar.getMinimumValue() + 2;
            } else if (ch instanceof RealCharacter) {
                totalNumStates = ((RealCharacter) ch).getKeyStateBoundaries().size();
            } else {
                throw new RuntimeException("Invalid character type " + ch.toString());
            }

            int[] subgroupsNumTaxa = new int[totalNumStates];
            double[] subgroupFrequencies = new double[totalNumStates];

            List<Attribute> charAttributes = dataset.getAttributesForCharacter(ch.getCharacterId());

            for (Attribute attr : charAttributes) {
                Item taxon = attr.getItem();
                if (!taxaAvailability.get(taxon)) {
                    continue;
                }

                // List<Integer> stateValues = null;
                boolean[] statePresence = new boolean[totalNumStates];
                int numStatesPresent = 0;

                // Treat attribute unknown as variable
                if (attr.isUnknown()) {
                    Arrays.fill(statePresence, true);
                    numStatesPresent = totalNumStates;
                } else {
                    Arrays.fill(statePresence, false);

                    if (ch.getCharacterType() == CharacterType.OrderedMultiState || ch.getCharacterType() == CharacterType.UnorderedMultiState) {
                        MultiStateAttribute multiStateAttr = (MultiStateAttribute) attr;
                        Set<Integer> attrPresentStates = multiStateAttr.getPresentStates();

                        for (int i = 0; i < totalNumStates; i++) {
                            if (attrPresentStates.contains(i + 1)) {
                                statePresence[i] = true;
                                numStatesPresent++;
                            }
                        }

                    } else if (ch.getCharacterType() == CharacterType.IntegerNumeric) {
                        IntegerCharacter intChar = (IntegerCharacter) ch;
                        IntegerAttribute intAttr = (IntegerAttribute) attr;

                        Set<Integer> attrPresentStates = intAttr.getPresentValues();

                        int offset = intChar.getMinimumValue() - 1;

                        for (int i = 0; i < totalNumStates; i++) {
                            if (attrPresentStates.contains(i + offset)) {
                                statePresence[i] = true;
                                numStatesPresent++;
                            }
                        }

                    } else if (ch.getCharacterType() == CharacterType.RealNumeric) {
                        RealCharacter realChar = (RealCharacter) ch;
                        RealAttribute realAttr = (RealAttribute) attr;
                        FloatRange presentRange = realAttr.getPresentRange();

                        numStatesPresent = generateKeyStatesForRealCharacter(realChar, presentRange, statePresence);
                    } else {
                        throw new RuntimeException("Invalid character type " + ch.toString());
                    }
                }

                // work out size of character subgroups
                for (int i = 0; i < totalNumStates; i++) {

                    if (statePresence[i] == true) {
                        subgroupsNumTaxa[i]++;

                        // frequency of items with current state of current
                        // character
                        double stateFrequency = 1.0 / (double) numStatesPresent;
                        stateFrequency += subgroupFrequencies[i];
                        subgroupFrequencies[i] = stateFrequency;
                    }
                }

            }

            int totalNumSubgroups = 0;

            // work out sum of subgroup sizes and frequencies
            for (int i = 0; i < totalNumStates; i++) {
                sumNumTaxaInSubgroups += subgroupsNumTaxa[i];
                sumSubgroupsFrequencies += subgroupFrequencies[i];

                if (subgroupsNumTaxa[i] > 0) {
                    totalNumSubgroups++;
                }
            }

            // character is unsuitable if it divides the characters into a
            // single
            // subgroup
            // TODO slightly dodgy there recurising over keys of one map but
            // reading from two
            boolean allTaxaInOneGroup = false;
            for (int i = 0; i < totalNumStates; i++) {
                int numTaxaInSubgroup = subgroupsNumTaxa[i];

                if (numTaxaInSubgroup == sumNumTaxaInSubgroups) {
                    allTaxaInOneGroup = true;
                    break;
                } else {
                    if (numTaxaInSubgroup == numAvailableTaxa) {
                        numSubgroupsSameSizeAsOriginalGroup++;
                    }

                    if (subgroupsNumTaxa[i] > 0) {
                        sup0 += (subgroupFrequencies[i] * log2(subgroupsNumTaxa[i]));
                    }
                }
            }

            if (allTaxaInOneGroup) {
                unsuitableCharacters.add(ch);
                continue;
            }

            // TODO something about control characters here???
            boolean isControllingChar = !ch.getDependentCharacters().isEmpty();
            // WTF is this test for???
            if (!isControllingChar && (totalNumSubgroups == numSubgroupsSameSizeAsOriginalGroup || (sumNumTaxaInSubgroups > numAvailableTaxa && sumNumTaxaInSubgroups == totalNumStates))) {
                unsuitableCharacters.add(ch);
                continue;
            }

            sup0 = sup0 / sumSubgroupsFrequencies;

            if (ch.getCharacterId() == 11) {
                System.out.println("11!!!");
            }

            if (numAvailableTaxa > 1 && sumNumTaxaInSubgroups > numAvailableTaxa) {
                dupf = varw * (1 + 100 * numSubgroupsSameSizeAsOriginalGroup) * (sumNumTaxaInSubgroups - numAvailableTaxa) * ((numAvailableTaxa + 8) / (numAvailableTaxa * log2(numAvailableTaxa)));
            } else {
                dupf = 0;
            }

            sep = -sup0 + log2(numAvailableTaxa);

            // TODO some stuff about rounding errors

            // TODO don't display controlling characters with 0 separation
            if (isControllingChar && sep == 0) {
                continue;
            }

            sup = sup0 + dupf;

            su = charCosts[ch.getCharacterId() - 1] + cmin * sup;

            sepVals[ch.getCharacterId() - 1] = sep;
            suVals[ch.getCharacterId() - 1] = su;
        }

        availableCharacters.removeAll(unsuitableCharacters);

        List<Character> sortedChars = new ArrayList<Character>(availableCharacters);
        Collections.sort(sortedChars, new Comparator<Character>() {

            @Override
            public int compare(Character c1, Character c2) {
                // TODO had to make suMap final - dodgy

                return Double.valueOf(suVals[c1.getCharacterId() - 1]).compareTo(Double.valueOf(suVals[c2.getCharacterId() - 1]));
            }
        });

        System.out.println(availableCharacters.size());
        
        LinkedHashMap<Character, Double> retMap = new LinkedHashMap<Character, Double>();
        
        for (Character ch : sortedChars) {
            // System.out.println(String.format("%s. %s - cost: %s su: %s sep: %.2f",
            // ch.getCharacterId(), ch.getDescription(), costMap.get(ch),
            // suMap.get(ch), sepMap.get(ch)));
            System.out.println(String.format("(%s) %.2f %s. %s", suVals[ch.getCharacterId() - 1], sepVals[ch.getCharacterId() - 1], ch.getCharacterId(), ch.getDescription()));
            // System.out.println(String.format("%s;%.2f", ch.getCharacterId(),
            // sepMap.get(ch)));
            
            retMap.put(ch, sepVals[ch.getCharacterId() - 1]);
        }

        return retMap;
    }

    private double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    private int generateKeyStatesForRealCharacter(RealCharacter realChar, FloatRange presentRange, boolean[] statePresence) {
        int numStatesPresent = 0;

        List<Float> boundaries = realChar.getKeyStateBoundaries();

        float rangeMin = presentRange.getMinimumFloat();
        float rangeMax = presentRange.getMaximumFloat();

        int i = 0;
        for (; i < boundaries.size(); i++) {
            if (rangeMin <= boundaries.get(i)) {
                statePresence[i] = true;
                numStatesPresent++;
                break;
            }
        }

        for (; i < boundaries.size() - 1; i++) {
            if (rangeMax > boundaries.get(i)) {
                statePresence[i + 1] = true;
                numStatesPresent++;
            }
        }

        return numStatesPresent;
    }

    private class ReliabilityComparator implements Comparator<Character> {

        @Override
        public int compare(Character c1, Character c2) {

            int compareResult = Float.valueOf(c1.getReliability()).compareTo(Float.valueOf(c2.getReliability()));

            // multiply by -1 to get descending order
            return compareResult * -1;
        }
    }
}

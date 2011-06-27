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

     @Test
     public void testSimpleDataSet() throws Exception {
     URL initFileUrl =
     getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
    
     IntkeyContext context = new IntkeyContext(null);
     context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
    
     Map<Character, Double> bestMap = orderBestOther(context);
     List<Character> orderedCharList = new
     ArrayList<Character>(bestMap.keySet());
    
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
     URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
    
     IntkeyContext context = new IntkeyContext(null);
     context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
    
     Map<Character, Double> bestMap = orderBestOther(context);
     List<Character> orderedCharList = new
     ArrayList<Character>(bestMap.keySet());
    
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

//    @Test
//    public void testGrassesDataset() throws Exception {
//
//        IntkeyContext context = new IntkeyContext(null);
//        context.newDataSetFile(new File("C:\\Users\\ChrisF\\Documents\\grasses\\intkey.ini").getAbsolutePath());
//
//        long startTime = System.currentTimeMillis();
//        Map<Character, Double> bestMap = orderBestOther(context);
//        long endTime = System.currentTimeMillis();
//        long duration = (endTime - startTime) / 1000;
//        System.out.println(duration + " seconds");
//    }

    @Test
    public void testBestOrder2() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");

        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());

        new UseDirective().parseAndProcess(context, "38,5");

        Map<Character, Double> bestMap = orderBestOther(context);
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
        //bestTestHelper(23, 12, 0.22, orderedCharList, bestMap);
        //bestTestHelper(24, 41, 0.42, orderedCharList, bestMap);
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

    private void bestTestHelper(int index, int expectedCharNum, double expectedSeparation, List<Character> orderedChars, Map<Character, Double> separationMap) {

        int actualCharNum = orderedChars.get(index).getCharacterId();
        String charFailureMessage = String.format("Index: %s, expected character: %s, actual character: %s.", index, expectedCharNum, actualCharNum);
        assertEquals(charFailureMessage, expectedCharNum, actualCharNum);

        BigDecimal bdExpectedSeparation = new BigDecimal(expectedSeparation).setScale(2, RoundingMode.HALF_UP);
        BigDecimal bdActualSeparation = new BigDecimal(separationMap.get(orderedChars.get(index))).setScale(2, RoundingMode.HALF_UP);
        String separationFailureMessage = String.format("Index: %s, expected separation: %s, actual separation: %s.", index, bdExpectedSeparation, bdActualSeparation);
        assertEquals(separationFailureMessage, bdExpectedSeparation, bdActualSeparation);
    }

    private LinkedHashMap<au.org.ala.delta.model.Character, Double> orderBestOther(IntkeyContext context) {
        IntkeyDataset dataset = context.getDataset();
        Specimen specimen = context.getSpecimen();

        List<Character> allCharacters = dataset.getCharacters();

        // TODO refactor so that this array does not need to be final
        final double[] suVals = new double[allCharacters.size()];
        double[] sepVals = new double[allCharacters.size()];

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
        Map<Item, Integer> taxonDifferences = specimen.getTaxonDifferences();
        for (Item taxon : dataset.getTaxa()) {

            boolean ignore = false;
            
            if (taxonDifferences != null && taxonDifferences.get(taxon) > context.getTolerance()) {
                ignore = true;;
            }

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

        // minimum cost - this will always be the cost of the available
        // character with the greatest reliability
        double cmin = charCosts[availableCharacters.get(0).getCharacterId() - 1];

        List<Character> unsuitableCharacters = new ArrayList<Character>();

        for (Character ch : availableCharacters) {

            int sumNumTaxaInSubgroups = 0;
            double sumSubgroupsFrequencies = 0;
            int numSubgroupsSameSizeAsOriginalGroup = 0;
            double sup0 = 0; // theoretical partition component of sup.
            double dupf = 0; // arbitrary intra-taxon variability component of
                             // sup.
            double sep = 0; // separating power of the character
            double sup = 0; // total partition component of su. sup = sup0 +
                            // dupf
            double su = 0; // character suitability

            // NOTE: to simplify the algorithm, all characters are treated as
            // multistate characters. Integer and real
            // characters are converted into multistate representations.

            // Determine the total available states for each character
            int totalNumStates = 0;
            if (ch instanceof MultiStateCharacter) {
                totalNumStates = ((MultiStateCharacter) ch).getNumberOfStates();
            } else if (ch instanceof IntegerCharacter) {
                // for an integer character, 1 state for each value between
                // the minimum and
                // maximum (inclusive), 1 state for all values below the
                // minimum, and 1 state for
                // all values above the maximum
                IntegerCharacter intChar = (IntegerCharacter) ch;
                totalNumStates = intChar.getMaximumValue() - intChar.getMinimumValue() + 3;
            } else if (ch instanceof RealCharacter) {
                // the real character's key state boundaries are used to convert
                // a real value into a
                // multistate value (see below). The total number of possible
                // states is equal to the number of
                // key state boundaries.
                totalNumStates = ((RealCharacter) ch).getKeyStateBoundaries().size();
            } else {
                throw new RuntimeException("Invalid character type " + ch.toString());
            }

            // number of taxa in character subgroups
            int[] subgroupsNumTaxa = new int[totalNumStates];

            // frequency of character subgroups
            double[] subgroupFrequencies = new double[totalNumStates];

            List<Attribute> charAttributes = dataset.getAttributesForCharacter(ch.getCharacterId());

            for (Attribute attr : charAttributes) {
                Item taxon = attr.getItem();
                
                // Skip any attributes that pertain to taxa that are not
                // available
                if (!taxaAvailability.get(taxon)) {
                    continue;
                }

                // has a boolean value for each character state. A true value
                // designates the presence of the corresponding character state
                // for the attribute.
                boolean[] statePresence = new boolean[totalNumStates];

                int numStatesPresent = 0;

                // determine which character states are present for the
                // attribute.
                if (attr.isUnknown()) {
                    // Treat attribute unknown as variable
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

                        // for an integer character, 1 state for each value
                        // between
                        // the minimum and
                        // maximum (inclusive), 1 state for all values below the
                        // minimum, and 1 state for
                        // all values above the maximum

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

                        // convert real value into multistate value.
                        numStatesPresent = generateKeyStatesForRealCharacter(realChar, presentRange, statePresence);
                    } else {
                        throw new RuntimeException("Invalid character type " + ch.toString());
                    }
                }

                // work out size of character subgroups.
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

            // total number of non-empty character subgroups
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

            // A character is unsuitable if it has the same value for all
            // available characters
            if (allTaxaInOneGroup) {
                unsuitableCharacters.add(ch);
                continue;
            }

            boolean isControllingChar = !ch.getDependentCharacters().isEmpty();
            // TODO what is this test for???
            if (!isControllingChar && (totalNumSubgroups == numSubgroupsSameSizeAsOriginalGroup || (sumNumTaxaInSubgroups > numAvailableTaxa && sumNumTaxaInSubgroups == totalNumStates))) {
                unsuitableCharacters.add(ch);
                continue;
            }

            sup0 = sup0 / sumSubgroupsFrequencies;

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

                //float c1Su = (float) suVals[c1.getCharacterId() - 1];
                //float c2Su = (float) suVals[c2.getCharacterId() - 1];
                
                //return Float.valueOf(c1Su).compareTo(Float.valueOf(c2Su));
                
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

    /**
     * Helper method for orderBest. Takes a real character's value and converts
     * it into a multistate value using the real character's key state
     * boundaries
     * 
     * @param realChar
     *            The real character
     * @param realValue
     *            The value for the real character
     * @param statePresence
     *            This array will be filled by the method. A true value in this
     *            array indicates that the corresponding state value is present
     * @return the number of key states present for the supplied real value
     */
    private int generateKeyStatesForRealCharacter(RealCharacter realChar, FloatRange realValue, boolean[] statePresence) {
        int numStatesPresent = 0;

        List<Float> boundaries = realChar.getKeyStateBoundaries();

        float rangeMin = realValue.getMinimumFloat();
        float rangeMax = realValue.getMaximumFloat();

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

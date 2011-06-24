package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

    @Ignore
    @Test
    public void testBestOrder() throws Exception {
        URL initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
        //URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");

        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());
        //context.newDataSetFile(new File("C:\\Users\\ChrisF\\Documents\\grasses\\intkey.ini").getAbsolutePath());
        // context.newDataSetFile(new
        // File("C:\\Users\\ChrisF\\Documents\\salix\\intkey.ini").getAbsolutePath());

        long startTime = System.currentTimeMillis();

        List<Character> bestChars = orderBestOther(context);

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        long durationSeconds = duration / 1000;

        System.out.println("Duration: " + durationSeconds + " seconds");

        // System.out.println(bestChars.toString());
    }

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

    private List<au.org.ala.delta.model.Character> orderBestOther(IntkeyContext context) {
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
            double cmin = charCosts[0]; // minimum
                                        // cost -
                                        // this will
                                        // always be
                                        // the
                                        // cost of
                                        // the
                                        // character
                                        // with
                                        // the
                                        // greatest
                                        // reliability
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
            for (int i=0; i < totalNumStates; i++) {
                int numTaxaInSubgroup = subgroupsNumTaxa[i];

                if (numTaxaInSubgroup == sumNumTaxaInSubgroups) {
                    allTaxaInOneGroup = true;
                } else {
                    if (numTaxaInSubgroup == numAvailableTaxa) {
                        numSubgroupsSameSizeAsOriginalGroup++;
                    }

                    sup0 += (subgroupFrequencies[i] * log2(subgroupsNumTaxa[i]));
                }
            }

            if (allTaxaInOneGroup) {
                unsuitableCharacters.add(ch);
                continue;
            }

            // TODO something about control characters here???
            boolean isControllingChar = !ch.getDependentCharacters().isEmpty();
            // WTF is this test for???
            if (!isControllingChar
                    && (totalNumSubgroups == numSubgroupsSameSizeAsOriginalGroup || (sumNumTaxaInSubgroups > numAvailableTaxa && sumNumTaxaInSubgroups == totalNumStates))) {
                unsuitableCharacters.add(ch);
                continue;
            }

            sup0 = sup0 / sumSubgroupsFrequencies;

            if (numAvailableTaxa > 1 && sumNumTaxaInSubgroups > numAvailableTaxa) {
                dupf = varw * (1 + 100 * numSubgroupsSameSizeAsOriginalGroup) * (sumNumTaxaInSubgroups - numAvailableTaxa)
                        * ((numAvailableTaxa + 8) / (numAvailableTaxa * log2(numAvailableTaxa)));
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

            su = charCosts[ch.getCharacterId()] + cmin * sup;

            sepVals[ch.getCharacterId()] = sep;
            suVals[ch.getCharacterId()] = su;
        }

        availableCharacters.removeAll(unsuitableCharacters);

        List<Character> sortedChars = new ArrayList<Character>(availableCharacters);
        Collections.sort(sortedChars, new Comparator<Character>() {

            @Override
            public int compare(Character c1, Character c2) {
                // TODO had to make suMap final - dodgy

                return Double.valueOf(suVals[c1.getCharacterId()]).compareTo(Double.valueOf(suVals[c2.getCharacterId()]));
            }
        });

        System.out.println(availableCharacters.size());
        for (Character ch : sortedChars) {
            // System.out.println(String.format("%s. %s - cost: %s su: %s sep: %.2f",
            // ch.getCharacterId(), ch.getDescription(), costMap.get(ch),
            // suMap.get(ch), sepMap.get(ch)));
            System.out.println(String.format("(%s) %.2f %s. %s", suVals[ch.getCharacterId()], sepVals[ch.getCharacterId()], ch.getCharacterId(), ch.getDescription()));
            // System.out.println(String.format("%s;%.2f", ch.getCharacterId(),
            // sepMap.get(ch)));
        }

        return sortedChars;
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

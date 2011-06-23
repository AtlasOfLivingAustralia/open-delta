package au.org.ala.delta.inkey.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;
import org.junit.Test;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
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
    public void testBestOrder() throws Exception {
        //URL initFileUrl = getClass().getResource("/dataset/controlling_characters_simple/intkey.ink");
        URL initFileUrl = getClass().getResource("/dataset/sample/intkey.ink");
        IntkeyContext context = new IntkeyContext(null);
        context.newDataSetFile(new File(initFileUrl.toURI()).getAbsolutePath());

        List<Character> bestChars = orderBestOther(context, context.getDataset().getCharacters());
        // System.out.println(bestChars.toString());
    }

    /*
     * private List<au.org.ala.delta.model.Character> orderBest(IntkeyContext
     * context, List<Character> chars) { Map<Character, Boolean>
     * unsuitabilityMap = new HashMap<Character, Boolean>();
     * 
     * for (Character ch : chars) { unsuitabilityMap.put(ch, false); }
     * 
     * List<Double> costList = new ArrayList<Double>();
     * 
     * for (Character ch : chars) { double charCost =
     * Math.pow(context.getRBase(), 5.0 - Math.min(10.0, ch.getReliability()));
     * costList.add(charCost); }
     * 
     * double varw = (1 - context.getVaryWeight() /
     * Math.max(context.getVaryWeight(), 0.01));
     * 
     * List<Character> charsSortedByReliabilities = new
     * ArrayList<Character>(chars); Collections.sort(charsSortedByReliabilities,
     * new ReliabilityComparator());
     * 
     * // MAIN FOR LOOP for (Character ch : chars) {
     * 
     * // Ignore character if it has been used if
     * (context.getSpecimen().hasValueFor(ch)) { continue; }
     * 
     * // Ignore character if its reliability is zero if (ch.getReliability() ==
     * 0) { continue; }
     * 
     * // Ignore character if it is a text character if (ch instanceof
     * TextCharacter) { continue; }
     * 
     * // TODO ignore EXACT characters that have been eliminated
     * 
     * // TODO ignore characters not "masked in" - excluded characters?
     * 
     * // Ignore unsuitable characters if (unsuitabilityMap.get(ch)) { continue;
     * }
     * 
     * if (ch instanceof UnorderedMultiStateCharacter || ch instanceof
     * MultiStateCharacter) {
     * 
     * } else if (ch instanceof IntegerCharacter) {
     * 
     * } else if (ch instanceof RealCharacter) {
     * 
     * }
     * 
     * for (Item taxon : context.getDataset().getTaxa()) { // TODO skip if taxon
     * is not included
     * 
     * if (context.getSpecimen().getTaxonDifferences().get(taxon) >
     * context.getTolerance()) { continue; }
     * 
     * // TODO skip if there are EXACT characters and this taxon has // been
     * eliminated
     * 
     * } }
     * 
     * return Collections.EMPTY_LIST; }
     */

    private List<au.org.ala.delta.model.Character> orderBestOther(IntkeyContext context, List<Character> chars) {
        IntkeyDataset dataset = context.getDataset();
        Specimen specimen = context.getSpecimen();

        final Map<Character, Double> suMap = new HashMap<Character, Double>();
        Map<Character, Double> sepMap = new HashMap<Character, Double>();

        // Calculate costs of characters
        Map<Character, Double> costMap = new HashMap<Character, Double>();
        for (Character ch : dataset.getCharacters()) {
            double charCost = Math.pow(context.getRBase(), 5.0 - Math.min(10.0, ch.getReliability()));
            costMap.put(ch, charCost);
        }

        double varw = (1 - context.getVaryWeight()) / context.getVaryWeight();

        // Build list of available characters
        List<Character> availableCharacters = new ArrayList<Character>(specimen.getAvailableCharacters());
        List<Character> ignoredCharacters = new ArrayList<Character>();
        for (Character ch : availableCharacters) {
            if (ch.getCharacterId() == 38) {
                System.out.println("foo");
            }

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
        List<Item> availableTaxa = new ArrayList<Item>();
        // TODO comment back in Map<Item, Integer> taxonDifferences =
        // specimen.getTaxonDifferences();
        for (Item taxon : dataset.getTaxa()) {
            /*
             * if (taxonDifferences.get(taxon) > context.getTolerance()) {
             * continue; }
             */

            // TODO skip if taxon is not included

            // TODO skip if there are EXACT characters and this taxon has
            // been eliminated

            availableTaxa.add(taxon);
        }

        // sort available characters by reliability (descending)
        Collections.sort(availableCharacters, new ReliabilityComparator());

        List<Character> unsuitableCharacters = new ArrayList<Character>();

        for (Character ch : availableCharacters) {
            if (ch.getCharacterId() == 38) {
                System.out.println("bar");
            }

            Map<Integer, Integer> subgroupsNumTaxa = new HashMap<Integer, Integer>();
            Map<Integer, Double> subgroupFrequencies = new HashMap<Integer, Double>();
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
            double cmin = costMap.get(availableCharacters.get(0)); // minimum
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

            List<Attribute> charAttributes = dataset.getAttributesForCharacter(ch.getCharacterId());

            for (Attribute attr : charAttributes) {
                Item taxon = attr.getItem();
                if (!availableTaxa.contains(taxon)) {
                    continue;
                }

                // Treat attribute unknown or not applicable as variable
                boolean variable = false;
                if (attr.isUnknown()) {
                    variable = true;
                    //System.out.println(String.format("Variable - character: %s taxon: %s", ch.getCharacterId(), taxon.getItemNumber()));
                }

                List<Integer> stateValues = null;

                if (ch instanceof MultiStateCharacter) {
                    MultiStateCharacter msChar = (MultiStateCharacter) ch;
                    if (variable) {
                        IntRange r = new IntRange(1, msChar.getStates().length);
                        stateValues = new ArrayList<Integer>();
                        for (int val : r.toArray()) {
                            stateValues.add(val);
                        }
                    } else {
                        MultiStateAttribute multiStateAttr = (MultiStateAttribute) attr;
                        stateValues = new ArrayList<Integer>(multiStateAttr.getPresentStates());
                        Collections.sort(stateValues);
                    }
                } else if (ch instanceof IntegerCharacter) {
                    IntegerCharacter intChar = (IntegerCharacter) ch;
                    if (variable) {
                        IntRange r = new IntRange(intChar.getMinimumValue() - 1, intChar.getMaximumValue() + 1);
                        stateValues = new ArrayList<Integer>();
                        for (int val : r.toArray()) {
                            stateValues.add(val);
                        }
                    } else {
                        IntegerAttribute intAttr = (IntegerAttribute) attr;
                        stateValues = new ArrayList<Integer>(intAttr.getPresentValues());
                        Collections.sort(stateValues);
                    }
                } else if (ch instanceof RealCharacter) {
                    RealCharacter realChar = (RealCharacter) ch;
                    if (variable) {
                        List<Float> keyStateBoundaries = realChar.getKeyStateBoundaries();
                        Collections.sort(keyStateBoundaries);
                        float rangeLowerBound = keyStateBoundaries.get(0) - 1;
                        float rangeUpperBound = keyStateBoundaries.get(keyStateBoundaries.size() - 1) + 1;

                        stateValues = generateKeyStatesForRealCharacter(realChar, new FloatRange(rangeLowerBound, rangeUpperBound));
                    } else {
                        RealAttribute realAttr = (RealAttribute) attr;
                        FloatRange presentRange = realAttr.getPresentRange();

                        stateValues = generateKeyStatesForRealCharacter(realChar, presentRange);
                    }
                } else {
                    throw new RuntimeException("Invalid character type " + ch.toString());
                }

                // work out size of character subgroups
                for (int stateValue : stateValues) {

                    int subgroupSize = 0;
                    if (subgroupsNumTaxa.containsKey(stateValue)) {
                        subgroupSize = subgroupsNumTaxa.get(stateValue);
                        subgroupSize++;
                        subgroupsNumTaxa.put(stateValue, subgroupSize);
                    } else {
                        subgroupSize = 1;
                    }
                    subgroupsNumTaxa.put(stateValue, subgroupSize);

                    // frequency of items with current state of current
                    // character
                    double stateFrequency = Integer.valueOf(1).doubleValue() / Integer.valueOf(stateValues.size()).doubleValue();

                    if (subgroupFrequencies.containsKey(stateValue)) {
                        stateFrequency += subgroupFrequencies.get(stateValue);
                    }
                    subgroupFrequencies.put(stateValue, stateFrequency);
                    
                    if (ch.getCharacterId() == 38) {
                        /*System.out.println(String.format("Taxon: %s State Value: %s subgroupSize: %s frequency: %s", taxon.getItemNumber(), stateValue, subgroupSize, stateFrequency));
                        System.out.println(subgroupsNumTaxa.toString());
                        System.out.println(subgroupFrequencies.toString());
                        System.out.println();*/
                    }
                }

            }
            // work out sum of subgroup sizes and frequencies
            for (int stateValue : subgroupsNumTaxa.keySet()) {
                sumNumTaxaInSubgroups += subgroupsNumTaxa.get(stateValue);
            }

            for (int stateValue : subgroupFrequencies.keySet()) {
                sumSubgroupsFrequencies += subgroupFrequencies.get(stateValue);
            }

            // character is unsuitable if it divides the characters into a
            // single
            // subgroup
            // TODO slightly dodgy there recurising over keys of one map but
            // reading from two
            boolean allTaxaInOneGroup = false;
            for (int stateValue : subgroupsNumTaxa.keySet()) {
                int numTaxaInSubgroup = subgroupsNumTaxa.get(stateValue);

                if (numTaxaInSubgroup == sumNumTaxaInSubgroups) {
                    System.out.println(String.format("%s all taxa in one group", ch.toString()));
                    allTaxaInOneGroup = true;
                } else {
                    if (numTaxaInSubgroup == availableTaxa.size()) {
                        numSubgroupsSameSizeAsOriginalGroup++;
                    }

                    sup0 += (subgroupFrequencies.get(stateValue) * log2(subgroupsNumTaxa.get(stateValue)));
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
                    && (subgroupsNumTaxa.keySet().size() == numSubgroupsSameSizeAsOriginalGroup || (sumNumTaxaInSubgroups > availableTaxa.size() && sumNumTaxaInSubgroups == totalNumStates))) {
                unsuitableCharacters.add(ch);
                continue;
            }

            sup0 = sup0 / sumSubgroupsFrequencies;

            if (availableTaxa.size() > 1 && sumNumTaxaInSubgroups > availableTaxa.size()) {
                dupf = varw * (1 + 100 * numSubgroupsSameSizeAsOriginalGroup) * (sumNumTaxaInSubgroups - availableTaxa.size())
                        * ((availableTaxa.size() + 8) / (availableTaxa.size() * log2(availableTaxa.size())));
            } else {
                dupf = 0;
            }

            sep = -sup0 + log2(availableTaxa.size());

            // TODO some stuff about rounding errors

            // TODO don't display controlling characters with 0 separation
            if (isControllingChar && sep == 0) {
                continue;
            }

            sup = sup0 + dupf;

            su = costMap.get(ch) + cmin * sup;

            sepMap.put(ch, sep);
            suMap.put(ch, su);
        }

        availableCharacters.removeAll(unsuitableCharacters);

        List<Character> sortedChars = new ArrayList<Character>(availableCharacters);
        Collections.sort(sortedChars, new Comparator<Character>() {

            @Override
            public int compare(Character c1, Character c2) {
                // TODO had to make suMap final - dodgy

                return suMap.get(c1).compareTo(suMap.get(c2));
            }
        });

        System.out.println(availableCharacters.size());
        for (Character ch : sortedChars) {
          //System.out.println(String.format("%s. %s - cost: %s su: %s sep: %.2f", ch.getCharacterId(), ch.getDescription(), costMap.get(ch), suMap.get(ch), sepMap.get(ch)));
          System.out.println(String.format("%.2f %s. %s", sepMap.get(ch), ch.getCharacterId(), ch.getDescription()));
          //System.out.println(String.format("%s;%.2f", ch.getCharacterId(), sepMap.get(ch)));
        }

        return Collections.EMPTY_LIST;
    }

    private double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    private List<Integer> generateKeyStatesForRealCharacter(RealCharacter realChar, FloatRange presentRange) {
        List<Integer> retList = new ArrayList<Integer>();

        List<Float> boundaries = realChar.getKeyStateBoundaries();

        float rangeMin = presentRange.getMinimumFloat();
        float rangeMax = presentRange.getMaximumFloat();

        int i = 0;
        for (; i < boundaries.size(); i++) {
            if (rangeMin <= boundaries.get(i)) {
                retList.add(i);
                break;
            }
        }

        for (; i < boundaries.size() - 1; i++) {
            if (rangeMax > boundaries.get(i)) {
                retList.add(i + 1);
            }
        }

        return retList;
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

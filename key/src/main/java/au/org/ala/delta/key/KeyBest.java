package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.best.Best;
import au.org.ala.delta.best.Best.OrderingType;
import au.org.ala.delta.key.directives.AllowImproperSubgroupsDirective;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.util.Pair;

/**
 * Utilities for sorting characters using the BEST algorithm
 * 
 * @author ChrisF
 * 
 */
public class KeyBest {

    /**
     * A separating power below this is treated as zero
     */
    private static double minimumSeparatingPower = 0.0001;

    /**
     * Determines best order and separating power of all characters in the
     * supplied context's dataset
     * 
     * @param context
     *            the application's global state
     * @return a map of characters to their separating powers. The best order of
     *         the characters can be obtained by getting the keyset of the
     *         supplied map
     */
    public static LinkedHashMap<au.org.ala.delta.model.Character, Double> orderBest(DeltaDataSet dataset, double[] charCosts, double[] itemAbundanceValues, List<Integer> availableCharacterNumbers, List<Integer> availableTaxaNumbers, double rBase,
            double aBase, double reuse, double varyWt, boolean allowImproperSubgroups) {
        
        LinkedHashMap<Character, Double> retMap = new LinkedHashMap<Character, Double>();

        if (availableCharacterNumbers.isEmpty() || availableTaxaNumbers.isEmpty()) {
            // no available characters or taxa - just return an empty map
            return retMap;
        }

        int numAvailableTaxa = availableTaxaNumbers.size();

        final double[] suVals = new double[dataset.getNumberOfCharacters()];
        double[] sepVals = new double[dataset.getNumberOfCharacters()];

        double varw = 0;
        if (varyWt > 0) {
            varw = (1 - varyWt) / varyWt;    
        } 

        List<Character> availableCharacters = new ArrayList<Character>();
        for (int availableCharNum : availableCharacterNumbers) {
            availableCharacters.add(dataset.getCharacter(availableCharNum));
        }

        Set<Item> availableTaxa = new HashSet<Item>();
        for (int availableTaxonNum : availableTaxaNumbers) {
            availableTaxa.add(dataset.getItem(availableTaxonNum));
        }

        // sort available characters by reliability (descending)
        Collections.sort(availableCharacters, new Best.ReliabilityComparator());

        // minimum cost - this will always be the cost of the available
        // character with the greatest reliability
        double cmin = charCosts[availableCharacters.get(0).getCharacterId() - 1];

        List<Character> unsuitableCharacters = new ArrayList<Character>();

        charLoop: for (Character ch : availableCharacters) {

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

            // Determine the total available states for each character
            int totalNumStates = 0;
            if (ch instanceof MultiStateCharacter) {
                totalNumStates = ((MultiStateCharacter) ch).getNumberOfStates();
            } else {
                throw new RuntimeException("Invalid character type " + ch.toString() + " KEY only uses multistate characters");
            }

            // number of taxa in character subgroups
            int[] subgroupsNumTaxa = new int[totalNumStates];

            // frequency of character subgroups
            double[] subgroupAbundances = new double[totalNumStates];

            List<Attribute> charAttributes = dataset.getAllAttributesForCharacter(ch.getCharacterId());

            for (Attribute attr : charAttributes) {
//                if (attr.isInapplicable()) {
//                    System.out.println("Inapplicable! " + attr.toString());
//                    
//                }
                Item taxon = attr.getItem();

                // Skip any attributes that pertain to taxa that are not
                // available
                if (!availableTaxa.contains(taxon)) {
                    continue;
                }

                if (attr.isUnknown() || (attr.isInapplicable())) {
                    unsuitableCharacters.add(ch);
                    continue charLoop;
                }

                Pair<boolean[], Integer> statePresencePair = Best.getStatePresenceForAttribute(attr, totalNumStates, OrderingType.BEST, null);

                boolean[] statePresence = statePresencePair.getFirst();
                int numStatesPresent = statePresencePair.getSecond();

                // work out size of character subgroups.
                for (int i = 0; i < totalNumStates; i++) {
                    if (statePresence[i] == true) {
                        subgroupsNumTaxa[i]++;

                        // frequency of items with current state of current
                        // character
                        // double stateFrequency = 1.0 / (double)
                        // numStatesPresent;
                        // stateFrequency += subgroupFrequencies[i];
                        // subgroupFrequencies[i] = stateFrequency;
                        subgroupAbundances[i] += itemAbundanceValues[attr.getItem().getItemNumber() - 1];
                    }
                }

            }

            // total number of non-empty character subgroups
            int totalNumSubgroups = 0;

            // work out sum of subgroup sizes and frequencies
            for (int i = 0; i < totalNumStates; i++) {
                sumNumTaxaInSubgroups += subgroupsNumTaxa[i];
                sumSubgroupsFrequencies += subgroupAbundances[i];

                if (subgroupsNumTaxa[i] > 0) {
                    totalNumSubgroups++;
                }
            }

            for (int i = 0; i < totalNumStates; i++) {
                int numTaxaInSubgroup = subgroupsNumTaxa[i];

                if (numTaxaInSubgroup == numAvailableTaxa) {
                    numSubgroupsSameSizeAsOriginalGroup++;
                }

                if (subgroupsNumTaxa[i] > 0) {
                    sup0 += (subgroupAbundances[i] * Best.log2(subgroupsNumTaxa[i]));
                }
            }

            sup0 = sup0 / sumSubgroupsFrequencies;

            dupf = 0;
            if (sumNumTaxaInSubgroups > numAvailableTaxa) {
                if (numSubgroupsSameSizeAsOriginalGroup == totalNumStates) {
                    unsuitableCharacters.add(ch);
                    continue charLoop;
                }

                // Why???
//                if (varyWt == 0) {
//                    continue;
//                }

                // TODO thing with preset characters here
                if (numSubgroupsSameSizeAsOriginalGroup != 0 && !allowImproperSubgroups) {
                    unsuitableCharacters.add(ch);
                    continue charLoop;
                }
                
                dupf = varw * (1 + 100 * numSubgroupsSameSizeAsOriginalGroup) * (sumNumTaxaInSubgroups - numAvailableTaxa)
                * ((numAvailableTaxa + 8) / (numAvailableTaxa * Best.log2(numAvailableTaxa)));
            }

            sep = -sup0 + Best.log2(numAvailableTaxa);

            // handle rounding errors
            if (Math.abs(sep) <= minimumSeparatingPower) {
                sep = 0.0;
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
                double suValC1 = suVals[c1.getCharacterId() - 1];
                double suValC2 = suVals[c2.getCharacterId() - 1];

                if (suValC1 == suValC2) {
                    return Integer.valueOf(c1.getCharacterId()).compareTo(Integer.valueOf(c2.getCharacterId()));
                } else {
                    return Double.valueOf(suValC1).compareTo(Double.valueOf(suValC2));
                }
            }
        });

        for (Character ch : sortedChars) {
            retMap.put(ch, suVals[ch.getCharacterId() - 1]);
        }

        return retMap;
    }

}

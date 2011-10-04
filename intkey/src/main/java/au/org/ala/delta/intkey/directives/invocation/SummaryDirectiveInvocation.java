package au.org.ala.delta.intkey.directives.invocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.util.Pair;

public class SummaryDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<Item> _taxa;

    private List<Character> _characters;

    public void setSelectedTaxaSpecimen(Pair<List<Item>, Boolean> pair) {
        this._taxa = pair.getFirst();
        // The specimen has no relevance here. Simply ignore it if it is
        // specified.
    }

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        RTFBuilder builder = new RTFBuilder();

        for (Character ch : _characters) {
            List<Attribute> attrs = context.getDataset().getAttributesForCharacter(ch.getCharacterId());

            if (ch instanceof MultiStateCharacter) {
                // processMultiStateCharacter(builder, (MultiStateCharacter) ch,
                // attrs);
            } else if (ch instanceof IntegerCharacter) {
                processIntegerCharacter(builder, (IntegerCharacter) ch, attrs);
            } else if (ch instanceof RealCharacter) {
                processRealCharacter(builder, attrs);
            } else if (ch instanceof TextCharacter) {
                processRealCharacter(builder, attrs);
            }
        }

        return true;
    }

    private void processMultiStateCharacter(RTFBuilder builder, MultiStateCharacter ch, List<Attribute> attrs) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        int[] stateTotals = new int[ch.getNumberOfStates()];

        for (Item taxon : _taxa) {
            MultiStateAttribute attr = (MultiStateAttribute) attrs.get(taxon.getItemNumber() - 1);
            if (attr.isUnknown() && !attr.isInapplicable()) {
                numUnknown++;
            } else if (attr.isUnknown() && attr.isInapplicable()) {
                numInapplicable++;
            } else {
                numRecorded++;
            }

            for (int i = 0; i < ch.getNumberOfStates(); i++) {
                int stateTotal = stateTotals[i];
                if (attr.isStatePresent(i + 1)) {
                    stateTotal++;
                }

                stateTotals[i] = stateTotal;
            }
        }

        System.out.println(String.format("%s. %s %s %s", ch.getCharacterId(), numUnknown, numInapplicable, numRecorded));

        for (int i = 0; i < ch.getNumberOfStates(); i++) {
            int stateTotal = stateTotals[i];
            if (stateTotal > 0) {
                System.out.println(String.format("%s (%s)", i + 1, stateTotal));
            }
        }
    }

    private void processIntegerCharacter(RTFBuilder builder, IntegerCharacter ch, List<Attribute> attrs) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        Map<Integer, Integer> valueTotals = new HashMap<Integer, Integer>();

        // Collect data points to use to calculate mean and standard deviation
        List<Double> valuesForMeanAndStdDev = new ArrayList<Double>();

        for (Item taxon : _taxa) {
            IntegerAttribute attr = (IntegerAttribute) attrs.get(taxon.getItemNumber() - 1);
            if (attr.isUnknown() && !attr.isInapplicable()) {
                numUnknown++;
                continue;
            } else if (attr.isUnknown() && attr.isInapplicable()) {
                numInapplicable++;
                continue;
            } else {
                numRecorded++;
            }

            Set<Integer> values = attr.getPresentValues();
            int valuesSum = 0;

            for (int value : values) {
                int valueTotal;
                if (valueTotals.containsKey(value)) {
                    valueTotal = valueTotals.get(value);
                } else {
                    valueTotal = 0;
                }

                valueTotal++;
                valueTotals.put(value, valueTotal);
                valuesSum += value;
            }

            // for calculating the mean and standard deviation, use the average
            // of the available values.
            valuesForMeanAndStdDev.add((double) valuesSum / (double) values.size());

        }

        System.out.println(String.format("%s. %s %s %s", ch.getCharacterId(), numUnknown, numInapplicable, numRecorded));

        List<Integer> sortedValues = new ArrayList<Integer>(valueTotals.keySet());
        Collections.sort(sortedValues);
        for (int value : sortedValues) {
            int valueTotal = valueTotals.get(value);
            if (valueTotal > 0) {
                System.out.println(String.format("%s (%s)", value, valueTotal));
            }
        }

        // Calculate mean and standard deviation
        double sum = 0;
        for (double value : valuesForMeanAndStdDev) {
            sum += value;
        }

        double mean = sum / valuesForMeanAndStdDev.size();
        
        double sumSquaredDifferences = 0;
        for (double value : valuesForMeanAndStdDev) {
            double diff = value - mean;
            sumSquaredDifferences += (diff * diff);
        }
        
        double variance = sumSquaredDifferences / (valuesForMeanAndStdDev.size() - 1);
        double stdDev = Math.sqrt(variance);

        System.out.println(mean);
        System.out.println(stdDev);
    }

    private void processRealCharacter(RTFBuilder builder, List<Attribute> attrs) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        double mean = 0;
        double ssq = 0;

        for (Item taxon : _taxa) {
            Attribute attr = attrs.get(taxon.getItemNumber() - 1);
            if (attr.isUnknown() && !attr.isInapplicable()) {
                numUnknown++;
            } else if (attr.isInapplicable()) {
                numInapplicable++;
            } else {
                numRecorded++;
            }

        }
    }

    private void processTextCharacter(RTFBuilder builder, TextCharacter ch, List<Attribute> attrs) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        for (Item taxon : _taxa) {
            Attribute attr = attrs.get(taxon.getItemNumber() - 1);
            if (attr.isUnknown() && !attr.isInapplicable()) {
                numUnknown++;
            } else if (attr.isInapplicable()) {
                numInapplicable++;
            } else {
                numRecorded++;
            }

        }

        System.out.println(String.format("%s. %s %s %s", ch.getCharacterId(), numUnknown, numInapplicable, numRecorded));
    }

}

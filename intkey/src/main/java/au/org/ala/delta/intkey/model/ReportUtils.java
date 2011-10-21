package au.org.ala.delta.intkey.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealAttribute;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.util.Pair;

/**
 * Utility methods used to generate reports
 * 
 * @author ChrisF
 * 
 */
public class ReportUtils {

    public static List<Object> generateMultiStateSummaryInformation(MultiStateCharacter ch, List<Attribute> attrs, List<Item> taxa) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        Map<Integer, Integer> stateDistribution = new HashMap<Integer, Integer>();

        for (Item taxon : taxa) {
            MultiStateAttribute attr = (MultiStateAttribute) attrs.get(taxon.getItemNumber() - 1);
            if (attr.isUnknown() && !attr.isInapplicable()) {
                numUnknown++;
                continue;
            } else if (attr.isUnknown() && attr.isInapplicable()) {
                numInapplicable++;
                continue;
            } else {
                numRecorded++;
            }

            Set<Integer> presentStates = attr.getPresentStates();

            for (int state : presentStates) {
                int stateTotal;
                if (stateDistribution.containsKey(state)) {
                    stateTotal = stateDistribution.get(state);
                } else {
                    stateTotal = 0;
                }

                stateTotal++;
                stateDistribution.put(state, stateTotal);
            }
        }

        return Arrays.asList(new Object[] { numUnknown, numInapplicable, numRecorded, stateDistribution });
    }

    public static List<Object> generateIntegerSummaryInformation(IntegerCharacter ch, List<Attribute> attrs, List<Item> taxa) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        Map<Integer, Integer> valueDistribution = new HashMap<Integer, Integer>();

        // Collect data points to use to calculate mean and standard deviation
        List<Double> valuesForMeanAndStdDev = new ArrayList<Double>();

        for (Item taxon : taxa) {
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
                if (valueDistribution.containsKey(value)) {
                    valueTotal = valueDistribution.get(value);
                } else {
                    valueTotal = 0;
                }

                valueTotal++;
                valueDistribution.put(value, valueTotal);
                valuesSum += value;
            }

            // for calculating the mean and standard deviation, use the average
            // of the available values.
            valuesForMeanAndStdDev.add((double) valuesSum / (double) values.size());
        }

        Pair<Double, Double> pairMeanStdDev = calcuateMeanAndStandardDeviation(valuesForMeanAndStdDev);
        double mean = pairMeanStdDev.getFirst();
        double stdDev = pairMeanStdDev.getSecond();

        return Arrays.asList(new Object[] { numUnknown, numInapplicable, numRecorded, valueDistribution, mean, stdDev });
    }

    public static List<Object> generateRealSummaryInformation(RealCharacter ch, List<Attribute> attrs, List<Item> taxa) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        int minValueTaxonIndex = 0;
        int maxValueTaxonIndex = 0;

        // Collect data points to use to calculate mean and standard deviation
        List<Double> valuesForMeanAndStdDev = new ArrayList<Double>();

        for (Item taxon : taxa) {
            RealAttribute attr = (RealAttribute) attrs.get(taxon.getItemNumber() - 1);
            if (attr.isUnknown() && !attr.isInapplicable()) {
                numUnknown++;
                continue;
            } else if (attr.isUnknown() && attr.isInapplicable()) {
                numInapplicable++;
                continue;
            } else {
                numRecorded++;
            }

            FloatRange presentRange = attr.getPresentRange();

            if (presentRange.getMinimumDouble() < minValue) {
                minValue = presentRange.getMinimumDouble();
                minValueTaxonIndex = taxon.getItemNumber();
            }

            if (presentRange.getMaximumDouble() > maxValue) {
                maxValue = presentRange.getMaximumDouble();
                maxValueTaxonIndex = taxon.getItemNumber();
            }

            // for calculating the mean and standard deviation, use the average
            // the two numbers that
            // specify the range.
            valuesForMeanAndStdDev.add((presentRange.getMinimumDouble() + presentRange.getMaximumDouble()) / 2);
        }

        Pair<Double, Double> pairMeanStdDev = calcuateMeanAndStandardDeviation(valuesForMeanAndStdDev);
        double mean = pairMeanStdDev.getFirst();
        double stdDev = pairMeanStdDev.getSecond();

        return Arrays.asList(new Object[] { numUnknown, numInapplicable, numRecorded, minValue, maxValue, minValueTaxonIndex, maxValueTaxonIndex, mean, stdDev });
    }

    public static List<Object> generateTextSummaryInformation(TextCharacter ch, List<Attribute> attrs, List<Item> taxa) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        for (Item taxon : taxa) {
            Attribute attr = attrs.get(taxon.getItemNumber() - 1);
            if (attr.isUnknown() && !attr.isInapplicable()) {
                numUnknown++;
            } else if (attr.isUnknown() && attr.isInapplicable()) {
                numInapplicable++;
            } else {
                numRecorded++;
            }
        }

        return Arrays.asList(new Object[] { numUnknown, numInapplicable, numRecorded });
    }

    private static Pair<Double, Double> calcuateMeanAndStandardDeviation(List<Double> valuesForMeanAndStdDev) {
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

        return new Pair<Double, Double>(mean, stdDev);
    }
}

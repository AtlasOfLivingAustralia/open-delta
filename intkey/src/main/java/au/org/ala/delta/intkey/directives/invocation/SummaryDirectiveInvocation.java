/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey.directives.invocation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
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
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.util.Pair;

public class SummaryDirectiveInvocation extends LongRunningIntkeyDirectiveInvocation<String> {

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
    public String doRunInBackground(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        RTFBuilder builder = new RTFBuilder();
        CharacterFormatter characterFormatter = new CharacterFormatter(false, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE_SURROUNDING_REPLACE_INNER, true, false);
        builder.startDocument();

        appendReportHeading(builder);
        
        int numCharsProcessed = 0;
        updateProgess(numCharsProcessed, _characters.size());

        for (Character ch : _characters) {
            int characterNumber = ch.getCharacterId();
            String characterDescription = characterFormatter.formatCharacterDescription(ch);
            List<Attribute> attrs = context.getDataset().getAllAttributesForCharacter(ch.getCharacterId());

            if (ch instanceof MultiStateCharacter) {
                MultiStateCharacter msChar = (MultiStateCharacter) ch;
                List<Object> summaryInformation = generateMultiStateSummaryInformation(msChar, attrs);

                int numUnknown = (Integer) summaryInformation.get(0);
                int numInapplicable = (Integer) summaryInformation.get(1);
                int numRecorded = (Integer) summaryInformation.get(2);
                Map<Integer, Integer> stateDistribution = (Map<Integer, Integer>) summaryInformation.get(3);

                String characterDetail = MessageFormat.format(UIUtils.getResourceString("SummaryDirective.MultiStateCharacterDetail"), msChar.getNumberOfStates());
                appendCharacterHeading(builder, characterNumber, characterDetail, characterDescription);
                builder.increaseIndent();
                appendUnknownInapplicableRecorded(builder, numUnknown, numInapplicable, numRecorded);

                // state distribution map will be empty for characters that are
                // inapplicable or unknown for
                // all the taxa being summarized.
                if (!stateDistribution.isEmpty()) {
                    appendDistributionOfValues(builder, stateDistribution);
                }

                builder.appendText("");
                builder.decreaseIndent();
            } else if (ch instanceof IntegerCharacter) {
                IntegerCharacter intChar = (IntegerCharacter) ch;
                List<Object> summaryInformation = generateIntegerSummaryInformation(intChar, attrs);

                int numUnknown = (Integer) summaryInformation.get(0);
                int numInapplicable = (Integer) summaryInformation.get(1);
                int numRecorded = (Integer) summaryInformation.get(2);
                Map<Integer, Integer> valueDistribution = (Map<Integer, Integer>) summaryInformation.get(3);
                double mean = (Double) summaryInformation.get(4);
                double stdDev = (Double) summaryInformation.get(5);

                String characterDetail = UIUtils.getResourceString("SummaryDirective.IntegerCharacterDetail");
                appendCharacterHeading(builder, characterNumber, characterDetail, characterDescription);
                builder.increaseIndent();
                appendUnknownInapplicableRecorded(builder, numUnknown, numInapplicable, numRecorded);

                // value distribution map will be empty for characters that are
                // inapplicable or unknown for
                // all the taxa being summarized.
                if (!valueDistribution.isEmpty()) {
                    appendDistributionOfValues(builder, valueDistribution);
                }

                builder.appendText(String.format("%s\\tab %.2f", UIUtils.getResourceString("SummaryDirective.Mean"), mean));
                builder.appendText(String.format("%s\\tab %.2f", UIUtils.getResourceString("SummaryDirective.StandardDeviation"), stdDev));
                builder.appendText("");
                builder.decreaseIndent();
            } else if (ch instanceof RealCharacter) {
                RealCharacter realChar = (RealCharacter) ch;
                List<Object> summaryInformation = generateRealSummaryInformation(realChar, attrs);

                int numUnknown = (Integer) summaryInformation.get(0);
                int numInapplicable = (Integer) summaryInformation.get(1);
                int numRecorded = (Integer) summaryInformation.get(2);
                double minValue = (Double) summaryInformation.get(3);
                double maxValue = (Double) summaryInformation.get(4);
                int minValueTaxonIndex = (Integer) summaryInformation.get(5);
                int maxValueTaxonIndex = (Integer) summaryInformation.get(6);
                double mean = (Double) summaryInformation.get(7);
                double stdDev = (Double) summaryInformation.get(8);

                String characterDetail = UIUtils.getResourceString("SummaryDirective.RealCharacterDetail");
                appendCharacterHeading(builder, characterNumber, characterDetail, characterDescription);
                builder.increaseIndent();
                appendUnknownInapplicableRecorded(builder, numUnknown, numInapplicable, numRecorded);

                builder.appendText(String.format("%s\\tab %.2f", UIUtils.getResourceString("SummaryDirective.Mean"), mean));
                builder.appendText(String.format("%s\\tab %.2f", UIUtils.getResourceString("SummaryDirective.StandardDeviation"), stdDev));
                builder.appendText(String.format("%s\\tab %.2f (%s)", UIUtils.getResourceString("SummaryDirective.Minimum"), minValue,
                        MessageFormat.format(UIUtils.getResourceString("SummaryDirective.ItemNumber"), minValueTaxonIndex)));
                builder.appendText(String.format("%s\\tab %.2f (%s)", UIUtils.getResourceString("SummaryDirective.Maximum"), maxValue,
                        MessageFormat.format(UIUtils.getResourceString("SummaryDirective.ItemNumber"), maxValueTaxonIndex)));

                builder.appendText("");
                builder.decreaseIndent();
            } else if (ch instanceof TextCharacter) {
                TextCharacter textChar = (TextCharacter) ch;
                List<Object> summaryInformation = generateTextSummaryInformation(textChar, attrs);

                int numUnknown = (Integer) summaryInformation.get(0);
                int numInapplicable = (Integer) summaryInformation.get(1);
                int numRecorded = (Integer) summaryInformation.get(2);

                String characterDetail = UIUtils.getResourceString("SummaryDirective.TextCharacterDetail");
                appendCharacterHeading(builder, characterNumber, characterDetail, characterDescription);
                builder.increaseIndent();
                appendUnknownInapplicableRecorded(builder, numUnknown, numInapplicable, numRecorded);

                builder.appendText("");
                builder.decreaseIndent();
            }
            updateProgess(++numCharsProcessed, _characters.size());
        }

        builder.endDocument();
        
        return builder.toString();
    }
    
    @Override
    protected void handleProcessingDone(IntkeyContext context, String result) {
        context.getUI().displayRTFReport(result, UIUtils.getResourceString("SummaryDirective.ReportTitle"));        
    }
    
    private void updateProgess(int numCharsProcessed, int totalNumChars) {
        int progressPercent = (int) Math.floor((((double) numCharsProcessed) / totalNumChars) * 100);
        progress(UIUtils.getResourceString("SummaryDirective.Progress.Generating", progressPercent));
    }

    private void appendReportHeading(RTFBuilder builder) {
        // output taxon numbers, with consecutive numbers grouped into ranges
        StringBuilder taxonRangeListBuilder = new StringBuilder();

        int startRange = 0;
        int previousTaxon = 0;
        for (Item taxon : _taxa) {
            int taxonNumber = taxon.getItemNumber();
            if (startRange == 0) {
                startRange = taxonNumber;
            } else if (taxonNumber != previousTaxon + 1) {
                taxonRangeListBuilder.append(" ");
                taxonRangeListBuilder.append(startRange);
                if (previousTaxon != startRange) {
                    taxonRangeListBuilder.append("-");
                    taxonRangeListBuilder.append(previousTaxon);
                }
                startRange = taxonNumber;
            }

            previousTaxon = taxonNumber;

            if (taxon == _taxa.get(_taxa.size() - 1)) {
                taxonRangeListBuilder.append(" ");
                taxonRangeListBuilder.append(startRange);
                if (taxonNumber != startRange) {
                    taxonRangeListBuilder.append("-");
                    taxonRangeListBuilder.append(taxonNumber);
                }
            }
        }

        builder.appendText(MessageFormat.format(UIUtils.getResourceString("SummaryDirective.ReportHeading"), taxonRangeListBuilder.toString()));
        builder.appendText(" ");
    }

    private void appendCharacterHeading(RTFBuilder builder, int characterNumber, String characterDetail, String characterDescription) {
        builder.appendText(String.format("#%s. (%s) - %s", characterNumber, characterDetail, characterDescription));
    }

    private void appendUnknownInapplicableRecorded(RTFBuilder builder, int numUnknown, int numInapplicable, int numRecorded) {
        builder.appendText(String.format("%s \\tab %s/%s/%s", UIUtils.getResourceString("SummaryDirective.UnknownInapplicableRecorded"), numUnknown, numInapplicable, numRecorded));
    }

    // Write out the distribution of values for an integer or multistate
    // character. Consecutive values that have the
    // same distribution count are grouped together. E.g. if states 1 and 2 both
    // have a distribution count of five,
    // this will be written out as 1-2(5).
    private void appendDistributionOfValues(RTFBuilder builder, Map<Integer, Integer> valueDistribution) {
        StringBuilder distributionStringBuilder = new StringBuilder();

        int startRange = 0;
        int rangeCount = 0;
        int prevValue = 0;

        List<Integer> sortedValues = new ArrayList<Integer>(valueDistribution.keySet());
        Collections.sort(sortedValues);
        for (int value : sortedValues) {
            int valueCount = valueDistribution.get(value);

            // first value
            if (value == sortedValues.get(0)) {
                rangeCount = valueCount;
                startRange = value;
            } else if (value != prevValue + 1 || valueCount != rangeCount) {
                appendDistributionForRange(distributionStringBuilder, startRange, prevValue, rangeCount);

                startRange = value;
                rangeCount = valueCount;
            }

            prevValue = value;

            // last value
            if (value == sortedValues.get(sortedValues.size() - 1)) {
                appendDistributionForRange(distributionStringBuilder, startRange, prevValue, rangeCount);
            }
        }

        builder.appendText(String.format("%s%s", UIUtils.getResourceString("SummaryDirective.DistributionOfValues"), distributionStringBuilder.toString()));
    }

    private void appendDistributionForRange(StringBuilder distributionStringBuilder, int startRange, int endRange, int rangeCount) {
        distributionStringBuilder.append("\\tab ");
        distributionStringBuilder.append(startRange);
        if (endRange != startRange) {
            distributionStringBuilder.append("-");
            distributionStringBuilder.append(endRange);
        }

        // Include the distribution count for the range only if the report if
        // for more than one taxon. For a summary report with a single taxon,
        // the distribution count will always be 1.
        if (_taxa.size() > 1) {
            distributionStringBuilder.append("(");
            distributionStringBuilder.append(rangeCount);
            distributionStringBuilder.append(")");
        }
    }

    private List<Object> generateMultiStateSummaryInformation(MultiStateCharacter ch, List<Attribute> attrs) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        Map<Integer, Integer> stateDistribution = new HashMap<Integer, Integer>();

        for (Item taxon : _taxa) {
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

    private List<Object> generateIntegerSummaryInformation(IntegerCharacter ch, List<Attribute> attrs) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        Map<Integer, Integer> valueDistribution = new HashMap<Integer, Integer>();

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

    private List<Object> generateRealSummaryInformation(RealCharacter ch, List<Attribute> attrs) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        int minValueTaxonIndex = 0;
        int maxValueTaxonIndex = 0;

        // Collect data points to use to calculate mean and standard deviation
        List<Double> valuesForMeanAndStdDev = new ArrayList<Double>();

        for (Item taxon : _taxa) {
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

    private List<Object> generateTextSummaryInformation(TextCharacter ch, List<Attribute> attrs) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        for (Item taxon : _taxa) {
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

    private Pair<Double, Double> calcuateMeanAndStandardDeviation(List<Double> valuesForMeanAndStdDev) {
        double sum = 0;
        for (double value : valuesForMeanAndStdDev) {
            sum += value;
        }

        double mean = sum / valuesForMeanAndStdDev.size();

        double stdDev = 0;

        if (valuesForMeanAndStdDev.size() > 1) {
            double sumSquaredDifferences = 0;
            for (double value : valuesForMeanAndStdDev) {
                double diff = value - mean;
                sumSquaredDifferences += (diff * diff);
            }

            double variance = sumSquaredDifferences / (valuesForMeanAndStdDev.size() - 1);
            stdDev = Math.sqrt(variance);
        }

        return new Pair<Double, Double>(mean, stdDev);
    }

}

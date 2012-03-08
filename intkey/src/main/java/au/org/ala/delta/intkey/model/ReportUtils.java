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
package au.org.ala.delta.intkey.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.best.DiagType;
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
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

/**
 * Utility methods used to generate reports
 * 
 * @author ChrisF
 * 
 */
public class ReportUtils {

    public static List<Object> generateMultiStateSummaryInformation(MultiStateCharacter ch, List<Attribute> attrs, List<Item> taxa, boolean outputToDeltaFormat) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        Map<Integer, Integer> stateDistribution = new HashMap<Integer, Integer>();

        for (Item taxon : taxa) {
            MultiStateAttribute attr = (MultiStateAttribute) attrs.get(taxon.getItemNumber() - 1);
            if (attr.isUnknown() && !attr.isInapplicable()) {
                numUnknown++;
                continue;
            } else if (attr.isInapplicable()) {
                if (outputToDeltaFormat && attr.getCharacter().getControllingCharacters().isEmpty()) {
                    numInapplicable++;
                } else if (!outputToDeltaFormat && attr.isUnknown()) {
                    numInapplicable++;
                }

                if (attr.isUnknown()) {
                    continue;
                }
            }

            numRecorded++;

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

    public static List<Object> generateIntegerSummaryInformation(IntegerCharacter ch, List<Attribute> attrs, List<Item> taxa, boolean outputToDeltaFormat) {
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
            } else if (attr.isInapplicable()) {
                if (outputToDeltaFormat && attr.getCharacter().getControllingCharacters().isEmpty()) {
                    numInapplicable++;
                } else if (!outputToDeltaFormat && attr.isUnknown()) {
                    numInapplicable++;
                }

                if (attr.isUnknown()) {
                    continue;
                }
            }

            numRecorded++;

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

    public static List<Object> generateRealSummaryInformation(RealCharacter ch, List<Attribute> attrs, List<Item> taxa, boolean outputToDeltaFormat) {
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
            } else if (attr.isInapplicable()) {
                if (outputToDeltaFormat && attr.getCharacter().getControllingCharacters().isEmpty()) {
                    numInapplicable++;
                } else if (!outputToDeltaFormat && attr.isUnknown()) {
                    numInapplicable++;
                }

                if (attr.isUnknown()) {
                    continue;
                }
            }

            numRecorded++;

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

    public static List<Object> generateTextSummaryInformation(TextCharacter ch, List<Attribute> attrs, List<Item> taxa, boolean outputToDeltaFormat) {
        int numUnknown = 0;
        int numInapplicable = 0;
        int numRecorded = 0;

        for (Item taxon : taxa) {
            Attribute attr = attrs.get(taxon.getItemNumber() - 1);
            if (attr.isUnknown() && !attr.isInapplicable()) {
                numUnknown++;
                continue;
            } else if (attr.isInapplicable()) {
                if (outputToDeltaFormat && attr.getCharacter().getControllingCharacters().isEmpty()) {
                    numInapplicable++;
                } else if (!outputToDeltaFormat && attr.isUnknown()) {
                    numInapplicable++;
                }

                if (attr.isUnknown()) {
                    continue;
                }
            }

            numRecorded++;
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

    public static void generateStatusDisplayContent(IntkeyContext context, RTFBuilder builder) {
        String onValue = UIUtils.getResourceString("Status.onValue");
        String offValue = UIUtils.getResourceString("Status.offValue");

        String displayUnknownsValue = context.displayUnknowns() ? onValue : offValue;
        String displayInapplicablesValue = context.displayInapplicables() ? onValue : offValue;
        String displayCommentsValue = context.displayComments() ? onValue : offValue;
        String displayContinuousValue = context.displayContinuous() ? onValue : offValue;
        String displayEndIdentifyValue = context.displayEndIdentify() ? onValue : offValue;
        String displayKeywordsValue = context.displayKeywords() ? onValue : offValue;
        String displayLogValue = context.getUI().isLogVisible() ? onValue : offValue;
        String displayInputValue = context.displayInput() ? onValue : offValue;
        String displayNumberingValue = context.displayNumbering() ? onValue : offValue;

        String displayImagesValue;
        switch (context.getImageDisplayMode()) {
        case AUTO:
            displayImagesValue = UIUtils.getResourceString("Status.Display.imageDisplayAuto");
            break;
        case MANUAL:
            displayImagesValue = UIUtils.getResourceString("Status.Display.imageDisplayManual");
            break;
        case OFF:
            displayImagesValue = offValue;
            break;
        default:
            throw new IllegalArgumentException("Unrecognized image display mode");
        }

        String displayScaledValue = context.displayScaled() ? onValue : offValue;

        builder.appendText(UIUtils.getResourceString("Status.Display.content", displayUnknownsValue, displayInapplicablesValue, displayCommentsValue, displayContinuousValue, displayEndIdentifyValue,
                displayKeywordsValue, displayLogValue, displayInputValue, displayNumberingValue, displayImagesValue, displayScaledValue));

    }

    public static void generateStatusIncludeCharactersContent(IntkeyContext context, RTFBuilder builder) {
        List<Character> includedCharacters = context.getIncludedCharacters();
        List<Integer> includedCharacterNumbers = new ArrayList<Integer>();
        for (Character ch : includedCharacters) {
            includedCharacterNumbers.add(ch.getCharacterId());
        }

        String formattedCharacterNumbers = Utils.formatIntegersAsListOfRanges(includedCharacterNumbers);

        builder.appendText(UIUtils.getResourceString("Status.IncludeCharacters.content", includedCharacters.size(), formattedCharacterNumbers));
    }

    public static void generateStatusIncludeTaxaContent(IntkeyContext context, RTFBuilder builder) {
        List<Item> includedTaxa = context.getIncludedTaxa();
        List<Integer> includedTaxaNumbers = new ArrayList<Integer>();
        for (Item taxon : includedTaxa) {
            includedTaxaNumbers.add(taxon.getItemNumber());
        }

        String formattedTaxaNumbers = Utils.formatIntegersAsListOfRanges(includedTaxaNumbers);

        builder.appendText(UIUtils.getResourceString("Status.IncludeTaxa.content", includedTaxa.size(), formattedTaxaNumbers));
    }

    public static void generateStatusExcludeCharactersContent(IntkeyContext context, RTFBuilder builder) {
        List<Character> excludedCharacters = context.getExcludedCharacters();
        List<Integer> excludedCharacterNumbers = new ArrayList<Integer>();
        for (Character ch : excludedCharacters) {
            excludedCharacterNumbers.add(ch.getCharacterId());
        }

        String formattedCharacterNumbers = Utils.formatIntegersAsListOfRanges(excludedCharacterNumbers);

        builder.appendText(UIUtils.getResourceString("Status.ExcludeCharacters.content", excludedCharacters.size(), formattedCharacterNumbers));

    }

    public static void generateStatusExcludeTaxaContent(IntkeyContext context, RTFBuilder builder) {
        List<Item> excludedTaxa = context.getExcludedTaxa();
        List<Integer> excludedTaxaNumbers = new ArrayList<Integer>();
        for (Item taxon : excludedTaxa) {
            excludedTaxaNumbers.add(taxon.getItemNumber());
        }

        String formattedTaxaNumbers = Utils.formatIntegersAsListOfRanges(excludedTaxaNumbers);

        builder.appendText(UIUtils.getResourceString("Status.ExcludeTaxa.content", excludedTaxa.size(), formattedTaxaNumbers));
    }

    public static void generateStatusFileContent(IntkeyContext context, RTFBuilder builder) {
        File journalFile = context.getJournalFile();
        File logFile = context.getLogFile();
        File outputFile = context.getOutputFile();

        builder.appendText(UIUtils.getResourceString("Status.Files.heading"));

        if (journalFile == null && logFile == null && outputFile == null) {
            builder.appendText(UIUtils.getResourceString("Status.Files.noFiles"));
        }

        if (journalFile != null) {
            builder.appendText(RTFUtils.escapeRTF(UIUtils.getResourceString("Status.Files.journalFile", journalFile.getAbsolutePath())));
        }

        if (logFile != null) {
            builder.appendText(RTFUtils.escapeRTF(UIUtils.getResourceString("Status.Files.logFile", logFile.getAbsolutePath())));
        }

        if (outputFile != null) {
            builder.appendText(RTFUtils.escapeRTF(UIUtils.getResourceString("Status.Files.outputFile", outputFile.getAbsolutePath())));
        }
    }

    public static void generateStatusSetContent(IntkeyContext context, RTFBuilder builder) {
        builder.appendText(UIUtils.getResourceString("Status.Set.heading"));

        String autoToleranceSetting = context.isAutoTolerance() ? UIUtils.getResourceString("Status.onValue") : UIUtils.getResourceString("Status.offValue");
        int stopBestSetting = context.getStopBest();
        double rbaseSetting = context.getRBase();
        int toleranceSetting = context.getTolerance();
        double varywt = context.getVaryWeight();
        // TODO need to implement set demonstration
        String demonstrationSetting = context.isDemonstrationMode() ? UIUtils.getResourceString("Status.onValue") : UIUtils.getResourceString("Status.offValue");
        String imagePaths = RTFUtils.escapeRTF(StringUtils.join(context.getImageSettings().getResourcePathLocations(), ";"));
        String infoPaths = RTFUtils.escapeRTF(StringUtils.join(context.getInfoSettings().getResourcePathLocations(), ";"));

        builder.appendText(UIUtils.getResourceString("Status.Set.line1", autoToleranceSetting, stopBestSetting, rbaseSetting, toleranceSetting, varywt, demonstrationSetting, imagePaths, infoPaths));

        StringBuilder matchValueBuilder = new StringBuilder();
        if (context.getMatchInapplicables()) {
            matchValueBuilder.append(UIUtils.getResourceString("Status.Set.inapplicablesMatchValue"));
            matchValueBuilder.append(" ");
        }

        if (context.getMatchUnknowns()) {
            matchValueBuilder.append(UIUtils.getResourceString("Status.Set.unknownsMatchValue"));
            matchValueBuilder.append(" ");
        }

        switch (context.getMatchType()) {
        case OVERLAP:
            matchValueBuilder.append(UIUtils.getResourceString("Status.Set.overlapMatchValue"));
            break;
        case SUBSET:
            matchValueBuilder.append(UIUtils.getResourceString("Status.Set.subsetMatchValue"));
            break;
        case EXACT:
            matchValueBuilder.append(UIUtils.getResourceString("Status.Set.exactMatchValue"));
            break;
        default:
            throw new IllegalArgumentException("Unrecognized match type");
        }

        builder.appendText(UIUtils.getResourceString("Status.Set.matchSettings", matchValueBuilder.toString()));

        int diagLevel = context.getDiagLevel();
        String diagTypeString;
        if (context.getDiagType() == DiagType.SPECIMENS) {
            diagTypeString = UIUtils.getResourceString("Status.Set.specimensDiagType");
        } else {
            diagTypeString = UIUtils.getResourceString("Status.Set.taxaDiagType");
        }

        builder.appendText(UIUtils.getResourceString("Status.Set.diagSettings", diagLevel, diagTypeString));

        builder.appendText(UIUtils.getResourceString("Status.Set.reliabilities", buildReliabilitiesString(context.getDataset().getCharactersAsList())));

        Set<Character> exactCharacters = context.getExactCharacters();
        List<Integer> exactCharacterNumbers = new ArrayList<Integer>();
        for (Character ch : exactCharacters) {
            exactCharacterNumbers.add(ch.getCharacterId());
        }
        Collections.sort(exactCharacterNumbers);

        String exactCharacterNumbersAsString;
        if (exactCharacterNumbers.isEmpty()) {
            exactCharacterNumbersAsString = UIUtils.getResourceString("Status.Set.emptyCharacterSet");
        } else {
            exactCharacterNumbersAsString = Utils.formatIntegersAsListOfRanges(exactCharacterNumbers);
        }

        builder.appendText(UIUtils.getResourceString("Status.Set.exactCharacters", exactCharacterNumbersAsString));

        List<Integer> fixedCharacterNumbers = context.getFixedCharactersList();
        Collections.sort(fixedCharacterNumbers);

        String fixedCharacterNumbersAsString;
        if (fixedCharacterNumbers.isEmpty()) {
            fixedCharacterNumbersAsString = UIUtils.getResourceString("Status.Set.emptyCharacterSet");
        } else {
            fixedCharacterNumbersAsString = Utils.formatIntegersAsListOfRanges(fixedCharacterNumbers);
        }

        builder.appendText(UIUtils.getResourceString("Status.Set.fixedCharacters", fixedCharacterNumbersAsString));
    }
    
    private static String buildReliabilitiesString(List<Character> characters) {
        StringBuilder builder = new StringBuilder();

        int startRangeCharacterNumber = 0;
        float rangeReliabilityValue = 0;
        int prevCharacterNumber = 0;

        for (int i = 0; i < characters.size(); i++) {
            Character ch = characters.get(i);
            int characterNumber = ch.getCharacterId();
            float charReliabilityValue = ch.getReliability();

            // First character
            if (i == 0) {
                startRangeCharacterNumber = characterNumber;
                rangeReliabilityValue = charReliabilityValue;
            } else if (charReliabilityValue != rangeReliabilityValue) {
                appendReliabilityForCharacterRange(builder, startRangeCharacterNumber, prevCharacterNumber, rangeReliabilityValue);
                startRangeCharacterNumber = characterNumber;
                rangeReliabilityValue = charReliabilityValue;
            }

            prevCharacterNumber = characterNumber;

            // Last character
            if (i == characters.size() - 1) {
                appendReliabilityForCharacterRange(builder, startRangeCharacterNumber, prevCharacterNumber, rangeReliabilityValue);
            }
        }

        return builder.toString();
    }

    private static void appendReliabilityForCharacterRange(StringBuilder builder, int startRange, int endRange, float reliabilityValue) {
        if (startRange == endRange) {
            builder.append(String.format("%d,%.1f", startRange, reliabilityValue));
        } else {
            builder.append(String.format("%d-%d,%.1f", startRange, endRange, reliabilityValue));
        }

        builder.append(" ");
    }

}

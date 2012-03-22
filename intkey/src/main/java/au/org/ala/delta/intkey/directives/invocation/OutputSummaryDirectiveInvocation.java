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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.ReportUtils;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.util.Pair;

public class OutputSummaryDirectiveInvocation extends IntkeyDirectiveInvocation {

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
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {

        StringBuilder builder = new StringBuilder();

        if (context.getLastOutputLineWasComment()) {
            context.setLastOutputLineWasComment(false);
        } else {
            builder.append("Output Summary\n");
        }

        int columnNumber = 0;

        for (Character ch : _characters) {
            if (ch instanceof TextCharacter) {
                continue;
            }

            int characterNumber = ch.getCharacterId();
            List<Attribute> attrs = context.getDataset().getAllAttributesForCharacter(ch.getCharacterId());

            if (ch instanceof MultiStateCharacter) {
                MultiStateCharacter msChar = (MultiStateCharacter) ch;

                List<Object> multiStateSummaryInformation = ReportUtils.generateMultiStateSummaryInformation((MultiStateCharacter) ch, attrs, _taxa, true);
                int numUnknown = (Integer) multiStateSummaryInformation.get(0);
                int numInapplicable = (Integer) multiStateSummaryInformation.get(1);
                int numRecorded = (Integer) multiStateSummaryInformation.get(2);
                Map<Integer, Integer> stateDistribution = (Map<Integer, Integer>) multiStateSummaryInformation.get(3);

                if (numInapplicable == 0 && numRecorded == 0) {
                    continue;
                }

                builder.append(characterNumber);
                builder.append(",");

                boolean statesWritten = false;
                for (int i = 0; i < msChar.getNumberOfStates(); i++) {
                    int stateNumber = i + 1;

                    if (stateDistribution.containsKey(stateNumber)) {

                        if (statesWritten) {
                            builder.append("/");
                        }

                        if (stateDistribution.get(stateNumber) == _taxa.size()) {
                            builder.append(stateNumber);
                        } else {
                            builder.append(stateNumber);

                            // Include that distribution value for the range
                            // only if the report is being run for more than one
                            // taxon.
                            // If only one taxon is provided to the report, the
                            // distribution value will always be 1.
                            if (_taxa.size() > 1) {
                                builder.append("<");
                                builder.append(stateDistribution.get(stateNumber));

                                if (numRecorded < _taxa.size()) {
                                    builder.append("/");
                                    builder.append(numRecorded);
                                }

                                builder.append(">");
                            }
                        }
                        statesWritten = true;
                    }
                }

                if (numInapplicable > 0) {
                    if (statesWritten) {
                        builder.append("/");
                    }
                    builder.append("-");
                }
            } else if (ch instanceof IntegerCharacter) {
                IntegerCharacter intChar = (IntegerCharacter) ch;
                List<Object> summaryInformation = ReportUtils.generateIntegerSummaryInformation(intChar, attrs, _taxa, true);

                int numUnknown = (Integer) summaryInformation.get(0);
                int numInapplicable = (Integer) summaryInformation.get(1);
                int numRecorded = (Integer) summaryInformation.get(2);
                Map<Integer, Integer> valueDistribution = (Map<Integer, Integer>) summaryInformation.get(3);
                double mean = (Double) summaryInformation.get(4);

                if (numInapplicable == 0 && numRecorded == 0) {
                    continue;
                }

                builder.append(characterNumber);
                builder.append(",");

                if (numInapplicable == _taxa.size()) {
                    builder.append("-");
                } else {
                    // group consecutive integer values that have the same
                    // distribution into ranges.
                    List<IntRange> rangesList = new ArrayList<IntRange>();
                    Map<IntRange, Integer> rangeDistributionValues = new HashMap<IntRange, Integer>();

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
                            IntRange range = new IntRange(startRange, prevValue);
                            rangesList.add(range);
                            rangeDistributionValues.put(range, rangeCount);

                            startRange = value;
                            rangeCount = valueCount;
                        }

                        prevValue = value;

                        // last value
                        if (value == sortedValues.get(sortedValues.size() - 1)) {
                            IntRange range = new IntRange(startRange, prevValue);
                            rangesList.add(range);
                            rangeDistributionValues.put(range, rangeCount);
                        }
                    }

                    if (rangesList.size() <= 8) {
                        // output complete data

                        for (int i = 0; i < rangesList.size(); i++) {
                            IntRange range = rangesList.get(i);
                            int distributionValue = rangeDistributionValues.get(range);

                            if (i > 0) {
                                builder.append("/");
                            }

                            builder.append(range.getMinimumInteger());
                            if (range.getMaximumInteger() != range.getMinimumInteger()) {
                                builder.append("-");
                                builder.append(range.getMaximumInteger());
                            }

                            // Include that distribution value for the range
                            // only if the report is being run for more than one
                            // taxon.
                            // If only one taxon is provided to the report, the
                            // distribution value will always be 1.
                            if (_taxa.size() > 1) {
                                builder.append("<");
                                builder.append(distributionValue);

                                if (numRecorded < _taxa.size()) {
                                    builder.append("/");
                                    builder.append(numRecorded);
                                }

                                builder.append(">");
                            }
                        }
                    } else {
                        // output mean etc.
                        builder.append(intChar.getMinimumValue());
                        builder.append("-");
                        builder.append(Math.round(mean));
                        builder.append("-");
                        builder.append(intChar.getMaximumValue());
                    }
                }
            } else if (ch instanceof RealCharacter) {
                RealCharacter realChar = (RealCharacter) ch;
                List<Object> realSummaryInformation = ReportUtils.generateRealSummaryInformation(realChar, attrs, _taxa, true);
                int numInapplicable = (Integer) realSummaryInformation.get(1);
                int numUnknown = (Integer) realSummaryInformation.get(0);
                int numRecorded = (Integer) realSummaryInformation.get(2);
                double minValue = (Double) realSummaryInformation.get(3);
                double maxValue = (Double) realSummaryInformation.get(4);
                double mean = (Double) realSummaryInformation.get(7);

                if (numRecorded == 0 && numInapplicable < _taxa.size()) {
                    continue;
                }

                builder.append(characterNumber);
                builder.append(",");

                if (numInapplicable == _taxa.size()) {
                    builder.append("-");
                } else {
                    // Special case for an integer character represented in the
                    // intkey dataset as a real character.
                    if (realChar.isIntegerRepresentedAsReal()) {
                        long longMin = Math.round(minValue);
                        long longMax = Math.round(maxValue);
                        long longMean = Math.round(mean);

                        if (longMin < longMean) {
                            builder.append(String.format("%d-", longMin));
                        }

                        builder.append(String.format("%d", longMean));

                        if (longMax > longMean) {
                            builder.append(String.format("-%d", longMax));
                        }

                    } else {
                        boolean meanOutput = false;
                        if (minValue < mean) {
                            builder.append(String.format("%.2f", minValue));
                            builder.append("-");
                        }

                        if (_taxa.size() > 1 || (_taxa.size() == 1 && minValue == maxValue)) {
                            builder.append(String.format("%.2f", mean));
                            meanOutput = true;
                        }

                        if (maxValue > mean) {
                            if (meanOutput) {
                                builder.append("-");
                            }
                            builder.append(String.format("%.2f", maxValue));
                        }
                    }
                }

            } else {
                throw new IllegalArgumentException("Unrecognised character type");
            }

            if (columnNumber == 4) {
                builder.append("\n");
                columnNumber = 0;
            } else {
                builder.append(" ");
                columnNumber++;
            }
        }

        try {
            context.appendTextToOutputFile(builder.toString());
        } catch (IllegalStateException ex) {
            throw new IntkeyDirectiveInvocationException("NoOutputFileOpen.error");
        }

        return true;
    }
}

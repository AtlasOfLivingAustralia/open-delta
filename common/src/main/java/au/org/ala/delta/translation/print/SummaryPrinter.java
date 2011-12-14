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
package au.org.ala.delta.translation.print;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.Range;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericAttribute;
import au.org.ala.delta.model.NumericRange;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.translation.AbstractIterativeTranslator;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.PrintFile;

/**
 * The SummaryPrinter is responsible for producing the output of the
 * PRINT SUMMARY directive.
 */
public class SummaryPrinter extends AbstractIterativeTranslator  {

	
	class CharacterStats {
		IdentificationKeyCharacter _character;
		int uncodedOrUnknown;
		int notApplicable;
		int coded;
		int variable;
		int keyStatesVariable;
		int[] stateDistribution;
		int[] keyStateDistribution;
		double max;
		double min;
		int minItem;
		int maxItem;
		List<Double> values = new ArrayList<Double>();
		
		public CharacterStats(IdentificationKeyCharacter character) {
			_character = character;
			keyStateDistribution = new int[character.getNumberOfKeyStates()];
			Arrays.fill(keyStateDistribution, 0);
			min = Double.MAX_VALUE;
			max = -Double.MAX_VALUE;
			if (_character.getCharacterType().isMultistate()) {
				MultiStateCharacter multiStateChar = (MultiStateCharacter)getCharacter();
				stateDistribution = new int[multiStateChar.getNumberOfStates()];
				Arrays.fill(stateDistribution, 0);
				
			}
		}
		
		public au.org.ala.delta.model.Character getCharacter() {
			return _character.getCharacter();
		}
		
		public IdentificationKeyCharacter getKeyChar() {
			return _character;
		}
		
		public void accumulate(double value) {
			values.add(value);
		}
		
		public double getMean() {
			double sum = 0;
			for (double value : values) {
				sum += value;
			}
			return sum / values.size();
		}
		
		public double getStdDev() {
			if (values.size() < 2) {
				return 0;
			}
			double mean = getMean();
			double sum =0;
			for (double value : values) {
				sum += Math.pow(value-mean, 2);
			}
			return Math.sqrt(sum/(values.size()-1));
		}
		
		public int minInt() {
			return (int)min;
		}
		
		public int maxInt() {
			return (int)max;
		}
	}
	
	private DeltaContext _context;
	private FilteredDataSet _dataSet;
	private PrintFile _printFile;
	private Map<Integer, CharacterStats> _stats;
	
	public SummaryPrinter(DeltaContext context, FilteredDataSet dataSet, PrintFile printFile) {
		_context = context;
		_dataSet = dataSet;
		_stats = new HashMap<Integer, CharacterStats>();
		_printFile = printFile;
	}
	
	@Override
	public void beforeItem(Item item) {
		
		Iterator<IdentificationKeyCharacter> chars = _dataSet.identificationKeyCharacterIterator();
		while (chars.hasNext()) {
			IdentificationKeyCharacter character = chars.next();
			
			updateStats(item, getStats(character));
		}
	}
	
	private CharacterStats getStats(IdentificationKeyCharacter character) {
		int charNum = character.getCharacter().getCharacterId();
		CharacterStats stats = _stats.get(charNum);
		if (stats == null) {
			stats = new CharacterStats(character);
			_stats.put(charNum, stats);
		}
		return stats;
	}
	
	private void updateStats(Item item, CharacterStats stats) {
		
		Attribute attribute = item.getAttribute(stats.getCharacter());
		if (isInapplicable(attribute)) {
			stats.notApplicable++;
		}
		else if (_dataSet.isUncoded(item, stats.getCharacter())) {
			stats.uncodedOrUnknown++;
		}
		else if (attribute.isVariable()) {
			stats.variable++;
		}
		else {
			stats.coded++;
			
			IdentificationKeyCharacter keyChar = stats.getKeyChar();
			if (keyChar == null || keyChar.getCharacterType().isText()) {
				return;
			}
			List<Integer> states = null;
			if (attribute instanceof NumericAttribute) {
				
				NumericAttribute numericAttribute = (NumericAttribute)attribute;
				states = keyChar.getPresentStates(numericAttribute);
				if (states.size() > 1) {
					stats.keyStatesVariable++;
				}
				List<NumericRange> values = numericAttribute.getNumericValue();
				double count = 0;
				double sum = 0;
				for (NumericRange value : values) {
					Range range;
					
					if (_context.getUseNormalValues(keyChar.getCharacterNumber())) {
						range = value.getNormalRange();
					}
					else {
						range = value.getFullRange();
					}
					double min = range.getMinimumDouble();
					if (stats.min > min) {
						stats.min = min;
						stats.minItem = item.getItemNumber();
					}
					
					double max = range.getMaximumDouble();
					if (stats.max < max) {
						stats.max = max;
						stats.maxItem = item.getItemNumber();
					}
					range = value.getNormalRange();
					
					if (value.hasMiddleValue()) {
						 count++;
						 sum += value.getMiddle().doubleValue();
					}
					else if (range.getMinimumDouble() == range.getMaximumDouble()) {
						count++;
						sum += range.getMinimumDouble();
					}
					else {
						count +=2;
						sum += range.getMinimumDouble();
						sum += range.getMaximumDouble();
					}
					
				}
				if (count > 0) {
					stats.accumulate(sum/(double)count);
				}
				
				for (int state : states) {
					stats.keyStateDistribution[state-1]++;
				}
				
			}
			else if (attribute instanceof MultiStateAttribute) {
				if (keyChar.getNumberOfKeyStates() > 0) {
					states = keyChar.getPresentStates((MultiStateAttribute)attribute);
					if (states.size() > 1) {
						stats.keyStatesVariable++;
					}
					for (int state : states) {
						stats.keyStateDistribution[state-1]++;
					}
				}
				List<Integer> originalStates = ((MultiStateAttribute) attribute).getPresentStatesAsList();
				if (originalStates.size() > 1) {
					stats.variable++;
				}
				for (int state : originalStates) {
					stats.stateDistribution[state-1]++;
				}
			}
			
		}
		
	}
	
	private boolean isInapplicable(Attribute attribute) {
		if (!attribute.isExclusivelyInapplicable(true)) {
			ControllingInfo controllingInfo = _dataSet.checkApplicability(
					attribute.getCharacter(), attribute.getItem());
			return (controllingInfo.isInapplicable());
		}
		return true;
	}
	
	@Override
	public void afterLastItem() {
		writeHeader();
		writeStats();
	}
	
	private void writeStats() {
		
		Iterator<IdentificationKeyCharacter> chars = _dataSet.identificationKeyCharacterIterator();
		while (chars.hasNext()) {
			IdentificationKeyCharacter character = chars.next();
			
			writeStats(getStats(character));
		}
		
	}
	
	private void writeHeader() {
		_printFile.setIndent(0);
		_printFile.setTrimInput(false);
		_printFile.outputLine(_context.getHeading(HeadingType.HEADING));
		_printFile.writeBlankLines(1, 0);
		_printFile.setIndent(1);
		_printFile.outputLine("Summary.");
		_printFile.writeBlankLines(1, 0);
		_printFile.outputLine(String.format("Characters %7d Read %7d Included.", _dataSet.getNumberOfCharacters(), _dataSet.getNumberOfFilteredCharacters()));
		_printFile.outputLine(String.format("Items %12d Read %7d Included.", _dataSet.getMaximumNumberOfItems(), _dataSet.getNumberOfFilteredItems()));
		_printFile.writeBlankLines(1,0);
		

	}
	
	private void writeStats(CharacterStats stats) {
		CharacterType type = stats.getCharacter().getCharacterType();
		_printFile.setIndentOnLineWrap(true);
		_printFile.setLineWrapIndent(29);
		_printFile.outputLine(String.format("Character %20d", stats.getCharacter().getCharacterId()));
		_printFile.outputLine(String.format("  Type %23s", type.toTypeCode()));
		_printFile.outputLine(String.format("  Uncoded or unknown %9d", stats.uncodedOrUnknown));
		
		if (!type.isText()) {
			_printFile.outputLine(String.format("  Not applicable %13d", stats.notApplicable));
			
			if (stats.coded > 0) { 
				if (type.isNumeric()) {
					if (stats.min != Double.MAX_VALUE) {
						_printFile.outputLine(String.format("  Mean %23.2f", stats.getMean()));
						_printFile.outputLine(String.format("  Std deviation %14.2f", stats.getStdDev()));
						
						if (type == CharacterType.IntegerNumeric) {
						    _printFile.outputLine(String.format("  Minimum %20d (Item %d)", stats.minInt(), stats.minItem));
						    _printFile.outputLine(String.format("  Maximum %20d (Item %d)", stats.maxInt(), stats.maxItem));
						}
						else {
							_printFile.outputLine(String.format("  Minimum %20.2f (Item %d)", stats.min, stats.minItem));
						    _printFile.outputLine(String.format("  Maximum %20.2f (Item %d)", stats.max, stats.maxItem));
						}
					}
					
				}
				if (type.isMultistate()) {
					MultiStateCharacter multiStateChar = (MultiStateCharacter)stats.getCharacter();
					_printFile.outputLine(String.format("  Number of states %11d", multiStateChar.getNumberOfStates()));
				    StringBuilder states = new StringBuilder();
				    for (int count : stats.stateDistribution) {
					    states.append(String.format("%5d", count));
				    }
				    _printFile.outputLine("  Distribution of states "+states.toString());
				    if (stats._character.getNumberOfKeyStates() > 0) { 
				    	_printFile.outputLine(String.format("  Items variable %13d",stats.variable));
				    }
				}
				if (stats._character.getNumberOfKeyStates() > 0) {
				    _printFile.outputLine(String.format("  Key states %17d", stats.getKeyChar().getNumberOfKeyStates()));
				    StringBuilder states = new StringBuilder();
				    for (int count : stats.keyStateDistribution) {
					    states.append(String.format("%5d", count));
				    }
				    _printFile.outputLine("  Distribution of states "+states.toString());
					
				}
				
				_printFile.outputLine(String.format("  Items coded %16d",stats.coded));
				if (stats._character.getNumberOfKeyStates() > 0) { 
					_printFile.outputLine(String.format("  Items variable %13d",stats.keyStatesVariable));
				}
				else if (type.isMultistate()) {
					_printFile.outputLine(String.format("  Items variable %13d",stats.variable));
				}
				
			}
		}
		_printFile.writeBlankLines(1,0);
	}
	
}

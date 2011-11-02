package au.org.ala.delta.dist;

import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.NumericAttribute;
import au.org.ala.delta.model.NumericRange;
import au.org.ala.delta.translation.FilteredCharacter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.FilteredItem;

/**
 * Does the work of calculating the distance matrix that is the main output
 * of the DIST program.
 */
public class DistanceMatrixCalculator {

	private DistContext _context;
	private FilteredDataSet _dataSet;
	private float[] _ranges;
	private MultiStateDifferenceCalculator _multistateDifferenceCalculator;
	
	public DistanceMatrixCalculator(DistContext context, FilteredDataSet dataSet) {
		_context = context;
		_dataSet = dataSet;
		initialiseRanges();
		if (context.getMatchOverlap()) {
			_multistateDifferenceCalculator = new MatchOverlapMultiStateDifferenceCalculator();
		}
		else {
			_multistateDifferenceCalculator = new DefaultMultiStateDifferenceCalculator();
		}
	}
	
	private void initialiseRanges() {
		_ranges = new float[_dataSet.getNumberOfCharacters()];
		
		Iterator<FilteredCharacter> characters = _dataSet.filteredCharacters();
		while (characters.hasNext()) {
			Character character = characters.next().getCharacter();
			float range = -1;
			if (character.getCharacterType().isNumeric()) {
				float min = Float.MAX_VALUE;
				float max = Float.MIN_VALUE;
				
				Iterator<FilteredItem> items = _dataSet.filteredItems();
				while (items.hasNext()) {
					FilteredItem item1 = items.next();
					
					NumericAttribute attribute = (NumericAttribute)item1.getItem().getAttribute(character);
					if (!attribute.isUnknown()) {
						float value = getSingleValue(attribute);
						if (value > -999f) {
							min = Math.min(min, value);
							max = Math.max(max, value);
						}
					}
				}
				range = Math.abs(max-min);
			}
			
			_ranges[character.getCharacterId()-1] = range;
		}
		
	}
	
	public DistanceMatrix calculateDistanceMatrix() {
		DistanceMatrix matrix = new DistanceMatrix(_dataSet.getNumberOfFilteredItems());
		
		Iterator<FilteredItem> items = _dataSet.filteredItems();
		while (items.hasNext()) {
			FilteredItem item1 = items.next();
			
			Iterator<FilteredItem> itemsToCompareAgainst = _dataSet.filteredItems();
			while (itemsToCompareAgainst.hasNext()) {
				FilteredItem item2 = itemsToCompareAgainst.next();
				
				if (item2.getItemNumber() > item1.getItemNumber()) {
					matrix.set(item1.getItemNumber(), item2.getItemNumber(), computeDistance(item1.getItem(), item2.getItem()));
				}
			}
		}
		
		return matrix;	
	}


	private float computeDistance(Item item1, Item item2) {
		float weightedSum = 0f;
		float sumOfWeights = 0f;
		int comparisonCount = 0;
		Iterator<FilteredCharacter> characters = _dataSet.filteredCharacters();
		while (characters.hasNext()) {
			Character character = characters.next().getCharacter();
			
			// The weight values are stored as reliabilities but the values are
			// not modified.
			float weight = character.getReliability();
			Attribute attribute1 = item1.getAttribute(character);
			Attribute attribute2 = item2.getAttribute(character);
			if (attribute1.isUnknown() || attribute2.isUnknown()) {
				continue;
			}
			float distance;
			if (character.getCharacterType().isNumeric()) {
				distance = computeCharacterDifference((NumericAttribute)attribute1, (NumericAttribute)attribute2);
			}
			else {
				distance = _multistateDifferenceCalculator.computeMultiStateDifference((MultiStateAttribute)attribute1, (MultiStateAttribute)attribute2);
			}
			weightedSum += weight * distance;
			sumOfWeights += weight;
			comparisonCount++;
		}
		if (comparisonCount < _context.getMinimumNumberOfComparisons()) {
			return Float.NaN;
		}
		else {
			return weightedSum / sumOfWeights;
		}
	}


	private float computeCharacterDifference(NumericAttribute attribute1, NumericAttribute attribute2) {
		int charNum = attribute1.getCharacter().getCharacterId();
		float range = _ranges[charNum-1];
		if (range == 0) {
			return 0;
		}
		
		return Math.abs(getSingleValue(attribute1) - getSingleValue(attribute2)) / range;
	}
	
	private float getSingleValue(NumericAttribute attribute) {
		List<NumericRange> values = attribute.getNumericValue();
		// if the attribute is not unknown we know it has 
		// a single value as CONFOR calculates it.
		return values.get(0).getNormalRange().getMaximumFloat();
	}
}

package au.org.ala.delta.translation.intkey;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.Range;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.intkey.WriteOnceIntkeyItemsFile;
import au.org.ala.delta.io.OutputFileSelector;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.NumericAttribute;
import au.org.ala.delta.model.NumericRange;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.translation.delta.DeltaWriter;
import au.org.ala.delta.translation.delta.ImageOverlayWriter;

/**
 * Writes the intkey items file using the data in a supplied DeltaContext and
 * associated data set.
 */
public class IntkeyItemsFileWriter {

	static final int INTEGER_RANGE_WARNING_THRESHOLD = 64;
	static final int INTEGER_RANGE_MAX_THRESHOLD = 200;
	
	private WriteOnceIntkeyItemsFile _itemsFile;
	private DeltaDataSet _dataSet;
	private DeltaContext _context;
	
	public IntkeyItemsFileWriter(DeltaContext context, WriteOnceIntkeyItemsFile itemsFile) {
		_itemsFile = itemsFile;
		_dataSet = context.getDataSet();
		_context = context;
	}
	
	public void writeItemDescrptions() {
		
		List<String> descriptions = new ArrayList<String>(_dataSet.getMaximumNumberOfItems());
		for (int i=1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			descriptions.add(_dataSet.getItem(i).getDescription());
		}
		_itemsFile.writeItemDescrptions(descriptions);
	}
	
	public void writeCharacterSpecs() {
		
		List<Integer> types = new ArrayList<Integer>(_dataSet.getNumberOfCharacters());
		List<Integer> states = new ArrayList<Integer>(_dataSet.getNumberOfCharacters());
		List<Float> reliabilities = new ArrayList<Float>(_dataSet.getNumberOfCharacters());
		
	    Iterator<IdentificationKeyCharacter> iterator = _context.identificationKeyCharacterIterator();
		while(iterator.hasNext()) {
			IdentificationKeyCharacter character = iterator.next();
			types.add(typeToInt(character.getCharacterType()));
			states.add(numStates(character));
			reliabilities.add((float)_context.getCharacterReliability(character.getCharacterNumber()));
			
		}
		_itemsFile.writeCharacterSpecs(types, states, reliabilities);	
	}
	
	private int typeToInt(CharacterType type) {
		switch (type) {
		case UnorderedMultiState:
			return 1;
		case OrderedMultiState:
			return 2;
		case IntegerNumeric:
			return 3;
		case RealNumeric:
			return 4;
		case Text:
			return 5;
		}
		
		throw new IllegalArgumentException("Invalid character type: "+type);
	}
	
	private int numStates(IdentificationKeyCharacter character) {
		if (character.getCharacterType().isMultistate()) {
			return character.getNumberOfStates();
		}
		else if (character.getCharacterType().isNumeric()) {
			return 1;
		}
		else {
			return 0;
		}
	}
	
	public void writeCharacterDependencies() {
		throw new NotImplementedException();
	}
	
	public void writeAttributeData() {
		
		Iterator<IdentificationKeyCharacter> keyChars = _context.identificationKeyCharacterIterator();
		List<IntRange> intRanges = new ArrayList<IntRange>();
		Set<Float> floats = new HashSet<Float>();
		while (keyChars.hasNext()) {
			IdentificationKeyCharacter keyChar = keyChars.next();
			IntRange minMax = new IntRange(0);
			if (keyChar.getCharacterType().isMultistate()) {
				writeMultiStateAttributes(keyChar);
			}	
			else if (keyChar.getCharacterType() == CharacterType.IntegerNumeric) {
				minMax = writeIntegerAttributes(keyChar.getCharacter());
			}
			else if (keyChar.getCharacterType() == CharacterType.RealNumeric) {
				List<FloatRange> attributeFloats = writeRealAttributes(keyChar.getCharacter());
				for (FloatRange range : attributeFloats) {
					if (range.getMinimumFloat() != Float.MAX_VALUE) {
						floats.add(range.getMinimumFloat());
					}
					if (range.getMaximumFloat() != Float.MAX_VALUE) {
						floats.add(range.getMaximumFloat());
					}
				}
			}
			else {
				writeTextAttributes(keyChar.getCharacter());
			}
			intRanges.add(minMax);
		}
		_itemsFile.writeMinMaxValues(intRanges);
		ArrayList<Float> keyStateBoundards = new ArrayList<Float>(floats);
		Collections.sort(keyStateBoundards);
		//_itemsFile.writeKeyStateBoundaries(keyStateBoundaries);
	}
	
	private void writeMultiStateAttributes(IdentificationKeyCharacter character) {
		
		int charNumber = character.getCharacterNumber();
		int numStates = character.getNumberOfStates();
		List<BitSet> attributes = new ArrayList<BitSet>();
		for (int i=1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			MultiStateAttribute attribute = (MultiStateAttribute)_dataSet.getAttribute(i, character.getCharacterNumber());
		
			List<Integer> states = character.getPresentStates(attribute);
			
			// Turn into bitset.
			BitSet bits = new BitSet();
			for (int state : states) {
				bits.set(state-1);
			}
			
			if (attribute.isInapplicable()) {
				bits.set(numStates);
			}
			attributes.add(bits);
		}
		
		_itemsFile.writeAttributeBits(charNumber, attributes, numStates+1);
	}
	
	private IntRange writeIntegerAttributes(Character character) {
		IntRange characterRange = determineIntegerRange(character);
		if (characterRange == null) {
			// The range was too large - treat this character as a real.
			writeRealAttributes(character);
		}
		else {
			int charNumber = character.getCharacterId();
			int numStates = characterRange.getMaximumInteger()-characterRange.getMinimumInteger();
			List<BitSet> attributes = new ArrayList<BitSet>();
			for (int i=1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
				
				// Turn into bitset.
				BitSet bits = new BitSet();
				IntegerAttribute attribute = (IntegerAttribute)_dataSet.getAttribute(i, charNumber);
				if (attribute.isUnknown()) {
					attributes.add(bits);
					continue;
				}
				
				if (attribute.isInapplicable()) {
					bits.set(numStates+2);
				}
				
				List<NumericRange> ranges = attribute.getNumericValue();
				
				for (NumericRange range : ranges) {
					Range usedRange;
					if (_context.getUseNormalValues()) {
						usedRange = range.getNormalRange();
					}
					else {
						usedRange = range.getFullRange();
					}
					
					for (int j=usedRange.getMinimumInteger(); j<=usedRange.getMaximumInteger(); j++) {
						if (j<characterRange.getMinimumInteger()) {
							bits.set(0);
						}
						else if (j<characterRange.getMaximumInteger()) {
							bits.set(j - characterRange.getMinimumInteger()+1);
						}
						else {
							bits.set(numStates+1);
						}
					}
				}
				attributes.add(bits);
				
			}
			
			_itemsFile.writeAttributeBits(charNumber, attributes, numStates+3);
		}
		return characterRange;
	}
	
	
	private IntRange determineIntegerRange(Character intChar) {
		
		Set<Integer> values = new HashSet<Integer>();
		boolean hasMultiRangeAttribute = populateValues(intChar.getCharacterId(), values);
		
		List<Integer> orderedValues = new ArrayList<Integer>(values);
		
		if (orderedValues.size() == 0) {
			return new IntRange(0);
		}
		Collections.sort(orderedValues);
		
		int min = orderedValues.get(0);
		int max = orderedValues.get(values.size()-1);
		
		int upperLimit = hasMultiRangeAttribute ? INTEGER_RANGE_MAX_THRESHOLD : INTEGER_RANGE_WARNING_THRESHOLD;
		if (max-min > upperLimit) {
			int index = Collections.binarySearch(orderedValues, min+upperLimit);
			if (index < 0) {
				index = -(index+1);
			}
			
			if (index > values.size()/2) {
				return null;
			}
			max = orderedValues.get(index);
		}
		return new IntRange(min, max);
	}

	/**
	 * Puts all of the values of an integer character into a List as a 
	 * part of the process of converting an integer character into the
	 * equivalent of a multistate character.  Note that CONFOR uses a
	 * fixed sized array of 200 values and starts discarding values after
	 * this which we are not doing.
	 * @param characterNumber the character to use.
	 * @param values the values of all of the attributes using the supplied
	 * character.
	 * @return true if one or more of the attributes have more than a single
	 * range of values encoded.
	 */
	private boolean populateValues(int characterNumber, Set<Integer> values) {
		boolean useNormalValues = _context.getUseNormalValues();
		
		boolean hasMultiRangeAttribute = false;
		for (int i=1; i<_dataSet.getMaximumNumberOfItems(); i++) {
		
			IntegerAttribute attribute = (IntegerAttribute)_dataSet.getAttribute(i, characterNumber);
			if (attribute == null || attribute.isUnknown() || attribute.isInapplicable() || attribute.isVariable()) {
				continue;
			}
			List<NumericRange> ranges = attribute.getNumericValue();
			if (ranges.size() > 1) {
				hasMultiRangeAttribute = true;
			}
			
			for (NumericRange range : ranges) {
				if (!useNormalValues) {
					if (range.hasExtremeLow()) {
						values.add(range.getExtremeLow().intValue());
					}
					if (range.hasExtremeHigh()) {
						values.add(range.getExtremeHigh().intValue());
					}
					
				}
				values.add(range.getNormalRange().getMinimumInteger());
				values.add(range.getNormalRange().getMaximumInteger());
				if (range.hasMiddleValue()) {
					values.add(range.getMiddle().intValue());
				}
			}
			
		}
		return hasMultiRangeAttribute;
	}
	
	private List<FloatRange> writeRealAttributes(Character realChar) {
		boolean useNormalValues = _context.getUseNormalValues();
		int characterNumber = realChar.getCharacterId();
		
		List<FloatRange> values = new ArrayList<FloatRange>();
		BitSet inapplicableBits = new BitSet();
		for (int i=1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			
			NumericAttribute attribute = (NumericAttribute)_dataSet.getAttribute(i, characterNumber);
			if (attribute == null || attribute.isUnknown() || attribute.isInapplicable() || attribute.isVariable()) {
				FloatRange range = new FloatRange(Float.MAX_VALUE);
				values.add(range);
				if (attribute.isInapplicable()) {
					inapplicableBits.set(i-1);
				}
				continue;
			}
			List<NumericRange> ranges = attribute.getNumericValue();
			
			Range useRange;
			for (NumericRange range : ranges) {
				if (useNormalValues) {
					useRange = range.getNormalRange();
				}
				else {
					useRange = range.getFullRange();
				}
				FloatRange floatRange = new FloatRange(useRange.getMinimumFloat(), useRange.getMaximumFloat());
				values.add(floatRange);
			}
		}
		_itemsFile.writeAttributeFloats(characterNumber, inapplicableBits, values);
		return values;
	}
	
	private void writeTextAttributes(Character textChar) {
		int characterNumber = textChar.getCharacterId();
		
		List<String> values = new ArrayList<String>();
		BitSet inapplicableBits = new BitSet();
		for (int i=1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			Attribute attribute = _dataSet.getAttribute(i, characterNumber);
			
			if (attribute == null || attribute.isUnknown()) {
				values.add("");
				continue;
			}
			if (attribute.isInapplicable()) {
				inapplicableBits.set(i-1);
				values.add("");
			}
			else {
				values.add(attribute.getValueAsString());
			}
		}
		_itemsFile.writeAttributeStrings(characterNumber, inapplicableBits, values);
	}
	
	public void writeKeyStateBoundaries() {
		throw new NotImplementedException();
	}
	
	public void writeTaxonImages() {
		List<String> imageList = new ArrayList<String>(_dataSet.getMaximumNumberOfItems());
	
		
		for (int i=1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			Item item = _dataSet.getItem(i);
			List<Image> images = item.getImages();
			if (images.isEmpty()) {
				imageList.add("");
			}
			else {
				StringBuilder buffer = new StringBuilder();
				ImageOverlayWriter overlayWriter = createOverlayWriter(buffer);
				for (Image image : images) {
					buffer.append(image.getFileName()).append(" ");
					overlayWriter.writeOverlays(image.getOverlays(), 0, item);
				}
				imageList.add(buffer.toString());
			}
			
		}
		_itemsFile.writeTaxonImages(imageList);
	}
	
	private ImageOverlayWriter createOverlayWriter(StringBuilder buffer) {
		DeltaWriter writer = new DeltaWriter(buffer);
		return new ImageOverlayWriter(writer);
	}
	
	public void writeEnableDeltaOutput() {
		_itemsFile.writeEnableDeltaOutput(_context.isDeltaOutputDisabled());
	}
	
	public void writeChineseFormat() {
		_itemsFile.writeChineseFormat(_context.isChineseFormat());
	}
	
	public void writeCharacterSynonomy() {
		
		List<Boolean> charsForSynonymy = charactersToBooleans(_context.getCharactersForSynonymy());
		_itemsFile.writeCharacterSynonymy(charsForSynonymy);
	}
	
	private List<Boolean> charactersToBooleans(Set<Integer> charNumbers) {
		List<Boolean> booleans = new ArrayList<Boolean>(_dataSet.getNumberOfCharacters());
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			booleans.add(charNumbers.contains(i));
		}
		return booleans;
	}
	
	public void writeOmitOr() {
		List<Boolean> booleans = new ArrayList<Boolean>(_dataSet.getNumberOfCharacters());
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			booleans.add(_context.isOrOmmitedForCharacter(i));
		}
		_itemsFile.writeOmitOr(booleans);
	}
	
	public void writeUseControllingFirst() {
		Set<Integer> values = new HashSet<Integer>(_dataSet.getNumberOfCharacters());
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			if (_context.isUseControllingCharacterFirst(i)) {
				values.add(i);
			}
		}
		_itemsFile.writeUseControllingFirst(values);
	}

	public void writeTaxonLinks() {
		int numItems = _dataSet.getMaximumNumberOfItems();
		List<String> taxonLinksList = new ArrayList<String>(numItems);
		for (int i=1; i<=numItems; i++) {
			String taxonLinks = _context.getTaxonLinks(i);
			if (taxonLinks == null) {
				taxonLinks = "";	
			}
			taxonLinksList.add(taxonLinks);
		}
		
		_itemsFile.writeTaxonLinks(0, taxonLinksList);
	}
	
	public void writeOmitPeriod() {
		Set<Integer> values = new HashSet<Integer>(_dataSet.getNumberOfCharacters());
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			if (_context.getOmitPeriodForCharacter(i)) {
				values.add(i);
			}
		}
		_itemsFile.writeOmitPeriod(values);
	}
	
	public void writeNewParagraph() {
		_itemsFile.writeNewParagraph(_context.getNewParagraphCharacters());
	}
	
	public void writeNonAutoControllingChars() {
		Set<Integer> values = new HashSet<Integer>(_dataSet.getNumberOfCharacters());
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			if (_context.getNonautomaticControllingCharacter(i)) {
				values.add(i);
			}
		}
		_itemsFile.writeNonAutoControllingChars(values);
	}
	
	public void writeSubjectForOutputFiles() {
		OutputFileSelector outputFileSelector = _context.getOutputFileSelector();
		String subject = outputFileSelector.getSubjectForOutputFiles();
		if (StringUtils.isEmpty(subject)) {
			return;
		}
		
		int numItems = _dataSet.getMaximumNumberOfItems();
		List<String> taxonLinksList = new ArrayList<String>(numItems);
		for (int i=1; i<=numItems; i++) {
			StringBuffer text = new StringBuffer();
			
			String outputFile = outputFileSelector.getOutputFile(i);
			if (StringUtils.isNotEmpty(outputFile)) {
				text.append(outputFile).append(" ");
				text.append("<@subject ").append(subject).append(">");
			}
			
			taxonLinksList.add(text.toString());
		}
		
		_itemsFile.writeTaxonLinks(1, taxonLinksList);
	}
}

package au.org.ala.delta.translation.intkey;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.Range;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.intkey.WriteOnceIntkeyItemsFile;
import au.org.ala.delta.io.BinaryKeyFileEncoder;
import au.org.ala.delta.io.OutputFileSelector;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.NumericAttribute;
import au.org.ala.delta.model.NumericRange;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.translation.FilteredCharacter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.FilteredItem;
import au.org.ala.delta.util.Pair;

/**
 * Writes the intkey items file using the data in a supplied DeltaContext and
 * associated data set.
 */
public class IntkeyItemsFileWriter {

	static final int INTEGER_RANGE_WARNING_THRESHOLD = 64;
	public static final int INTEGER_RANGE_MAX_THRESHOLD = 200;
	
	private WriteOnceIntkeyItemsFile _itemsFile;
	private FilteredDataSet _dataSet;
	private DeltaContext _context;
	private BinaryKeyFileEncoder _encoder;
	
	
	public IntkeyItemsFileWriter(DeltaContext context, FilteredDataSet dataSet, WriteOnceIntkeyItemsFile itemsFile) {
		_itemsFile = itemsFile;
		_dataSet = dataSet;
		_context = context;
		_encoder = new BinaryKeyFileEncoder();
	}
	
	public void writeAll() {
		
		// To retain compatibility with CONFOR (and older versions of intkey)
		// we write a blank heading here.
		_itemsFile.writeStringWithLength(2, " ");
		
		// For compatibility with CONFOR we write the attributes in this order:
		writeUnorderedMultistateAttributes();
		writeOrderedMultistateAttributes();
		Pair<List<IntRange>, Set<Integer>> result = writeIntegerAttributes();
		writeRealAttributes(result.getSecond());
		writeTextAttributes();
		
		writeItemDescriptions();
		writeCharacterSpecs(result.getSecond());
		
		writeMinMaxValues(result.getFirst());
		
		writeCharacterDependencies();
		
		_itemsFile.writeAttributeIndex();
		_itemsFile.writeKeyStateBoundariesIndex();
		
		writeTaxonImages();
		writeChineseFormat();
		writeCharacterSynonomy();
		writeOmitOr();
		writeUseControllingFirst();
		writeTaxonLinks();
		writeOmitPeriod();
		writeNewParagraph();
		writeNonAutoControllingChars();
		writeSubjectForOutputFiles();
		writeEnableDeltaOutput();
		_itemsFile.writeHeader();
	}
	
	public void writeItemDescriptions() {
		
		List<String> descriptions = new ArrayList<String>(_dataSet.getNumberOfFilteredItems());
		Iterator<FilteredItem> items = _dataSet.filteredItems();
		while (items.hasNext()) {
			descriptions.add(items.next().getItem().getDescription());
		}
		_itemsFile.writeItemDescriptions(descriptions);
	}
	
	public void writeCharacterSpecs(Set<Integer> intsConvertedToFloat) {
		
		List<Integer> types = new ArrayList<Integer>(_dataSet.getNumberOfFilteredCharacters());
		List<Integer> states = new ArrayList<Integer>(_dataSet.getNumberOfFilteredCharacters());
		List<Float> reliabilities = new ArrayList<Float>(_dataSet.getNumberOfFilteredCharacters());
		
	    Iterator<IdentificationKeyCharacter> iterator = _dataSet.identificationKeyCharacterIterator();
		while(iterator.hasNext()) {
			IdentificationKeyCharacter character = iterator.next();
			if (intsConvertedToFloat.contains(character.getCharacterNumber())) {
				types.add(-_encoder.typeToInt(CharacterType.RealNumeric));
			}
			else {
				types.add(_encoder.typeToInt(character.getCharacterType()));
			}
			states.add(numStates(character));
			reliabilities.add((float)_context.getCharacterReliability(character.getCharacterNumber()));
			
		}
		_itemsFile.writeCharacterSpecs(types, states, _dataSet.getMaximumNumberOfStates(), reliabilities);	
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
		
		List<Integer> dependencyData = _encoder.encodeCharacterDependencies(_dataSet, true);
		List<Integer> invertedDependencyData = _encoder.encodeCharacterDependenciesInverted(_dataSet);
		_itemsFile.writeCharacterDependencies(dependencyData, invertedDependencyData);
	}
	
	private void writeMultistateAttributes(CharacterType type) {
		Iterator<IdentificationKeyCharacter> keyChars = _dataSet.identificationKeyCharacterIterator();
		while (keyChars.hasNext()) {
			IdentificationKeyCharacter keyChar = keyChars.next();
			if (keyChar.getCharacterType() == type) {
				writeMultiStateAttributes(keyChar);
			}	
			
		}
	}
	
	public void writeUnorderedMultistateAttributes() {
		writeMultistateAttributes(CharacterType.UnorderedMultiState);
	}
	
	public void writeOrderedMultistateAttributes() {
		writeMultistateAttributes(CharacterType.OrderedMultiState);
	}
	
	public Pair<List<IntRange>, Set<Integer>> writeIntegerAttributes() {
		List<IntRange> intRanges = new ArrayList<IntRange>();
		Iterator<IdentificationKeyCharacter> keyChars = _dataSet.identificationKeyCharacterIterator();
		Set<Integer> convertToReal = new HashSet<Integer>();
		
		while (keyChars.hasNext()) {
			IdentificationKeyCharacter keyChar = keyChars.next();
			IntRange minMax = new IntRange(WriteOnceIntkeyItemsFile.CONFOR_INT_MAX);
				
			if (keyChar.getCharacterType() == CharacterType.IntegerNumeric) {
				Pair<IntRange, Boolean> result = writeIntegerAttributes(keyChar.getFilteredCharacterNumber(), keyChar.getCharacter());
				minMax = result.getFirst();
				if (result.getSecond()) {
					convertToReal.add(keyChar.getCharacterNumber());
				}
			}
			
			intRanges.add(minMax);
		}
		
		return new Pair<List<IntRange>, Set<Integer>>(intRanges, convertToReal);
	
	}
	
	public void writeMinMaxValues(List<IntRange> minMaxValues) {
		if (_dataSet.getNumberOfIntegerCharacters() > 0) {
			_itemsFile.writeMinMaxValues(minMaxValues);
		}
	}
	
	public void writeRealAttributes(Set<Integer> convertToReal) {
		Iterator<IdentificationKeyCharacter> keyChars = _dataSet.identificationKeyCharacterIterator();
		while (keyChars.hasNext()) {
			IdentificationKeyCharacter keyChar = keyChars.next();
			
			boolean converted = convertToReal.contains(keyChar.getCharacterNumber());
			if (keyChar.getCharacterType() == CharacterType.RealNumeric || converted) {	
			    writeRealAttributes(keyChar.getFilteredCharacterNumber(), keyChar.getCharacter(), converted);
			}
		}
	}
	
	public void writeTextAttributes() {
		Iterator<IdentificationKeyCharacter> keyChars = _dataSet.identificationKeyCharacterIterator();
		while (keyChars.hasNext()) {
			IdentificationKeyCharacter keyChar = keyChars.next();
			if (keyChar.getCharacterType().isText()) {
				writeTextAttributes(keyChar.getFilteredCharacterNumber(), keyChar.getCharacter());
			}	
		}
	}
	
	private void writeMultiStateAttributes(IdentificationKeyCharacter character) {
		
		int charNumber = character.getFilteredCharacterNumber();
		int numStates = character.getNumberOfStates();
		List<BitSet> attributes = new ArrayList<BitSet>();
		Iterator<FilteredItem> items = _dataSet.filteredItems();
		
		while (items.hasNext()) {
			int itemNum = items.next().getItem().getItemNumber();
			MultiStateAttribute attribute = (MultiStateAttribute)_dataSet.getAttribute(itemNum, character.getCharacterNumber());
		
			List<Integer> states = new ArrayList<Integer>();
			if (attribute.isImplicit()) {
				ControllingInfo controllingInfo = _dataSet.checkApplicability(
						attribute.getCharacter(), attribute.getItem());
				if (!controllingInfo.isInapplicable()) {
					states = character.getPresentStates(attribute);
				}
			}
			else {
				 states = character.getPresentStates(attribute);
			}
			
			// Turn into bitset.
			BitSet bits = new BinaryKeyFileEncoder().encodeAttributeStates(states);
			
			if (isInapplicable(attribute)) {
				bits.set(numStates);
			}
			attributes.add(bits);
		}
		
		_itemsFile.writeAttributeBits(charNumber, attributes, numStates+1);
	}
	
	private boolean isInapplicable(Attribute attribute) {
		
		if (!attribute.isInapplicable()) {
			ControllingInfo controllingInfo = _dataSet.checkApplicability(
					attribute.getCharacter(), attribute.getItem());
			return controllingInfo.isStrictlyInapplicable() || 
				(controllingInfo.isMaybeInapplicable() && !attribute.isUnknown());		
		}
		return true;
	}
	
	
	private Pair<IntRange, Boolean> writeIntegerAttributes(int filteredCharacterNumber, Character character) {
		
		// Returning null here will trigger a change from integer to real
		// character type.
		if (_context.getTreatIntegerCharacterAsReal(character.getCharacterId())) {
			return new Pair<IntRange, Boolean>(new IntRange(0), true);
		}
		Pair<IntRange, Boolean> result = determineIntegerRange(character);
		IntRange characterRange = result.getFirst();
		if (!result.getSecond()) {
		
			int unfilteredCharNumber = character.getCharacterId();
			int numStates = characterRange.getMaximumInteger()-characterRange.getMinimumInteger();
			List<BitSet> attributes = new ArrayList<BitSet>();
			for (int i=1; i<=_dataSet.getNumberOfFilteredItems(); i++) {
				
				// Turn into bitset.
				BitSet bits = new BitSet();
				IntegerAttribute attribute = (IntegerAttribute)_dataSet.getAttribute(i, unfilteredCharNumber);
				if (isInapplicable(attribute)) {
					bits.set(numStates+3);
				}
				if (attribute.isUnknown()) {
					attributes.add(bits);
					continue;
				}
				
				List<NumericRange> ranges = attribute.getNumericValue();
				
				for (NumericRange range : ranges) {
					Range usedRange;
					if (_context.getUseNormalValues(unfilteredCharNumber)) {
						usedRange = range.getNormalRange();
					}
					else {
						usedRange = range.getFullRange();
					}
					
					for (int j=usedRange.getMinimumInteger(); j<=usedRange.getMaximumInteger(); j++) {
						if (j<characterRange.getMinimumInteger()) {
							bits.set(0);
						}
						else if (j<=characterRange.getMaximumInteger()) {
							bits.set(j - characterRange.getMinimumInteger()+1);
						}
						else {
							bits.set(numStates+2);
						}
					}
				}
				attributes.add(bits);
				
			}
			
			_itemsFile.writeAttributeBits(filteredCharacterNumber, attributes, numStates+4);
		}
		return new Pair<IntRange, Boolean>(characterRange, result.getSecond());
	}
	
	
	private Pair<IntRange, Boolean> determineIntegerRange(Character intChar) {
		
		Set<Integer> values = new HashSet<Integer>();
		boolean hasMultiRangeAttribute = populateValues(intChar.getCharacterId(), values);
		
		List<Integer> orderedValues = new ArrayList<Integer>(values);
		
		if (orderedValues.size() == 0) {
			return new Pair<IntRange, Boolean>(new IntRange(0), false);
		}
		boolean outOfRange = false;
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
				outOfRange = true;
			}
			else {
				max = orderedValues.get(index);
			}
		}
		return new Pair<IntRange, Boolean>( new IntRange(min, max), outOfRange);
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
		boolean useNormalValues = _context.getUseNormalValues(characterNumber);
		
		boolean hasMultiRangeAttribute = false;
		for (int i=1; i<=_dataSet.getNumberOfFilteredItems(); i++) {
		
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
	
	private Set<Float> writeRealAttributes(int filteredCharNumber, Character realChar, boolean wasInteger) {
		int unfilteredCharNumber = realChar.getCharacterId();
		boolean useNormalValues = _context.getUseNormalValues(unfilteredCharNumber);
		
		List<FloatRange> values = new ArrayList<FloatRange>();
		BitSet inapplicableBits = new BitSet();
		for (int i=1; i<=_dataSet.getNumberOfFilteredItems(); i++) {
			
			NumericAttribute attribute = (NumericAttribute)_dataSet.getAttribute(i, unfilteredCharNumber);
			
			if (attribute == null || attribute.isCodedUnknown() || attribute.isInapplicable() || attribute.isVariable()) {
				FloatRange range = new FloatRange(Float.MAX_VALUE);
				values.add(range);
				if (isInapplicable(attribute)) {
					inapplicableBits.set(i-1);
				}
				continue;
			}
			List<NumericRange> ranges = attribute.getNumericValue();
			// This can happen if the attribute has a comment but no value.
			if (ranges.isEmpty()) {
				FloatRange range = new FloatRange(-Float.MAX_VALUE);
				values.add(range);
				if (isInapplicable(attribute)) {
					inapplicableBits.set(i-1);
				}
				continue;
			}
			Range useRange;
			float min = Float.MAX_VALUE;
			float max = -Float.MIN_VALUE;
			for (NumericRange range : ranges) {
				if (_context.hasAbsoluteError(unfilteredCharNumber)) {
					range.setAbsoluteError(_context.getAbsoluteError(unfilteredCharNumber));
				}
				else if (_context.hasPercentageError(unfilteredCharNumber)) {
					range.setPercentageError(_context.getPercentageError(unfilteredCharNumber));
				}
				if (useNormalValues) {
					useRange = range.getNormalRange();
				}
				else {
					useRange = range.getFullRange();
				}
				min = Math.min(min, useRange.getMinimumFloat());
				max = Math.max(max, useRange.getMaximumFloat());
				
			}
			FloatRange floatRange = new FloatRange(min, max);
			values.add(floatRange);
		}
		
		Set<Float> floats = new HashSet<Float>();
		for (FloatRange range : values) {
			if (range.getMinimumFloat() != Float.MAX_VALUE && 
				range.getMinimumFloat() != -Float.MAX_VALUE	) {
				floats.add(range.getMinimumFloat());
			}
			else {
				if (range.getMinimumFloat() == -Float.MAX_VALUE && !wasInteger) {
				floats.add(0f);  // For CONFOR compatibility, seems wrong.
				}
			}
			if (range.getMaximumFloat() != Float.MAX_VALUE &&
			    range.getMinimumFloat() != -Float.MAX_VALUE	){
				floats.add(range.getMaximumFloat());
			}
			else{ 
				if (range.getMinimumFloat() == -Float.MAX_VALUE && !wasInteger) {
				floats.add(1.0f);   // For CONFOR compatibility, seems wrong.
				}
			}
		}
		List<Float> boundaries = new ArrayList<Float>(floats);
		Collections.sort(boundaries);
		_itemsFile.writeAttributeFloats(filteredCharNumber, inapplicableBits, values, boundaries);
		
		return floats;
	}
	
	private void writeTextAttributes(int filteredCharNumber, Character textChar) {
		int characterNumber = textChar.getCharacterId();
		
		List<String> values = new ArrayList<String>();
		BitSet inapplicableBits = new BitSet();
		Iterator<FilteredItem> items = _dataSet.filteredItems();
		while (items.hasNext()) {
			FilteredItem item = items.next();
			Attribute attribute = _dataSet.getAttribute(item.getItem().getItemNumber(), characterNumber);
			
			if (isInapplicable(attribute)) {
				inapplicableBits.set(item.getItemNumber()-1);
			}
			
			if (attribute == null || attribute.isUnknown()) {
				values.add("");
				continue;
			}
			
			values.add(attribute.getValueAsString());
			
		}
		_itemsFile.writeAttributeStrings(filteredCharNumber, inapplicableBits, values);
	}
	
	public void writeTaxonImages() {
		List<String> imageList = new ArrayList<String>(_dataSet.getNumberOfFilteredItems());
	
		IntkeyImageWriter imageWriter = new IntkeyImageWriter();
		Iterator<FilteredItem> items = _dataSet.filteredItems();
		boolean hasImages = false;
		while (items.hasNext()) {
			Item item = items.next().getItem();
			List<Image> images = item.getImages();
			String image = "";
			if (!images.isEmpty()) {
				image = imageWriter.imagesToString(images, item);
				hasImages = true;
			}
			imageList.add(image);
			
		}
		if (hasImages) {
			_itemsFile.writeTaxonImages(imageList);
		}
	}
	
	
	public void writeEnableDeltaOutput() {
		_itemsFile.writeEnableDeltaOutput(!_context.isDeltaOutputDisabled());
	}
	
	public void writeChineseFormat() {
		_itemsFile.writeChineseFormat(_context.isChineseFormat());
	}
	
	public void writeCharacterSynonomy() {
		
		List<Boolean> charsForSynonymy = charactersToBooleans(_context.getCharactersForSynonymy());
		_itemsFile.writeCharacterSynonymy(charsForSynonymy);
	}
	
	private List<Boolean> charactersToBooleans(Set<Integer> charNumbers) {
		List<Boolean> booleans = new ArrayList<Boolean>(_dataSet.getNumberOfFilteredCharacters());
		Iterator<FilteredCharacter> characters = _dataSet.filteredCharacters();
		while (characters.hasNext()) {
			booleans.add(charNumbers.contains(characters.next().getCharacter().getCharacterId()));
		}
		return booleans;
	}
	
	public void writeOmitOr() {
		List<Boolean> booleans = new ArrayList<Boolean>(_dataSet.getNumberOfFilteredCharacters());
		Iterator<FilteredCharacter> characters = _dataSet.filteredCharacters();
		boolean omitOrPresent = false;
		while (characters.hasNext()) {
			boolean omitOr = _context.isOrOmmitedForCharacter(characters.next().getCharacterNumber());
			if (omitOr) {
				omitOrPresent = true;
			}
			booleans.add(omitOr);
		}
		if (omitOrPresent) {
			_itemsFile.writeOmitOr(booleans);
		}
	}
	
	public void writeUseControllingFirst() {
		Set<Integer> values = new HashSet<Integer>(_dataSet.getNumberOfFilteredCharacters());
		Iterator<FilteredCharacter> characters = _dataSet.filteredCharacters();
		while (characters.hasNext()) {
			if (_context.isUseControllingCharacterFirst(characters.next().getCharacterNumber())) {		
				values.add(characters.next().getCharacterNumber());
			}
		}
		if (!values.isEmpty()) {
			_itemsFile.writeUseControllingFirst(values);
		}
	}

	public void writeTaxonLinks() {
		int numItems = _dataSet.getNumberOfFilteredItems();
		List<String> taxonLinksList = new ArrayList<String>(numItems);
		boolean linkPresent = false;
		for (int i=1; i<=numItems; i++) {
			String taxonLinks = _context.getTaxonLinks(i);
			if (taxonLinks == null) {
				taxonLinks = "";	
			}
			if (StringUtils.isNotBlank(taxonLinks)) {
				linkPresent = true;
			}
			taxonLinksList.add(taxonLinks);
		}
		if (linkPresent) {
			_itemsFile.writeTaxonLinks(0, taxonLinksList);
		}
	}
	
	public void writeOmitPeriod() {
		Set<Integer> values = new HashSet<Integer>(_dataSet.getNumberOfFilteredCharacters());
		Iterator<FilteredCharacter> characters = _dataSet.filteredCharacters();
		while (characters.hasNext()) {
			int filteredItemNum = characters.next().getCharacterNumber();
			if (_context.getOmitPeriodForCharacter(filteredItemNum)) {
				values.add(filteredItemNum);
			}
		}
		if (!values.isEmpty()) {
			_itemsFile.writeOmitPeriod(values);
		}
	}
	
	public void writeNewParagraph() {
		Set<Integer> newParagraphChars = _context.getNewParagraphCharacters();
		if (!newParagraphChars.isEmpty()) {
			_itemsFile.writeNewParagraph(newParagraphChars);
		}
	}
	
	public void writeNonAutoControllingChars() {
		Set<Integer> values = new HashSet<Integer>(_dataSet.getNumberOfFilteredCharacters());
		Iterator<FilteredCharacter> characters = _dataSet.filteredCharacters();
		while (characters.hasNext()) {
			int filteredItemNum = characters.next().getCharacterNumber();
			if (_context.getNonautomaticControllingCharacter(filteredItemNum)) {
				values.add(filteredItemNum);
			}
		}
		if (!values.isEmpty()) {
			_itemsFile.writeNonAutoControllingChars(values);
		}
	}
	
	public void writeSubjectForOutputFiles() {
		OutputFileSelector outputFileSelector = _context.getOutputFileSelector();
		String subject = outputFileSelector.getSubjectForOutputFiles();
		if (StringUtils.isEmpty(subject)) {
			return;
		}
		
		int numItems = _dataSet.getNumberOfFilteredItems();
		List<String> taxonLinksList = new ArrayList<String>(numItems);
		for (int i=1; i<=numItems; i++) {
			StringBuffer text = new StringBuffer();
			
			String outputFile = outputFileSelector.getItemOutputFile(i);
			if (StringUtils.isNotEmpty(outputFile)) {
				text.append(outputFile).append(" ");
				text.append("<@subject ").append(subject).append(">");
			}
			
			taxonLinksList.add(text.toString());
		}
		
		_itemsFile.writeTaxonLinks(1, taxonLinksList);
	}
}

package au.org.ala.delta.translation.intkey;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.intkey.WriteOnceIntkeyItemsFile;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.translation.delta.DeltaWriter;
import au.org.ala.delta.translation.delta.ImageOverlayWriter;

/**
 * Writes the intkey items file using the data in a supplied DeltaContext and
 * associated data set.
 */
public class IntkeyItemsFileWriter {

	private WriteOnceIntkeyItemsFile _itemsFile;
	private DeltaDataSet _dataSet;
	private DeltaContext _context;
	
	public IntkeyItemsFileWriter(DeltaContext context, WriteOnceIntkeyItemsFile itemsFile) {
		_itemsFile = itemsFile;
		_dataSet = context.getDataSet();
		_context = context;
	}
	
	public void writeItemDescrptions() {
		
		List<String> descriptions = new ArrayList<String>(_dataSet.getNumberOfCharacters());
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			descriptions.add(_dataSet.getItem(i).getDescription());
		}
		_itemsFile.writeItemDescrptions(descriptions);
	}
	
	public void writeCharacterSpecs() {
		
		List<Integer> types = new ArrayList<Integer>(_dataSet.getNumberOfCharacters());
		List<Integer> states = new ArrayList<Integer>(_dataSet.getNumberOfCharacters());
		List<Float> reliabilities = new ArrayList<Float>(_dataSet.getNumberOfCharacters());
		
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			Character character = _dataSet.getCharacter(i);
			types.add(typeToInt(character.getCharacterType()));
			states.add(numStates(character));
			reliabilities.add((float)_context.getCharacterReliability(i));
			
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
	
	private int numStates(Character character) {
		if (character.getCharacterType().isMultistate()) {
			return ((MultiStateCharacter)character).getNumberOfStates();
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
		while (keyChars.hasNext()) {
			IdentificationKeyCharacter keyChar = keyChars.next();
			if (keyChar.getCharacterType().isMultistate()) {
				writeMultiStateAttributes(keyChar);
			}	
		}
	}
	
	private void writeMultiStateAttributes(IdentificationKeyCharacter character) {
		
		int charNumber = character.getCharacterNumber();
		int numStates = character.getNumberOfStates();
		List<BitSet> attributes = new ArrayList<BitSet>();
		for (int i=1; i<=_dataSet.getMaximumNumberOfItems(); i++) {
			Attribute attribute = _dataSet.getAttribute(i, character.getCharacterNumber());
		
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
		
		_itemsFile.writeAttributeBits(charNumber, attributes, numStates);
		
		
	}
	
	private void processIntegerAttributes(int characterNumber) {
		int max1 = 64;
		int max2 = 200;
		
		IntegerAttribute attribute = (IntegerAttribute)_dataSet.getAttribute(1, 1);
		List<Integer> values = new ArrayList<Integer>();
		
		int value = 0;
		values.add(value);
		
		Collections.sort(values);
		
		int min = values.get(0);
		int max = values.get(values.size()-1);
		if (max-min > max2) {
			int index = Collections.binarySearch(values, min+max2);
			if (index < 0) {
				index = -(index+1);
			}
			
			if (index > values.size()/2) {
				convertToReal(characterNumber);
			}
		}
		
		
		
	}
	
	private void convertToReal(int characterNumber) {
		
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
		throw new NotImplementedException();
	}
	
	public void writeOmitPeriod() {
		throw new NotImplementedException();
	}
	
	public void writeNewParagraph() {
		throw new NotImplementedException();
	}
	
	public void writeNonAutoControllingChars() {
		throw new NotImplementedException();
	}
	
	public void writeSubjectForOutputFiles() {
		throw new NotImplementedException();
	}
	
	
}

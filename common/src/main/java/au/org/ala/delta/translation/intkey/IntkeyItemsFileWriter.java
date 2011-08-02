package au.org.ala.delta.translation.intkey;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.BitField;
import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.intkey.WriteOnceIntkeyItemsFile;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;

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
		
		Iterator<IdentificationKeyCharacter> keyChars = null;// _context.identificationKeyCharacterIterator();
		while (keyChars.hasNext()) {
			
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
		throw new NotImplementedException();
	}
	
	public void writeEnableDeltaOutput() {
		throw new NotImplementedException();
	}
	
	public void writeChineseFormat() {
		throw new NotImplementedException();
	}
	
	public void writeCharacterSynonomy() {
		throw new NotImplementedException();
	}
	
	public void writeOmitOr() {
		throw new NotImplementedException();
	}
	
	public void writeUseControllingFirst() {
		throw new NotImplementedException();
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

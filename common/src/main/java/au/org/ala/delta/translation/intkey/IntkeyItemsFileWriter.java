package au.org.ala.delta.translation.intkey;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.intkey.WriteOnceIntkeyItemsFile;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
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
	
	public void writeMinMaxValues() {
		throw new NotImplementedException();
	}
	
	public void writeCharacterDependencies() {
		throw new NotImplementedException();
	}
	
	public void writeAttributeData() {
		throw new NotImplementedException();
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

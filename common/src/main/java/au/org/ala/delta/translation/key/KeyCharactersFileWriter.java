package au.org.ala.delta.translation.key;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.key.WriteOnceKeyCharsFile;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.translation.FilteredDataSet;

/**
 * Writes the key chars file using the data in a supplied DeltaContext and
 * associated data set.
 */
public class KeyCharactersFileWriter {

	private WriteOnceKeyCharsFile _charsFile;
	private FilteredDataSet _dataSet;
	private CharacterFormatter _formatter;
	private KeyStateTranslator _keyStateTranslator;
	public KeyCharactersFileWriter(
			FilteredDataSet dataSet,
			CharacterFormatter formatter,
			KeyStateTranslator keyStateTranslator,
			WriteOnceKeyCharsFile charsFile) {
		_charsFile = charsFile;
		_dataSet = dataSet;
		_formatter = formatter;
		_keyStateTranslator = keyStateTranslator;
	}
	
	public void writeAll() {
		
		writeKeyStates();
		writeCharacterFeatures();
		
		// Need to write the header last as it is updated as each section 
		// is written.
		_charsFile.writeHeader();
	}
	
	protected void writeKeyStates() {
		Iterator<IdentificationKeyCharacter> characters = _dataSet.unfilteredIdentificationKeyCharacterIterator();
		List<Integer> keyStates = new ArrayList<Integer>();
		while (characters.hasNext()) {
			IdentificationKeyCharacter keyChar = characters.next();
			keyStates.add(keyChar.getNumberOfStates());
		}
		_charsFile.writeKeyStates(keyStates);
	}
	
	
	protected void writeCharacterFeatures() {
		List<List<String>> features = new ArrayList<List<String>>();
		
		Iterator<IdentificationKeyCharacter> characters = _dataSet.unfilteredIdentificationKeyCharacterIterator();
		while (characters.hasNext()) {
			IdentificationKeyCharacter keyChar = characters.next();
			List<String> feature = new ArrayList<String>();
			Character character = keyChar.getCharacter();
			feature.add(_formatter.formatCharacterDescription(character));
			addCharacterStates(keyChar, feature);
			features.add(feature);
		}
		
		_charsFile.writeCharacterFeatures(features);
	}

	private void addCharacterStates(IdentificationKeyCharacter keyChar, List<String> feature) {
		if (keyChar.getStates().size() > 0) {
			for (int i=1; i<=keyChar.getNumberOfStates(); i++) {
				feature.add(_keyStateTranslator.translateState(keyChar, i));
			}
		}
		else if (keyChar.getCharacterType().isMultistate()) {
			MultiStateCharacter character = (MultiStateCharacter)keyChar.getCharacter();
			for (int i=1; i<=character.getNumberOfStates(); i++) {
				feature.add(_formatter.formatState(character, i));
			}
		}
		
	}

}

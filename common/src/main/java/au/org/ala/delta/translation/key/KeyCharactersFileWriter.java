package au.org.ala.delta.translation.key;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.intkey.WriteOnceIntkeyCharsFile;
import au.org.ala.delta.key.WriteOnceKeyCharsFile;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.CharacterNoteMarks;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageInfo;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.ImageSettings.FontInfo;
import au.org.ala.delta.model.image.ImageSettings.OverlayFontType;
import au.org.ala.delta.model.image.ImageType;
import au.org.ala.delta.translation.FilteredCharacter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.Words;
import au.org.ala.delta.translation.Words.Word;
import au.org.ala.delta.translation.delta.DeltaWriter;
import au.org.ala.delta.translation.delta.OverlayFontWriter;
import au.org.ala.delta.translation.intkey.IntkeyImageWriter;

/**
 * Writes the key chars file using the data in a supplied DeltaContext and
 * associated data set.
 */
public class KeyCharactersFileWriter {

	private WriteOnceKeyCharsFile _charsFile;
	private FilteredDataSet _dataSet;
	private DeltaContext _context;
	private CharacterFormatter _formatter;
	
	public KeyCharactersFileWriter(
			DeltaContext context, 
			FilteredDataSet dataSet,
			CharacterFormatter formatter,
			WriteOnceKeyCharsFile charsFile) {
		_charsFile = charsFile;
		_dataSet = dataSet;
		_context = context;
		_formatter = formatter;
	}
	
	public void writeAll() {
		
		writeKeyStates();
		writeCharacterFeatures();
		
		// Need to write the header last as it is updated as each section 
		// is written.
		_charsFile.writeHeader();
	}
	
	protected void writeKeyStates() {
		Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
		List<Integer> keyStates = new ArrayList<Integer>();
		while (characters.hasNext()) {
			IdentificationKeyCharacter keyChar = characters.next();
			keyStates.add(keyChar.getNumberOfStates());
		}
		_charsFile.writeKeyStates(keyStates);
	}
	
	
	protected void writeCharacterFeatures() {
		List<List<String>> features = new ArrayList<List<String>>();
		
		Iterator<FilteredCharacter> characters = _dataSet.filteredCharacters();
		while (characters.hasNext()) {
			Character character = characters.next().getCharacter();
			List<String> feature = new ArrayList<String>();
			feature.add(_formatter.formatCharacterDescription(character));
			if (character.getCharacterType().isMultistate()) {
				MultiStateCharacter multiStateChar = (MultiStateCharacter)character;
				for (int j=1; j<=multiStateChar.getNumberOfStates(); j++) {
					feature.add(_formatter.formatState(multiStateChar,j));
				}
			}
			else if (character.getCharacterType().isNumeric()) {
				NumericCharacter<?> numericChar = (NumericCharacter<?>)character;
				if (numericChar.hasUnits()) {
					feature.add(_formatter.formatUnits(numericChar));
				}
			}
			features.add(feature);
		}
		
		_charsFile.writeCharacterFeatures(features);
	}
	
	

}

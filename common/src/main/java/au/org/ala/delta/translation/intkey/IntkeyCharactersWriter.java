package au.org.ala.delta.translation.intkey;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.intkey.WriteOnceIntkeyCharsFile;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;

/**
 * Writes various d
 */
public class IntkeyCharactersWriter {

	private WriteOnceIntkeyCharsFile _charsFile;
	private DeltaDataSet _dataSet;
	private DeltaContext _context;
	
	public IntkeyCharactersWriter(DeltaContext context, WriteOnceIntkeyCharsFile charsFile) {
		_charsFile = charsFile;
		_dataSet = context.getDataSet();
		_context = context;
	}
	
	public void writeCharacterNotes() {
		String[] allNotes = new String[_dataSet.getNumberOfCharacters()];
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			Character character = _dataSet.getCharacter(i);
			String notes = character.getNotes();
			if (notes == null) {
				notes = "";
			}
			allNotes[i-1] = notes;
		}
	}
	
	
	public void writeCharacterFeatures() {
		List<List<String>> features = new ArrayList<List<String>>();
		
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			
			Character character = _dataSet.getCharacter(i);
			List<String> feature = new ArrayList<String>();
			feature.add(character.getDescription());
			if (character.getCharacterType().isMultistate()) {
				MultiStateCharacter multiStateChar = (MultiStateCharacter)character;
				for (int j=1; j<=multiStateChar.getNumberOfStates(); j++) {
					feature.add( multiStateChar.getState(j));
				}
			}
			else if (character.getCharacterType().isNumeric()) {
				feature.add(((NumericCharacter<?>)character).getUnits());
			}
			features.add(feature);
		}
		
		_charsFile.writeCharacterFeatures(features);
	}
}

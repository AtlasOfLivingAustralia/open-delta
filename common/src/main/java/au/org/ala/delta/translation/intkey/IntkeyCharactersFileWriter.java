package au.org.ala.delta.translation.intkey;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.intkey.WriteOnceIntkeyCharsFile;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.CharacterNoteMarks;

/**
 * Writes the intkey chars file using the data in a supplied DeltaContext and
 * associated data set.
 */
public class IntkeyCharactersFileWriter {

	private WriteOnceIntkeyCharsFile _charsFile;
	private DeltaDataSet _dataSet;
	private DeltaContext _context;
	
	public IntkeyCharactersFileWriter(DeltaContext context, WriteOnceIntkeyCharsFile charsFile) {
		_charsFile = charsFile;
		_dataSet = context.getDataSet();
		_context = context;
	}
	
	public void writeCharacterNotes() {
		List<String> allNotes = new ArrayList<String>(_dataSet.getNumberOfCharacters());
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			Character character = _dataSet.getCharacter(i);
			String notes = character.getNotes();
			if (notes == null) {
				notes = "";
			}
			allNotes.add(notes);
		}
		_charsFile.writeCharacterNotes(allNotes);
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
	
	public void writeCharacterNotesFormat() {
		TypeSettingMark mark = _context.getFormattingMark(CharacterNoteMarks.CHARACTER_NOTES_FORMAT);
		String markText = "";
		if (mark != null) {
			markText = mark.getMarkText();
		}
		_charsFile.writeCharacterNotesFormat(markText);
	}
	
	public void writeCharacterNotesHelpFormat() {
		TypeSettingMark mark = _context.getFormattingMark(CharacterNoteMarks.CHARACTER_NOTES_HELP_FORMAT);
		String markText = "";
		if (mark != null) {
			markText = mark.getMarkText();
		}
		_charsFile.writeCharacterNotesHelpFormat(markText);
	}
	
	public void writeCharacterImages() {
		throw new NotImplementedException();
	}
	
	public void writeStartupImages() {
		throw new NotImplementedException();
	}
	
	public void writeCharacterKeyImages() {
		throw new NotImplementedException();
	}
	
	public void writeHeading() {
		throw new NotImplementedException();	
	}
	
	public void writeSubHeading() {
		throw new NotImplementedException();
	}
	
	public void writeValidationString() {
		throw new NotImplementedException();
	}
	
	public void writeCharacterMask() {
		throw new NotImplementedException();
	}
	
	public void writeOrWord() {
		throw new NotImplementedException();
	}
	
	public void writeFonts() {
		throw new NotImplementedException();
	}
	
	public void writeItemSubheadings() {
		throw new NotImplementedException();
	}
}

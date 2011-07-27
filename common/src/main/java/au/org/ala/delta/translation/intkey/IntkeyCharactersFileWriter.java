package au.org.ala.delta.translation.intkey;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.intkey.WriteOnceIntkeyCharsFile;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.CharacterNoteMarks;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageInfo;
import au.org.ala.delta.model.image.ImageType;
import au.org.ala.delta.translation.delta.DeltaWriter;
import au.org.ala.delta.translation.delta.ImageOverlayWriter;

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
		List<String> imageList = new ArrayList<String>(_dataSet.getNumberOfCharacters());
	
		
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			Character character = _dataSet.getCharacter(i);
			List<Image> images = character.getImages();
			if (images.isEmpty()) {
				imageList.add("");
			}
			else {
				StringBuilder buffer = new StringBuilder();
				ImageOverlayWriter overlayWriter = createOverlayWriter(buffer);
				for (Image image : images) {
					buffer.append(image.getFileName()).append(" ");
					overlayWriter.writeOverlays(image.getOverlays(), 0, character);
				}
				imageList.add(buffer.toString());
			}
			
		}
		_charsFile.writeCharacterImages(imageList);
	}
	
	private ImageOverlayWriter createOverlayWriter(StringBuilder buffer) {
		DeltaWriter writer = new DeltaWriter(buffer);
		return new ImageOverlayWriter(writer);
	}
	
	public void writeStartupImages() {
		List<ImageInfo> startupImages = _context.getImages(ImageType.IMAGE_STARTUP);
		String images = imagesToString(startupImages);
		_charsFile.writeStartupImages(images);
	}

	private String imagesToString(List<ImageInfo> startupImages) {
		StringBuilder buffer = new StringBuilder();
		ImageOverlayWriter overlayWriter = createOverlayWriter(buffer);
		for (ImageInfo image : startupImages) {
			if (buffer.length() > 0) {
				buffer.append(" ");
			}
			buffer.append(image.getFileName()).append(" ");
			overlayWriter.writeOverlays(image.getOverlays(), 0, null);
		}
		return buffer.toString();
	}
	
	public void writeCharacterKeyImages() {
		List<ImageInfo> startupImages = _context.getImages(ImageType.IMAGE_CHARACTER_KEYWORD);
		String images = imagesToString(startupImages);
		_charsFile.writeCharacterKeyImages(images);
	}
	
	public void writeHeading() {
		String heading = _context.getHeading(HeadingType.HEADING);
		_charsFile.writeHeading(heading);
	}
	
	public void writeSubHeading() {
		String heading = _context.getHeading(HeadingType.REGISTRATION_SUBHEADING);
		_charsFile.writeSubHeading(heading);
	}
	
	public void writeCharacterMask() {
		
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

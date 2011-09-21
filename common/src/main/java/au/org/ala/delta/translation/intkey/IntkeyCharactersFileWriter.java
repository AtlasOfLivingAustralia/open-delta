package au.org.ala.delta.translation.intkey;

import java.util.ArrayList;
import java.util.List;

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
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.ImageSettings.FontInfo;
import au.org.ala.delta.model.image.ImageSettings.OverlayFontType;
import au.org.ala.delta.model.image.ImageType;
import au.org.ala.delta.translation.Words;
import au.org.ala.delta.translation.Words.Word;
import au.org.ala.delta.translation.delta.DeltaWriter;
import au.org.ala.delta.translation.delta.OverlayFontWriter;

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
	
	public void writeAll() {
		
		writeCharacterFeatures();
		writeCharacterNotes();
		writeCharacterNotesFormat();
		writeCharacterNotesHelpFormat();
		writeCharacterImages();
		writeStartupImages();
		writeCharacterKeywordImages();
		writeTaxonKeywordImages();
		writeHeading();
		writeSubHeading();
		writeCharacterMask();
		writeOrWord();
		writeFonts();
		writeItemSubheadings();
		
		// Need to write the header last as it is updated as each section 
		// is written.
		_charsFile.writeHeader();
	}
	
	protected void writeCharacterNotes() {
		List<String> allNotes = new ArrayList<String>(_dataSet.getNumberOfCharacters());
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			Character character = _dataSet.getCharacter(i);
			String notes = character.getNotes();
			add(allNotes, notes);
		}
		_charsFile.writeCharacterNotes(allNotes);
	}
	
	
	protected void writeCharacterFeatures() {
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
				NumericCharacter<?> numericChar = (NumericCharacter<?>)character;
				if (numericChar.hasUnits()) {
					feature.add(numericChar.getUnits());
				}
			}
			features.add(feature);
		}
		
		_charsFile.writeCharacterFeatures(features);
	}
	
	protected void writeCharacterNotesFormat() {
		TypeSettingMark mark = _context.getFormattingMark(CharacterNoteMarks.CHARACTER_NOTES_FORMAT);
		String markText = "";
		if (mark != null) {
			markText = mark.getMarkText();
		}
		_charsFile.writeCharacterNotesFormat(markText);
	}
	
	protected void writeCharacterNotesHelpFormat() {
		TypeSettingMark mark = _context.getFormattingMark(CharacterNoteMarks.CHARACTER_NOTES_HELP_FORMAT);
		String markText = "";
		if (mark != null) {
			markText = mark.getMarkText();
		}
		_charsFile.writeCharacterNotesHelpFormat(markText);
	}
	
	protected void writeCharacterImages() {
		List<String> imageList = new ArrayList<String>(_dataSet.getNumberOfCharacters());
	
		IntkeyImageWriter imageWriter = new IntkeyImageWriter();
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			Character character = _dataSet.getCharacter(i);
			List<Image> images = character.getImages();
			
			imageList.add(imageWriter.imagesToString(images, character));
			
		}
		_charsFile.writeCharacterImages(imageList);
	}
	
	
	
	protected void writeStartupImages() {
		List<ImageInfo> startupImages = _context.getImages(ImageType.IMAGE_STARTUP);
		String images = imagesToString(startupImages);
		_charsFile.writeStartupImages(images);
	}

	private String imagesToString(List<ImageInfo> images) {
		IntkeyImageWriter imageWriter = new IntkeyImageWriter();
		return imageWriter.imagesToString(images);
	}
	
	protected void writeCharacterKeywordImages() {
		List<ImageInfo> imageInfo = _context.getImages(ImageType.IMAGE_CHARACTER_KEYWORD);
		String images = imagesToString(imageInfo);
		_charsFile.writeCharacterKeyImages(images);
	}
	
	protected void writeTaxonKeywordImages() {
		List<ImageInfo> imageInfo = _context.getImages(ImageType.IMAGE_TAXON_KEYWORD);
		String images = imagesToString(imageInfo);
		_charsFile.writeTaxonKeyImages(images);
	}
	
	protected void writeHeading() {
		String heading = _context.getHeading(HeadingType.HEADING);
		_charsFile.writeHeading(heading);
	}
	
	protected void writeSubHeading() {
		String heading = _context.getHeading(HeadingType.REGISTRATION_SUBHEADING);
		_charsFile.writeSubHeading(heading);
	}
	
	protected void writeCharacterMask() {
		
	}
	
	protected void writeOrWord() {
		String orWord = Words.word(Word.OR);
		_charsFile.writeOrWord(orWord);
	}
	
	protected void writeFonts() {
		List<String> fonts = new ArrayList<String>();
		ImageSettings settings = _dataSet.getImageSettings();
		if (settings != null) {
			for (OverlayFontType fontType : OverlayFontType.values()) {
				FontInfo font = settings.getFont(fontType);
				if (font != null) {
					addFontText(fonts, settings.getFont(fontType), fontType);
				}
			}
			_charsFile.writeFonts(fonts);
		}
	}
	
	protected void addFontText(List<String> fonts, FontInfo font, OverlayFontType type) {
		if (font != null) {
			StringBuilder fontText = new StringBuilder();
			OverlayFontWriter writer = createOverlayFontWriter(fontText);
			writer.writeFont(font, type);
			
			fonts.add(fontText.toString().trim());
		}
	}
	
	private OverlayFontWriter createOverlayFontWriter(StringBuilder buffer) {
		DeltaWriter writer = new DeltaWriter(buffer);
		return new OverlayFontWriter(writer);
	}
	
	protected void writeItemSubheadings() {
		List<String> subHeadings = new ArrayList<String>();
		for (int i=1; i<=_dataSet.getNumberOfCharacters(); i++) {
			
			String subheading = _context.getItemSubheading(i);
			add(subHeadings, subheading);
		}
		_charsFile.writeItemSubheadings(subHeadings);
		
	}
	
	protected void add(List<String> values, String value) {
		if (value == null) {
			value = "";
		}
		values.add(value);
	}


}

package au.org.ala.delta.translation.intkey;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.intkey.WriteOnceIntkeyCharsFile;
import au.org.ala.delta.io.BinaryKeyFileEncoder;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.CharacterNoteMarks;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
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

/**
 * Writes the intkey chars file using the data in a supplied DeltaContext and
 * associated data set.
 */
public class IntkeyCharactersFileWriter {

	private WriteOnceIntkeyCharsFile _charsFile;
	private FilteredDataSet _dataSet;
	private DeltaContext _context;
	private CharacterFormatter _formatter;
	private BinaryKeyFileEncoder _encoder;
	
	public IntkeyCharactersFileWriter(
			DeltaContext context, 
			FilteredDataSet dataSet,
			CharacterFormatter formatter,
			WriteOnceIntkeyCharsFile charsFile) {
		_charsFile = charsFile;
		_dataSet = dataSet;
		_context = context;
		_formatter = formatter;
		_encoder = new BinaryKeyFileEncoder();
	}
	
	public void writeAll() {
		
		writeCharacterFeatures();
		writeCharacterNotesFormat();
		writeCharacterNotesHelpFormat();
		writeCharacterNotes();
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
		List<String> allNotes = new ArrayList<String>(_dataSet.getNumberOfFilteredCharacters());
		List<Integer> groups = new ArrayList<Integer>();
		Iterator<FilteredCharacter> characters = _dataSet.filteredCharacters();
		while (characters.hasNext()) {
			Character character = characters.next().getCharacter();
			String notes = null;
			if (character.hasNotes()) {
				notes = _formatter.defaultFormat(character.getNotes(), CommentStrippingMode.STRIP_ALL);
				groups.add(character.getCharacterId());
			}
			else {
				groups.add(0);
			}
			add(allNotes, notes);
		}
		_charsFile.writeCharacterNotes(allNotes, groups);
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
	
	protected void writeCharacterNotesFormat() {
		TypeSettingMark mark = _context.getFormattingMark(CharacterNoteMarks.CHARACTER_NOTES_FORMAT);
		
		String markText = "[8i][12'i][1p]";
		if (mark != null) {
			markText = mark.getMarkText();
		}
		_charsFile.writeCharacterNotesFormat(markText);
	}
	
	protected void writeCharacterNotesHelpFormat() {
		TypeSettingMark mark = _context.getFormattingMark(CharacterNoteMarks.CHARACTER_NOTES_HELP_FORMAT);
		String markText = "[0i][0'i][2'z]";
		if (mark != null) {
			markText = mark.getMarkText();
		}
		_charsFile.writeCharacterNotesHelpFormat(markText);
	}
	
	protected void writeCharacterImages() {
		List<String> imageList = new ArrayList<String>(_dataSet.getNumberOfFilteredCharacters());
	
		IntkeyImageWriter imageWriter = new IntkeyImageWriter();
		Iterator<FilteredCharacter> characters = _dataSet.filteredCharacters();
		boolean hasImages = false;
		while (characters.hasNext()) {
			Character character = characters.next().getCharacter();
			List<Image> images = character.getImages();
			String image = "";
			if (!images.isEmpty()) {
				image = imageWriter.imagesToString(images, character); 
				hasImages = true;
			}
			imageList.add(image);
			
		}
		if (hasImages) {
			
			_charsFile.writeCharacterImages(imageList);
		}
	}
	
	protected void writeStartupImages() {
		List<ImageInfo> startupImages = _context.getImages(ImageType.IMAGE_STARTUP);
		if (!startupImages.isEmpty()) {
			String images = imagesToString(startupImages);
			_charsFile.writeStartupImages(images);
		}
	}

	private String imagesToString(List<ImageInfo> images) {
		IntkeyImageWriter imageWriter = new IntkeyImageWriter();
		return imageWriter.imagesToString(images);
	}
	
	protected void writeCharacterKeywordImages() {
		List<ImageInfo> imageInfo = _context.getImages(ImageType.IMAGE_CHARACTER_KEYWORD);
		if (!imageInfo.isEmpty()) {
			String images = imagesToString(imageInfo);
			_charsFile.writeCharacterKeyImages(images); 
		}
	}
	
	protected void writeTaxonKeywordImages() {
		List<ImageInfo> imageInfo = _context.getImages(ImageType.IMAGE_TAXON_KEYWORD);
		if (!imageInfo.isEmpty()) {
			String images = imagesToString(imageInfo);
			_charsFile.writeTaxonKeyImages(images);
		}
	}
	
	/**
	 * If a REGISTRATION SUBHEADING has been specified, use that.  Otherwise
	 * use the HEADING.  
	 */
	protected void writeHeading() {
		String heading = _context.getHeading(HeadingType.REGISTRATION_HEADING);
		if (StringUtils.isBlank(heading)) {
			heading = _context.getHeading(HeadingType.HEADING);
		}
		if (StringUtils.isBlank(heading)) {
			heading = " ";
		}
		_charsFile.writeHeading(heading);
		
	}
	
	protected void writeSubHeading() {
		String heading = _context.getHeading(HeadingType.REGISTRATION_SUBHEADING);
		if (StringUtils.isNotBlank(heading)) {
			_charsFile.writeSubHeading(heading);
		}
	}
	
	protected void writeCharacterMask() {
		List<Boolean> includedCharacters = _encoder.encodeCharacterMasks(_dataSet, true);
		BitSet charMask = new BitSet();
		for (int i=0; i<includedCharacters.size(); i++) {
			if (includedCharacters.get(i)) {
				charMask.set(i);
			}
		}
		_charsFile.writeCharacterMask(_dataSet.getNumberOfCharacters(), charMask);
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
					addFontText(fonts, settings.getFont(fontType));
				}
			}
			_charsFile.writeFonts(fonts);
		}
	}
	
	protected void addFontText(List<String> fonts, FontInfo font) {
		if (font != null) {
			StringBuilder fontText = new StringBuilder();
			OverlayFontWriter writer = createOverlayFontWriter(fontText);
			writer.writeFontInfo(font, 0);
			
			fonts.add(fontText.toString().trim());
		}
	}
	
	private OverlayFontWriter createOverlayFontWriter(StringBuilder buffer) {
		DeltaWriter writer = new DeltaWriter(buffer);
		return new OverlayFontWriter(writer);
	}
	
	protected void writeItemSubheadings() {
		List<String> subHeadings = new ArrayList<String>();
		boolean hasSubheadings = false;
		for (int i=1; i<=_dataSet.getNumberOfFilteredCharacters(); i++) {
			
			String subheading = _context.getItemSubheading(i);
			if (StringUtils.isNotBlank(subheading)) {
				hasSubheadings = true;
			}
			add(subHeadings, subheading);
		}
		if (hasSubheadings) {
			_charsFile.writeItemSubheadings(subHeadings);
		}
		
	}
	
	protected void add(List<String> values, String value) {
		if (value == null) {
			value = "";
		}
		values.add(value);
	}


}

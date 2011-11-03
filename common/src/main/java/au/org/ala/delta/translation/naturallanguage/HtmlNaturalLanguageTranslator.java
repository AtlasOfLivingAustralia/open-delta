package au.org.ala.delta.translation.naturallanguage;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.io.OutputFileSelector;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.Words.Word;

/**
 * Writes the html index file as a part of the translation process.
 */
public class HtmlNaturalLanguageTranslator extends NaturalLanguageTranslator {
	
	private IndexWriter _indexWriter;
	private int _charForTaxonImages;
	private OutputFileSelector _outputFileSelector;
	
	public HtmlNaturalLanguageTranslator(
			DeltaContext context, ItemListTypeSetter typeSetter, PrintFile printer, ItemFormatter itemFormatter, CharacterFormatter characterFormatter,
            AttributeFormatter attributeFormatter, IndexWriter indexWriter) {
        super(context, typeSetter, printer, itemFormatter, characterFormatter, attributeFormatter);
        _indexWriter = indexWriter;
        Integer charForTaxonImages = context.getCharacterForTaxonImages();
		if (charForTaxonImages != null) {
			_charForTaxonImages = charForTaxonImages;
		}
		else {
			_charForTaxonImages = -1;
		}
		_outputFileSelector = _context.getOutputFileSelector();
	}

	@Override
	public void beforeItem(Item item) {
		
		_indexWriter.addItemToIndex(item);
		
		super.beforeItem(item);
	}

	@Override
    public void afterItem(Item item) {
        finishWritingAttributes(item);
       
        if (_charForTaxonImages > 0) {
        	if (_context.startNewParagraphAtCharacter(_charForTaxonImages)) {
        		_typeSetter.beforeNewParagraphCharacter();
        	}
    	    writeCharacterImages(item);
        }
       
        _typeSetter.afterItem(item);
    }

	private void writeCharacterImages(Item item) {
		
		Character character = _dataSet.getCharacter(_charForTaxonImages);
		List<Image> images = item.getImages();
		if (!images.isEmpty()) {
			writeItemSubheading(_context.getItemSubheading(character.getCharacterId()));
			writeImages(images);
		}
	}
	
	private void writeImages(List<Image> images) {
		for (Image image : images) {
			StringBuilder text = new StringBuilder();
			
			text.append("&#149;&nbsp;<a href=\"");
			text.append(imageFileName(image));
			text.append("\">");
			text.append(_itemFormatter.defaultFormat(image.getSubjectText()));
			text.append("</a>");
			writeSentence(text.toString());
			for (ImageOverlay overlay : image.getOverlaysOfType(OverlayType.OLTEXT)) {
				writeSentence(_itemFormatter.defaultFormat(overlay.overlayText));
			}
		}
		
	}
	
	private void writeSentence(String text) {
		_printer.writeJustifiedText(text, -1);
		_printer.insertPunctuationMark(Word.FULL_STOP);
	}
	
	private String imageFileName(Image image) {
		String imageFile = image.getFileName();
		File imageFilePath = new File(new File(_outputFileSelector.getImageDirectory()), imageFile);
		// Using unix file separators instead of system here because we are producing a html URL.
		return FilenameUtils.separatorsToUnix(imageFilePath.getPath());
	}
	
}

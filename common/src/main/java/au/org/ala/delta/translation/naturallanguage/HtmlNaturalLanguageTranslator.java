package au.org.ala.delta.translation.naturallanguage;

import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.translation.DataSetFilter;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;

/**
 * Writes the html index file as a part of the translation process.
 */
public class HtmlNaturalLanguageTranslator extends NaturalLanguageTranslator {
	
	private IndexWriter _indexWriter;
	private int _charForTaxonImages;
	
	public HtmlNaturalLanguageTranslator(
			DeltaContext context, DataSetFilter filter, ItemListTypeSetter typeSetter, PrintFile printer, ItemFormatter itemFormatter, CharacterFormatter characterFormatter,
            AttributeFormatter attributeFormatter, IndexWriter indexWriter) {
        super(context, filter, typeSetter, printer, itemFormatter, characterFormatter, attributeFormatter);
        _indexWriter = indexWriter;
        Integer charForTaxonImages = context.getCharacterForTaxonImages();
		if (charForTaxonImages != null) {
			_charForTaxonImages = charForTaxonImages;
		}
		else {
			_charForTaxonImages = -1;
		}
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
			writeItemSubheading(character);
			writeImages(images);
		}
	}
	
	private void writeImages(List<Image> images) {
		StringBuilder text = new StringBuilder();
		for (Image image : images) {
			text.append("&#149;&nbsp;<a href=\"");
			text.append(image.getFileName());
			text.append("\">");
			text.append(_itemFormatter.defaultFormat(image.getSubjectText()));
			text.append("</a>");
			for (ImageOverlay overlay : image.getOverlaysOfType(OverlayType.OLTEXT)) {
				text.append(_itemFormatter.defaultFormat(overlay.overlayText));
			}
		}
		
		writeSentence(text.toString(), -1);
		
//		<p><B>Illustrations</B>. &#149;&nbsp;<a href="../images/ag01.gif">Habit -
//		<I>Agrostis eriantha</I> (plus call of peewee)</a>. <I>Agrostis eriantha</I>
//		var. <I>eriantha</I>. &#149;&nbsp;<a href="../images/ag25.gif">Floret (plus call
//		of magpie)</a>. 
	}
	
}

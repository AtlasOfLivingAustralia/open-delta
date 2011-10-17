package au.org.ala.delta.translation.naturallanguage;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;

/**
 * Writes the html index file as a part of the translation process.
 */
public class HtmlNaturalLanguageTranslator extends NaturalLanguageTranslator {
	
	private IndexWriter _indexWriter;
	
	public HtmlNaturalLanguageTranslator(
			DeltaContext context, ItemListTypeSetter typeSetter, PrintFile printer, ItemFormatter itemFormatter, CharacterFormatter characterFormatter,
            AttributeFormatter attributeFormatter, IndexWriter indexWriter) {
        super(context, typeSetter, printer, itemFormatter, characterFormatter, attributeFormatter);
        _indexWriter = indexWriter;
	}

	@Override
	public void beforeItem(Item item) {
		
		_indexWriter.addItemToIndex(item);
		
		super.beforeItem(item);
	}
	
}

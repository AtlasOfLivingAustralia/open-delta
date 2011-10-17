package au.org.ala.delta.translation.naturallanguage;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * The DataSetFilter is responsible for determining whether elements of a DeltaDataSet
 * should be included in a translation operation.
 * The HtmlNaturalLanguageDataSetFilter differs from it's parent in that
 * it allows the Character specified by the Character for Taxon Images
 * directive through the filter.
 */
public class HtmlNaturalLanguageDataSetFilter extends NaturalLanguageDataSetFilter {

	private int _charForTaxonImages;
	
	/**
	 * Creates a new DataSetFilter
	 * @param context
	 */
	public HtmlNaturalLanguageDataSetFilter(DeltaContext context) {
		super(context);
		Integer charForTaxonImages = _context.getCharacterForTaxonImages();
		if (charForTaxonImages != null) {
			_charForTaxonImages = charForTaxonImages;
		}
		else {
			_charForTaxonImages = -1;
		}
	}
	
	@Override
	public boolean filter(Item item, Character character) {
		
		boolean result = super.filter(item, character);
		
		return result || character.getCharacterId() == _charForTaxonImages ;
	}
}

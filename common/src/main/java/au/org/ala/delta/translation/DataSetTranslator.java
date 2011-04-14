package au.org.ala.delta.translation;

import java.util.logging.Logger;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.attribute.AttributeParser;
import au.org.ala.delta.translation.attribute.ParsedAttribute;
import au.org.ala.delta.translation.attribute.ParsedAttribute.CommentedValues;
import au.org.ala.delta.translation.attribute.ParsedAttribute.Values;

/**
 * The DataSetTranslator iterates through the Items and Attributes of a DeltaDataSet, raising
 * events for translator classes to handle.
 */
public class DataSetTranslator {

	private static Logger logger = Logger.getLogger(DataSetTranslator.class.getName());
	
	private DeltaContext _context;
	private Translator _interested;
	private DataSetFilter _filter;
	private AttributeParser _parser;
	
	public DataSetTranslator(DeltaContext context, Translator interested) {
		_context = context;;
		_interested = interested;
		_filter = new NaturalLanguageDataSetFilter(context);
		_parser = new AttributeParser();
	}
	
	public void translate() {
		
		DeltaDataSet dataSet = _context.getDataSet();
		
		_interested.beforeFirstItem();
		
		int numItems = dataSet.getMaximumNumberOfItems();
		for (int i=1; i<=numItems; i++) {
			
			Item item = dataSet.getItem(i);
			
			if (_filter.filter(item)) {
				_interested.beforeItem(item);
				translateItem(item);	
				_interested.afterItem(item);
			}	
		}
		
		_interested.afterLastItem();
		
		
	}
	
	/**
	 * Iterates through the attributes of an Item.
	 * @param item the item to translate.
	 */
	private void translateItem(Item item) {
		
		DeltaDataSet dataSet = _context.getDataSet();
		
		int numChars = dataSet.getNumberOfCharacters();
		for (int i=1; i<=numChars; i++) {
			
			au.org.ala.delta.model.Character character = dataSet.getCharacter(i);
			Attribute attribute = item.getAttribute(character);
			
			if (_filter.filter(item, character)) {
				logger.fine(item.getItemNumber() + ", "+character.getCharacterId()+" = "+attribute.getValue());
				_interested.beforeAttribute(attribute);
				translateAttribute(attribute);
				_interested.afterAttribute(attribute);
			}	
		}
	}
	
	private void translateAttribute(Attribute attribute) {
		String value = attribute.getValue();
		ParsedAttribute parsedAttribute = _parser.parse(value);
		
		_interested.attributeComment(parsedAttribute.getCharacterComment());
		
		for (CommentedValues values : parsedAttribute.getCommentedValues()) {
			_interested.attributeValues(values.getValues());
			_interested.attributeComment(values.getComment());
		}
	}
}

/**
 * Interface for classes that perform translations of DeltaDataSet.
 */
interface Translator {
	public void beforeFirstItem();
	public void beforeItem(Item item);
	public void afterItem(Item item);
	public void beforeAttribute(Attribute attribute);
	public void afterAttribute(Attribute attribute);
	public void afterLastItem();
	public void attributeComment(String comment);
	public void attributeValues(Values values);
	
}
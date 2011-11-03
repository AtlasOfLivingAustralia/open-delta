package au.org.ala.delta.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.util.Pair;


/**
 * The DataSetTranslator iterates through the Items and Attributes of a DeltaDataSet, raising
 * events for translator classes to handle.
 */
public class AbstractDataSetTranslator implements DataSetTranslator {

	private static Logger logger = Logger.getLogger(AbstractDataSetTranslator.class.getName());
	
	protected DeltaContext _context;
	private List<IterativeTranslator> _translators;
	private List<DataSetFilter> _filters;
	
	
	public AbstractDataSetTranslator(DeltaContext context) {
		_context = context;;
		_translators = new ArrayList<IterativeTranslator>();
		_filters = new ArrayList<DataSetFilter>();
	}
	
	public AbstractDataSetTranslator(DeltaContext context, DataSetFilter filter, IterativeTranslator translator) {
		this(context);
		_translators.add(translator);
		_filters.add(filter);
	}
	
	public void add(Pair<IterativeTranslator, DataSetFilter> translator) {
		_translators.add(translator.getFirst());
		_filters.add(translator.getSecond());
	}
	
	public void translateItems() {
		
		MutableDeltaDataSet dataSet = _context.getDataSet();
		
		beforeFirstItem();
		
		int numItems = dataSet.getMaximumNumberOfItems();
		for (int i=1; i<=numItems; i++) {
			Item item = dataSet.getItem(i);
			String description = RTFUtils.stripFormatting(item.getDescription());
			KeywordSubstitutions.put(KeywordSubstitutions.NAME, description);
			
			item(item);	
		}
		
		afterLastItem();
	}

	protected void item(Item item) {
		for (int i=0; i<_translators.size(); i++) {
			translateItem(item, _translators.get(i), _filters.get(i));
		}
	}

	protected void translateItem(Item item, IterativeTranslator translator, DataSetFilter filter) {
		if (filter.filter(item)) {
			translator.beforeItem(item);
			
			MutableDeltaDataSet dataSet = _context.getDataSet();
			
			int numChars = dataSet.getNumberOfCharacters();
			for (int i=1; i<=numChars; i++) {
				
				Character character = dataSet.getCharacter(i);
				Attribute attribute = item.getAttribute(character);

				if (filter.filter(item, character)) {
					logger.fine(item.getItemNumber() + ", "+character.getCharacterId()+" = "+attribute.getValueAsString());
					translator.beforeAttribute(attribute);
					
					translator.afterAttribute(attribute);
				}	
				
				
			}
			
		    translator.afterItem(item);
		}
	}
	
	public void translateCharacters() {
		
		beforeFirstCharacter();
		
		MutableDeltaDataSet dataSet = _context.getDataSet();
		int numChars = dataSet.getNumberOfCharacters();
		for (int i=1; i<=numChars; i++) {
			
			Character character = dataSet.getCharacter(i);
			
			for (int j=0; j<_translators.size(); j++) {
				translateCharacter(character, _translators.get(j), _filters.get(j));
			}
		}
		
		afterLastCharacter();
	}
	
	protected void translateCharacter(Character character, IterativeTranslator translator, DataSetFilter filter) {
		if (filter.filter(character)) {
			translator.beforeCharacter(character);
		
			translator.afterCharacter(character);
		}	
	}
	
	
	@Override
	public void translateOutputParameter(String parameterName) {}
	
	protected void beforeFirstItem() {
		for (IterativeTranslator translator : _translators) {
			translator.beforeFirstItem();
		}
	}
	
	protected void afterLastItem() {
		for (IterativeTranslator translator : _translators) {
			translator.afterLastItem();
		}
	}
	
	protected void beforeFirstCharacter() {
		for (IterativeTranslator translator : _translators) {
			translator.beforeFirstCharacter();
		}
	}
	
	protected void afterLastCharacter() {
		for (IterativeTranslator translator : _translators) {
			translator.afterLastCharacter();
		}
	}
}

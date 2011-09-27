package au.org.ala.delta.translation.intkey;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.AbstractDataSetTranslator;
import au.org.ala.delta.translation.Printer;
import au.org.ala.delta.translation.attribute.CommentedValueList.Values;
import au.org.ala.delta.translation.delta.DeltaFormatDataSetFilter;

/**
 * The IntkeyFormatTranslator converts a Delta data set into a format that
 * can be used by the Intkey program.
 */
public class IntkeyFormatTranslator extends AbstractDataSetTranslator {

	public IntkeyFormatTranslator(DeltaContext context, Printer printer, ItemFormatter itemFormatter) {
		super(context, new DeltaFormatDataSetFilter(context));
	}
	
	@Override
	public void beforeFirstItem() {
	}

	@Override
	public void beforeItem(Item item) {
	}

	@Override
	public void afterItem(Item item) {
	}

	@Override
	public void beforeAttribute(Attribute attribute) {
	}


	@Override
	public void afterAttribute(Attribute attribute) {}

	@Override
	public void afterLastItem() {}

	@Override
	public void attributeComment(String comment) {}

	@Override
	public void attributeValues(Values values) {}

	

	@Override
	public void beforeFirstCharacter() {
		
		
		
	}

	@Override
	public void beforeCharacter(Character character) {
	}
	
	public void afterCharacter(Character character) {
	}
	
}

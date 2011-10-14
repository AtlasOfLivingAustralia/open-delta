package au.org.ala.delta.translation.print;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;
import au.org.ala.delta.translation.FormattedTextTypeSetter;
import au.org.ala.delta.translation.PrintFile;

public class FormattedItemNameTypeSetter extends FormattedTextTypeSetter {

	public FormattedItemNameTypeSetter(DeltaContext context, PrintFile typeSetter) {
		super(context, typeSetter);
	}

	@Override 
	public void beforeItem(Item item) {
		writeTypeSettingMark(MarkPosition.BEFORE_ITEM_WITH_NATURAL_LANGUAGE);
	}
	
	@Override
	public void beforeItemName() {
		writeTypeSettingMark(MarkPosition.ITEM_DESCRIPTION_BEFORE_ITEM_NAME);
	}

	@Override
	public void afterItemName() {
		writeTypeSettingMark(MarkPosition.ITEM_DESCRIPTION_AFTER_ITEM_NAME);
	}
}

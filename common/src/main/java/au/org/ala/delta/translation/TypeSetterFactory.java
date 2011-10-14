package au.org.ala.delta.translation;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.translation.print.CharacterListTypeSetter;
import au.org.ala.delta.translation.print.FormattedItemNameTypeSetter;

/**
 * Creates the appropriate TypeSetter for the supplied context.  If the TYPESETTING MARKS
 * directive has been specified a FormattedTypeSetter will be returned. Otherwise a
 * PlainTextTypeSetter will be returned.
 * @param context the context in which the translator will run.
 * @param printer used for outputting to the print file.
 * @return a new instance of TypeSetter.
 */
public class TypeSetterFactory {
	
	public ItemListTypeSetter createTypeSetter(DeltaContext context, PrintFile printer) {
		
		if (context.getTypeSettingMarks().isEmpty()) {
			return new PlainTextTypeSetter(printer);
		}
		else {
			return new FormattedTextTypeSetter(context,  printer);
		}
		
	}
	
	/**
	 * Used when creating typesetters for the PRINT ITEM NAMES and PRINT
	 * ITEM DESCRIPTIONS print actions.
	 */
	public ItemListTypeSetter createItemListTypeSetter(DeltaContext context, PrintFile printer) {
		if (context.getTypeSettingMarks().isEmpty()) {
			return new PlainTextTypeSetter(printer);
		}
		else {
			return new FormattedItemNameTypeSetter(context, printer);
		}
	}
	
	
	public CharacterListTypeSetter createCharacterListTypeSetter(DeltaContext context, PrintFile printer) {
		
		if (context.getTypeSettingMarks().isEmpty()) {
			return new au.org.ala.delta.translation.print.PlainTextTypeSetter(printer);
		}
		else {
			return new au.org.ala.delta.translation.print.FormattedTypeSetter(context.getTypeSettingMarks(), printer);
		}
		
	}
}

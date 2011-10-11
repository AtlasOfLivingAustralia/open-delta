package au.org.ala.delta.translation;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.translation.print.CharacterListTypeSetter;

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
			return new FormattedTextTypeSetter(context.getTypeSettingMarks(), printer);
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

package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.TypeSettingMark;

/**
 * Processes the TYPESETTING MARKS directive.
 */
public class TypeSettingMarks extends AbstractFormattingDirective {

	public TypeSettingMarks() {
		super("typesetting", "marks");
	}
	
	@Override
	public void processMark(DeltaContext context, TypeSettingMark mark) {
		context.addTypeSettingMark(mark);
	}
	

}

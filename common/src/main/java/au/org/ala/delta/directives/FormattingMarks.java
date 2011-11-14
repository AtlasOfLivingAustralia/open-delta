package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.TypeSettingMark;

/**
 * Processes the FORMATTING MARKS directive.
 */
public class FormattingMarks extends AbstractFormattingDirective {

	public FormattingMarks() {
		super("formatting", "marks");
	}
	
	@Override
	public void processMark(DeltaContext context, TypeSettingMark mark) {
		context.addFormattingMark(mark);
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}

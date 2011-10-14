package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;

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
		if (mark.getId() == MarkPosition.START_OF_FILE.getId()) {
			context.getOutputFileSelector().setPrintFileHeader(mark.getMarkText());
		}
		else if (mark.getId() == MarkPosition.END_OF_FILE.getId()) {
			context.getOutputFileSelector().setPrintFileFooter(mark.getMarkText());
		}
	}
	

}

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
		
		String text = mark.getMarkText();
		String replacement = " ";
		if (!mark.getAllowLineBreaks()) {
			replacement = "";
		}
		text = text.replaceAll("[\\r\\n]+", replacement);
		TypeSettingMark newMark = new TypeSettingMark(mark.getId(), text, mark.getAllowLineBreaks());
		context.addTypeSettingMark(newMark);
		if (mark.getId() == MarkPosition.START_OF_FILE.getId()) {
			context.getOutputFileSelector().setPrintFileHeader(text);
		}
		else if (mark.getId() == MarkPosition.END_OF_FILE.getId()) {
			context.getOutputFileSelector().setPrintFileFooter(text);
		}
	}
	
	@Override
	public boolean canSpecifyTextDelimiter() {		
		return true;
	}
	
	@Override
	public int getOrder() {
		return 4;
	}

}

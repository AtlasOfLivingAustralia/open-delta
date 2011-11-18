package au.org.ala.delta.delfor.format;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;

public interface FormattingAction {
	public void format(DelforContext context, SlotFileDataSet dataSet);
}

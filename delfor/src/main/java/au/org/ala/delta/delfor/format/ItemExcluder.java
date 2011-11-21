package au.org.ala.delta.delfor.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;

/**
 * The ItemExcluder will delete any items that have been excluded by 
 * an INCLUDE ITEMS or EXCLUDE ITEMS directive.
 */
public class ItemExcluder implements FormattingAction {

	private List<Integer> _toExclude;
	
	public ItemExcluder(List<Integer> toExclude) {
		_toExclude = new ArrayList<Integer>(toExclude);
	}
	@Override
	public void format(DelforContext context, SlotFileDataSet dataSet) {
		
		Collections.sort(_toExclude);
		// Delete items in reverse order so the reordering will not
		// change the numbers of the items we are deleting.
		for (int i=_toExclude.size()-1; i>=0; i--) {
			dataSet.deleteItem(dataSet.getItem(_toExclude.get(i)));
		}
	}

}

package au.org.ala.delta.delfor.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;


/**
 * The CharacterExcluder will delete any Characters that have been excluded by 
 * an INCLUDE CHARACTERS or EXCLUDE CHARACTERS directive.
 */
public class CharacterExcluder implements FormattingAction {

	private List<Integer> _toExclude;
	
	public CharacterExcluder(List<Integer> toExclude) {
		_toExclude = new ArrayList<Integer>(toExclude);
	}
	@Override
	public void format(DelforContext context, SlotFileDataSet dataSet) {
		
		Collections.sort(_toExclude);
		// Delete characters in reverse order so the reordering will not
		// change the numbers of the characters we are deleting.
		for (int i=_toExclude.size()-1; i>=0; i--) {
			dataSet.deleteCharacter(dataSet.getCharacter(_toExclude.get(i)));
		}
	}

}

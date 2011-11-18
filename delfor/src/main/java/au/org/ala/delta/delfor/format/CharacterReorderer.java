package au.org.ala.delta.delfor.format;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.model.Character;

public class CharacterReorderer implements FormattingAction {

	private List<Integer> _newOrder;
	
	public CharacterReorderer(List<Integer> newOrder) {
		_newOrder = newOrder;
	}
	
	
	@Override
	public void format(DelforContext context, SlotFileDataSet dataSet) {
		
		DeltaVOP vop = dataSet.getVOP();
		
		// The reordering is much easier if we do it by id, as once we 
		// move the first character the rest are renumbered.
		List<Integer> ids = new ArrayList<Integer>();
		for (int i=1; i<=dataSet.getNumberOfCharacters(); i++) {
			ids.add(vop.getDeltaMaster().uniIdFromCharNo(i));
		}
		
		for (int i=0; i<_newOrder.size(); i++) {
			
			int toMove = _newOrder.get(i);
			int toPosition = i + 1;
			
			int charId = ids.get(toMove-1);
			int charNumToMove = vop.getDeltaMaster().charNoFromUniId(charId);
			Character character = dataSet.getCharacter(charNumToMove);
			dataSet.moveCharacter(character, toPosition);
		}
	}

}

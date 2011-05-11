package au.org.ala.delta.editor.model;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

public class DeltaViewModel extends DataSetWrapper implements EditorViewModel {

	public DeltaViewModel(EditorDataModel model) {
		super(model);
	}
	
	@Override
	public void setSelectedItem(Item selectedItem) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSelectedCharacter(Character selectedCharacter) {
		// TODO Auto-generated method stub

	}

	@Override
	public Item getSelectedItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Character getSelectedCharacter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortName() {
		// TODO Auto-generated method stub
		return null;
	}

}

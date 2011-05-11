package au.org.ala.delta.editor.model;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * The DeltaViewModel is designed to provide each view with a separate instance of
 * the model to work with.  The main reason for this is to allow components of the view
 * to register themselves as observers of the data model without needing to deregister when
 * the view is closed.
 */
public class DeltaViewModel extends DataSetWrapper implements EditorViewModel {

	/** The model we delegate to */
	private EditorDataModel _editorDataModel;
	
	public DeltaViewModel(EditorDataModel model) {
		super(model);
		_editorDataModel = model;
	}
	
	@Override
	public void setSelectedItem(Item selectedItem) {
		_editorDataModel.setSelectedItem(selectedItem);
	}

	@Override
	public void setSelectedCharacter(Character selectedCharacter) {
		_editorDataModel.setSelectedCharacter(selectedCharacter);
	}

	@Override
	public Item getSelectedItem() {
		return _editorDataModel.getSelectedItem();
	}

	@Override
	public Character getSelectedCharacter() {
		return _editorDataModel.getSelectedCharacter();
	}

	@Override
	public String getShortName() {
		return _editorDataModel.getShortName();
	}
}

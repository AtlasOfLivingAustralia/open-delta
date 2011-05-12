package au.org.ala.delta.editor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;

/**
 * The DeltaViewModel is designed to provide each view with a separate instance of
 * the model to work with.  The main reason for this is to allow components of the view
 * to register themselves as observers of the data model without needing to track them
 * carefully and deregister them individually when the view closes.
 * PreferenceChangeListener support has been implemented with the same philosophy.
 */
public class DeltaViewModel extends DataSetWrapper implements EditorViewModel, PreferenceChangeListener {

	/** The model we delegate to */
	private EditorDataModel _editorDataModel;
	
	/** A list of objects interested in being notified of preference changes */
	private List<PreferenceChangeListener> _preferenceChangeListeners;
	
	public DeltaViewModel(EditorDataModel model) {
		super(model);
		_editorDataModel = model;
		_editorDataModel.addPreferenceChangeListener(this);
		_preferenceChangeListeners = new ArrayList<PreferenceChangeListener>();
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
	
	public void addPreferenceChangeListener(PreferenceChangeListener listener) {
		_preferenceChangeListeners.add(listener);
	}
	
	public void removePreferenceChangeListener(PreferenceChangeListener listener) {
		_preferenceChangeListeners.remove(listener);
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		for (PreferenceChangeListener listener : _preferenceChangeListeners) {
			listener.preferenceChange(evt);
		}
	}
}

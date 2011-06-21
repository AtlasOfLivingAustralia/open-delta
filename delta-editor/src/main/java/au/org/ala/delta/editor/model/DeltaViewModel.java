package au.org.ala.delta.editor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.image.Image;

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
	
	/** The currently selected character */
	private Character _selectedCharacter;
	
	/** the currently selected image */ 
	private Image _selectedImage;
	
	/** The number of the selected state.  Only valid when the selected
	 * character is a multistate character (otherwise it's -1).
	 */
	private int _selectedState;
	
	/** The currently selected item */
	private Item _selectedItem;
	
	public DeltaViewModel(EditorDataModel model) {
		super(model);
		_editorDataModel = model;
		_editorDataModel.addPreferenceChangeListener(this);
		_preferenceChangeListeners = new ArrayList<PreferenceChangeListener>();
	}
	
	@Override
	public void setSelectedItem(Item selectedItem) {
		_selectedItem = selectedItem;
	}

	@Override
	public void setSelectedCharacter(Character selectedCharacter) {
		if (_selectedCharacter == null || selectedCharacter == null || 
				!_selectedCharacter.equals(selectedCharacter)) {
			_selectedState = -1;
		}
		
		_selectedCharacter = selectedCharacter;
	}
	
	@Override
	public void setSelectedState(int state) {
		if (!(_selectedCharacter instanceof MultiStateCharacter)) {
			_selectedState = -1;
		}
		_selectedState = state;
	}
	
	@Override
	public int getSelectedState() {
		return _selectedState;
	}

	@Override
	public Item getSelectedItem() {
		return _selectedItem;
	}

	@Override
	public Character getSelectedCharacter() {
		return _selectedCharacter;
	}
	
	@Override
	public Image getSelectedImage() {
		return _selectedImage;
	}
	
	@Override
	public void setSelectedImage(Image image) {
		_selectedImage = image;
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

	public String getImagePath() {
		return _editorDataModel.getImagePath();
	}
}

package au.org.ala.delta.editor.ui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.ui.rtf.RtfEditor;
import au.org.ala.delta.ui.rtf.RtfToolBar;

/**
 * The CharacterEditTab is a base class for tabs displayed on the CharacterEditor.
 */
public abstract class CharacterEditTab extends JPanel {

	private static final long serialVersionUID = 6534903951320447689L;

	protected RtfEditor editor;
	
	/** The character that is being edited by this tab */
	protected Character _character;
	
	/** The model being edited by this tab */
	protected EditorViewModel _model;

	/** Can be used by tabs for rich text support */
	protected RtfToolBar _toolbar;
	
	public CharacterEditTab(RtfToolBar toolbar) {
		super();
		_toolbar = toolbar;
	}

	public CharacterEditTab(LayoutManager layout) {
		super(layout);
	}

	public CharacterEditTab(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public CharacterEditTab(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public boolean isContentsValid() {
		return true;
	}
	
	/**
	 * Sets the Character for editing.
	 * @param character the Character to edit.
	 */
	public abstract void bind(EditorViewModel model, au.org.ala.delta.model.Character character);

}
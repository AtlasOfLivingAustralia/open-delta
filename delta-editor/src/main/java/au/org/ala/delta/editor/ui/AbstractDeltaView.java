package au.org.ala.delta.editor.ui;

import javax.swing.JInternalFrame;

import au.org.ala.delta.editor.DeltaView;

/**
 * Doesn't do much - saves child classes from implementing DeltaView methods
 * that they don't need.
 */
public abstract class AbstractDeltaView extends JInternalFrame implements DeltaView {

	private static final long serialVersionUID = 8155132044926348203L;


	@Override
	public void open() {
	}

	@Override
	public boolean editsValid() {
		return true;
	}

	@Override
	public ReorderableList getCharacterListView() {
		return null;
	}

	@Override
	public ReorderableList getItemListView() {
		return null;
	}

	@Override
	public boolean canClose() {
		return true;
	}

}

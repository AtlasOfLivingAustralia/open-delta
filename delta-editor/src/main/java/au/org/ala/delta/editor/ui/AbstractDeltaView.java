package au.org.ala.delta.editor.ui;

import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import au.org.ala.delta.editor.DeltaView;

/**
 * Doesn't do much - saves child classes from implementing DeltaView methods that they don't need.
 */
public abstract class AbstractDeltaView extends JInternalFrame implements DeltaView {

	private static final long serialVersionUID = 8155132044926348203L;

	private JInternalFrame _owner;

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

	protected void setOwner(JInternalFrame owner) {
		_owner = owner;
		if (_owner != null) {
			addInternalFrameListener(new InternalFrameAdapter() {
				@Override
				public void internalFrameClosed(InternalFrameEvent e) {
					if (_owner != null) {
						try {
							_owner.setSelected(true);
						} catch (PropertyVetoException ex) {
							// ignore
						}
						_owner.requestFocus();
					}
				}
			});
		}
	}

}

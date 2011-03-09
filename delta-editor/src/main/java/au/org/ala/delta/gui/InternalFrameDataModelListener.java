package au.org.ala.delta.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;


/**
 * Keeps an instance of EditorDataModel in sync with a JInternalFrame.
 */
public class InternalFrameDataModelListener implements PropertyChangeListener, InternalFrameListener {

	private JInternalFrame _frame;
	private EditorDataModel _model;
	private String _frameTitle;

	public InternalFrameDataModelListener(JInternalFrame frame, EditorDataModel model, String frameTitle) {
		_frame = frame;
		_model = model;
		_frameTitle = frameTitle;
		model.addPropertyChangeListener(this);
		frame.addInternalFrameListener(this);
		updateTitle();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("name".equals(evt.getPropertyName())) {
			updateTitle();
		}
	}

	public void updateTitle() {
		_frame.setTitle(String.format(_frameTitle, _model.getName()));
	}

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		_model.removePropertyChangeListener(this);
	}

	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
	}

	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
	}

}

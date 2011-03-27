package au.org.ala.delta.editor.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.model.observer.DeltaDataSetObserver;


/**
 * Keeps an instance of EditorDataModel in sync with a JInternalFrame.
 */
public class InternalFrameDataModelListener implements PropertyChangeListener, InternalFrameListener, DeltaDataSetObserver {

	private JInternalFrame _frame;
	private EditorDataModel _model;
	private String _frameTitle;

	public InternalFrameDataModelListener(JInternalFrame frame, EditorDataModel model, String frameTitle) {
		_frame = frame;
		_model = model;
		_frameTitle = frameTitle;
		model.addPropertyChangeListener(this);
		frame.addInternalFrameListener(this);
		_model.addDeltaDataSetObserver(this);
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
		_model.removeDeltaDataSetObserver(this);
		
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
	
	@Override
	public void itemAdded(DeltaDataSetChangeEvent event) {
		_frame.repaint();
	}

	@Override
	public void itemDeleted(DeltaDataSetChangeEvent event) {
		_frame.repaint();
	}

	@Override
	public void itemMoved(DeltaDataSetChangeEvent event) {
		_frame.repaint();
	}

	@Override
	public void itemEdited(DeltaDataSetChangeEvent event) {
		_frame.repaint();
	}

	@Override
	public void itemSelected(DeltaDataSetChangeEvent event) {
		_frame.repaint();
	}

	
	@Override
	public void characterAdded(DeltaDataSetChangeEvent event) {
		_frame.repaint();
	}

	@Override
	public void characterDeleted(DeltaDataSetChangeEvent event) {
		_frame.repaint();
	}

	@Override
	public void characterMoved(DeltaDataSetChangeEvent event) {
		_frame.repaint();
	}

	@Override
	public void characterEdited(DeltaDataSetChangeEvent event) {
		_frame.repaint();
	}

	@Override
	public void characterSelected(DeltaDataSetChangeEvent event) {
		_frame.repaint();
	}

}

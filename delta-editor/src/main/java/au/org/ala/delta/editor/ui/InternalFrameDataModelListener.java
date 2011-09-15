package au.org.ala.delta.editor.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.model.observer.DeltaDataSetObserver;


/**
 * Keeps an instance of EditorDataModel in sync with a JInternalFrame.
 */
public class InternalFrameDataModelListener extends InternalFrameAdapter implements PropertyChangeListener, DeltaDataSetObserver {

	private JInternalFrame _frame;
	private EditorDataModel _model;
	private String _frameTitle;

	public InternalFrameDataModelListener(JInternalFrame frame, EditorDataModel model, String frameTitle) {
		_frame = frame;
		_model = model;
		_frameTitle = frameTitle;
		model.addPropertyChangeListener(this);
		frame.addInternalFrameListener(this);
		model.addDeltaDataSetObserver(this);
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
	public void internalFrameClosed(InternalFrameEvent e) {
		_model.removePropertyChangeListener(this);
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
	
	@Override
	public void imageEdited(DeltaDataSetChangeEvent event) {
		_frame.repaint();
	}
	
	@Override
	public void characterTypeChanged(DeltaDataSetChangeEvent event) {
		_frame.repaint();	
	}
	
}

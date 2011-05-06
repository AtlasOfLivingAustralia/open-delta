package au.org.ala.delta.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.editor.ui.EditorDataModel;
import au.org.ala.delta.editor.ui.InternalFrameDataModelListener;
import au.org.ala.delta.model.DeltaDataSetRepository;
import au.org.ala.delta.ui.MessageDialogHelper;

/**
 * The DeltaViewControllers is responsible for managing a single instance of the EditorDataModel
 * and any associated views.
 */
public class DeltaViewController extends InternalFrameAdapter implements VetoableChangeListener {

	private static final String DELTA_FILE_EXTENSION = "dlt";
	
	private DeltaEditor _deltaEditor;
	
	/** The model this controller controls */
	private  EditorDataModel _dataSet;
	
	/** Used for saving the model */
	private DeltaDataSetRepository _repository;

	/** Keeps track of the active views of the model */
	private List<JInternalFrame> _activeViews;
	
	private String _newDataSetName;
	
	private String _closeWithoutSavingMessage;
	
	/** 
	 * Set while the closeAll method is being invoked, this flag modifies the behavior of 
	 * the close operations.
	 */
	private boolean _closingAll;
	
	/**
	 * Creates a new DeltaViewController.
	 * 
	 * @param dataSet
	 *            The data set associated with the viewer
	 * @param deltaEditor
	 *            Reference to the instance of DeltaEditor that created the viewer
	 */
	public DeltaViewController(EditorDataModel dataSet, DeltaEditor deltaEditor, DeltaDataSetRepository repository) {
		_dataSet = dataSet;
		_deltaEditor = deltaEditor;
		_repository = repository;
		_closingAll = false;
		_newDataSetName = "";
		_activeViews = new ArrayList<JInternalFrame>();
	}

	public void setNewDataSetName(String newDataSetName) {
		_newDataSetName = newDataSetName;
	}
	
	public void setCloseWithoutSavingMessage(String windowClosingMessage) {
		_closeWithoutSavingMessage = windowClosingMessage;
	}

	/**
	 * If the last view of a modified model is about to be closed this method will
	 * intervene and ask the user if they want to save.
	 * If the user selects cancel, the view will not be closed.
	 */
	@Override
	public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {

		
		if (JInternalFrame.IS_CLOSED_PROPERTY.equals(e.getPropertyName()) && (e.getNewValue().equals(Boolean.TRUE))) {
			
			if (_activeViews.size() == 1) {
				boolean canClose = confirmClose();
				if (!canClose) {
					throw new PropertyVetoException("Close cancelled", e);
				}
			}
		}
		else if (JInternalFrame.IS_SELECTED_PROPERTY.equals(e.getPropertyName()) && (e.getNewValue().equals(Boolean.FALSE))) {
			// Veto change if the frame is invalid.
		}
	}
	
	/**
	 * Notifies this controller there is a new view interested in the model.
	 * @param view the new view of the model.
	 */
	public void viewerOpened(JInternalFrame view) {
		_activeViews.add(view);
		view.addVetoableChangeListener(this);
		view.addInternalFrameListener(this);
		if (view instanceof DeltaView) {
			new InternalFrameDataModelListener(view, _dataSet, ((DeltaView)view).getViewTitle());
		}
	}
	
	
	/**
	 * Removes the view from the ones being tracked.
	 */
	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		if (!_closingAll) {
			_activeViews.remove(e.getInternalFrame());
		}
	}


	/**
	 * Asks the user whether they wish to save before closing.  If this method returns false
	 * the close will be aborted.
	 * @param model the model to be closed.
	 * @return true if the close can proceed.
	 */
	private boolean confirmClose() {
		if (_closingAll) {
			return true;
		}
		boolean canClose = true;
		if (_dataSet.isModified()) {
			String title = _dataSet.getName();
			if (title != null) {
				title = new File(title).getName();
			}
			else {
				title = newDataSetName();
			}
			int result = MessageDialogHelper.showConfirmDialog(_deltaEditor.getMainFrame(), title, _closeWithoutSavingMessage, 20);
			canClose = (result != JOptionPane.CANCEL_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				save();
			}
		}
		return canClose;
	}
	
	/**
	 * Saves the model.
	 */
	public void save() {
		if (StringUtils.isEmpty(_dataSet.getName())) {
			saveAs();
		}
		else {
			_repository.save(_dataSet.getCurrentDataSet(), null);
		}
	}
	
	/**
	 * Closes all views of the model, asking the user to save first if the model has been
	 * modified.
	 * @return true if the close proceeded.
	 */
	public boolean closeAll() {
		
		if (confirmClose()) {
			try {
				_closingAll = true;
				for (JInternalFrame view : _activeViews) {
					view.setClosed(true);
				}
				_activeViews.clear();
				return true;
			}
			catch (PropertyVetoException e) {}
			finally {
				_closingAll = false;
			}
		}
		return false;
	}
	
	
	/**
	 * Saves the model using a different name.
	 */
	public void saveAs() {

		File newFile = _deltaEditor.selectFile(false);
		
		if (newFile != null) {
			if (newFile.exists()){
				JOptionPane.showMessageDialog(_deltaEditor.getMainFrame(), "File already exists.");
				return;
			}
			if (!newFile.getName().endsWith("."+DELTA_FILE_EXTENSION)) {
				newFile = new File(newFile.getAbsolutePath()+"."+DELTA_FILE_EXTENSION);
			}
			
			
			_repository.saveAsName(_dataSet.getCurrentDataSet(), newFile.getAbsolutePath(), null);
			_dataSet.setName(newFile.getAbsolutePath());
			EditorPreferences.addFileToMRU(newFile.getAbsolutePath());
			
		}
	}
	
	/**
	 * Returns true if this controller is managing the supplied view.
	 * @param view the view to check.
	 * @return true if this controller manages the view.
	 */
	public boolean controls(JInternalFrame view) {
		return _activeViews.contains(view);
	}
	
	
	private String newDataSetName() {
		return _newDataSetName;
	}
	
	public EditorDataModel getModel() {
		return _dataSet;
	}
	
	/**
	 * 
	 * @return the number of active views of the model being controlled by this controller.
	 */
	public int getViewCount() {
		return _activeViews.size();
	}
	
}
 
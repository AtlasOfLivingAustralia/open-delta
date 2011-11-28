/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.editor.model.DeltaViewModel;
import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.editor.ui.InternalFrameDataModelListener;
import au.org.ala.delta.model.DeltaDataSetRepository;
import au.org.ala.delta.ui.MessageDialogHelper;

/**
 * The DeltaViewControllers is responsible for managing a single instance of the EditorDataModel
 * and any associated views.
 */
public class DeltaViewController extends InternalFrameAdapter implements VetoableChangeListener{

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
	
	private DeltaViewFactory _viewFactory;
	
	private Map<DeltaView, DeltaViewModel> _models;
	
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
		_viewFactory = new DeltaViewFactory();
		_activeViews = new ArrayList<JInternalFrame>();
		_models = new HashMap<DeltaView, DeltaViewModel>();
		_observers = new ArrayList<DeltaViewStatusObserver>();
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
			if (((DeltaView)e.getSource()).canClose()) {
				if (_activeViews.size() == 1) {
					boolean canClose = confirmClose();
					if (!canClose) {
						throw new PropertyVetoException("Close cancelled", e);
					}
				}
			}
			else {
				throw new PropertyVetoException("Close cancelled by view", e);
			}
		}
		else if (JInternalFrame.IS_SELECTED_PROPERTY.equals(e.getPropertyName())) {
			
			for (JInternalFrame frame : _activeViews) {
				DeltaView view = (DeltaView)frame;
				if (!view.editsValid()) {
					throw new PropertyVetoException("Select cancelled", e);
				}
			}
		}
	}
	
	/**
	 * Notifies this controller there is a new view interested in the model.
	 * @param view the new view of the model.
	 */
	public void viewerOpened(DeltaView view, DeltaViewModel model) {
		JInternalFrame frameView = (JInternalFrame)view;
		_activeViews.add(frameView);
		frameView.addVetoableChangeListener(this);
		frameView.addInternalFrameListener(this);
		_models.put(view, model);
		new InternalFrameDataModelListener(frameView, _dataSet, view.getViewTitle());
	}
	
	
	/**
	 * Removes the view from the ones being tracked.
	 */
	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		JInternalFrame frame = e.getInternalFrame();
		if (!_closingAll) {
			_activeViews.remove(frame);
		}
		DeltaView view = (DeltaView)frame;
		DeltaViewModel model = _models.remove(view);
		_dataSet.removeDeltaDataSetObserver(model);
		_dataSet.removePreferenceChangeListener(model);
		fireViewClosed(view);
		
		if (_activeViews.size() == 0) {
			_dataSet.close();
		}
	}
	
	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		fireViewSelected((DeltaView)e.getInternalFrame());
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
			_repository.save(_dataSet.getDeltaDataSet(), null);
			_dataSet.setModified(false);
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
				_dataSet.close();
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
			
			_repository.saveAsName(_dataSet.getDeltaDataSet(), newFile.getAbsolutePath(), null);
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
		if (_closingAll) {
			return 0;
		}
		return _activeViews.size();
	}
	

	public DeltaView createTreeView() {
		DeltaViewModel model = createViewModel();
		DeltaView view = _viewFactory.createTreeView(model);
		
		new TreeCharacterController(view.getCharacterListView(), model);
		new StateController(view.getCharacterListView(), model);
		new ItemController(view.getItemListView(), model);
		
		viewerOpened(view, model);
		
		return view;
	}
	
	public DeltaView createGridView() {
		DeltaViewModel model = createViewModel();
		DeltaView view = _viewFactory.createGridView(model);
		new CharacterController(view.getCharacterListView(), model);
		new ItemController(view.getItemListView(), model);
		viewerOpened(view, model);
		
		return view;
	}
	
	public DeltaView createItemEditView() {
		
		DeltaViewModel model = createViewModel();
		DeltaView view = _viewFactory.createItemEditView(model, getCurrentView());
		viewerOpened(view, model);
		
		return view;
	}
	
	private JInternalFrame getCurrentView() {
		for (JInternalFrame frame : _activeViews) {
			if (frame.isSelected()) {
				return frame;
			}
		}
		return null;
	}
	
	public DeltaView createCharacterEditView() {		
		DeltaViewModel model = createViewModel();		
		DeltaView view = _viewFactory.createCharacterEditView(model, getCurrentView());
		viewerOpened(view, model);
		
		return view;
	}
	
	public DeltaView createImageEditorView() {
		DeltaViewModel model = createViewModel();
		DeltaView view = _viewFactory.createImageEditorView(model);
		viewerOpened(view, model);
		
		return view;
	}
	
	public DeltaView createDirectivesEditorView() {
		DeltaViewModel model = createViewModel();
		DeltaView view = _viewFactory.createDirectivesEditorView(model);
		viewerOpened(view, model);
		
		return view;
	}
	
	public DeltaView createActionSetsView() {
		DeltaViewModel model = createViewModel();
		DeltaView view = _viewFactory.createActionSetsView(model);
		viewerOpened(view, model);
		
		return view;
	}

	private DeltaViewModel createViewModel() {
		
		DeltaViewModel model = new DeltaViewModel(_dataSet);
		DeltaViewModel selectedModel = selectedViewModel();
		if (selectedModel != null) {
			model.setSelectedCharacter(selectedModel.getSelectedCharacter());
			model.setSelectedItem(selectedModel.getSelectedItem());
			model.setSelectedState(selectedModel.getSelectedState());
			model.setSelectedImage(selectedModel.getSelectedImage());
			model.setSelectedDirectiveFile(selectedModel.getSelectedDirectiveFile());
		}
		return model;
	}
	
	private DeltaViewModel selectedViewModel() {
		for (JInternalFrame view : _activeViews) {
			if (view.isSelected()) {
				DeltaView deltaView = (DeltaView)view;
				return _models.get(deltaView);
			}
		}
		return null;
	}
	
	private List<DeltaViewStatusObserver> _observers;
	public void addDeltaViewStatusObserver(DeltaViewStatusObserver observer) {
		_observers.add(observer);
	}
	
	
	protected void fireViewClosed(DeltaView view) {
		for (int i=_observers.size()-1; i>=0; i--) {
			_observers.get(i).viewClosed(this, view);
		}
	}
	
	protected void fireViewSelected(DeltaView view) {
		for (int i=_observers.size()-1; i>=0; i--) {
			_observers.get(i).viewSelected(this, view);
		}
	}
	
}
 

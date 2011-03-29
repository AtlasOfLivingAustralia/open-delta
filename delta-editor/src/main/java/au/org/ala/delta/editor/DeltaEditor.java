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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ProxyActions;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;

import au.org.ala.delta.editor.directives.ImportController;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.editor.ui.EditorDataModel;
import au.org.ala.delta.editor.ui.ItemEditor;
import au.org.ala.delta.editor.ui.MatrixViewer;
import au.org.ala.delta.editor.ui.StatusBar;
import au.org.ala.delta.editor.ui.TreeViewer;
import au.org.ala.delta.editor.ui.help.HelpConstants;
import au.org.ala.delta.model.AbstractObservableDataSet;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.DeltaDataSetRepository;
import au.org.ala.delta.ui.AboutBox;
import au.org.ala.delta.ui.MessageDialogHelper;
import au.org.ala.delta.ui.help.HelpController;
import au.org.ala.delta.ui.util.IconHelper;
import au.org.ala.delta.util.IProgressObserver;

/**
 * The main class for the DELTA Editor.
 */
@ProxyActions("copyAll")
public class DeltaEditor extends InternalFrameApplication {

	/** Helper class for notifying listeners of property changes */
	private PropertyChangeSupport _propertyChangeSupport;

	private static final long serialVersionUID = 1L;

	private StatusBar _statusBar;

	private ActionMap _actionMap;

	// Yuk
	private DeltaDataSetRepository _dataSetRepository;

	private boolean _saveEnabled;
	private boolean _saveAsEnabled;
	/** Flag to keep track of whether we have already asked the user if they want to save before closing */
	private boolean _closingFromMenu;

	private HelpController _helpController;

	private int numViewersOpen;

	JMenu _fileMenu;

	@Resource
	String windowTitleWithoutFilename;

	@Resource
	String windowTitleWithFilename;

	@Resource
	private String warning;
	@Resource
	private String warningTitle;
	@Resource 
	private String closeWithoutSavingMessage;
	@Resource 
	private String newDataSetName;
	

	/** Tracks the data set being edited by which internal frame is currently focused */
	private EditorDataModel _selectedDataSet;
	/** Tracks the views open for each data set */
	private Map<AbstractObservableDataSet, List<JInternalFrame>> _activeViews;

	public static void main(String[] args) {
		launch(DeltaEditor.class, args);
	}

	public boolean getSaveEnabled() {
		return _saveEnabled;
	}

	/**
	 * A dummy property to disable the menus that aren't yet implemented.
	 * 
	 * @return always returns false
	 */
	public boolean isEnabled() {
		return false;
	}

	@Override
	protected void startup() {
		
		_saveEnabled = false;
		_saveAsEnabled = false;
		_closingFromMenu = false;
		_propertyChangeSupport = new PropertyChangeSupport(this);
		_activeViews = new HashMap<AbstractObservableDataSet, List<JInternalFrame>>();

		ResourceMap resourceMap = getContext().getResourceMap(AboutBox.class);
		resourceMap.injectFields(this);

		_actionMap = getContext().getActionMap(this);

		JFrame frame = getMainFrame();
		frame.setPreferredSize(new Dimension(800, 600));

		frame.setIconImages(IconHelper.getBlueIconList());

		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		addExitListener(new ExitListener() {
			
			@Override
			public void willExit(EventObject event) {}
			
			@Override
			public boolean canExit(EventObject event) {
				_closingFromMenu = true;
				
				boolean canClose = true;
				
				// Copy the keys to prevent concurrent modification exceptions.
				// (because keys are removed when there are no more active views of a dataset.
				Set<AbstractObservableDataSet> dataSets = new HashSet<AbstractObservableDataSet>(_activeViews.keySet());
				for (AbstractObservableDataSet dataSet : dataSets) {
					
					canClose = confirmClose(dataSet);
					if (canClose) {
						closeAll(dataSet);
					}
				}
				_closingFromMenu = false;
				return canClose;
			}
		});

		_helpController = new HelpController("help/delta_editor/DeltaEditor");
		_dataSetRepository = new SlotFileRepository();

		_statusBar = new StatusBar();
		getMainView().setStatusBar(_statusBar);

		getMainView().setMenuBar(buildMenus());

		_helpController.enableHelpKey(frame);

		createDesktop();
		show(_desktop);

	}

	@Override
	protected void ready() {

		JOptionPane.showConfirmDialog(getMainFrame(), warning, warningTitle, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
		super.ready();
	}

	public EditorDataModel getCurrentDataSet() {
		return _selectedDataSet;
	}

	private JMenuBar buildMenus() {

		JMenuBar menuBar = new JMenuBar();

		// File menu. This on is kind of special, in that it gets rebuilt each time a file is opened.
		_fileMenu = new JMenu();
		_fileMenu.setName("mnuFile");
		buildFileMenu(_fileMenu);
		menuBar.add(_fileMenu);

		// Edit Menu

		JMenu mnuEdit = buildEditMenu();
		menuBar.add(mnuEdit);

		// View Menu
		JMenu mnuView = buildViewMenu();

		menuBar.add(mnuView);

		// Window Menu
		JMenu mnuWindow = new JMenu();
		mnuWindow.setName("mnuWindow");
		JMenuItem mnuItTile = new JMenuItem();
		mnuItTile.setAction(_actionMap.get("tileFrames"));
		mnuWindow.add(mnuItTile);

		mnuWindow.addSeparator();

		JMenu mnuLF = new JMenu();
		mnuLF.setName("mnuLF");
		mnuWindow.add(mnuLF);

		JMenuItem mnuItMetalLF = new JMenuItem(new LookAndFeelAction(getMainFrame(), new MetalLookAndFeel()));
		mnuItMetalLF.setName("mnuItMetalLF");
		mnuLF.add(mnuItMetalLF);

		try {
			Class<?> c = Class.forName(UIManager.getSystemLookAndFeelClassName());
			LookAndFeel sysLaf = (LookAndFeel) c.newInstance();
			JMenuItem mnuItWindowsLF = new JMenuItem(new LookAndFeelAction(getMainFrame(), sysLaf));
			mnuItWindowsLF.setName("mnuItWindowsLF");
			mnuLF.add(mnuItWindowsLF);
		} catch (Exception ex) {
			// do nothing
		}
		try {
			// Nimbus L&F was added in update java 6 update 10.
			LookAndFeel nimbusLaF = (LookAndFeel) Class.forName("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel").newInstance();
			JMenuItem mnuItNimbusLF = new JMenuItem(new LookAndFeelAction(getMainFrame(), nimbusLaF));
			mnuItNimbusLF.setName("mnuItNimbusLF");
			mnuLF.add(mnuItNimbusLF);
		} catch (Exception e) {
			// The Nimbus L&F is not available, no matter.
		}
		menuBar.add(mnuWindow);

		JMenu mnuHelp = new JMenu();
		mnuHelp.setName("mnuHelp");
		JMenuItem mnuItHelpContents = new JMenuItem();
		mnuItHelpContents.setName("mnuItHelpContents");
		mnuHelp.add(mnuItHelpContents);
		mnuItHelpContents.addActionListener(_helpController.helpAction());

		JMenuItem mnuItHelpOnSelection = new JMenuItem(IconHelper.createImageIcon("help_cursor.png"));
		mnuItHelpOnSelection.setName("mnuItHelpOnSelection");

		mnuItHelpOnSelection.addActionListener(_helpController.helpOnSelectionAction());
		mnuHelp.add(mnuItHelpOnSelection);

		JMenuItem mnuItAbout = new JMenuItem();
		mnuItAbout.setAction(_actionMap.get("openAbout"));

		mnuHelp.addSeparator();
		mnuHelp.add(mnuItAbout);

		menuBar.add(mnuHelp);

		return menuBar;
	}

	private void buildFileMenu(JMenu mnuFile) {

		mnuFile.removeAll();
	

		String[] fileMenuActions = { 
				"newFile", "loadFile", "closeFile", "-", 
				"saveFile", "saveAsFile", "-", 
				"importDirectives", "exportDirectives"};

		for (String action : fileMenuActions) {
			addMenu(mnuFile, action);
		}
		
		JMenuItem mnuItFileExit = new JMenuItem();
		mnuItFileExit.setAction(_actionMap.get("exitApplication"));

		mnuFile.addSeparator();
		String[] previous = EditorPreferences.getPreviouslyUsedFiles();
		if (previous != null && previous.length > 0) {
			javax.swing.Action a = this._actionMap.get("loadPreviousFile");
			if (a != null) {
				for (int i = 0; i < previous.length; ++i) {
					String filename = previous[i];
					JMenuItem item = new JMenuItem();
					item.setAction(a);
					item.setText(String.format("%d %s", i + 1, filename));
					item.putClientProperty("Filename", filename);
					item.setMnemonic(KeyEvent.VK_1 + i);					
					mnuFile.add(item);
				}
				mnuFile.addSeparator();
			}
		}
		mnuFile.add(mnuItFileExit);

	}
	
	/**
	 * Loads a previously loaded delta file from the Most Recently Used list.
	 * It is assumed that the source ActionEvent as set the filename in a client property
	 * called "Filename".
	 * 
	 * @param e The action event that triggered this action
	 * @return A DeltaFileLoader task
	 */
	@Action(block=BlockingScope.APPLICATION)
	public DeltaFileLoader loadPreviousFile(ActionEvent e) {
		DeltaFileLoader fileOpenTask = null;
		JComponent item = (JComponent) e.getSource();
		if (item != null) {
			File toOpen = new File((String) item.getClientProperty("Filename"));
			if (toOpen != null && toOpen.exists()) {
				fileOpenTask = new DeltaFileLoader(this, toOpen);
				fileOpenTask.addPropertyChangeListener(_statusBar);
			}
		}
		return fileOpenTask;		 
	}

	private JMenu buildEditMenu() {
		JMenu mnuEdit = new JMenu();
		mnuEdit.setName("mnuEdit");

		String[] viewMenuActions = { "copy", "paste", "-", "copyAll" };

		for (String action : viewMenuActions) {
			addMenu(mnuEdit, action);
		}

		return mnuEdit;

	}

	/**
	 * Builds and returns the View menu.
	 * 
	 * @return a new JMenu ready to be added to the menu bar.
	 */
	private JMenu buildViewMenu() {
		JMenu mnuView = new JMenu();
		mnuView.setName("mnuView");

		String[] viewMenuActions = { "newTreeView", "newGridView", "-", "viewCharacterEditor", "viewTaxonEditor", "-", "viewActionSets", "viewImageSettings" };

		for (String action : viewMenuActions) {
			addMenu(mnuView, action);
		}

		return mnuView;
	}

	/**
	 * Creates and adds a menu item to the supplied menu with an action identified by the supplied actionName.
	 * 
	 * @param menu
	 *            the menu to add the new item to.
	 * @param actionName
	 *            the name of the action, or "-" to add a separator.
	 */
	private void addMenu(JMenu menu, String actionName) {
		if ("-".equals(actionName)) {
			menu.addSeparator();
		} else {
			JMenuItem menuItem = new JMenuItem();
			menuItem.setAction(_actionMap.get(actionName));
			menu.add(menuItem);
		}
	}

	private File _lastDirectory = null;

	private File selectFile(boolean open) {
		File selectedFile = null;
		JFileChooser chooser = new JFileChooser();

		if (_lastDirectory != null) {
			chooser.setCurrentDirectory(_lastDirectory);
		}

		chooser.setFileFilter(new FileNameExtensionFilter("Delta Editor files *.dlt", "dlt"));
		int dialogResult;
		if (open) {
			dialogResult = chooser.showOpenDialog(getMainFrame());
		} else {
			dialogResult = chooser.showSaveDialog(getMainFrame());
		}
		if (dialogResult == JFileChooser.APPROVE_OPTION) {
			selectedFile = chooser.getSelectedFile();
			_lastDirectory = chooser.getCurrentDirectory();
		}
		return selectedFile;
	}

	private void newMatrix(EditorDataModel dataSet) {
		
		MatrixViewer matrixViewer = new MatrixViewer(dataSet);
		newView(matrixViewer, dataSet, HelpConstants.GRID_VIEW_HELP_KEY);
	}

	private void newTree(EditorDataModel dataSet) {
		TreeViewer treeViewer = new TreeViewer(dataSet);
		newView(treeViewer, dataSet, HelpConstants.TREE_VIEW_HELP_KEY);
	}
	
	private void newView(JInternalFrame view, EditorDataModel dataSet, String helpKey) {
		getMainFrame().setTitle(String.format(windowTitleWithFilename, dataSet.getName()));
		ViewerFrameListener listener = new ViewerFrameListener(dataSet, DeltaEditor.this);
		view.addInternalFrameListener(listener);
		view.addVetoableChangeListener(listener);
		
		_helpController.setHelpKeyForComponent(view, helpKey);
		show(view);
		viewerOpened(dataSet, view);
	}

	private void createAboutBox() {
		AboutBox aboutBox = new AboutBox(getMainFrame());
		show(aboutBox);
	}

	
	abstract class ProgressObservingTask<T, V> extends Task<T, V> implements IProgressObserver {

		public ProgressObservingTask(Application app) {
			super(app);
		}

		@Override
		public void progress(String message, int percentComplete) {
			// message(message);
			setProgress(percentComplete);
		}

	}

	/**
	 * Loads a Delta file and creates a new tree view when it finishes.
	 */
	class DeltaFileLoader extends ProgressObservingTask<AbstractObservableDataSet, Void> {

		/** The file to load */
		private File _deltaFile;

		/**
		 * Creates a DeltaFileLoader for the specified application that will load the supplied DELTA file.
		 * 
		 * @param app
		 *            the application this task is a part of.
		 * @param deltaFile
		 *            the file to load.
		 */
		public DeltaFileLoader(Application app, File deltaFile) {
			super(app);
			_deltaFile = deltaFile;

		}

		@Override
		protected AbstractObservableDataSet doInBackground() throws Exception {
			message("loading", _deltaFile.getAbsolutePath());
			return (AbstractObservableDataSet)_dataSetRepository.findByName(_deltaFile.getAbsolutePath(), this);
		}

		@Override
		protected void succeeded(AbstractObservableDataSet result) {
			EditorDataModel model = new EditorDataModel(result);
			EditorPreferences.addFileToMRU(_deltaFile.getAbsolutePath());
			buildFileMenu(_fileMenu);
			newTree(model);
			_saveEnabled = true;
		}

		/**
		 * Shows an error dialog with the message from the supplied Throwable.
		 */
		@Override
		protected void failed(Throwable cause) {
			JOptionPane.showMessageDialog(getMainFrame(), cause.getMessage(), getTitle(), JOptionPane.ERROR_MESSAGE);
		}

		@Override
		protected void finished() {
			setMessage("");
		}
	}

	

	void viewerOpened(EditorDataModel model, JInternalFrame view) {
		AbstractObservableDataSet dataSet = model.getCurrentDataSet();
		List<JInternalFrame> views = _activeViews.get(dataSet);
		if (views == null) {
			views = new ArrayList<JInternalFrame>();
			_activeViews.put(dataSet, views);
		}
		views.add(view);
		numViewersOpen++;
	}

	boolean viewerClosing(EditorDataModel model, JInternalFrame view) {
		AbstractObservableDataSet dataSet = model.getCurrentDataSet();
		List<JInternalFrame> views = _activeViews.get(dataSet);
		boolean canClose = true;
		// If we are about to close the last view of a data set, check if it needs to be saved.
		if ((views.size() == 1 && !_closingFromMenu)) {
			canClose = confirmClose(model.getCurrentDataSet());
		}
		if (canClose) {
			views.remove(view);
			if (views.isEmpty()) {
				_activeViews.remove(dataSet);
			}
			numViewersOpen--;
			if (numViewersOpen == 0) {
				getMainFrame().setTitle(windowTitleWithoutFilename);
				setSaveAsEnabled(false);
				setSaveEnabled(false);
				_selectedDataSet = null;
			}
		}
		return canClose;
	}

	/**
	 * Asks the user whether they wish to save before closing.  If this method returns false
	 * the close will be aborted.
	 * @param model the model to be closed.
	 * @return true if the close can proceed.
	 */
	private boolean confirmClose(DeltaDataSet dataSet) {
		boolean canClose = true;
		if (dataSet.isModified()) {
			String title = dataSet.getName();
			if (title != null) {
				title = new File(title).getName();
			}
			else {
				title = newDataSetName;
			}
			int result = MessageDialogHelper.showConfirmDialog(this.getMainFrame(), title, closeWithoutSavingMessage, 20);
			canClose = (result != JOptionPane.CANCEL_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				saveFile();
			}
		}
		return canClose;
	}

	void viewerFocusGained(EditorDataModel dataSet) {
		getMainFrame().setTitle(String.format(windowTitleWithFilename, dataSet.getName()));
		setSaveEnabled(true);
		setSaveAsEnabled(true);
		_selectedDataSet = dataSet;
	}

	@Action(block = BlockingScope.APPLICATION)
	public Task<AbstractObservableDataSet, Void> loadFile() {

		Task<AbstractObservableDataSet, Void> fileOpenTask = null;
		File toOpen = selectFile(true);
		if (toOpen != null) {
			fileOpenTask = new DeltaFileLoader(this, toOpen);
			fileOpenTask.addPropertyChangeListener(_statusBar);
		}
		return fileOpenTask;
	}

	@Action(enabledProperty = "saveEnabled")
	public void saveFile() {
		EditorDataModel model = getCurrentDataSet();
		
		if (model != null) {
			if (StringUtils.isEmpty(model.getName())) {
				saveAsFile();
			}
			else {
				_dataSetRepository.save(model.getCurrentDataSet(), null);
			}
		}
	}

	@Action(enabledProperty = "saveAsEnabled")
	public void saveAsFile() {

		File newFile = selectFile(false);
		if (newFile != null) {
			EditorDataModel model = getCurrentDataSet();
			if (model != null) {
				_dataSetRepository.saveAsName(model.getCurrentDataSet(), newFile.getAbsolutePath(), null);
				model.setName(newFile.getAbsolutePath());
				EditorPreferences.addFileToMRU(newFile.getAbsolutePath());
				buildFileMenu(_fileMenu);
				// Force a refresh of the main application title.
				viewerFocusGained(model);
			}
		}
	}

	@Action
	public void exitApplication() {
		exit();
	}

	@Action(enabledProperty = "saveAsEnabled")
	public void newGridView() {
		EditorDataModel model = new EditorDataModel(getCurrentDataSet().getCurrentDataSet());
		if (model != null) {
			newMatrix(model);
		}
	}

	@Action(enabledProperty = "saveAsEnabled")
	public void newTreeView() {
		EditorDataModel model = new EditorDataModel(getCurrentDataSet().getCurrentDataSet());
		if (model != null) {
			newTree(model);
		}
	}

	@Action
	public void tileFrames() {
		tileFramesInDesktopPane();
	}

	@Action
	public void openAbout() {
		createAboutBox();
	}

	@Action(enabledProperty = "enabled")
	public void viewCharacterEditor() {
	}

	@Action(enabledProperty = "saveAsEnabled")
	public void viewTaxonEditor() {
		ItemEditor editor = new ItemEditor();
		editor.bind(getCurrentDataSet());
		show(editor);
	}

	@Action(enabledProperty = "enabled")
	public void viewActionSets() {
	}

	@Action(enabledProperty = "enabled")
	public void viewImageSettings() {
	}
	
	@Action(enabledProperty = "saveAsEnabled")
	public void importDirectives() {
		new ImportController(this).begin();
		
	}
	
	@Action(enabledProperty = "saveAsEnabled")
	public void exportDirectives() {
		
	}
	
	@Action
	public void newFile() {
		EditorDataModel model = new EditorDataModel((AbstractObservableDataSet)_dataSetRepository.newDataSet());
		newTree(model);
	}
	
	@Action(enabledProperty = "saveAsEnabled") 
	public void closeFile() {
		try {
			_closingFromMenu = true;
			if (confirmClose(getCurrentDataSet()) == true) {
				AbstractObservableDataSet dataSet = getCurrentDataSet().getCurrentDataSet();
				closeAll(dataSet);
			}
		}
		finally {
			_closingFromMenu = false;
		}
		
	}

	/**
	 * @param dataSet
	 */
	private void closeAll(AbstractObservableDataSet dataSet) {
		List<JInternalFrame> views = _activeViews.get(dataSet);
		// Close in reverse order as the event handlers for close actually remove the
		// value from the list.
		for (int i=views.size()-1; i>=0; i--) {
			try {
				views.get(i).setClosed(true);
			} catch (PropertyVetoException e) {
			}
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void setSaveEnabled(boolean saveEnabled) {
		boolean oldSaveEnabled = _saveEnabled;
		_saveEnabled = saveEnabled;
		_propertyChangeSupport.firePropertyChange("saveEnabled", oldSaveEnabled, _saveEnabled);
	}

	public boolean isSaveEnabled() {
		return _saveEnabled;
	}

	public void setSaveAsEnabled(boolean saveEnabled) {
		boolean oldSaveAsEnabled = _saveAsEnabled;
		_saveAsEnabled = saveEnabled;
		_propertyChangeSupport.firePropertyChange("saveAsEnabled", oldSaveAsEnabled, _saveAsEnabled);
	}

	public boolean isSaveAsEnabled() {
		return _saveAsEnabled;
	}

}

class LookAndFeelAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private LookAndFeel _laf;
	private Frame _frame;

	public LookAndFeelAction(Frame frame, LookAndFeel laf) {
		super(laf.getName());
		_laf = laf;
		_frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			UIManager.setLookAndFeel(_laf);
			SwingUtilities.updateComponentTreeUI(_frame);
		} catch (Exception ex) {
			System.err.println(ex);
		}

	}

}

/**
 * Used to alert the DeltaEditor when grid and matrix views are closed or brought into focus.
 * 
 * @author Chris
 * 
 */
class ViewerFrameListener implements InternalFrameListener, VetoableChangeListener {

	EditorDataModel _dataSet;
	DeltaEditor _deltaEditor;

	/**
	 * ctor
	 * 
	 * @param dataSet
	 *            The data set associated with the viewer
	 * @param deltaEditor
	 *            Reference to the instance of DeltaEditor that created the viewer
	 */
	public ViewerFrameListener(EditorDataModel dataSet, DeltaEditor deltaEditor) {
		_dataSet = dataSet;
		_deltaEditor = deltaEditor;
	}

	public void internalFrameOpened(InternalFrameEvent e) {
	}

	public void internalFrameClosing(InternalFrameEvent e) {
	}

	public void internalFrameClosed(InternalFrameEvent e) {
		
	}

	public void internalFrameIconified(InternalFrameEvent e) {
	}

	public void internalFrameDeiconified(InternalFrameEvent e) {
	}

	public void internalFrameActivated(InternalFrameEvent e) {
		_deltaEditor.viewerFocusGained(_dataSet);
	}

	public void internalFrameDeactivated(InternalFrameEvent e) {
	}

	@Override
	public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {

		if (JInternalFrame.IS_CLOSED_PROPERTY.equals(e.getPropertyName()) && (e.getNewValue().equals(Boolean.TRUE))) {
			boolean canClose = _deltaEditor.viewerClosing(_dataSet, (JInternalFrame)e.getSource());
			if (!canClose) {
				throw new PropertyVetoException("Close cancelled", e);
			}
		}
	}
}

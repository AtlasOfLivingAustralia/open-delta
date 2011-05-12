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
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ProxyActions;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;

import au.org.ala.delta.editor.directives.ImportController;
import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.editor.support.InternalFrameApplication;
import au.org.ala.delta.editor.ui.CharacterEditor;
import au.org.ala.delta.editor.ui.StatusBar;
import au.org.ala.delta.editor.ui.help.HelpConstants;
import au.org.ala.delta.editor.ui.util.MenuBuilder;
import au.org.ala.delta.model.AbstractObservableDataSet;
import au.org.ala.delta.model.DeltaDataSetRepository;
import au.org.ala.delta.ui.AboutBox;
import au.org.ala.delta.ui.help.HelpController;
import au.org.ala.delta.ui.util.IconHelper;
import au.org.ala.delta.util.IProgressObserver;

/**
 * The main class for the DELTA Editor.
 */
@ProxyActions("copyAll")
public class DeltaEditor extends InternalFrameApplication implements
		PreferenceChangeListener, DeltaViewStatusObserver, PropertyChangeListener {

	private static final String DELTA_FILE_EXTENSION = "dlt";

	/** Helper class for notifying listeners of property changes */
	private PropertyChangeSupport _propertyChangeSupport;

	private static final long serialVersionUID = 1L;

	private StatusBar _statusBar;

	private ActionMap _actionMap;

	/** Used to create/find/save data sets */
	private DeltaDataSetRepository _dataSetRepository;
	
	private boolean _saveEnabled;
	private boolean _saveAsEnabled;
	
	/**
	 * There is one DeltaViewController for each open DeltaDataSet.  Each controller
	 * is responsible for one or more DeltaViews.
	 */
	private List<DeltaViewController> _controllers;
	
	/** The DeltaViewController responsible for the currently selected/focused DeltaView */
	private DeltaViewController _activeController;
	
	private HelpController _helpController;

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

	public static void main(String[] args) {
		setupMacSystemProperties(DeltaEditor.class);
		launch(DeltaEditor.class, args);
	}

	@Override
	protected void initialize(String[] args) {
		ResourceMap resourceMap = getContext()
				.getResourceMap(DeltaEditor.class);
		resourceMap.injectFields(this);
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

		_controllers = new ArrayList<DeltaViewController>();
		_saveEnabled = false;
		_saveAsEnabled = false;
		
		_propertyChangeSupport = new PropertyChangeSupport(this);
		
		_actionMap = getContext().getActionMap(this);

		JFrame frame = getMainFrame();
		frame.setPreferredSize(new Dimension(800, 600));

		frame.setIconImages(IconHelper.getBlueIconList());

		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		addExitListener(new ExitListener() {

			@Override
			public void willExit(EventObject event) {
			}

			@Override
			public boolean canExit(EventObject event) {
			
				boolean canClose = closeAll();
				
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

		JOptionPane.showConfirmDialog(getMainFrame(), warning, warningTitle,
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
		super.ready();
	}

	public EditorDataModel getCurrentDataSet() {
		return _activeController.getModel();
	}

	private JMenuBar buildMenus() {

		JMenuBar menuBar = new JMenuBar();

		// File menu. This on is kind of special, in that it gets rebuilt each
		// time a file is opened.
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

		JMenuItem mnuItMetalLF = new JMenuItem(new LookAndFeelAction(
				getMainFrame(), new MetalLookAndFeel()));
		mnuItMetalLF.setName("mnuItMetalLF");
		mnuLF.add(mnuItMetalLF);

		try {
			Class<?> c = Class.forName(UIManager
					.getSystemLookAndFeelClassName());
			LookAndFeel sysLaf = (LookAndFeel) c.newInstance();
			JMenuItem mnuItWindowsLF = new JMenuItem(new LookAndFeelAction(
					getMainFrame(), sysLaf));
			mnuItWindowsLF.setName("mnuItWindowsLF");
			mnuLF.add(mnuItWindowsLF);
		} catch (Exception ex) {
			// do nothing
		}
		try {
			// Nimbus L&F was added in update java 6 update 10.
			LookAndFeel nimbusLaF = (LookAndFeel) Class.forName(
					"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel")
					.newInstance();
			JMenuItem mnuItNimbusLF = new JMenuItem(new LookAndFeelAction(
					getMainFrame(), nimbusLaF));
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

		JMenuItem mnuItHelpOnSelection = new JMenuItem(
				IconHelper.createImageIcon("help_cursor.png"));
		mnuItHelpOnSelection.setName("mnuItHelpOnSelection");

		mnuItHelpOnSelection.addActionListener(_helpController
				.helpOnSelectionAction());
		mnuHelp.add(mnuItHelpOnSelection);

		javax.swing.Action openAboutAction = _actionMap.get("openAbout");

		if (isMac()) {
			configureMacAboutBox(openAboutAction);
		} else {
			JMenuItem mnuItAbout = new JMenuItem();
			mnuItAbout.setAction(openAboutAction);
			mnuHelp.addSeparator();
			mnuHelp.add(mnuItAbout);
		}
		menuBar.add(mnuHelp);

		return menuBar;
	}

	private void buildFileMenu(JMenu mnuFile) {

		mnuFile.removeAll();

		String[] fileMenuActions = { "newFile", "loadFile", "closeFile", "-",
				"saveFile", "saveAsFile", "-", "importDirectives",
				"exportDirectives" };

		MenuBuilder.buildMenu(mnuFile, fileMenuActions, _actionMap);

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
			}
		}

		if (!isMac()) {
			mnuFile.addSeparator();
			JMenuItem mnuItFileExit = new JMenuItem();
			mnuItFileExit.setAction(_actionMap.get("exitApplication"));
			mnuFile.add(mnuItFileExit);
		}

	}

	/**
	 * Loads a previously loaded delta file from the Most Recently Used list. It
	 * is assumed that the source ActionEvent as set the filename in a client
	 * property called "Filename".
	 * 
	 * @param e
	 *            The action event that triggered this action
	 * @return A DeltaFileLoader task
	 */
	@Action(block = BlockingScope.APPLICATION)
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

		String[] editMenuActions = { "copy", "paste", "-", "copyAll" };

		MenuBuilder.buildMenu(mnuEdit, editMenuActions, _actionMap);

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

		String[] viewMenuActions = { "newTreeView", "newGridView", "-",
				"viewCharacterEditor", "viewTaxonEditor", "-",
				"viewActionSets", "viewImageSettings" };

		MenuBuilder.buildMenu(mnuView, viewMenuActions, _actionMap);

		return mnuView;
	}

	private File _lastDirectory = null;

	public File selectFile(boolean open) {
		File selectedFile = null;
		JFileChooser chooser = new JFileChooser();

		if (_lastDirectory != null) {
			chooser.setCurrentDirectory(_lastDirectory);
		}

		chooser.setFileFilter(new FileNameExtensionFilter(
				"Delta Editor files *.dlt", DELTA_FILE_EXTENSION));
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

	private void newMatrix() {

		DeltaView matrixViewer = _activeController.createGridView();
		newView(matrixViewer, HelpConstants.GRID_VIEW_HELP_KEY);
	}

	private void newTree() {
		DeltaView treeViewer = _activeController.createTreeView();
		newView(treeViewer, HelpConstants.TREE_VIEW_HELP_KEY);
	}

	private void newView(DeltaView view, String helpKey) {
		
		_helpController.setHelpKeyForComponent((JComponent)view, helpKey);
		// TODO need to remove this dependency on JInternalFrame....
		show((JInternalFrame)view);
		
		updateTitle();
	}

	private void createAboutBox() {
		AboutBox aboutBox = new AboutBox(getMainFrame());
		show(aboutBox);
	}

	abstract class ProgressObservingTask<T, V> extends Task<T, V> implements
			IProgressObserver {

		public ProgressObservingTask(Application app) {
			super(app);
		}

		@Override
		public void progress(String message, int percentComplete) {
			setProgress(percentComplete);
		}

	}

	/**
	 * Loads a Delta file and creates a new tree view when it finishes.
	 */
	class DeltaFileLoader extends
			ProgressObservingTask<AbstractObservableDataSet, Void> {

		/** The file to load */
		private File _deltaFile;

		/**
		 * Creates a DeltaFileLoader for the specified application that will
		 * load the supplied DELTA file.
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
			return (AbstractObservableDataSet) _dataSetRepository.findByName(
					_deltaFile.getAbsolutePath(), this);
		}

		@Override
		protected void succeeded(AbstractObservableDataSet result) {
			
			EditorPreferences.addFileToMRU(_deltaFile.getAbsolutePath());
			_activeController = createController(result);
			newTree();
		}

		/**
		 * Shows an error dialog with the message from the supplied Throwable.
		 */
		@Override
		protected void failed(Throwable cause) {
			JOptionPane.showMessageDialog(getMainFrame(), cause.getMessage(),
					getTitle(), JOptionPane.ERROR_MESSAGE);
		}

		@Override
		protected void finished() {
			setMessage("");
		}
	}

	/**
	 * Creates a controller to manage the supplied data set.
	 * @param dataSet the data set that requires a controller.
	 * @return a controller for the supplied dataset.
	 */
	private DeltaViewController createController(AbstractObservableDataSet dataSet) {
		EditorDataModel model = new EditorDataModel(dataSet);
		model.addPropertyChangeListener(this);
		DeltaViewController controller = new DeltaViewController(model, DeltaEditor.this, _dataSetRepository);
		controller.setNewDataSetName(newDataSetName);
		controller.setCloseWithoutSavingMessage(closeWithoutSavingMessage);
		controller.addDeltaViewStatusObserver(this);
		_controllers.add(controller);
		return controller;
	}

	public void viewClosed(DeltaViewController controller, DeltaView view) {
		
		if (controller.getViewCount() == 0) {
		
			_controllers.remove(controller);
			
			if (_controllers.isEmpty()) {
				getMainFrame().setTitle(windowTitleWithoutFilename);
				setSaveAsEnabled(false);
				setSaveEnabled(false);
				_activeController = null;
			}
		}
	}

	
	public void viewSelected(DeltaViewController controller, DeltaView view) {
		_activeController = controller;
		updateTitle();
		setSaveEnabled(controller.getModel().isModified());
		setSaveAsEnabled(true);
	}
	
	private void updateTitle() {
		String dataSetName = getCurrentDataSet().getName();
		getMainFrame().setTitle(String.format(windowTitleWithFilename,dataSetName));
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
		_activeController.save();
	}

	@Action(enabledProperty = "saveAsEnabled")
	public void saveAsFile() {
		_activeController.saveAs();
		
		updateTitle();
	}

	@Action
	public void exitApplication() {
		exit();
	}

	@Action(enabledProperty = "saveAsEnabled")
	public void newGridView() {
		
		if (_activeController != null) {
			newMatrix();
		}
	}

	@Action(enabledProperty = "saveAsEnabled")
	public void newTreeView() {

		if (_activeController != null) {
			newTree();
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

	@Action(enabledProperty = "saveAsEnabled")
	public void viewCharacterEditor() {
		CharacterEditor editor = new CharacterEditor(this.getMainFrame());
		editor.bind(getCurrentDataSet());
		show(editor);
	}

	@Action(enabledProperty = "saveAsEnabled")
	public void viewTaxonEditor() {
		DeltaView editor = _activeController.createItemEditView();
		newView(editor, "T");
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
		
		AbstractObservableDataSet dataSet = (AbstractObservableDataSet) _dataSetRepository.newDataSet();
		_activeController = createController(dataSet);
		
		newTree();
	}

	@Action(enabledProperty = "saveAsEnabled")
	public void closeFile() {
		_activeController.closeAll();
	}

	/**
	 * @param dataSet
	 */
	private boolean closeAll() {
		for (DeltaViewController controller : _controllers) {
			if (!controller.closeAll()) {
				return false;
			}
		}
		return true;
	}
	
	

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (_activeController == null) {
			return;
		}
		if (evt.getSource() == _activeController.getModel()) {
			if ("modified".equals(evt.getPropertyName())) {
				setSaveEnabled((Boolean)evt.getNewValue());
			}
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void setSaveEnabled(boolean saveEnabled) {
		boolean oldSaveEnabled = _saveEnabled;
		_saveEnabled = saveEnabled;
		_propertyChangeSupport.firePropertyChange("saveEnabled",
				oldSaveEnabled, _saveEnabled);
	}

	public boolean isSaveEnabled() {
		return _saveEnabled;
	}

	public void setSaveAsEnabled(boolean saveEnabled) {
		boolean oldSaveAsEnabled = _saveAsEnabled;
		_saveAsEnabled = saveEnabled;
		_propertyChangeSupport.firePropertyChange("saveAsEnabled",
				oldSaveAsEnabled, _saveAsEnabled);
		if ((saveEnabled) && isMac()) {
			getMainFrame().getRootPane().putClientProperty("Window.documentModified", saveEnabled);
		}
	}

	public boolean isSaveAsEnabled() {
		return _saveAsEnabled;
	}

	/**
	 * Updates the file menu when a value is added to the most recently used
	 * list.
	 */
	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (EditorPreferences.MRU_PREF_KEY.equals(evt.getKey())) {
			buildFileMenu(_fileMenu);
		}
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

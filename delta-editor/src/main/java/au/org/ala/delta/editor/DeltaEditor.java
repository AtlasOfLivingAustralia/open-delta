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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.ActionMap;
import javax.swing.JCheckBoxMenuItem;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ProxyActions;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;

import au.org.ala.delta.editor.directives.DirectiveFileInfo;
import au.org.ala.delta.editor.directives.ExportController;
import au.org.ala.delta.editor.directives.ImportController;
import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.editor.support.InternalFrameApplication;
import au.org.ala.delta.editor.ui.ActionSetsDialog;
import au.org.ala.delta.editor.ui.StatusBar;
import au.org.ala.delta.editor.ui.help.HelpConstants;
import au.org.ala.delta.editor.ui.image.ImageSettingsDialog;
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
@ProxyActions({"copySelectedWithHeaders", "selectAll"})
public class DeltaEditor extends InternalFrameApplication implements
		PreferenceChangeListener, DeltaViewStatusObserver, PropertyChangeListener {

	private static final String DELTA_FILE_EXTENSION = "dlt";

	/** Helper class for notifying listeners of property changes */
	private PropertyChangeSupport _propertyChangeSupport;

	private static final long serialVersionUID = 1L;

	private StatusBar _statusBar;

	private ActionMap _actionMap;
	private ResourceMap _resourceMap;

	/** Used to create/find/save data sets */
	private DeltaDataSetRepository _dataSetRepository;
	
	private boolean _saveEnabled;
	private boolean _saveAsEnabled;
	
	/** Flag to prevent concurrent modification exception on close all */
	private boolean _closingAll;
	
	/**
	 * There is one DeltaViewController for each open DeltaDataSet.  Each controller
	 * is responsible for one or more DeltaViews.
	 */
	private List<DeltaViewController> _controllers;
	
	/** The DeltaViewController responsible for the currently selected/focused DeltaView */
	private DeltaViewController _activeController;
	
	private HelpController _helpController;

	private JMenu _fileMenu;
	private JMenu _windowMenu;
	
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
	
	public DeltaEditor() {
		_controllers = new ArrayList<DeltaViewController>();
		_saveEnabled = false;
		_saveAsEnabled = false;
		_closingAll = false;
		
		_propertyChangeSupport = new PropertyChangeSupport(this);
		
	}

	@Override
	protected void initialize(String[] args) {
		_resourceMap = getContext() .getResourceMap(DeltaEditor.class);
		_resourceMap.injectFields(this);
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

		EditorPreferences.addPreferencesChangeListener(this);
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

		// Window menu
		_windowMenu = new JMenu();
		_windowMenu.setName("mnuWindow");
		buildWindowMenu(_windowMenu);
		menuBar.add(_windowMenu);

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

	private void buildWindowMenu(JMenu mnuWindow) {
		mnuWindow.removeAll();
		
		JMenuItem mnuItTile = new JMenuItem();
		mnuItTile.setAction(_actionMap.get("tileFrames"));
		mnuWindow.add(mnuItTile);

		mnuWindow.addSeparator();

		JMenu mnuLF = new JMenu();
		mnuLF.setName("mnuLF");
		mnuLF.setText(_resourceMap.getString("mnuLF.text"));
		mnuWindow.add(mnuLF);

		JMenuItem mnuItMetalLF = new JMenuItem();
		mnuItMetalLF.setAction(_actionMap.get("metalLookAndFeel"));
		mnuLF.add(mnuItMetalLF);

		
		JMenuItem mnuItWindowsLF = new JMenuItem();
		mnuItWindowsLF.setAction(_actionMap.get("systemLookAndFeel"));
		mnuLF.add(mnuItWindowsLF);
		
		try {
			// Nimbus L&F was added in update java 6 update 10.
			Class.forName( "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel").newInstance();
			JMenuItem mnuItNimbusLF = new JMenuItem();
			mnuItNimbusLF.setAction(_actionMap.get("nimbusLookAndFeel"));
			mnuLF.add(mnuItNimbusLF);
		} catch (Exception e) {
			// The Nimbus L&F is not available, no matter.
		}
		mnuWindow.addSeparator();
		
		for (final JInternalFrame frame : _frames) {
			JMenuItem windowItem = new JCheckBoxMenuItem();
			windowItem.setText(frame.getTitle());
			windowItem.setSelected(frame.isSelected());
			windowItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						frame.setSelected(true);
					} catch (PropertyVetoException e1) {
					}
				}
			});
			mnuWindow.add(windowItem);
		}
		
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
	@Action
	public void systemLookAndFeel() {
		try {
			Class<?> c = Class.forName(UIManager
					.getSystemLookAndFeelClassName());
			LookAndFeel sysLaf = (LookAndFeel) c.newInstance();
			changeLookAndFeel(sysLaf);
		}
		catch (Exception e) {}
	}
	
	@Action
	public void metalLookAndFeel() {
		changeLookAndFeel(new MetalLookAndFeel());
	}
	
	@Action
	public void nimbusLookAndFeel() {
		// Nimbus L&F was added in update java 6 update 10.
		LookAndFeel nimbusLaF;
		try {
			nimbusLaF = (LookAndFeel) Class.forName(
					"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel")
					.newInstance();
			changeLookAndFeel(nimbusLaF);
		} catch (Exception e) {} 
	}
	
	private void changeLookAndFeel(LookAndFeel laf) {
		try {
			UIManager.setLookAndFeel(laf);
			SwingUtilities.updateComponentTreeUI(getMainFrame());
		} catch (Exception ex) {
			System.err.println(ex);
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
			String filename = (String) item.getClientProperty("Filename");
			File toOpen = new File(filename);
			if (toOpen != null && toOpen.exists()) {
				fileOpenTask = new DeltaFileLoader(this, toOpen);
				fileOpenTask.addPropertyChangeListener(_statusBar);
			} else {
				JOptionPane.showMessageDialog(getMainFrame(), "File not found or not readable!", "File open failed", JOptionPane.ERROR_MESSAGE);								
				item.getParent().remove(item);
				EditorPreferences.removeFileFromMRU(filename);
			}
		}
		return fileOpenTask;
	}

	private JMenu buildEditMenu() {
		JMenu mnuEdit = new JMenu();
		mnuEdit.setName("mnuEdit");

		String[] editMenuActions = { "copy", "paste", "-", "selectAll", "-", "copySelectedWithHeaders" };

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
		AboutBox aboutBox = new AboutBox(getMainFrame(), IconHelper.createBlue32ImageIcon());
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

	/**
	 * Called when any view is closed.  Does tidy up if there are no remaining views.
	 */
	public void viewClosed(DeltaViewController controller, DeltaView view) {
		if (!_closingAll) {
			if (controller.getViewCount() == 0) {
				_controllers.remove(controller);
				
				if (_controllers.isEmpty()) {
					_activeController = null;
					setSaveAsEnabled(false);
					setSaveEnabled(false);
					
				}
			}
		}
	}

	/**
	 * Called when a view is selected.  Updates the title and the state of the save/save as menus.
	 */
	public void viewSelected(DeltaViewController controller, DeltaView view) {
		_activeController = controller;
		updateTitle();
		setSaveEnabled(getCurrentDataSet().isModified());
		setSaveAsEnabled(true);
	}
	
	/**
	 * Updates the main window title with the name of the data set displayed by the currently
	 * selected view.
	 */
	private void updateTitle() {
		
		if (_activeController == null) {
			if (isMac()) {
				getMainFrame().getRootPane().putClientProperty("Window.documentModified", Boolean.FALSE);
			}
			getMainFrame().setTitle(windowTitleWithoutFilename);
			return;
		}
		String dataSetName = getCurrentDataSet().getName();
		String title = String.format(windowTitleWithFilename,dataSetName);
		
		boolean modified = getCurrentDataSet().isModified();
		if (modified) {
			if (isMac()) {
				getMainFrame().getRootPane().putClientProperty("Window.documentModified", Boolean.TRUE);
			}
			else {
				title = title + "*";
			}
		} else  {
			if (isMac()) {
				getMainFrame().getRootPane().putClientProperty("Window.documentModified", Boolean.FALSE);
			}
		}
		getMainFrame().setTitle(title);
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
		
		DeltaView editor = _activeController.createCharacterEditView();
		newView(editor, "C");
	}

	@Action(enabledProperty = "saveAsEnabled")
	public void viewTaxonEditor() {
		DeltaView editor = _activeController.createItemEditView();
		newView(editor, "T");
	}

	@Action(enabledProperty = "saveAsEnabled")
	public void viewActionSets() {
		ActionSetsDialog dialog = new ActionSetsDialog(getMainFrame(), getCurrentDataSet());
		show(dialog);
	}

	@Action(enabledProperty = "saveAsEnabled")
	public void viewImageSettings() {
		ImageSettingsDialog dialog = new ImageSettingsDialog(getMainFrame(), getCurrentDataSet(), getCurrentDataSet().getImageSettings());
		show(dialog);
	}

	@Action
	public void viewImageEditor() {
		DeltaView editor = _activeController.createImageEditorView();
		newView(editor, "I");
	}
	
	@Action
	public void viewDirectivesEditor() {
		DeltaView editor = _activeController.createDirectivesEditorView();
		newView(editor, "");
	}
	
	
	@Action(enabledProperty = "saveAsEnabled")
	public void importDirectives() {
		new ImportController(this, getCurrentDataSet()).begin();

	}

	@Action(enabledProperty = "saveAsEnabled")
	public void exportDirectives() {
		new ExportController(this).begin();
	}

	@Action
	public void newFile() {
		
		AbstractObservableDataSet dataSet = (AbstractObservableDataSet) _dataSetRepository.newDataSet();
		
		_activeController = createController(dataSet);
		
		initialiseNewDataSet(_activeController.getModel());
		
		newTree();
	}
	
	/**
	 * Populates the VOP with the set of template directives files that are distributed
	 * with the DELTA suite.  These templates take the form of _<type>_<filename> where type
	 * can be one of "c" (confor), "i" (intkey), "k" (key) or "d" (dist).
	 */
	private void initialiseNewDataSet(EditorDataModel dataSet) {
		
		File workingDirectory = new File(".");
		File[] templateFiles = workingDirectory.listFiles();
		List<DirectiveFileInfo> toImport = new ArrayList<DirectiveFileInfo>();
		for (File file : templateFiles) {
			String fileName = file.getName();
			if (fileName.length() <= 3) {
				continue;
			}
			DirectiveType type = null;
			if (fileName.startsWith("_")) {
				switch (fileName.charAt(1)) {
				case 'c':
					type = DirectiveType.CONFOR;
					break;
				case 'i':
					type = DirectiveType.INTKEY;
					break;
				case 'k':
					type = DirectiveType.KEY;
					break;
				case 'd':
					type = DirectiveType.DIST;
					break;
				default:
					continue;
				}
				
				String name = fileName.substring(3);
				toImport.add(new DirectiveFileInfo(name, fileName, type));
			}
		}
		
		ImportController controller = new ImportController(this, dataSet);
		controller.doSilentImport(workingDirectory, toImport);
	}
		

	@Action(enabledProperty = "saveAsEnabled")
	public void closeFile() {
		_activeController.closeAll();
	}

	/**
	 * @param dataSet
	 */
	private boolean closeAll() {
		_closingAll = true;
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
		updateTitle();
		
	}

	public boolean isSaveEnabled() {
		return _saveEnabled;
	}

	public void setSaveAsEnabled(boolean saveEnabled) {
		boolean oldSaveAsEnabled = _saveAsEnabled;
		_saveAsEnabled = saveEnabled;
		_propertyChangeSupport.firePropertyChange("saveAsEnabled",
				oldSaveAsEnabled, _saveAsEnabled);
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

	/**
     * Invoked when an internal frame is activated.
     */
    public void internalFrameActivated(InternalFrameEvent e) {
    	buildWindowMenu(_windowMenu);
    }

	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		buildWindowMenu(_windowMenu);
	}

	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		buildWindowMenu(_windowMenu);
	}
	
}


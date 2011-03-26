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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;

import au.org.ala.delta.editor.directives.ImportController;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;
import au.org.ala.delta.editor.ui.EditorDataModel;
import au.org.ala.delta.editor.ui.ItemEditor;
import au.org.ala.delta.editor.ui.MatrixViewer;
import au.org.ala.delta.editor.ui.TreeViewer;
import au.org.ala.delta.editor.ui.help.HelpConstants;
import au.org.ala.delta.editor.ui.util.EditorUIUtils;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.DeltaDataSetRepository;
import au.org.ala.delta.ui.AboutBox;
import au.org.ala.delta.ui.help.HelpController;
import au.org.ala.delta.ui.util.IconHelper;
import au.org.ala.delta.util.IProgressObserver;

/**
 * The main class for the DELTA Editor.
 */
@ProxyActions("copyAll")
public class DeltaEditor extends SingleFrameApplication {

	/** Helper class for notifying listeners of property changes */
	private PropertyChangeSupport _propertyChangeSupport;

	private static final long serialVersionUID = 1L;

	private JDesktopPane _desktop;
	private StatusBar _statusBar;

	private ActionMap _actionMap;

	// Yuk
	private DeltaDataSetRepository _dataSetRepository;

	private boolean _saveEnabled;
	private boolean _saveAsEnabled;

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

	/** Tracks the data set being edited by which internal frame is currently focussed */
	private EditorDataModel _selectedDataSet;

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
		_propertyChangeSupport = new PropertyChangeSupport(this);

		ResourceMap resourceMap = getContext().getResourceMap(AboutBox.class);
		resourceMap.injectFields(this);

		_actionMap = getContext().getActionMap(this);

		JFrame frame = getMainFrame();
		frame.setPreferredSize(new Dimension(800, 600));

		frame.setIconImages(IconHelper.getBlueIconList());

		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		_helpController = new HelpController("help/delta_editor/DeltaEditor");
		_dataSetRepository = new SlotFileRepository();

		_desktop = new JDesktopPane();
		_desktop.setBackground(SystemColor.control);

		_statusBar = new StatusBar();
		getMainView().setStatusBar(_statusBar);

		getMainView().setMenuBar(buildMenus());

		_helpController.enableHelpKey(frame);

		show(_desktop);

	}

	/**
	 * Closes all internal frames to allow their associated data models to be closed.
	 */
	@Override
	protected void shutdown() {
		for (JInternalFrame frame : _desktop.getAllFrames()) {
			frame.dispose();
		}

		super.shutdown();
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
		getMainFrame().setTitle(String.format(windowTitleWithFilename, dataSet.getName()));
		MatrixViewer matrixViewer = new MatrixViewer(dataSet);
		matrixViewer.addInternalFrameListener(new ViewerFrameListener(dataSet, DeltaEditor.this));
		_helpController.setHelpKeyForComponent(matrixViewer, HelpConstants.GRID_VIEW_HELP_KEY);
		addToDesktop(matrixViewer);
		viewerOpened();
	}

	private void newTree(EditorDataModel dataSet) {
		getMainFrame().setTitle(String.format(windowTitleWithFilename, dataSet.getName()));
		TreeViewer treeViewer = new TreeViewer(dataSet);
		treeViewer.addInternalFrameListener(new ViewerFrameListener(dataSet, DeltaEditor.this));
		_helpController.setHelpKeyForComponent(treeViewer, HelpConstants.TREE_VIEW_HELP_KEY);
		addToDesktop(treeViewer);
		viewerOpened();
	}

	private void createAboutBox() {
		AboutBox aboutBox = new AboutBox(getMainFrame());
		show(aboutBox);
	}

	private void addToDesktop(JInternalFrame frame) {
		frame.setFrameIcon(EditorUIUtils.createDeltaImageIcon());
		_desktop.add(frame);
		frame.setClosable(true);
		frame.setMaximizable(true);
		frame.setResizable(true);
		frame.setIconifiable(true);
		frame.setVisible(true);
		frame.setFrameIcon(EditorUIUtils.createInternalFrameNormalIcon());
		frame.addPropertyChangeListener(JInternalFrame.IS_MAXIMUM_PROPERTY, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Boolean newValue = (Boolean) evt.getNewValue();
				JInternalFrame frame = (JInternalFrame) evt.getSource();
				if (newValue) {
					frame.setFrameIcon(EditorUIUtils.createInternalFrameMaximizedIcon());
				} else {
					frame.setFrameIcon(EditorUIUtils.createInternalFrameNormalIcon());
				}
			}
		});

		try {
			frame.setMaximum(false);
		} catch (Exception ex) {
			// ignore
		}
		frame.setSize(new Dimension(800, 500));
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
	class DeltaFileLoader extends ProgressObservingTask<DeltaDataSet, Void> {

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
		protected DeltaDataSet doInBackground() throws Exception {
			message("loading", _deltaFile.getAbsolutePath());
			return _dataSetRepository.findByName(_deltaFile.getAbsolutePath(), this);
		}

		@Override
		protected void succeeded(DeltaDataSet result) {
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

	// This could be turned into a utility method
	private void tileFramesInDesktopPane(JDesktopPane desk) {

		// How many frames do we have?
		JInternalFrame[] allframes = desk.getAllFrames();
		int count = allframes.length;
		if (count == 0)
			return;

		// Determine the necessary grid size
		int sqrt = (int) Math.sqrt(count);
		int rows = sqrt;
		int cols = sqrt;
		if (rows * cols < count) {
			cols++;
			if (rows * cols < count) {
				rows++;
			}
		}

		// Define some initial values for size & location.
		Dimension size = desk.getSize();

		int w = size.width / cols;
		int h = size.height / rows;
		int x = 0;
		int y = 0;

		// Iterate over the frames, deiconifying any iconified frames and then
		// relocating & resizing each.
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
				JInternalFrame f = allframes[(i * cols) + j];

				if (!f.isClosed()) {
					try {
						f.setMaximum(false);
						f.setIcon(false);
					} catch (PropertyVetoException ignored) {
					}
				}

				desk.getDesktopManager().resizeFrame(f, x, y, w, h);
				x += w;
			}
			y += h; // start the next row
			x = 0;
		}
	}

	void viewerOpened() {
		numViewersOpen++;
	}

	void viewerClosed(EditorDataModel dataSet) {
		numViewersOpen--;
		if (numViewersOpen == 0) {
			getMainFrame().setTitle(windowTitleWithoutFilename);
			setSaveAsEnabled(false);
			setSaveEnabled(false);
		}
	}

	void viewerFocusGained(EditorDataModel dataSet) {
		getMainFrame().setTitle(String.format(windowTitleWithFilename, dataSet.getName()));
		setSaveEnabled(true);
		setSaveAsEnabled(true);
		_selectedDataSet = dataSet;
	}

	@Action(block = BlockingScope.APPLICATION)
	public Task<DeltaDataSet, Void> loadFile() {

		Task<DeltaDataSet, Void> fileOpenTask = null;
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
		EditorDataModel model = getCurrentDataSet();
		if (model != null) {
			newMatrix(model);
		}
	}

	@Action(enabledProperty = "saveAsEnabled")
	public void newTreeView() {
		EditorDataModel model = getCurrentDataSet();
		if (model != null) {
			newTree(model);
		}
	}

	@Action
	public void tileFrames() {
		tileFramesInDesktopPane(_desktop);
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
		EditorDataModel model = new EditorDataModel(_dataSetRepository.newDataSet());
		newTree(model);
	}
	
	@Action(enabledProperty = "saveAsEnabled") 
	public void closeFile() {
		
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
class ViewerFrameListener implements InternalFrameListener {

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
		_deltaEditor.viewerClosed(_dataSet);
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

}

class StatusBar extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private JProgressBar _prog;
	private JLabel _label;

	public StatusBar() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(400, 23));
		_prog = new JProgressBar();
		_prog.setPreferredSize(new Dimension(0, 20));
		_prog.setVisible(false);
		_label = new JLabel();
		_prog.setMaximum(100);
		_prog.setMinimum(0);
		add(_prog, BorderLayout.WEST);
		add(_label, BorderLayout.CENTER);
		_prog.setValue(0);
	}

	public void clear() {
		_prog.setValue(0);
		_label.setText("");
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {

			int percentComplete = (Integer) evt.getNewValue();
			int value = Math.min(percentComplete, 100);
			if (value < 0 || value == 100) {
				if (!_prog.isVisible()) {
					_prog.setVisible(true);
					_prog.setPreferredSize(new Dimension(400, 23));
				}
				_prog.setValue(value);
			} else {
				_prog.setVisible(false);
				_prog.setPreferredSize(new Dimension(0, 23));
			}
		} else if ("message".equals(evt.getPropertyName())) {
			_label.setText((String) evt.getNewValue());
		}

	}

}

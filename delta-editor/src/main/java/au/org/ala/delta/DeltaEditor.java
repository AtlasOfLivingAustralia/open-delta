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
package au.org.ala.delta;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.editor.controller.HelpController;
import au.org.ala.delta.gui.EditorDataModel;
import au.org.ala.delta.gui.util.IconHelper;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.DeltaDataSetRepository;
import au.org.ala.delta.slotfile.model.SlotFileRepository;
import au.org.ala.delta.util.IProgressObserver;

public class DeltaEditor extends SingleFrameApplication {

	private static final long serialVersionUID = 1L;

	private JDesktopPane _desktop;
	private StatusBar _statusBar;

	// Yuk
	private DeltaDataSetRepository _dataSetRepository;
	
	private boolean _saveEnabled;
	
	private HelpController _helpController;
	
	@Resource 
	String windowTitleWithoutFilename;
	
	@Resource 
	String windowTitleWithFilename;
	
	
	public static void main(String[] args) {
		launch(DeltaEditor.class, args);
	}
	
	public boolean getSaveEnabled() {
		return _saveEnabled;
	}
	
	@Override
	protected void startup() {
		_saveEnabled = false;
		
		ResourceMap resourceMap = getContext().getResourceMap(AboutBox.class);
		resourceMap.injectFields(this);
	
		JFrame frame = getMainFrame();
		frame.setIconImage(IconHelper.createDeltaImageIcon().getImage());
	
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		_helpController = new HelpController();
		_dataSetRepository = new SlotFileRepository();

		_desktop = new JDesktopPane();
		_desktop.setBackground(SystemColor.control);

		_statusBar = new StatusBar();
		getMainView().setStatusBar(_statusBar);

		getMainView().setMenuBar(buildMenus());
		
		_helpController.enableHelpKey(frame);
		
		show(_desktop);

	}

	private EditorDataModel getCurrentDataSet() {
		EditorDataModel model = null;
		if (_desktop.getSelectedFrame() instanceof IContextHolder) {
			model = ((IContextHolder)_desktop.getSelectedFrame()).getContext();
		}
		return model;
	}
	private JMenuBar buildMenus() {
		ActionMap actionMap = getContext().getActionMap(this);

		JMenuBar menuBar = new JMenuBar();

		JMenu mnuFile = new JMenu();
		mnuFile.setName("mnuFile");

		JMenuItem mnuItFileOpen = new JMenuItem();
		mnuItFileOpen.setAction(actionMap.get("loadFile"));
		
		JMenuItem mnuItFileSave = new JMenuItem();
		mnuItFileSave.setAction(actionMap.get("saveFile"));
		
		JMenuItem mnuItFileSaveAs = new JMenuItem();
		mnuItFileSaveAs.setAction(actionMap.get("saveAsFile"));

		JMenuItem mnuItFileExit = new JMenuItem();
		mnuItFileExit.setAction(actionMap.get("exitApplication"));

		mnuFile.add(mnuItFileOpen);
		mnuFile.addSeparator();
		mnuFile.add(mnuItFileSave);
		mnuFile.add(mnuItFileSaveAs);
		mnuFile.addSeparator();
		mnuFile.add(mnuItFileExit);
		menuBar.add(mnuFile);

		JMenu mnuView = new JMenu();
		mnuView.setName("mnuView");

		JMenuItem mnuItGrid = new JMenuItem();
		mnuItGrid.setAction(actionMap.get("newGridView")); 

		mnuView.add(mnuItGrid);

		JMenuItem mnuItTree = new JMenuItem();
		mnuItTree.setAction(actionMap.get("newTreeView")); 

		mnuView.add(mnuItTree);

		menuBar.add(mnuView);

		JMenu mnuWindow = new JMenu();
		mnuWindow.setName("mnuWindow");
		JMenuItem mnuItTile = new JMenuItem();
		mnuItTile.setAction(actionMap.get("tileFrames"));
		mnuWindow.add(mnuItTile);

		mnuWindow.addSeparator();

		JMenu mnuLF = new JMenu();
		mnuLF.setName("mnuLF");
		mnuWindow.add(mnuLF);
		
		JMenuItem mnuItMetalLF = new JMenuItem(new LookAndFeelAction(getMainFrame(), new MetalLookAndFeel()));
		mnuItMetalLF.setName("mnuItMetalLF");
		mnuLF.add(mnuItMetalLF);
		
		try {
			Class c = Class.forName(UIManager.getSystemLookAndFeelClassName());
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
		}
		catch (Exception e) {
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
		mnuItAbout.setAction(actionMap.get("openAbout"));
		
		mnuHelp.addSeparator();
		mnuHelp.add(mnuItAbout);
		
		menuBar.add(mnuHelp);
		
		return menuBar;
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
		}
		else {
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
		matrixViewer.addFocusListener(new viewerFocusListener(this, dataSet));
		_helpController.setHelpKeyForComponent(matrixViewer, HelpController.GRID_VIEW_HELP_KEY);
		addToDesktop(matrixViewer);
	}

	private void newTree(EditorDataModel dataSet) {
		getMainFrame().setTitle(String.format(windowTitleWithFilename, dataSet.getName()));
		TreeViewer treeViewer = new TreeViewer(dataSet);
		treeViewer.addFocusListener(new viewerFocusListener(this, dataSet));
		_helpController.setHelpKeyForComponent(treeViewer, HelpController.TREE_VIEW_HELP_KEY);
		addToDesktop(treeViewer);
	}
	
	private void createAboutBox() {
		AboutBox aboutBox = new AboutBox(getMainFrame());
		show(aboutBox);
	}
	
	private void addToDesktop(JInternalFrame frame) {
		frame.setFrameIcon(IconHelper.createDeltaImageIcon());
		_desktop.add(frame);		
		frame.setClosable(true);
		frame.setMaximizable(true);
		frame.setResizable(true);
		frame.setIconifiable(true);
		frame.setVisible(true);
		frame.setFrameIcon(IconHelper.createInternalFrameNormalIcon());
		frame.addPropertyChangeListener(JInternalFrame.IS_MAXIMUM_PROPERTY, new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Boolean newValue = (Boolean) evt.getNewValue();
				JInternalFrame frame = (JInternalFrame) evt.getSource();
				if (newValue) {
					frame.setFrameIcon(IconHelper.createInternalFrameMaximizedIcon());
				} else {
					frame.setFrameIcon(IconHelper.createInternalFrameNormalIcon());
				}
			}
		});
		
		try {
			frame.setMaximum(false);
		} catch (Exception ex) {
			// ignore
		}
		frame.setSize(new Dimension(800,500));		
	}

	private void loadFile(final File file) {

		Thread t = new Thread() {

			{
				this.setDaemon(true);
				this.setName("File loader");
			}

			@Override
			public void run() {
				try {
					getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					DeltaDataSet dataSet = _dataSetRepository.findByName(file.getAbsolutePath(), _statusBar);
					EditorDataModel model = new EditorDataModel(dataSet);
					newMatrix(model);
					_saveEnabled = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					getMainFrame().setCursor(Cursor.getDefaultCursor());
					_statusBar.clear();
				}
			}

		};

		t.start();

	}
	
	//This could be turned into a utility method
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
	
	public void viewerFocusLost(EditorDataModel dataSet) {
		getMainFrame().setTitle(String.format(windowTitleWithoutFilename, dataSet.getName()));
	}
	
	public void viewFocusGained(EditorDataModel dataSet) {
		getMainFrame().setTitle(String.format(windowTitleWithFilename, dataSet.getName()));
	}
	
	@Action
	public void loadFile() {
		
		File toOpen = selectFile(true);
		if (toOpen != null) {
			loadFile(toOpen);
		}
	}
	
	@Action(enabledProperty="saveEnabled")
	public void saveFile() {
		EditorDataModel model = getCurrentDataSet();
		if (model != null) {
			_dataSetRepository.save(model.getCurrentDataSet(), null);
		}
	}

	@Action(enabledProperty="saveEnabled")
	public void saveAsFile() {
		
		File newFile = selectFile(false);
		if (newFile != null) {
			EditorDataModel model = getCurrentDataSet();
			if (model != null) {
				_dataSetRepository.saveAsName(model.getCurrentDataSet(), newFile.getAbsolutePath(), null);
			}
		}
	}
	
	@Action
	public void exitApplication() {
		exit();
	}
	
	@Action
	public void newGridView() {
		EditorDataModel model = getCurrentDataSet();
		if (model != null) {
			newMatrix(model);
		}
	}
	
	@Action
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

class viewerFocusListener implements FocusListener {

	DeltaEditor _deltaEditor;
	EditorDataModel _dataSet;
	
	public viewerFocusListener(DeltaEditor deltaEditor, EditorDataModel dataSet) {
		_dataSet = dataSet;
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		_deltaEditor.viewFocusGained(_dataSet);
	}

	@Override
	public void focusLost(FocusEvent e) {
		_deltaEditor.viewerFocusLost(_dataSet);
	}
	
}

class StatusBar extends JPanel implements IProgressObserver {

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
	public void progress(final String message, final int percentComplete) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				_label.setText(message);
				int value = Math.min(percentComplete, 100);
				if (value < 0 || value == 100) {
					if (!_prog.isVisible()) {
						_prog.setVisible(true);
						_prog.setPreferredSize(new Dimension(400,23));
					}
					_prog.setValue(value);	
				} else {
					_prog.setVisible(false);					
					_prog.setPreferredSize(new Dimension(0,23));
				}				
			}
		});

	}

}

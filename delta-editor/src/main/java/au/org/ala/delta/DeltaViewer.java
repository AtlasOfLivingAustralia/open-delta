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
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.AbstractAction;
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
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalLookAndFeel;

import au.org.ala.delta.editor.controller.HelpController;
import au.org.ala.delta.gui.EditorDataModel;
import au.org.ala.delta.gui.util.IconHelper;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.DeltaDataSetRepository;
import au.org.ala.delta.slotfile.model.SlotFileRepository;
import au.org.ala.delta.util.IProgressObserver;

public class DeltaViewer extends JFrame {

	private static final long serialVersionUID = 1L;

	private JDesktopPane _desktop;
	private StatusBar _statusBar;

	// Yuk.
	private DeltaDataSetRepository _dataSetRepository;
	
	private SaveAction _saveAction;
	private SaveAsAction _saveAsAction;
	
	private HelpController _helpController;
	
	
	public static void main(String[] args) {		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception ex) {
					System.err.println(ex);
				}
		
				DeltaViewer instance = new DeltaViewer();
				instance.setVisible(true);
			}
		});

	}

	protected DeltaViewer() {
		super("DELTA - DEscription Language for TAxonomy (prototype)");
		setIconImage(IconHelper.createDeltaImageIcon().getImage());
	
		this.setExtendedState(MAXIMIZED_BOTH);
		this.getContentPane().setLayout(new BorderLayout());
		
		
		_helpController = new HelpController();
		_saveAction = new SaveAction();
		_saveAsAction = new SaveAsAction();
		_dataSetRepository = new SlotFileRepository();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		_desktop = new JDesktopPane();

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(_desktop, BorderLayout.CENTER);

		_desktop.setBackground(SystemColor.control);

		_statusBar = new StatusBar();
		getContentPane().add(_statusBar, BorderLayout.SOUTH);

		setJMenuBar(buildMenus());
		
		_helpController.enableHelpKey(this);

	}

	private EditorDataModel getCurrentDataSet() {
		EditorDataModel model = null;
		if (_desktop.getSelectedFrame() instanceof IContextHolder) {
			model = ((IContextHolder)_desktop.getSelectedFrame()).getContext();
		}
		return model;
	}
	private JMenuBar buildMenus() {

		JMenuBar menuBar = new JMenuBar();

		JMenu mnuFile = new JMenu("File");
		mnuFile.setMnemonic('f');

		JMenuItem mnuFileOpen = new JMenuItem("Open Dataset...");
		mnuFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		mnuFileOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loadFile();
			}
		});
		
		JMenuItem mnuFileSave = new JMenuItem("Save Dataset");
		
		mnuFileSave.setAction(_saveAction);
		
		JMenuItem mnuFileSaveAs = new JMenuItem("Save Dataset As...");
		mnuFileSaveAs.setAction(_saveAsAction);

		JMenuItem mnuFileExit = new JMenuItem("Exit");
		mnuFileExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DeltaViewer.this.dispose();
			}
		});

		mnuFile.add(mnuFileOpen);
		mnuFile.addSeparator();
		mnuFile.add(mnuFileSave);
		mnuFile.add(mnuFileSaveAs);
		mnuFile.addSeparator();
		mnuFile.add(mnuFileExit);
		menuBar.add(mnuFile);

		JMenu mnuView = new JMenu("View");
		mnuView.setMnemonic('v');

		JMenuItem mnuGrid = new JMenuItem("New Grid view");
		mnuGrid.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EditorDataModel model = getCurrentDataSet();
				if (model != null) {
					newMatrix(model);
				}
				
			}
		});

		mnuView.add(mnuGrid);

		JMenuItem mnuTree = new JMenuItem("New Tree view");
		mnuTree.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EditorDataModel model = getCurrentDataSet(); 
				if (model != null) {
					newTree(model);
				}
			}
		});

		mnuView.add(mnuTree);

		menuBar.add(mnuView);

		JMenu mnuWindow = new JMenu("Window");
		mnuWindow.setMnemonic('w');
		JMenuItem mnuTile = new JMenuItem(new TileAction(_desktop));
		mnuWindow.add(mnuTile);

		mnuWindow.addSeparator();

		JMenu mnuLF = new JMenu("Look & feel");
		mnuWindow.add(mnuLF);
		
		mnuLF.add(new JMenuItem(new LookAndFeelAction(this, new MetalLookAndFeel())));
		try {
			Class c = Class.forName(UIManager.getSystemLookAndFeelClassName());
			LookAndFeel sysLaf = (LookAndFeel) c.newInstance();
			mnuLF.add(new JMenuItem(new LookAndFeelAction(this, sysLaf)));
		} catch (Exception ex) {
			
		}
		try {
			// Nimbus L&F was added in update java 6 update 10.
			LookAndFeel nimbusLaF = (LookAndFeel) Class.forName("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel").newInstance(); 
			mnuLF.add(new JMenuItem(new LookAndFeelAction(this, nimbusLaF)));
		}
		catch (Exception e) {
			// The Nimbus L&F is not available, no matter.
		}
		menuBar.add(mnuWindow);
		
		JMenu mnuHelp = new JMenu("Help");
		JMenuItem mnuHelpContents = new JMenuItem("Contents");
		mnuHelp.add(mnuHelpContents);
		mnuHelpContents.addActionListener(_helpController.helpAction());
		
		JMenuItem helpOnSelection = new JMenuItem("Select Component", IconHelper.createImageIcon("help_cursor.png"));
		
		helpOnSelection.addActionListener(_helpController.helpOnSelectionAction());
		mnuHelp.add(helpOnSelection);

		
		JMenuItem mnuAbout = new JMenuItem("About...");
		mnuAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				newAboutBox();
			}
			
		});
		mnuHelp.add(mnuAbout);
		
		menuBar.add(mnuHelp);
		
		return menuBar;
	}

	private File _lastDirectory = null;

	private void loadFile() {
		
		File toOpen = selectFile(true);
		if (toOpen != null) {
			loadFile(toOpen);
		}
	}
	
	private File selectFile(boolean open) {
		File selectedFile = null;
		JFileChooser chooser = new JFileChooser();

		if (_lastDirectory != null) {
			chooser.setCurrentDirectory(_lastDirectory);
		}

		chooser.setFileFilter(new FileNameExtensionFilter("Delta Editor files *.dlt", "dlt"));
		int dialogResult;
		if (open) {
			dialogResult = chooser.showOpenDialog(this);
		}
		else {
			dialogResult = chooser.showSaveDialog(this);
		}
		if (dialogResult == JFileChooser.APPROVE_OPTION) {
			selectedFile = chooser.getSelectedFile();
			_lastDirectory = chooser.getCurrentDirectory();
		}
		return selectedFile;
	}

	private void newMatrix(EditorDataModel dataSet) {
		MatrixViewer matrixViewer = new MatrixViewer(dataSet);
		_helpController.setHelpKeyForComponent(matrixViewer, HelpController.GRID_VIEW_HELP_KEY);
		addToDesktop(matrixViewer);
	}

	private void newTree(EditorDataModel dataSet) {
		TreeViewer treeViewer = new TreeViewer(dataSet);
		_helpController.setHelpKeyForComponent(treeViewer, HelpController.TREE_VIEW_HELP_KEY);
		addToDesktop(treeViewer);
	}
	
	private void newAboutBox() {
		AboutBox aboutBox = new AboutBox(this);
		aboutBox.setVisible(true);
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
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					DeltaDataSet dataSet = _dataSetRepository.findByName(file.getAbsolutePath(), _statusBar);
					EditorDataModel model = new EditorDataModel(dataSet);
					newMatrix(model);
					_saveAction.setEnabled(true);
					_saveAsAction.setEnabled(true);
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					setCursor(Cursor.getDefaultCursor());
					_statusBar.clear();
				}
			}

		};

		t.start();

	}
	
	class SaveAction extends AbstractAction {
		
		public SaveAction() {
			super("Save Dataset");
			setEnabled(false);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			putValue(SHORT_DESCRIPTION, "Saves the changes made to the current data set");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			EditorDataModel model = getCurrentDataSet();
			if (model != null) {
				_dataSetRepository.save(model.getCurrentDataSet(), null);
			}
		}
	}

	class SaveAsAction extends AbstractAction {
		
		public SaveAsAction() {
			super("Save Dataset As...");
			setEnabled(false);
			putValue(SHORT_DESCRIPTION, "Saves the contents of the current data set into a new file");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			File newFile = selectFile(false);
			if (newFile != null) {
				EditorDataModel model = getCurrentDataSet();
				if (model != null) {
					_dataSetRepository.saveAsName(model.getCurrentDataSet(), newFile.getAbsolutePath(), null);
				}
			}
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

class TileAction extends AbstractAction {

	private JDesktopPane desk; // the desktop to work with

	public TileAction(JDesktopPane desk) {
		super("Tile Frames");
		this.desk = desk;
	}

	public void actionPerformed(ActionEvent ev) {

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

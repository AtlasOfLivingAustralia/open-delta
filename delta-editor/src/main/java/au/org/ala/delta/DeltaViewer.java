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
import au.org.ala.delta.model.DeltaDataSetRepository;
import au.org.ala.delta.slotfile.SlotFileRepository;
import au.org.ala.delta.util.IProgressObserver;

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

public class DeltaViewer extends JFrame {

	private static final long serialVersionUID = 1L;

	private JDesktopPane _desktop;
	private StatusBar _statusBar;

	private DeltaContext _currentDataSet;
	// Yuk.
	private DeltaDataSetRepository _dataSetRepository;
	
	private SaveAction _saveAction;
	private SaveAsAction _saveAsAction;
	
	
	public static void main(String[] args) {		
		System.out.println("Using "+System.getProperty("file.encoding"));
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
		super("Delta Viewer (prototype)");
		this.setExtendedState(MAXIMIZED_BOTH | DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		
		_saveAction = new SaveAction();
		_saveAsAction = new SaveAsAction();
		_dataSetRepository = new SlotFileRepository();
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		_desktop = new JDesktopPane();

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(_desktop, BorderLayout.CENTER);

		_desktop.setBackground(SystemColor.control);

		_statusBar = new StatusBar();
		getContentPane().add(_statusBar, BorderLayout.SOUTH);

		setJMenuBar(buildMenus());
		

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
				JInternalFrame f = _desktop.getSelectedFrame();
				if (f != null && f instanceof IContextHolder) {
					DeltaContext context = ((IContextHolder) f).getContext();
					newMatrix(context);
				}
			}
		});

		mnuView.add(mnuGrid);

		JMenuItem mnuTree = new JMenuItem("New Tree view");
		mnuTree.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JInternalFrame f = _desktop.getSelectedFrame();
				if (f != null && f instanceof IContextHolder) {
					DeltaContext context = ((IContextHolder) f).getContext();
					newTree(context);
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
		mnuLF.add(new JMenuItem(new LookAndFeelAction(this, new NimbusLookAndFeel())));

		menuBar.add(mnuWindow);
		
		JMenu mnuHelp = new JMenu("Help");
		JMenuItem mnuHelpContents = new JMenuItem("Contents");
		mnuHelp.add(mnuHelpContents);
		mnuHelpContents.addActionListener(
				new HelpController().helpAction()
		);
		menuBar.add(mnuHelp);

		return menuBar;

	}

	private File _lastDirectory = null;

	private void loadFile() {
		
		File toOpen = selectFile();
		if (toOpen != null) {
			loadFile(toOpen);
		}
	}
	
	private File selectFile() {
		File selectedFile = null;
		JFileChooser chooser = new JFileChooser();

		if (_lastDirectory != null) {
			chooser.setCurrentDirectory(_lastDirectory);
		}

		chooser.setFileFilter(new FileNameExtensionFilter("Delta Editor files *.dlt", "dlt"));
		int dialogResult = chooser.showOpenDialog(this);
		if (dialogResult == JFileChooser.APPROVE_OPTION) {
			selectedFile = chooser.getSelectedFile();
			_lastDirectory = chooser.getCurrentDirectory();
		}
		return selectedFile;
	}

	private void newMatrix(DeltaContext context) {
		addToDesktop(new MatrixViewer(context));
	}

	private void newTree(DeltaContext context) {
		addToDesktop(new TreeViewer(context));
	}
	
	private void addToDesktop(JInternalFrame frame) {
		_desktop.add(frame);		
		frame.setClosable(true);
		frame.setMaximizable(true);
		frame.setResizable(true);
		frame.setIconifiable(true);
		frame.setVisible(true);
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
					DeltaContext context = DeltaFileReader.readDeltaFile(file.getAbsolutePath(), _statusBar);
					newMatrix(context);
					_saveAction.setEnabled(true);
					_saveAsAction.setEnabled(true);
					_currentDataSet = context;
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
			_dataSetRepository.save(_currentDataSet, null);
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
			
			File newFile = selectFile();
			if (newFile != null) {
				_dataSetRepository.saveAsName(_currentDataSet, newFile.getAbsolutePath(), null);
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

	private JProgressBar _prog;
	private JLabel _label;

	public StatusBar() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(400, 23));
		_prog = new JProgressBar();
		_prog.setPreferredSize(new Dimension(200, 20));
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
				_prog.setValue(Math.min(percentComplete, 100));
			}
		});

	}

}

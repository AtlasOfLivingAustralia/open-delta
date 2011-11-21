package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.MessageFormat;

import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.model.SearchDirection;
import au.org.ala.delta.ui.SearchController;
import au.org.ala.delta.ui.SearchDialog;
import au.org.ala.delta.ui.SearchOptions;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;

public class RtfReportDisplayDialog extends IntkeyDialog {
	/**
     * 
     */
	private static final long serialVersionUID = 7668085877552672061L;

	private JTextPane _textPane;
	private JScrollPane _scrollPane;

	private String _contentSource;
	private JMenuBar _menuBar;
	private JMenu _mnuFile;
	private JMenuItem _mnuItSaveAs;
	private JMenuItem _mnuItPrint;
	private JMenu _mnuEdit;
	private JMenuItem _mnuItFind;
	private JMenuItem _mnuItCopy;
	private JMenuItem _mnuItSelectAll;
	private JMenu _mnuWindow;
	private JMenuItem _mnuItCascade;
	private JMenuItem _mnuItTile;
	private JMenuItem _mnuItCloseAll;

	private SearchDialog _searchDialog;

	@Resource
	String fileFilterDescription;

	@Resource
	String fileWriteError;

	@Resource
	String fileChooserTitle;

	/**
	 * @wbp.parser.constructor
	 */
	public RtfReportDisplayDialog(Dialog owner, EditorKit editorKit, String contentSource, String title) {
		super(owner, false, true);
		initialize(editorKit, contentSource, title);
	}

	public RtfReportDisplayDialog(Frame owner, EditorKit editorKit, String contentSource, String title) {
		super(owner, false, true);
		initialize(editorKit, contentSource, title);
	}

	public void initialize(EditorKit editorKit, String contentSource, String title) {
		ActionMap actionMap = Application.getInstance().getContext().getActionMap(RtfReportDisplayDialog.class, this);
		ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(RtfReportDisplayDialog.class);
		resourceMap.injectFields(this);

		setPreferredSize(new Dimension(800, 450));

		_scrollPane = new JScrollPane();
		getContentPane().add(_scrollPane, BorderLayout.CENTER);

		_textPane = new JTextPane();

		_textPane.setEditable(false);
		_scrollPane.setViewportView(_textPane);

		_contentSource = contentSource;

		_textPane.setEditorKit(editorKit);
		_textPane.setText(contentSource);

		// ensure that top of text is visible after text has been inserted.
		_textPane.setCaretPosition(0);

		setTitle(title);

		_menuBar = new JMenuBar();
		setJMenuBar(_menuBar);

		_mnuFile = new JMenu();
		_mnuFile.setName("rtfReportDisplayDialog_mnuFile");
		_menuBar.add(_mnuFile);

		_mnuItSaveAs = new JMenuItem();
		_mnuItSaveAs.setAction(actionMap.get("rtfReportDisplayDialog_mnuItSaveAs"));
		_mnuFile.add(_mnuItSaveAs);

		_mnuItPrint = new JMenuItem();
		_mnuItPrint.setAction(actionMap.get("rtfReportDisplayDialog_mnuItPrint"));
		_mnuFile.add(_mnuItPrint);

		_mnuEdit = new JMenu();
		_mnuEdit.setName("rtfReportDisplayDialog_mnuEdit");
		_mnuEdit.setEnabled(true);
		_menuBar.add(_mnuEdit);

		_mnuItFind = new JMenuItem();
		_mnuItFind.setAction(actionMap.get("rtfReportDisplayDialog_mnuItFind"));
		_mnuEdit.add(_mnuItFind);

		_mnuEdit.addSeparator();

		_mnuItCopy = new JMenuItem();
		_mnuItCopy.setAction(actionMap.get("rtfReportDisplayDialog_mnuItCopy"));
		_mnuEdit.add(_mnuItCopy);

		_mnuEdit.addSeparator();

		_mnuItSelectAll = new JMenuItem();
		_mnuItSelectAll.setAction(actionMap.get("rtfReportDisplayDialog_mnuItSelectAll"));
		_mnuEdit.add(_mnuItSelectAll);

		_mnuWindow = new JMenu();
		_mnuWindow.setName("rtfReportDisplayDialog_mnuWindow");
		_menuBar.add(_mnuWindow);

		_mnuItCascade = new JMenuItem();
		_mnuItCascade.setAction(actionMap.get("rtfReportDisplayDialog_mnuItCascade"));
		_mnuWindow.add(_mnuItCascade);

		_mnuItTile = new JMenuItem();
		_mnuItTile.setAction(actionMap.get("rtfReportDisplayDialog_mnuItTile"));
		_mnuWindow.add(_mnuItTile);

		_mnuItCloseAll = new JMenuItem();
		_mnuItCloseAll.setAction(actionMap.get("rtfReportDisplayDialog_mnuItCloseAll"));
		_mnuWindow.add(_mnuItCloseAll);

	}

	public void setEditorKit(EditorKit editorKit) {
		_textPane.setEditorKit(editorKit);
	}

	public void setContent(String source) {
		_contentSource = source;
		_textPane.setText(_contentSource);
	}

	@Action
	public void rtfReportDisplayDialog_mnuItSaveAs() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(fileChooserTitle);
		chooser.setFileFilter(new FileNameExtensionFilter(fileFilterDescription, "rtf"));
		chooser.setAcceptAllFileFilterUsed(false);

		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			if (selectedFile != null) {
				if (!selectedFile.getName().endsWith(".rtf")) {
					selectedFile = new File(selectedFile.getAbsolutePath() + ".rtf");
				}

				try {
					FileWriter fw = new FileWriter(selectedFile);
					fw.append(_contentSource);
					fw.flush();
					fw.close();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(this, MessageFormat.format(fileWriteError, selectedFile.getAbsolutePath()));
				}
			}
		}
	}

	@Action
	public void rtfReportDisplayDialog_mnuItPrint() {
		try {
			JTextPane temp = new JTextPane();
			temp.setEditorKit(new SimpleRtfEditorKit(null, false, false));
			temp.setText(_contentSource);
			temp.print();
		} catch (PrinterException ex) {
		}
	}

	@Action
	public void rtfReportDisplayDialog_mnuItFind() {
		if (_searchDialog == null) {
			_searchDialog = new SearchDialog(new RTFSearchController(_textPane));
		}
		String findText = _textPane.getSelectedText();
		if ((findText != null) && (findText.length() > 0)) {
			_searchDialog.setFindText(findText);
		}
		_searchDialog.setVisible(true);
	}

	@Action
	public void rtfReportDisplayDialog_mnuItCopy() {
		_textPane.copy();
	}

	public String getRtfTextBody() {
		String rtfText = null;
		Document doc = _textPane.getDocument();
		SimpleRtfEditorKit kit = new SimpleRtfEditorKit(_textPane);
		StringWriter writer = new StringWriter();
		try {
			kit.writeBody(writer, doc, 0, doc.getLength());
			rtfText = writer.toString().trim();
			while (rtfText.endsWith("\\par")) {
				rtfText = rtfText.substring(0, rtfText.lastIndexOf("\\par"));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return _textPane.getSelectedText();
		}
		return rtfText;
	}

	@Action
	public void rtfReportDisplayDialog_mnuItSelectAll() {
		_textPane.selectAll();
	}

	@Action
	public void rtfReportDisplayDialog_mnuItCascade() {
		IntKeyDialogController.cascadeWindows();
	}

	@Action
	public void rtfReportDisplayDialog_mnuItTile() {
		IntKeyDialogController.cascadeWindows();
	}

	@Action
	public void rtfReportDisplayDialog_mnuItCloseAll() {
		IntKeyDialogController.closeWindows();
	}

	protected static class RTFSearchController implements SearchController {

		private ResourceMap _messages;
		private JTextPane _textpane;

		public RTFSearchController(JTextPane textpane) {
			SingleFrameApplication application = (SingleFrameApplication) Application.getInstance();
			_messages = application.getContext().getResourceMap();
			_textpane = textpane;
		}

		@Override
		public String getTitle() {
			return _messages.getString("rtfReportDisplayDialog_mnuItFind.Action.text");
		}

		@Override
		public JComponent getOwningComponent() {
			return _textpane;
		}

		@Override
		public boolean findNext(SearchOptions options) {
			Document doc = _textpane.getDocument();

			String text = options.getSearchTerm();
			if (!options.isCaseSensitive()) {
				text = text.toLowerCase();
			}

			try {
				String docText = doc.getText(0, doc.getLength());
				if (!options.isCaseSensitive()) {
					docText = docText.toLowerCase();
				}
				int caretPos = _textpane.getCaretPosition();
				boolean found = false;
				if (options.getSearchDirection() == SearchDirection.Forward) {
					int pos = docText.indexOf(text, caretPos);
					if (pos >= 0) {
						found = true;
						_textpane.select(pos, pos + text.length());
					}
				} else {

					int startFrom = caretPos > 0 ? caretPos - 2 : 0;

					int pos = indexOfReverse(docText, startFrom, text);
					if (pos >= 0) {
						found = true;
						_textpane.select(pos, pos + text.length());
					}
				}

				if (!found && options.isWrappedSearch()) {
					if (options.getSearchDirection() == SearchDirection.Forward) {
						int pos = docText.indexOf(text, 0);
						if (pos >= 0) {
							found = true;
							_textpane.select(pos, pos + text.length());
						}
					} else {
						int pos = indexOfReverse(docText, doc.getLength(), text);
						if (pos >= 0) {
							found = true;
							_textpane.select(pos, pos + text.length());
						}
					}
				}

				if (!found) {
					_textpane.select(-1, -1);
				}

				return found;
			} catch (BadLocationException ex) {
				throw new RuntimeException(ex);
			}

		}

		public int indexOfReverse(String text, int offset, String searchText) {
			int length = text.length();
			int searchLength = searchText.length();
			if (offset == length) {
				offset -= 1;
			}
			outer: for (int i = offset - searchLength + 1; i >= 0; i--) {
				for (int j = searchLength - 1; j >= 0; j--) {
					if (searchText.charAt(j) != text.charAt(i + j)) {
						continue outer;
					}
				}
				return i;
			}
			return -1;
		}

	}

}

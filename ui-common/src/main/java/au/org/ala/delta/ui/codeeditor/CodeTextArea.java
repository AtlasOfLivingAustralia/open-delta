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
package au.org.ala.delta.ui.codeeditor;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.text.TabExpander;
import javax.swing.text.Utilities;

import au.org.ala.delta.model.SearchDirection;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;
import au.org.ala.delta.ui.SearchOptions;
import au.org.ala.delta.ui.SearchReplaceDialog;
import au.org.ala.delta.ui.SearchAndReplaceController;
import au.org.ala.delta.ui.codeeditor.action.BackspaceKeyAction;
import au.org.ala.delta.ui.codeeditor.action.CommentBlockAction;
import au.org.ala.delta.ui.codeeditor.action.CopyKeyAction;
import au.org.ala.delta.ui.codeeditor.action.CutKeyAction;
import au.org.ala.delta.ui.codeeditor.action.DeleteKeyAction;
import au.org.ala.delta.ui.codeeditor.action.DownKeyAction;
import au.org.ala.delta.ui.codeeditor.action.EndKeyAction;
import au.org.ala.delta.ui.codeeditor.action.EnterKeyAction;
import au.org.ala.delta.ui.codeeditor.action.FindKeyAction;
import au.org.ala.delta.ui.codeeditor.action.FindNextAction;
import au.org.ala.delta.ui.codeeditor.action.GotoLineKeyAction;
import au.org.ala.delta.ui.codeeditor.action.HomeKeyAction;
import au.org.ala.delta.ui.codeeditor.action.InsertCharAction;
import au.org.ala.delta.ui.codeeditor.action.InsertKeyAction;
import au.org.ala.delta.ui.codeeditor.action.LeftKeyAction;
import au.org.ala.delta.ui.codeeditor.action.PageDownKeyAction;
import au.org.ala.delta.ui.codeeditor.action.PageUpKeyAction;
import au.org.ala.delta.ui.codeeditor.action.PasteKeyAction;
import au.org.ala.delta.ui.codeeditor.action.RightKeyAction;
import au.org.ala.delta.ui.codeeditor.action.SelectAllAction;
import au.org.ala.delta.ui.codeeditor.action.TabKeyAction;
import au.org.ala.delta.ui.codeeditor.action.ToggleLineNumbersAction;
import au.org.ala.delta.ui.codeeditor.action.ToggleShowWhitespaceAction;
import au.org.ala.delta.ui.codeeditor.action.UpKeyAction;
import au.org.ala.delta.ui.codeeditor.document.RegExDocument;
import au.org.ala.delta.ui.codeeditor.document.TextDocument;
import au.org.ala.delta.ui.codeeditor.document.TextDocument.ITokenAccumulator;
import au.org.ala.delta.ui.codeeditor.document.TextDocumentFactory;

public class CodeTextArea extends JComponent implements Scrollable, TabExpander {

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_GUTTER_WIDTH = 45;

	private int _gutterWidth = DEFAULT_GUTTER_WIDTH;

	private static Color GUTTER_COLOR = new Color(0xf0f0f0);

	/** The caret position listeners. */
	private Set<CaretPositionListener> caretPositionListeners = new HashSet<CaretPositionListener>();

	/** The key bindings. */
	private Map<KeyStroke, ActionListener> keyBindings;

	/** The insert char action. */
	protected ActionListener insertCharAction = null;

	// package-private members
	private int currentLineIndex;

	private Token currentLineTokens;

	private Segment currentLine;

	/** The syntax styles. */
	protected SyntaxStyle[] styles = SyntaxUtilities.getDefaultSyntaxStyles();

	/** The caret color. */
	protected Color caretColor = Color.black;

	/** The selection color. */
	protected Color selectionColor = new Color(0xccccff);

	/** The line highlight color. */
	protected Color lineHighlightColor = new Color(0xe0e0e0);

	/** The bracket highlight color. */
	protected Color bracketHighlightColor = Color.black;

	/** The color of the end of line marker. */
	protected Color eolMarkerColor = new Color(0x009999);

	/** Indicates, whether the text area should be editable or not. */
	protected boolean editable = true;

	/** Indicates, whether the line higlighting should be done or not. */
	protected boolean lineHighlight = true;

	/** Indicates, whether the bracket higlighting should be done or not. */
	protected boolean bracketHighlight = true;

	/** Indicates, whether the end of line markers should be visible or not. */
	protected boolean eolMarkers = true;

	/** Indicates, whether the caret should blink or not. */
	protected boolean caretBlinks = true;

	/** Indicates, whether the caret should be visible or not. */
	protected boolean caretVisible = true;

	/** Indicates, whether the caret currently blinks or not. */
	protected boolean blink;

	/** The tabulator size in pixels. */
	protected int tabSizePixels;

	protected int tabSize;

	/** The metrics of the current font. */
	protected FontMetrics fm;

	/** The highlight handler. */
	protected Highlight highlight;

	/** The timer for bliking caret. */
	protected Timer caretTimer;

	/** The popup menu. */
	protected JPopupMenu popup;

	/** The Search/Replace dialog. */
	private SearchReplaceDialog searchReplaceDialog = null;

	/** The 'goto line' dialog. */
	private GotoLineDialog gotoLineDialog = null;

	/** The event listeners. */
	protected EventListenerList listenerList;

	protected MutableCaretEvent caretEvent;

	/** The text document. */
	protected TextDocument document;

	protected DocumentHandler documentHandler;

	protected Segment lineSegment;

	protected int selectionStart;

	protected int selectionStartLine;

	protected int selectionEnd;

	protected int selectionEndLine;

	protected boolean biasLeft;

	protected int bracketPosition;

	protected int bracketLine;

	protected int magicCaret;

	protected boolean overwrite;

	private boolean _showwhitespace;

	/**
	 * Creates a new JEditTextArea with the default settings.
	 */
	public CodeTextArea() {
		this("text/plain");
	}

	/**
	 * Creates a new JEditTextArea with the specified settings.
	 * 
	 * @param mimeType
	 *            The mime type of the document.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CodeTextArea(String mimeType) {
		// Enable the necessary events
		enableEvents(AWTEvent.KEY_EVENT_MASK);

		setAutoscrolls(true);
		setDoubleBuffered(true);
		setOpaque(true);

		ToolTipManager.sharedInstance().registerComponent(this);

		currentLine = new Segment();
		currentLineIndex = -1;

		setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

		setFont(new Font("Monospaced", Font.PLAIN, 12));
		setForeground(Color.black);
		setBackground(Color.white);

		// initializing caret timer
		this.caretTimer = new Timer(500, new CaretBlinker());
		this.caretTimer.setInitialDelay(500);
		// this.caretTimer.start();

		// Initialize some misc. stuff
		documentHandler = new DocumentHandler();
		listenerList = new EventListenerList();
		caretEvent = new MutableCaretEvent();
		lineSegment = new Segment();
		bracketLine = bracketPosition = -1;
		blink = true;

		// Add some event listeners
		addMouseListener(new MouseHandler());
		addMouseMotionListener(new DragHandler());
		addFocusListener(new FocusListener() {

			/**
			 * Invoked when a component gains the keyboard focus.
			 * 
			 * @param evt
			 *            The event to process.
			 */
			public void focusGained(FocusEvent evt) {
				setCaretVisible(true);
			}

			/**
			 * Invoked when a component loses the keyboard focus.
			 * 
			 * @param evt
			 *            The event to process.
			 */
			public void focusLost(FocusEvent evt) {
				setCaretVisible(false);
			}

		});
		addCaretListener(new CaretHandler());

		// configuring key bindings
		keyBindings = new HashMap<KeyStroke, ActionListener>();
		insertCharAction = new InsertCharAction(this);
		setDefaultKeyBindings();

		// creating the document
		setDocument(TextDocumentFactory.createDocument(mimeType));

		try {
			Set<Object> focusKeys = new HashSet<Object>();
			Class keyStrokeClazz = Class.forName("java.awt.AWTKeyStroke");
			Method getKeyStrokeMethod = keyStrokeClazz.getMethod("getAWTKeyStroke", new Class[] { int.class, int.class });
			Method setFocusTraversalKeysMethod = this.getClass().getMethod("setFocusTraversalKeys", new Class[] { int.class, Set.class });
			Method setFocusableMethod = this.getClass().getMethod("setFocusable", new Class[] { boolean.class });

			focusKeys.add(getKeyStrokeMethod.invoke(null, new Object[] { new Integer(KeyEvent.VK_TAB), new Integer(InputEvent.CTRL_MASK) }));
			setFocusTraversalKeysMethod.invoke(this, new Object[] { new Integer(0), focusKeys });
			setFocusableMethod.invoke(this, new Object[] { new Boolean(true) });

			focusKeys = new HashSet<Object>();
			focusKeys.add(getKeyStrokeMethod.invoke(null, new Object[] { new Integer(KeyEvent.VK_TAB), new Integer(InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK) }));
			setFocusTraversalKeysMethod.invoke(this, new Object[] { new Integer(1), focusKeys });
		} catch (Throwable ex) {
			System.err.println("To get focus traversal working properly use J2SDK 1.4 or above!");
		}
	}

	public void setShowLineNumbers(boolean show) {
		if (show) {
			_gutterWidth = DEFAULT_GUTTER_WIDTH;
		} else {
			_gutterWidth = 0;
		}
		repaint();
	}

	public boolean getShowLineNumbers() {
		return _gutterWidth > 0;
	}

	public void commentSelectedLines() {
		try {
			String comment = getDocument().getLineComment();
			for (int line = getSelectionStartLine(); line <= getSelectionEndLine(); ++line) {
				int offset = getLineStartOffset(line);
				getDocument().insertString(offset, comment, null);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void uncommentSelectedLines() {
		try {
			String comment = getDocument().getLineComment();
			for (int line = getSelectionStartLine(); line <= getSelectionEndLine(); ++line) {
				String txt = getLineText(line);
				if (!txt.startsWith(comment)) {
					return;
				}
			}
			for (int line = getSelectionStartLine(); line <= getSelectionEndLine(); ++line) {
				int offset = getLineStartOffset(line);
				getDocument().remove(offset, comment.length());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void toggleShowWhitespace() {
		_showwhitespace = !_showwhitespace;
		eolMarkers = _showwhitespace;
		this.repaint();
	}

	public void indentSelectedLines() {
		int endline = getSelectionEndLine();
		if (getCaretPosition() == getLineStartOffset(getCaretLine())) {
			endline--;
		}
		for (int line = getSelectionStartLine(); line <= endline; ++line) {
			int offset = getLineStartOffset(line);
			this.insertTab(offset);
		}
	}

	public void unindentSelectedLines() {
		try {
			char[] buf = new char[tabSize];
			Arrays.fill(buf, ' ');
			String tabstr = new String(buf);

			int endline = getSelectionEndLine();
			if (getCaretPosition() == getLineStartOffset(getCaretLine())) {
				endline--;
			}

			for (int line = getSelectionStartLine(); line <= endline; ++line) {
				String txt = getLineText(line);
				if (!txt.startsWith("\t") && !txt.startsWith(tabstr.toString())) {
					return;
				}
			}

			for (int line = getSelectionStartLine(); line <= endline; ++line) {
				int offset = getLineStartOffset(line);
				String txt = getLineText(line);
				if (txt.startsWith("\t")) {
					getDocument().remove(offset, 1);
				} else if (txt.startsWith(" ")) {
					getDocument().remove(offset, tabSize);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Returns the syntax styles used to paint colorized text. Entry <i>n</i> will be used to paint tokens with id = <i>n</i>.
	 * 
	 * @return The current syntax styles.
	 */
	public final SyntaxStyle[] getStyles() {
		return styles;
	}

	/**
	 * Sets the syntax styles used to paint colorized text. Entry <i>n</i> will be used to paint tokens with id = <i>n</i>.
	 * 
	 * @param styles
	 *            The syntax styles
	 */
	public final void setStyles(SyntaxStyle[] styles) {
		this.styles = styles;
		repaint();
	}

	/**
	 * Returns the caret color.
	 * 
	 * @return The current caret color.
	 */
	public final Color getCaretColor() {
		return caretColor;
	}

	/**
	 * Sets the caret color.
	 * 
	 * @param caretColor
	 *            The caret color
	 */
	public final void setCaretColor(Color caretColor) {
		this.caretColor = caretColor;
		invalidateSelectedLines();
	}

	/**
	 * Returns the selection color.
	 * 
	 * @return The current selection color.
	 */
	public final Color getSelectionColor() {
		return selectionColor;
	}

	/**
	 * Sets the selection color.
	 * 
	 * @param selectionColor
	 *            The selection color
	 */
	public final void setSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
		invalidateSelectedLines();
	}

	/**
	 * Returns the line highlight color.
	 * 
	 * @return The current highlight color.
	 */
	public final Color getLineHighlightColor() {
		return lineHighlightColor;
	}

	/**
	 * Sets the line highlight color.
	 * 
	 * @param lineHighlightColor
	 *            The line highlight color
	 */
	public final void setLineHighlightColor(Color lineHighlightColor) {
		this.lineHighlightColor = lineHighlightColor;
		invalidateSelectedLines();
	}

	/**
	 * Returns true if line highlight is enabled, false otherwise.
	 * 
	 * @return true if line highlight is enabled, false otherwise.
	 */
	public final boolean isLineHighlightEnabled() {
		return lineHighlight;
	}

	/**
	 * Enables or disables current line highlighting.
	 * 
	 * @param lineHighlight
	 *            True if current line highlight should be enabled, false otherwise
	 */
	public final void setLineHighlightEnabled(boolean lineHighlight) {
		this.lineHighlight = lineHighlight;
		invalidateSelectedLines();
	}

	/**
	 * Returns the bracket highlight color.
	 * 
	 * @return The current bracket highlight color.
	 */
	public final Color getBracketHighlightColor() {
		return bracketHighlightColor;
	}

	/**
	 * Sets the bracket highlight color.
	 * 
	 * @param bracketHighlightColor
	 *            The bracket highlight color
	 */
	public final void setBracketHighlightColor(Color bracketHighlightColor) {
		this.bracketHighlightColor = bracketHighlightColor;
		invalidateLine(getBracketLine());
	}

	/**
	 * Returns true if bracket highlighting is enabled, false otherwise. When bracket highlighting is enabled, the bracket matching the one before the caret (if any) is highlighted.
	 * 
	 * @return true in case bracket hihllighting is enabled, else false.
	 */
	public final boolean isBracketHighlightEnabled() {
		return bracketHighlight;
	}

	/**
	 * Enables or disables bracket highlighting. When bracket highlighting is enabled, the bracket matching the one before the caret (if any) is highlighted.
	 * 
	 * @param bracketHighlight
	 *            True if bracket highlighting should be enabled, false otherwise
	 */
	public final void setBracketHighlightEnabled(boolean bracketHighlight) {
		this.bracketHighlight = bracketHighlight;
		invalidateLine(getBracketLine());
	}

	/**
	 * Returns the EOL marker color.
	 * 
	 * @return The current end of line marker color.
	 */
	public final Color getEOLMarkerColor() {
		return eolMarkerColor;
	}

	/**
	 * Sets the EOL marker color.
	 * 
	 * @param eolMarkerColor
	 *            The EOL marker color
	 */
	public final void setEOLMarkerColor(Color eolMarkerColor) {
		this.eolMarkerColor = eolMarkerColor;
		repaint();
	}

	/**
	 * Returns true if EOL markers are drawn, false otherwise.
	 * 
	 * @return true if end of line markers are drawn, false otherwise.
	 */
	public final boolean getEOLMarkersPainted() {
		return eolMarkers;
	}

	/**
	 * Sets if EOL markers are to be drawn.
	 * 
	 * @param eolMarkers
	 *            True if EOL markers should be drawn, false otherwise
	 */
	public final void setEOLMarkersPainted(boolean eolMarkers) {
		this.eolMarkers = eolMarkers;
		repaint();
	}

	/**
	 * Adds a custom highlight painter.
	 * 
	 * @param highlight
	 *            The highlight
	 */
	public void addCustomHighlight(Highlight highlight) {
		highlight.init(this, this.highlight);
		this.highlight = highlight;
	}

	/**
	 * Adds a CaretPositionListener instance.
	 * 
	 * @param listener
	 *            A CaretPositionListener instance.
	 */
	public void addCaretPositionListener(CaretPositionListener listener) {
		caretPositionListeners.add(listener);
		updateCaret();
	}

	/**
	 * Removes a CaretPositionListener instance.
	 * 
	 * @param listener
	 *            A CaretPositionListener instance.
	 */
	public void removeCaretPositionListener(CaretPositionListener listener) {
		caretPositionListeners.remove(listener);
	}

	/**
	 * Returns true if the caret is blinking, false otherwise.
	 * 
	 * @return true if the caret is blinking, false otherwise.
	 */
	public final boolean isCaretBlinkEnabled() {
		return caretBlinks;
	}

	/**
	 * Toggles caret blinking.
	 * 
	 * @param caretBlinks
	 *            True if the caret should blink, false otherwise
	 */
	public void setCaretBlinkEnabled(boolean caretBlinks) {
		this.caretBlinks = caretBlinks;
		if (!caretBlinks) {
			blink = false;
		}

		invalidateSelectedLines();
	}

	/**
	 * Returns true if the caret is visible, false otherwise.
	 * 
	 * @return true if the caret is visible, false otherwise.
	 */
	public final boolean isCaretVisible() {
		return (!caretBlinks || blink) && caretVisible;
	}

	/**
	 * Sets if the caret should be visible.
	 * 
	 * @param caretVisible
	 *            True if the caret should be visible, false otherwise
	 */
	public void setCaretVisible(boolean caretVisible) {
		this.caretVisible = caretVisible;
		blink = true;
		invalidateSelectedLines();
	}

	/**
	 * Blinks the caret.
	 */
	public final void blinkCaret() {
		if (caretBlinks) {
			blink = !blink;
			invalidateSelectedLines();
		} else {
			blink = true;
		}
	}

	/**
	 * Returns the tool tip to display at the specified location.
	 * 
	 * @param evt
	 *            The mouse event
	 * @return The requested tooltip text.
	 */
	public String getToolTipText(MouseEvent evt) {
		if (highlight != null) {
			return highlight.getToolTipText(evt);			
		} else {
			int lineIndex = yToLine(evt.getY());
			int columnIndex = xToOffset(lineIndex, evt.getX());
			Segment seg = new Segment();
			getLineText(lineIndex, seg);
			if (lineIndex >= 0) {
				TokenList tokList = new TokenList();
				getDocument().markTokens(seg, lineIndex, tokList);
				if (tokList.getList() != null && tokList.getList().size() > 0) {
					for (TokenDescriptor tok : tokList.getList()) {
						if (columnIndex >= tok.offset && columnIndex <= tok.offset + tok.length && tok.tag != null) {
							return tok.tag.toString();
						}
					}										
				}
			}
			return null;
		}
	}
	
	static class TokenDescriptor {
		
		public int offset;
		public int length;
		public byte tokenId;
		public Object tag;
		
		public TokenDescriptor(int offset, int length, byte tokenId, Object tag) {
			this.offset = offset;
			this.length = length;
			this.tokenId = tokenId;
			this.tag = tag;				
		}
	}
	
	static class TokenList implements ITokenAccumulator {
		
		private List<TokenDescriptor> _list = new ArrayList<CodeTextArea.TokenDescriptor>();

		@Override
		public void addToken(int offset, int length, byte id, Object tag) {
			_list.add(new TokenDescriptor(offset, length, id, tag));			
		}
		
		public List<TokenDescriptor> getList() {
			return _list;
		}
		
	}

	/**
	 * Gets the surrounding frame.
	 * 
	 * @return The surrounding frame.
	 */
	public Frame getFrame() {
		Container parent = this.getParent();
		while (parent != null) {
			if (parent instanceof Frame) {
				return (Frame) parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	/**
	 * Shows the find/replace dialog.
	 */
	public void showFindDialog() {
		if (searchReplaceDialog == null) {
			searchReplaceDialog = new SearchReplaceDialog(new SearchableAdapter(this));
		}
		String findText = this.getSelectedText();
		if ((findText != null) && (findText.length() > 0)) {
			searchReplaceDialog.setFindText(findText);
		}
		searchReplaceDialog.show();
	}

	/**
	 * Finds the next occurrence of the text, specified by the find/replace dialog.
	 */
	public void findNext() {
		if (searchReplaceDialog == null) {
			searchReplaceDialog = new SearchReplaceDialog(new SearchableAdapter(this));
			searchReplaceDialog.show();
		}
		String findText = searchReplaceDialog.getFindText();
		if ((findText != null) && (findText.length() > 0)) {
			// finding text
			find(searchReplaceDialog.getSearchOptions());
		}
	}

	/**
	 * Shows the 'goto line' dialog.
	 */
	public void showGotoLineDialog() {
		if (gotoLineDialog == null) {
			gotoLineDialog = new GotoLineDialog(this);
		}
		gotoLineDialog.setLineNumber(getSelectionStartLine());
		gotoLineDialog.setVisible(true);
	}

	/**
	 * Returns the font metrics used by this component.
	 * 
	 * @return The current font metrics.
	 */
	public FontMetrics getFontMetrics() {
		return fm;
	}

	/**
	 * Sets the font for this component. This is overridden to update the cached font metrics and to recalculate which lines are visible.
	 * 
	 * @param font
	 *            The font
	 */
	@SuppressWarnings("deprecation")
	public void setFont(Font font) {
		super.setFont(font);
		fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
	}

	/**
	 * Repaints the text.
	 * 
	 * @param gfx
	 *            The graphics context
	 */
	public void paint(Graphics gfx) {
		tabSize = getDocument().getTabSize();
		tabSizePixels = fm.charWidth(' ') * tabSize;

		Rectangle clipRect = gfx.getClipBounds();
		// Clear any crap
		gfx.setColor(getBackground());
		gfx.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
		// Paint the gutter background
		if (_gutterWidth > 0) {
			gfx.setColor(GUTTER_COLOR);
			gfx.fillRect(0, clipRect.y, _gutterWidth, clipRect.height);
		}

		int height = fm.getHeight();
		int firstInvalid = clipRect.y / height;
		int lastInvalid = (clipRect.y + clipRect.height - 1) / height;

		try {
			TextDocument doc = getDocument();

			for (int line = firstInvalid; line <= lastInvalid; line++) {
				paintLine(gfx, line);
			}

			if (doc != null && doc.isNextLineRequested()) {
				int h = clipRect.y + clipRect.height;
				repaint(0, h, getWidth(), getHeight() - h);
			}
		} catch (Exception e) {
			System.err.println("Error repainting line" + " range {" + firstInvalid + "," + lastInvalid + "}:");
			e.printStackTrace();
		}
	}

	/**
	 * Marks a line as needing a repaint.
	 * 
	 * @param line
	 *            The line to invalidate
	 */
	public final void invalidateLine(int line) {
		repaint(0, lineToY(line) + fm.getMaxDescent() + fm.getLeading(), getWidth(), fm.getHeight());
	}

	/**
	 * Marks a range of lines as needing a repaint.
	 * 
	 * @param firstLine
	 *            The first line to invalidate
	 * @param lastLine
	 *            The last line to invalidate
	 */
	public final void invalidateLineRange(int firstLine, int lastLine) {
		repaint(0, lineToY(firstLine) + fm.getMaxDescent() + fm.getLeading(), getWidth(), (lastLine - firstLine + 1) * fm.getHeight());
	}

	/**
	 * Repaints the lines containing the selection.
	 */
	public final void invalidateSelectedLines() {
		invalidateLineRange(getSelectionStartLine(), getSelectionEndLine());
	}

	/**
	 * Converts a line index to a y co-ordinate.
	 * 
	 * @param line
	 *            The line to determine the y coordinate for.
	 * @return The computed vertical position.
	 */
	public int lineToY(int line) {
		FontMetrics fm = getFontMetrics();
		return (line) * fm.getHeight() - (fm.getLeading() + fm.getMaxDescent());
	}

	/**
	 * Converts a y co-ordinate to a line index.
	 * 
	 * @param y
	 *            The y co-ordinate
	 * @return The computed line index.
	 */
	public int yToLine(int y) {
		FontMetrics fm = getFontMetrics();
		int height = fm.getHeight();
		return Math.max(0, Math.min(getLineCount() - 1, y / height));
	}

	/**
	 * Converts an offset in a line into an x co-ordinate. This is a slow version that can be used any time.
	 * 
	 * @param line
	 *            The line
	 * @param offset
	 *            The offset, from the start of the line
	 * @return The computed horizontal position.
	 */
	public final int offsetToX(int line, int offset) {
		// don't use cached tokens
		currentLineTokens = null;
		return _offsetToX(line, offset);
	}

	/**
	 * Converts an offset in a line into an x co-ordinate. This is a fast version that should only be used if no changes were made to the text since the last repaint.
	 * 
	 * @param line
	 *            The line
	 * @param offset
	 *            The offset, from the start of the line
	 * @return The computed horizontal position.
	 */
	public int _offsetToX(int line, int offset) {

		/* Use painter's cached info for speed */
		FontMetrics fm = getFontMetrics();

		getLineText(line, lineSegment);

		int segmentOffset = lineSegment.offset;
		int x = _gutterWidth;

		/*
		 * If syntax coloring is enabled, we have to do this because tokens can vary in width
		 */
		Token tokens;
		if (currentLineIndex == line && currentLineTokens != null) {
			tokens = currentLineTokens;
		} else {
			currentLineIndex = line;
			tokens = currentLineTokens = getDocument().markTokens(lineSegment, line);
		}

		Font defaultFont = getFont();
		SyntaxStyle[] styles = getStyles();

		byte id = tokens.id;
		while ((id = tokens.id) != Token.END) {
			if (id == Token.NULL) {
				fm = getFontMetrics();
			} else {
				fm = styles[id].getFontMetrics(defaultFont);
			}

			int length = tokens.length;

			if (offset + segmentOffset < lineSegment.offset + length) {
				lineSegment.count = offset - (lineSegment.offset - segmentOffset);
				return x + Utilities.getTabbedTextWidth(lineSegment, fm, x, this, 0);
			} else {
				lineSegment.count = length;
				x += Utilities.getTabbedTextWidth(lineSegment, fm, x, this, 0);
				lineSegment.offset += length;
			}
			tokens = tokens.next;
		}
		return x;
	}

	/**
	 * Converts an x co-ordinate to an offset within a line.
	 * 
	 * @param line
	 *            The line
	 * @param x
	 *            The x co-ordinate
	 * @return The computed document offset.
	 */
	public int xToOffset(int line, int x) {

		x = x - _gutterWidth;

		/* Use painter's cached info for speed */
		FontMetrics fm = getFontMetrics();

		getLineText(line, lineSegment);

		char[] segmentArray = lineSegment.array;
		int segmentOffset = lineSegment.offset;

		int width = 0;

		Token tokens;
		if (currentLineIndex == line && currentLineTokens != null) {
			tokens = currentLineTokens;
		} else {
			currentLineIndex = line;
			tokens = currentLineTokens = getDocument().markTokens(lineSegment, line);
		}

		int offset = 0;
		Font defaultFont = getFont();
		SyntaxStyle[] styles = getStyles();

		byte id = tokens.id;
		while ((id = tokens.id) != Token.END) {
			if (id == Token.NULL) {
				fm = getFontMetrics();
			} else {
				fm = styles[id].getFontMetrics(defaultFont);
			}

			int length = tokens.length;

			for (int i = 0; i < length; i++) {
				char c = segmentArray[segmentOffset + offset + i];
				int charWidth;
				if (c == '\t') {
					charWidth = (int) nextTabStop(width, offset + i) - width;
				} else {
					charWidth = fm.charWidth(c);
				}

				if (x - charWidth / 2 <= width) {
					return offset + i;
				}

				width += charWidth;
			}

			offset += length;
			tokens = tokens.next;
		}
		return offset;
	}

	/**
	 * Converts a point to an offset, from the start of the text.
	 * 
	 * @param x
	 *            The x co-ordinate of the point
	 * @param y
	 *            The y co-ordinate of the point
	 * @return The computed document offset.
	 */
	public int xyToOffset(int x, int y) {
		int line = yToLine(y);
		int start = getLineStartOffset(line);
		return start + xToOffset(line, x);
	}

	/**
	 * Returns the document this text area is editing.
	 * 
	 * @return The text document.
	 */
	public final TextDocument getDocument() {
		return document;
	}

	/**
	 * Sets the document this text area is editing.
	 * 
	 * @param document
	 *            The document
	 */
	public void setDocument(TextDocument document) {
		if (this.document == document) {
			return;
		}

		if (this.document != null) {
			this.document.stopUndo();
			this.document.removeDocumentListener(documentHandler);
			removeKeyBinding(KeyEvent.VK_Z, KeyEvent.CTRL_MASK);
			removeKeyBinding(KeyEvent.VK_Y, KeyEvent.CTRL_MASK);
		}
		this.document = document;
		addKeyBinding(KeyEvent.VK_Z, KeyEvent.CTRL_MASK, document.getUndoAction());
		addKeyBinding(KeyEvent.VK_Y, KeyEvent.CTRL_MASK, document.getRedoAction());
		document.addDocumentListener(documentHandler);
		document.startUndo();

		select(0, 0);
		repaint();
	}

	/**
	 * Returns the length of the document. Equivalent to calling <code>getDocument().getLength()</code>.
	 * 
	 * @return The document length.
	 */
	public final int getDocumentLength() {
		return document.getLength();
	}

	/**
	 * Returns the number of lines from the document.
	 * 
	 * @return The number of visible lines.
	 */
	public final int getVisibleLines() {
		Dimension viewportSize = null;
		Component parent = getParent();
		if ((parent != null) && (parent instanceof JViewport)) {
			JViewport vp = (JViewport) parent;
			viewportSize = vp.getSize();
		} else {
			viewportSize = getSize();
		}
		int visibleLines = viewportSize.height / getLineHeight();
		return visibleLines;
	}

	/**
	 * Returns the number of lines from the document.
	 * 
	 * @return The number of document lines.
	 */
	public final int getLineCount() {
		return document.getHeight();
	}

	/**
	 * Returns the number of columns from the document.
	 * 
	 * @return The number of document columns.
	 */
	public final int getColumnCount() {
		return document.getWidth();
	}

	/**
	 * Returns the line containing the specified offset.
	 * 
	 * @param offset
	 *            The document offset.
	 * @return The computed line index.
	 */
	public final int getLineOfOffset(int offset) {
		return document.getDefaultRootElement().getElementIndex(offset);
	}

	/**
	 * Returns the start offset of the specified line.
	 * 
	 * @param line
	 *            The line
	 * @return The start offset of the specified line, or -1 if the line is invalid
	 */
	public int getLineStartOffset(int line) {
		Element lineElement = document.getDefaultRootElement().getElement(line);
		if (lineElement == null) {
			return -1;
		} else {
			return lineElement.getStartOffset();
		}
	}

	/**
	 * Returns the end offset of the specified line.
	 * 
	 * @param line
	 *            The line
	 * @return The end offset of the specified line, or -1 if the line is invalid.
	 */
	public int getLineEndOffset(int line) {
		Element lineElement = document.getDefaultRootElement().getElement(line);
		if (lineElement == null) {
			return -1;
		} else {
			return lineElement.getEndOffset();
		}
	}

	/**
	 * Returns the length of the specified line.
	 * 
	 * @param line
	 *            The line index.
	 * @return The length of the given line.
	 */
	public int getLineLength(int line) {
		Element lineElement = document.getDefaultRootElement().getElement(line);
		if (lineElement == null) {
			return -1;
		} else {
			return lineElement.getEndOffset() - lineElement.getStartOffset() - 1;
		}
	}

	/**
	 * Points the curser to the begin of the given line.
	 * 
	 * @param lineNumber
	 *            The line to go to.
	 * @return true if the line exists, else false.
	 */
	public boolean gotoLine(int lineNumber) {
		Element lineElement = document.getDefaultRootElement().getElement(lineNumber);
		if (lineElement == null) {
			return false;
		}
		this.select(lineElement.getStartOffset(), lineElement.getStartOffset());
		return true;
	}

	/**
	 * Returns the entire text of this text area.
	 * 
	 * @return The content of the document.
	 */
	public String getText() {
		try {
			return document.getText(0, document.getLength());
		} catch (BadLocationException bl) {
			bl.printStackTrace();
			return null;
		}
	}

	/**
	 * Sets the entire text of this text area.
	 * 
	 * @param text
	 *            The text to set.
	 */
	public void setText(String text) {
		try {
			document.stopUndo();
			document.replace(0, document.getLength(), text, null, true);
			setCaretPosition(0);
			document.startUndo();
			scrollRectToVisible(new Rectangle(0, 0, 0, 0));
			invalidate();
		} catch (BadLocationException bl) {
			bl.printStackTrace();
		}
	}

	/**
	 * Returns the specified substring of the document.
	 * 
	 * @param start
	 *            The start offset
	 * @param len
	 *            The length of the substring
	 * @return The substring, or null if the offsets are invalid
	 */
	public final String getText(int start, int len) {
		try {
			return document.getText(start, len);
		} catch (BadLocationException bl) {
			bl.printStackTrace();
			return null;
		}
	}

	/**
	 * Copies the specified substring of the document into a segment. If the offsets are invalid, the segment will contain a null string.
	 * 
	 * @param start
	 *            The start offset
	 * @param len
	 *            The length of the substring
	 * @param segment
	 *            The segment
	 */
	public final void getText(int start, int len, Segment segment) {
		try {
			document.getText(start, len, segment);
		} catch (BadLocationException bl) {
			bl.printStackTrace();
			segment.offset = segment.count = 0;
		}
	}

	/**
	 * Returns the text on the specified line.
	 * 
	 * @param lineIndex
	 *            The line
	 * @return The text, or null if the line is invalid
	 */
	public final String getLineText(int lineIndex) {
		int start = getLineStartOffset(lineIndex);
		return getText(start, getLineEndOffset(lineIndex) - start - 1);
	}

	/**
	 * Copies the text on the specified line into a segment. If the line is invalid, the segment will contain a null string.
	 * 
	 * @param lineIndex
	 *            The line index.
	 * @param segment
	 *            The document segment.
	 */
	public final void getLineText(int lineIndex, Segment segment) {
		int start = getLineStartOffset(lineIndex);
		getText(start, getLineEndOffset(lineIndex) - start - 1, segment);
	}

	/**
	 * Returns the selection start offset.
	 * 
	 * @return The selection start index.
	 */
	public final int getSelectionStart() {
		return selectionStart;
	}

	/**
	 * Returns the offset where the selection starts on the specified
	 * 
	 * @param line
	 *            The line to detemine the selection start for.
	 * @return The selection start index.
	 */
	public int getSelectionStart(int line) {
		if (line == selectionStartLine) {
			return selectionStart;
		} else {
			return getLineStartOffset(line);
		}
	}

	/**
	 * Returns the selection start line.
	 * 
	 * @return The selection start line.
	 */
	public final int getSelectionStartLine() {
		return selectionStartLine;
	}

	/**
	 * Sets the selection start. The new selection will be the new selection start and the old selection end.
	 * 
	 * @param selectionStart
	 *            The selection start
	 * @see #select(int,int)
	 */
	public final void setSelectionStart(int selectionStart) {
		select(selectionStart, selectionEnd);
	}

	/**
	 * Returns the selection end offset.
	 * 
	 * @return The index of the last selected element.
	 */
	public final int getSelectionEnd() {
		return selectionEnd;
	}

	/**
	 * Returns the offset where the selection ends on the specified
	 * 
	 * @param line
	 *            The line to detemine the selection end for.
	 * @return The index of the last selected element within the given line.
	 */
	public int getSelectionEnd(int line) {
		if (line == selectionEndLine) {
			return selectionEnd;
		} else {
			return getLineEndOffset(line) - 1;
		}
	}

	/**
	 * Returns the selection end line.
	 * 
	 * @return The selection end line.
	 */
	public final int getSelectionEndLine() {
		return selectionEndLine;
	}

	/**
	 * Sets the selection end. The new selection will be the old selection start and the bew selection end.
	 * 
	 * @param selectionEnd
	 *            The selection end
	 * @see #select(int,int)
	 */
	public final void setSelectionEnd(int selectionEnd) {
		select(selectionStart, selectionEnd);
	}

	/**
	 * Returns the caret position. This will either be the selection start or the selection end, depending on which direction the selection was made in.
	 * 
	 * @return The caret position index.
	 */
	public final int getCaretPosition() {
		return (biasLeft ? selectionStart : selectionEnd);
	}

	/**
	 * Returns the caret line.
	 * 
	 * @return The caret line index.
	 */
	public final int getCaretLine() {
		return (biasLeft ? selectionStartLine : selectionEndLine);
	}

	/**
	 * Returns the mark position. This will be the opposite selection bound to the caret position.
	 * 
	 * @return The position of the marked character.
	 * @see #getCaretPosition()
	 */
	public final int getMarkPosition() {
		return (biasLeft ? selectionEnd : selectionStart);
	}

	/**
	 * Returns the mark line.
	 * 
	 * @return The marked line position.
	 */
	public final int getMarkLine() {
		return (biasLeft ? selectionEndLine : selectionStartLine);
	}

	/**
	 * Sets the caret position. The new selection will consist of the caret position only (hence no text will be selected)
	 * 
	 * @param caret
	 *            The caret position
	 * @see #select(int,int)
	 */
	public final void setCaretPosition(int caret) {
		select(caret, caret);
	}

	/**
	 * Selects all text in the document.
	 */
	public final void selectAll() {
		select(0, getDocumentLength());
	}

	/**
	 * Moves the mark to the caret position.
	 */
	public final void selectNone() {
		select(getCaretPosition(), getCaretPosition());
	}

	/**
	 * Selects from the start offset to the end offset. This is the general selection method used by all other selecting methods. The caret position will be start if start &lt; end, and end if end
	 * &gt; start.
	 * 
	 * @param start
	 *            The start offset
	 * @param end
	 *            The end offset
	 */
	public void select(int start, int end) {
		int newStart, newEnd;
		boolean newBias;
		if (start <= end) {
			newStart = start;
			newEnd = end;
			newBias = false;
		} else {
			newStart = end;
			newEnd = start;
			newBias = true;
		}

		if (newStart < 0 || newEnd > getDocumentLength()) {
			throw new IllegalArgumentException("Bounds out of" + " range: " + newStart + "," + newEnd);
		}

		// If the new position is the same as the old, we don't
		// do all this crap, however we still do the stuff at
		// the end (clearing magic position, scrolling)
		if (newStart != selectionStart || newEnd != selectionEnd || newBias != biasLeft) {
			int newStartLine = getLineOfOffset(newStart);
			int newEndLine = getLineOfOffset(newEnd);

			if (isBracketHighlightEnabled()) {
				if (bracketLine != -1) {
					invalidateLine(bracketLine);
				}
				updateBracketHighlight(end);
				if (bracketLine != -1) {
					invalidateLine(bracketLine);
				}
			}

			invalidateLineRange(selectionStartLine, selectionEndLine);
			invalidateLineRange(newStartLine, newEndLine);

			selectionStart = newStart;
			selectionEnd = newEnd;
			selectionStartLine = newStartLine;
			selectionEndLine = newEndLine;
			biasLeft = newBias;

			fireCaretEvent();
		}

		// When the user is typing, etc, we don't want the caret
		// to blink
		blink = true;
		caretTimer.restart();

		// Clear the `magic' caret position used by up/down
		magicCaret = -1;
	}

	/**
	 * Returns the selected text, or null if no selection is active.
	 * 
	 * @return The selected text.s
	 */
	public final String getSelectedText() {
		if (selectionStart == selectionEnd) {
			return null;
		}

		return getText(selectionStart, selectionEnd - selectionStart);
	}

	/**
	 * Inserts the given text into the document. If a text area is currently selected, this area gets overwritten with the given text.
	 * 
	 * @param text
	 *            The text to be inserted.
	 */
	public void insertText(String text) {
		if (!editable) {
			throw new InternalError("Text component" + " read only");
		}

		try {
			if (text != null) {
				if ((selectionEnd - selectionStart) == 0) {
					document.insertString(selectionStart, text, null, true);
				} else {
					document.replace(selectionStart, selectionEnd - selectionStart, text, null, true);
				}
			} else {
				document.remove(selectionStart, selectionEnd - selectionStart, true);
			}
		} catch (BadLocationException bl) {
			bl.printStackTrace();
			throw new InternalError("Cannot replace" + " selection");
		}

		setCaretPosition(selectionEnd);
	}

	/**
	 * Gets the editable flag.
	 * 
	 * @return true if this text area is editable, false otherwise.
	 */
	public final boolean isEditable() {
		return editable;
	}

	/**
	 * Sets if this component is editable.
	 * 
	 * @param editable
	 *            True if this text area should be editable, false otherwise
	 */
	public final void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * Returns the right click popup menu.
	 * 
	 * @return The popup menu to be opened on right click.s
	 */
	public final JPopupMenu getRightClickPopup() {
		return popup;
	}

	/**
	 * Sets the right click popup menu.
	 * 
	 * @param popup
	 *            The popup
	 */
	public final void setRightClickPopup(JPopupMenu popup) {
		this.popup = popup;
	}

	/**
	 * Returns the `magic' caret position. This can be used to preserve the column position when moving up and down lines.
	 * 
	 * @return The magic caret position.s
	 */
	public final int getMagicCaretPosition() {
		return magicCaret;
	}

	/**
	 * Sets the `magic' caret position. This can be used to preserve the column position when moving up and down lines.
	 * 
	 * @param magicCaret
	 *            The magic caret position
	 */
	public final void setMagicCaretPosition(int magicCaret) {
		this.magicCaret = magicCaret;
	}

	public void insertTab(int offset) {
		int lineoffset = offset - getLineStartOffset(getLineOfOffset(offset));
		int count = 0;
		StringBuilder strTab = new StringBuilder();
		while (count == 0 || ((lineoffset + count) % tabSize != 0)) {
			strTab.append(' ');
			count++;
		}
		try {
			getDocument().insertString(offset, strTab.toString(), null);
		} catch (BadLocationException blex) {
			blex.printStackTrace();
		}
	}

	/**
	 * Similar to <code>setSelectedText()</code>, but overstrikes the appropriate number of characters if overwrite mode is enabled.
	 * 
	 * @param str
	 *            The string to overwrite the selected area width.
	 * @see #isOverwriteEnabled()
	 */
	public void overwriteSetSelectedText(String str) {
		// Don't overstrike if there is a selection
		if (!overwrite || selectionStart != selectionEnd) {
			insertText(str);
			return;
		}

		// Don't overstrike if we're on the end of
		// the line
		int caret = getCaretPosition();
		int caretLineEnd = getLineEndOffset(getCaretLine());
		if (caretLineEnd - caret <= str.length()) {
			insertText(str);
			return;
		}

		try {
			document.replace(caret, str.length(), str, null, true);
		} catch (BadLocationException bl) {
			bl.printStackTrace();
		}
	}

	/**
	 * Returns true if overwrite mode is enabled, false otherwise.
	 * 
	 * @return The current value of the overwriteEnabled flag.
	 */
	public final boolean isOverwriteEnabled() {
		return overwrite;
	}

	/**
	 * Sets if overwrite mode should be enabled.
	 * 
	 * @param overwrite
	 *            True if overwrite mode should be enabled, false otherwise.
	 */
	public final void setOverwriteEnabled(boolean overwrite) {
		this.overwrite = overwrite;
		invalidateSelectedLines();
	}

	/**
	 * Returns the position of the highlighted bracket (the bracket matching the one before the caret)
	 * 
	 * @return The index of the bracket position in the document.
	 */
	public final int getBracketPosition() {
		return bracketPosition;
	}

	/**
	 * Returns the line of the highlighted bracket (the bracket matching the one before the caret)
	 * 
	 * @return The index of the bracket line.
	 */
	public final int getBracketLine() {
		return bracketLine;
	}

	/**
	 * Adds a caret change listener to this text area.
	 * 
	 * @param listener
	 *            The listener
	 */
	public final void addCaretListener(CaretListener listener) {
		listenerList.add(CaretListener.class, listener);
	}

	/**
	 * Removes a caret change listener from this text area.
	 * 
	 * @param listener
	 *            The listener
	 */
	public final void removeCaretListener(CaretListener listener) {
		listenerList.remove(CaretListener.class, listener);
	}

	/**
	 * Deletes the selected text from the text area and places it into the clipboard.
	 */
	public void cut() {
		if (editable) {
			copy();
			insertText("");
		}
	}

	/**
	 * Places the selected text into the clipboard.
	 */
	public void copy() {
		if (selectionStart != selectionEnd) {
			Clipboard clipboard = getToolkit().getSystemClipboard();
			String selection = getSelectedText();
			clipboard.setContents(new StringSelection(selection), null);
		}
	}

	/**
	 * Inserts the clipboard contents into the text.
	 */
	public void paste() {
		if (editable) {
			Clipboard clipboard = getToolkit().getSystemClipboard();
			try {
				String selection = ((String) clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor));
				insertText(selection);
			} catch (Exception e) {
				getToolkit().beep();
				System.err.println("Clipboard does not" + " contain a string");
			}
		}
	}

	/**
	 * Finds the search string in the document and moves the cursor to it.
	 * 
	 * @param searchString
	 *            The text to find.
	 */
	public boolean find(SearchOptions options) {
		
		int caretLine = getCaretLine();
		int caretPosition = getCaretPosition();
		int lastLine = getLineCount();
		boolean found = false;
		int i = caretLine;
		int indexOfSearch = 0;
		String lineText = null;
		String searchString = options.getSearchTerm();
		if (!options.isCaseSensitive()) {
			searchString = searchString.toLowerCase();
		}

		if (options.getSearchDirection() == SearchDirection.Forward) {
			int indexIntoLine = Math.max(caretPosition - getLineStartOffset(caretLine), 0);
			while (!found && i < lastLine) {
				lineText = getLineText(i);
				if (!options.isCaseSensitive()) {
					lineText = lineText.toLowerCase();
				}
				if ((indexOfSearch = lineText.indexOf(searchString, indexIntoLine)) >= 0) {
					found = true;
				} else {
					indexIntoLine = 0;
					++i;
				}
			}
			if (found) {
				caretPosition = getLineStartOffset(i) + indexOfSearch;
				setSelectionEnd(caretPosition + searchString.length());
				setSelectionStart(caretPosition);
			}
		} else {
			// int indexIntoLine = caretPosition - getLineEndOffset(caretLine);
			int selectionStart = getSelectionStart(caretLine);
			if (selectionStart >= 0) {
				caretPosition = selectionStart;
			}
			int indexIntoLine = caretPosition - getLineStartOffset(caretLine);
			while (!found && i >= 0) {
				lineText = getLineText(i);
				if (!options.isCaseSensitive()) {
					lineText = lineText.toLowerCase();
				}
				if (i != caretLine) {
					indexIntoLine = lineText.length() - 1;
				}
				if ((indexOfSearch = indexOfReverse(lineText, indexIntoLine, searchString)) >= 0) {
					found = true;
				} else {
					--i;
				}
			}
			if (found) {
				caretPosition = getLineStartOffset(i) + indexOfSearch;
				setSelectionStart(caretPosition);
				setSelectionEnd(caretPosition + searchString.length());
			}
		}

		if (!found && options.isWrappedSearch()) {
			// seach document from start/end (depends on direction) to caret
			// position
			if (options.getSearchDirection() == SearchDirection.Forward) {
				i = 0;
				int indexIntoLine = 0; // getLineStartOffset(caretLine);
				while (!found && i <= caretLine) {
					lineText = getLineText(i);
					if (!options.isCaseSensitive()) {
						lineText = lineText.toLowerCase();
					}
					if ((indexOfSearch = lineText.indexOf(searchString, indexIntoLine)) >= 0) {
						found = true;
					} else {
						indexIntoLine = 0;
						++i;
					}
				}
				if (found) {
					caretPosition = getLineStartOffset(i) + indexOfSearch;
					setSelectionStart(caretPosition);
					setSelectionEnd(caretPosition + searchString.length());
				}
			} else {
				i = lastLine - 1;
				int indexIntoLine = caretPosition - getLineStartOffset(caretLine);
				while (!found && i >= caretLine) {
					lineText = getLineText(i);
					if (!options.isCaseSensitive()) {
						lineText = lineText.toLowerCase();
					}
					indexIntoLine = lineText.length() - 1;
					if ((indexOfSearch = indexOfReverse(lineText, indexIntoLine, searchString)) >= 0) {
						found = true;
					} else {
						indexIntoLine = 0;
						--i;
					}
				}
				if (found) {
					caretPosition = getLineStartOffset(i) + indexOfSearch;
					setSelectionEnd(caretPosition + searchString.length());
					setSelectionStart(caretPosition);
				}
			}
		}
		return found;
	}

	/**
	 * Searches reversely the given searchText in the given text.
	 * 
	 * @param text
	 *            The text to search.
	 * @param offset
	 *            The offset to search from.
	 * @param searchText
	 *            The text to search.
	 * @return The index of the matched region.
	 */
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

	/**
	 * Replaces the currently selected area with the given text.
	 * 
	 * @param textToReplaceWith
	 *            The text to replace the current selection with.
	 */
	public void replace(String textToReplaceWith) {
		int selectionStart = getSelectionStart();
		int selectionEnd = getSelectionEnd();
		try {
			getDocument().replace(selectionStart, selectionEnd - selectionStart, textToReplaceWith, null, true);
			setSelectionStart(selectionStart);
			setSelectionEnd(selectionStart + textToReplaceWith.length());
		} catch (BadLocationException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	/**
	 * Replaces the currently selected area with the given text.
	 * 
	 * @param searchString
	 *            The text to replace.
	 * @param textToReplaceWith
	 *            The text to replace the search string with.
	 * @return The number of occurrences that have been replaced.
	 */
	public int replaceAll(String searchString, String textToReplaceWith, SearchOptions options) {

		int searchStringLen = searchString.length();
		int replaceStringLen = textToReplaceWith.length();
		int srDiff = searchStringLen - replaceStringLen;

		boolean caseSensitive = options.isCaseSensitive();
		if (!caseSensitive) {
			searchString = searchString.toLowerCase();
		}
		char[] searchChars = searchString.toCharArray();

		int caretPosition = getCaretPosition();
		int selectionStart = getSelectionStart();
		int selectionEnd = getSelectionEnd();
		int newCaretPosition = caretPosition;
		int newSelectionStart = selectionStart;
		int newSelectionEnd = selectionEnd;

		String text = getText();
		String searchText = text;
		if (!caseSensitive) {
			searchText = text.toLowerCase();
		}
		int textLen = text.length();

		int replaceCount = 0;
		try {
			StringBuffer textBuf = new StringBuffer();
			outer: for (int i = 0; i < textLen; i++) {
				char searchChar = searchText.charAt(i);
				char c = text.charAt(i);

				if (searchChar == searchChars[0]) {
					for (int j = 1; (i + j < textLen) && (j < searchStringLen); j++) {
						if (searchChars[j] != searchText.charAt(i + j)) {
							textBuf.append(c);
							continue outer;
						}
					}
					// OK, match detected!
					textBuf.append(textToReplaceWith);
					i += searchStringLen - 1;
					++replaceCount;
					if (srDiff != 0) {
						if (i < caretPosition) {
							newCaretPosition -= srDiff;
						}
						if (i < selectionStart) {
							newSelectionStart -= srDiff;
						}
						if (i < selectionEnd) {
							newSelectionEnd -= srDiff;
						}
					}
					continue;
				}
				textBuf.append(c);
			}

			// replacing text
			document.replace(0, textLen, textBuf.toString(), null, true);
			setCaretPosition(newCaretPosition);
			setSelectionEnd(newSelectionEnd);
			setSelectionStart(newSelectionStart);
		} catch (BadLocationException ex) {
			throw new RuntimeException(ex.getMessage());
		}
		return replaceCount;
	}

	/**
	 * Sets the default key bindings.
	 */
	public void setDefaultKeyBindings() {
		ActionListener act = new BackspaceKeyAction(this);
		addKeyBinding(KeyEvent.VK_BACK_SPACE, 0, act);
		addKeyBinding(KeyEvent.VK_BACK_SPACE, KeyEvent.CTRL_MASK, act);

		act = new DeleteKeyAction(this);
		addKeyBinding(KeyEvent.VK_DELETE, 0, act);
		addKeyBinding(KeyEvent.VK_DELETE, KeyEvent.CTRL_MASK, act);

		addKeyBinding(KeyEvent.VK_ENTER, 0, new EnterKeyAction(this));
		addKeyBinding(KeyEvent.VK_TAB, 0, new TabKeyAction(this));
		addKeyBinding(KeyEvent.VK_TAB, KeyEvent.SHIFT_MASK, new TabKeyAction(this));
		addKeyBinding(KeyEvent.VK_INSERT, 0, new InsertKeyAction(this));

		act = new CommentBlockAction(this);
		addKeyBinding(KeyEvent.VK_MINUS, KeyEvent.CTRL_MASK, act);
		addKeyBinding(KeyEvent.VK_MINUS, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK, act);

		act = new HomeKeyAction(this);
		addKeyBinding(KeyEvent.VK_HOME, 0, act);
		addKeyBinding(KeyEvent.VK_HOME, KeyEvent.SHIFT_MASK, act);
		addKeyBinding(KeyEvent.VK_HOME, KeyEvent.CTRL_MASK, act);
		addKeyBinding(KeyEvent.VK_HOME, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK, act);

		act = new EndKeyAction(this);
		addKeyBinding(KeyEvent.VK_END, 0, act);
		addKeyBinding(KeyEvent.VK_END, KeyEvent.SHIFT_MASK, act);
		addKeyBinding(KeyEvent.VK_END, KeyEvent.CTRL_MASK, act);
		addKeyBinding(KeyEvent.VK_END, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK, act);

		act = new PageUpKeyAction(this);
		addKeyBinding(KeyEvent.VK_PAGE_UP, 0, act);
		addKeyBinding(KeyEvent.VK_PAGE_UP, KeyEvent.SHIFT_MASK, act);

		act = new PageDownKeyAction(this);
		addKeyBinding(KeyEvent.VK_PAGE_DOWN, 0, act);
		addKeyBinding(KeyEvent.VK_PAGE_DOWN, KeyEvent.SHIFT_MASK, act);

		act = new LeftKeyAction(this);
		addKeyBinding(KeyEvent.VK_LEFT, 0, act);
		addKeyBinding(KeyEvent.VK_LEFT, KeyEvent.SHIFT_MASK, act);
		addKeyBinding(KeyEvent.VK_LEFT, KeyEvent.CTRL_MASK, act);
		addKeyBinding(KeyEvent.VK_LEFT, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK, act);

		act = new RightKeyAction(this);
		addKeyBinding(KeyEvent.VK_RIGHT, 0, act);
		addKeyBinding(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_MASK, act);
		addKeyBinding(KeyEvent.VK_RIGHT, KeyEvent.CTRL_MASK, act);
		addKeyBinding(KeyEvent.VK_RIGHT, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK, act);

		act = new UpKeyAction(this);
		addKeyBinding(KeyEvent.VK_UP, 0, act);
		addKeyBinding(KeyEvent.VK_UP, KeyEvent.SHIFT_MASK, act);

		act = new DownKeyAction(this);
		addKeyBinding(KeyEvent.VK_DOWN, 0, act);
		addKeyBinding(KeyEvent.VK_DOWN, KeyEvent.SHIFT_MASK, act);

		addKeyBinding(KeyEvent.VK_C, KeyEvent.CTRL_MASK, new CopyKeyAction(this));
		addKeyBinding(KeyEvent.VK_X, KeyEvent.CTRL_MASK, new CutKeyAction(this));
		addKeyBinding(KeyEvent.VK_DELETE, KeyEvent.SHIFT_MASK, new CutKeyAction(this));
		addKeyBinding(KeyEvent.VK_V, KeyEvent.CTRL_MASK, new PasteKeyAction(this));
		addKeyBinding(KeyEvent.VK_INSERT, KeyEvent.SHIFT_MASK, new PasteKeyAction(this));
		addKeyBinding(KeyEvent.VK_F, KeyEvent.CTRL_MASK, new FindKeyAction(this));
		addKeyBinding(KeyEvent.VK_F3, 0, new FindNextAction(this));

		act = new SelectAllAction(this);
		addKeyBinding(KeyEvent.VK_A, KeyEvent.CTRL_MASK, act);

		act = new GotoLineKeyAction(this);
		addKeyBinding(KeyEvent.VK_L, KeyEvent.CTRL_MASK, act);

		addKeyBinding(KeyEvent.VK_W, KeyEvent.CTRL_MASK, new ToggleShowWhitespaceAction(this));

		addKeyBinding(KeyEvent.VK_N, KeyEvent.CTRL_MASK, new ToggleLineNumbersAction(this));
	}

	/**
	 * Adds a key binding to this input handler.
	 * 
	 * @param keyCode
	 *            The key code.
	 * @param modifiers
	 *            The key modifiers.
	 * @param action
	 *            The action
	 */
	public void addKeyBinding(int keyCode, int modifiers, ActionListener action) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
		keyBindings.put(keyStroke, action);
	}

	/**
	 * Removes a key binding from this input handler.
	 * 
	 * @param keyCode
	 *            The key code.
	 * @param modifiers
	 *            The key modifiers.
	 */
	public void removeKeyBinding(int keyCode, int modifiers) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
		keyBindings.remove(keyStroke);
	}

	/**
	 * Removes all key bindings from this input handler.
	 */
	public void removeAllKeyBindings() {
		keyBindings.clear();
	}

	/**
	 * Forwards key events directly to the input handler. This is slightly faster than using a KeyListener because some Swing overhead is avoided.
	 * 
	 * @param evt
	 *            The key event to process.
	 */
	public void processKeyEvent(KeyEvent evt) {
		int keyCode = evt.getKeyCode();
		int modifiers = evt.getModifiers();

		switch (evt.getID()) {
		case KeyEvent.KEY_TYPED:
			char c = evt.getKeyChar();
			if (c != KeyEvent.CHAR_UNDEFINED && (modifiers & KeyEvent.ALT_MASK) == 0) {
				if (c >= 0x20 && c != 0x7f) {
					KeyStroke keyStroke = KeyStroke.getKeyStroke(Character.toUpperCase(c));
					ActionListener act = (ActionListener) keyBindings.get(keyStroke);
					if (act == null) {
						act = insertCharAction;
					}
					if (!executeInputAction(act, evt.getSource(), String.valueOf(evt.getKeyChar()), evt.getModifiers())) {
						super.processKeyEvent(evt);
					}
				}
			}
			break;

		case KeyEvent.KEY_PRESSED:
			if (keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_ALT || keyCode == KeyEvent.VK_META)
				return;

			if ((modifiers & ~KeyEvent.SHIFT_MASK) != 0 || evt.isActionKey() || keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_DELETE || keyCode == KeyEvent.VK_ENTER
					|| keyCode == KeyEvent.VK_TAB || keyCode == KeyEvent.VK_ESCAPE) {

				KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
				ActionListener act = (ActionListener) keyBindings.get(keyStroke);
				if (executeInputAction(act, evt.getSource(), null, evt.getModifiers())) {
					evt.consume();
				} else {
					super.processKeyEvent(evt);
				}
			}
			break;

		default:
			super.processKeyEvent(evt);
		}
	}

	/**
	 * Executes the specified action, repeating and recording it as necessary.
	 * 
	 * @param listener
	 *            The action listener
	 * @param source
	 *            The event source
	 * @param actionCommand
	 *            The action command
	 * @param modifiers
	 *            The key modifiers.
	 * @return true, if the action has been executed, else false.
	 */
	public boolean executeInputAction(ActionListener listener, Object source, String actionCommand, int modifiers) {
		if (listener != null) {
			// create event
			ActionEvent evt = new ActionEvent(source, ActionEvent.ACTION_PERFORMED, actionCommand, modifiers);
			// execute the action
			listener.actionPerformed(evt);
			revalidate();
			repaint();
			return true;
		}
		return false;
	}

	protected void fireCaretEvent() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i--) {
			if (listeners[i] == CaretListener.class) {
				((CaretListener) listeners[i + 1]).caretUpdate(caretEvent);
			}
		}
	}

	protected void updateBracketHighlight(int newCaretPosition) {
		if (newCaretPosition == 0) {
			bracketPosition = bracketLine = -1;
			return;
		}

		try {
			int offset = TextUtilities.findMatchingBracket(document, newCaretPosition - 1);
			if (offset != -1) {
				bracketLine = getLineOfOffset(offset);
				bracketPosition = offset - getLineStartOffset(bracketLine);
				return;
			}
		} catch (BadLocationException bl) {
			bl.printStackTrace();
		}

		bracketLine = bracketPosition = -1;
	}

	protected int documentChanged(DocumentEvent evt) {
		DocumentEvent.ElementChange ch = evt.getChange(document.getDefaultRootElement());

		int count;
		if (ch == null) {
			count = 0;
		} else {
			count = ch.getChildrenAdded().length - ch.getChildrenRemoved().length;
		}

		int line = getLineOfOffset(evt.getOffset());
		if (count == 0) {
			invalidateLine(line);
		}
		return line;
	}

	/**
	 * Returns the height of one single line.
	 * 
	 * @return The height of one line.
	 */
	public int getLineHeight() {
		return fm.getHeight();
	}

	/**
	 * Returns the width of one single character.
	 * 
	 * @return The width of one single character.
	 */
	public int getColumnWidth() {
		return fm.charWidth('w');
	}

	/**
	 * Returns the painter's preferred size.
	 * 
	 * @return The preferred size of the component.
	 */
	public Dimension getPreferredSize() {
		Dimension dim = new Dimension();
		int parentWidth = 0;
		int parentHeight = 0;
		if (getParent() != null) {
			parentWidth = getParent().getWidth();
			parentHeight = getParent().getHeight();
		}
		dim.width = Math.max(getColumnWidth() * getColumnCount() + 5 + _gutterWidth, parentWidth);
		dim.height = Math.max(getLineHeight() * getLineCount() + 5, parentHeight);
		return dim;
	}

	/**
	 * Returns the painter's minimum size.
	 * 
	 * @return The minimum size of the component.
	 */
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	/**
	 * Returns the preferred size of the viewport for a view component.
	 * 
	 * @return The preferredSize of a JViewport whose view is this Scrollable.
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	/**
	 * Components that display logical rows or columns should compute the scroll increment that will completely expose one new row or column, depending on the value of orientation. Ideally, components
	 * should handle a partially exposed row or column by returning the distance required to completely expose the item.
	 * <p>
	 * Scrolling containers, like JScrollPane, will use this method each time the user requests a unit scroll.
	 * 
	 * @param visibleRect
	 *            The view area visible within the viewport
	 * @param orientation
	 *            Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
	 * @param direction
	 *            Less than zero to scroll up/left, greater than zero for down/right.
	 * @return The "unit" increment for scrolling in the specified direction. This value should always be positive.
	 */
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return getLineHeight();
	}

	/**
	 * Components that display logical rows or columns should compute the scroll increment that will completely expose one block of rows or columns, depending on the value of orientation.
	 * <p>
	 * Scrolling containers, like JScrollPane, will use this method each time the user requests a block scroll.
	 * 
	 * @param visibleRect
	 *            The view area visible within the viewport
	 * @param orientation
	 *            Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
	 * @param direction
	 *            Less than zero to scroll up/left, greater than zero for down/right.
	 * @return The "block" increment for scrolling in the specified direction. This value should always be positive.
	 */
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return getLineHeight() * 3;
	}

	/**
	 * Return true if a viewport should always force the width of this <code>Scrollable</code> to match the width of the viewport.
	 * 
	 * @return True if a viewport should force the Scrollables width to match its own.
	 */
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	/**
	 * Return true if a viewport should always force the height of this Scrollable to match the height of the viewport.
	 * 
	 * @return True if a viewport should force the Scrollables height to match its own.
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	/**
	 * Paints one single line.
	 * 
	 * @param gfx
	 *            The graphics object.
	 * @param line
	 *            The line number.
	 */
	protected void paintLine(Graphics gfx, int line) {
		Font defaultFont = getFont();
		Color defaultColor = getForeground();

		currentLineIndex = line;
		int y = lineToY(line);

		if ((line >= 0) && (line < getLineCount())) {
			getLineText(currentLineIndex, currentLine);
			currentLineTokens = getDocument().markTokens(currentLine, currentLineIndex);

			paintHighlight(gfx, line, y);
			y += fm.getHeight();

			gfx.setFont(defaultFont);
			gfx.setColor(defaultColor);
			if (_gutterWidth > 0) {
				String gutter = String.format("%5d", line + 1);
				gfx.drawString(gutter, 0, y);
			}

			int endPos = SyntaxUtilities.paintSyntaxLine(currentLine, currentLineTokens, styles, this, gfx, _gutterWidth, y, _showwhitespace);

			if (eolMarkers) {
				gfx.setColor(eolMarkerColor);
				gfx.setFont(defaultFont);
				gfx.drawString(EOL_MARKER, endPos, y);
			}
		}
	}

	private static final String EOL_MARKER = "" + (char) 928;

	/**
	 * Paints the highlighting stuff.
	 * 
	 * @param gfx
	 *            The graphics object.
	 * @param line
	 *            The line number.
	 * @param y
	 *            The vertical position.
	 */
	protected void paintHighlight(Graphics gfx, int line, int y) {
		if (line >= getSelectionStartLine() && line <= getSelectionEndLine()) {
			paintLineHighlight(gfx, line, y);
		}

		if (highlight != null) {
			highlight.paintHighlight(gfx, line, y);
		}

		if (bracketHighlight && line == getBracketLine()) {
			paintBracketHighlight(gfx, line, y);
		}

		if (line == getCaretLine()) {
			paintCaret(gfx, line, y);
		}
	}

	/**
	 * Paints the line highlight.
	 * 
	 * @param gfx
	 *            The graphics object.
	 * @param line
	 *            The line number.
	 * @param y
	 *            The vertical position.
	 */
	protected void paintLineHighlight(Graphics gfx, int line, int y) {
		int height = fm.getHeight();
		y += fm.getLeading() + fm.getMaxDescent();

		int selectionStart = getSelectionStart();
		int selectionEnd = getSelectionEnd();

		if (selectionStart == selectionEnd) {
			if (lineHighlight) {
				gfx.setColor(lineHighlightColor);
				gfx.fillRect(_gutterWidth, y, getWidth(), height);
			}
		} else {
			gfx.setColor(selectionColor);

			int selectionStartLine = getSelectionStartLine();
			int selectionEndLine = getSelectionEndLine();
			int lineStart = getLineStartOffset(line);

			int x1, x2;
			if (selectionStartLine == selectionEndLine) {
				x1 = _offsetToX(line, selectionStart - lineStart);
				x2 = _offsetToX(line, selectionEnd - lineStart);
			} else if (line == selectionStartLine) {
				x1 = _offsetToX(line, selectionStart - lineStart);
				x2 = getWidth();
			} else if (line == selectionEndLine) {
				x1 = _gutterWidth;
				x2 = _offsetToX(line, selectionEnd - lineStart);
			} else {
				x1 = _gutterWidth;
				x2 = getWidth();
			}

			// "inlined" min/max()
			gfx.fillRect(x1 > x2 ? x2 : x1, y, x1 > x2 ? (x1 - x2) : (x2 - x1), height);
		}

	}

	/**
	 * Paints the bracket highlight.
	 * 
	 * @param gfx
	 *            The graphics object.
	 * @param line
	 *            The bracket line.
	 * @param y
	 *            The vertical position.
	 */
	protected void paintBracketHighlight(Graphics gfx, int line, int y) {
		int position = getBracketPosition();
		if (position == -1)
			return;
		y += fm.getLeading() + fm.getMaxDescent();
		int x = _offsetToX(line, position);
		gfx.setColor(bracketHighlightColor);
		// Hack!!! Since there is no fast way to get the character
		// from the bracket matching routine, we use ( since all
		// brackets probably have the same width anyway
		gfx.drawRect(x, y, fm.charWidth('(') - 1, fm.getHeight() - 1);
	}

	/**
	 * Paints the caret.
	 * 
	 * @param gfx
	 *            The graphics object.
	 * @param line
	 *            The caret line.
	 * @param y
	 *            The vertical position.
	 */
	protected void paintCaret(Graphics gfx, int line, int y) {
		if (isCaretVisible()) {
			int offset = getCaretPosition() - getLineStartOffset(line);
			int caretX = _offsetToX(line, offset);
			int caretWidth = (isOverwriteEnabled() ? fm.charWidth('w') : 1);
			y += fm.getLeading() + fm.getMaxDescent();
			int height = fm.getHeight();

			gfx.setColor(caretColor);

			if (isOverwriteEnabled()) {
				gfx.fillRect(caretX, y + height - 1, caretWidth, 1);
			} else {
				gfx.drawRect(caretX, y, caretWidth - 1, height - 1);
			}
		}
	}

	/**
	 * Implementation of TabExpander interface. Returns next tab stop after a specified point.
	 * 
	 * @param x
	 *            The x co-ordinate
	 * @param tabOffset
	 *            Ignored
	 * @return The next tab stop after <i>x</i>
	 */
	public float nextTabStop(float x, int tabOffset) {
		int ntabs = ((int) x - _gutterWidth) / tabSizePixels;
		return ((ntabs + 1) * tabSizePixels) + _gutterWidth;
	}

	/**
	 * Doing layout update.
	 */
	void updateLayout() {
		doLayout();
		Dimension size = getPreferredSize();
		int x = 0;
		int y = 0;
		int width = size.width - x;
		int height = size.height - y;

		Component parent = getParent();
		Point viewPos = new Point(0, 0);
		if ((parent != null) && (parent instanceof JViewport)) {
			JViewport vp = (JViewport) parent;
			viewPos = vp.getViewPosition();
		}
		setBounds(x, y, width, height);
		if ((parent != null) && (parent instanceof JViewport)) {
			JViewport vp = (JViewport) parent;
			vp.setViewPosition(viewPos);
		}

		updateCaret();
	}

	/**
	 * Doing caret update.
	 */
	@SuppressWarnings("rawtypes")
	private void updateCaret() {
		int caretLine = getCaretLine();
		int caretColumn = getCaretPosition() - getLineStartOffset(caretLine);
		try {
			char[] lineChars = document.getLine(caretLine);
			int tabSize = document.getTabSize();
			int effectivePos = caretColumn;
			for (int j = 0; j < caretColumn; j++) {
				if (lineChars[j] == '\t') {
					effectivePos += (tabSize - 1);
				}
			}
			caretColumn = effectivePos;
		} catch (BadLocationException ex) {
			throw new RuntimeException(ex.getMessage());
		}
		int columnWidth = getColumnWidth();
		int lineHeight = getLineHeight();
		Point caretPos = new Point(columnWidth * caretColumn, lineHeight * caretLine);

		Rectangle visibleRect = getVisibleRect();
		Rectangle caretRect = new Rectangle(caretPos, new Dimension(columnWidth, lineHeight));
		if (!visibleRect.contains(caretRect)) {
			this.scrollRectToVisible(caretRect);
		} else {
			this.scrollRectToVisible(caretRect);
		}

		// invoking caret position listeners
		CaretPositionEvent cpe = new CaretPositionEvent(this, caretLine, caretColumn);
		Iterator caretPositionListenerIter = caretPositionListeners.iterator();
		while (caretPositionListenerIter.hasNext()) {
			((CaretPositionListener) caretPositionListenerIter.next()).positionUpdate(cpe);
		}

	}

	/**
	 * The caret blinker.
	 */
	class CaretBlinker implements ActionListener {

		/**
		 * Invoked when an action occurs.
		 * 
		 * @param evt
		 *            The event to process.
		 */
		public void actionPerformed(ActionEvent evt) {
			if (hasFocus()) {
				blinkCaret();
			}
		}
	}

	/**
	 * The mutable caret event.
	 */
	class MutableCaretEvent extends CaretEvent {

		/**
         *
         */
		private static final long serialVersionUID = 1L;

		/**
		 * Creates a new MutableCaretEvent object.
		 */
		public MutableCaretEvent() {
			super(CodeTextArea.this);
		}

		/**
		 * Fetches the location of the caret.
		 * 
		 * @return the dot >= 0
		 */
		public int getDot() {
			return getCaretPosition();
		}

		/**
		 * Fetches the location of other end of a logical selection. If there is no selection, this will be the same as dot.
		 * 
		 * @return the mark >= 0
		 */
		public int getMark() {
			return getMarkPosition();
		}
	}

	/**
	 * Highlight interface.
	 */
	public interface Highlight {

		/**
		 * Called after the highlight painter has been added.
		 * 
		 * @param textArea
		 *            The text area
		 * @param next
		 *            The painter this one should delegate to
		 */
		void init(CodeTextArea textArea, Highlight next);

		/**
		 * This should paint the highlight and delgate to the next highlight
		 * 
		 * @param gfx
		 *            The graphics context
		 * @param line
		 *            The line number
		 * @param y
		 *            The y co-ordinate of the line
		 */
		void paintHighlight(Graphics gfx, int line, int y);

		/**
		 * Returns the tool tip to display at the specified location. If this highlighter doesn't know what to display, it should delegate to the next highlight
		 * 
		 * @param evt
		 *            The mouse event
		 * @return The tool tip text.
		 */
		String getToolTipText(MouseEvent evt);
	}

	/**
	 * The document handler.
	 */
	class DocumentHandler implements DocumentListener {

		/**
		 * Gives notification that there was an insert into the document. The range given by the DocumentEvent bounds the freshly inserted region.
		 * 
		 * @param evt
		 *            the document event
		 */
		public void insertUpdate(DocumentEvent evt) {
			documentChanged(evt);

			int offset = evt.getOffset();
			int length = evt.getLength();

			int newStart;
			int newEnd;

			if (selectionStart > offset || (selectionStart == selectionEnd && selectionStart == offset)) {
				newStart = selectionStart + length;
			} else {
				newStart = selectionStart;
			}

			if (selectionEnd >= offset) {
				newEnd = selectionEnd + length;
			} else {
				newEnd = selectionEnd;
			}

			select(newStart, newEnd);

			// invalidating the rest of the view
			repaint(0, lineToY(newEnd) + fm.getMaxDescent() + fm.getLeading(), getWidth(), getLineCount() * fm.getHeight());

			updateLayout();
		}

		/**
		 * Gives notification that a portion of the document has been removed. The range is given in terms of what the view last saw (that is, before updating sticky positions).
		 * 
		 * @param evt
		 *            the document event
		 */
		public void removeUpdate(DocumentEvent evt) {
			documentChanged(evt);

			int offset = evt.getOffset();
			int length = evt.getLength();

			int newStart;
			int newEnd;

			if (selectionStart > offset) {
				if (selectionStart > offset + length) {
					newStart = selectionStart - length;
				} else {
					newStart = offset;
				}
			} else
				newStart = selectionStart;

			if (selectionEnd > offset) {
				if (selectionEnd > offset + length) {
					newEnd = selectionEnd - length;
				} else {
					newEnd = offset;
				}
			} else {
				newEnd = selectionEnd;
			}

			select(newStart, newEnd);

			// invalidating the rest of the view
			repaint(0, lineToY(newEnd) + fm.getMaxDescent() + fm.getLeading(), getWidth(), getHeight());

			updateLayout();
		}

		/**
		 * Gives notification that an attribute or set of attributes changed.
		 * 
		 * @param evt
		 *            the document event
		 */
		public void changedUpdate(DocumentEvent evt) {
			updateLayout();
		}
	}

	/**
	 * The caret handler.
	 */
	class CaretHandler implements CaretListener {

		/**
		 * Called when the caret position is updated.
		 * 
		 * @param e
		 *            the caret event
		 */
		public void caretUpdate(CaretEvent e) {
			updateCaret();
		}
	}

	/**
	 * The drag handler.
	 */
	class DragHandler implements MouseMotionListener {

		/**
		 * Invoked when a mouse button is pressed on a component and then dragged.
		 * 
		 * @param evt
		 *            The event to process.
		 */
		public void mouseDragged(MouseEvent evt) {
			if (popup != null && popup.isVisible()) {
				return;
			}
			select(getMarkPosition(), xyToOffset(evt.getX(), evt.getY()));
		}

		/**
		 * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
		 * 
		 * @param evt
		 *            The event to process.
		 */
		public void mouseMoved(MouseEvent evt) {
		}
	}

	/**
	 * The mouse handler.
	 */
	class MouseHandler extends MouseAdapter {
		
		/**
		 * Invoked when the mouse has been clicked on a component.
		 * 
		 * @param evt
		 *            The event to process.
		 */
		public void mousePressed(MouseEvent evt) {
			requestFocus();

			// Focus events not fired sometimes?
			setCaretVisible(true);

			if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0 && popup != null) {
				popup.show(CodeTextArea.this, evt.getX(), evt.getY());
				return;
			}

			int line = yToLine(evt.getY());
			int offset = xToOffset(line, evt.getX());
			int dot = getLineStartOffset(line) + offset;

			switch (evt.getClickCount()) {
			case 1:
				doSingleClick(evt, line, offset, dot);
				break;
			case 2:
				// It uses the bracket matching stuff, so
				// it can throw a BLE
				try {
					doDoubleClick(evt, line, offset, dot);
				} catch (BadLocationException bl) {
					bl.printStackTrace();
				}
				break;
			case 3:
				doTripleClick(evt, line, offset, dot);
				break;
			}
		}

		/**
		 * Performs the single click action.
		 * 
		 * @param evt
		 *            The mouse event.
		 * @param line
		 *            The line where the mouse action took place.
		 * @param offset
		 *            The horizontal offset of the mouse position.
		 * @param dot
		 *            The document position of the character at mouse pointer position.
		 */
		private void doSingleClick(MouseEvent evt, int line, int offset, int dot) {
			if ((evt.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
				select(getMarkPosition(), dot);
			} else {
				setCaretPosition(dot);
			}
		}

		/**
		 * Performs the double click action.
		 * 
		 * @param evt
		 *            The mouse event.
		 * @param line
		 *            The line where the mouse action took place.
		 * @param offset
		 *            The horizontal offset of the mouse position.
		 * @param dot
		 *            The document position of the character at mouse pointer position.
		 * @exception BadLocationException
		 *                If an out-of-bounds access was attempted on the document text
		 */
		private void doDoubleClick(MouseEvent evt, int line, int offset, int dot) throws BadLocationException {
			// Ignore empty lines
			if (getLineLength(line) == 0)
				return;

			try {
				int bracket = TextUtilities.findMatchingBracket(document, Math.max(0, dot - 1));
				if (bracket != -1) {
					int mark = getMarkPosition();
					// Hack
					if (bracket > mark) {
						bracket++;
						mark--;
					}
					select(mark, bracket);
					return;
				}
			} catch (BadLocationException bl) {
				bl.printStackTrace();
			}

			// Ok, it's not a bracket... select the word
			String lineText = getLineText(line);
			char ch = lineText.charAt(Math.max(0, offset - 1));

			String noWordSep = (String) document.getProperty("noWordSep");
			if (noWordSep == null) {
				noWordSep = "";
			}

			// If the user clicked on a non-letter char,
			// we select the surrounding non-letters
			boolean selectNoLetter = (!Character.isLetterOrDigit(ch) && noWordSep.indexOf(ch) == -1);

			int wordStart = 0;

			for (int i = offset - 1; i >= 0; i--) {
				ch = lineText.charAt(i);
				if (selectNoLetter ^ (!Character.isLetterOrDigit(ch) && noWordSep.indexOf(ch) == -1)) {
					wordStart = i + 1;
					break;
				}
			}

			int wordEnd = lineText.length();
			for (int i = offset; i < lineText.length(); i++) {
				ch = lineText.charAt(i);
				if (selectNoLetter ^ (!Character.isLetterOrDigit(ch) && noWordSep.indexOf(ch) == -1)) {
					wordEnd = i;
					break;
				}
			}

			int lineStart = getLineStartOffset(line);
			select(lineStart + wordStart, lineStart + wordEnd);
		}

		private void doTripleClick(MouseEvent evt, int line, int offset, int dot) {
			select(getLineStartOffset(line), getLineEndOffset(line) - 1);
		}

	}

	static class SearchableAdapter implements SearchAndReplaceController {

		private CodeTextArea _component;

		public SearchableAdapter(CodeTextArea component) {
			_component = component;
		}

		@Override
		public JComponent getEditorComponent() {
			return _component;
		}

		@Override
		public boolean find(SearchOptions options) {
			return _component.find(options);
		}

		@Override
		public void replaceSelected(String replacementText) {
			_component.replace(replacementText);
		}

		@Override
		public int replaceAll(SearchOptions options, String replacementText) {
			return _component.replaceAll(replacementText, replacementText, options);
		}

	}

}

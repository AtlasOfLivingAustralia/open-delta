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

package au.org.ala.delta.ui.rtf;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.UndoManager;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.ui.util.IconHelper;

/**
 * Extends the RtfEditorPane to provide the facility to create a toolbar with
 * buttons that control basic style elements of the document (bold, italic,
 * etc).
 */
public class RtfEditor extends RtfEditorPane {

	private static final long serialVersionUID = -6629395451399732726L;
	private JToolBar _toolBar;
	private JToggleButton _btnBold;
	private JToggleButton _btnItalic;
	private JToggleButton _btnUnderline;
	private JToggleButton _btnSuperScript;
	private JToggleButton _btnSubScript;
	private List<SpecialCharHandler> _specialCharHandlers = new ArrayList<SpecialCharHandler>();	
	private SpecialCharHandler _currentCharHandler;
	private UndoManager _undoManager;
	private UndoAction _undoAction;
	private ResourceMap _resources = Application.getInstance().getContext().getResourceManager().getResourceMap();
	
	public RtfEditor() {
		super();		
		try {
			URL url = this.getClass().getResource("/au/org/ala/delta/ui/resources/RTFSpecialBindings.properties");
			Properties p = new Properties();
			p.load(url.openStream());
			for (Object objKey : p.keySet()) {
				String key = (String) objKey;
				String hex = p.getProperty(key);
				int code = Integer.parseInt(hex, 16);
				KeyStroke keyStroke = KeyStroke.getKeyStroke(key.replaceAll("[.]", " "));				
				registerKeyStrokeAction(key, new InsertCharacterAction(this, (char) code), keyStroke);
			}
		} catch (IOException ioex) {
			throw new RuntimeException(ioex);
		}
		
		_specialCharHandlers.add(new AcuteCharacterHandler());
		_specialCharHandlers.add(new GraveAccentCharacterHandler());
		_specialCharHandlers.add(new CircumflexCharacterHandler());
		_specialCharHandlers.add(new UmlautCharacterHandler());
		_specialCharHandlers.add(new TildeCharacterHandler());
		_specialCharHandlers.add(new RingCharacterHandler());
		_specialCharHandlers.add(new HacekCharacterHandler());
		_specialCharHandlers.add(new CedillaCharacterHandler());
		_specialCharHandlers.add(new LigatureCharacterHandler());
		_specialCharHandlers.add(new SlashCharacterHandler());
		_specialCharHandlers.add(new DoubleAcuteCharacterHandler());
		_specialCharHandlers.add(new GreekCharacterHandler());
		
		this.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {				
				// System.err.printf("KeyReleased: %s %s (%s)\n", e.getModifiersExText(e.getModifiersEx()), e.getKeyText(e.getKeyCode()), e.getKeyChar());				
				if (_currentCharHandler != null && _currentCharHandler.hasBeenProcessed()) {
					_currentCharHandler = null;
					e.consume();
				}
			}
			
			@Override
			public void keyTyped(KeyEvent e) {
				if (_currentCharHandler != null) {
					e.consume();
				}
			}
							
			@Override
			public void keyPressed(KeyEvent e) {				
				if (_currentCharHandler != null) {
					if (_currentCharHandler.processFollowUpKey(e, RtfEditor.this)) {
						e.consume();
					}					
				} else {
					for (SpecialCharHandler handler : _specialCharHandlers) {						
						if (handler.getModifiers() == e.getModifiers() && handler.getKeyCode() == e.getKeyCode()) {
							_currentCharHandler = handler;
							_currentCharHandler.setHasBeenProcessed(false);
							e.consume();
							break;
						}
					}
				}
			}
						
		});
		
		
	}
	
	protected void registerKeyStrokeAction(String actionMapKey, Action action, KeyStroke keyStroke) {
		getInputMap().put(keyStroke, actionMapKey);
		getActionMap().put(actionMapKey, action);
	}
	

	/**
	 * Creates a JToolBar with buttons representing supported text styles and configures event handlers
	 * for those buttons so they reflect the state of the currently selected text.
	 * @return the newly created JToolBar so that it can be positioned out by the client.
	 */
	public JToolBar buildAndInstallToolbar() {

		_toolBar = new JToolBar();
		_btnBold = toolbarButtonForAction(new StyledEditorKit.BoldAction(), "text_bold");
		_btnItalic = toolbarButtonForAction(new StyledEditorKit.ItalicAction(), "text_italic");
		_btnUnderline = toolbarButtonForAction(new StyledEditorKit.UnderlineAction(), "text_underline");
		_btnSuperScript = toolbarButtonForAction(new SuperscriptAction(), "text_superscript");
		_btnSubScript = toolbarButtonForAction(new SubscriptAction(), "text_subscript");
		
		_toolBar.add(_btnBold);
		_toolBar.add(_btnItalic);
		_toolBar.add(_btnUnderline);
		_toolBar.add(_btnSuperScript);
		_toolBar.add(_btnSubScript);

		addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateToolbarForCurrentStyle();
					}
				});
			}
		});
		// Enable/Disable the toolbar buttons when this pane gains/loses focus.
		addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				enableToolbarButtons(false);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				enableToolbarButtons(true);
			}
		});

		addUndoSupport();
		
		return _toolBar;
	}
	
	/**
	 * Adds undo support to the editor, including the addition of an undo button to the toolbar.
	 */
	protected void addUndoSupport() {
		_undoManager = new UndoManager();
		getDocument().addUndoableEditListener(new UndoListener());
		
		_undoAction = new UndoAction();
		_undoAction.setEnabled(false);
		JButton undoButton = new JButton();
		decorateToolbarButton(undoButton, _undoAction, "undo");
		undoButton.setFocusable(false);
		undoButton.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		
		_toolBar.addSeparator();
		_toolBar.add(undoButton);
	}
	
	private void enableToolbarButtons(boolean enable) {
		for (Component comp :_toolBar.getComponents()) {
			comp.setEnabled(enable);
		}
	}
	
	public void insertCharAtCaret(char ch) {
		int offset = getCaret().getDot();
		try {
			getDocument().insertString(offset, "" + ch, null);
		} catch (BadLocationException ex) {
			throw new RuntimeException(ex);
		}		
	}
	
	/**
	 * Updates the state of the style buttons in the toolbar to match the style of the currently
	 * selected text in the editor kit.
	 */
	private void updateToolbarForCurrentStyle() {
		StyledEditorKit kit = (StyledEditorKit) getEditorKit();
		MutableAttributeSet attr = kit.getInputAttributes();
		_btnBold.setSelected(StyleConstants.isBold(attr));
		_btnItalic.setSelected(StyleConstants.isItalic(attr));
		_btnUnderline.setSelected(StyleConstants.isUnderline(attr));
		_btnSuperScript.setSelected(StyleConstants.isSuperscript(attr));
		_btnSubScript.setSelected(StyleConstants.isSubscript(attr));
	}
	
	private JToggleButton toolbarButtonForAction(Action action, String iconName) {
		JToggleButton button = new JToggleButton();
		decorateToolbarButton(button, action, iconName);
		
		return button;
	}

	private void decorateToolbarButton(AbstractButton button, Action action, String name) {
		
		String keyPrefix = name + ".Action.";
		String iconName = _resources.getString(keyPrefix+"icon");
		ImageIcon icon = IconHelper.createImageIcon(iconName);
		action.putValue(Action.SMALL_ICON, icon);
		action.putValue(Action.SHORT_DESCRIPTION, _resources.getString(keyPrefix+"shortDescription"));
		button.setAction(action);
		button.setFocusable(false);
		button.setHideActionText(true);
	}
	
	/**
	 * Overrides setText to reset our undo list.
	 */
	@Override
	public void setText(String t) {
		super.setText(t);
		_undoManager.discardAllEdits();
	}

	/**
	 * An action used by the tool bar buttons to modify the current attributes of the text in this
	 * text pane.
	 */
	private abstract class StyleChangeAction extends StyledEditorKit.StyledTextAction {
		
		private static final long serialVersionUID = 5480559734108757527L;

		public StyleChangeAction(String text) {
			super(text);
		}
		
		public void actionPerformed(ActionEvent ae) {

			StyledEditorKit kit = getStyledEditorKit(RtfEditor.this);
			MutableAttributeSet inputAttributes = kit.getInputAttributes();
			SimpleAttributeSet characterAttributes = new SimpleAttributeSet();
			updateCharacterAttributes(inputAttributes, characterAttributes);
			
			setCharacterAttributes(RtfEditor.this, characterAttributes, false);
			
			// Update the state of the toolbar to refect this change.
			updateToolbarForCurrentStyle();
		}
		
		protected abstract void updateCharacterAttributes(MutableAttributeSet inputAttributes, MutableAttributeSet characterAttributes);

	}
	
	/**
	 * Handles the subscript button press.
	 */
	class SubscriptAction extends StyleChangeAction {

		private static final long serialVersionUID = 1L;

		public SubscriptAction() {
			super(StyleConstants.Subscript.toString());
		}

		protected void updateCharacterAttributes(MutableAttributeSet inputAttributes, MutableAttributeSet characterAttributes) {
			boolean subscript = (StyleConstants.isSubscript(inputAttributes)) ? false : true;
			StyleConstants.setSubscript(characterAttributes, subscript);
			// Turn off superscript if it's on
			StyleConstants.setSuperscript(characterAttributes, false);
		}
	}

	/**
	 * Handles the superscript button press.
	 */
	class SuperscriptAction extends StyleChangeAction {

		private static final long serialVersionUID = 1L;

		public SuperscriptAction() {
			super(StyleConstants.Superscript.toString());
		}
		
		protected void updateCharacterAttributes(MutableAttributeSet inputAttributes, MutableAttributeSet characterAttributes) {
			boolean superscript = (StyleConstants.isSuperscript(inputAttributes)) ? false : true;
			
			StyleConstants.setSuperscript(characterAttributes, superscript);
			// Turn off subscript if it's on
			StyleConstants.setSubscript(characterAttributes, false);		
		}
	}
	
	/**
	 * Undoes all changes made to the document since the last invocation of "setText".
	 */
	class UndoAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent e) {
			while (_undoManager.canUndo()) {
				_undoManager.undo();
			}
			setEnabled(false);
		}
	}
	
	class UndoListener implements UndoableEditListener {

		@Override
		public void undoableEditHappened(UndoableEditEvent e) {
			_undoAction.setEnabled(true);
			_undoManager.addEdit(e.getEdit());
		}
		
	}

}

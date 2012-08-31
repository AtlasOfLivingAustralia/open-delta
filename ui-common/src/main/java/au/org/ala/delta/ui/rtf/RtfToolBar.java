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

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.ui.util.IconHelper;

/**
 * Works closely with instances of RtfEditor to provide styling and undo support. Each RtfTooBar instance can work with multiple instances of RtfEditor.
 */
public class RtfToolBar extends JToolBar implements FocusListener, UndoableEditListener {

	private static final long serialVersionUID = -7981104525018100076L;
	private JToggleButton _btnBold;
	private JToggleButton _btnItalic;
	private JToggleButton _btnUnderline;
	private JToggleButton _btnSuperScript;
	private JToggleButton _btnSubScript;
	private JButton _undoButton;
	private ResourceMap _resources;

	private UndoAction _undoAction;

	private RtfEditor _focusedEditor;

	private JInternalFrame _owner;

	public RtfToolBar(JInternalFrame owner) {

		_owner = owner;

		_owner.addInternalFrameListener(new InternalFrameAdapter() {		
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				
				// When the toolbar is floating it is housed in a JDialog subclass provided by the Toolbar UI (protected)
				// spin up the ancestry and try and find it...
				Component c = RtfToolBar.this.getParent();
				while (c != null && !(c instanceof JDialog)) {
					c = c.getParent();
				}
				
				if (c != null) {
					// close the dialog.
					JDialog dlg = (JDialog) c;
					dlg.dispose();
				}				
			}
		});

		_resources = Application.getInstance().getContext().getResourceManager().getResourceMap();

		_btnBold = toolbarButtonForAction(new StyledEditorKit.BoldAction(), "text_bold");
		_btnItalic = toolbarButtonForAction(new StyledEditorKit.ItalicAction(), "text_italic");
		_btnUnderline = toolbarButtonForAction(new StyledEditorKit.UnderlineAction(), "text_underline");
		_btnSuperScript = toolbarButtonForAction(new SuperscriptAction(), "text_superscript");
		_btnSubScript = toolbarButtonForAction(new SubscriptAction(), "text_subscript");

		_undoAction = new UndoAction();
		_undoAction.setEnabled(false);
		_undoButton = new JButton();
		decorateToolbarButton(_undoButton, _undoAction, "undo");
		_undoButton.setFocusable(false);
		_undoButton.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

		add(_btnBold);
		add(_btnItalic);
		add(_btnUnderline);
		add(_btnSuperScript);
		add(_btnSubScript);
		addSeparator();
		add(_undoButton);

		enableToolbarButtons(false);
	}

	public void addEditor(RtfEditor editor) {
		editor.addFocusListener(this);
	}

	private JToggleButton toolbarButtonForAction(Action action, String iconName) {
		JToggleButton button = new JToggleButton();
		decorateToolbarButton(button, action, iconName);

		return button;
	}

	private void decorateToolbarButton(AbstractButton button, Action action, String name) {

		String keyPrefix = name + ".Action.";
		String iconName = _resources.getString(keyPrefix + "icon");
		ImageIcon icon = IconHelper.createImageIcon(iconName);
		action.putValue(Action.SMALL_ICON, icon);
		action.putValue(Action.SHORT_DESCRIPTION, _resources.getString(keyPrefix + "shortDescription"));
		button.setAction(action);
		button.setFocusable(false);
		button.setHideActionText(true);
	}

	/**
	 * Updates the state of the style buttons in the toolbar to match the style of the currently selected text in the editor kit.
	 */
	public void updateToolbarForCurrentStyle(StyledEditorKit kit) {

		MutableAttributeSet attr = kit.getInputAttributes();
		_btnBold.setSelected(StyleConstants.isBold(attr));
		_btnItalic.setSelected(StyleConstants.isItalic(attr));
		_btnUnderline.setSelected(StyleConstants.isUnderline(attr));
		_btnSuperScript.setSelected(StyleConstants.isSuperscript(attr));
		_btnSubScript.setSelected(StyleConstants.isSubscript(attr));
	}

	/**
	 * An action used by the tool bar buttons to modify the current attributes of the text in this text pane.
	 */
	private abstract class StyleChangeAction extends StyledEditorKit.StyledTextAction {

		private static final long serialVersionUID = 5480559734108757527L;

		public StyleChangeAction(String text) {
			super(text);
		}

		public void actionPerformed(ActionEvent ae) {

			JEditorPane editor = getEditor(ae);
			StyledEditorKit kit = getStyledEditorKit(editor);
			MutableAttributeSet inputAttributes = kit.getInputAttributes();
			SimpleAttributeSet characterAttributes = new SimpleAttributeSet();
			updateCharacterAttributes(inputAttributes, characterAttributes);

			setCharacterAttributes(editor, characterAttributes, false);

			// Update the state of the toolbar to refect this change.
			updateToolbarForCurrentStyle(kit);
		}

		protected abstract void updateCharacterAttributes(MutableAttributeSet inputAttributes, MutableAttributeSet characterAttributes);

	}

	@Override
	public void focusLost(FocusEvent e) {
		if (!(e.getOppositeComponent() instanceof RtfEditor)) {
			enableToolbarButtons(false);
		}
		RtfEditor editor = (RtfEditor) e.getComponent();
		editor.getDocument().removeUndoableEditListener(this);
		_focusedEditor = null;
	}

	@Override
	public void focusGained(FocusEvent e) {
		RtfEditor editor = (RtfEditor) e.getComponent();
		_focusedEditor = editor;
		editor.getDocument().addUndoableEditListener(this);
		editor.installToolbar(this);
		enableToolbarButtons(true);
	}

	private void enableToolbarButtons(boolean enable) {
		for (Component comp : getComponents()) {
			if ((comp != _undoButton) || !enable) {
				comp.setEnabled(enable);
			}
		}
	}

	public void disableUndo() {
		_undoAction.setEnabled(false);
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		_undoAction.setEnabled(true);
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
			if (_focusedEditor != null) {
				_focusedEditor.undo();
			}
			setEnabled(false);
		}
	}
}

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

package au.org.ala.delta.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

import au.org.ala.delta.gui.util.IconHelper;

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
	

	/**
	 * Creates a JToolBar with buttons representing supported text styles and configures event handlers
	 * for those buttons so they reflect the state of the currently selected text.
	 * @return the newly created JToolBar so that it can be positioned out by the client.
	 */
	public JToolBar buildAndInstallToolbar() {

		_toolBar = new JToolBar();
		_btnBold = decorateToolbarAction(new StyledEditorKit.BoldAction(), "text_bold.png");
		_btnItalic = decorateToolbarAction(new StyledEditorKit.ItalicAction(), "text_italic.png");
		_btnUnderline = decorateToolbarAction(new StyledEditorKit.UnderlineAction(), "text_underline.png");
		_btnSuperScript = decorateToolbarAction(new SuperscriptAction(), "text_superscript.png");
		_btnSubScript = decorateToolbarAction(new SubscriptAction(), "text_subscript.png");
		
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

		return _toolBar;
	}
	
	private void enableToolbarButtons(boolean enable) {
		for (Component comp :_toolBar.getComponents()) {
			comp.setEnabled(enable);
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

	private JToggleButton decorateToolbarAction(Action action, String iconName) {
		ImageIcon icon = IconHelper.createImageIcon(iconName);
		action.putValue(Action.SMALL_ICON, icon);
		JToggleButton b = new JToggleButton(action);
		
		b.setFocusable(false);
		b.setHideActionText(true);
	
		return b;
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
			// Turn off superscript if it's on
			StyleConstants.setSubscript(characterAttributes, false);		
		}
	}

}

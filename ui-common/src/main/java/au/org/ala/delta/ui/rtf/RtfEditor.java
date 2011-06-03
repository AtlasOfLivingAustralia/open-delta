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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.UndoManager;

/**
 * Extends the RtfEditorPane to provide the facility to create a toolbar with
 * buttons that control basic style elements of the document (bold, italic,
 * etc).
 */
public class RtfEditor extends RtfEditorPane {

	private static final long serialVersionUID = -6629395451399732726L;
	private RtfToolBar _toolBar;
	
	private List<SpecialCharHandler> _specialCharHandlers = new ArrayList<SpecialCharHandler>();	
	private SpecialCharHandler _currentCharHandler;
	private UndoManager _undoManager;
	
	
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
		
		_undoManager = new UndoManager();
		
		addEventHandlers();
	}
	
	private void addEventHandlers() {
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
		
		addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (_toolBar != null) {
							_toolBar.updateToolbarForCurrentStyle((StyledEditorKit)RtfEditor.this.getEditorKit());
						}
					}
				});
			}
		});
		
		getDocument().addUndoableEditListener(new UndoListener());
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
	public void installToolbar(RtfToolBar toolBar) {
		_toolBar = toolBar;
	}
	
	public void insertCharAtCaret(char ch) {
		int offset = getCaret().getDot();
		try {
			getDocument().insertString(offset, "" + ch, null);
		} catch (BadLocationException ex) {
			throw new RuntimeException(ex);
		}		
	}
	
	public void undo() {
		while (_undoManager.canUndo()) {
			_undoManager.undo();
		}
		_toolBar.disableUndo();
	}
	
	/**
	 * Overrides setText to reset our undo list.
	 */
	@Override
	public void setText(String t) {
		super.setText(t);
		if (_undoManager != null) {
			_undoManager.discardAllEdits();
		}
		if (_toolBar != null) {
			_toolBar.disableUndo();
		}
	}
	
	class UndoListener implements UndoableEditListener {

		@Override
		public void undoableEditHappened(UndoableEditEvent e) {
			_undoManager.addEdit(e.getEdit());
		}
	}

}

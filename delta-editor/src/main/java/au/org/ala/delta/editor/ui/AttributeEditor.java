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
package au.org.ala.delta.editor.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.application.Application;

import au.org.ala.delta.editor.EditorPreferences;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.util.EditorUIUtils;
import au.org.ala.delta.editor.ui.validator.AttributeValidator;
import au.org.ala.delta.editor.ui.validator.RtfEditorValidator;
import au.org.ala.delta.editor.ui.validator.ValidationListener;
import au.org.ala.delta.editor.ui.validator.ValidationResult;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.observer.AbstractDataSetObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.ui.rtf.RtfEditor;
import au.org.ala.delta.ui.rtf.RtfToolBar;

/**
 * The AttributeEditor allows a user to change the value of an attribute.
 */
public class AttributeEditor extends JPanel implements ValidationListener, PreferenceChangeListener {

	private static final long serialVersionUID = 1L;

	public RtfEditor _textPane;
	private JTable _characterDetailsTable;
	private JToggleButton advanceItem;
	private JToggleButton advanceCharacter;
	
	private boolean _valid = true;

	private Character _character;
	private Item _item;
	private EditorViewModel _dataSet;

	/** Tracks whether the attribute has been modified since it was displayed */
	private boolean _modified;
	
	/** Tracks whether we are committing changes - and hence can ignore the change event */
	private boolean _committing;

	private EditListener _editListener;

	private List<AttributeEditorListener> _listeners = new ArrayList<AttributeEditorListener>();

	private boolean _inapplicable;
	
	/**
	 * Creates a new AttributeEditor using the supplied EditorDataModel.
	 * @param dataSet the model for the AttributeEditor.
	 */
	public AttributeEditor(EditorViewModel dataSet) {

		ActionMap actions = Application.getInstance().getContext().getActionMap(this);
		
		_dataSet = dataSet;
		_committing = false;
		
		createUI(actions);
		addEventHandlers();		
	}

	/**
	 * Adds event handles to the user interface.
	 */
	private void addEventHandlers() {
		
		_dataSet.addDeltaDataSetObserver(new AbstractDataSetObserver() {
			@Override
			public void itemEdited(DeltaDataSetChangeEvent event) {
				if (event.getItem() == _item) {
					if (!_committing) {
						// This is lazy but has the desired effect of updating the displayed values.
						bind(_character, _item);
					}
				}
			}
			
		});
		_dataSet.addPreferenceChangeListener(this);
		
		_editListener = new EditListener();
		_textPane.getDocument().addDocumentListener(_editListener);
		_textPane.addFocusListener(new EditCommitter());
		_textPane.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (noModifiersOrShift(e.getModifiers()) && e.getKeyCode() == KeyEvent.VK_ENTER) {
					e.consume();
				}
			}
			
			@Override
			public void keyTyped(KeyEvent e) {
				if (noModifiersOrShift(e.getModifiers()) && e.getKeyCode() == KeyEvent.VK_ENTER) {
					e.consume();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (noModifiersOrShift(e.getModifiers()) && e.getKeyCode() == KeyEvent.VK_ENTER) {
					e.consume();
					InputVerifier validator = _textPane.getInputVerifier();
					if (validator != null) {
						if (!validator.verify(_textPane)) {
							return;
						}
					}
					if (commitChanges()) {
						if (e.getModifiers() == 0) {
							// Notify the host of this control to advance to either the next item or character...
							fireAdvance();	
						}
						else {
							fireReverse();
						}
					}
				}
			}
			
			private boolean noModifiersOrShift(int modifiers) {
				return ((modifiers == 0) || ((modifiers & KeyEvent.SHIFT_MASK) > 0));
			}

		});
	}
	
	private void createUI(ActionMap actions) {
		setLayout(new BorderLayout());
		JSplitPane split = new JSplitPane();

		_textPane = new RtfEditor();
		_characterDetailsTable = new JTable();
		_characterDetailsTable.setShowGrid(false);

		JScrollPane scrollPane = new JScrollPane(_textPane);
		split.setLeftComponent(scrollPane);

		split.setRightComponent(_characterDetailsTable);

		split.setDividerLocation(300);
		split.setResizeWeight(0.5);

		RtfToolBar toolbar = new RtfToolBar();
		toolbar.addEditor(_textPane);
		add(toolbar, BorderLayout.NORTH);
		
		add(split, BorderLayout.CENTER);
		
		
		advanceItem = new JToggleButton();
		advanceItem.setAction(actions.get("advanceItem"));
		advanceItem.setFocusable(false);
		advanceCharacter = new JToggleButton();
	    advanceCharacter.setAction(actions.get("advanceCharacter"));
		advanceCharacter.setFocusable(false);
		ButtonGroup buttons = new ButtonGroup();
		buttons.add(advanceItem);
		buttons.add(advanceCharacter);
		
		
		if (EditorPreferences.getEditorAdvanceMode().equals(EditorAdvanceMode.Item)) {
			advanceItem.setSelected(true);
		}
		else {
			advanceCharacter.setSelected(true);
		}
		toolbar.addSeparator();
		toolbar.add(advanceItem);
		toolbar.add(advanceCharacter);
	}
	

	public void add(AttributeEditorListener listener) {
		if (!_listeners.contains(listener)) {
			_listeners.add(listener);
		}
	}

	public void remove(AttributeEditorListener listener) {
		if (_listeners.contains(listener)) {
			_listeners.remove(listener);
		}
	}

	protected void fireAdvance() {
		for (AttributeEditorListener l : _listeners) {
			l.advance();
		}
	}
	
	protected void fireReverse() {
		for (AttributeEditorListener l : _listeners) {
			l.reverse();
		}
	}
	
	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		if (EditorPreferences.ADVANCE_MODE_KEY.equals(evt.getKey())) {
			if (EditorAdvanceMode.Character.equals(EditorPreferences.getEditorAdvanceMode())) {
				advanceCharacter.setSelected(true);
			}
			else {
				advanceItem.setSelected(true);
			}
		}
	}

	/**
	 * Updates the state of the AttributeEditor to match the supplied Item and Character.
	 * @param ch the character that identifies the attribute.
	 * @param item the Item to edit.
	 */
	public void bind(Character ch, Item item) {
		if (!_valid) {
			return;
		}
		
		if (_modified) {
			if (!commitChanges()) {
				return;
			}
		}
		// Disable the change listener so that the bind doesn't set the changed flag
		try {
			_editListener.setDisabled(true);
			_character = ch;
			_item = item;
			_inapplicable = false;
			if (ch != null && item != null) {
				_inapplicable = _character.checkApplicability(_item).isInapplicable();
				Attribute attr = _item.getAttribute(_character);
				if (attr != null) {
					String value = attr.getValueAsString();
					if (value != null) {
						if (!value.startsWith("{\\rtf1")) {
							value = String.format("{\\rtf1\\ansi\\ansicpg1252 %s }", value);
						}

						_textPane.setText(value);
					} else {
						_textPane.setText("");
					}
				} else {
					_textPane.setText("");
				}

				if (ch instanceof MultiStateCharacter) {
					MultiStateCharacter mc = (MultiStateCharacter) ch;
					_characterDetailsTable.setModel(new StateListModel(mc, _item));
					TableColumn onlyColumn = _characterDetailsTable.getColumnModel().getColumn(0);
					StateRenderer renderer = new StateRenderer();
					
					_characterDetailsTable.setRowHeight(renderer.getPreferredSize().height);
					onlyColumn.setCellRenderer(renderer);
					onlyColumn.setCellEditor(new StateEditor());
					
					_characterDetailsTable.setCellEditor(new DefaultCellEditor(new JCheckBox()));
				} else {
					_characterDetailsTable.setModel(new CharacterModel(ch));
					_characterDetailsTable.getColumnModel().getColumn(0).setCellRenderer(new CharacterRenderer());
				}
				AttributeValidator validator = new AttributeValidator(_dataSet, _character);
				RtfEditorValidator rtfValidator = new RtfEditorValidator(validator, this);
				_textPane.setInputVerifier(rtfValidator);
			} else {
				_textPane.setInputVerifier(null);

			}
			_modified = false;
			
		} finally {
			// Re-enable the change listener
			_editListener.setDisabled(false);
		}
	}

	/**
	 * Changes the behaviour of Enter/Shift-Enter such that it will advance the 
	 * selection to the next Item.
	 */
	@org.jdesktop.application.Action
	public void advanceItem() {
		if (advanceItem.isSelected()) {
			EditorPreferences.setEditorAdvanceMode(EditorAdvanceMode.Item);
		}
	}
	
	/**
	 * Changes the behaviour of Enter/Shift-Enter such that it will advance the 
	 * selection to the next Character.
	 */
	@org.jdesktop.application.Action
	public void advanceCharacter() {
		if (advanceCharacter.isSelected()) {
			EditorPreferences.setEditorAdvanceMode(EditorAdvanceMode.Character);
		}
	}
	
	
	@Override
	public void validationSuceeded(ValidationResult results) {
		_valid = true;

	}

	@Override
	public void validationFailed(ValidationResult results) {
		_valid = false;
	}

	public boolean isAttributeValid() {
		return _valid;
	}
	
	class EditListener implements DocumentListener {

		private boolean _disabled;

		@Override
		public void insertUpdate(DocumentEvent e) {
			if (!_disabled) {
				_modified = true;
			}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			if (!_disabled) {
				_modified = true;
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			if (!_disabled) {
				_modified = true;
			}
		}

		public void setDisabled(boolean disabled) {
			_disabled = disabled;
		}

	}

	private boolean commitChanges() {

		try {
			_committing = true;
			if (!_valid || _item == null) {
				return false;
			}
	
			if (!_modified) {
				return true;
			}
	
			try {
				String attributeText = _textPane.getRtfTextBody();
				Attribute attr = _item.getAttribute(_character);
				if (attr != null) {
					attr.setValueFromString(attributeText);
				} else {
					System.err.println("No Attribute! should I be allowed to edit this?");
				}
				_modified = false;
			} catch (Exception ex) {
				_textPane.requestFocusInWindow();
				return false;
			}
		}
		finally {
			_committing = false;
		}

		return true;
	}

	/**
	 * Edits are committed on focus lost events. TODO a failed validate prevents focus transferal but it doesn't prevent a selection on the table or list from updating the text in the document!
	 * 
	 * @see java.awt.event.FocusAdapter#focusLost(java.awt.event.FocusEvent)
	 */
	class EditCommitter extends FocusAdapter {

		@Override
		public void focusLost(FocusEvent e) {
			commitChanges();
		}

	}

	class StateRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		private MultiStateCheckbox stateRenderer = new MultiStateCheckbox();

		
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
			
			stateRenderer.setBackground(getBackground());
			stateRenderer.setForeground(getForeground());
			stateRenderer.setText(value.toString());
			stateRenderer.setSelected(false);
			stateRenderer.bind((MultiStateCharacter) _character, _item, row + 1, _inapplicable);
			if (!_inapplicable) {
				Attribute attribute = _item.getAttribute(_character);
				stateRenderer.setEnabled(attribute.isSimple());
			}
			
			return stateRenderer;
		}

		@Override
		public Dimension getPreferredSize() {
			
			return stateRenderer.getPreferredSize();
		}
	}
	
	/**
	 * Allows other components to transfer focus manually to the AttributeEditor.
	 * @param e the key event to forward to the rtf editor component.
	 */
	public void acceptKeyEvent(KeyEvent e) {
		_textPane.requestFocusInWindow();
		_textPane.dispatchEvent(e);
	}
	
	/**
	 * Allows a state of a multistate attribute to be marked as present / absent using
	 * a checkbox.
	 */
	class StateEditor extends DefaultCellEditor {
		
		private static final long serialVersionUID = 8431473832073654661L;
		
		
		public StateEditor() {
			super(new MultiStateCheckbox());
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			
			JCheckBox checkBox = (JCheckBox)super.getTableCellEditorComponent(table, value, isSelected, row, column);
			checkBox.setOpaque(false);
			MultiStateAttribute attr = (MultiStateAttribute) _item.getAttribute(_character);
			checkBox.setSelected(attr.isStatePresent(row+1));
			checkBox.setText((String)value);
			return checkBox;
		}
	}
	

	/**
	 * Renders a Character as an icon, and if it's a numeric character, it's units.
	 * 
	 * @author god08d
	 * 
	 */
	class CharacterRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;
		
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Character ch = (Character) value;
			setIcon(EditorUIUtils.iconForCharacter(ch));

			if (ch instanceof NumericCharacter) {
				setText(((NumericCharacter<?>) ch).getUnits());
			} else {
				setText("");
			}
			return this;
		}
	}


	class StateListModel extends AbstractTableModel {
	
		private static final long serialVersionUID = 1L;
		private CharacterFormatter _characterFormatter = new CharacterFormatter(true, false, false, true);
		
		private Item _item;
		private MultiStateCharacter _character;
	
		public StateListModel(MultiStateCharacter character, Item item) {
			_character = character;
			_item = item;
		}
		
		@Override
		public int getRowCount() {
			return _character.getNumberOfStates();
		}
	
		@Override
		public int getColumnCount() {
			return 1;
		}
	
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return _characterFormatter.formatState(_character, rowIndex + 1);
		}
	
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			
			Attribute attribute = _item.getAttribute(_character);
			return !_inapplicable && attribute.isSimple();
		}
	
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		    MultiStateAttribute attr = (MultiStateAttribute) _item.getAttribute(_character); 
		    attr.setStatePresent(rowIndex+1, (Boolean)aValue);
		}
		
	}
}

class CharacterModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private Character _character;

	public CharacterModel(Character character) {
		_character = character;
	}

	@Override
	public int getRowCount() {
		return 1;
	}
	
	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return _character;
	}
}

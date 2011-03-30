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

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import au.org.ala.delta.editor.ui.util.EditorUIUtils;
import au.org.ala.delta.editor.ui.validator.AttributeValidator;
import au.org.ala.delta.editor.ui.validator.RtfEditorValidator;
import au.org.ala.delta.editor.ui.validator.ValidationListener;
import au.org.ala.delta.editor.ui.validator.ValidationResult;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.observer.AbstractDataSetObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.ui.rtf.RtfEditor;

public class AttributeEditor extends JPanel implements ValidationListener {

	private static final long serialVersionUID = 1L;

	private RtfEditor _textPane;
	private JList _list;
	private boolean _valid = true;

	private Character _character;
	private Item _item;
	private EditorDataModel _dataSet;

	/** Tracks whether the attribute has been modified since it was displayed */
	private boolean _modified;

	private EditListener _editListener;
	
	private boolean _inapplicable;

	public AttributeEditor(EditorDataModel dataSet) {

		_dataSet = dataSet;

		_dataSet.addDeltaDataSetObserver(new AbstractDataSetObserver() {
			@Override
			public void itemEdited(DeltaDataSetChangeEvent event) {
				if (event.getItem() == _item) {
					repaint();
				}
			}
			
		});
		
		setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(200, 150));
		JSplitPane split = new JSplitPane();

		_textPane = new RtfEditor();
		_list = new JList();
		
		JScrollPane scrollPane = new JScrollPane(_textPane);
		split.setLeftComponent(scrollPane);
		
		split.setRightComponent(_list);

		split.setDividerLocation(300);
		split.setResizeWeight(0.5);

		JToolBar toolbar = _textPane.buildAndInstallToolbar();
		add(toolbar, BorderLayout.NORTH);

		_editListener = new EditListener();
		_textPane.getDocument().addDocumentListener(_editListener);

		_textPane.addFocusListener(new EditCommitter());

		add(split, BorderLayout.CENTER);
	}

	public void bind(Character ch, Item item) {
		if (!_valid) {
			return;
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
					String value = attr.getValue();
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
					_list.setCellRenderer(new StateRenderer());
					_list.setModel(new StateListModel(mc));					
				} else {
					_list.setCellRenderer(new CharacterRenderer());
					_list.setModel(new CharacterModel(ch));					
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

	@Override
	public void validationSuceeded(ValidationResult results) {
		_valid = true;

	}

	@Override
	public void validationFailed(ValidationResult results) {
		_valid = false;
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

	/**
	 * Edits are committed on focus lost events - hence we validate at this point. TODO a failed validate prevents focus transferal but it doesn't prevent a selection on the table or list from
	 * updating the text in the document!
	 * 
	 * @see java.awt.event.FocusAdapter#focusLost(java.awt.event.FocusEvent)
	 */
	class EditCommitter extends FocusAdapter {

		@Override
		public void focusLost(FocusEvent e) {
			if (!_modified || !_valid || _item == null) {
				return;
			}
			String attributeText = _textPane.getRtfTextBody();

			Attribute attr = _item.getAttribute(_character);
			if (attr != null) {
				attr.setValue(attributeText);
			} else {
				System.err.println("No Attribute! should I be allowed to edit this?");
			}
			_modified = false;
		}

	}

	class StateRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		private MultiStateCheckbox stateRenderer = new MultiStateCheckbox();

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent( javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			stateRenderer.setBackground(getBackground());
			stateRenderer.setForeground(getForeground());
			stateRenderer.setText(value.toString());
			stateRenderer.setSelected(false);			
			stateRenderer.bind((MultiStateCharacter) _character, _item, index + 1, _inapplicable);

			return stateRenderer;
		}

	}

	/**
	 * Renders a Character as an icon, and if it's a numeric character, it's units.
	 * 
	 * @author god08d
	 * 
	 */
	class CharacterRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		@SuppressWarnings("rawtypes")
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			Character ch = (Character) value;
			setIcon(EditorUIUtils.iconForCharacter(ch));

			if (ch instanceof NumericCharacter) {
				setText(((NumericCharacter) ch).getUnits());
			} else {
				setText("");
			}
			return this;
		}
	}
}

class StateListModel extends AbstractListModel {

	private static final long serialVersionUID = 1L;

	private MultiStateCharacter _character;

	public StateListModel(MultiStateCharacter character) {
		_character = character;
	}

	@Override
	public int getSize() {
		return _character.getNumberOfStates();
	}

	@Override
	public Object getElementAt(int index) {
		return _character.getState(index + 1);
	}
}

class CharacterModel extends AbstractListModel {

	private static final long serialVersionUID = 1L;

	private Character _character;

	public CharacterModel(Character character) {
		_character = character;
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public Object getElementAt(int index) {
		return _character;
	}
}

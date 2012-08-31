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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.application.Application;

import au.org.ala.delta.editor.StateController;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.observer.AbstractDataSetObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.ui.rtf.RtfEditor;
import au.org.ala.delta.ui.rtf.RtfToolBar;

/**
 * The StateEditor provides a user with the ability to add / delete / edit / reorder the states of a multistate character.
 */
public class StateEditor extends CharacterEditTab {

	private static final long serialVersionUID = 7879506441983307844L;
	private JButton btnAdd;
	private JButton btnDelete;
	private JCheckBox chckbxImplicit;
	private SelectionList stateList;
	private RtfEditor stateDescriptionPane;
	private MultiStateCharacter _character;

	private CharacterFormatter _formatter;
	private EditorViewModel _model;
	private StateController _stateController;

	private boolean _ignoreUpdates;
	private int _selectedState;

	public StateEditor(RtfToolBar toolbar) {
		super(toolbar);
		_ignoreUpdates = false;
		_formatter = new CharacterFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, false);
		createUI();
		addEventHandlers();
	}

	public void setModel(EditorViewModel model) {
		_model = model;
		_stateController.setModel(model);
	}

	/**
	 * Adds the event handlers to the UI components.
	 */
	private void addEventHandlers() {
		_stateController = new StateController(stateList, _model);
		ActionMap actions = Application.getInstance().getContext().getActionMap(_stateController);
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stateDescriptionPane.requestFocusInWindow();
			}
		});
		btnAdd.setAction(actions.get("addState"));
		btnDelete.setAction(actions.get("deleteState"));
		chckbxImplicit.setAction(actions.get("toggleStateImplicit"));

		stateList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateScreen();
			}
		});

		stateDescriptionPane.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateStateText();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateStateText();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateStateText();
			}
		});

		stateDescriptionPane.addKeyListener(new SelectionNavigationKeyListener() {

			@Override
			protected void reverseSelection() {

				int selected = stateList.getSelectedIndex();
				if (selected > 0) {
					stateList.setSelectedIndex(selected - 1);
				}
			}

			@Override
			protected void advanceSelection() {
				int selected = stateList.getSelectedIndex();
				if (selected < stateList.getModel().getSize() - 1) {
					stateList.setSelectedIndex(selected + 1);
				}
			}
		});
	}

	/**
	 * Creates the UI components and lays them out.
	 */
	private void createUI() {
		setName("stateEditor");
		JLabel lblDefinedStates = new JLabel("Defined states:");
		lblDefinedStates.setName("definedStatesLabel");

		stateList = new SelectionList();
		stateList.setDragEnabled(true);
		stateList.setDropMode(DropMode.INSERT);
		JScrollPane listScroller = new JScrollPane(stateList);

		chckbxImplicit = new JCheckBox("Implicit");

		btnAdd = new JButton("Add");

		btnDelete = new JButton("Delete");

		JLabel stateDescriptionLabel = new JLabel("Edit state description");
		stateDescriptionLabel.setName("stateDescriptionLabel");

		stateDescriptionPane = new RtfEditor();
		_toolbar.addEditor(stateDescriptionPane);
		JScrollPane descriptionScroller = new JScrollPane(stateDescriptionPane);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(lblDefinedStates).addComponent(listScroller, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.LEADING, false)
										.addGroup(
												groupLayout.createSequentialGroup().addComponent(chckbxImplicit, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addGroup(
												groupLayout.createSequentialGroup().addComponent(btnDelete, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addGroup(
												groupLayout.createSequentialGroup().addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
						.addGap(0)
						.addGroup(
								groupLayout.createParallelGroup(Alignment.LEADING).addComponent(stateDescriptionLabel)
										.addComponent(descriptionScroller, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addGap(10)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblDefinedStates).addComponent(stateDescriptionLabel))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.TRAILING)
										.addGroup(
												groupLayout.createSequentialGroup().addComponent(chckbxImplicit).addGap(96).addComponent(btnAdd).addPreferredGap(ComponentPlacement.UNRELATED)
														.addComponent(btnDelete)).addComponent(listScroller, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
										.addComponent(descriptionScroller, GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)).addContainerGap()));
		setLayout(groupLayout);
	}

	/**
	 * Updates the contents of the screen based on the selected state.
	 */
	public void updateScreen() {
		if (_ignoreUpdates) {
			return;
		}
		try {
			_ignoreUpdates = true;
			int selectedIndex = stateList.getSelectedIndex();

			_selectedState = selectedIndex + 1;
			int stateCount = _character.getNumberOfStates();

			if (_selectedState > stateCount) {
				stateDescriptionPane.setText("");
			} else {
				if ((stateCount > 0) && (_selectedState > 0)) {
					stateDescriptionPane.setText(_character.getState(_selectedState));
				} else {
					stateDescriptionPane.setText("");
				}
				if (_selectedState > 0) {
					chckbxImplicit.setSelected(_character.getUncodedImplicitState() == _selectedState);
				}
			}
			chckbxImplicit.setEnabled(_selectedState > 0 && _selectedState <= stateCount);
		} finally {
			_ignoreUpdates = false;
		}

	}

	/**
	 * Updates the character being displayed by this StateEditor.
	 * 
	 * @param character
	 *            the character to display/edit.
	 */
	@Override
	public void bind(EditorViewModel model, au.org.ala.delta.model.Character character) {
		if (character == _character) {
			return;
		}
		
		_character = (MultiStateCharacter) character;

		setModel(model);

		if (_character != null) {
			ListModel listModel = new StateListModel(_model, _character);
			stateList.setModel(listModel);
			if (model.getSelectedState() > 0) {
				stateList.setSelectedIndex(model.getSelectedState() - 1);
			} else {
				stateList.setSelectedIndex(0);
			}
		}
		updateScreen();
	}

	public void updateStateText() {
		if (!_ignoreUpdates && _selectedState > 0) {

			if (_selectedState == _character.getNumberOfStates() + 1) {
				try {
					_ignoreUpdates = true;
					_stateController.addState(null);
				}
				finally {
					_ignoreUpdates = false;
				}
			}

			String text = stateDescriptionPane.getRtfTextBody();
			_character.setState(_selectedState, text);

		}
	}

	class StateListModel extends AbstractListModel {

		private static final long serialVersionUID = -8487636933835456688L;
		private MultiStateCharacter _character;

		public StateListModel(EditorViewModel model, MultiStateCharacter character) {
			model.addDeltaDataSetObserver(new CharacterChangeListener());
			_character = character;
		}

		@Override
		public int getSize() {
			return _character.getNumberOfStates() + 1;
		}

		@Override
		public Object getElementAt(int index) {
			if (index == _character.getNumberOfStates()) {
				return "";
			}
			return _formatter.formatState(_character, index + 1);
		}

		class CharacterChangeListener extends AbstractDataSetObserver {

			@Override
			public void characterEdited(DeltaDataSetChangeEvent event) {
				if (event.getCharacter().equals(_character)) {
					fireContentsChanged(this, 0, _character.getNumberOfStates());
				}
			}

		}
	}

}

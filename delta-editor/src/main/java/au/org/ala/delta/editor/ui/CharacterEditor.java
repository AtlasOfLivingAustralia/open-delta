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

import au.org.ala.delta.editor.CharacterController;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.util.MessageDialogHelper;
import au.org.ala.delta.editor.ui.validator.CharacterValidator;
import au.org.ala.delta.editor.ui.validator.TextComponentValidator;
import au.org.ala.delta.editor.ui.validator.ValidationResult;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.observer.AbstractDataSetObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.ui.rtf.RtfEditor;
import au.org.ala.delta.ui.rtf.RtfToolBar;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.beans.PropertyVetoException;

/**
 * Provides a user interface that allows a character to be edited.
 */
public class CharacterEditor extends AbstractDeltaView {

	private static final int CONTROLS_EDITOR_TAB_INDEX = 4;

	private static final int NOTES_EDITOR_TAB_INDEX = 3;
	private static final int UNITS_EDITOR_TAB_INDEX = 1;

	private static final int STATE_EDITOR_TAB_INDEX = 0;

	private static final long serialVersionUID = 9193388605723396077L;

	/** Contains the items we can edit */
	private EditorViewModel _dataSet;

	/** The currently selected character */
	private Character _selectedCharacter;

	/** Flag to allow updates to the model to be disabled during new character selection */
	private boolean _editsDisabled;

	private JSpinner spinner;
	private RtfEditor rtfEditor;
	private JCheckBox mandatoryCheckBox;
	private JButton btnDone;
	private JLabel lblEditCharacterName;
	private JToggleButton btnSelect;
	private CharacterList characterSelectionList;
	private JScrollPane editorScroller;
	private JCheckBox exclusiveCheckBox;
	private JLabel characterNumberLabel;
	private StateEditor stateEditor;

	@Resource
	private String titleSuffix;
	@Resource
	private String editCharacterLabelText;
	@Resource
	private String selectCharacterLabelText;
	private JComboBox comboBox;
	private JTabbedPane tabbedPane;

	private ApplicationContext _context;
	private ResourceMap _resources;

	private MessageDialogHelper _dialogHelper;

	private CharacterValidator _validator;
	private CharacterNotesEditor characterNotesEditor;
	private UnitsEditor unitsEditor;
	private ImageDetailsPanel imageDetails;

	private ControllingAttributeEditor controllingAttributeEditor;

	private ControlledByEditor controlledByEditor;

	/** Handles significant edits performed using this CharacterEditor */
	private CharacterController _controller;

	public CharacterEditor(EditorViewModel model, JInternalFrame owner) {
		setName("CharacterEditorDialog");

		setOwner(owner);
		_dialogHelper = new MessageDialogHelper();
		_context = Application.getInstance().getContext();
		_resources = _context.getResourceMap(CharacterEditor.class);
		_resources.injectFields(this);
		ActionMap map = Application.getInstance().getContext().getActionMap(this);
		createUI(model);
		_controller = new CharacterController(characterSelectionList, model);
		createCharacterForEmptyDataSet(model);
		addEventHandlers(map);
		bind(model);
	}

	private void createCharacterForEmptyDataSet(EditorViewModel model) {
		if (model.getNumberOfCharacters() == 0) {
			addCharacter(model);
		}
	}

	private void addCharacter(EditorViewModel model) {
		Character character = _controller.addCharacter();
		model.setSelectedCharacter(character);
	}

	/**
	 * Adds the event handlers to the UI components.
	 */
	private void addEventHandlers(ActionMap map) {

		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (_editsDisabled) {
					return;
				}
				updateCharacterSelection((Integer) spinner.getValue());
			}

		});

		rtfEditor.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				characterEditPerformed();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				characterEditPerformed();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				characterEditPerformed();
			}
		});
		rtfEditor.addKeyListener(new SelectionNavigationKeyListener());
		characterSelectionList.setSelectionAction(map.get("characterSelected"));

		characterSelectionList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (_editsDisabled) {
					return;
				}
				setSelectedCharacter(_dataSet.getCharacter(characterSelectionList.getSelectedIndex() + 1));

			}
		});

		btnDone.setAction(map.get("characterEditDone"));
		mandatoryCheckBox.setAction(map.get("mandatoryChanged"));
		exclusiveCheckBox.setAction(map.get("exclusiveChanged"));

		btnSelect.setAction(map.get("selectCharacterByName"));

		comboBox.setAction(map.get("characterTypeChanged"));

		// Give the item description text area focus.
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				rtfEditor.requestFocusInWindow();
			}
		});
	}

	@Override
	public boolean canClose() {
        return validateCharacter();
	}

	private boolean validateCharacter() {
        if (_validator != null) {
			ValidationResult result = _validator.validateDescription(rtfEditor.getText());
			if (result.isValid()) {
				result = _validator.validateStates();

                if (result.isValid()) {

                    if (_selectedCharacter.getCharacterType() == CharacterType.Unknown) {
                        result = ValidationResult.error("unknown.character.type");
                    }

			    }
            }
            if (!result.isValid()) {
                final ValidationResult finalResult = result;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        _dialogHelper.displayValidationResult(finalResult);
                    }
                });
                return false;
			}
		}

		return true;
	}

	private void updateCharacterSelection(int characterNum) {

		if (characterNum > _dataSet.getNumberOfCharacters()) {
			addCharacter(_dataSet);
		}

		setSelectedCharacter(_dataSet.getCharacter(characterNum));
	}

	@Action
	public void characterEditDone() {
		try {
			setClosed(true);
		} catch (PropertyVetoException e) {

		}
	}

	@Action
	public void mandatoryChanged() {

		boolean mandatory = !_selectedCharacter.isMandatory();
		ValidationResult result = _validator.validateMandatory(mandatory);

		if (!result.isValid()) {
			_dialogHelper.displayValidationResult(result);
		}
		if (!result.isError()) {
			_selectedCharacter.setMandatory(mandatory);
		}
	}

	@Action
	public void exclusiveChanged() {
		if (_selectedCharacter.getCharacterType().isMultistate()) {
			MultiStateCharacter multiStateChar = (MultiStateCharacter) _selectedCharacter;
			boolean currentlyExclusive = multiStateChar.isExclusive();

			ValidationResult result = _validator.validateExclusive(!currentlyExclusive);
			if (result.isValid()) {
				multiStateChar.setExclusive(!currentlyExclusive);
			} else {
				_dialogHelper.displayValidationResult(result);
				// This is done to reset the checkbox as a result of the event this is
				// fired by the model.
				multiStateChar.setExclusive(false);
			}
		} else {
			throw new UnsupportedOperationException("Only MultiStateCharacters can be exclusive");
		}
	}

	@Action
	public void selectCharacterByName() {
		if (btnSelect.isSelected()) {
			mandatoryCheckBox.setEnabled(false);
			spinner.setEnabled(false);
			exclusiveCheckBox.setEnabled(false);
			comboBox.setEnabled(false);
			lblEditCharacterName.setText(selectCharacterLabelText);
			editorScroller.setViewportView(characterSelectionList);
			characterSelectionList.requestFocusInWindow();

		} else {
			mandatoryCheckBox.setEnabled(true);
			spinner.setEnabled(true);
			exclusiveCheckBox.setEnabled(true);
			comboBox.setEnabled(true);
			lblEditCharacterName.setText(editCharacterLabelText);
			editorScroller.setViewportView(rtfEditor);
		}
	}

	public void setSelectedCharacter(Character character) {
		_dataSet.setSelectedCharacter(character);
		_selectedCharacter = character;
		characterNotesEditor.bind(_dataSet, _selectedCharacter);
		imageDetails.bind(_dataSet, _selectedCharacter);
		unitsEditor.bind(_dataSet, character);
		controlledByEditor.bind(_dataSet, character);
		controllingAttributeEditor.bind(_dataSet, character);
		TextComponentValidator validator = new TextComponentValidator(CharacterValidator.descriptionValidator(_dataSet, character));
		rtfEditor.setInputVerifier(validator);
        _validator = new CharacterValidator(_dataSet, _selectedCharacter);
        updateScreen();
    }

	@Action
	public void characterSelected() {
		btnSelect.setSelected(false);
		selectCharacterByName();
	}

	/**
	 * Invoked in response to a change in the character type combo box. Will change the type of the Character being edited if that is allowed.
	 */
	@Action
	public void characterTypeChanged() {
		CharacterType existingType = _selectedCharacter.getCharacterType();
		CharacterType type = (CharacterType) comboBox.getSelectedItem();

		boolean result = _controller.changeCharacterType(type);

		if (!result) {
			comboBox.getModel().setSelectedItem(existingType);
		}
        else {
            if (type.isMultistate()) {
                tabbedPane.setSelectedComponent(stateEditor);
            }
            else if (type.isNumeric()) {
                tabbedPane.setSelectedComponent(unitsEditor);
            }
        }
	}

	/**
	 * Creates the user interface components of this dialog.
	 */
	private void createUI(EditorViewModel model) {

		characterNumberLabel = new JLabel();
		characterNumberLabel.setName("characterNumberLabel");

		spinner = new JSpinner(new CharacterSpinnerNumberModel(model) {
            @Override
            protected boolean canChange() {
                if (_selectedCharacter == null || _editsDisabled) {
                    return false;
                }
                return validateCharacter();
            }});

		btnSelect = new JToggleButton();
		btnSelect.setName("selectTaxonNumberButton");

		lblEditCharacterName = new JLabel("");
		lblEditCharacterName.setName("lblEditCharacterName");

		rtfEditor = new RtfEditor();
		editorScroller = new JScrollPane(rtfEditor);

		mandatoryCheckBox = new JCheckBox();
		mandatoryCheckBox.setName("mandatoryCheckbox");

		btnDone = new JButton();
		btnDone.setName("doneEditingTaxonButton");

		characterSelectionList = new CharacterList();

		exclusiveCheckBox = new JCheckBox();

		comboBox = new JComboBox();
		comboBox.setModel(new CharacterTypeComboModel());
		comboBox.setRenderer(new CharacterTypeRenderer());

		JLabel lblCharacterType = new JLabel();
		lblCharacterType.setName("characterTypeLabel");

		RtfToolBar toolbar = new RtfToolBar(this);
		createTabbedPane(toolbar);

		JPanel mainPanel = new JPanel();

		GroupLayout groupLayout = new GroupLayout(mainPanel);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												groupLayout
														.createSequentialGroup()
														.addGroup(
																groupLayout
																		.createParallelGroup(Alignment.LEADING)
																		.addGroup(
																				groupLayout.createParallelGroup(Alignment.LEADING, false)
																						.addComponent(characterNumberLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																						.addComponent(spinner, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
																						.addComponent(comboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addComponent(lblCharacterType)
																		.addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(mandatoryCheckBox)))
														.addPreferredGap(ComponentPlacement.RELATED)
														.addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(exclusiveCheckBox).addComponent(btnSelect))
														.addPreferredGap(ComponentPlacement.RELATED)
														.addGroup(
																groupLayout.createParallelGroup(Alignment.LEADING).addComponent(lblEditCharacterName)
																		.addComponent(editorScroller, GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)))
										.addGroup(
												groupLayout
														.createSequentialGroup()
														.addGroup(
																groupLayout
																		.createParallelGroup(Alignment.LEADING)
																		.addGroup(
																				groupLayout.createSequentialGroup().addGap(0, 543, Short.MAX_VALUE).addComponent(btnDone).addGap(5)
																						).addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)).addGap(1)))
						.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(characterNumberLabel).addComponent(lblEditCharacterName))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												groupLayout
														.createSequentialGroup()
														.addGroup(
																groupLayout.createParallelGroup(Alignment.BASELINE)
																		.addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																		.addComponent(btnSelect))
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(lblCharacterType)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addGroup(
																groupLayout
																		.createParallelGroup(Alignment.TRAILING)
																		.addGroup(
																				groupLayout.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
																						.addComponent(exclusiveCheckBox))
																		.addGroup(
																				groupLayout.createSequentialGroup()
																						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																						.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																						.addComponent(mandatoryCheckBox))))
										.addComponent(editorScroller, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)).addGap(9).addComponent(tabbedPane).addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(btnDone)).addGap(17)));

		mainPanel.setLayout(groupLayout);
		setPreferredSize(new Dimension(827, 500));
		setMinimumSize(new Dimension(748, 444));

		toolbar.addEditor(rtfEditor);
		getContentPane().add(toolbar, BorderLayout.NORTH);
		getContentPane().add(mainPanel, BorderLayout.CENTER);
	}

	private void createTabbedPane(RtfToolBar toolbar) {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		stateEditor = new StateEditor(toolbar);
		addTab("states", stateEditor);

		unitsEditor = new UnitsEditor(toolbar);
		addTab("units", unitsEditor);

		imageDetails = new ImageDetailsPanel();
		addTab("images", imageDetails);

		characterNotesEditor = new CharacterNotesEditor(toolbar);
		addTab("notes", characterNotesEditor);

		controllingAttributeEditor = new ControllingAttributeEditor(toolbar);
		addTab("controls", controllingAttributeEditor);

		controlledByEditor = new ControlledByEditor(toolbar);
		addTab("controlledBy", controlledByEditor);
	}

	private void addTab(String titleKeyPrefix, JComponent tab) {
		_context.getResourceMap(tab.getClass()).injectComponents(tab);
		String title = _resources.getString(titleKeyPrefix + ".tab.title");
		tabbedPane.addTab(title, tab);
	}

	/**
	 * Provides the backing model for this Dialog.
	 * 
	 * @param dataSet
	 *            the data set the dialog operates from.
	 */
	public void bind(EditorViewModel dataSet) {
		_dataSet = dataSet;
		characterSelectionList.setModel(dataSet);
		setSelectedCharacter(dataSet.getSelectedCharacter());

		_dataSet.addDeltaDataSetObserver(new AbstractDataSetObserver() {

			@Override
			public void characterTypeChanged(DeltaDataSetChangeEvent event) {
				setSelectedCharacter((Character) event.getExtraInformation());
			}

			@Override
			public void characterEdited(DeltaDataSetChangeEvent event) {
				if (event.getCharacter().equals(_selectedCharacter)) {
					// This is to handle CharacterType changes.
					// _selectedCharacter = _dataSet.getCharacter(_selectedCharacter.getCharacterId());
					//
					// updateScreen();
				}
			}
		});
	}

	private void characterEditPerformed() {
		if (_editsDisabled) {
			return;
		}
		_selectedCharacter.setDescription(rtfEditor.getRtfTextBody());
	}

	/**
	 * Synchronizes the state of the UI with the currently selected Item.
	 */
	private void updateScreen() {

		_editsDisabled = true;

		if (_selectedCharacter == null) {
			_selectedCharacter = _dataSet.getCharacter(1);
		}

        CharacterSpinnerNumberModel model = (CharacterSpinnerNumberModel) spinner.getModel();
        model.setValue(_selectedCharacter.getCharacterId());

        // This check prevents update errors on the editor pane Document.
		if (!_selectedCharacter.getDescription().equals(rtfEditor.getRtfTextBody())) {
			rtfEditor.setText(_selectedCharacter.getDescription());
		}
		mandatoryCheckBox.setSelected(_selectedCharacter.isMandatory());

		if (!_selectedCharacter.getCharacterType().equals(comboBox.getSelectedItem())) {
			comboBox.setSelectedItem(_selectedCharacter.getCharacterType());
		}
		if (_selectedCharacter instanceof MultiStateCharacter) {
			MultiStateCharacter multistateChar = (MultiStateCharacter) _selectedCharacter;
			stateEditor.bind(_dataSet, multistateChar);
			tabbedPane.setEnabledAt(STATE_EDITOR_TAB_INDEX, true);
			tabbedPane.setEnabledAt(UNITS_EDITOR_TAB_INDEX, false);
			tabbedPane.setEnabledAt(CONTROLS_EDITOR_TAB_INDEX, true);
			if (!tabbedPane.isEnabledAt(tabbedPane.getSelectedIndex())) {
				tabbedPane.setSelectedIndex(0);
			}
			exclusiveCheckBox.setEnabled(true);
			exclusiveCheckBox.setSelected(multistateChar.isExclusive());
		} else {
			exclusiveCheckBox.setEnabled(false);
			exclusiveCheckBox.setSelected(false);
			tabbedPane.setEnabledAt(STATE_EDITOR_TAB_INDEX, false);
			tabbedPane.setEnabledAt(CONTROLS_EDITOR_TAB_INDEX, false);

			tabbedPane.setEnabledAt(UNITS_EDITOR_TAB_INDEX, _selectedCharacter instanceof NumericCharacter<?>);

			if (!tabbedPane.isEnabledAt(tabbedPane.getSelectedIndex())) {
				if (tabbedPane.isEnabledAt(UNITS_EDITOR_TAB_INDEX)) {
					tabbedPane.setSelectedIndex(UNITS_EDITOR_TAB_INDEX);
				} else {
					tabbedPane.setSelectedIndex(NOTES_EDITOR_TAB_INDEX);
				}
			}
		}
        _editsDisabled = false;
	}

	@Override
	public String getViewTitle() {
		return titleSuffix;
	}

	class CharacterTypeComboModel extends AbstractListModel implements ComboBoxModel {

		private static final long serialVersionUID = -9004809838787455121L;
		private Object _selected;

		@Override
		public int getSize() {
			return CharacterType.values().length;
		}

		@Override
		public Object getElementAt(int index) {
			return CharacterType.values()[index];
		}

		@Override
		public void setSelectedItem(Object anItem) {
			_selected = anItem;
			fireContentsChanged(this, -1, -1);
		}

		@Override
		public Object getSelectedItem() {
			return _selected;
		}

	}

	class CharacterTypeRenderer extends BasicComboBoxRenderer {

		private static final long serialVersionUID = 7953163275755684592L;

		private static final String PREFIX = "characterType.";

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			String key = PREFIX + value;
			setText(_resources.getString(key));

			return this;
		}
	}
}

class CharacterSpinnerNumberModel extends AbstractSpinnerModel {

	private int _value;
	private EditorViewModel _model;

	public CharacterSpinnerNumberModel(EditorViewModel model) {
		_model = model;
		_value = 1;
	}

	@Override
	public Object getValue() {
		return _value;
	}

	@Override
	public void setValue(Object value) {
		if ((value == null) || !(value instanceof Integer)) {
			throw new IllegalArgumentException("illegal value");
		}
		if (!value.equals(_value)) {
			_value = (Integer) value;
			fireStateChanged();
		}
	}

	@Override
	public Object getNextValue() {
		return incr(SpinnerDirection.Up);
	}

	@Override
	public Object getPreviousValue() {
		return incr(SpinnerDirection.Down);
	}

	private Integer incr(SpinnerDirection direction) {
		
		if (!canChange()) {
			return null;
		}
		
		int newValue = _value + (direction == SpinnerDirection.Up ? 1 : -1);

		if (newValue > _model.getNumberOfCharacters() + 1) {
			return null;
		}

		if (newValue < 1) {
			return null;
		}

		return newValue;
	}
	
	protected boolean canChange() {		
		return true;
	}

	public static enum SpinnerDirection {
		Up, Down
	}

}

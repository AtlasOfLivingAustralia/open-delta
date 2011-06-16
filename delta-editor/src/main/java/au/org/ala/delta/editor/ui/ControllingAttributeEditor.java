package au.org.ala.delta.editor.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.CharacterDependencyController;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.CharacterDependencyFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.ui.rtf.RtfToolBar;

/**
 * The ControllingAttributeEditor provides a user interface for the creation
 * and modification of controlling attributes / character dependencies.
 * It appears on the Character Editor as a tab with the (english) name of
 * "Controls".
 */
public class ControllingAttributeEditor extends CharacterDepencencyEditor {
	
	private static final long serialVersionUID = -1550092824029396438L;
	private JComboBox attributeCombo;
	
	private CharacterDependency _controllingAttribute;
	private List<Character> _remainingCharacters;
	private List<Character> _controlledCharacters;
	private CharacterDependencyFormatter _formatter;
	private CharacterFormatter _characterFormatter = new CharacterFormatter(true, false, false, true);
	private ResourceMap _resources;
	private List<StateViewModel> _states;
	private CharacterDependencyController _controller;
	
	private JTable stateList;
	private JList controlledCharacterList;
	private JList remainingCharacterList;
	private JButton moveRightButton;
	private JButton moveLeftButton;
	private JButton btnDefine;

	public ControllingAttributeEditor(RtfToolBar toolbar) {
		super(toolbar);
		
		_resources = Application.getInstance().getContext().getResourceMap();
		_controlledCharacters = new ArrayList<Character>();
		_remainingCharacters = new ArrayList<Character>();
		
		createUI();
		addEventHandlers();
	}
	
	private void addEventHandlers() {
		ActionMap actions = Application.getInstance().getContext().getActionMap(this);
		attributeCombo.addActionListener(actions.get("selectedAttributeChanged"));
		moveLeftButton.setAction(actions.get("moveToControlledList"));
		moveRightButton.setAction(actions.get("moveFromControlledList"));
		btnDefine.setAction(actions.get("defineControllingAttribute"));
		btnDefine.setEnabled(false);
		
		new ButtonEnabler(moveLeftButton, remainingCharacterList);
		new ButtonEnabler(moveRightButton, controlledCharacterList);
	}

	private void createUI() {
		JPanel controllingAttributes = new JPanel();
		JLabel lblControllingAttribute = new JLabel("Controlling attribute*");
		lblControllingAttribute.setName("controllingAttributeLabel");
		
		attributeCombo = new JComboBox();
		attributeCombo.setRenderer(new ControllingAttributeRenderer());
		
		JLabel lblDefinedByStates = new JLabel("Defined by states:*");
		lblDefinedByStates.setName("definedByStatesLabel");
		
		JScrollPane stateListScroller = new JScrollPane();
		
		btnDefine = new JButton("Redefine");
		GroupLayout gl_controllingAttributes = new GroupLayout(controllingAttributes);
		gl_controllingAttributes.setHorizontalGroup(
			gl_controllingAttributes.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_controllingAttributes.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_controllingAttributes.createParallelGroup(Alignment.LEADING)
						.addComponent(attributeCombo, Alignment.TRAILING, 0, 367, Short.MAX_VALUE)
						.addGroup(gl_controllingAttributes.createSequentialGroup()
							.addComponent(lblDefinedByStates)
							.addPreferredGap(ComponentPlacement.RELATED, 164, Short.MAX_VALUE)
							.addComponent(btnDefine))
						.addComponent(stateListScroller, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
						.addComponent(lblControllingAttribute))
					.addContainerGap())
		);
		gl_controllingAttributes.setVerticalGroup(
			gl_controllingAttributes.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_controllingAttributes.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblControllingAttribute)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(attributeCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_controllingAttributes.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDefinedByStates)
						.addComponent(btnDefine))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(stateListScroller, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		stateList = new JTable();
		stateList.setShowGrid(false);
		stateListScroller.setViewportView(stateList);
		controllingAttributes.setLayout(gl_controllingAttributes);
		
		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(controllingAttributes, GroupLayout.PREFERRED_SIZE, 300, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(controllingAttributes, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		JLabel lblMakesInapplicable = new JLabel("Makes inapplicable:*");
		lblMakesInapplicable.setName("makesInapplicableLabel");
		
		JScrollPane scrollPane = new JScrollPane();
		
		moveRightButton = new JButton("");
		
		moveLeftButton = new JButton("");
		
		JLabel characterListLabel = new JLabel("Character list:*");
		characterListLabel.setName("characterListLabel");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(moveLeftButton)
								.addComponent(moveRightButton)))
						.addComponent(lblMakesInapplicable))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(characterListLabel)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblMakesInapplicable)
								.addComponent(characterListLabel))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
								.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
								.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(72)
							.addComponent(moveRightButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(moveLeftButton)))
					.addContainerGap())
		);
		
		remainingCharacterList = new JList();
		scrollPane_1.setViewportView(remainingCharacterList);
		
		controlledCharacterList = new JList();
		controlledCharacterList.setCellRenderer(new CharacterListRenderer());
		scrollPane.setViewportView(controlledCharacterList);
		panel.setLayout(gl_panel);
		setLayout(groupLayout);
	}
	
	private void enableListEditing(boolean enabled) {
		controlledCharacterList.setEnabled(enabled);
		remainingCharacterList.setEnabled(enabled);
	}
	
	/**
	 * Updates the model and character that this user interface is 
	 * centered on.
	 */
	public void bind(EditorViewModel model, Character character) {
		_model = model;
		_formatter = new CharacterDependencyFormatter(_model);
		_controller = new CharacterDependencyController(_model);
	
		_remainingCharacters = new ArrayList<Character>(_model.getNumberOfCharacters());
		for (int i=1; i<=_model.getNumberOfCharacters(); i++) {
			_remainingCharacters.add(_model.getCharacter(i));
		}
		_controlledCharacters = new ArrayList<Character>(0);
		
		if (character.getCharacterType().isMultistate()) {
			_character = character;
			attributeCombo.setModel(new ControllingAttributeModel());
			attributeCombo.setRenderer(new ControllingAttributeRenderer());
			attributeCombo.setSelectedItem(attributeCombo.getItemAt(0));
		}
		else {
			attributeCombo.setModel(new DefaultComboBoxModel());
			attributeCombo.setRenderer(new DefaultListCellRenderer());
			_character = null;
			
			updateScreen();
		}
		
	}
	
	private void updateStateViewModel() {
		_states = new ArrayList<StateViewModel>();
		if (_character != null) {
			MultiStateCharacter multiStateCharacter = (MultiStateCharacter)_character;
			
			for (int i=0; i<multiStateCharacter.getNumberOfStates(); i++) {
				StateViewModel state = new StateViewModel(i+1);
			
				if (_controllingAttribute != null) {
					Set<Integer> states = _controllingAttribute.getStates();
					if (states.contains(i+1)) {
						state.setPresent(true);
					}
				}
				if (_character != null) {
					int implicitState = ((MultiStateCharacter)_character).getUncodedImplicitState();
					if (i+1 == implicitState) {
						state.setImplicit(true);
					}
				}
			
				state.setDescription(_characterFormatter.formatState(((MultiStateCharacter)_character), i + 1));
				_states.add(state);
			}
		}
	}
	
	@Action
	public void selectedAttributeChanged() { 
		
		Object selected = attributeCombo.getSelectedItem();
		_controlledCharacters = new ArrayList<Character>();
		
		// The [New] option could be selected.
		if (selected instanceof CharacterDependency) {
			_controllingAttribute = (CharacterDependency)attributeCombo.getSelectedItem();
		
			List<Integer> numbers = new ArrayList<Integer>(_controllingAttribute.getDependentCharacterIds());
			Collections.sort(numbers);
		
			
			for (int number: numbers) {
				_controlledCharacters.add(_model.getCharacter(number));
			}
			
			Collections.sort(_controlledCharacters);
			
			btnDefine.setText(_resources.getString("defineControllingAttributeButton.editText"));
			enableListEditing(true);
		}
		else {
			btnDefine.setText(_resources.getString("defineControllingAttributeButton.newText"));
			_controllingAttribute = null;
			enableListEditing(false);
		}
		updateScreen();
	}
	
	@Action
	public void moveToControlledList() {
		Object[] selectedDependencies = remainingCharacterList.getSelectedValues();
		
		for (Object selected : selectedDependencies) {
			Character dependent = (Character)selected;
			_controllingAttribute.addDependentCharacter(dependent);
		}
		
		selectedAttributeChanged();
	}
	
	@Action
	public void moveFromControlledList() {
		Object[] selectedDependencies = controlledCharacterList.getSelectedValues();
		
		for (Object selected : selectedDependencies) {
			Character dependent = (Character)selected;
			_controllingAttribute.removeDependentCharacter(dependent);
		}
		
		selectedAttributeChanged();
	}
	
	@Action
	public void defineControllingAttribute() {
		
		Set<Integer> states = new HashSet<Integer>();
		for (StateViewModel state : _states) {
			if (state.isPresent()) {
				states.add(state.getStateNumber());
			}
		}
		if (_controllingAttribute == null) {
			_controller.defineCharacterDependency((MultiStateCharacter)_character, states);
		}
		else {
			_controller.redefineCharacterDependency(_controllingAttribute, states);
		}
		// Force a refresh.
		bind(_model, _character);
	}
	
	private void updateScreen() {
		
		updateStateViewModel();
		stateList.setModel(new StateListModel());
		stateList.getColumnModel().getColumn(0).setCellRenderer(new StateRenderer());
		stateList.getColumnModel().getColumn(0).setCellEditor(new StateEditor());
		
		stateList.setTableHeader(null);
		
		List<Character> unselectables = new ArrayList<Character>(_controlledCharacters);
		if (_character != null) {
			unselectables.add(_character);
		}
		controlledCharacterList.setModel(new CharacterListModel(_controlledCharacters));
		remainingCharacterList.setCellRenderer(new CharacterListRenderer(unselectables));
		remainingCharacterList.setModel(new CharacterListModel(_remainingCharacters));
	}
	
	class ControllingAttributeModel extends AbstractListModel implements ComboBoxModel {

		private static final long serialVersionUID = -9004809838787455121L;
		private Object _selected;
		private List<CharacterDependency> _controllingAttributes;
		
		public ControllingAttributeModel() {
			if (_character != null) {
				_controllingAttributes = _character.getDependentCharacters();
				if (_controllingAttributes.size() > 0) {
					_selected = _controllingAttributes.get(0);
				}
			}
			else {
				_controllingAttributes = new ArrayList<CharacterDependency>(0);
			}
		}
		@Override
		public int getSize() {
			
			return _controllingAttributes.size() + 1;
		}

		@Override
		public Object getElementAt(int index) {
			if (index == _controllingAttributes.size()) {
				return "[New]";
			}
			return _controllingAttributes.get(index);
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
	
	class ControllingAttributeRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = -3556946058778276619L;
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			
			String description = "";
			// The combo contains one special entry that is a string, "[New]".
			if (value instanceof CharacterDependency) {
				CharacterDependency dependency = (CharacterDependency)value;
				
				if (value != null) {
					description = _formatter.formatCharacterDependency(dependency);
				}
			}
			else {
				description = (String)value;
			}
			
			return super.getListCellRendererComponent(list, description, index, isSelected, cellHasFocus);
		}
	}
	
	class StateListModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 1L;
		
		@Override
		public int getRowCount() {
			return _states.size();
		}
	
		@Override
		public int getColumnCount() {
			return 1;
		}
	
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return _states.get(rowIndex);
		}
	
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {	
	    	return true;
		}
	
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		    _states.get(rowIndex).setPresent((Boolean)aValue);
		    
		    boolean modified = false;
		    for (StateViewModel state : _states) {
		    	if (state.isModified()) {
		    		modified = true;
		    		break;
		    	}
		    }
		    btnDefine.setEnabled(modified);
		    enableListEditing(_controllingAttribute != null && !modified);
		}
		
	}
	
	class StateViewModel {
		public boolean _present;
		public boolean _implicit;
		public String _description;
		public int _stateNumber;
		
		public StateViewModel(int stateNumber) {
			_stateNumber = stateNumber;
			_description = "";
			_present = false;
			_implicit = false;
		}
		
		public Integer getStateNumber() {
			return _stateNumber;
		}

		public void setPresent(boolean present) {
			_present = present;
		}
		
		public void setImplicit(boolean implicit) {
			_implicit = implicit;
		}
		
		public void setDescription(String description) {
			_description = description;
		}
		
		public boolean isPresent() {
			return _present;
		}
		
		public boolean isImplicit() {
			return _implicit;
		}
		
		public String getDescription() {
			return _description;
		}
		
		public boolean isModified() {
			
			if (_controllingAttribute == null) {
				return _present;
			}
			else {
				return _present != _controllingAttribute.getStates().contains(_stateNumber);
			}
		}
		
	}
	
	class CharacterListModel extends AbstractListModel {

		private static final long serialVersionUID = 6573565854830718124L;
		
		private List<Character> _characters;
		
		public CharacterListModel(List<Character> characters) {
			_characters = characters;
		}
		
		@Override
		public int getSize() {
			return _characters.size();
		}

		@Override
		public Object getElementAt(int index) {
			return _characters.get(index);
		}
	}
	
	class CharacterListRenderer extends GreyOutValuesRenderer {

		private static final long serialVersionUID = 865677225829236016L;

		public CharacterListRenderer() {
			super(new ArrayList<Character>());
		}
		
		public CharacterListRenderer(List<Character> toGreyOut) {
			super(toGreyOut);
		}
	
		@Override
		public String formatValue(Object value) {
			return _characterFormatter.formatCharacterDescription((Character)value);
		}
	}
	
	class StateRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		private JCheckBox stateRenderer = new JCheckBox();
		private Font _defaultFont = UIManager.getFont("Label.font");
		
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			StateViewModel state = (StateViewModel)value;
			super.getTableCellRendererComponent(table, state.getDescription(), isSelected, hasFocus,
					row, column);
			
			stateRenderer.setBackground(getBackground());
			stateRenderer.setForeground(getForeground());
			stateRenderer.setText(state.getDescription());
			stateRenderer.setSelected(state.isPresent());
			
			stateRenderer.setFont(_defaultFont);
			int fontModifier = Font.PLAIN;
			
			if (state.isPresent()) {
				fontModifier = fontModifier | Font.BOLD;
				
			}
			if (state.isImplicit()) {
				fontModifier = fontModifier | Font.ITALIC;
			}
			stateRenderer.setFont(getFont().deriveFont(fontModifier));
			
			
			return stateRenderer;
		}

		@Override
		public Dimension getPreferredSize() {
			
			return stateRenderer.getPreferredSize();
		}
	}
	
	/**
	 * Allows a state to be marked as present / absent from the
	 * controlling attribute using a checkbox.
	 */
	class StateEditor extends DefaultCellEditor {
		
		private static final long serialVersionUID = 8431473832073654661L;
		
		
		public StateEditor() {
			super(new JCheckBox());
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			
			StateViewModel state = (StateViewModel)value;
			JCheckBox checkBox = (JCheckBox)super.getTableCellEditorComponent(table, value, isSelected, row, column);
			checkBox.setOpaque(false);
			checkBox.setText(state.getDescription());
			checkBox.setSelected(state.isPresent());

			return checkBox;
		}
	}
}

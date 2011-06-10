package au.org.ala.delta.editor.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.format.CharacterDependencyFormatter;

/**
 * The ControlledByEditor provides users with an interface for changes which characters 
 * control the selected character.
 */
public class ControlledByEditor extends CharacterDepencencyEditor {

	private static final long serialVersionUID = -2474020595227842958L;
	private JList madeInapplicableList;
	private JList controllingAttributesList;
	private JButton moveToRightButton;
	private JButton moveToLeftButton;
	private CharacterDependencyFormatter _formatter;
	
	private List<CharacterDependency> _allControllingAttributes;
	private List<CharacterDependency> _controllingAttributes;
	
	public ControlledByEditor() {
		_allControllingAttributes = new ArrayList<CharacterDependency>();
		createUI();
		addEventHandlers();
	}
	
	private void addEventHandlers() {
		ApplicationContext context = Application.getInstance().getContext();
		ActionMap actions = context.getActionMap(this);
		
		moveToLeftButton.setAction(actions.get("moveToInapplicableList"));
		moveToRightButton.setAction(actions.get("moveFromInapplicableList"));
		
		new ButtonEnabler(moveToLeftButton, controllingAttributesList);
		new ButtonEnabler(moveToRightButton, madeInapplicableList);
		
		controllingAttributesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int[] indices = controllingAttributesList.getSelectedIndices();
				List<Integer> newSelection = new ArrayList<Integer>();
				for (int i : indices) {
					newSelection.add(i);
				}
				for (int index : indices) {
					for (CharacterDependency controllingAttribute : _controllingAttributes) {
						if (_allControllingAttributes.indexOf(controllingAttribute) == index) {
							newSelection.remove(index);
						}
					}
				}
				if (newSelection.size() != indices.length) {
					int[] selection = new int[newSelection.size()];
					int i = 0;
					for (int index : newSelection) {
						selection[i++] = index;
					}
					controllingAttributesList.setSelectedIndices(selection);
				}
			}
		});
	}
	
	private void createUI() {

		JLabel madeInapplicableByLabel = new JLabel("Made inapplicable by:*");
		madeInapplicableByLabel.setName("madeInapplicableByLabel");
		
		JScrollPane scrollPane = new JScrollPane();
		
		moveToRightButton = new JButton(">>");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JLabel lblDefinedControllingAttributes = new JLabel("Defined controlling attributes:*");
		lblDefinedControllingAttributes.setName("definedControllingAttributesLabel");
		
		moveToLeftButton = new JButton("<<");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(moveToLeftButton)
								.addComponent(moveToRightButton)))
						.addComponent(madeInapplicableByLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblDefinedControllingAttributes)
							.addContainerGap())))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(121)
							.addComponent(moveToRightButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(moveToLeftButton))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(madeInapplicableByLabel)
								.addComponent(lblDefinedControllingAttributes))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
									.addGap(2))
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))))
					.addGap(1))
		);
		
		controllingAttributesList = new JList();
		scrollPane_1.setViewportView(controllingAttributesList);
		
		madeInapplicableList = new JList();
		madeInapplicableList.setCellRenderer(new ControllingAttributeRenderer(new ArrayList<CharacterDependency>()));
		scrollPane.setViewportView(madeInapplicableList);
		setLayout(groupLayout);
	}
	
	/**
	 * Sets the Character for editing.
	 * @param character the Character to edit.
	 */
	public void bind(EditorViewModel model, au.org.ala.delta.model.Character character) {
		_model = model;
		_character = character;
		
		_formatter = new CharacterDependencyFormatter(_model);
		_allControllingAttributes = _model.getAllCharacterDependencies();
		
		
		updateScreen();
	}
	
	private void updateScreen() {
		_controllingAttributes = _character.getControllingCharacters();

		controllingAttributesList.setModel(new ControllingAttributeListModel(_allControllingAttributes));
		madeInapplicableList.setModel(new ControllingAttributeListModel(_controllingAttributes));
		controllingAttributesList.setCellRenderer(new ControllingAttributeRenderer(_controllingAttributes));
		
	}
	
	/**
	 * Called when the "left arrow" button is clicked.  Adds the Character to the list 
	 * of the characters controlled by the selected controlling attributes.
	 */
	@Action
	public void moveToInapplicableList() {
		Object[] selectedDependencies = controllingAttributesList.getSelectedValues();
		
		for (Object selected : selectedDependencies) {
			CharacterDependency controllingChar = (CharacterDependency)selected;
			controllingChar.addDependentCharacter(_character);
		}
		
		updateScreen();
	}
	
	@Action
	public void moveFromInapplicableList() {
		Object[] selectedDependencies = madeInapplicableList.getSelectedValues();
		
		for (Object selected : selectedDependencies) {
			CharacterDependency controllingChar = (CharacterDependency)selected;
			controllingChar.removeDependentCharacter(_character);
		}
		
		updateScreen();
	}
	
	class ControllingAttributeListModel extends AbstractListModel {

		private static final long serialVersionUID = 6479256123027870457L;

		private List<CharacterDependency> _characterDependencies;
		
		public ControllingAttributeListModel(List<CharacterDependency> dependencies) {
			_characterDependencies = dependencies;
		}
		
		@Override
		public int getSize() {
			return _characterDependencies.size();
		}

		@Override
		public Object getElementAt(int index) {
			return _characterDependencies.get(index);
		}
	}
	
	class ControllingAttributeRenderer extends GreyOutValuesRenderer {

		private static final long serialVersionUID = -5583376161387170367L;
		
		public ControllingAttributeRenderer(List<? extends Object> valuesToGreyOut) {
			super(valuesToGreyOut);
		}
		
		@Override
		public String formatValue(Object value) {
			return _formatter.formatCharacterDependency((CharacterDependency)value);
		}
	}
	
}

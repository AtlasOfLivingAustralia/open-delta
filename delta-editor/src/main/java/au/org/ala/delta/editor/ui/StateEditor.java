package au.org.ala.delta.editor.ui;

import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.ui.rtf.RtfEditor;
import javax.swing.JList;

/**
 * The StateEditor provides a user with the ability to add / delete / edit / reorder the states
 * of a multistate character.
 */
public class StateEditor extends JPanel {
	
	private static final long serialVersionUID = 7879506441983307844L;
	private JButton btnAdd;
	private JButton btnDelete;
	private JCheckBox chckbxImplicit;
	private JList stateList;
	private RtfEditor stateDescriptionPane;
	private MultiStateCharacter _character;
	
	private CharacterFormatter _formatter;

	public StateEditor() {
		_formatter = new CharacterFormatter(true, false, false, true);
		createUI();
		addEventHandlers();
	}

	/**
	 * Adds the event handlers to the UI components.
	 */
	private void addEventHandlers() {
		ActionMap actions = Application.getInstance().getContext().getActionMap(this);
		btnAdd.setAction(actions.get("addState"));
		btnDelete.setAction(actions.get("deleteState"));
		chckbxImplicit.setAction(actions.get("toggleStateImplicit"));
		
		stateList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateScreen();
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
		
		
		stateList = new JList();
		JScrollPane listScroller = new JScrollPane(stateList);
		
		chckbxImplicit = new JCheckBox("Implicit");
		
		btnAdd = new JButton("Add");
		
		btnDelete = new JButton("Delete");
		
		JLabel stateDescriptionLabel = new JLabel("Edit state description");
		stateDescriptionLabel.setName("stateDescriptionLabel");
		
		stateDescriptionPane = new RtfEditor();
		JScrollPane descriptionScroller = new JScrollPane(stateDescriptionPane);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblDefinedStates)
						.addComponent(listScroller, GroupLayout.PREFERRED_SIZE, 270, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxImplicit)
						.addComponent(btnDelete)
						.addComponent(btnAdd))
					.addGap(27)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(stateDescriptionLabel)
						.addComponent(descriptionScroller, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(10)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDefinedStates)
						.addComponent(stateDescriptionLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(chckbxImplicit)
							.addGap(96)
							.addComponent(btnAdd)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnDelete))
						.addComponent(listScroller, GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
						.addComponent(descriptionScroller))
					.addContainerGap())
		);
		setLayout(groupLayout);
	}
	
	/**
	 * Updates the contents of the screen based on the selected state.
	 */
	public void updateScreen() {
		
		int selectedIndex = stateList.getSelectedIndex();
		
		int selectedState = selectedIndex + 1;
		if ((_character.getNumberOfStates() > 0) && (selectedState > 0)) {
			stateDescriptionPane.setText(_character.getState(selectedState));
		}
		else {
			stateDescriptionPane.setText("");
		}
		chckbxImplicit.setSelected(_character.getUncodedImplicitState() == selectedState);
		
	}
	
	/**
	 * Updates the character being displayed by this StateEditor.
	 * @param character the character to display/edit.
	 */
	public void bind(MultiStateCharacter character) {
		_character = character;
		DefaultListModel model = new DefaultListModel();
		for (int i=1; i<=_character.getNumberOfStates(); i++) {
			model.addElement(_formatter.formatState(_character, i));
		}
		stateList.setModel(model);
		stateList.setSelectedIndex(0);
		updateScreen();
	}
	
	/**
	 * Adds a new state to the character.
	 */
	@Action
	public void addState() {
	
	}
	
	/**
	 * Deletes the selected state from the character.
	 */
	@Action
	public void deleteState() {
		
	}
	
	/**
	 * Toggles the "implicit" property of the current state.  Only one state may be implicit
	 * in a multistate character.
	 */
	@Action
	public void toggleStateImplicit() {
		
	}
	
	
}

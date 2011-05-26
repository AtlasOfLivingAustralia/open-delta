package au.org.ala.delta.editor.ui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.MultiStateCharacter;

public class ControllingAttributeEditor extends CharacterEditTab {
	
	private static final long serialVersionUID = -1550092824029396438L;

	public ControllingAttributeEditor() {
		
		createUI();
	}

	private void createUI() {
		JPanel controllingAttributes = new JPanel();
		JLabel lblControllingAttribute = new JLabel("Controlling attribute");
		
		JComboBox comboBox = new JComboBox();
		
		JLabel lblDefinedByStates = new JLabel("Defined by states:");
		
		JScrollPane list = new JScrollPane();
		
		JButton btnRedefine = new JButton("Redefine");
		GroupLayout gl_controllingAttributes = new GroupLayout(controllingAttributes);
		gl_controllingAttributes.setHorizontalGroup(
			gl_controllingAttributes.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_controllingAttributes.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_controllingAttributes.createParallelGroup(Alignment.LEADING)
						.addComponent(lblControllingAttribute)
						.addComponent(comboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_controllingAttributes.createSequentialGroup()
							.addComponent(lblDefinedByStates)
							.addGap(155)
							.addComponent(btnRedefine))
						.addComponent(list, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_controllingAttributes.setVerticalGroup(
			gl_controllingAttributes.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_controllingAttributes.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblControllingAttribute)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_controllingAttributes.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDefinedByStates)
						.addComponent(btnRedefine))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(list, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JList list_2 = new JList();
		list.setViewportView(list_2);
		controllingAttributes.setLayout(gl_controllingAttributes);
		
		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(controllingAttributes, GroupLayout.PREFERRED_SIZE, 375, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 522, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(100, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(controllingAttributes, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 205, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(43, Short.MAX_VALUE))
		);
		
		JLabel lblNewLabel = new JLabel("Makes inapplicable:");
		
		JScrollPane scrollPane = new JScrollPane();
		
		JButton button = new JButton("");
		
		JButton button_1 = new JButton("");
		
		JLabel lblNewLabel_1 = new JLabel("Character list:");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(button_1)
								.addComponent(button)))
						.addComponent(lblNewLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_1)
						.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 197, GroupLayout.PREFERRED_SIZE))
					.addGap(19))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewLabel)
								.addComponent(lblNewLabel_1))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
								.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 171, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(72)
							.addComponent(button)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(button_1)))
					.addContainerGap())
		);
		
		JList list_1 = new JList();
		scrollPane.setViewportView(list_1);
		panel.setLayout(gl_panel);
		setLayout(groupLayout);
	}
	
	public void bind(EditorViewModel model, Character character) {
		_model = model;
		
	
		if (character.getCharacterType().isMultistate()) {
			_character = (MultiStateCharacter)character;
		}
		else {
			_character = null;
		}
	}
	
	
}

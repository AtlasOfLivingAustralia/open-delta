package au.org.ala.delta.editor.ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.format.CharacterDependencyFormatter;

/**
 * The ControlledByEditor provides users with an interface for changes which characters 
 * control the selected character.
 */
public class ControlledByEditor extends CharacterEditTab {

	private static final long serialVersionUID = -2474020595227842958L;
	private JList madeInapplicableList;
	private JList controllingAttributesList;
	private JButton moveToRightButton;
	private JButton moveToLeftButton;
	private CharacterDependencyFormatter _formatter;
	
	private List<CharacterDependency> _allControllingAttributes;
	
	public ControlledByEditor() {
		_allControllingAttributes = new ArrayList<CharacterDependency>();
		createUI();
	}
	
	private void createUI() {

		JLabel lblNewLabel = new JLabel("Made inapplicable by:");
		
		JScrollPane scrollPane = new JScrollPane();
		
		moveToRightButton = new JButton(">>");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JLabel lblDefinedControllingAttributes = new JLabel("Defined controlling attributes:");
		
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
						.addComponent(lblNewLabel))
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
								.addComponent(lblNewLabel)
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
		controllingAttributesList.setCellRenderer(new ControllingAttributeRenderer());
		
		madeInapplicableList = new JList();
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
		controllingAttributesList.setModel(new ControllingAttributeListModel());
	}
	
	class ControllingAttributeListModel extends AbstractListModel {

		private static final long serialVersionUID = 6479256123027870457L;

		@Override
		public int getSize() {
			return _allControllingAttributes.size();
		}

		@Override
		public Object getElementAt(int index) {
			return _allControllingAttributes.get(index);
		}
		
	}
	
	class ControllingAttributeRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = -5583376161387170367L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			
			String description = _formatter.formatCharacterDependency((CharacterDependency)value);
			return super.getListCellRendererComponent(list, description, index, isSelected,
					cellHasFocus);
		}
	}

}

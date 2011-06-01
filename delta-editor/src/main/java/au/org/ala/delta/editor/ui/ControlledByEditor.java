package au.org.ala.delta.editor.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;

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
public class ControlledByEditor extends CharacterEditTab {

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
		controllingAttributesList.setCellRenderer(new AllControllingAttributesRenderer());
		
		madeInapplicableList = new JList();
		madeInapplicableList.setCellRenderer(new ControllingAttributeRenderer());
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
		_controllingAttributes = character.getControllingCharacters();
		
		controllingAttributesList.setModel(new ControllingAttributeListModel(_allControllingAttributes));
		madeInapplicableList.setModel(new ControllingAttributeListModel(_controllingAttributes));
	}
	
	@Action
	public void moveToInapplicableList() {
		
	}
	
	@Action
	public void moveFromInapplicableList() {
		
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
	
	class AllControllingAttributesRenderer extends ControllingAttributeRenderer {

		private static final long serialVersionUID = 8322653405800736584L;
		
		private Color _usedForeground = Color.LIGHT_GRAY;
		private Color _normalForeground = UIManager.getColor("Label.foreground");
	
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (_controllingAttributes.contains(value)) {
				setForeground(_usedForeground);
			}
			else {
				setForeground(_normalForeground);
			}
			return this;
		}
	}

}

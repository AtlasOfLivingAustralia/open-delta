package au.org.ala.delta.editor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.ComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.model.EditorDataModel;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.ui.rtf.RtfEditor;
import au.org.ala.delta.ui.util.IconHelper;

/**
 * Provides a user interface that allows a character to be edited.
 */
public class CharacterEditor extends JDialog {
	
	private static final long serialVersionUID = 9193388605723396077L;

	/** Contains the items we can edit */
	private EditorDataModel _dataSet;
	
	/** The currently selected character */
	private Character _selectedCharacter;
	
	/** Flag to allow updates to the model to be disabled during new item selection */
	private boolean _editsDisabled;
	
	private JSpinner spinner;
	private RtfEditor rtfEditor;
	private JCheckBox mandatoryCheckBox;
	private JButton btnDone;
	private JLabel lblEditCharacterName;
	private JToggleButton btnSelect;
	private SelectionList characterSelectionList;
	private JScrollPane editorScroller;
	private JCheckBox exclusiveCheckBox; 
	private JLabel characterNumberLabel;
	
	@Resource
	private String titleSuffix;
	@Resource
	private String editCharacterLabelText;
	@Resource
	private String selectCharacterLabelText;
	private JComboBox comboBox;
	
	public CharacterEditor(Window parent) {	
		super(parent);
		setName("CharacterEditorDialog");
		ResourceMap resources = Application.getInstance().getContext().getResourceMap(CharacterEditor.class);
		resources.injectFields(this);
		ActionMap map = Application.getInstance().getContext().getActionMap(this);
		createUI();
		addEventHandlers(map);
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
				_selectedCharacter = _dataSet.getCharacter((Integer)spinner.getValue());
				updateUI();
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
		characterSelectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		characterSelectionList.setModel(new CharacterListModel());
		characterSelectionList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (_editsDisabled) {
					return;
				}
				_selectedCharacter = _dataSet.getCharacter(characterSelectionList.getSelectedIndex()+1);
				updateUI();
			}
		});
		
		btnDone.setAction(map.get("characterEditDone"));
		mandatoryCheckBox.setAction(map.get("mandatoryChanged"));
		exclusiveCheckBox.setAction(map.get("exclusiveChanged"));
		
		btnSelect.setAction(map.get("selectCharacterByName"));
		characterSelectionList.setSelectionAction(map.get("characterSelected"));
	}
	
	@Action
	public void characterEditDone() {
		setVisible(false);
	}
	
	@Action
	public void mandatoryChanged() {
		_selectedCharacter.setMandatory(mandatoryCheckBox.isSelected());
	}
	
	@Action
	public void exclusiveChanged() {
		_selectedCharacter.setExclusive(mandatoryCheckBox.isSelected());
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
			
		}
		else {
			mandatoryCheckBox.setEnabled(true);
			spinner.setEnabled(true);
			exclusiveCheckBox.setEnabled(true);
			comboBox.setEnabled(true);
			lblEditCharacterName.setText(editCharacterLabelText);
			editorScroller.setViewportView(rtfEditor);
		}
	}
	
	@Action
	public void characterSelected() {
		btnSelect.setSelected(false);
		selectCharacterByName();
	}
	
	/**
	 * Creates the user interface components of this dialog.
	 */
	private void createUI() {
		setIconImages(IconHelper.getBlueIconList());
		characterNumberLabel = new JLabel("Character Number:");
		characterNumberLabel.setName("characterNumberLabel");
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(1, 1, 1, 1));
		
		btnSelect = new JToggleButton("Select");
		btnSelect.setName("selectTaxonNumberButton");
		
		lblEditCharacterName = new JLabel("");
		lblEditCharacterName.setName("lblEditCharacterName");
		
		rtfEditor = new RtfEditor();
		editorScroller = new JScrollPane(rtfEditor);
		
		mandatoryCheckBox = new JCheckBox();
		mandatoryCheckBox.setName("mandatoryCheckbox");
		
		JPanel panel = new JPanel();
		
		btnDone = new JButton("Done");
		btnDone.setName("doneEditingTaxonButton");
		
		JButton btnHelp = new JButton("Help");
		btnHelp.setName("helpWithTaxonEditorButton");
		
		characterSelectionList = new ItemList();
		
	    exclusiveCheckBox = new JCheckBox("Exclusive");
		
		comboBox = new JComboBox();
		comboBox.setModel(new CharacterTypeComboModel());
		
		JLabel lblCharacterType = new JLabel("Character Type:");
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
									.addComponent(characterNumberLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(spinner, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
									.addComponent(comboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addComponent(lblCharacterType)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(6)
									.addComponent(mandatoryCheckBox)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(exclusiveCheckBox)
								.addComponent(btnSelect))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblEditCharacterName)
								.addComponent(editorScroller, GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(0, 567, Short.MAX_VALUE)
									.addComponent(btnDone)
									.addGap(5)
									.addComponent(btnHelp))
								.addComponent(panel, GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE))
							.addGap(1)))
					.addGap(19))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(characterNumberLabel)
						.addComponent(lblEditCharacterName))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnSelect))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblCharacterType)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(groupLayout.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
									.addComponent(exclusiveCheckBox))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(mandatoryCheckBox))))
						.addComponent(editorScroller, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
					.addGap(9)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnDone)
						.addComponent(btnHelp))
					.addGap(17))
		);
		panel.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		StateEditor stateEditor = new StateEditor();
		tabbedPane.addTab("States", stateEditor);
		
		ImageDetailsPanel imageDetails = new ImageDetailsPanel();
		imageDetails.setEnabled(false);
		tabbedPane.addTab("Images", imageDetails);
		
		
		panel.add(tabbedPane);
		getContentPane().setLayout(groupLayout);
		setPreferredSize(new Dimension(827, 500));
		setMinimumSize(new Dimension(748, 444));
		setModal(true);
	}
	
	/**
	 * Provides the backing model for this Dialog.
	 * @param dataSet the data set the dialog operates from.
	 * @param itemNumber the currently selected item
	 */
	public void bind(EditorDataModel dataSet) {
		_dataSet = dataSet;
		_selectedCharacter = dataSet.getSelectedCharacter();
		
		updateUI();
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
	private void updateUI() {
		
		_editsDisabled = true;
		setTitle(_dataSet.getName() + " "+titleSuffix);
		if (_selectedCharacter == null) {
			_selectedCharacter = _dataSet.getCharacter(1);
		}
		
		SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
		model.setMaximum(_dataSet.getNumberOfCharacters());
		model.setValue(_selectedCharacter.getCharacterId());
		
		
		rtfEditor.setText(_selectedCharacter.getDescription());
		
		mandatoryCheckBox.setSelected(_selectedCharacter.isMandatory());
		exclusiveCheckBox.setSelected(_selectedCharacter.isExclusive());
		comboBox.setSelectedItem(_selectedCharacter.getCharacterType());
		
		_editsDisabled = false;
	}
	
	class CharacterListModel extends AbstractListModel {

		private CharacterFormatter _formatter = new CharacterFormatter();
		private static final long serialVersionUID = 6573565854830718124L;

		@Override
		public int getSize() {
			return _dataSet.getNumberOfCharacters();
		}

		@Override
		public Object getElementAt(int index) {
			return _formatter.formatCharacterDescription(_dataSet.getCharacter(index+1));
		}
		
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
}

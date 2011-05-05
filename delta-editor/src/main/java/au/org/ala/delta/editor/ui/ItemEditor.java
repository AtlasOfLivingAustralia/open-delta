package au.org.ala.delta.editor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.ui.validator.ItemValidator;
import au.org.ala.delta.editor.ui.validator.TextComponentValidator;
import au.org.ala.delta.editor.ui.validator.ValidationListener;
import au.org.ala.delta.editor.ui.validator.ValidationResult;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.ui.rtf.RtfEditor;

/**
 * Provides a user interface that allows an item description and images to be edited.
 */
public class ItemEditor extends JInternalFrame implements ValidationListener {
	
	private static final long serialVersionUID = 9193388605723396077L;

	/** Contains the items we can edit */
	private EditorDataModel _dataSet;
	
	/** The currently selected Item */
	private Item _selectedItem;
	
	/** Flag to allow updates to the model to be disabled during new item selection */
	private boolean _editsDisabled;
	
	/** 
	 * The behavior is slightly different editing a new Item - instead of disallowing
	 * a blank description the item is deleted if the edit finishes with a blank.
	 */
	private boolean _editingNewItem;
	
	/** Validates the item description */
	private TextComponentValidator _validator;
	
	private JSpinner spinner;
	private RtfEditor rtfEditor;
	private JCheckBox chckbxTreatAsVariant;
	private JButton btnDone;
	private JLabel lblEditTaxonName;
	private JToggleButton btnSelect;
	private ItemList taxonSelectionList;
	private JScrollPane editorScroller;
	private ImageDetailsPanel imageDetails;
	
	@Resource
	private String titleSuffix;
	@Resource
	private String editTaxonLabelText;
	@Resource
	private String selectTaxonLabelText;
	
	public ItemEditor(Window parent, EditorDataModel model) {	
		super();
		setName("ItemEditorDialog");
		
		ResourceMap resources = Application.getInstance().getContext().getResourceMap(ItemEditor.class);
		resources.injectFields(this);
		ActionMap map = Application.getInstance().getContext().getActionMap(this);
		createUI();
		addEventHandlers(map);
		bind(model);		
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
				_selectedItem = _dataSet.getItem((Integer)spinner.getValue());
				updateDisplay();
			}
		});
		
		rtfEditor.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				itemEditPerformed();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				itemEditPerformed();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				itemEditPerformed();
			}
		});
		taxonSelectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		taxonSelectionList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (_editsDisabled) {
					return;
				}
				_selectedItem = _dataSet.getItem(taxonSelectionList.getSelectedIndex()+1);
				updateDisplay();
			}
		});
		
		btnDone.setAction(map.get("itemEditDone"));
		chckbxTreatAsVariant.setAction(map.get("itemVarianceChanged"));
		btnSelect.setAction(map.get("selectItemByName"));
		taxonSelectionList.setSelectionAction(map.get("taxonSelected"));
		_validator = new TextComponentValidator(new ItemValidator(), this);
	}
	
	@Action
	public void itemEditDone() {
		if (_editingNewItem && StringUtils.isEmpty(_selectedItem.getDescription())) {
			_dataSet.deleteItem(_selectedItem);
		}
		setVisible(false);
	}
	
	@Action
	public void itemVarianceChanged() {
		_selectedItem.setVariant(chckbxTreatAsVariant.isSelected());
	}
	
	@Action
	public void selectItemByName() {
		if (btnSelect.isSelected()) {
			chckbxTreatAsVariant.setEnabled(false);
			spinner.setEnabled(false);
			lblEditTaxonName.setText(selectTaxonLabelText);
			editorScroller.setViewportView(taxonSelectionList);
			taxonSelectionList.requestFocusInWindow();
		}
		else {
			chckbxTreatAsVariant.setEnabled(true);
			spinner.setEnabled(true);
			lblEditTaxonName.setText(editTaxonLabelText);
			editorScroller.setViewportView(rtfEditor);
		}
	}
	
	@Action
	public void taxonSelected() {
		btnSelect.setSelected(false);
		selectItemByName();
	}
	
	/**
	 * Creates the user interface components of this dialog.
	 */
	private void createUI() {
		
		JPanel content = new JPanel();
		JLabel lblTaxonNumber = new JLabel("Taxon Number:");
		lblTaxonNumber.setName("taxonNumberLabel");
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(1, 1, 1, 1));
		
		btnSelect = new JToggleButton("Select");
		btnSelect.setName("selectTaxonNumberButton");
		
		lblEditTaxonName = new JLabel(editTaxonLabelText);
		
		rtfEditor = new RtfEditor();
		editorScroller = new JScrollPane(rtfEditor);
		
		chckbxTreatAsVariant = new JCheckBox("Treat as Variant");
		chckbxTreatAsVariant.setName("treatAsVariantCheckbox");
		
		JPanel panel = new JPanel();
		
		btnDone = new JButton("Done");
		btnDone.setName("doneEditingTaxonButton");
		
		JButton btnHelp = new JButton("Help");
		btnHelp.setName("helpWithTaxonEditorButton");
		
		taxonSelectionList = new ItemList();
		
		GroupLayout groupLayout = new GroupLayout(content);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(spinner)
										.addComponent(lblTaxonNumber, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addGap(18)
									.addComponent(btnSelect))
								.addComponent(chckbxTreatAsVariant))
							.addGap(23)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblEditTaxonName)
								.addComponent(editorScroller, GroupLayout.DEFAULT_SIZE, 703, Short.MAX_VALUE)))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(0, 759, Short.MAX_VALUE)
									.addComponent(btnDone)
									.addGap(5)
									.addComponent(btnHelp))
								.addComponent(panel, GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE))
							.addGap(1)))
					.addGap(19))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTaxonNumber)
						.addComponent(lblEditTaxonName))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnSelect))
							.addPreferredGap(ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
							.addComponent(chckbxTreatAsVariant))
						.addComponent(editorScroller, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
					.addGap(18)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnDone)
						.addComponent(btnHelp))
					.addGap(17))
		);
		panel.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		imageDetails = new ImageDetailsPanel();
		imageDetails.setEnabled(false);
		tabbedPane.addTab("Images", imageDetails);
		panel.add(tabbedPane);
		content.setLayout(groupLayout);
	
		setPreferredSize(new Dimension(827, 500));
		setMinimumSize(new Dimension(748, 444));
		
		JToolBar toolbar = rtfEditor.buildAndInstallToolbar();
		getContentPane().add(toolbar, BorderLayout.NORTH);
		getContentPane().add(content, BorderLayout.CENTER);
	}
	
	/**
	 * Provides the backing model for this Dialog.
	 * @param dataSet the data set the dialog operates from.
	 * @param itemNumber the currently selected item
	 */
	public void bind(EditorDataModel dataSet) {
		_dataSet = dataSet;
		taxonSelectionList.setDataSet(dataSet);
		imageDetails.setDataSet(dataSet);
		_selectedItem = dataSet.getSelectedItem();
		_editingNewItem = StringUtils.isEmpty(_selectedItem.getDescription());
		if (!_editingNewItem) {
			rtfEditor.setInputVerifier(_validator);
		}
		else {
			rtfEditor.setInputVerifier(null);
		}
	
		updateDisplay();
	}
	
	private void itemEditPerformed() {
		if (_editsDisabled) {
			return;
		}
		if (_validator.verify(rtfEditor)) {
		
			_selectedItem.setDescription(rtfEditor.getRtfTextBody());
		}
	}
	
	/**
	 * Synchronizes the state of the UI with the currently selected Item.
	 */
	private void updateDisplay() {
		
		_editsDisabled = true;
		setTitle(_dataSet.getName() + " "+titleSuffix);
		if (_selectedItem == null) {
			_selectedItem = _dataSet.getItem(1);
		}
		
		SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
		model.setMaximum(_dataSet.getMaximumNumberOfItems());
		model.setValue(_selectedItem.getItemNumber());
		
		
		rtfEditor.setText(_selectedItem.getDescription());
		
		chckbxTreatAsVariant.setSelected(_selectedItem.isVariant());
		imageDetails.bind(_selectedItem);
		_editsDisabled = false;
	}

	/**
	 * Overrides pack() to give focus to the rtfEditor by default.
	 */
	@Override
	public void pack() {
		super.pack();
		rtfEditor.requestFocusInWindow();
	}

	@Override
	public void validationSuceeded(ValidationResult results) {
		btnDone.setEnabled(true);
	}

	@Override
	public void validationFailed(ValidationResult results) {
		btnDone.setEnabled(false);
		
	}
}

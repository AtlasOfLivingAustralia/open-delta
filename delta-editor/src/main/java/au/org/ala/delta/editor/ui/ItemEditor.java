package au.org.ala.delta.editor.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.ui.rtf.RtfEditor;
import au.org.ala.delta.ui.util.IconHelper;

/**
 * Provides a user interface that allows an item description and images to be edited.
 */
public class ItemEditor extends JDialog {
	
	private static final long serialVersionUID = 9193388605723396077L;

	/** Contains the items we can edit */
	private DeltaDataSet _dataSet;
	
	/** The currently selected Item */
	private Item _selectedItem;
	
	/** Flag to allow updates to the model to be disabled during new item selection */
	private boolean _editsDisabled;
	
	private JSpinner spinner;
	private RtfEditor rtfEditor;
	private JCheckBox chckbxTreatAsVariant;
	private JButton btnDone;
	private JLabel lblEditTaxonName;
	private JToggleButton btnSelect;
	private ItemList taxonSelectionList;
	private JScrollPane editorScroller;
	
	@Resource
	private String titleSuffix;
	@Resource
	private String editTaxonLabelText;
	@Resource
	private String selectTaxonLabelText;
	
	public ItemEditor() {	
		setName("ItemEditorDialog");
		ResourceMap resources = Application.getInstance().getContext().getResourceMap(ItemEditor.class);
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
				_selectedItem = _dataSet.getItem((Integer)spinner.getValue());
				updateUI();
			}
		});
		
		chckbxTreatAsVariant.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
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
				updateUI();
			}
		});
		
		btnDone.setAction(map.get("itemEditDone"));
		chckbxTreatAsVariant.setAction(map.get("itemVarianceChanged"));
		btnSelect.setAction(map.get("selectItemByName"));
		taxonSelectionList.setSelectionAction(map.get("taxonSelected"));
	}
	
	@Action
	public void itemEditDone() {
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
		setIconImages(IconHelper.getBlueIconList());
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
		
		GroupLayout groupLayout = new GroupLayout(getContentPane());
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
		taxonSelectionList.setDataSet(dataSet);
		_selectedItem = dataSet.getSelectedItem();
		updateUI();
	}
	
	private void itemEditPerformed() {
		if (_editsDisabled) {
			return;
		}
		_selectedItem.setDescription(rtfEditor.getRtfTextBody());
	}
	
	/**
	 * Synchronizes the state of the UI with the currently selected Item.
	 */
	private void updateUI() {
		
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
		
		_editsDisabled = false;
	}
}

package au.org.ala.delta.editor.ui;

import javax.swing.JDialog;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JButton;

import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.ui.rtf.RtfEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Provides a user interface that allows an item description and images to be edited.
 */
public class ItemEditor extends JDialog {
	
	private static final long serialVersionUID = 9193388605723396077L;

	/** Contains the items we can edit */
	private DeltaDataSet _dataSet;
	
	/** The currently selected Item */
	private Item _selectedItem;
	
	private JSpinner spinner;
	private RtfEditor rtfEditor;
	private JCheckBox chckbxTreatAsVariant;
	
	public ItemEditor() {	
		createUI();
		addEventHandlers();
	}

	/**
	 * Adds the event handlers to the UI components.
	 */
	private void addEventHandlers() {
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				_selectedItem = _dataSet.getItem((Integer)spinner.getValue());
				updateUI();
			}
		});
		
		chckbxTreatAsVariant.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_selectedItem.setVariant(chckbxTreatAsVariant.isSelected());
			}
		});
		
		rtfEditor.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				_selectedItem.setDescription(rtfEditor.getRtfTextBody());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				_selectedItem.setDescription(rtfEditor.getRtfTextBody());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				_selectedItem.setDescription(rtfEditor.getRtfTextBody());
			}
		});
	}
	
	
	/**
	 * Creates the user interface components of this dialog.
	 */
	private void createUI() {
		JLabel lblTaxonNumber = new JLabel("Taxon Number:");
		lblTaxonNumber.setName("taxonNumberLabel");
		
		spinner = new JSpinner();
		
		JButton btnSelect = new JButton("Select");
		btnSelect.setName("selectTaxonNumberButton");
		
		JLabel lblEditTaxonName = new JLabel("Edit taxon name:");
		lblEditTaxonName.setName("editTaxonNameLabel");
		
		rtfEditor = new RtfEditor();
		JScrollPane editorScroller = new JScrollPane(rtfEditor);
		
		chckbxTreatAsVariant = new JCheckBox("Treat as Variant");
		chckbxTreatAsVariant.setName("treatAsVariantLabel");
		
		JPanel panel = new JPanel();
		
		JButton btnDone = new JButton("Done");
		btnDone.setName("doneEditingTaxonButton");
		
		JButton btnHelp = new JButton("Help");
		btnHelp.setName("helpWithTaxonEditorButton");
		
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
										.addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblTaxonNumber, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnSelect))
								.addComponent(chckbxTreatAsVariant))
							.addGap(31)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblEditTaxonName)
								.addComponent(editorScroller, GroupLayout.PREFERRED_SIZE, 327, GroupLayout.PREFERRED_SIZE)))
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
							.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
								.addComponent(btnDone)
								.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnHelp))
							.addComponent(panel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 502, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(19, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTaxonNumber)
						.addComponent(lblEditTaxonName))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnSelect))
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(chckbxTreatAsVariant))
						.addComponent(editorScroller, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnDone)
						.addComponent(btnHelp))
					.addContainerGap(17, Short.MAX_VALUE))
		);
		panel.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		panel.add(tabbedPane);
		getContentPane().setLayout(groupLayout);
		pack();
	}
	
	/**
	 * Provides the backing model for this Dialog.
	 * @param dataSet the data set the dialog operates from.
	 * @param itemNumber the currently selected item
	 */
	public void bind(EditorDataModel dataSet) {
		_dataSet = dataSet;
		
		_selectedItem = dataSet.getSelectedItem();
		updateUI();
	}
	
	/**
	 * Synchronizes the state of the UI with the currently selected Item.
	 */
	private void updateUI() {
		
		if (_selectedItem == null) {
			_selectedItem = _dataSet.getItem(1);
		}
		SpinnerNumberModel model = new SpinnerNumberModel(
				_selectedItem.getItemNumber(), 1, _dataSet.getMaximumNumberOfItems(), 1);
		spinner.setModel(model);
		
		rtfEditor.setText(_selectedItem.getDescription());
		
		chckbxTreatAsVariant.setSelected(_selectedItem.isVariant());
		
	}
	
	
}

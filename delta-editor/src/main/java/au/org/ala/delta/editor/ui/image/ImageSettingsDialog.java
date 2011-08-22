package au.org.ala.delta.editor.ui.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.ImageSettings.ButtonAlignment;

/**
 * Allows the user to edit image settings (fonts, defaults when creating
 * new overlays and the image path).
 */
public class ImageSettingsDialog extends JDialog {
	
	private static final long serialVersionUID = 8761867230419524659L;
	
	private ImageSettings _imageSettings;
	private ResourceMap _resources;
	private String _defaultFontCommment;
	private String _featureFontComment;
	private String _buttonFontComment;
	
	private JTextField imagePathTextField;
	private JComboBox buttonFontCombo;
	private JComboBox defaultFontCombo;
	private JComboBox defaultSizeCombo;
	private JComboBox featureSizeCombo;
	private JComboBox buttonSizeCombo;
	private JCheckBox defaultBoldCheckBox;
	private JCheckBox buttonBoldCheckBox;
	private JCheckBox defaultItalicCheckBox;
	private JCheckBox featureItalicCheckBox;
	private JCheckBox buttonItalicCheckBox;
	private JTextField sampleTextField;
	private JCheckBox chckbxSaveSampleAs;
	private JCheckBox chckbxCentreInBox;
	private JCheckBox chckbxIncludeComments;
	private JCheckBox chckbxOmitDescription;
	private JCheckBox chckbxUseIntegralHeight;
	private JCheckBox chckbxHotspotsPopUp;
	private JCheckBox chckbxCustomPopupColour;
	private JLabel selectedColourLabel;
	private JButton chooseColourButton;
	private JComboBox buttonAlignmentCombo;
	private JButton imagePathButton;
	private JComboBox featureFontCombo;
	private JCheckBox featureBoldCheckBox;
	private JButton btnOk;
	private JButton btnCancel;
	private JButton btnApply;
	
	public ImageSettingsDialog(Window parent, ImageSettings settings) {
		super(parent);
		
		_imageSettings = settings;
		_resources = Application.getInstance().getContext().getResourceMap();
		createUI();
		pack();
		
		updateGUI();
		addEventHandlers();
	}
	
	private void addEventHandlers() {
		ActionMap actions = Application.getInstance().getContext().getActionMap(this);
		btnOk.setAction(actions.get("okImageSettingsChanges"));
		btnApply.setAction(actions.get("applyImageSettingsChanges"));
		btnCancel.setAction(actions.get("cancelImageSettingsChanges"));
		imagePathButton.setAction(actions.get("addToImagePath"));
		
		javax.swing.Action defaultFontChange = actions.get("defaultFontPropertyChanged");
		defaultFontCombo.setAction(defaultFontChange);
		defaultSizeCombo.setAction(defaultFontChange);
		defaultBoldCheckBox.setAction(defaultFontChange);
		defaultItalicCheckBox.setAction(defaultFontChange);
		
		javax.swing.Action featureFontChange = actions.get("featureFontPropertyChanged");
		featureFontCombo.setAction(featureFontChange);
		featureSizeCombo.setAction(featureFontChange);
		featureBoldCheckBox.setAction(featureFontChange);
		featureItalicCheckBox.setAction(featureFontChange);
		
		javax.swing.Action buttonFontChange = actions.get("buttonFontPropertyChanged");
		buttonFontCombo.setAction(buttonFontChange);
		buttonSizeCombo.setAction(buttonFontChange);
		buttonBoldCheckBox.setAction(buttonFontChange);
		buttonItalicCheckBox.setAction(buttonFontChange);
		
		chooseColourButton.setAction(actions.get("displayColourChooser"));
	}

	private void createUI() {
		JPanel overlayDefaultsPanel = new JPanel();
		overlayDefaultsPanel.setBorder(new TitledBorder(null, "Defaults for new overlays", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel imagePathPanel = new JPanel();
		imagePathPanel.setBorder(new TitledBorder(null, "Image Path", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel overlayFontDefaultsPanel = new JPanel();
		overlayFontDefaultsPanel.setBorder(new TitledBorder(null, "Overlay font settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(overlayDefaultsPanel, GroupLayout.PREFERRED_SIZE, 236, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
						.addComponent(overlayFontDefaultsPanel, GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
						.addComponent(imagePathPanel, GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(10)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(overlayDefaultsPanel, 0, 0, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(imagePathPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(overlayFontDefaultsPanel, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		btnOk = new JButton("Ok");
		buttonPanel.add(btnOk);
		
		btnCancel = new JButton("Cancel");
		buttonPanel.add(btnCancel);
		
		btnApply = new JButton("Apply");
		btnApply.setHorizontalAlignment(SwingConstants.RIGHT);
		buttonPanel.add(btnApply);
		
		chckbxCentreInBox = new JCheckBox("Centre in box");
		
		chckbxIncludeComments = new JCheckBox("Include Comments");
		
		chckbxOmitDescription = new JCheckBox("Omit description");
		
		chckbxUseIntegralHeight = new JCheckBox("Use integral height");
		
		chckbxHotspotsPopUp = new JCheckBox("Hotspots pop up");
		
		chckbxCustomPopupColour = new JCheckBox("Custom popup colour");
		
		JPanel panel = new JPanel();
		
		selectedColourLabel = new JLabel("");
		selectedColourLabel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		selectedColourLabel.setPreferredSize(new Dimension(25, 25));
		selectedColourLabel.setOpaque(true);
		selectedColourLabel.setEnabled(false);
		
		chooseColourButton = new JButton("Choose Colour");
		chooseColourButton.setEnabled(false);
		
		JLabel lblButtonAlignment = new JLabel("Button alignment");
		
		buttonAlignmentCombo = new JComboBox();
		buttonAlignmentCombo.setModel(new ButtonAlignmentModel());
		GroupLayout gl_overlayDefaultsPanel = new GroupLayout(overlayDefaultsPanel);
		gl_overlayDefaultsPanel.setHorizontalGroup(
			gl_overlayDefaultsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_overlayDefaultsPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_overlayDefaultsPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_overlayDefaultsPanel.createSequentialGroup()
							.addGap(6)
							.addComponent(lblButtonAlignment))
						.addComponent(chckbxIncludeComments)
						.addComponent(chckbxOmitDescription)
						.addComponent(chckbxUseIntegralHeight)
						.addComponent(chckbxHotspotsPopUp)
						.addComponent(chckbxCustomPopupColour)
						.addComponent(buttonAlignmentCombo, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 193, GroupLayout.PREFERRED_SIZE)
						.addComponent(chckbxCentreInBox))
					.addContainerGap(25, Short.MAX_VALUE))
		);
		gl_overlayDefaultsPanel.setVerticalGroup(
			gl_overlayDefaultsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_overlayDefaultsPanel.createSequentialGroup()
					.addGap(10)
					.addComponent(chckbxCentreInBox)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxIncludeComments)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxOmitDescription)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxUseIntegralHeight)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxHotspotsPopUp)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxCustomPopupColour)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addGap(12)
					.addComponent(lblButtonAlignment)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(buttonAlignmentCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(144, Short.MAX_VALUE))
		);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(selectedColourLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chooseColourButton)
					.addGap(68))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(chooseColourButton)
						.addComponent(selectedColourLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		overlayDefaultsPanel.setLayout(gl_overlayDefaultsPanel);
		
		JLabel lblFontName = new JLabel("Font name:");
		
		JLabel lblSize = new JLabel("Size");
		
		JLabel lblBold = new JLabel("Bold");
		
		JLabel lblItalic = new JLabel("Italic");
		
		JLabel featureLabel = new JLabel("Feature:");
		
		buttonFontCombo = new JComboBox();
		buttonFontCombo.setModel(new FontFamilyModel());
		
		featureSizeCombo = new JComboBox();
		featureSizeCombo.setModel(new FontSizeModel());
		
		buttonBoldCheckBox = new JCheckBox("");
		
		featureItalicCheckBox = new JCheckBox("");
		
		JLabel buttonLabel = new JLabel("Button:");
		
		featureFontCombo = new JComboBox();
		featureFontCombo.setModel(new FontFamilyModel());
		
		buttonSizeCombo = new JComboBox();
		buttonSizeCombo.setModel(new FontSizeModel());
		
		featureBoldCheckBox = new JCheckBox("");
		
		buttonItalicCheckBox = new JCheckBox("");
		
		JLabel defaultLabel = new JLabel("Default:");
		
		defaultFontCombo = new JComboBox();
		defaultFontCombo.setModel(new FontFamilyModel());
		
		defaultSizeCombo = new JComboBox();
		defaultSizeCombo.setModel(new FontSizeModel());
		
		defaultBoldCheckBox = new JCheckBox("");
		
		defaultItalicCheckBox = new JCheckBox("");
		
		JLabel lblSample = new JLabel("Sample:");
		
		sampleTextField = new JTextField("");
		
		chckbxSaveSampleAs = new JCheckBox("Save sample as comment");
		
		
		GroupLayout gl_overlayFontDefaultsPanel = new GroupLayout(overlayFontDefaultsPanel);
		gl_overlayFontDefaultsPanel.setHorizontalGroup(
			gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_overlayFontDefaultsPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_overlayFontDefaultsPanel.createSequentialGroup()
							.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(defaultLabel)
								.addComponent(featureLabel)
								.addComponent(buttonLabel))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblFontName, 0, 345, Short.MAX_VALUE)
								.addGroup(gl_overlayFontDefaultsPanel.createSequentialGroup()
									.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.TRAILING)
										.addComponent(defaultFontCombo, Alignment.LEADING, 0, 339, Short.MAX_VALUE)
										.addComponent(featureFontCombo, 0, 339, Short.MAX_VALUE)
										.addComponent(buttonFontCombo, 0, 339, Short.MAX_VALUE))
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSize, 0, 94, Short.MAX_VALUE)
								.addComponent(defaultSizeCombo, 0, 94, Short.MAX_VALUE)
								.addComponent(featureSizeCombo, 0, 94, Short.MAX_VALUE)
								.addComponent(buttonSizeCombo, 0, 94, Short.MAX_VALUE)))
						.addGroup(gl_overlayFontDefaultsPanel.createSequentialGroup()
							.addComponent(lblSample)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(chckbxSaveSampleAs)
								.addComponent(sampleTextField, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))))
					.addGap(18)
					.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(featureBoldCheckBox, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
						.addComponent(defaultBoldCheckBox, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
						.addComponent(buttonBoldCheckBox, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblBold, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(buttonItalicCheckBox, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
						.addComponent(featureItalicCheckBox, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblItalic, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
						.addComponent(defaultItalicCheckBox, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_overlayFontDefaultsPanel.setVerticalGroup(
			gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_overlayFontDefaultsPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSize)
						.addComponent(lblFontName)
						.addComponent(lblItalic)
						.addComponent(lblBold))
					.addGap(10)
					.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(defaultFontCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(defaultSizeCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(defaultLabel))
						.addComponent(defaultItalicCheckBox)
						.addComponent(defaultBoldCheckBox))
					.addGap(10)
					.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(featureFontCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(featureSizeCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(featureLabel))
						.addComponent(featureItalicCheckBox)
						.addComponent(featureBoldCheckBox))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_overlayFontDefaultsPanel.createSequentialGroup()
							.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(buttonSizeCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(buttonFontCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(buttonLabel))
							.addGap(18)
							.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(sampleTextField, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblSample))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chckbxSaveSampleAs))
						.addComponent(buttonItalicCheckBox)
						.addComponent(buttonBoldCheckBox))
					.addContainerGap(16, Short.MAX_VALUE))
		);
		overlayFontDefaultsPanel.setLayout(gl_overlayFontDefaultsPanel);
		
		imagePathTextField = new JTextField();
		imagePathTextField.setColumns(10);
		
		imagePathButton = new JButton("New button");
		GroupLayout gl_imagePathPanel = new GroupLayout(imagePathPanel);
		gl_imagePathPanel.setHorizontalGroup(
			gl_imagePathPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_imagePathPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(imagePathTextField, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(imagePathButton))
		);
		gl_imagePathPanel.setVerticalGroup(
			gl_imagePathPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_imagePathPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_imagePathPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(imagePathTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(imagePathButton))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		imagePathPanel.setLayout(gl_imagePathPanel);
		getContentPane().setLayout(groupLayout);
	}
	
	private void updateGUI() {
		imagePathTextField.setText(_imageSettings.getResourcePath());
		Font defaultFont = _imageSettings.getDefaultFont();
		updateFromFont(defaultFont, defaultFontCombo, defaultSizeCombo, defaultBoldCheckBox, defaultItalicCheckBox);
		Font featureFont = _imageSettings.getDefaultFeatureFont();
		updateFromFont(featureFont, featureFontCombo, featureSizeCombo, featureBoldCheckBox, featureItalicCheckBox);
		Font buttonFont = _imageSettings.getDefaultButtonFont();
		updateFromFont(buttonFont, buttonFontCombo, buttonSizeCombo, buttonBoldCheckBox, buttonItalicCheckBox);
		
		sampleTextField.setFont(fontFromComponents(defaultFontCombo, defaultSizeCombo, defaultBoldCheckBox, defaultItalicCheckBox));
		String sample = _imageSettings.getDefaultFontInfo().comment;
		sampleTextField.setText(sample);
		
		
		_defaultFontCommment = _imageSettings.getDefaultFontInfo().comment;
		if (StringUtils.isEmpty(_defaultFontCommment)) {
			_defaultFontCommment = _resources.getString("defaultFont.samplePrefix");
		}
		_featureFontComment = _imageSettings.getDefaultFeatureFontInfo().comment;
		if (StringUtils.isEmpty(_featureFontComment)) {
			_featureFontComment = _resources.getString("defaultFeatureFont.samplePrefix");
		}
		_buttonFontComment = _imageSettings.getDefaultButtonFontInfo().comment;
		if (StringUtils.isEmpty(_buttonFontComment)) {
			_buttonFontComment = _resources.getString("defaultButtonFont.samplePrefix");
		}
		chckbxCentreInBox.setSelected(_imageSettings.getCentreInBox());
		chckbxIncludeComments.setSelected(_imageSettings.getIncludeComments());
		chckbxOmitDescription.setSelected(_imageSettings.getOmitDescription());
		chckbxUseIntegralHeight.setSelected(_imageSettings.getUseIntegralHeight());
		chckbxHotspotsPopUp.setSelected(_imageSettings.getHotspotsPopup());
		chckbxCustomPopupColour.setSelected(_imageSettings.getUseCustomPopupColour());
		selectedColourLabel.setOpaque(true);
		selectedColourLabel.setBackground(_imageSettings.getCustomPopupColour());
		buttonAlignmentCombo.getModel().setSelectedItem(_imageSettings.getButtonAlignment());
	}
	
	
	
	private void updateFromFont(Font font, JComboBox name, JComboBox size, JCheckBox bold, JCheckBox italic) {
		name.getModel().setSelectedItem(font.getFamily());
		size.getModel().setSelectedItem(font.getSize());
		bold.setSelected(font.isBold());
		italic.setSelected(font.isItalic());
	}
	
	private Font fontFromComponents(JComboBox name, JComboBox sizeCombo, JCheckBox bold, JCheckBox italic) {
		String family = (String)name.getSelectedItem();
		int size = (Integer)sizeCombo.getSelectedItem();
		int style = 0;
		if (bold.isSelected()) {
			style |= Font.BOLD;
		}
		if (italic.isSelected()) {
			style |= Font.ITALIC;
		}
		Font font = new Font(family, style, size);
		
		return font;	
	}
	
	@Action
	public void addToImagePath() {
		JFileChooser chooser = new JFileChooser(_imageSettings.getDataSetPath());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showDialog(this, _resources.getString("okImageSettingsChanges.Action.text"));
		if (result == JFileChooser.APPROVE_OPTION) {
			_imageSettings.addToResourcePath(chooser.getSelectedFile());
			imagePathTextField.setText(_imageSettings.getResourcePath());
		}
	}
	
	@Action
	public void displayColourChooser() {
		Color currentDefault = _imageSettings.getCustomPopupColour();
		String title = _resources.getString("hotSpotColourChooser.title");
		Color newDefault = JColorChooser.showDialog(this, title, currentDefault);
		selectedColourLabel.setBackground(newDefault);
	}
	
	@Action
	public void applyImageSettingsChanges() {
		applyChanges();
	}
	
	@Action
	public void okImageSettingsChanges() {
		applyChanges();
		setVisible(false);
	}
	
	@Action
	public void cancelImageSettingsChanges() {
		setVisible(false);
	}
	
	@Action
	public void defaultFontPropertyChanged() {
		sampleTextField.setFont(fontFromComponents(defaultFontCombo, defaultSizeCombo, defaultBoldCheckBox, defaultItalicCheckBox));
		sampleTextField.setText(_defaultFontCommment);
	}
	
	@Action
	public void featureFontPropertyChanged() {
		sampleTextField.setFont(fontFromComponents(featureFontCombo, featureSizeCombo, featureBoldCheckBox, featureItalicCheckBox));
		sampleTextField.setText(_featureFontComment);
	}
	
	@Action
	public void buttonFontPropertyChanged() {
		sampleTextField.setFont(fontFromComponents(buttonFontCombo, buttonSizeCombo, buttonBoldCheckBox, buttonItalicCheckBox));
		sampleTextField.setText(_buttonFontComment);
	}
	
	private void applyChanges() {
		
	}
	
	static String[] fontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	
	
	private class FontFamilyModel extends DefaultComboBoxModel {

		private static final long serialVersionUID = 8071821047820831134L;
		@Override
		public Object getElementAt(int index) {
			return fontFamilyNames[index];
		}
		@Override
		public int getSize() {
			return fontFamilyNames.length;
		}
	}
	
	private class FontSizeModel extends DefaultComboBoxModel {
		
		private static final long serialVersionUID = -1338904577467370497L;
		private static final int MIN_SIZE = 7;
		private static final int MAX_SIZE = 25;
		
		@Override
		public Object getElementAt(int index) {
			return MIN_SIZE + index;
		}
		@Override
		public int getSize() {
			return MAX_SIZE-MIN_SIZE+1;
		}
	}
	
	private class ButtonAlignmentModel extends DefaultComboBoxModel {
		
		private static final long serialVersionUID = -2435924644345786329L;
		@Override
		public Object getElementAt(int index) {
			return ButtonAlignment.values()[index];
		}
		@Override
		public int getSize() {
			return ButtonAlignment.values().length;
		}
	}
}

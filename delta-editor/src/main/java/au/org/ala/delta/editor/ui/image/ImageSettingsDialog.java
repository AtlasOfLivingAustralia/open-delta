/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.editor.ui.image;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.AbstractDeltaView;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.ImageSettings.ButtonAlignment;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Allows the user to edit image settings (fonts, defaults when creating new overlays and the image path).
 */
public class ImageSettingsDialog extends AbstractDeltaView {

	private static final long serialVersionUID = 8761867230419524659L;

	private ImageSettings _imageSettings;
	private EditorViewModel _model;
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

	public ImageSettingsDialog(EditorViewModel model, ImageSettings settings) {
		super();

		_imageSettings = settings;
		_model = model;
		_resources = Application.getInstance().getContext().getResourceMap();
		createUI();
		pack();
		setName("ImageSettings");
		updateGUI();
		addEventHandlers();
		this.setMinimumSize(new Dimension(650,430));
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
		defaultBoldCheckBox.setText("");
		defaultItalicCheckBox.setAction(defaultFontChange);
		defaultItalicCheckBox.setText("");
		FocusListener defaultFontFocusListener = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				defaultFontPropertyChanged();
			}
		};
		defaultFontCombo.addFocusListener(defaultFontFocusListener);
		defaultSizeCombo.addFocusListener(defaultFontFocusListener);

		javax.swing.Action featureFontChange = actions.get("featureFontPropertyChanged");
		featureFontCombo.setAction(featureFontChange);
		featureSizeCombo.setAction(featureFontChange);
		featureBoldCheckBox.setAction(featureFontChange);
		featureBoldCheckBox.setText("");
		featureItalicCheckBox.setAction(featureFontChange);
		featureItalicCheckBox.setText("");
		FocusListener featureFontFocusListener = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				featureFontPropertyChanged();
			}
		};
		featureFontCombo.addFocusListener(featureFontFocusListener);
		featureSizeCombo.addFocusListener(featureFontFocusListener);

		javax.swing.Action buttonFontChange = actions.get("buttonFontPropertyChanged");
		buttonFontCombo.setAction(buttonFontChange);
		buttonSizeCombo.setAction(buttonFontChange);
		buttonBoldCheckBox.setAction(buttonFontChange);
		buttonBoldCheckBox.setText("");
		buttonItalicCheckBox.setAction(buttonFontChange);
		buttonItalicCheckBox.setText("");
		FocusListener buttonFontFocusListener = new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				buttonFontPropertyChanged();
			}
		};
		buttonFontCombo.addFocusListener(buttonFontFocusListener);
		buttonSizeCombo.addFocusListener(buttonFontFocusListener);

		chooseColourButton.setAction(actions.get("displayColourChooser"));
	}

	private void createUI() {
		JPanel overlayDefaultsPanel = new JPanel();
		String overlayDefaultsTitle = _resources.getString("imageSettingsOverlayDefaults.title");
		overlayDefaultsPanel.setBorder(new TitledBorder(null, overlayDefaultsTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel imagePathPanel = new JPanel();
		String imagePathTitle = _resources.getString("imageSettingsImagePath.title");
		imagePathPanel.setBorder(new TitledBorder(null, imagePathTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel overlayFontDefaultsPanel = new JPanel();
		String overlayFontTitle = _resources.getString("imageSettingsOverlayFonts.title");
		overlayFontDefaultsPanel.setBorder(new TitledBorder(null, overlayFontTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(overlayDefaultsPanel, GroupLayout.PREFERRED_SIZE, 236, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								groupLayout.createParallelGroup(Alignment.LEADING).addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
										.addComponent(overlayFontDefaultsPanel, GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
										.addComponent(imagePathPanel, GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addGap(10)
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.LEADING, false)
										.addComponent(overlayDefaultsPanel, 0, 0, Short.MAX_VALUE)
										.addGroup(
												groupLayout.createSequentialGroup().addComponent(imagePathPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(overlayFontDefaultsPanel, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)))
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		btnOk = new JButton("Ok");
		buttonPanel.add(btnOk);

		btnCancel = new JButton("Cancel");
		buttonPanel.add(btnCancel);

		btnApply = new JButton("Apply");
		btnApply.setHorizontalAlignment(SwingConstants.RIGHT);
		buttonPanel.add(btnApply);

		chckbxCentreInBox = new JCheckBox();
		chckbxCentreInBox.setName("imageSettingsCentreInBox");
		chckbxIncludeComments = new JCheckBox();
		chckbxIncludeComments.setName("imageSettingsIncludeComments");
		chckbxOmitDescription = new JCheckBox();
		chckbxOmitDescription.setName("imageSettingsOmitDescription");
		chckbxUseIntegralHeight = new JCheckBox();
		chckbxUseIntegralHeight.setName("imageSettingsUseIntegralHeight");
		chckbxHotspotsPopUp = new JCheckBox();
		chckbxHotspotsPopUp.setName("imageSettingsHotspotsPopUp");
		chckbxCustomPopupColour = new JCheckBox();
		chckbxCustomPopupColour.setName("imageSettingsCustomPopupColour");
		JPanel panel = new JPanel();

		selectedColourLabel = new JLabel("");
		selectedColourLabel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		selectedColourLabel.setPreferredSize(new Dimension(25, 25));
		selectedColourLabel.setOpaque(true);
		selectedColourLabel.setEnabled(false);

		chooseColourButton = new JButton();
		chooseColourButton.setEnabled(false);

		JLabel lblButtonAlignment = new JLabel();
		lblButtonAlignment.setName("imageSettingsButtonAlignment");
		buttonAlignmentCombo = new JComboBox();
		buttonAlignmentCombo.setModel(new ButtonAlignmentModel());
		buttonAlignmentCombo.setRenderer(new ButtonAlignmentRenderer());
		GroupLayout gl_overlayDefaultsPanel = new GroupLayout(overlayDefaultsPanel);
		gl_overlayDefaultsPanel.setHorizontalGroup(gl_overlayDefaultsPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_overlayDefaultsPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_overlayDefaultsPanel.createParallelGroup(Alignment.LEADING).addGroup(gl_overlayDefaultsPanel.createSequentialGroup().addGap(6).addComponent(lblButtonAlignment))
										.addComponent(chckbxIncludeComments).addComponent(chckbxOmitDescription).addComponent(chckbxUseIntegralHeight).addComponent(chckbxHotspotsPopUp)
										.addComponent(chckbxCustomPopupColour).addComponent(buttonAlignmentCombo, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
										.addComponent(panel, GroupLayout.PREFERRED_SIZE, 193, GroupLayout.PREFERRED_SIZE).addComponent(chckbxCentreInBox)).addContainerGap(25, Short.MAX_VALUE)));
		gl_overlayDefaultsPanel.setVerticalGroup(gl_overlayDefaultsPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_overlayDefaultsPanel.createSequentialGroup().addGap(10).addComponent(chckbxCentreInBox).addPreferredGap(ComponentPlacement.RELATED).addComponent(chckbxIncludeComments)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(chckbxOmitDescription).addPreferredGap(ComponentPlacement.RELATED).addComponent(chckbxUseIntegralHeight)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(chckbxHotspotsPopUp).addPreferredGap(ComponentPlacement.RELATED).addComponent(chckbxCustomPopupColour)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(panel, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE).addGap(12).addComponent(lblButtonAlignment)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(buttonAlignmentCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(144, Short.MAX_VALUE)));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup().addContainerGap().addComponent(selectedColourLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(chooseColourButton).addGap(68)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_panel.createParallelGroup(Alignment.LEADING).addComponent(chooseColourButton)
										.addComponent(selectedColourLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);
		overlayDefaultsPanel.setLayout(gl_overlayDefaultsPanel);

		JLabel lblFontName = new JLabel();
		lblFontName.setName("imageSettingsFontName");
		JLabel lblSize = new JLabel();
		lblSize.setName("imageSettingsFontSize");
		JLabel lblBold = new JLabel();
		lblBold.setName("imageSettingsFontBold");
		JLabel lblItalic = new JLabel();
		lblItalic.setName("imageSettingsFontItalic");

		JLabel featureLabel = new JLabel();
		featureLabel.setName("imageSettingsFeatureFont");
		buttonFontCombo = new JComboBox();
		buttonFontCombo.setModel(new FontFamilyModel());

		featureSizeCombo = new JComboBox();
		featureSizeCombo.setModel(new FontSizeModel());

		buttonBoldCheckBox = new JCheckBox("");

		featureItalicCheckBox = new JCheckBox("");

		JLabel buttonLabel = new JLabel();
		buttonLabel.setName("imageSettingsButtonFont");

		featureFontCombo = new JComboBox();
		featureFontCombo.setModel(new FontFamilyModel());

		buttonSizeCombo = new JComboBox();
		buttonSizeCombo.setModel(new FontSizeModel());

		featureBoldCheckBox = new JCheckBox("");

		buttonItalicCheckBox = new JCheckBox("");

		JLabel defaultLabel = new JLabel();
		defaultLabel.setName("imageSettingsDefaultFont");
		defaultFontCombo = new JComboBox();
		defaultFontCombo.setModel(new FontFamilyModel());

		defaultSizeCombo = new JComboBox();
		defaultSizeCombo.setModel(new FontSizeModel());

		defaultBoldCheckBox = new JCheckBox("");

		defaultItalicCheckBox = new JCheckBox("");

		JLabel lblSample = new JLabel();
		lblSample.setName("imageSettingsSample");
		sampleTextField = new JTextField("");

		chckbxSaveSampleAs = new JCheckBox();
		chckbxSaveSampleAs.setSelected(true);
		chckbxSaveSampleAs.setName("imageSettingsSaveSampleAs");

		GroupLayout gl_overlayFontDefaultsPanel = new GroupLayout(overlayFontDefaultsPanel);
		gl_overlayFontDefaultsPanel.setHorizontalGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_overlayFontDefaultsPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_overlayFontDefaultsPanel
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												gl_overlayFontDefaultsPanel
														.createSequentialGroup()
														.addGroup(
																gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING).addComponent(defaultLabel).addComponent(featureLabel)
																		.addComponent(buttonLabel))
														.addPreferredGap(ComponentPlacement.RELATED)
														.addGroup(
																gl_overlayFontDefaultsPanel
																		.createParallelGroup(Alignment.TRAILING)
																		.addComponent(lblFontName, 0, 345, Short.MAX_VALUE)
																		.addGroup(
																				gl_overlayFontDefaultsPanel
																						.createSequentialGroup()
																						.addGroup(
																								gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.TRAILING)
																										.addComponent(defaultFontCombo, Alignment.LEADING, 0, 339, Short.MAX_VALUE)
																										.addComponent(featureFontCombo, 0, 339, Short.MAX_VALUE)
																										.addComponent(buttonFontCombo, 0, 339, Short.MAX_VALUE))
																						.addPreferredGap(ComponentPlacement.RELATED)))
														.addGroup(
																gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING).addComponent(lblSize, 0, 94, Short.MAX_VALUE)
																		.addComponent(defaultSizeCombo, 0, 94, Short.MAX_VALUE).addComponent(featureSizeCombo, 0, 94, Short.MAX_VALUE)
																		.addComponent(buttonSizeCombo, 0, 94, Short.MAX_VALUE)))
										.addGroup(
												gl_overlayFontDefaultsPanel
														.createSequentialGroup()
														.addComponent(lblSample)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addGroup(
																gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING).addComponent(chckbxSaveSampleAs)
																		.addComponent(sampleTextField, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))))
						.addGap(18)
						.addGroup(
								gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING, false).addComponent(featureBoldCheckBox, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
										.addComponent(defaultBoldCheckBox, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
										.addComponent(buttonBoldCheckBox, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblBold, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.TRAILING)
										.addComponent(buttonItalicCheckBox, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
										.addComponent(featureItalicCheckBox, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblItalic, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
										.addComponent(defaultItalicCheckBox, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)).addContainerGap()));
		gl_overlayFontDefaultsPanel.setVerticalGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_overlayFontDefaultsPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.BASELINE).addComponent(lblSize).addComponent(lblFontName).addComponent(lblItalic).addComponent(lblBold))
						.addGap(10)
						.addGroup(
								gl_overlayFontDefaultsPanel
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.BASELINE)
														.addComponent(defaultFontCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(defaultSizeCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(defaultLabel))
										.addComponent(defaultItalicCheckBox).addComponent(defaultBoldCheckBox))
						.addGap(10)
						.addGroup(
								gl_overlayFontDefaultsPanel
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.BASELINE)
														.addComponent(featureFontCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(featureSizeCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(featureLabel))
										.addComponent(featureItalicCheckBox).addComponent(featureBoldCheckBox))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(
								gl_overlayFontDefaultsPanel
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												gl_overlayFontDefaultsPanel
														.createSequentialGroup()
														.addGroup(
																gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.BASELINE)
																		.addComponent(buttonSizeCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																		.addComponent(buttonFontCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																		.addComponent(buttonLabel))
														.addGap(18)
														.addGroup(
																gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.BASELINE)
																		.addComponent(sampleTextField, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE).addComponent(lblSample))
														.addPreferredGap(ComponentPlacement.RELATED).addComponent(chckbxSaveSampleAs)).addComponent(buttonItalicCheckBox)
										.addComponent(buttonBoldCheckBox)).addContainerGap(16, Short.MAX_VALUE)));
		overlayFontDefaultsPanel.setLayout(gl_overlayFontDefaultsPanel);

		imagePathTextField = new JTextField();
		imagePathTextField.setColumns(10);

		imagePathButton = new JButton("New button");
		GroupLayout gl_imagePathPanel = new GroupLayout(imagePathPanel);
		gl_imagePathPanel.setHorizontalGroup(gl_imagePathPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_imagePathPanel.createSequentialGroup().addContainerGap().addComponent(imagePathTextField, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED).addComponent(imagePathButton)));
		gl_imagePathPanel.setVerticalGroup(gl_imagePathPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_imagePathPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_imagePathPanel.createParallelGroup(Alignment.BASELINE)
										.addComponent(imagePathTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(imagePathButton))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
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
		String family = (String) name.getSelectedItem();
		int size = (Integer) sizeCombo.getSelectedItem();
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
		_imageSettings.setResourcePath(imagePathTextField.getText());

		Font defaultFont = fontFromComponents(defaultFontCombo, defaultSizeCombo, defaultBoldCheckBox, defaultItalicCheckBox);
		Font featureFont = fontFromComponents(featureFontCombo, featureSizeCombo, featureBoldCheckBox, featureItalicCheckBox);
		Font buttonFont = fontFromComponents(buttonFontCombo, buttonSizeCombo, buttonBoldCheckBox, buttonItalicCheckBox);
		if (chckbxSaveSampleAs.isSelected()) {
			_imageSettings.setDefaultFont(defaultFont, _defaultFontCommment);
			_imageSettings.setDefaultFeatureFont(featureFont, _featureFontComment);
			_imageSettings.setDefaultButtonFont(buttonFont, _buttonFontComment);

		} else {
			_imageSettings.setDefaultFont(defaultFont);
			_imageSettings.setDefaultFeatureFont(featureFont);
			_imageSettings.setDefaultButtonFont(buttonFont);
		}

		_imageSettings.setCentreInBox(chckbxCentreInBox.isSelected());
		_imageSettings.setIncludeComments(chckbxIncludeComments.isSelected());
		_imageSettings.setOmitDescription(chckbxOmitDescription.isSelected());
		_imageSettings.setUseIntegralHeight(chckbxUseIntegralHeight.isSelected());
		_imageSettings.setHotspotsPopup(chckbxHotspotsPopUp.isSelected());
		_imageSettings.setUseCustomPopupColour(chckbxCustomPopupColour.isSelected());
		if (_imageSettings.getUseCustomPopupColour()) {
			_imageSettings.setCustomPopupColour(selectedColourLabel.getBackground());
		}
		_imageSettings.setButtonAlignment((ButtonAlignment) buttonAlignmentCombo.getModel().getSelectedItem());

		_model.setImageSettings(_imageSettings);
	}

    @Override
    public String getViewTitle() {
        return _resources.getString("imageSettingsDialog.title");
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
			return MAX_SIZE - MIN_SIZE + 1;
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

	private class ButtonAlignmentRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 7525531913965500140L;
		private final String RESOURCE_PREFIX = "imageSettingsDialog.buttonAlignment.";

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			ButtonAlignment align = (ButtonAlignment) value;

			String suffix;
			switch (align) {
			case ALIGN_HORIZONTALLY:
				suffix = "alignHorizontally";
				break;
			case ALIGN_VERTICALLY:
				suffix = "alignVertically";
				break;
			case NO_ALIGN:
			default:
				suffix = "noAlign";
				break;
			}
			String displayValue = _resources.getString(RESOURCE_PREFIX + suffix);
			return super.getListCellRendererComponent(list, displayValue, index, isSelected, cellHasFocus);
		}

	}
}

package au.org.ala.delta.editor.ui.image;

import java.awt.FlowLayout;

import javax.swing.JDialog;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import javax.swing.border.BevelBorder;

public class ImageSettingsDialog extends JDialog {
	
	private static final long serialVersionUID = 8761867230419524659L;
	private JTextField textField;
	private JComboBox defaultFontCombo;

	public ImageSettingsDialog() {
		
		createUI();
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
		
		JButton btnOk = new JButton("Ok");
		buttonPanel.add(btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		buttonPanel.add(btnCancel);
		
		JButton btnApply = new JButton("Apply");
		btnApply.setHorizontalAlignment(SwingConstants.RIGHT);
		buttonPanel.add(btnApply);
		
		JCheckBox chckbxCentreInBox = new JCheckBox("Centre in box");
		
		JCheckBox chckbxIncludeComments = new JCheckBox("Include Comments");
		
		JCheckBox chckbxOmitDescription = new JCheckBox("Omit description");
		
		JCheckBox chckbxUseIntegralHeight = new JCheckBox("Use integral height");
		
		JCheckBox chckbxHotspotsPopUp = new JCheckBox("Hotspots pop up");
		
		JCheckBox chckbxCustomPopupColour = new JCheckBox("Custom popup colour");
		
		JPanel panel = new JPanel();
		
		JLabel label = new JLabel("");
		label.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		label.setPreferredSize(new Dimension(25, 25));
		label.setOpaque(true);
		label.setEnabled(false);
		
		JButton button = new JButton("Choose Colour");
		button.setEnabled(false);
		
		JLabel lblButtonAlignment = new JLabel("Button alignment");
		
		JComboBox comboBox = new JComboBox();
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
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
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
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(144, Short.MAX_VALUE))
		);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(label, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(button)
					.addGap(68))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(button)
						.addComponent(label, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		overlayDefaultsPanel.setLayout(gl_overlayDefaultsPanel);
		
		JLabel lblFontName = new JLabel("Font name:");
		
		JLabel lblSize = new JLabel("Size");
		
		JLabel lblBold = new JLabel("Bold");
		
		JLabel lblItalic = new JLabel("Italic");
		
		JLabel lblDefault = new JLabel("Default:");
		
		defaultFontCombo = new JComboBox();
		
		JComboBox defaultSizeCombo = new JComboBox();
		
		JCheckBox defaultBoldCheckBox = new JCheckBox("");
		
		JCheckBox defaultItalicCheckBox = new JCheckBox("");
		
		JLabel lblFeature = new JLabel("Feature:");
		
		JComboBox featureFontCombo = new JComboBox();
		
		JComboBox featureSizeCombo = new JComboBox();
		
		JCheckBox featureBoldCheckBox = new JCheckBox("");
		
		JCheckBox featureItalicCheckBox = new JCheckBox("");
		
		JLabel lblButton = new JLabel("Button:");
		
		JComboBox buttonFontCombo = new JComboBox();
		
		JComboBox buttonSizeCombo = new JComboBox();
		
		JCheckBox buttonBoldCheckBox = new JCheckBox("");
		
		JCheckBox buttonItalicCheckBox = new JCheckBox("");
		
		JLabel lblSample = new JLabel("Sample:");
		
		JTextField lblNewLabel = new JTextField("");
		
		JCheckBox chckbxSaveSampleAs = new JCheckBox("Save sample as comment");
		
		
		GroupLayout gl_overlayFontDefaultsPanel = new GroupLayout(overlayFontDefaultsPanel);
		gl_overlayFontDefaultsPanel.setHorizontalGroup(
			gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_overlayFontDefaultsPanel.createSequentialGroup()
					.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_overlayFontDefaultsPanel.createSequentialGroup()
							.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_overlayFontDefaultsPanel.createSequentialGroup()
									.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
										.addComponent(lblButton)
										.addComponent(lblDefault))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblFontName, 0, 334, Short.MAX_VALUE)
										.addGroup(gl_overlayFontDefaultsPanel.createSequentialGroup()
											.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.TRAILING)
												.addComponent(buttonFontCombo, Alignment.LEADING, 0, 328, Short.MAX_VALUE)
												.addComponent(featureFontCombo, 0, 328, Short.MAX_VALUE))
											.addPreferredGap(ComponentPlacement.RELATED))))
								.addGroup(gl_overlayFontDefaultsPanel.createSequentialGroup()
									.addComponent(lblFeature)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(defaultFontCombo, 0, 328, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSize, 0, 83, Short.MAX_VALUE)
								.addComponent(buttonSizeCombo, 0, 83, Short.MAX_VALUE)
								.addComponent(defaultSizeCombo, 0, 83, Short.MAX_VALUE)
								.addComponent(featureSizeCombo, 0, 83, Short.MAX_VALUE)))
						.addGroup(gl_overlayFontDefaultsPanel.createSequentialGroup()
							.addComponent(lblSample)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(chckbxSaveSampleAs)
								.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE))))
					.addGap(18)
					.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(featureBoldCheckBox, 0, 50, Short.MAX_VALUE)
						.addComponent(buttonBoldCheckBox, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
						.addComponent(defaultBoldCheckBox, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblBold, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(featureItalicCheckBox, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
						.addComponent(defaultItalicCheckBox, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblItalic, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
						.addComponent(buttonItalicCheckBox, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
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
							.addComponent(lblButton)
							.addComponent(buttonFontCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(buttonSizeCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(buttonItalicCheckBox)
						.addComponent(buttonBoldCheckBox))
					.addGap(10)
					.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(featureFontCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblDefault)
							.addComponent(defaultSizeCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(defaultItalicCheckBox)
						.addComponent(featureBoldCheckBox))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_overlayFontDefaultsPanel.createSequentialGroup()
							.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(defaultFontCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(featureSizeCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblFeature))
							.addGap(18)
							.addGroup(gl_overlayFontDefaultsPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblSample)
								.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chckbxSaveSampleAs))
						.addComponent(featureItalicCheckBox)
						.addComponent(defaultBoldCheckBox))
					.addContainerGap(16, Short.MAX_VALUE))
		);
		overlayFontDefaultsPanel.setLayout(gl_overlayFontDefaultsPanel);
		
		textField = new JTextField();
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("New button");
		GroupLayout gl_imagePathPanel = new GroupLayout(imagePathPanel);
		gl_imagePathPanel.setHorizontalGroup(
			gl_imagePathPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_imagePathPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton))
		);
		gl_imagePathPanel.setVerticalGroup(
			gl_imagePathPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_imagePathPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_imagePathPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnNewButton))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		imagePathPanel.setLayout(gl_imagePathPanel);
		getContentPane().setLayout(groupLayout);
	}
}

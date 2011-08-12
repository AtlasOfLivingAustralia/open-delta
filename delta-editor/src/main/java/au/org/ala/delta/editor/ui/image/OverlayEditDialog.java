package au.org.ala.delta.editor.ui.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;

import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.ui.rtf.RtfEditor;

/**
 * Provides a user interface for editing the details of an image overlay.
 */
public class OverlayEditDialog extends JDialog {

	private static final long serialVersionUID = 3460369707621339162L;
	private ImageOverlay _overlay;
	private Image _image;
	private ResourceMap _resources;
	
	private JFormattedTextField xDimension;
	private JFormattedTextField yDimension;
	private JFormattedTextField wDimension;
	private JFormattedTextField hDimension;
	private JSpinner stateNumberSpinner;
	private RtfEditor textEditor;
	private JCheckBox chckbxCentreInBox;
	private JCheckBox chckbxIncludeComments;
	private JCheckBox chckbxOmitDescription;
	private JCheckBox chckbxUseIntegralHeight;
	private JButton btnOk;
	private JButton btnCancel;
	private JButton btnApply;
	private JLabel stateNumLabel;
	private JLabel lblImageUnits;
	
	public OverlayEditDialog(Window parent, Image image, ImageOverlay overlay) {
		super(parent);
		setName("overlayEditDialog");
		_image = image;
		_overlay = overlay;
		_resources = Application.getInstance().getContext().getResourceMap();
		createUI();
		addEventHandlers();
		updateGUI();
	}
	
	private void addEventHandlers() {
		ActionMap actions = Application.getInstance().getContext().getActionMap(this);
		btnOk.setAction(actions.get("okOverlayChanges"));
		btnApply.setAction(actions.get("applyOverlayChanges"));
		btnCancel.setAction(actions.get("cancelOverlayChanges"));
	}
	
	private void createUI() {
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		
		stateNumLabel = new JLabel();
		stateNumLabel.setName("overlayEditStateNumberLabel");
		
		stateNumberSpinner = new JSpinner();
		
		textEditor = new RtfEditor();
		textEditor.setBackground(new Color(255, 255, 255));
		JScrollPane textScroller = new JScrollPane(textEditor);
		
		JLabel lblAdditionalText = new JLabel();
		lblAdditionalText.setName("overlayEditAdditionalTextLabel");
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(null);
		
		JPanel panel_2 = new JPanel();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panel.createSequentialGroup()
								.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 412, Short.MAX_VALUE)
								.addContainerGap())
							.addGroup(gl_panel.createSequentialGroup()
								.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
									.addComponent(stateNumberSpinner)
									.addComponent(stateNumLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGap(19)
								.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
									.addComponent(textScroller, GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
									.addComponent(lblAdditionalText))
								.addContainerGap()))))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(10)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(stateNumLabel)
						.addComponent(lblAdditionalText))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(textScroller, GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
						.addComponent(stateNumberSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		btnOk = new JButton("OK");
		
		btnCancel = new JButton("Cancel");
		
		btnApply = new JButton("Apply");
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap(18, Short.MAX_VALUE)
					.addComponent(btnOk)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCancel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnApply)
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOk)
						.addComponent(btnCancel)
						.addComponent(btnApply))
					.addContainerGap(7, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		
		chckbxCentreInBox = new JCheckBox("Centre in box");
		chckbxCentreInBox.setName("overlayEditCentreInBoxLabel");
		chckbxIncludeComments = new JCheckBox("Include comments");
		chckbxIncludeComments.setName("overlayEditIncludeCommentsLabel");
		chckbxOmitDescription = new JCheckBox("Omit description");
		chckbxOmitDescription.setName("overlayEditOmitDescriptionLabel");
		chckbxUseIntegralHeight = new JCheckBox("Use integral height");
		chckbxUseIntegralHeight.setName("overlayEditUseIntegralHeightLabel");
		JPanel dimensionsPanel = new JPanel();
		String dimensionsTitle = _resources.getString("overlayEditDimensionsLabel.text");
		dimensionsPanel.setBorder(new TitledBorder(null, dimensionsTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxCentreInBox)
						.addComponent(chckbxIncludeComments)
						.addComponent(chckbxOmitDescription)
						.addComponent(chckbxUseIntegralHeight))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(dimensionsPanel, GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(dimensionsPanel, GroupLayout.PREFERRED_SIZE, 123, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(chckbxCentreInBox)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chckbxIncludeComments)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chckbxOmitDescription)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chckbxUseIntegralHeight)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setName("overlayEditXLabel");
		xDimension = numberField();
		xDimension.setColumns(10);
		
		JLabel lblY = new JLabel();
		lblY.setName("overlayEditYLabel");
		yDimension = numberField();
		yDimension.setColumns(10);
		
		
		JLabel lblW = new JLabel();
		lblW.setName("overlayEditWidthLabel");
		wDimension = numberField();
		wDimension.setColumns(10);
		
		
		JLabel lblH = new JLabel();
		lblH.setName("overlayEditHeightLabel");
		hDimension = numberField();
		hDimension.setColumns(10);
		
		
		lblImageUnits = new JLabel();
		lblImageUnits.setText(_resources.getString("overlayEditImageUnitsLabel.text"));
		GroupLayout gl_dimensionsPanel = new GroupLayout(dimensionsPanel);
		gl_dimensionsPanel.setHorizontalGroup(
			gl_dimensionsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_dimensionsPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_dimensionsPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_dimensionsPanel.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(xDimension, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblW)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(wDimension, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_dimensionsPanel.createSequentialGroup()
							.addComponent(lblY)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(yDimension, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblH)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(hDimension, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(lblImageUnits)))
					.addContainerGap(124, Short.MAX_VALUE))
		);
		gl_dimensionsPanel.setVerticalGroup(
			gl_dimensionsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_dimensionsPanel.createSequentialGroup()
					.addGroup(gl_dimensionsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(xDimension, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblW)
						.addComponent(wDimension, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_dimensionsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblY)
						.addComponent(yDimension, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblH)
						.addComponent(hDimension, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblImageUnits))
					.addContainerGap(33, Short.MAX_VALUE))
		);
		dimensionsPanel.setLayout(gl_dimensionsPanel);
		panel_1.setLayout(gl_panel_1);
		panel.setLayout(gl_panel);
	}
	
	private JFormattedTextField numberField() {
		JFormattedTextField field = new JFormattedTextField();
		field.setValue(0);
		return field;
	}
	
	private void updateGUI() {
		if (_overlay.isType(OverlayType.OLSTATE)) {
			stateNumberSpinner.setValue(_overlay.stateId);
		}
		else {
			stateNumLabel.setVisible(false);
			stateNumberSpinner.setVisible(false);
		}
		
		textEditor.setText(_overlay.overlayText);
		chckbxCentreInBox.setSelected(_overlay.centreText());
		chckbxIncludeComments.setSelected(_overlay.includeComments());
		chckbxOmitDescription.setSelected(_overlay.omitDescription());
		chckbxUseIntegralHeight.setSelected(_overlay.integralHeight());
		
		xDimension.setValue(_overlay.getX());
		yDimension.setValue(_overlay.getY());
		wDimension.setValue(_overlay.getWidth());
		hDimension.setValue(_overlay.getHeight());
		String key = "overlayEditImageUnitsLabel.text";
		if (_overlay.integralHeight()) {
			key = "overlayEditLinesLabel.text";
		}
		lblImageUnits.setText(_resources.getString(key));
	}
	
	@Action
	public void applyOverlayChanges() {
		applyChanges();
	}
	
	@Action
	public void okOverlayChanges() {
		applyChanges();
		setVisible(false);
	}
	
	private void applyChanges() {
		if (_overlay.isType(OverlayType.OLSTATE)) {
			_overlay.stateId = (Integer)stateNumberSpinner.getValue();
		}
		_overlay.overlayText = textEditor.getRtfTextBody();
		_overlay.setCentreText(chckbxCentreInBox.isSelected());
		_overlay.setIncludeComments(chckbxIncludeComments.isSelected());
		_overlay.setOmitDescription(chckbxOmitDescription.isSelected());
		_overlay.setIntegralHeight(chckbxUseIntegralHeight.isSelected());
		
		_overlay.setX((Integer)xDimension.getValue());
		_overlay.setY((Integer)yDimension.getValue());
		_overlay.setWidth((Integer)wDimension.getValue());
		_overlay.setHeight((Integer)hDimension.getValue());
		
		_image.updateOverlay(_overlay);
		
	}
	
	@Action
	public void cancelOverlayChanges() {
		setVisible(false);
	}
}


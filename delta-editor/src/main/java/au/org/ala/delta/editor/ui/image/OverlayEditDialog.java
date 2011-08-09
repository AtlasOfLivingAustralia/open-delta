package au.org.ala.delta.editor.ui.image;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

import org.jdesktop.application.Action;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.ui.rtf.RtfEditor;

/**
 * Provides a user interface for editing the details of an image overlay.
 */
public class OverlayEditDialog extends JDialog {

	private static final long serialVersionUID = 3460369707621339162L;
	private ImageOverlay _overlay;
	private JTextField xDimension;
	private JTextField yDimension;
	private JTextField wDimension;
	private JTextField hDimension;
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
	
	public OverlayEditDialog(ImageOverlay overlay) {
		
		_overlay = overlay;
		createUI();
		addEventHandlers();
		updateGUI();
	}
	
	
	private void addEventHandlers() {
		
	}
	
	
	private void createUI() {
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		
		stateNumLabel = new JLabel("State Number:");
		
		stateNumberSpinner = new JSpinner();
		
		textEditor = new RtfEditor();
		textEditor.setBackground(new Color(255, 255, 255));
		JScrollPane textScroller = new JScrollPane(textEditor);
		
		JLabel lblAdditionalText = new JLabel("Additional Text:");
		
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
		
		chckbxIncludeComments = new JCheckBox("Include comments");
		
		chckbxOmitDescription = new JCheckBox("Omit description");
		
		chckbxUseIntegralHeight = new JCheckBox("Use integral height");
		
		JPanel dimensionsPanel = new JPanel();
		dimensionsPanel.setBorder(new TitledBorder(null, "Dimensions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
		
		JLabel lblNewLabel = new JLabel("X:");
		
		xDimension = new JTextField();
		xDimension.setColumns(10);
		
		JLabel lblY = new JLabel("Y:");
		
		yDimension = new JTextField();
		yDimension.setColumns(10);
		
		JLabel lblW = new JLabel("W:");
		
		wDimension = new JTextField();
		wDimension.setColumns(10);
		
		JLabel lblH = new JLabel("H:");
		
		hDimension = new JTextField();
		hDimension.setColumns(10);
		
		JLabel lblImageUnits = new JLabel("Image Units");
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
		
		xDimension.setText(Integer.toString(_overlay.getX()));
		yDimension.setText(Integer.toString(_overlay.getY()));
		wDimension.setText(Integer.toString(_overlay.getWidth()));
		hDimension.setText(Integer.toString(_overlay.getHeight()));
		
	}
	
	@Action
	public void applyOverlayChanges() {
		
	}
}

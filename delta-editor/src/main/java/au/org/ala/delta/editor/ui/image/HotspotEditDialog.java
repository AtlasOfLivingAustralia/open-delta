package au.org.ala.delta.editor.ui.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayLocation;
import au.org.ala.delta.model.image.OverlayLocation.OLDrawType;

/**
 * Provides a user interface for editing the details of an overlay hotspot.
 */
public class HotspotEditDialog extends JDialog {

	private static final long serialVersionUID = 3460369707621339162L;
	private OverlayLocation _hotSpot;
	private Image _image;
	private int _index;
	private ImageOverlay _overlay;
	private ResourceMap _resources;
	
	private JFormattedTextField xDimension;
	private JFormattedTextField yDimension;
	private JFormattedTextField wDimension;
	private JFormattedTextField hDimension;
	private JCheckBox popupCheckBox;
	private JCheckBox useCustomColourCheckBox;
	private JButton btnOk;
	private JButton btnCancel;
	private JButton btnApply;
	private JLabel lblImageUnits;
	private JPanel panel_4;
	private JLabel hotSpotColourLabel;
	private JButton btnChooseColour;
	private JRadioButton rdbtnRectangle;
	private JRadioButton rdbtnEllipse;
	
	public HotspotEditDialog(Window parent, Image image, ImageOverlay overlay, int hotSpotIndex) {
		super(parent);
		setName("hotspotEditDialog");
		_image = image;
		_overlay = overlay;
		_index = hotSpotIndex;
		_hotSpot = new OverlayLocation();
		_hotSpot.copy(overlay.getLocation(hotSpotIndex));
		_resources = Application.getInstance().getContext().getResourceMap();
		setTitle(_resources.getString("hotSpotEditDialog.title", overlay.stateId));
		createUI();
		addEventHandlers();
		updateGUI();
	}
	
	private void addEventHandlers() {
		ButtonGroup shapes = new ButtonGroup();
		shapes.add(rdbtnEllipse);
		shapes.add(rdbtnRectangle);
		
		ActionMap actions = Application.getInstance().getContext().getActionMap(this);
		btnOk.setAction(actions.get("okOverlayChanges"));
		btnApply.setAction(actions.get("applyOverlayChanges"));
		btnCancel.setAction(actions.get("cancelOverlayChanges"));
		btnChooseColour.setAction(actions.get("displayColourChooser"));
		popupCheckBox.setAction(actions.get("popupSelected"));
		useCustomColourCheckBox.setAction(actions.get("useCustomColourSelected"));
		rdbtnEllipse.setAction(actions.get("ellipseSelected"));
		rdbtnRectangle.setAction(actions.get("rectangleSelected"));
		
	}
	
	private void createUI() {
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(null);
		
		JPanel panel_2 = new JPanel();
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Hotspot Shape", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel_1, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 438, Short.MAX_VALUE)
						.addComponent(panel_3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
						.addComponent(panel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_3, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
					.addGap(18)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		rdbtnRectangle = new JRadioButton("Rectangle");
		
		rdbtnEllipse = new JRadioButton("Ellipse");
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addComponent(rdbtnRectangle)
						.addComponent(rdbtnEllipse))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addContainerGap()
					.addComponent(rdbtnRectangle)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rdbtnEllipse)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_3.setLayout(gl_panel_3);
		
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
		
		popupCheckBox = new JCheckBox("Pop up");
		useCustomColourCheckBox = new JCheckBox("Use custom colour");
		JPanel dimensionsPanel = new JPanel();
		String dimensionsTitle = _resources.getString("overlayEditDimensionsLabel.text");
		dimensionsPanel.setBorder(new TitledBorder(null, dimensionsTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panel_4 = new JPanel();
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(10)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(popupCheckBox)
						.addComponent(useCustomColourCheckBox)
						.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(13)
					.addComponent(dimensionsPanel, GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(dimensionsPanel, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(popupCheckBox)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(useCustomColourCheckBox)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		hotSpotColourLabel = new JLabel("");
		hotSpotColourLabel.setPreferredSize(new Dimension(20, 20));
		hotSpotColourLabel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_4.add(hotSpotColourLabel);
		
		btnChooseColour = new JButton("Choose Colour");
		panel_4.add(btnChooseColour);
		
		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setName("overlayEditXLabel");
		xDimension = new JFormattedTextField();
		xDimension.setColumns(10);
		
		JLabel lblY = new JLabel();
		lblY.setName("overlayEditYLabel");
		yDimension = new JFormattedTextField();
		yDimension.setColumns(10);
		
		
		JLabel lblW = new JLabel();
		lblW.setName("overlayEditWidthLabel");
		wDimension = new JFormattedTextField();
		wDimension.setColumns(10);
		
		
		JLabel lblH = new JLabel();
		lblH.setName("overlayEditHeightLabel");
		hDimension = new JFormattedTextField();
		hDimension.setColumns(10);
		
		
		lblImageUnits = new JLabel();
		lblImageUnits.setName("overlayEditImageUnitsLabel");
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
		hotSpotColourLabel.setOpaque(true);
		if (_hotSpot.drawType == OLDrawType.ellipse) {
			rdbtnEllipse.setSelected(true);
		}
		else {
			rdbtnRectangle.setSelected(true);
		}
		popupCheckBox.setSelected(_hotSpot.isPopup());
		useCustomColourCheckBox.setEnabled(false);
		hotSpotColourLabel.setEnabled(false);
		btnChooseColour.setEnabled(false);
		if (_hotSpot.isPopup()) {
			useCustomColourCheckBox.setEnabled(true);
			useCustomColourCheckBox.setSelected(_hotSpot.isColorSet());
			hotSpotColourLabel.setEnabled(true);
			hotSpotColourLabel.setBackground(new Color(_hotSpot.getColor()));
			
			if (_hotSpot.isColorSet()) {
				btnChooseColour.setEnabled(true);
			}
		}
		
		wDimension.setValue(_hotSpot.W);
		hDimension.setValue(_hotSpot.H);
		String key = "overlayEditImageUnitsLabel.text";
		if (_hotSpot.integralHeight()) {
			key = "overlayEditLinesLabel.text";
		}
		lblImageUnits.setText(_resources.getString(key));
		
		xDimension.setValue(_hotSpot.X);
		yDimension.setValue(_hotSpot.Y);
		
		
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
	
	@Action
	public void displayColourChooser() {
		Color c = JColorChooser.showDialog(this, "Hotspot color", new Color(_hotSpot.getColor()));
		_hotSpot.setColor(c.getRGB());
		updateGUI();
	}
	
	@Action
	public void popupSelected() {
		_hotSpot.setPopup(popupCheckBox.isSelected());
		updateGUI();
	}
	
	@Action
	public void useCustomColourSelected() {
		_hotSpot.setUseCustomColour(useCustomColourCheckBox.isSelected());
		updateGUI();
	}
	
	@Action
	public void ellipseSelected() {
		_hotSpot.drawType = OLDrawType.ellipse;
	}

	@Action
	public void rectangleSelected() {
		_hotSpot.drawType = OLDrawType.rectangle;
	}
		
	
	private void applyChanges() {
		
		_hotSpot.setCentreText(popupCheckBox.isSelected());
		_hotSpot.setIncludeComments(useCustomColourCheckBox.isSelected());
		_hotSpot.setW((Short)wDimension.getValue());
		_hotSpot.setH((Short)hDimension.getValue());
		
		_hotSpot.setX((Short)xDimension.getValue());
		_hotSpot.setY((Short)yDimension.getValue());
		
		_overlay.getLocation(_index).copy(_hotSpot);
		_image.updateOverlay(_overlay);
	}
	
	@Action
	public void cancelOverlayChanges() {
		setVisible(false);
	}
}


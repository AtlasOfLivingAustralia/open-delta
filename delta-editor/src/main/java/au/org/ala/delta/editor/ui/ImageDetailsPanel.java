package au.org.ala.delta.editor.ui;

import java.awt.Component;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

import au.org.ala.delta.ui.rtf.RtfEditorPane;

/**
 * Displays the details of images associated with an Item or Character.
 */
public class ImageDetailsPanel extends JPanel {
	
	private static final long serialVersionUID = -1973824161019895786L;

	public ImageDetailsPanel() {
		
		JPanel panel = new JPanel();
		
		JPanel buttonPanel = new JPanel();
		
		JPanel panel_2 = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 262, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 265, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
						.addComponent(buttonPanel, 0, 0, Short.MAX_VALUE)
						.addComponent(panel_2, 0, 0, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		JButton btnSettings = new JButton("Settings...");
		
		JButton btnDisplay = new JButton("Display");
		
		JButton btnAdd = new JButton("Add");
		
		JButton btnDelete = new JButton("Delete");
		GroupLayout gl_buttonPanel = new GroupLayout(buttonPanel);
		gl_buttonPanel.setHorizontalGroup(
			gl_buttonPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_buttonPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_buttonPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(btnSettings, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnDisplay, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
						.addComponent(btnAdd, GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
						.addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_buttonPanel.setVerticalGroup(
			gl_buttonPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_buttonPanel.createSequentialGroup()
					.addContainerGap(119, Short.MAX_VALUE)
					.addComponent(btnSettings)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnDisplay)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnAdd)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnDelete)
					.addContainerGap())
		);
		buttonPanel.setLayout(gl_buttonPanel);
		
		JLabel lblSubjectText = new JLabel("Subject text:");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JLabel lblDevelopersNotes = new JLabel("Developer's notes:");
		
		JScrollPane scrollPane_2 = new JScrollPane();
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Image sound", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
						.addComponent(lblDevelopersNotes)
						.addComponent(panel_3, GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
						.addComponent(lblSubjectText))
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblSubjectText)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblDevelopersNotes)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		RtfEditorPane rtfEditorPane_1 = new RtfEditorPane();
		scrollPane_2.setViewportView(rtfEditorPane_1);
		
		RtfEditorPane rtfEditorPane = new RtfEditorPane();
		scrollPane_1.setViewportView(rtfEditorPane);
		
		JComboBox comboBox = new JComboBox();
		
		JButton button = new JButton("..");
		
		JButton button_1 = new JButton("..");
		
		JButton btnInsert = new JButton("Insert");
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addComponent(comboBox, 0, 0, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(button)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(button_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnInsert)
					.addContainerGap())
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addContainerGap(5, Short.MAX_VALUE)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button)
						.addComponent(button_1)
						.addComponent(btnInsert)))
		);
		panel_3.setLayout(gl_panel_3);
	
		panel_2.setLayout(gl_panel_2);
		
		JLabel lblImageFiles = new JLabel("Image files:");
		lblImageFiles.setAlignmentY(Component.TOP_ALIGNMENT);
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblImageFiles)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblImageFiles)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JList list = new JList();
		scrollPane.setViewportView(list);
		panel.setLayout(gl_panel);
		setLayout(groupLayout);
	}
}

package au.org.ala.delta;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class LicenseInfoBox extends JDialog {

	public LicenseInfoBox(Dialog owner) {
		super(owner, "License Information",  true);
		
		this.setMinimumSize(new Dimension(800, 800));
		
		String labelText = "<html><center>This software is made available under Version 1.1 of the Mozilla Public License<br>" +
			"Source code can be downloaded from: http://code.google.com/p/open-delta</center></html>";
		
		JLabel topLabel = new JLabel(labelText);
		topLabel.setFont(new Font(topLabel.getFont().getName(), topLabel.getFont().getStyle(), 14));
		topLabel.setBorder(BorderFactory.createEmptyBorder(20,0,20,0));

		JPanel topPanel = new JPanel();
		topPanel.add(topLabel, BorderLayout.CENTER);
		
		JTextArea textArea = new JTextArea(loadLicenseText());
		textArea.setEnabled(false);
		textArea.setDisabledTextColor(Color.BLACK);
		
		JScrollPane scrollPane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
		
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LicenseInfoBox.this.dispose();
			}
			
		});
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));
		okButton.setAlignmentX(RIGHT_ALIGNMENT);
		bottomPanel.add(okButton, BorderLayout.WEST);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(20,0,20,20));
		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(bottomPanel, BorderLayout.SOUTH);
		
		//center the dialog on screen
		this.setLocationRelativeTo(owner);
	}
	
	public String loadLicenseText() {
		
		StringBuilder licenseText = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(LicenseInfoBox.class.getResourceAsStream("/au/org/ala/delta/resources/MPL-1.1.txt")));
		String line;
		
		try {
			while ((line = in.readLine()) != null) {
				licenseText.append(line);
				licenseText.append("\n");
			}
		} catch (Exception ex) {
			Logger.log("Error while reading Mozilla License text from resource file: %s", ex.getMessage());
		}
		
		return licenseText.toString();
	}
}

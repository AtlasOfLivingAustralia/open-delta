package au.org.ala.delta;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import au.org.ala.delta.gui.util.IconHelper;

public class AboutBox extends JDialog {

	private static final long serialVersionUID = 1L;
	 
	public AboutBox(Frame owner) {
		super(owner, "About Delta", true);
		
		this.setMinimumSize(new Dimension(500, 200));
		this.setResizable(false);
		
		JPanel topPanel = new JPanel();
				
		String topText = "<html><center>" +
				"Delta Editor<br>" +
				"Version " + getVersionFromManifest() +
				"</center></html>";
		
		JLabel topTextLabel = new JLabel(topText);
		topTextLabel.setFont(new Font(topTextLabel.getFont().getName(), topTextLabel.getFont().getStyle(), 16));
		
		Icon deltaIcon = IconHelper.createLargeIcon();
		JLabel iconLabel = new JLabel(deltaIcon);
		iconLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));
		
		topPanel.add(iconLabel, BorderLayout.EAST);
		topPanel.add(topTextLabel, BorderLayout.CENTER);
		

		String middleText = "<html><center>" +
			"Part of the Open Delta Suite<br>" +
			"Based on work by M.J. Dallwitz, T.A. Paine and E.J. Zurcher<br>" +
			"Copyright Atlas of Living Australia 2011<br>" +
			"</center></html>";
		
		JLabel middleTextLabel = new JLabel(middleText);
		middleTextLabel.setFont(new Font(middleTextLabel.getFont().getName(), middleTextLabel.getFont().getStyle(), 16));
		
		JPanel middlePanel = new JPanel();
		middlePanel.add(middleTextLabel, BorderLayout.CENTER);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutBox.this.dispose();
			}
			
		});
		
		JButton licenseButton = new JButton("License Details");
		licenseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LicenseInfoBox licenseInfoBox = new LicenseInfoBox(AboutBox.this);
				licenseInfoBox.setVisible(true);
			}
			
		});
		
		JButton copyVersionButton = new JButton("Copy Version Info To Clipboard");
		copyVersionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				copyVersionInfoToClipboard();
			}
			
		});
		
		JPanel leftButtonPanel = new JPanel();
		JPanel rightButtonPanel = new JPanel();
		
		leftButtonPanel.add(licenseButton, BorderLayout.EAST);
		leftButtonPanel.add(copyVersionButton, BorderLayout.WEST);
		leftButtonPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));
		
		rightButtonPanel.add(okButton, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(leftButtonPanel, BorderLayout.EAST);
		bottomPanel.add(rightButtonPanel, BorderLayout.WEST);

		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(middlePanel, BorderLayout.CENTER);
		this.add(bottomPanel, BorderLayout.SOUTH);
		
		//center the dialog on screen
		this.setLocationRelativeTo(owner);
	}
	
	
	private String getVersionFromManifest() {
		String versionString = getClass().getPackage().getImplementationVersion();
		return versionString;
	}
	
	private void copyVersionInfoToClipboard() {
		StringBuilder versionInfo = new StringBuilder();
		versionInfo.append("Delta Editor " + getVersionFromManifest());
		versionInfo.append("\n");
		versionInfo.append("java.version: ");
		versionInfo.append(System.getProperty("java.version"));
		versionInfo.append("\n");
		versionInfo.append("java.vendor: ");
		versionInfo.append(System.getProperty("java.vendor"));
		versionInfo.append("\n");
		versionInfo.append("os.name: ");
		versionInfo.append(System.getProperty("os.name"));
		versionInfo.append("\n");
		versionInfo.append("os.arch: ");
		versionInfo.append(System.getProperty("os.arch"));
		versionInfo.append("\n");
		versionInfo.append("os.version: ");
		versionInfo.append(System.getProperty("os.version"));
		versionInfo.append("\n");
		versionInfo.append("user.language: ");
		versionInfo.append(System.getProperty("user.language"));
		versionInfo.append("\n");
		versionInfo.append("user.region: ");
		versionInfo.append(System.getProperty("user.region"));
		
		StringSelection selection = new StringSelection(versionInfo.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
		
	}
}

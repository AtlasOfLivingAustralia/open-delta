package au.org.ala.delta;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
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
		
		//Common attributes for our different text areas
		SimpleAttributeSet aSet = new SimpleAttributeSet();
		StyleConstants.setFontFamily(aSet, "Tahoma");
		StyleConstants.setFontSize(aSet, 16);
		StyleConstants.setAlignment(aSet, StyleConstants.ALIGN_CENTER);
		
		JPanel topPanel = new JPanel();
		
		JTextPane topTextPane = new JTextPane();		
		String topText = "Delta Editor\n" +
		"Version " + getVersionFromManifest();
		topTextPane.setText(topText);
		topTextPane.setEnabled(false);
		topTextPane.setBackground(this.getBackground());
		topTextPane.setFont(this.getFont());
		topTextPane.getStyledDocument().setParagraphAttributes(0, topText.length(), aSet, true);
		
		Icon deltaIcon = IconHelper.createLargeIcon();
		JLabel iconLabel = new JLabel(deltaIcon);
		iconLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));
		
		topPanel.add(iconLabel, BorderLayout.EAST);
		topPanel.add(topTextPane, BorderLayout.CENTER);
		

		JTextPane middleTextPane = new JTextPane();
		String middleText = "Based on work by M.J. Dallwitz, T.A. Paine and E.J. Zurcher\n" +
		"Copyright Atlas of Living Australia 2011";
		middleTextPane.setText(middleText);
		middleTextPane.setEnabled(false);
		middleTextPane.setBackground(this.getBackground());
		middleTextPane.setFont(this.getFont());
		middleTextPane.getStyledDocument().setParagraphAttributes(0, middleText.length(), aSet, true);
		
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutBox.this.dispose();
			}
			
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton, BorderLayout.CENTER);
		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(middleTextPane, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		//center the dialog on screen
		this.setLocationRelativeTo(owner);
	}
	
	
	private String getVersionFromManifest() {
		String versionString = getClass().getPackage().getImplementationVersion();
		return versionString;
	}
}

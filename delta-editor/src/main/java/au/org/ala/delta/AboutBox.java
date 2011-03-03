
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
import java.awt.FlowLayout;
import javax.swing.border.EmptyBorder;

public class AboutBox extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private static final int BYTES_IN_MEGABTYE = 1048576;
	 
	public AboutBox(Frame owner) {
		super(owner, "About Delta", true);
		
		this.setMinimumSize(new Dimension(500, 200));
		this.setResizable(false);
		
		JPanel pnlTop = new JPanel();
		pnlTop.setBackground(Color.WHITE);
				
		String topText = "<html><center>" +
				"Delta Editor<br>" +
				"Version " + getVersionFromManifest() +
				"</center></html>";
		
		JLabel lblTopText = new JLabel(topText);
		lblTopText.setFont(new Font(lblTopText.getFont().getName(), lblTopText.getFont().getStyle(), 16));
		
		Icon deltaIcon = IconHelper.createLargeIcon();
		JLabel lblIcon = new JLabel(deltaIcon);
		lblIcon.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));
		
		pnlTop.add(lblIcon, BorderLayout.EAST);
		pnlTop.add(lblTopText, BorderLayout.CENTER);
		

		String middleText = "<html><center>" +
			"Part of the Open Delta Suite<br>" +
			"Based on work by M.J. Dallwitz, T.A. Paine and E.J. Zurcher<br>" +
			"Copyright Atlas of Living Australia 2011<br>" +
			"</center></html>";
		
		JLabel lblMiddleText = new JLabel(middleText);
		lblMiddleText.setFont(new Font(lblMiddleText.getFont().getName(), lblMiddleText.getFont().getStyle(), 16));
		
		JPanel pnlMiddle = new JPanel();
		pnlMiddle.setBackground(Color.WHITE);
		pnlMiddle.add(lblMiddleText, BorderLayout.CENTER);
		
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutBox.this.dispose();
			}
			
		});
		
		JButton btnLicenseDetails = new JButton("License Details");
		btnLicenseDetails.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				LicenseInfoBox licenseInfoBox = new LicenseInfoBox(AboutBox.this);
				licenseInfoBox.setVisible(true);
			}
			
		});
		
		JButton btnViewSysInfo = new JButton("View System Information");
		btnViewSysInfo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SystemInfoBox sysInfoBox = new SystemInfoBox(AboutBox.this, generateSystemInfo());
				sysInfoBox.setVisible(true);
			}
			
		});
		
		JPanel pnlLeftButton = new JPanel();
		FlowLayout fl_pnlLeftButton = (FlowLayout) pnlLeftButton.getLayout();
		fl_pnlLeftButton.setAlignment(FlowLayout.LEFT);
		JPanel pnlRightButton = new JPanel();
		
		pnlLeftButton.add(btnLicenseDetails, BorderLayout.EAST);
		pnlLeftButton.add(btnViewSysInfo, BorderLayout.WEST);
		pnlLeftButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		pnlRightButton.add(btnOK, BorderLayout.CENTER);
		
		JPanel pnlBottom = new JPanel();
		pnlBottom.setLayout(new BorderLayout(0, 0));
		pnlBottom.add(pnlLeftButton, BorderLayout.WEST);
		pnlBottom.add(pnlRightButton, BorderLayout.EAST);

		
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		
		//center the dialog on screen
		this.setLocationRelativeTo(owner);
	}
	
	
	private String getVersionFromManifest() {
		String versionString = getClass().getPackage().getImplementationVersion();
		return versionString;
	}
	
	private String generateSystemInfo() {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzzz");
		Calendar cal = Calendar.getInstance();
		Date currentTime = cal.getTime();
		
		//Free, max and total memory should be written out in megabytes
		long freeMemory = Runtime.getRuntime().freeMemory() / BYTES_IN_MEGABTYE;
		long maxMemory = Runtime.getRuntime().maxMemory() / BYTES_IN_MEGABTYE;
		long totalMemory = Runtime.getRuntime().totalMemory() / BYTES_IN_MEGABTYE;
		
		StringBuilder versionInfo = new StringBuilder();
		versionInfo.append("Delta Editor " + getVersionFromManifest());
		versionInfo.append("\n");
		versionInfo.append("date: ");
		versionInfo.append(df.format(currentTime));
		versionInfo.append("\n");
		versionInfo.append("free memory: ");
		versionInfo.append(freeMemory);
		versionInfo.append(" MB \n");
		versionInfo.append("total memory: ");
		versionInfo.append(totalMemory);
		versionInfo.append(" MB \n");
		versionInfo.append("max memory: ");
		versionInfo.append(maxMemory);
		versionInfo.append(" MB\n");
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
		
		return versionInfo.toString();
	}
}

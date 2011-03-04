
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

import javax.swing.ActionMap;
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
import au.org.ala.delta.util.Utils;

import java.awt.FlowLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.UIManager;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

public class AboutBox extends JDialog {

	private static final long serialVersionUID = 1L;
	
	@Resource 
	String windowTitle;
	
	@Resource
	String applicationTitle;
	
	@Resource
	String versionString;
	
	@Resource
	String attributionLine1;
	
	@Resource
	String attributionLine2;
	
	@Resource
	String copyrightString;
	 
	public AboutBox(Frame owner) {
		super(owner, true);
		
		ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
		ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(AboutBox.class);
		resourceMap.injectFields(this);
		
		this.setTitle(windowTitle);
		
		this.setMinimumSize(new Dimension(500, 200));
		this.setResizable(false);
		
		JPanel pnlTop = new JPanel();
		pnlTop.setBackground(Color.WHITE);
				
		StringBuilder topTextBuilder = new StringBuilder();
		topTextBuilder.append("<html><center>");
		topTextBuilder.append(applicationTitle);
		topTextBuilder.append("<br>");
		topTextBuilder.append(String.format(versionString, getVersionFromManifest()));
		topTextBuilder.append("</center></html>");		
		
		JLabel lblTopText = new JLabel(topTextBuilder.toString());
		lblTopText.setFont(new Font(lblTopText.getFont().getName(), lblTopText.getFont().getStyle(), 16));
		
		Icon deltaIcon = IconHelper.createLargeIcon();
		JLabel lblIcon = new JLabel(deltaIcon);
		lblIcon.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));
		
		pnlTop.add(lblIcon, BorderLayout.EAST);
		pnlTop.add(lblTopText, BorderLayout.CENTER);
		
		StringBuilder middleTextBuilder = new StringBuilder();
		middleTextBuilder.append("<html><center>");
		middleTextBuilder.append("<p>");
		middleTextBuilder.append(attributionLine1);
		middleTextBuilder.append("</p>");
		middleTextBuilder.append("<p>");
		middleTextBuilder.append(copyrightString);
		middleTextBuilder.append("</p>");
		middleTextBuilder.append("<br>");
		middleTextBuilder.append(attributionLine2);

		middleTextBuilder.append("</center></html>");
		
		JLabel lblMiddleText = new JLabel(middleTextBuilder.toString());
		lblMiddleText.setFont(new Font(lblMiddleText.getFont().getName(), lblMiddleText.getFont().getStyle(), 16));
		
		JPanel pnlMiddle = new JPanel();
		pnlMiddle.setBackground(Color.WHITE);
		pnlMiddle.add(lblMiddleText, BorderLayout.CENTER);
		
		JButton btnOK = new JButton();
		btnOK.setAction(actionMap.get("closeAboutBox"));
		
		JButton btnLicenseDetails = new JButton();
		btnLicenseDetails.setAction(actionMap.get("showLicense"));
		
		JButton btnViewSysInfo = new JButton();
		btnViewSysInfo.setAction(actionMap.get("showSystemInfo"));
		
		JPanel pnlLeftButton = new JPanel();
		FlowLayout fl_pnlLeftButton = (FlowLayout) pnlLeftButton.getLayout();
		fl_pnlLeftButton.setAlignment(FlowLayout.LEFT);
		JPanel pnlRightButton = new JPanel();
		
		pnlLeftButton.add(btnLicenseDetails, BorderLayout.EAST);
		pnlLeftButton.add(btnViewSysInfo, BorderLayout.WEST);
		pnlLeftButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		pnlRightButton.add(btnOK, BorderLayout.CENTER);
		
		JPanel pnlBottom = new JPanel();
		pnlBottom.setBorder(new MatteBorder(1, 0, 0, 0, (Color) UIManager.getColor("Button.darkShadow")));
		pnlBottom.setLayout(new BorderLayout(0, 0));
		pnlBottom.add(pnlLeftButton, BorderLayout.WEST);
		pnlBottom.add(pnlRightButton, BorderLayout.EAST);

		
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		
		//center the dialog on screen
		this.setLocationRelativeTo(owner);
	}
	
	@Action
	public void closeAboutBox() {
		this.dispose();
	}
	
	@Action
	public void showLicense() {
		LicenseInfoBox licenseInfoBox = new LicenseInfoBox(this);
		licenseInfoBox.setVisible(true);
	}

	@Action
	public void showSystemInfo() {
		SystemInfoBox sysInfoBox = new SystemInfoBox(this);
		sysInfoBox.setVisible(true);
	}

	private String getVersionFromManifest() {
		return Utils.getVersionFromManifest();
	}
}

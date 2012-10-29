/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.HierarchyListener;

import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.util.Utils;

/**
 * The Help->About... box for the DELTA application.
 */
public class AboutBox extends JDialog implements HyperlinkListener {

	private static final long serialVersionUID = 1L;
	
	/** Calls Desktop.getDesktop on a background thread as it's slow to initialise */
	private SwingWorker<Desktop, Void> _desktopWorker;
	
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
	private String alaAttribution;
	
	@Resource
	String copyrightString;
	 
	public AboutBox(Frame owner, Icon icon) {
		super(owner, true);
		
		loadDesktopInBackground();
		setName("aboutBox");
		
		ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
		ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(AboutBox.class);
		resourceMap.injectFields(this);
		
		this.setTitle(windowTitle);
		
		this.setMinimumSize(new Dimension(500, 240));
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
		
		JLabel lblIcon = new JLabel(icon);
	
		pnlTop.add(lblIcon, BorderLayout.EAST);
		pnlTop.add(lblTopText, BorderLayout.CENTER);
		
		JPanel pnlMiddle = new JPanel();
		pnlMiddle.setBackground(Color.WHITE);
		pnlMiddle.setLayout(new BorderLayout());
		
		JEditorPane attribution = createAttribution(lblIcon);
		pnlMiddle.add(attribution, BorderLayout.CENTER);
		
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
	}
	
	/**
	 * We do this because Desktop.getDesktop is very slow (at least on my system).
	 */
	private void loadDesktopInBackground() {
		_desktopWorker = new SwingWorker<Desktop, Void>() {
			
			protected Desktop doInBackground() {
				return Desktop.getDesktop();
			}
		};
		_desktopWorker.execute();
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
		SystemInfoBox sysInfoBox = new SystemInfoBox(this, applicationTitle);
		sysInfoBox.setVisible(true);
	}

	private String getVersionFromManifest() {
		return Utils.getVersionFromManifest();
	}

	@Override
	public void addHierarchyListener(HierarchyListener l) {
		// do nothing - working around a StackOverflowError when SAF saves the dialog properties under Open JDK on
		// linux. We don't need to save these properties anyway.
	}	
	
	/**
	 * Creates a text area displaying the attribution for the application.
	 * @param example used to get fonts and colours.
	 * @return a new JEditorPane containing the attribution text.
	 */
	public JEditorPane createAttribution(JLabel example) {
		
		JEditorPane alaAttributionPane = new JEditorPane();
		alaAttributionPane.setEditorKit(new HTMLEditorKit());
		
		alaAttributionPane.setFont(example.getFont());
		alaAttributionPane.setBackground(example.getBackground());
		alaAttributionPane.setForeground(example.getForeground());
		alaAttributionPane.setEditable(false);
		
		StringBuilder middleTextBuilder = new StringBuilder();
		middleTextBuilder.append("<html><center><hr/>");
		middleTextBuilder.append(attributionLine1);
		middleTextBuilder.append("<br/>");
		middleTextBuilder.append(copyrightString);
		middleTextBuilder.append("<br/>");
		middleTextBuilder.append(attributionLine2);
		middleTextBuilder.append("<br/>");
		middleTextBuilder.append(alaAttribution);
		middleTextBuilder.append("</center></html>");
		
		alaAttributionPane.setText(middleTextBuilder.toString());
		
		// add a CSS rule to force body tags to use the default label font
        // instead of the value in javax.swing.text.html.default.csss
        Font font = UIManager.getFont("Label.font");
        String bodyRule = "body { font-family: " + font.getFamily() + "; " +
                "font-size: 14 pt; }";
        ((HTMLDocument)alaAttributionPane.getDocument()).getStyleSheet().addRule(bodyRule);

        alaAttributionPane.addHyperlinkListener(this);

		
		return alaAttributionPane;
	}

	/**
	 * Opens the URL supplied in the event in a browser.
	 */
	@Override
	public void hyperlinkUpdate(HyperlinkEvent event) {
		try {
			if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

				if (Desktop.isDesktopSupported() && (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))) {
					try {
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						_desktopWorker.get().browse(event.getURL().toURI());
					}
					finally {
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}
		}
		catch (Exception ex) {
			// error displaying link... oh well not much we can do.
		}
	}
	
	
}

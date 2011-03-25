package au.org.ala.delta.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.util.Utils;

public class SystemInfoBox extends JDialog {

	private static final long serialVersionUID = 1L;

	private String configDetails;
	
	@Resource
	String windowTitle;
	
	public SystemInfoBox(Dialog owner) {
		super(owner, true);
		
		ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
		ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(AboutBox.class);
		resourceMap.injectFields(this);
		
		this.configDetails = Utils.generateSystemInfo();
		
		setTitle(windowTitle);
		this.setMinimumSize(new Dimension(800, 400));
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel pnlButton = new JPanel();
		pnlButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(pnlButton, BorderLayout.SOUTH);
		pnlButton.setLayout(new BorderLayout(0, 0));
		
		JButton btnCopyToClipboard = new JButton();
		btnCopyToClipboard.setAction(actionMap.get("copySystemInfoToClipboard"));
		pnlButton.add(btnCopyToClipboard, BorderLayout.WEST);
		
		JButton btnOK = new JButton();
		btnOK.setAction(actionMap.get("closeSystemInfoBox"));
		btnOK.setAlignmentX(Component.RIGHT_ALIGNMENT);
		pnlButton.add(btnOK, BorderLayout.EAST);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JTextArea textArea = new JTextArea();
		textArea.setText(this.configDetails);
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		//center the dialog on screen
		this.setLocationRelativeTo(owner);
	}
	
	@Action
	public void copySystemInfoToClipboard() {
		StringSelection selection = new StringSelection(this.configDetails);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
	}
	
	@Action
	public void closeSystemInfoBox() {
		this.dispose();
	}
	
}

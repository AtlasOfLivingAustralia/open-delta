package au.org.ala.delta;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class SystemInfoBox extends JDialog {

	private String configDetails;
	public SystemInfoBox(Dialog owner, String configDetails) {
		super(owner, true);
		
		this.configDetails = configDetails;
		
		setTitle("System Information");
		this.setMinimumSize(new Dimension(800, 400));
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel pnlButton = new JPanel();
		pnlButton.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(pnlButton, BorderLayout.SOUTH);
		pnlButton.setLayout(new BorderLayout(0, 0));
		
		JButton btnCopyToClipboard = new JButton("Copy to clipboard");
		pnlButton.add(btnCopyToClipboard, BorderLayout.WEST);
		
		btnCopyToClipboard.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				copyToClipboard();
			}
		});
		
		JButton btnOK = new JButton("OK");
		btnOK.setAlignmentX(Component.RIGHT_ALIGNMENT);
		pnlButton.add(btnOK, BorderLayout.EAST);
		
		btnOK.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SystemInfoBox.this.dispose();
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JTextArea textArea = new JTextArea();
		textArea.setText(this.configDetails);
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		//center the dialog on screen
		this.setLocationRelativeTo(owner);
	}
	
	private void copyToClipboard() {
		StringSelection selection = new StringSelection(this.configDetails);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
	}
	
}

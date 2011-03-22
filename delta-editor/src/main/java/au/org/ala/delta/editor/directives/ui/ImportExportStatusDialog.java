package au.org.ala.delta.editor.directives.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;

import au.org.ala.delta.editor.directives.ImportExportStatus;

/**
 * Displays the status of an import or export operation.
 */
public class ImportExportStatusDialog extends JDialog {
	
	private static final long serialVersionUID = 398371452571368807L;
	private JLabel heading;
	private JLabel importDirectory;
	private JLabel currentFile;
	private JLabel currentDirective;
	private JLabel totalLines;
	private JLabel totalErrors;
	private JLabel currentFileLine;
	private JLabel currentFileErrors;
	private JLabel textFromLastShowDirective;

	public ImportExportStatusDialog() {
		
		setMinimumSize(new Dimension(300, 300));
		setPreferredSize(new Dimension(300, 300));
		JPanel statusPanel = new JPanel();
		getContentPane().add(statusPanel, BorderLayout.CENTER);
		
		JLabel lblImportingDeltaData = new JLabel("Importing DELTA data set");
		lblImportingDeltaData.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		JPanel panel_1 = new JPanel();
		
		JLabel lblTextFromLast = new JLabel("Text from last *SHOW directive:");
		lblTextFromLast.setHorizontalAlignment(SwingConstants.CENTER);
		
		textFromLastShowDirective = new JLabel();
		textFromLastShowDirective.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JCheckBox chckbxPauseOnErrors = new JCheckBox("Pause on errors and messages");
		
		JButton btnDone = new JButton("Done");
		
		JButton btnCancel = new JButton("Cancel");
		GroupLayout gl_statusPanel = new GroupLayout(statusPanel);
		gl_statusPanel.setHorizontalGroup(
			gl_statusPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_statusPanel.createSequentialGroup()
					.addGroup(gl_statusPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_statusPanel.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_statusPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
								.addComponent(lblImportingDeltaData, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
								.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
								.addGroup(Alignment.LEADING, gl_statusPanel.createSequentialGroup()
									.addComponent(chckbxPauseOnErrors)
									.addGap(215))
								.addComponent(lblTextFromLast, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
								.addComponent(textFromLastShowDirective, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)))
						.addGroup(gl_statusPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnDone)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCancel)
							.addPreferredGap(ComponentPlacement.RELATED, 163, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_statusPanel.setVerticalGroup(
			gl_statusPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_statusPanel.createSequentialGroup()
					.addComponent(lblImportingDeltaData)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblTextFromLast)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textFromLastShowDirective, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxPauseOnErrors)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_statusPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnDone)
						.addComponent(btnCancel))
					.addContainerGap(17, Short.MAX_VALUE))
		);
		
		JLabel lblStatistics = new JLabel("Statistics");
		
		JLabel lblTota = new JLabel("Total");
		
		JLabel lblCurrentFile_1 = new JLabel("Current file");
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		JLabel lblLines = new JLabel("Lines:");
		
		totalLines = new JLabel("1861");
		
		currentFileLine = new JLabel("8");
		
		JLabel lblErrors = new JLabel("Errors:");
		
		totalErrors = new JLabel("0");
		
		currentFileErrors = new JLabel("0");
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGap(1)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(lblLines)
							.addGap(113)
							.addComponent(totalLines)
							.addGap(108)
							.addComponent(currentFileLine))
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(lblErrors)
							.addGap(108)
							.addComponent(totalErrors)
							.addGap(126)
							.addComponent(currentFileErrors))))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(lblLines)
						.addComponent(totalLines)
						.addComponent(currentFileLine))
					.addGap(5)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(lblErrors)
						.addComponent(totalErrors)
						.addComponent(currentFileErrors)))
		);
		panel_2.setLayout(gl_panel_2);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(1)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(lblStatistics, GroupLayout.PREFERRED_SIZE, 122, GroupLayout.PREFERRED_SIZE)
							.addGap(5)
							.addComponent(lblTota, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblCurrentFile_1, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE))
						.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 386, GroupLayout.PREFERRED_SIZE)))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(lblStatistics)
						.addComponent(lblTota)
						.addComponent(lblCurrentFile_1, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		panel_1.setLayout(gl_panel_1);
		
		JLabel lblCurrent = new JLabel("Current:");
		
		JLabel lblCurrentFile = new JLabel("Current file:");
		
		JLabel lblImportDirectory = new JLabel("Import directory:");
		
		JLabel lblHeading = new JLabel("Heading:");
		
		heading = new JLabel("New label");
		
		importDirectory = new JLabel("New label");
		
		currentFile = new JLabel("New label");
		
		currentDirective = new JLabel("New label");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblCurrent)
							.addGap(47)
							.addComponent(currentDirective, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblHeading)
							.addGap(45)
							.addComponent(heading, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblImportDirectory)
								.addComponent(lblCurrentFile))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(currentFile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(importDirectory, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHeading)
						.addComponent(heading))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblImportDirectory)
						.addComponent(importDirectory))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCurrentFile)
						.addComponent(currentFile))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCurrent, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
						.addComponent(currentDirective)))
		);
		panel.setLayout(gl_panel);
		statusPanel.setLayout(gl_statusPanel);
	}
	
	/**
	 * Updates the state of the UI with the data from the supplied ImportExportStatus.
	 * @param status contains the current import status.
	 */
	public void update(ImportExportStatus status) {
		heading.setText(status.getHeading());
		importDirectory.setText(status.getImportDirectory());
		currentFile.setText(status.getCurrentFile());
		currentDirective.setText(status.getCurrentDirective());
		
		totalLines.setText(Integer.toString(status.getTotalLines()));
		totalErrors.setText(Integer.toString(status.getTotalErrors()));
		currentFileLine.setText(Integer.toString(status.getLineInCurentFile()));
		currentFileErrors.setText(Integer.toString(status.getErrorsInCurrentFile()));
		
		textFromLastShowDirective.setText(status.getTextFromLastShowDirective());
	}
}

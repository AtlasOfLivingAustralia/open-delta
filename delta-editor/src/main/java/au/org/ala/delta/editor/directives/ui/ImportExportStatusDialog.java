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
package au.org.ala.delta.editor.directives.ui;

import au.org.ala.delta.editor.directives.ImportExportStatus;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;

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
	private JButton btnDone;
	private JButton btnContinue;
	private JButton btnCancel;
	private JCheckBox chckbxPauseOnErrors;
	
	private ImportExportStatus _status;
	private ActionMap _actions;
	private String _resourcePrefix;
	
	
	/**
	 * Displays the status of a directives import during the import process.
	 */
	public ImportExportStatusDialog(Window parent, String prefix) {
		super(parent);
		_actions = Application.getInstance().getContext().getActionMap(this);
		_resourcePrefix = prefix;
		createUI();
		addEventListeners();
	}
	
	private void addEventListeners() {
		
		javax.swing.Action done = _actions.get("importExportFinished");
		done.setEnabled(false);
		btnDone.setAction(done);
		
		javax.swing.Action continueAction = _actions.get("continueImportExport");
		continueAction.setEnabled(false);
		btnContinue.setAction(continueAction);
		
		javax.swing.Action cancelAction = _actions.get("cancelImportExport");
		cancelAction.setEnabled(false);
		btnCancel.setAction(cancelAction);
		
		chckbxPauseOnErrors.setAction(_actions.get("pauseOnErrors"));
		
	}

	private void createUI() {
		setName(_resourcePrefix+"StatusDialog");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(465, 354));
		setPreferredSize(new Dimension(465, 354));
		JPanel statusPanel = new JPanel();
		getContentPane().add(statusPanel, BorderLayout.CENTER);
		
		JLabel lblImportingDeltaData = new JLabel();
        String headingKey = _resourcePrefix+"Heading";
		lblImportingDeltaData.setName(headingKey);

		
		lblImportingDeltaData.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		JPanel panel_1 = new JPanel();
		
		JLabel lblTextFromLast = new JLabel("");
		lblTextFromLast.setName("lastShowDirectiveLabel");
		lblTextFromLast.setHorizontalAlignment(SwingConstants.CENTER);
		
		textFromLastShowDirective = new JLabel();
		textFromLastShowDirective.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		chckbxPauseOnErrors = new JCheckBox();
		chckbxPauseOnErrors.setSelected(true);
		
		btnContinue = new JButton();
		btnDone = new JButton();
		
		btnCancel = new JButton();
		GroupLayout gl_statusPanel = new GroupLayout(statusPanel);
		gl_statusPanel.setHorizontalGroup(
			gl_statusPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_statusPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_statusPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
						.addComponent(lblImportingDeltaData, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
						.addGroup(gl_statusPanel.createSequentialGroup()
							.addComponent(chckbxPauseOnErrors)
							.addGap(215))
						.addComponent(lblTextFromLast, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
						.addComponent(textFromLastShowDirective, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
						.addGroup(gl_statusPanel.createSequentialGroup()
							.addComponent(btnContinue)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDone)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCancel)))
					.addContainerGap())
		);
		gl_statusPanel.setVerticalGroup(
			gl_statusPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_statusPanel.createSequentialGroup()
					.addComponent(lblImportingDeltaData)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblTextFromLast)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textFromLastShowDirective, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxPauseOnErrors)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_statusPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnContinue)
						.addComponent(btnCancel)
						.addComponent(btnDone))
					.addGap(14))
		);
		
		JLabel lblStatistics = new JLabel();
		lblStatistics.setName("importExportStatisticsLabel");
		JLabel lblTota = new JLabel();
		lblTota.setName("importExportTotalLabel");
		JLabel lblCurrentFile_1 = new JLabel();
		lblCurrentFile_1.setName("importExportCurrentFileLabel");
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		JLabel lblLines = new JLabel();
		lblLines.setName("importExportDirectivesLabel");
		
		totalLines = new JLabel("");
		totalLines.setHorizontalAlignment(SwingConstants.TRAILING);
		
		currentFileLine = new JLabel("");
		currentFileLine.setHorizontalAlignment(SwingConstants.TRAILING);
		
		JLabel lblErrors = new JLabel();
		lblErrors.setName("importExportErrorsLabel");
		totalErrors = new JLabel("");
		totalErrors.setHorizontalAlignment(SwingConstants.TRAILING);
		
		currentFileErrors = new JLabel("");
		currentFileErrors.setHorizontalAlignment(SwingConstants.TRAILING);
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(lblLines)
							.addGap(113)
							.addComponent(totalLines))
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(lblErrors)
							.addGap(108)
							.addComponent(totalErrors, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addGap(108)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
						.addComponent(currentFileLine, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
						.addComponent(currentFileErrors, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE))
					.addGap(84))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblLines, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
							.addComponent(totalLines, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(currentFileLine, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
							.addComponent(totalErrors)
							.addComponent(currentFileErrors))
						.addComponent(lblErrors))
					.addContainerGap())
		);
		panel_2.setLayout(gl_panel_2);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblStatistics, GroupLayout.PREFERRED_SIZE, 122, GroupLayout.PREFERRED_SIZE)
					.addGap(20)
					.addComponent(lblTota, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
					.addComponent(lblCurrentFile_1, GroupLayout.PREFERRED_SIZE, 134, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(1)
					.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(lblStatistics)
						.addComponent(lblTota, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblCurrentFile_1, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
					.addGap(2)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(3))
		);
		panel_1.setLayout(gl_panel_1);
		
		JLabel lblCurrent = new JLabel();
		lblCurrent.setName("importExportCurrentLabel");
		JLabel lblCurrentFile = new JLabel();
		lblCurrentFile.setName("importExportCurrentFileLabel");
		JLabel lblImportDirectory = new JLabel();
		lblImportDirectory.setName(_resourcePrefix+"DirectoryLabel");
		JLabel lblHeading = new JLabel();
		lblHeading.setName("importExportHeadingLabel");
		heading = new JLabel("");
		
		importDirectory = new JLabel("");
		
		currentFile = new JLabel("");
		
		currentDirective = new JLabel("");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblCurrent)
							.addGap(47)
							.addComponent(currentDirective, GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(88)
							.addComponent(heading, GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblImportDirectory)
								.addComponent(lblCurrentFile))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(currentFile, GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
								.addComponent(importDirectory, GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)))
						.addComponent(lblHeading))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(heading)
						.addComponent(lblHeading))
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
						.addComponent(currentDirective))
					.addGap(0, 0, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		statusPanel.setLayout(gl_statusPanel);
	}
	
	/**
	 * Updates the state of the UI with the data from the supplied ImportExportStatus.
	 * @param status contains the current import status.
	 */
	public void update(ImportExportStatus status) {
		_status = status;
		heading.setText(status.getHeading());
		importDirectory.setText(status.getImportDirectory());
		currentFile.setText(status.getCurrentFile());
		currentDirective.setText(status.getCurrentDirective());
		
		totalLines.setText(Integer.toString(status.getTotalDirectives()));
		totalErrors.setText(Integer.toString(status.getTotalErrors()));
		currentFileLine.setText(Integer.toString(status.getDirectivesInCurentFile()));
		currentFileErrors.setText(Integer.toString(status.getErrorsInCurrentFile()));
		
		textFromLastShowDirective.setText(status.getTextFromLastShowDirective());
		
		javax.swing.Action done = _actions.get("importExportFinished");
		done.setEnabled(_status.isFinished());
		
		javax.swing.Action continueAction = _actions.get("continueImportExport");
		continueAction.setEnabled(_status.isPaused());
		
		javax.swing.Action cancelAction = _actions.get("cancelImportExport");
		cancelAction.setEnabled(_status.isPaused());
	}
	
	@Action
	public void importExportFinished() {
		setVisible(false);
	}
	
	@Action
	public void cancelImportExport() {
		if (_status != null) {
			_status.cancel();
			_status.resume();
			
		}
		setVisible(false);
	}
	
	@Action
	public void continueImportExport() {
		if (_status != null) {
			_status.resume();
		}
	}
	public boolean getPauseOnError() {
		return chckbxPauseOnErrors.isSelected();
	}
	
	@Action
	public void pauseOnErrors() {
		if (_status != null) {
			_status.setPauseOnError(chckbxPauseOnErrors.isSelected());
		}
	}
}

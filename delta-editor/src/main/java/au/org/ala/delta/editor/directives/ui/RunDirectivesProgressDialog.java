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

import au.org.ala.delta.ui.TextFileViewer;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Displays progress and the output of running a directives file.
 */
public class RunDirectivesProgressDialog extends JDialog {

	private static final long serialVersionUID = -2658247261229069862L;
	private JTable table;
	private JProgressBar progressBar;
	private OutputFileTableModel _model;
	private List<File> _results;
	private JButton btnOk;
	private JLabel lblDirectivesfilelabel;
	private JTextArea textArea;

    private ResourceMap _resources;
	
	public RunDirectivesProgressDialog(Window owner, String message) {
		super(owner);
        setName("runDirectivesProgressDialog");
        _resources = Application.getInstance().getContext().getResourceMap();
		createGUI();
		addEventHandlers();
		setMessage(message);

	}
	
	public void setMessage(String message) {
		lblDirectivesfilelabel.setText(message);
	}

	public void addEventHandlers() {
		btnOk.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}

	protected void createGUI() {

        setTitle("Actions - Results");
		lblDirectivesfilelabel = new JLabel("");
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		
		
		JLabel progressLabel = new JLabel("");
        progressLabel.setName("runDirectivesProgressLabel");
		
		JScrollPane scrollPane = new JScrollPane();
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setName("runDirectivesOutputFilesLabel");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JLabel lblOutput = new JLabel("");
        lblOutput.setName("runDirectivesOutputLabel");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblOutput)
						.addComponent(lblDirectivesfilelabel)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(progressLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE))
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
						.addComponent(lblNewLabel)
						.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblDirectivesfilelabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(progressLabel)
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblOutput)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(32))
		);
		
		textArea = new JTextArea();
		textArea.setRows(8);
		scrollPane_1.setViewportView(textArea);
		
		btnOk = new JButton("");
        btnOk.setName("runDirectivesFileCloseButton");
		panel.add(btnOk);
		
		table = new JTable();
		_model = new OutputFileTableModel();
		table.setModel(_model);
		table.getColumnModel().getColumn(1).setCellRenderer(new ButtonRenderer());
		table.getColumnModel().getColumn(1).setCellEditor(new ButtonEditor());
		scrollPane.setViewportView(table);
		table.getColumnModel().getColumn(1).setMaxWidth(75);
		progressBar.setMaximum(100);
		
		getContentPane().setLayout(groupLayout);
	}
	
	public void print(String string) {
		textArea.append(string);
	}
	
	public void println(String string) {
		textArea.append(string+"\n");
	}
	
	public void setOutputFiles(List<File> files) {
		_results = files;
		for (File file : files) {
			_model.addRow(new String[] {file.getName(), ""});
		}
		updateProgress(100);
	}
	
	public void updateProgress(int progress) {
		if (progress > 0) {
			progressBar.setIndeterminate(false);
			progressBar.setValue(100);
		}
		else {
			progressBar.setIndeterminate(true);
			textArea.setText("");
			table.getColumnModel().getColumn(1).getCellEditor().stopCellEditing();
			_model.setRowCount(0);
		}
	}
	
	private void displayResults(File file) {
		try {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(file);
				return;
			}
			catch (IOException e) {}
		}
		
		TextFileViewer viewer = new TextFileViewer(((SingleFrameApplication)Application.getInstance()).getMainFrame(), file);
		viewer.setVisible(true);
		}
		catch (Exception e) {}
	}
	
	class OutputFileTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 6579566851056202044L;
		
		public OutputFileTableModel() {

            String columnHeader = _resources.getString("runDirectivesProgressDialog.fileNameColumn.text");
			setColumnCount(2);
			setColumnIdentifiers(new String[]{columnHeader, ""});
		}
		
	}
	
	class ButtonRenderer extends DefaultTableCellRenderer {
		
		private static final long serialVersionUID = -1030899285535143846L;
		private JButton _viewButton = new JButton(_resources.getString("runDirectivesProgressDialog.viewButton.text"));
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
			
			return _viewButton;
		}
	}
	
	class ButtonEditor extends AbstractCellEditor implements TableCellEditor {

        private static final String ROW_CLIENT_PROPERTY = "row";
		private static final long serialVersionUID = -1030899285535143846L;
		private JButton _viewButton;
		
		public ButtonEditor() {
			_viewButton = new JButton();
            _viewButton.setText(_resources.getString("runDirectivesProgressDialog.viewButton.text"));
			_viewButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Integer row = (Integer)_viewButton.getClientProperty(ROW_CLIENT_PROPERTY);
					if (row != null && row >= 0) {
						displayResults(_results.get(row));
					}
				}
			});
		}
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			_viewButton.putClientProperty(ROW_CLIENT_PROPERTY, row);
			return _viewButton;
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}
		
		
		
	}
	
}

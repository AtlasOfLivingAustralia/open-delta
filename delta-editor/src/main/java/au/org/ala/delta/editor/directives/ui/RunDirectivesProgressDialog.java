package au.org.ala.delta.editor.directives.ui;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.ui.TextFileViewer;

public class RunDirectivesProgressDialog extends JDialog {

	private static final long serialVersionUID = -2658247261229069862L;
	private JTable table;
	private JProgressBar progressBar;
	private OutputFileTableModel _model;
	private List<File> _results;
	private JButton btnOk;
	private JLabel lblDirectivesfilelabel;
	private JTextArea textArea;
	
	public RunDirectivesProgressDialog(Window owner, String message) {
		super(owner);
		createGUI();
		lblDirectivesfilelabel.setText(message);
		addEventHandlers();
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
		lblDirectivesfilelabel = new JLabel("directivesFileLabel");
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		
		
		JLabel progressLabel = new JLabel("Progress");
		
		JScrollPane scrollPane = new JScrollPane();
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		JLabel lblNewLabel = new JLabel("Output files");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JLabel lblOutput = new JLabel("Output");
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
							.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED))
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
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(21, Short.MAX_VALUE))
		);
		
		textArea = new JTextArea();
		scrollPane_1.setViewportView(textArea);
		
		btnOk = new JButton("Close");
		panel.add(btnOk);
		
		table = new JTable();
		_model = new OutputFileTableModel();
		table.setModel(_model);
		table.getColumnModel().getColumn(1).setCellRenderer(new ButtonRenderer());
		table.getColumnModel().getColumn(1).setCellEditor(new ButtonEditor());
		scrollPane.setViewportView(table);
		table.getColumnModel().getColumn(1).setMaxWidth(75);
		
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
		progressBar.setIndeterminate(false);
		progressBar.setMaximum(100);
		progressBar.setValue(100);
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
			setColumnCount(2);
			setColumnIdentifiers(new String[]{"File name", ""});
		}
		
	}
	
	class ButtonRenderer extends DefaultTableCellRenderer {
		
		private static final long serialVersionUID = -1030899285535143846L;
		private JButton _viewButton = new JButton("View...");
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
			
			return _viewButton;
		}
	}
	
	class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
		
		private static final long serialVersionUID = -1030899285535143846L;
		private JButton _viewButton = new JButton("View...");
		
		public ButtonEditor() {
			_viewButton = new JButton("View...");
			_viewButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int selected = table.getSelectedRow();
					if (selected >= 0) {
						displayResults(_results.get(selected));
					}
				}
			});
		}
		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			return _viewButton;
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}
		
		
		
	}
	
}

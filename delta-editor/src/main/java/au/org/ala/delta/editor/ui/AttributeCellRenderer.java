package au.org.ala.delta.editor.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class AttributeCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private static Color NON_SIMPLE_BACKGROUND = new Color(0xE8, 0xE8, 0xE8);
	private static Color IMPLICT_BACKGROUND = new Color(0xA8, 0xA8, 0xA8);

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		MatrixCellViewModel viewModel = (MatrixCellViewModel) value;
		Component comp = super.getTableCellRendererComponent(table, viewModel.getText(), isSelected, hasFocus, row, column);

		if (comp instanceof JLabel) {
			((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
			((JLabel) comp).setVerticalAlignment(TOP);
		}

		MatrixTableModel model = (MatrixTableModel) table.getModel();

		if (isSelected) {
			setBackground(SystemColor.textHighlight);
			setForeground(SystemColor.textHighlightText);
		} else {
			if (viewModel.isImplicit()) {
				setForeground(IMPLICT_BACKGROUND);
			} else {
				setForeground(SystemColor.controlText);
			}

			if (model.isCellEditable(row, column)) {
				setBackground(Color.WHITE);
			} else {
				setBackground(NON_SIMPLE_BACKGROUND);
			}
		}
		return comp;
	}
}

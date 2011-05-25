package au.org.ala.delta.editor.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemColor;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import au.org.ala.delta.ui.util.IconHelper;

/**
 * The AttributeCellRenderer is responsible for rendering the table cells in the grid view.
 * Attributes are rendered based on whether they are inapplicable, simple, implicit or 
 * are missing a value (if the character is mandatory).
 */
public class AttributeCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private static final String MANDATORY_ATTRIBUTE_ICON_PATH = "/au/org/ala/delta/editor/resources/icons/error.png";
	private static Color NON_SIMPLE_BACKGROUND = new Color(0xE8, 0xE8, 0xE8);
	private static Color IMPLICT_BACKGROUND = new Color(0xA8, 0xA8, 0xA8);
	
	private Icon _mandatoryAttributeMissingIcon;
	private boolean _inapplicable;
	
	public AttributeCellRenderer() {
		_mandatoryAttributeMissingIcon = IconHelper.createImageIconFromAbsolutePath(MANDATORY_ATTRIBUTE_ICON_PATH);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		MatrixCellViewModel viewModel = (MatrixCellViewModel) value;
		Component comp = super.getTableCellRendererComponent(table, viewModel.getText(), isSelected, hasFocus, row, column);

		if (comp instanceof JLabel) {
			((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
			((JLabel) comp).setVerticalAlignment(TOP);
		}

		MatrixTableModel model = (MatrixTableModel) table.getModel();
		
		_inapplicable = viewModel.isInapplicable();

		if (isSelected) {
			setBackground(SystemColor.textHighlight);
			if (_inapplicable) {
				setForeground(Color.red);
			} else {
				setForeground(SystemColor.textHighlightText);
			}
		} else {			
			if (_inapplicable) {
				setForeground(Color.red);
				setText("");
				setBackground(Color.WHITE);
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
		}
		if (!_inapplicable && viewModel.isUncodedMandatory()) {
			setIcon(_mandatoryAttributeMissingIcon);
		}
		else {
			setIcon(null);
		}
		return comp;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (_inapplicable) {
			g.setColor(Color.red);			
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			int xx = getWidth() / 2;
			int yy = ((getHeight() + 1) / 2);
			g.drawLine(xx - 5, yy, xx + 5, yy);
		}
	}
}

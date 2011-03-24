package au.org.ala.delta.editor.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;

public class TableHeaderRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 1L;
	
	private HeaderPanel _renderer = new HeaderPanel();
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		_renderer.bind(column, (String) value); 
		return _renderer;
	}
	
	public static class HeaderPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		private JLabel _label = new JLabel();
		private JTextArea _text = new JTextArea();
		
		public HeaderPanel() {
			setPreferredSize(new Dimension(40, 100));
			_label.setHorizontalAlignment(CENTER);
			_text.setEditable(false);
			_text.setFont(_label.getFont());
			_text.setOpaque(false);
			_text.setLineWrap(true);
			_text.setWrapStyleWord(true);
			this.setLayout(new BorderLayout());
			add(_label,BorderLayout.NORTH);
			add(_text, BorderLayout.CENTER);
			setBorder(new BottomLineBorder(SystemColor.controlShadow));
		}
		
		public void bind(int colIndex, String text) {
			_label.setText(String.format("%d", colIndex + 1));
			_text.setText(text);
			
		}
		
		@Override
		public void paint(Graphics g) {		
			super.paint(g);
			g.setColor(SystemColor.controlShadow);
			g.drawLine(0, _label.getHeight()-1, getWidth() - 2, _label.getHeight()-1);
			g.setColor(SystemColor.controlLtHighlight);
			g.drawLine(0, _label.getHeight(), getWidth() - 2, _label.getHeight());			
		}
		
	}
	
}

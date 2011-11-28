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

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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class FixedColumnRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private RowHeaderPanel _renderer = new RowHeaderPanel();

	public FixedColumnRenderer() {
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		_renderer.setRowIndex(row);
		_renderer.setText(value == null ? "" : value.toString());
		if (isSelected) {
			_renderer.setBorder(BorderFactory.createLineBorder(SystemColor.controlShadow));
		}
		else {
			_renderer.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		}

		return _renderer;
	}

	public static class RowHeaderPanel extends JPanel {

		private static final long serialVersionUID = 1L;
		private JLabel _rowIndexLabel = new JLabel();
		private JLabel _textLabel = new JLabel();

		public RowHeaderPanel() {
			this.setLayout(new BorderLayout());
			Dimension d = _rowIndexLabel.getPreferredSize();
			_rowIndexLabel.setPreferredSize(new Dimension(40, d.height));
			_rowIndexLabel.setHorizontalAlignment(CENTER);
			_rowIndexLabel.setVerticalAlignment(TOP);

			_textLabel.setHorizontalAlignment(LEFT);
			_textLabel.setVerticalAlignment(TOP);

			this.add(_rowIndexLabel, BorderLayout.WEST);
			this.add(_textLabel, BorderLayout.CENTER);
			setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		}

		public void setRowIndex(int rowIndex) {
			_rowIndexLabel.setText(String.format("%d", rowIndex + 1));
		}

		public void setText(String text) {
			_textLabel.setText(text);
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(SystemColor.controlShadow);
			g.drawLine(_rowIndexLabel.getWidth() - 2, 0, _rowIndexLabel.getWidth() - 2, getHeight());
			g.setColor(SystemColor.controlLtHighlight);
			g.drawLine(_rowIndexLabel.getWidth() - 1, 0, _rowIndexLabel.getWidth() - 1, getHeight());
		}

	}

}

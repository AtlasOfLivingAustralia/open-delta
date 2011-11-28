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
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class StatusBar extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private JProgressBar _prog;
	private JLabel _label;

	public StatusBar() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(400, 23));
		_prog = new JProgressBar();
		_prog.setPreferredSize(new Dimension(0, 20));
		_prog.setVisible(false);
		_label = new JLabel();
		_prog.setMaximum(100);
		_prog.setMinimum(0);
		add(_prog, BorderLayout.WEST);
		add(_label, BorderLayout.CENTER);
		_prog.setValue(0);
	}

	public void clear() {
		_prog.setValue(0);
		_label.setText("");
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {

			int percentComplete = (Integer) evt.getNewValue();
			int value = Math.min(percentComplete, 100);
			if (value < 0 || value == 100) {
				if (!_prog.isVisible()) {
					_prog.setVisible(true);
					_prog.setPreferredSize(new Dimension(400, 23));
				}
				_prog.setValue(value);
			} else {
				_prog.setVisible(false);
				_prog.setPreferredSize(new Dimension(0, 23));
			}
		} else if ("message".equals(evt.getPropertyName())) {
			_label.setText((String) evt.getNewValue());
		}

	}

}

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

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import au.org.ala.delta.ui.rtf.RtfToolBar;

/**
 * Contains functionality common to the ControlledByEditor and the ControllingAttributeEditor
 */
public abstract class CharacterDepencencyEditor extends CharacterEditTab {

	private static final long serialVersionUID = 6353749833335368389L;

	
	public CharacterDepencencyEditor(RtfToolBar toolbar) {
		super(toolbar);
	}
	
	class ButtonEnabler implements ListSelectionListener {

		private AbstractButton _button;
		private JList _list;
		
		public ButtonEnabler(AbstractButton button, JList list) {
			_list = list;
			_button = button;
			list.addListSelectionListener(this);
			valueChanged(null);
		}
		@Override
		public void valueChanged(ListSelectionEvent e) {
			_button.setEnabled(_list.getSelectedValues().length > 0);
		}	
	}
	

	class GreyOutValuesRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 8322653405800736584L;
		
		private Color _usedForeground = Color.LIGHT_GRAY;
		private Color _normalForeground = UIManager.getColor("Label.foreground");
	
		private List<? extends Object> _valuesToGreyOut;
		
		public GreyOutValuesRenderer(List<? extends Object> valuesToGreyOut) {
			_valuesToGreyOut = valuesToGreyOut;
		}
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String description = formatValue(value);
			super.getListCellRendererComponent(list, description, index, isSelected, cellHasFocus);
			if (_valuesToGreyOut.contains(value)) {
				setForeground(_usedForeground);
			}
			else {
				setForeground(_normalForeground);
			}
			return this;
		}
		
		public String formatValue(Object value) {
			return value.toString();
		}
	}
}

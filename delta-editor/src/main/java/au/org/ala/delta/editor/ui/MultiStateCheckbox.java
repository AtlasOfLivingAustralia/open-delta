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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.ui.util.IconHelper;

public class MultiStateCheckbox extends JCheckBox {

	private static final long serialVersionUID = 1L;

	private static final String ICON_PATH = "/au/org/ala/delta/editor/ui/resources/icons";

	private static final ImageIcon _unselected_icon = IconHelper.createImageIconFromAbsolutePath(ICON_PATH + "/checkbox_unselected.png");
	private static final ImageIcon _disabled_icon = IconHelper.createImageIconFromAbsolutePath(ICON_PATH + "/checkbox_disabled.png");
	private static final ImageIcon _explicit_icon = IconHelper.createImageIconFromAbsolutePath(ICON_PATH + "/checkbox_explicit.png");
	private static final ImageIcon _implicit_icon = IconHelper.createImageIconFromAbsolutePath(ICON_PATH + "/checkbox_implicit.png");
	private static final ImageIcon _inapplicable_icon = IconHelper.createImageIconFromAbsolutePath(ICON_PATH + "/checkbox_inapplicable.png");

	private MultiStateCharacter _character;
	private int _stateNo;
	private Item _item;
	private boolean _inapplicable;

	public MultiStateCheckbox() {
		super();
		setIcon(_unselected_icon);
		setDisabledIcon(_disabled_icon);
		setSelectedIcon(_explicit_icon);

		this.addPropertyChangeListener("selected", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				calculateState();
			}
		});

	}

	protected void calculateState() {
		setSelected(false);
		if (_item == null || _character == null || _stateNo <= 0) {
			this.setEnabled(false);
		} else {
			this.setEnabled(!_inapplicable);
			
			if (_inapplicable) {
				setDisabledIcon(_inapplicable_icon);
			} else {
				setDisabledIcon(_disabled_icon);
				MultiStateAttribute attr = (MultiStateAttribute) _item.getAttribute(_character);
				setSelected(attr.isStatePresent(_stateNo));
				if (attr.isImplicit()) {
					setSelectedIcon(_implicit_icon);
				}
				else {
					setSelectedIcon(_explicit_icon);
				}
			}

		}
	}

	public void bind(MultiStateCharacter character, Item item, int stateNo, boolean inapplicable) {
		_character = character;
		_stateNo = stateNo;
		_item = item;
		_inapplicable = inapplicable;
		calculateState();
	}

}

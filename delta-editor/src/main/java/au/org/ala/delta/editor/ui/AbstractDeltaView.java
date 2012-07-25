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

import au.org.ala.delta.editor.DeltaView;
import au.org.ala.delta.ui.help.HelpController;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.beans.PropertyVetoException;

/**
 * Doesn't do much - saves child classes from implementing DeltaView methods that they don't need.
 */
public abstract class AbstractDeltaView extends JInternalFrame implements DeltaView {

	private static final long serialVersionUID = 8155132044926348203L;

	private JInternalFrame _owner;

	@Override
	public void open() {
	}

	@Override
	public boolean editsValid() {
		return true;
	}

	@Override
	public ReorderableList getCharacterListView() {
		return null;
	}

	@Override
	public ReorderableList getItemListView() {
		return null;
	}

    @Override
    public ReorderableList getStateListView() {
        return null;
    }

	@Override
	public boolean canClose() {
		return true;
	}

    /**
     * Default implementation that uses the class name as the help key.
     *
     * @param helpController handles Help notifications and actions.
     */
    @Override
    public void registerHelp(HelpController helpController) {
        helpController.setHelpKeyForComponent(this, getClass().getSimpleName());
    }

    protected void setOwner(JInternalFrame owner) {
		_owner = owner;
		if (_owner != null) {
			addInternalFrameListener(new InternalFrameAdapter() {
				@Override
				public void internalFrameClosed(InternalFrameEvent e) {
					if (_owner != null) {
						try {
							_owner.setSelected(true);
						} catch (PropertyVetoException ex) {
							// ignore
						}
						_owner.requestFocus();
					}
				}
			});
		}
	}

}

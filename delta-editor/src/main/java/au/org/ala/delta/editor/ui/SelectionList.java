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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JList;
import javax.swing.KeyStroke;


/**
 * A SelectionList is a JList that responds to a double click or enter key press to 
 * signify a selection.
 */
public class SelectionList extends JList implements ReorderableList {

	private static final long serialVersionUID = 5432779887537902393L;
	private static final String SELECTION_ACTION_NAME = "selectionAction";
	
	
	/**
	 * Registers the action to take when a selection (double click or Enter key) has been made on
	 * this list.
	 * @param action the action that will be invoked on selection.
	 */
	public void setSelectionAction(Action action) {
		addMouseListener(new DoubleClickToAction());
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), SELECTION_ACTION_NAME);
		getActionMap().put(SELECTION_ACTION_NAME, action);
	}
	
	/**
	 * Detects double clicks and treats them as a different type of selection event.
	 */
	public class DoubleClickToAction extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
				int index = locationToIndex(e.getPoint());
				setSelectedIndex(index);
				
				Action action = getActionMap().get(SELECTION_ACTION_NAME);
	
				if (action != null) {
					ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
					action.actionPerformed(event);
				}
			}
		}
	}
	
	@Override
	public void setSelectedIndex(int index) {
		super.setSelectedIndex(index);
		ensureIndexIsVisible(index);
	}
	
	@Override
	public int getDropLocationIndex(javax.swing.TransferHandler.DropLocation dropLocation) {
		
		if (dropLocation != null) {
			return ((DropLocation)dropLocation).getIndex();
		}
		return -1;
	}

}

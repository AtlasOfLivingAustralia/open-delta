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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Listens for Enter / Shift Enter.  By default does nothing and can be
 * used to simply consume these key combinations.
 */
public class SelectionNavigationKeyListener implements KeyListener {
	
	@Override
	public void keyReleased(KeyEvent e) {
		if (isSelectionNavigationKeyCombination(e)) {
			e.consume();
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		if (isSelectionNavigationKeyCombination(e)) {
			e.consume();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (isSelectionNavigationKeyCombination(e)) {
			e.consume();
			if (e.getModifiers() == 0) {
				advanceSelection();
			}
			else {
				reverseSelection();
			}
		}
	}
	
	protected void advanceSelection() {}
	protected void reverseSelection() {}
		
	protected boolean isSelectionNavigationKeyCombination(KeyEvent e) {
		return noModifiersOrShift(e.getModifiers()) && e.getKeyCode() == KeyEvent.VK_ENTER;
	}
		
	protected boolean noModifiersOrShift(int modifiers) {
		return ((modifiers == 0) || ((modifiers & KeyEvent.SHIFT_MASK) > 0));
	}
}

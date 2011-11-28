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
package au.org.ala.delta.editor.ui.util;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 * Convenience class to display popup menus in a platform dependent way.
 * Taken from the Swing tutorial with minor changes.
 *
 */
public class PopupMenuListener extends MouseAdapter {
	
	private JPopupMenu _popup;
	protected JComponent _component;
	
	public PopupMenuListener(JPopupMenu popup, JComponent component) {
		_popup = popup;
		_component = component;
		component.addMouseListener(this);
	}
	
	public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    protected void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
        	showPopup(new Point(e.getX(), e.getY()));
        }
    }
    
    protected JPopupMenu getPopup() {
    	return _popup;
    }
    
    protected void showPopup(Point p) {
    	getPopup().show(_component, p.x, p.y);
    }
}

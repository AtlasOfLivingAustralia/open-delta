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
package au.org.ala.delta.ui.image.overlay;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import au.org.ala.delta.model.image.ImageOverlay;

/**
 * An RectangleHotSpot draws and implements detection based on an rectangular shaped
 * HotSpot.
 */
public class RectangleHotSpot extends HotSpot implements MouseListener {

	private static final long serialVersionUID = 1190841871644406245L;

	public RectangleHotSpot(ImageOverlay overlay, int index) {
		super(overlay, index);
		addMouseListener(this);
	}
	
	protected void drawHotSpot(Graphics g) {
		Rectangle bounds = getBounds();
		g.drawRect(0, 0, bounds.width-1, bounds.height-1);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		fireHotSpotEntered();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		fireHotSpotExited();
	}
}

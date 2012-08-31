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
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;

import au.org.ala.delta.model.image.ImageOverlay;

/**
 * An OvalHotSpot draws and implements detection based on an oval shaped
 * HotSpot.
 */
public class OvalHotSpot extends HotSpot implements MouseMotionListener, MouseListener {

	private static final long serialVersionUID = -5472490468706567843L;

	public OvalHotSpot(ImageOverlay overlay, int index) {
		super(overlay, index);
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	
	protected void drawHotSpot(Graphics g) {
		Rectangle bounds = getBounds();
		g.drawOval(0, 0, bounds.width-1, bounds.height-1);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		Rectangle bounds = getBounds();
		Shape ellipse = new Ellipse2D.Double(0d, 0d, (double)bounds.width-1, (double)bounds.height-1);
		if (ellipse.contains(e.getX(), e.getY())) {
			fireHotSpotEntered();
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {
		fireHotSpotExited();
	}

}

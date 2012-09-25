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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.ui.image.ImageViewer;
import au.org.ala.delta.ui.image.OverlaySelectionObserver;
import au.org.ala.delta.ui.image.SelectableOverlay;

/**
 * An OverlayButton is used to display the image overlay types:
 * OK, Cancel and Notes.
 */
public class OverlayButton extends JButton implements ActionListener, SelectableOverlay, OverlayLocationProvider {
	private static final long serialVersionUID = 7019370330547978789L;

	private ImageOverlay _overlay;
	private SelectableOverlaySupport _support;
	
	public OverlayButton(ImageOverlay overlay, String text) {
		super(text);
		_overlay = overlay;
		_support = new SelectableOverlaySupport();
		addActionListener(this);
		this.setMargin(new Insets(2, 2, 2, 2));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		_support.fireOverlaySelected(this);
	}

	@Override
	public OverlayLocation location(ImageViewer viewer) {
	    System.out.println(this.getMargin());
		return new PreferredSizeTextOverlayLocation(viewer, this, _overlay.getLocation(0));
	}

	@Override
	public void setSelected(boolean selected) {}
	
	@Override
	public boolean isSelected() {
		return false;
	}
	
	public void linkTo(OverlayButton other) {
		
	}

	@Override
	public ImageOverlay getImageOverlay() {
		return _overlay;
	}

	@Override
	public void addOverlaySelectionObserver(OverlaySelectionObserver observer) {
		_support.addOverlaySelectionObserver(observer);
	}

	@Override
	public void removeOverlaySelectionObserver(OverlaySelectionObserver observer) {
		_support.removeOverlaySelectionObserver(observer);
	}
	
	
}

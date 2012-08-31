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

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.ui.image.OverlaySelectionObserver;
import au.org.ala.delta.ui.image.SelectableOverlay;

public class SelectableOverlaySupport {

	private List<OverlaySelectionObserver> _observers;

	public SelectableOverlaySupport() {
		_observers = new ArrayList<OverlaySelectionObserver>();
	}
	
	public void addOverlaySelectionObserver(OverlaySelectionObserver observer) {
		_observers.add(observer);
	}
	
	public void removeOverlaySelectionObserver(OverlaySelectionObserver observer) {
		_observers.remove(observer);
	}
	
	public void fireOverlaySelected(SelectableOverlay overlay) {
		for (int i=_observers.size()-1; i>=0; i--) {
			_observers.get(i).overlaySelected(overlay);
		}
	}
}

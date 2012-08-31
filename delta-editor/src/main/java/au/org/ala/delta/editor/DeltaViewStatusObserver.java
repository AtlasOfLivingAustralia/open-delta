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
package au.org.ala.delta.editor;

/**
 * Provides information about changes to the state of a DeltaView.
 */
public interface DeltaViewStatusObserver {
	/**
	 * Called when a view is closed.
	 * @param controller the controller managing the view.
	 * @param view the view that has closed.
	 */
	public void viewClosed(DeltaViewController controller, DeltaView view);
	
	/**
	 * Called when a view is selected/focused.
	 * @param controller the controller managing the view.
	 * @param view the view that has been selected.
	 */
	public void viewSelected(DeltaViewController controller, DeltaView view);
}

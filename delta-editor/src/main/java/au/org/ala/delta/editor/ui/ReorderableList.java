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

import javax.swing.*;
import javax.swing.TransferHandler.DropLocation;

/**
 * An interface to be implemented by user interface components presenting lists of Characters, Items or Character
 * States.
 * Its use allows the controller classes to be reused across multiple views.
 */
public interface ReorderableList {
	
	public int getSelectedIndex();
	
	public void setSelectedIndex(int index);
	
	public int getDropLocationIndex(DropLocation dropLocation);
	
	public void setSelectionAction(Action action);

    public JComponent getListViewComponent();
}

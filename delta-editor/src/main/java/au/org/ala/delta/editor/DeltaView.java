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

import au.org.ala.delta.editor.ui.ReorderableList;

/**
 * This interface should be implemented by views of a DeltaDataSet.
 * It's purpose is to allow a view to be implemented as a tab or internal frame. (and
 * potentially to allow mocking of a view in unit tests).
 */
public interface DeltaView {

	public String getViewTitle();
	public void open();
	public boolean editsValid();
	
	public ReorderableList getCharacterListView();
	public ReorderableList getItemListView();
	public boolean canClose();
}

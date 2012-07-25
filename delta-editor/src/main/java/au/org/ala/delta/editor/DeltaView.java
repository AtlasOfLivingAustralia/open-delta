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
import au.org.ala.delta.ui.help.HelpController;

/**
 * This interface should be implemented by views of a DeltaDataSet.
 * It's purpose is to allow a view to be implemented as a tab or internal frame. (and
 * potentially to allow mocking of a view in unit tests).
 */
public interface DeltaView {

    /**
     * @return the title to be displayed for this view.
     */
	public String getViewTitle();

    /**
     * Callback when the view is opened.
     */
	public void open();

    /**
     * Should return true if any edits made on the view are currently valid.  Implementations of this method should
     * attempt to commit any in progress edits when this method is invoked.
     * @return false if there are invalid edits current, true otherwise.
     */
	public boolean editsValid();

    /**
     * A DeltaView may provide an interface that displays a list of Characters - if so this method should return
     * it so the controller can add appropriate event handlers to the view.
     * @return the view's implementation of the Character list or null if the view does not provide a way to
     * interact with a list of Characters.
     */
	public ReorderableList getCharacterListView();


    /**
     * A DeltaView may provide an interface that displays a list of Items - if so this method should return
     * it so the controller can add appropriate event handlers to the view.
     * @return the view's implementation of the Item list or null if the view does not provide a way to
     * interact with a list of Items.
     */
    public ReorderableList getItemListView();


    /**
     * A callback to the view when the view has been attempted to be closed by the user.  If the view shouldn't
     * be closed (for example there are invalid edits) then this method should return false.
     * @return true if the view can be closed, false otherwise.
     */
    public boolean canClose();

    /**
     * Gives the view the opportunity to create the appropriate associations with the HelpController to support
     * context sensitive help.
     * @param helpController handles Help notifications and actions.
     */
    public void registerHelp(HelpController helpController);
}

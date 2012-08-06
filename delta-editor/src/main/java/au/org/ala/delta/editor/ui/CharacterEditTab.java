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

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.ui.rtf.RtfEditor;
import au.org.ala.delta.ui.rtf.RtfToolBar;

import javax.swing.*;
import java.awt.*;

/**
 * The CharacterEditTab is a base class for tabs displayed on the CharacterEditor.
 */
public abstract class CharacterEditTab extends JPanel {

	private static final long serialVersionUID = 6534903951320447689L;

	protected RtfEditor editor;
	
	/** The character that is being edited by this tab */
	protected Character _character;
	
	/** The model being edited by this tab */
	protected EditorViewModel _model;

	/** Can be used by tabs for rich text support */
	protected RtfToolBar _toolbar;
	
	public CharacterEditTab(RtfToolBar toolbar) {
		super();
		_toolbar = toolbar;
	}

	public CharacterEditTab(LayoutManager layout) {
		super(layout);
	}

	public CharacterEditTab(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public CharacterEditTab(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

    /**
     * Requests that any edits in progress are immediately validated and committed.
     * @return true if the edits are valid and the commit succeeded.
     */
	public boolean isContentsValid() {
		return true;
	}
	
	/**
	 * Sets the Character for editing.
	 * @param character the Character to edit.
	 */
	public abstract void bind(EditorViewModel model, au.org.ala.delta.model.Character character);

}

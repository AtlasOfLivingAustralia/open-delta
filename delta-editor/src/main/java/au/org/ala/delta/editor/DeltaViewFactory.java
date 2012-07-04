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

import au.org.ala.delta.editor.model.DeltaViewModel;
import au.org.ala.delta.editor.ui.ActionSetsDialog;
import au.org.ala.delta.editor.ui.CharacterEditor;
import au.org.ala.delta.editor.ui.DirectiveFileEditor;
import au.org.ala.delta.editor.ui.ItemEditor;
import au.org.ala.delta.editor.ui.MatrixViewer;
import au.org.ala.delta.editor.ui.TreeViewer;
import au.org.ala.delta.editor.ui.image.ImageEditor;
import au.org.ala.delta.editor.ui.image.ImageSettingsDialog;

import javax.swing.*;


/**
 * The DeltaViewFactory is responsible for creating DeltaViews.
 * Right now it doesn't do much - the intent is to eventually turn this into an 
 * abstract factory to remove the current dependency on JInternalFrames.
 * This is so an alternative view implementation (e.g. tabs) will be easier to write.
 */
public class DeltaViewFactory {

	
	public DeltaView createTreeView(DeltaViewModel model) {
		return new TreeViewer(model);
	}
	
	public DeltaView createGridView(DeltaViewModel model) {
		return new MatrixViewer(model);
	}
	
	public DeltaView createItemEditView(DeltaViewModel model, JInternalFrame owner) {
		return new ItemEditor(model, owner);
	}

	public DeltaView createCharacterEditView(DeltaViewModel model, JInternalFrame owner) {
		return new CharacterEditor(model, owner);
	}
	
	public DeltaView createImageEditorView(DeltaViewModel model) {
		return new ImageEditor(model);
	}

	public DeltaView createDirectivesEditorView(DeltaViewModel model) {
		return new DirectiveFileEditor(model);
	}

	public DeltaView createActionSetsView(DeltaViewModel model) {
		return new ActionSetsDialog(model);
	}

    public DeltaView createImageSettingsView(DeltaViewModel model) {
        return new ImageSettingsDialog(model, model.getImageSettings());
    }
}

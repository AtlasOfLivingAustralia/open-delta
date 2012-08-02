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
package au.org.ala.delta.delfor;

import au.org.ala.delta.editor.directives.DirectivesFileImporter;
import au.org.ala.delta.editor.directives.ImportContext;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.util.Pair;

import java.util.Arrays;

/**
 * This subclass of DirectivesFileImporter behaves in the same way as it's
 * parent.  The only difference is that some of the CONFOR directives
 * that have reconfigured so that they do not execute some critical directives
 * that should have already been executed.
 */
public class DelforDirectivesFileImporter extends DirectivesFileImporter {
	
	private Directive[] _conforDirArray;
	
	public DelforDirectivesFileImporter(EditorViewModel model, ImportContext context) {
		super(model, context);
		initialiseConforDirectives();
	}

	private void initialiseConforDirectives() {
		_conforDirArray = Arrays.copyOf(ConforDirType.ConforDirArray, ConforDirType.ConforDirArray.length);
		int[] toCopy = {ConforDirType.CHARACTER_TYPES, ConforDirType.MAXIMUM_NUMBER_OF_ITEMS, 
				ConforDirType.MAXIMUM_NUMBER_OF_STATES, ConforDirType.NUMBER_OF_CHARACTERS, 
				ConforDirType.NUMBERS_OF_STATES, ConforDirType.ITEM_DESCRIPTIONS};
		
		for (int dirId : toCopy) {
			_conforDirArray[dirId] = copyWithoutImplementationClass(_conforDirArray[dirId]);
		}
	}

	@Override
	protected Pair<Directive[], Integer> directivesOfType(DirectiveType type) {
		Pair<Directive[], Integer> directives = super.directivesOfType(type);

		if (directives.getFirst() == ConforDirType.ConforDirArray) {
			directives = new Pair<Directive[], Integer>(_conforDirArray, directives.getSecond());
		}
		return directives;
	}

	private Directive copyWithoutImplementationClass(Directive directive) {
		return new Directive(directive.getName(), directive.getLevel(), 
				directive.getNumber(), DirectiveArgType.DIRARG_NONE, directive.getInFunc(),
				directive.getOutFunc());
	}
	
}

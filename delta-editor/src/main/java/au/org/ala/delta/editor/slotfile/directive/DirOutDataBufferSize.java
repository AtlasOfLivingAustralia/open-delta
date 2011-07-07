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
package au.org.ala.delta.editor.slotfile.directive;

import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.model.DeltaDataSet;


/**
 * Exports the DATA BUFFER SIZE directive.
 */
public class DirOutDataBufferSize extends AbstractDirOutFunctor {

	@Override
	public void writeDirectiveArguments(DirectiveInOutState state) {
		DeltaDataSet dataSet = state.getDataSet();
		DirectiveArguments args = state.getCurrentDirective().getDirectiveArguments();
		_textBuffer.append(' ');
		float val = 0;
		if (args.size() > 0) {
		    val = args.get(0).getValue().floatValue();
		}
		val = Math.max(val, 4000);
		_textBuffer.append((int)Math.max(val, dataSet.getNumberOfCharacters() * 50));
		writeLine(state, _textBuffer.toString());
	}

}

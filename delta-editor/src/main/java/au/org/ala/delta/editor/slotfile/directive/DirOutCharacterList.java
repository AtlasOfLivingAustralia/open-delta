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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.TranslateType;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.DataSetTranslatorFactory;
import au.org.ala.delta.translation.ItemListTypeSetterAdapter;

/**
 * Exports the CHARACTER LIST directive.
 */
public class DirOutCharacterList extends AbstractDirOutFunctor {

	@Override
	public void writeDirectiveArguments(DirectiveInOutState state) {
		state.getPrinter().writeBlankLines(1, 0);
		state.getPrinter().setUseParagraphIndentOnLineWrap(false);
		
		DataSetTranslatorFactory factory = new DataSetTranslatorFactory();
		DeltaContext context = new DeltaContext(state.getDataSet());
		context.setTranslateType(TranslateType.Delta);
		
		DataSetTranslator translator = factory.createDeltaFormatTranslator(
				context, state.getPrinter(), new ItemListTypeSetterAdapter());
		try {
			translator.translateCharacters();
		}
		catch (DirectiveException e) {
			state.error(e);
		}
		state.getPrinter().setUseParagraphIndentOnLineWrap(true);
	}

}

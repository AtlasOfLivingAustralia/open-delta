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
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.translation.DataSetTranslator;
import au.org.ala.delta.translation.DataSetTranslatorFactory;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.ItemListTypeSetterAdapter;

public class DirOutItemDescriptions extends AbstractDirOutFunctor {

	@Override
	public void writeDirectiveArguments(DirectiveInOutState state) {
		
		DataSetTranslatorFactory factory = new DataSetTranslatorFactory();
		DeltaContext context = new DeltaContext(state.getDataSet());
		context.setTranslateType(TranslateType.Delta);
		ItemListTypeSetter typeSetter = null;
		final PrintFile printFile = state.getPrinter();
		printFile.setUseParagraphIndentOnLineWrap(false);
		if (state.getNewLineAfterAttributes()) {
			
			typeSetter = new ItemListTypeSetterAdapter() {
				@Override
				public void beforeFirstItem() {
					printFile.setIndentOnLineWrap(true);
					printFile.setLineWrapIndent(2);
				}
				@Override
				public void afterAttribute(Attribute attribute) {
					printFile.printBufferLine();
				}
			};
		}
		else {
			typeSetter = new ItemListTypeSetterAdapter();
		}
		DataSetTranslator translator = factory.createDeltaFormatTranslator(context, printFile, typeSetter);
		
		translator.translateItems();
		printFile.setUseParagraphIndentOnLineWrap(true);
		
	}
}

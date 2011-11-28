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
package au.org.ala.delta.translation;

import java.util.List;

import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.directives.validation.DirectiveException;

/**
 * Allows multiple translations to occur (sequentially) on the same data
 * set.
 */
public class CompositeDataSetTranslator implements DataSetTranslator {

	private List<DataSetTranslator> _translators;
	
	public CompositeDataSetTranslator(List<DataSetTranslator> translators) {
		_translators = translators;
	}
	
	@Override
	public void translateCharacters() throws DirectiveException {
		for (DataSetTranslator translator : _translators) {
			translator.translateCharacters();
		}
	}

	@Override
	public void translateItems() throws DirectiveException {
		for (DataSetTranslator translator : _translators) {
			translator.translateItems();
		}
	}

	@Override
	public void translateOutputParameter(OutputParameter parameter) {
		for (DataSetTranslator translator : _translators) {
			translator.translateOutputParameter(parameter);
		}
	}

}

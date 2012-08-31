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

import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.directives.validation.DirectiveException;

/**
 * A DataSetTranslator is responsible for turning a DeltaDataSet into another format.
 * Translations are potentially triggered at three points in the directive processing procedure:
 * <ul>
 *     <li>when the CHARACTER LIST directive is encountered.  This will result in a call on the translateCharacters method.</li>
 *     <li>when the ITEM DESCRIPTIONS directive is encountered.  This will result in a call on the translateItems method.</li>
 *     <li>when the OUTPUT PARAMETERS directive is encountered.  This will result in one of more calls to the translateOutputParameter method.</li>
 * </ul>
 * Implementations are not required to handle each of these directives, if they do not support a particular translation
 * they should implement a method that does nothing.
 */
public interface DataSetTranslator {

    public enum TranslationPhase {CHARACTERS, ITEMS, OUTPUT_PARAMETERS};

	public void translateCharacters() throws DirectiveException;
	
	public void translateItems() throws DirectiveException;
	
	public void translateOutputParameter(OutputParameter parameterName);
}

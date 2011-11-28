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

/**
 * A translator that does nothing.
 */
public class NullTranslator implements DataSetTranslator {

	@Override
	public void translateCharacters() {}

	@Override
	public void translateItems() {}

	@Override
	public void translateOutputParameter(OutputParameter parameterName) {}
}

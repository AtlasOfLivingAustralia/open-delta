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
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.attribute.CommentedValueList.Values;

public abstract class AbstractIterativeTranslator implements IterativeTranslator {
	
	@Override
	public void beforeFirstItem() {};

	@Override
	public void beforeItem(Item item) {};

	@Override
	public void afterItem(Item item) {};

	@Override
	public void beforeAttribute(Attribute attribute) {};

	@Override
	public void afterAttribute(Attribute attribute) {};

	@Override
	public void afterLastItem() {};

	@Override
	public void attributeComment(String comment) {};

	@Override
	public void attributeValues(Values values) {};
	
	@Override
	public void beforeFirstCharacter() {};
	
	@Override
	public void beforeCharacter(Character character) {};
	
	@Override
	public void afterCharacter(Character character) {};
	
	@Override
	public void afterLastCharacter() {};
	
	@Override
	public void translateOutputParameter(OutputParameter parameterName) {};
}

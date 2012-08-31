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

/**
 * An IterativeTranslator is one that can perform a data set translation
 * one Item or Character at a time.
 * It's purpose is to allow interleaving of the output of multiple translations
 * for example TRANSLATE INTO NATURAL LANGUAGE and PRINT UNCODED CHARACTERS.
 *
 */
public interface IterativeTranslator {
	public void beforeFirstItem();

	public void beforeItem(Item item);

	public void afterItem(Item item);

	public void beforeAttribute(Attribute attribute);

	public void afterAttribute(Attribute attribute);

	public void afterLastItem();

	public void attributeComment(String comment);

	public void attributeValues(Values values);
	
	public void beforeFirstCharacter();
	
	public void beforeCharacter(Character character);
	
	public void afterCharacter(Character character);
	
	public void afterLastCharacter();
	
	public void translateOutputParameter(OutputParameter parameterName);
}

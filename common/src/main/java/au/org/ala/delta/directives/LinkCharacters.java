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
package au.org.ala.delta.directives;

import java.util.HashSet;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;

/**
 * The link characters directive specifies that a set of characters that should be placed in the same 
 * sentence in natural language descriptions.
 */
public class LinkCharacters extends AbstractCharacterSetDirective<DeltaContext> {
	
	public LinkCharacters() {
		super("link", "characters");
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARGROUPS;
	}
	
	@Override
	protected void processCharacterSet(DeltaContext context, List<Integer> characters) {
		context.linkCharacters(new HashSet<Integer>(characters));
	}	
	
	@Override
	public int getOrder() {
		return 4;
	}
}

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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.model.MultiStateCharacter;

public class NumbersOfStates extends AbstractCharacterListDirective<DeltaContext, Integer> {
	
	public NumbersOfStates() {
		super("numbers", "of", "states");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_INTERNAL;
	}

	@Override
	protected Integer interpretRHS(DeltaContext context, String rhs) {
		return Integer.parseInt(rhs);
	}

	@Override
	protected void processCharacter(DeltaContext context, int charIndex, Integer rhs) {
		au.org.ala.delta.model.Character ch = context.getCharacter(charIndex);
		if (ch == null) {
			throw new RuntimeException("Attempt to set number of states on an undefined character: " + charIndex);
		}
		if (ch instanceof MultiStateCharacter) {
			Logger.debug("Setting number of states on character %d to %d", charIndex, rhs);			
			MultiStateCharacter msch = (MultiStateCharacter) ch;
			msch.setNumberOfStates(rhs);
		} else {
			throw new RuntimeException("Attempt to set number of states on an non-multistate character: " + charIndex);
		}
	}

	@Override
	protected void addArgument(DirectiveArguments args, int charIndex, String value) {}
	
}

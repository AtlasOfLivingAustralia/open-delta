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
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;

public class NumbersOfStates extends AbstractCharacterListDirective<DeltaContext, Integer> {
	
	public static final int DEFAULT_NUMBER_OF_STATES = 2;
	
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
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		super.process(context, directiveArguments);
		
		setDefaults(context);
	}

	/**
	 * Sets the number of states to "2" for any multistate characters not
	 * explicitly assigned a number of states.
	 */
	private void setDefaults(DeltaContext context) {
		DeltaDataSet dataSet = context.getDataSet();
		for (int i=1; i<=dataSet.getNumberOfCharacters(); i++) {
			au.org.ala.delta.model.Character character = dataSet.getCharacter(i);
			if (character.getCharacterType().isMultistate()) {
				MultiStateCharacter multiStateChar = (MultiStateCharacter)character;
				if (multiStateChar.getNumberOfStates() == 0) {
					processCharacter(context, i, DEFAULT_NUMBER_OF_STATES);
				}
			}
		}
	}
	
	@Override
	protected void addArgument(DirectiveArguments args, int charIndex, String value) {
		args.addNumericArgument(charIndex, value);
	}
}

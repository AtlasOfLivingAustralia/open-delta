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

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.util.Pair;

public class CharacterTypes extends AbstractCharacterListDirective<DeltaContext, String> {

	private static final CharacterType DEFAULT_CHAR_TYPE = CharacterType.UnorderedMultiState;
	/** 
	 * Tracks the number last character that was created to allow defaults to be created 
	 * for characters not explicitly specified
	 */
	private Map<Integer, Pair<CharacterType, Boolean>> _characterTypes;
	
	public CharacterTypes() {
		super("character", "types");
		_characterTypes = new HashMap<Integer, Pair<CharacterType,Boolean>>();
	}

	@Override
	protected String interpretRHS(DeltaContext context, String rhs) {
		return rhs;
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_INTERNAL;
	}
	
	/**
	 * Overrides process in the parent class to create any default characters required after
	 * the last explicitly typed one.
	 */
	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
		
		super.process(context, args);
		
		createCharacters(context);
		
	}

	protected void createCharacters(DeltaContext context) {
		MutableDeltaDataSet dataSet = context.getDataSet();
		for (int i=1; i<=context.getNumberOfCharacters(); i++) {
			CharacterType charType = DEFAULT_CHAR_TYPE;
			boolean exclusive = false;
			if (_characterTypes.containsKey(i)) {
				charType = _characterTypes.get(i).getFirst();
				exclusive = _characterTypes.get(i).getSecond();
			}
			
			au.org.ala.delta.model.Character character = dataSet.addCharacter(i, charType);
			if (charType.isMultistate()) {
				((MultiStateCharacter)character).setExclusive(exclusive);
			}
		}
	}

	@Override
	protected void processCharacter(DeltaContext context, int charNumber, String type) throws ParseException {
		Logger.debug("Setting type for character %d to %s", charNumber, type);
		
		
		boolean exclusive = false;
		if (type.startsWith("E")) {
			exclusive = true;
			type = type.substring(1);
		}
		
		CharacterType charType = CharacterType.parse(type);
		
		if (exclusive) {
			if (!charType.isMultistate()) {
				throw new ParseException("Invalid character type: "+type, 
						(int)context.getCurrentParsingContext().getCurrentOffset());
			}
		}
		_characterTypes.put(charNumber, new Pair<CharacterType, Boolean>(charType, exclusive));
	}

	@Override
	protected void addArgument(DirectiveArguments args, int charIndex, String value) {
		args.addTextArgument(charIndex, value);
	}
	
	
}

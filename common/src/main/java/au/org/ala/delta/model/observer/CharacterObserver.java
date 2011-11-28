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
package au.org.ala.delta.model.observer;

import au.org.ala.delta.model.Character;

/**
 * This interface should be implemented by classes interested in being notified of changes to Characters.
 * They should then call Character.addCharacterObserver(this) to register interest in changes to that
 * character.
 */
public interface CharacterObserver extends ImageObserver {

	/**
	 * Invoked when the Character changes.
	 * @param character the changed character.
	 */
	public void characterChanged(Character character);
	
	/**
	 * Invoked when the Character type changes
	 * @param oldCharacter The old character wrapper
	 * @param newCharacter The new character wrapper
	 */
	public void characterTypeChanged(Character oldCharacter, Character newCharacter);
	
	/**
	 * Invoked when a character state is added/deleted/moved/edited
	 * @param character the character with a changed state.
	 * @param stateNum the affected state number.
	 */
	public void characterStateChanged(Character character, int stateNum);
	
}

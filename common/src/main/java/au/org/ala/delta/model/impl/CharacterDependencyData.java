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
package au.org.ala.delta.model.impl;

import java.util.Set;

public interface CharacterDependencyData {
	public void addDependentCharacter(au.org.ala.delta.model.Character character);
	public void removeDependentCharacter(au.org.ala.delta.model.Character character);
	public abstract void setDescription(String description);
	public abstract String getDescription();
	public abstract Set<Integer> getStates();
	public abstract Set<Integer> getDependentCharacterIds();
	public abstract int getControllingCharacterId();
	public void setStates(Set<Integer> states);
	public void addState(int state);
}

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
}

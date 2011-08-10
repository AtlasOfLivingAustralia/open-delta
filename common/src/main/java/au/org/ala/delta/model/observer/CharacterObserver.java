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
	
}

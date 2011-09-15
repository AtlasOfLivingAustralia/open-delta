package au.org.ala.delta.model;

/**
 * This class is a placeholder for new characters whose type have yet to be determined (by the user).
 * 
 * @author baird
 *
 */
public class UnknownCharacter extends Character {

	protected UnknownCharacter(int number) {
		super(number, CharacterType.Unknown);
	}

}

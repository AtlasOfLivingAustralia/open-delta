package au.org.ala.delta.translation.print;

/**
 * Used to typeset or add typesetting marks when printing a character list
 * (see the PRINT CHARACTER LIST directive).
 */
public interface CharacterListTypeSetter {
	
	public void beforeCharacterOrHeading();
	
	public void beforeFirstCharacter();
	
	public void beforeCharacterHeading();
	
	public void afterCharacterHeading();
	
	public void beforeCharacter();
	
	public void beforeStateDescription();
	
	public void beforeCharacterNotes();
	
	public void afterCharacterList();
}

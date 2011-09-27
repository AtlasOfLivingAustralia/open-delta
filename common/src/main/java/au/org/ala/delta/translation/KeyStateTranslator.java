package au.org.ala.delta.translation;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.IdentificationKeyCharacter.KeyState;
import au.org.ala.delta.translation.attribute.CommentedValueList;
import au.org.ala.delta.translation.attribute.CommentedValueList.CommentedValues;

public class KeyStateTranslator {

	public KeyStateTranslator() {
		
	}
	
	
	public String translate(IdentificationKeyCharacter character) {
		return "";
		
	}
	
	
	private CommentedValueList toParsedAttribute(IdentificationKeyCharacter character) {
		
		List<CommentedValues> values = new ArrayList<CommentedValues>();
		CommentedValueList attribute = new CommentedValueList("", values);
		
		if (character.getCharacterType().isMultistate()) {
			for (KeyState state : character.getStates()) {
				
			}
		}
		
		return attribute;
	}
	
	
	
}

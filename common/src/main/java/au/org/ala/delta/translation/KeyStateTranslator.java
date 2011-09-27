package au.org.ala.delta.translation;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.IdentificationKeyCharacter.KeyState;
import au.org.ala.delta.model.IdentificationKeyCharacter.MultiStateKeyState;
import au.org.ala.delta.model.IdentificationKeyCharacter.NumericKeyState;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.translation.attribute.AttributeTranslator;
import au.org.ala.delta.translation.attribute.CommentedValueList.Values;
import au.org.ala.delta.translation.attribute.MultiStateAttributeTranslator;

public class KeyStateTranslator {

	private FormatterFactory _formatterFactory;
	
	public KeyStateTranslator(
			IdentificationKeyCharacter keyChar, FormatterFactory formatterFactory) {
		_formatterFactory = formatterFactory;
	}
	
	public String translateState(IdentificationKeyCharacter keyChar, int stateNumber) {
		
		KeyState keyState = keyChar.getKeyState(stateNumber);
		if (keyState != null) {
			return translate(keyChar, keyState);
		}
		else {
			// This cast is safe as getNumberOfStates will have already 
			// returned zero for other character types with no defined
			// key states.
			return ((MultiStateCharacter)keyChar.getCharacter()).getState(stateNumber); 
		}
	}
	
	public String translate(IdentificationKeyCharacter keyChar, KeyState state) {
		String separator;
		if (keyChar.getCharacterType().isMultistate()) {
			
			if (keyChar.getCharacterType() == CharacterType.OrderedMultiState) {
				separator = "-";
			}
			else {
				separator = "&";
			}
			return translateMultistateState(keyChar, (MultiStateKeyState)state, separator);
		}
	
		else if (keyChar.getCharacterType().isNumeric()) {
			
			return translateNumericState((NumericKeyState)state);
		}
		return null;
	}
	
	
	private String translateMultistateState(IdentificationKeyCharacter keyChar, MultiStateKeyState state, String separator) {
		List<String> states = new ArrayList<String>();
			
		MultiStateKeyState multiState = (MultiStateKeyState)state;
		for (int i : multiState.originalStates()) {
			states.add(Integer.toString(i));
		}
			
		AttributeTranslator at = new MultiStateAttributeTranslator(
				(MultiStateCharacter)keyChar.getCharacter(), 
				_formatterFactory.createCharacterFormatter(), 
				_formatterFactory.createAttributeFormatter());
			
		Values values =  new Values(states, separator);
		return at.translateValues(values);
	}
	
	private String translateNumericState(NumericKeyState state) {
		
//		FloatRange range = state.getStateNumber();
//				for (int i : multiState.originalStates()) {
//					states.add(Integer.toString(i));
//				}
//			
//			return new Values(states, separator);
		
		return "";
	}
	
	
	
}

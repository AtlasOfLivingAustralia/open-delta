package au.org.ala.delta.translation.key;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.FloatRange;

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.IdentificationKeyCharacter.KeyState;
import au.org.ala.delta.model.IdentificationKeyCharacter.MultiStateKeyState;
import au.org.ala.delta.model.IdentificationKeyCharacter.NumericKeyState;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.translation.FormatterFactory;
import au.org.ala.delta.translation.attribute.AttributeTranslator;
import au.org.ala.delta.translation.attribute.CommentedValueList.Values;
import au.org.ala.delta.translation.attribute.MultiStateAttributeTranslator;

public class KeyStateTranslator {

	private FormatterFactory _formatterFactory;

	public KeyStateTranslator(FormatterFactory formatterFactory) {
		_formatterFactory = formatterFactory;
	}

	public String translateState(IdentificationKeyCharacter keyChar, int stateNumber) {

		KeyState keyState = keyChar.getKeyState(stateNumber);
		if (keyState != null) {
			return translate(keyChar, keyState);
		} else {
			// This cast is safe as getNumberOfStates will have already
			// returned zero for other character types with no defined
			// key states.
			return ((MultiStateCharacter) keyChar.getCharacter()).getState(stateNumber);
		}
	}

	private String translate(IdentificationKeyCharacter keyChar, KeyState state) {
		String separator;
		if (keyChar.getCharacterType().isMultistate()) {

			if (keyChar.getCharacterType() == CharacterType.OrderedMultiState) {
				separator = "-";
			} else {
				separator = "&";
			}
			return translateMultistateState(keyChar, (MultiStateKeyState) state, separator);
		}

		else if (keyChar.getCharacterType().isNumeric()) {

			return translateNumericState(keyChar, (NumericKeyState) state);
		}
		return null;
	}

	private String translateMultistateState(IdentificationKeyCharacter keyChar, MultiStateKeyState state,
			String separator) {
		List<String> states = new ArrayList<String>();

		MultiStateKeyState multiState = (MultiStateKeyState) state;
		for (int i : multiState.originalStates()) {
			states.add(Integer.toString(i));
		}

		AttributeTranslator at = new MultiStateAttributeTranslator((MultiStateCharacter) keyChar.getCharacter(),
				_formatterFactory.createCharacterFormatter(), _formatterFactory.createAttributeFormatter());

		Values values = new Values(states, separator);
		return at.translateValues(values);
	}

	private String translateNumericState(IdentificationKeyCharacter keyChar, NumericKeyState state) {

		FloatRange range = state.stateRange();
		List<String> states = new ArrayList<String>();

		if (keyChar.getCharacter().getCharacterType() == CharacterType.IntegerNumeric) {
			states.add(Integer.toString(range.getMinimumInteger()));
			if (range.getMaximumInteger() != range.getMinimumInteger()) {
				states.add(Integer.toString(range.getMaximumInteger()));
			}
		} else {
			states.add(Float.toString(range.getMinimumFloat()));
			if (range.getMaximumFloat() != range.getMinimumFloat()) {
				states.add(Float.toString(range.getMaximumFloat()));
			}
		}

		AttributeTranslator at = new MultiStateAttributeTranslator((MultiStateCharacter) keyChar.getCharacter(),
				_formatterFactory.createCharacterFormatter(), _formatterFactory.createAttributeFormatter());

		Values values = new Values(states, "-");
		return at.translateValues(values);
	}

}

package au.org.ala.delta.translation.key;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.IdentificationKeyCharacter.KeyState;
import au.org.ala.delta.model.IdentificationKeyCharacter.MultiStateKeyState;
import au.org.ala.delta.model.IdentificationKeyCharacter.NumericKeyState;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.translation.Words;
import au.org.ala.delta.translation.Words.Word;
import au.org.ala.delta.translation.attribute.AttributeTranslator;
import au.org.ala.delta.translation.attribute.AttributeTranslatorFactory;
import au.org.ala.delta.translation.attribute.CommentedValueList.Values;

/**
 * Translates a state defined by the KEY STATES directive into a natural
 * language description used by the KEY program.
 */
public class KeyStateTranslator {

	private static final BigDecimal MIN_VALUE = new BigDecimal(-Float.MAX_VALUE);
	private static final BigDecimal MAX_VALUE = new BigDecimal(Float.MAX_VALUE);
	
	private AttributeTranslatorFactory _attributeTranslatorFactory;

	public KeyStateTranslator(AttributeTranslatorFactory translatorFactory) {
		_attributeTranslatorFactory = translatorFactory;
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

	/**
	 * Produces a state description of a key state defined for a multistate character.
	 * @param keyChar the character as defined by the KEY STATES directive.
	 * @param state details of the redefined state to describe.
	 * @param separator the separator to use.
	 * @return a description of the supplied state of the supplied character.
	 */
	private String translateMultistateState(IdentificationKeyCharacter keyChar, MultiStateKeyState state,
			String separator) {
		List<String> states = new ArrayList<String>();

		MultiStateKeyState multiState = (MultiStateKeyState) state;
		if (keyChar.getCharacterType() == CharacterType.UnorderedMultiState) {
			for (int i : multiState.originalStates()) {
				states.add(Integer.toString(i));
			}
		}
		else {
			List<Integer> originalStates = new ArrayList<Integer>(multiState.originalStates());
			Collections.sort(originalStates);
			states.add(Integer.toString(originalStates.get(0)));
			if (originalStates.size() > 1) {
				states.add(Integer.toString(originalStates.get(originalStates.size()-1)));
			}
		}

		AttributeTranslator at = _attributeTranslatorFactory.translatorFor(keyChar.getCharacter());
		Values values = new Values(states, separator);
		return at.translateValues(values);
	}

	/**
	 * Produces a state description of a key state defined for a numeric character.
	 * @param keyChar the character as defined by the KEY STATES directive.
	 * @param state details of the redefined state to describe.
	 * @param separator the separator to use.
	 * @return a description of the supplied state of the supplied character.
	 */
	private String translateNumericState(IdentificationKeyCharacter keyChar, NumericKeyState state) {

		BigDecimal min = state.min();
		BigDecimal max = state.max();
		List<String> states = new ArrayList<String>();

		String separator = " "+Words.word(Word.TO)+ " ";
		Values values = new Values(states, separator);
		
		if (keyChar.getCharacter().getCharacterType().isNumeric()) {
			
			if (min.equals(MIN_VALUE)) {
				values.setPrefix(Words.word(Word.UP_TO));
				states.add(max.toPlainString());
			}
			else if (max.equals(MAX_VALUE)) {
				values.setSuffix(Words.word(Word.OR_MORE));
				states.add(min.toPlainString());
			}
			else {
				states.add(min.toPlainString());
				if (min.compareTo(max) != 0) {
					states.add(max.toPlainString());
				}
			}
		} 

		AttributeTranslator at = _attributeTranslatorFactory.translatorFor(keyChar.getCharacter());
		

		StringBuffer result = new StringBuffer();
		if (StringUtils.isNotBlank((values.getPrefix()))) {
			result.append(values.getPrefix()).append(" ");
		}


		result.append( at.translateValues(values));
		
		if (StringUtils.isNotBlank(values.getSuffix())) {
			result.append(" ").append(values.getSuffix());
		}
		return result.toString();
	}


}

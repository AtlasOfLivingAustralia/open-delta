package au.org.ala.delta.translation.parameter;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.NumericAttribute;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.FilteredItem;
import au.org.ala.delta.translation.PrintFile;

/**
 * Writes attribute data in Paup / Henning format.
 */
public class PaupHenningAttributes extends ParameterTranslator {

	private int _outputColumns;
	private int _itemLength;
	private FilteredDataSet _dataSet;
	private DeltaContext _context;
	private ItemFormatter _itemFormatter;
	private StateEncoder _encoder;

	public PaupHenningAttributes(
			PrintFile outputFile, DeltaContext context,
			FilteredDataSet dataSet, ItemFormatter itemFormatter,
			int outputColumns, int itemLength) {
		super(outputFile);
		_outputColumns = outputColumns;
		_itemLength = itemLength;
		_dataSet = dataSet;
		_context = context;
		_itemFormatter = itemFormatter;
		_encoder = new StateEncoder(_context.getNumberStatesFromZero());
	}

	@Override
	public void translateParameter(OutputParameter parameter) {
		writeAttributes(nameOnNewLine());
	}
	
	protected boolean nameOnNewLine() {
		int numChars = _dataSet.getNumberOfFilteredCharacters();
		return (numChars > _outputColumns - (_itemLength+1));
	}
	
	public void writeItem(Item item) {
		writeItemName(nameOnNewLine(), item);

		Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
		StringBuilder statesOut = new StringBuilder();
		while (characters.hasNext()) {
			IdentificationKeyCharacter character = characters.next();
			Attribute attribute = item.getAttribute(character.getCharacter());
			
			if (isInapplicable(attribute)) {
				statesOut.append("?");
			} else {
				if (character.getCharacterType() == CharacterType.OrderedMultiState) {
					statesOut.append(toSingleValue(character, (MultiStateAttribute) attribute));
				} else if (attribute instanceof NumericAttribute) {
					statesOut.append(toSingleValue(character, (NumericAttribute) attribute));
				} else if (character.getCharacterType() == CharacterType.UnorderedMultiState) {
					statesOut.append(unorderedToSingleValue(character, (MultiStateAttribute) attribute));
				}

			}
		}
		_outputFile.writeJustifiedText(pad(statesOut.toString()), -1);
	}

	protected void writeAttributes(boolean nameOnNewLine) {

		Iterator<FilteredItem> items = _dataSet.filteredItems();
		while (items.hasNext()) {
			Item item = items.next().getItem();
			writeItem(item);
		}
	}

	protected void writeItemName(boolean nameOnNewLine, Item item) {
		String itemName = truncate(_itemFormatter.formatItemDescription(item), _itemLength);
		writeItemName(nameOnNewLine, itemName);
	}
	
	protected void writeItemName(boolean nameOnNewLine, String itemName) {
		if (nameOnNewLine) {
			itemName = pad(itemName);
		} else {
			itemName += " ";
		}
		_outputFile.writeJustifiedText(itemName, -1);
	}

	private boolean isInapplicable(Attribute attribute) {
		if (!attribute.isExclusivelyInapplicable(true)) {
			ControllingInfo controllingInfo = _dataSet
					.checkApplicability(attribute.getCharacter(), attribute.getItem());
			return (controllingInfo.isInapplicable());
		}
		return true;
	}

	private String toSingleValue(IdentificationKeyCharacter character, MultiStateAttribute attribute) {
		List<Integer> states = character.getPresentStates(attribute);
		return getSingleValue(character, states);
	}

	private String toSingleValue(IdentificationKeyCharacter character, NumericAttribute attribute) {
		character.setUseNormalValues(true);
		List<Integer> states = character.getPresentStates(attribute);
		return getSingleValue(character, states);
	}

	protected String getSingleValue(IdentificationKeyCharacter character, List<Integer> states) {
		if (!_context.getUseMeanValues()
				&& (states.size() == character.getNumberOfStates() || (states.size() > 1 && _context
						.getTreatVariableAsUnknown()))) {
			return "?";
		}
		double sum = 0;
		for (int state : states) {
			sum += state;
		}
		double average = sum / states.size();
		// 0.5 is rounded down, hence the strange rounding behavior below.
		int value = (int) Math.floor(average + 0.499d);
		if (value <= 0) {
			return "?";
		}
		return _encoder.encodeState(value);
	}

	private String unorderedToSingleValue(IdentificationKeyCharacter character, MultiStateAttribute attribute) {
		Set<Integer> states = attribute.getPresentStates();
		if (states.size() == character.getNumberOfStates()
				|| (states.size() > 1 && _context.getTreatVariableAsUnknown())) {
			return "?";
		}

		int state = -1;
		if (_context.getUseLastValueCoded()) {
			state = attribute.getLastStateCoded();
		} else {
			state = attribute.getFirstStateCoded();
		}

		state = character.convertToKeyState(state);
		if (state <= 0) {
			return "?";
		}
		return _encoder.encodeState(state);
	}

	private String pad(String value) {
		StringBuilder paddedValue = new StringBuilder(value);
		while (paddedValue.length() < _outputColumns) {
			paddedValue.append(' ');
		}
		return paddedValue.toString();
	}

	protected String truncate(String value, int maxLength) {
		if (value.length() < maxLength) {
			return value;
		} else {
			value = value.substring(0, maxLength);
			return value.trim();
		}
	}
}

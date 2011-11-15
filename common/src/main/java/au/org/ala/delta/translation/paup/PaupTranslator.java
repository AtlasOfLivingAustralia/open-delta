package au.org.ala.delta.translation.paup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.NumericAttribute;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.FilteredItem;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.delta.DeltaWriter;
import au.org.ala.delta.translation.parameter.Command;
import au.org.ala.delta.translation.parameter.ParameterBasedTranslator;
import au.org.ala.delta.translation.parameter.ParameterTranslator;
import au.org.ala.delta.translation.parameter.Specifications;
import au.org.ala.delta.translation.parameter.Symbols;

/**
 * Implements the translation into Nexus format as specified using the TRANSLATE
 * INTO PAUP FORMAT directive.
 * 
 * KEY WORDS REQUIRED FOR PAUP SPECIFICATIONS.
 *     1 - COMMENT. 2 - PARAMETERS. 3 - SYMBOLS.
 *     4 - UNORDERED. 5 - DATA. 6 - GO. 7 - WEIGHTS. 8 - END.
 *
 * 
 * PROCEDURE FOR DETERMINING PAUP VALUES (SAME AS FOR HENNIG).
 * 1. NUMERIC CHARACTERS WITHOUT KEY STATES ARE EXCLUDED.
 * 2. ONLY `NORMAL' VALUES OF NUMERIC CHARACTERS ARE USED. EXTREME VALUES
 *    ARE IGNORED.
 * 3. KEY STATES ARE APPLIED. ALL NUMERIC CHARACTERS ARE SUBSEQUENTLY TREATED
 *    AS ORDERED MULTISTATE.
 * 4. IF A `USE MEAN VALUES' DIRECTIVE IS IN FORCE, MULTIPLE VALUES OF ORDERED
 *    MULTISTATE CHARACTERS (INCLUDING FORMER NUMERIC CHARACTERS) ARE REPLACED
 *    BY THEIR MEAN.
 * 5. IF ONLY ONE STATE VALUE IS PRESENT, THAT VALUE IS OUTPUT.
 * 6. IF ALL POSSIBLE STATE VALUES ARE PRESENT, OR IF MORE THAN ONE VALUE IS
 *    PRESENT AND `TREAT VARIABLE AS UNKNOWN' HAS BEEN SPECIFIED, THEN ?
 *    IS OUTPUT.
 *    OTHERWISE -
 * 7. FOR ORDERED MULTISTATE CHARACTERS (INCLUDING FORMER NUMERICS)
 *    A SINGLE VALUE IS OBTAINED AS IN STEP 4.
 * 8. FOR UNORDERED MULTISTATES, A SINGLE VALUE IS OBTAINED FROM THE ORIGINAL
 *    DATA (BEFORE THE APPLICATION OF KEY STATES) BY SELECTING THE FIRST VALUE
 *    CODED, UNLESS `USE LAST VALUE CODED' HAS BEEN SPECIFIED, WHEN THE LAST
 *    VALUE IS SELECTED. KEY STATES ARE THEN APPLIED IF SPECIFIED.
 * 9. THE VALUE IS OUTPUT.

 */
public class PaupTranslator extends ParameterBasedTranslator {

	private enum PARAMETER {
		COMMENT("#COMMENT"), PARAMETERS("#PARAMETERS"), SYMBOLS("#SYMBOLS"), UNORDERED("#UNORDERED"), DATA("#DATA"), 
		GO("#GO"), WEIGHTS("#WEIGHTS"), END("#END");

		private String _name;

		private PARAMETER(String name) {
			_name = name;
		}
		
		public String getName() {
			return _name;
		}

	};


	private static final int ITEM_NAME_LENGTH = 8;
	private static final int OUTPUT_COLUMNS = 80;
	
	private static final String[] STATE_CODES = {
		"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", 
		"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", 
		"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", 
		"U", "V", "W", "X", "Y", "Z"};
	
	private DeltaContext _context;
	private PrintFile _outputFile;
	private FilteredDataSet _dataSet;
	private ItemFormatter _itemFormatter;
	
	public PaupTranslator(DeltaContext context, FilteredDataSet dataSet, 
			PrintFile outputFile, 
			CharacterFormatter characterFormatter, ItemFormatter itemFormatter) {
		_context = context;
		_dataSet = dataSet;
		_outputFile = outputFile;
		if (_outputFile != null) {
			_outputFile.setWrapingGroupChars('\'', '\'');
			_outputFile.setLineWrapIndent(0);
			_outputFile.setIndent(0);
			_outputFile.setTrimInput(false);
			_outputFile.setPrintWidth(OUTPUT_COLUMNS);
		}
		_itemFormatter = itemFormatter;
		addParameters();
	}

	@Override
	protected void unrecognisedParameter(String parameter) {
		if (!parameter.startsWith("#")) {
			_outputFile.outputLine(parameter);
		}
		else {
			throw new IllegalArgumentException("Unsupported parameter: " + parameter);
		}
	}

	/**
	 * The output parameters accepted by a Paup translation are:
	 * <ul>
	 * <li>COMMENT</li>
	 * <li>PARAMETERS</li>
	 * <li>SYMBOLS</li>
	 * <li>UNORDERED</li>
	 * <li>DATA</li>
	 * <li>GO</li>
	 * <li>WEIGHTS</li>
	 * <li>END</li>
	 * </ul>
	 */
	public void addParameters() {
		ParameterTranslator translator = null;
		for (PARAMETER param : PARAMETER.values()) {
			switch (param) {
			case COMMENT:
				translator = new Comment(_outputFile);
				break;
			case PARAMETERS:
				translator = new Specifications(_outputFile, _dataSet, "PARAMETERS", "NOTU", "NCHAR", 0);
				break;
			case SYMBOLS:
				translator = new PaupSymbols(_outputFile, _context.getNumberStatesFromZero());
				break;
			case UNORDERED:
				translator = new Unordered(_outputFile);
				break;
			case DATA:
				translator = new Data(_outputFile);
				break;
			case GO:
				translator = new Command(_outputFile, "GO ");
				break;
			case WEIGHTS:
				translator = new Weights(_outputFile);
				break;
			case END:
				translator = new Command(_outputFile, "END ");
				break;
			}
		
			addSupportedParameter(param.getName(), translator);
		}

	}
	
	class Comment extends ParameterTranslator {
		public Comment(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			_outputFile.outputLine("!"+_context.getHeading(HeadingType.HEADING));
		}
	}

	class PaupSymbols extends Symbols {
		public PaupSymbols(PrintFile outputFile, boolean numberFromZero) {
			super(outputFile, _dataSet, false, numberFromZero);
		}

		@Override
		protected void writeSymbols(StringBuilder symbols, int first, int last) {
			symbols.append(STATE_CODES[first]).append("-");
			if (last > 10 - first) {
				symbols.append(symbols.append(STATE_CODES[10]));
				symbols.append(" ");
				symbols.append(STATE_CODES[11]);
				if (last > 11 - first) {
					symbols.append("-");	
				}
			}
			symbols.append(STATE_CODES[last - first]);
		}
	}
	
	/**
	 * Writes the DATA command (which includes the attribute data).
	 */
	class Data extends ParameterTranslator {
		public Data(PrintFile outputFile) {
			super(outputFile);
		}
		
		@Override
		public void translateParameter(OutputParameter parameter) {
			boolean nameOnNewLine = writeDataSpecification();
			writeAttributes(nameOnNewLine);
		}

		protected boolean writeDataSpecification() {
			StringBuilder data = new StringBuilder();
			// the output width seems to be ignored by the PAUP translation.
			data.append("DATA ");
			int numChars = _dataSet.getNumberOfFilteredCharacters();
			boolean itemNameOnNewLine = (numChars > OUTPUT_COLUMNS - (ITEM_NAME_LENGTH+1));
			if (itemNameOnNewLine) {
				data.append(String.format("(A%d,A1/(%dA1));", ITEM_NAME_LENGTH, Math.max(numChars, OUTPUT_COLUMNS)));
			}
			else {
				data.append(String.format("(A%d,A1,%dA1);", ITEM_NAME_LENGTH, numChars));
			}
			_outputFile.outputLine(data.toString());
			return itemNameOnNewLine;
		}
		
		protected void writeAttributes(boolean nameOnNewLine) {
			
			Iterator<FilteredItem> items = _dataSet.filteredItems();
			while (items.hasNext()) {
				Item item = items.next().getItem();
				writeItemName(nameOnNewLine, item);
				
				Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
				StringBuilder statesOut = new StringBuilder();
				while (characters.hasNext()) {
					IdentificationKeyCharacter character = characters.next();
					Attribute attribute = item.getAttribute(character.getCharacter());
					if (item.getItemNumber() == 13 && character.getCharacterNumber() == 32) {
						System.out.println("Breakpoint");
					}
					if (isInapplicable(attribute)) {
						statesOut.append("?");
					}
					else {
						if (character.getCharacterType() == CharacterType.OrderedMultiState) {
							statesOut.append(toSingleValue(character, (MultiStateAttribute)attribute));
						}
						else if (attribute instanceof NumericAttribute) {
							statesOut.append(toSingleValue(character, (NumericAttribute)attribute));
						}
						else if (character.getCharacterType() == CharacterType.UnorderedMultiState) {
							statesOut.append(unorderedToSingleValue(character, (MultiStateAttribute)attribute));
						}
						
					}
				}
				_outputFile.writeJustifiedText(pad(statesOut.toString()), -1);
			}
		}

		protected void writeItemName(boolean nameOnNewLine, Item item) {
			String itemName = truncate(_itemFormatter.formatItemDescription(item), ITEM_NAME_LENGTH);
			if (nameOnNewLine) {
				itemName = pad(itemName);
			}
			else {
				itemName += " ";
			}
			_outputFile.writeJustifiedText(itemName, -1);
		}
		
		private boolean isInapplicable(Attribute attribute) {
			if (!attribute.isExclusivelyInapplicable(true)) {
				ControllingInfo controllingInfo = _dataSet.checkApplicability(
						attribute.getCharacter(), attribute.getItem());
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
			if (!_context.getUseMeanValues() && 
				(states.size() == character.getNumberOfStates() || 
			    (states.size() > 1 && _context.getTreatVariableAsUnknown()))) {
				return "?";
			}
			double sum = 0;
			for (int state : states) {
				sum += state;
			}
			double average = sum / states.size();
			// 0.5 is rounded down, hence the strange rounding behavior below.
			int value = (int)Math.floor(average + 0.499d);
			if (value <= 0) {
				return "?";
			}
			return Integer.toString(value);
		}
		
		private String unorderedToSingleValue(IdentificationKeyCharacter character, MultiStateAttribute attribute) {
			Set<Integer> states = attribute.getPresentStates();
			if (states.size() == character.getNumberOfStates() || 
		    (states.size() > 1 && _context.getTreatVariableAsUnknown())) {
				return "?";
			}
			
			int state = -1;
			if (_context.getUseLastValueCoded()) {
				state = attribute.getLastStateCoded();
			}
			else {
				state = attribute.getFirstStateCoded();
			}
			
			state = character.convertToKeyState(state);
			if (state <= 0) {
				return "?";
			}
			return Integer.toString(state);
		}
		
		private String pad(String value) {
			StringBuilder paddedValue = new StringBuilder(value);
			while (paddedValue.length() < OUTPUT_COLUMNS) {
				paddedValue.append(' ');
			}
			return paddedValue.toString();
		}
	}
	
	class Weights extends ParameterTranslator {
		
		public Weights(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			
			
			StringBuilder weightsOut = new StringBuilder();
			weightsOut.append("WEIGHTS");
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			int count = 0;
			BigDecimal weight = new BigDecimal(-1);
			while (characters.hasNext()) {
				IdentificationKeyCharacter character = characters.next();
				BigDecimal tmpWeight = _context.getCharacterWeightAsBigDecimal(character.getCharacterNumber());
				
				if (!tmpWeight.equals(weight)) {
					outputWeight(weightsOut, count, weight);
					count = 0;
				}
				weight = tmpWeight;
				count++;
				
			}
			outputWeight(weightsOut, count, weight);
			command(weightsOut.toString());
		}
		protected void outputWeight(StringBuilder weightsOut, int count, BigDecimal weight) {
			if (count == 1) {
				weightsOut.append(" ").append(weight);
			}
			else if (count > 1) {
				weightsOut.append(" ").append(count).append("*").append(weight.toPlainString());
			}
		}
	}
	
	class Unordered extends ParameterTranslator {
		
		public Unordered(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			List<Integer> unorderedMultiStateChars = new ArrayList<Integer>();
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			while (characters.hasNext()) {
				IdentificationKeyCharacter character = characters.next();
				CharacterType type = character.getCharacterType();
				if (type == CharacterType.UnorderedMultiState) {
					if (character.getNumberOfStates() > 2) {
						unorderedMultiStateChars.add(character.getFilteredCharacterNumber());
					}
				}
			}
			StringBuilder out = new StringBuilder();
			DeltaWriter writer = new DeltaWriter();
			
			out.append("UNORDERED  "); 
			
			out.append(writer.rangeToString(unorderedMultiStateChars));
			
			command(out.toString());
		}
	}

}

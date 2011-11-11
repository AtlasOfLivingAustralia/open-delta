package au.org.ala.delta.translation.paup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	private CharacterFormatter _characterFormatter;
	private ItemFormatter _itemFormatter;
	
	public PaupTranslator(DeltaContext context, FilteredDataSet dataSet, 
			PrintFile outputFile, 
			CharacterFormatter characterFormatter, ItemFormatter itemFormatter) {
		_context = context;
		_dataSet = dataSet;
		_outputFile = outputFile;
		if (_outputFile != null) {
			_outputFile.setWrapingGroupChars('\'', '\'');
			_outputFile.setLineWrapIndent(5);
		}
		_characterFormatter = characterFormatter;
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
				translator = new Specifications(_outputFile, _dataSet, "PARAMETERS", "NOTU", "NCHAR");
				break;
			case SYMBOLS:
				translator = new PaupSymbols(_outputFile, _context.getNumberStatesFromZero());
				break;
			case UNORDERED:
				translator = new Comment(_outputFile);
				break;
			case DATA:
				translator = new Command(_outputFile, "END ");
				break;
			case GO:
				translator = new Command(_outputFile, "GO ");
				break;
			case WEIGHTS:
				translator = new Comment(_outputFile);
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
			writeDataSpecification();
		}

		protected void writeDataSpecification() {
			StringBuilder data = new StringBuilder();
			// the output width seems to be ignored by the PAUP translation.
			data.append("DATA ");
			int numChars = _dataSet.getNumberOfFilteredCharacters();
			boolean itemNameOnNewLine = (numChars > OUTPUT_COLUMNS - (ITEM_NAME_LENGTH+1));
			if (itemNameOnNewLine) {
				data.append(String.format("(A8,A1/(%dA1))", numChars));
			}
			else {
				data.append(String.format("(A8,A1,%dA1)", numChars));
			}
			_outputFile.outputLine(data.toString());
		}
		
		protected void writeAttributes() {
			
			Iterator<FilteredItem> items = _dataSet.filteredItems();
			while (items.hasNext()) {
				Item item = items.next().getItem();
				Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
				StringBuilder statesOut = new StringBuilder();
				writeItem(item);
				while (characters.hasNext()) {
					IdentificationKeyCharacter character = characters.next();
					Attribute attribute = item.getAttribute(character.getCharacter());
					if (isInapplicable(attribute)) {
						statesOut.append("-");
					}
					else {
						List<Integer> states = new ArrayList<Integer>();
						
						if (attribute instanceof MultiStateAttribute) {
						    states.addAll(character.getPresentStates((MultiStateAttribute)attribute));
						}
						else if (attribute instanceof NumericAttribute) {
							states.addAll(character.getPresentStates((NumericAttribute)attribute));
						}
						
						addStates(statesOut, states);
					}
				}
				_outputFile.outputLine(statesOut.toString());
			}
			_outputFile.outputLine(";");
			_outputFile.writeBlankLines(1, 0);
		}
		
		private boolean isInapplicable(Attribute attribute) {
			if (!attribute.isExclusivelyInapplicable(true)) {
				ControllingInfo controllingInfo = _dataSet.checkApplicability(
						attribute.getCharacter(), attribute.getItem());
				return (controllingInfo.isInapplicable());
			}
			return true;
		}
		
		private String writeItem(Item item) {
			String itemName = _itemFormatter.formatItemDescription(item);
			if (itemName.length() > ITEM_NAME_LENGTH) {
				itemName = itemName.substring(0, ITEM_NAME_LENGTH);
			}
			return itemName;
		}
		
		private void addStates(StringBuilder statesOut, List<Integer> states) {
			if (states.size() == 0) {
				statesOut.append("?");
			}
			else if (states.size() > 1) {
				statesOut.append("(");
			}
			int offset = _context.getNumberStatesFromZero() ? 1 : 0;
			for (int state : states) {
				statesOut.append(state - offset);
			}
			if (states.size() > 1) {
				statesOut.append(")");
			}
		}
	}
	
	class WtSet extends ParameterTranslator {
		
		public WtSet(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			Map<BigDecimal, List<Integer>> weights = new TreeMap<BigDecimal, List<Integer>>();
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			while (characters.hasNext()) {
				IdentificationKeyCharacter character = characters.next();
				BigDecimal weight = _context.getCharacterWeightAsBigDecimal(character.getCharacterNumber());
				if (!weights.containsKey(weight)) {
					List<Integer> chars = new ArrayList<Integer>();
					weights.put(weight, chars);
				}
				weights.get(weight).add(character.getFilteredCharacterNumber());
			}
			
			StringBuilder weightsOut = new StringBuilder();
			DeltaWriter writer = new DeltaWriter();
			weightsOut.append("WTSET * untitled =");
			for (BigDecimal weight : weights.keySet()) {
				weightsOut.append(" ");
				weightsOut.append(weight.toPlainString());
				weightsOut.append(": ");
				weightsOut.append(writer.rangeToString(weights.get(weight)));
			}
			command(weightsOut.toString());
			_outputFile.writeBlankLines(1, 0);
		}
	}

	class TypeSet extends ParameterTranslator {
		
		public TypeSet(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(OutputParameter parameter) {
			List<Integer> unorderedMultiStateChars = new ArrayList<Integer>();
			List<Integer> orderedMultiStateChars = new ArrayList<Integer>();
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			while (characters.hasNext()) {
				IdentificationKeyCharacter character = characters.next();
				CharacterType type = character.getCharacterType();
				if (type == CharacterType.UnorderedMultiState) {
					unorderedMultiStateChars.add(character.getFilteredCharacterNumber());
				}
				else {
					orderedMultiStateChars.add(character.getFilteredCharacterNumber());
				}
				
			}
			StringBuilder out = new StringBuilder();
			DeltaWriter writer = new DeltaWriter();
			
			out.append("TYPESET * untitled = "); 
			if (!unorderedMultiStateChars.isEmpty()) {
				out.append("unord: ");
				out.append(writer.rangeToString(unorderedMultiStateChars));
				if (!orderedMultiStateChars.isEmpty()) {
					out.append(", ");
				}
			}
			if (!orderedMultiStateChars.isEmpty()) {
				out.append("ord: ");
				out.append(writer.rangeToString(orderedMultiStateChars));
			}
			command(out.toString());
			_outputFile.writeBlankLines(1, 0);
			
			
			
		}
	}

}

package au.org.ala.delta.translation.paup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericAttribute;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.FilteredCharacterFormatter;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.FilteredItem;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.delta.DeltaWriter;
import au.org.ala.delta.translation.key.KeyStateTranslator;
import au.org.ala.delta.translation.parameter.ParameterBasedTranslator;
import au.org.ala.delta.translation.parameter.ParameterTranslator;
import au.org.ala.delta.translation.parameter.Specifications;

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
			_name = name.substring(1, 3).toUpperCase();
		}
		
		public String getName() {
			return _name;
		}

	};


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
	private KeyStateTranslator _keyStateTranslator;

	public PaupTranslator(DeltaContext context, FilteredDataSet dataSet, 
			PrintFile outputFile, KeyStateTranslator keyStateTranslator,
			CharacterFormatter characterFormatter, ItemFormatter itemFormatter) {
		_context = context;
		_dataSet = dataSet;
		_outputFile = outputFile;
		_outputFile.setWrapingGroupChars('\'', '\'');
		_outputFile.setLineWrapIndent(5);
		
		_characterFormatter = characterFormatter;
		_itemFormatter = itemFormatter;
		_keyStateTranslator = keyStateTranslator;
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
				translator = new Command(_outputFile, "BEGIN DATA");
				break;
			case UNORDERED:
				translator = new Comment(_outputFile);
				break;
			case DATA:
				translator = new Command(_outputFile, "END");
				break;
			case GO:
				translator = new Format(_outputFile);
				break;
			case WEIGHTS:
				translator = new Comment(_outputFile);
				break;
			case END:
				translator = new StateLabels(_outputFile);
				break;
			}
		
			addSupportedParameter(param.getName(), translator);
		}

	}

	
	private static final int MAX_LENGTH = 30;
	private String truncate(String value) {
		if (value.length() < MAX_LENGTH) {
			return value;
		}
		else {
			value = value.substring(0, MAX_LENGTH);
			return value.trim();
		}
	}
	
	class Comment extends ParameterTranslator {
		public Comment(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(String parameter) {
			_outputFile.outputLine("!"+_context.getHeading(HeadingType.HEADING));
		}
	}
	
	class CharLabels extends ParameterTranslator {
		
		public CharLabels(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(String parameter) {

			_outputFile.outputLine("CHARLABELS");
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			while(characters.hasNext()) {
				outputCharacter(characters.next());
			}
			_outputFile.outputLine(";");
			_outputFile.writeBlankLines(1, 0);
		}
		
		private void outputCharacter(IdentificationKeyCharacter character) {
			FilteredCharacterFormatter _formatter = new FilteredCharacterFormatter();
			String description = _characterFormatter.formatCharacterDescription(character.getCharacter());
			StringBuilder charOut = new StringBuilder();
			charOut.append(comment(_formatter.formatCharacterNumber(character)));
			charOut.append(" ");
			charOut.append("'").append(truncate(description)).append("'");
			_outputFile.outputLine(charOut.toString());
			
		}
	}

	
	class Literal extends ParameterTranslator {
		private String _value;
		private int _trailingLines;
		
		public Literal(PrintFile outputFile, String value, int trailingLines) {
			super(outputFile);
			_value = value;
			_trailingLines = trailingLines;
		}
		
		@Override
		public void translateParameter(String parameter) {
			_outputFile.outputLine(_value);
			_outputFile.writeBlankLines(_trailingLines, 0);
		}
	}

	class Command extends ParameterTranslator {
		private String _value;
		private int _trailingLines;
		public Command(PrintFile outputFile, String value) {
			this(outputFile, value, 0);
		}
		public Command(PrintFile outputFile, String value, int trailingBlankLines) {
			super(outputFile);
			_value = value;
			_trailingLines = trailingBlankLines;
		}
		@Override
		public void translateParameter(String parameter) {
			command(_value);
			_outputFile.writeBlankLines(_trailingLines, 0);
		}
	}

	class Format extends ParameterTranslator {
		
		public Format(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(String parameter) {
			StringBuilder format = new StringBuilder();
			format.append("FORMAT MISSING=? GAP=- SYMBOLS=");
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			int max = 0;
			while (characters.hasNext()) {
				max = Math.max(max, characters.next().getNumberOfStates());
			}
			
			format.append("\"");
			int first = _context.getNumberStatesFromZero() ? 0 : 1;
			for (int i=first; i<max+first; i++) {
				format.append(STATE_CODES[i]);
			}
			format.append("\"");
			
			command(format.toString());
			_outputFile.writeBlankLines(1, 0);
		}
	}

	class StateLabels extends ParameterTranslator {
		public StateLabels(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(String parameter) {
			_outputFile.setIndentOnLineWrap(true);
			
			_outputFile.outputLine("STATELABELS");
			Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
			while(characters.hasNext()) {
				outputCharacterStates(characters.next());
			}
			_outputFile.outputLine(";");
			_outputFile.writeBlankLines(1, 0);
			
			_outputFile.setIndentOnLineWrap(false);
			
		}
		
		private void outputCharacterStates(IdentificationKeyCharacter character) {
			StringBuilder states = new StringBuilder();
			states.append(character.getFilteredCharacterNumber());
			states.append(" ");
			boolean hasKeyStates = !character.getStates().isEmpty();
			for (int i=1; i<=character.getNumberOfStates(); i++) {
				states.append("'");
				String state = null;
				if (hasKeyStates) {
					state = _keyStateTranslator.translateState(character, i);
				}
				else {
					MultiStateCharacter multiStateChar = (MultiStateCharacter)character.getCharacter();
					state = _characterFormatter.formatState(multiStateChar, i, CommentStrippingMode.STRIP_ALL);
				}
				states.append(truncate(state));
				states.append("'");
				if (i != character.getNumberOfStates()) {
					states.append(" ");
				}
			}
			states.append(",");
			_outputFile.outputLine(states.toString());
		}
	}

	class Matrix extends ParameterTranslator {
		public Matrix(PrintFile outputFile) {
			super(outputFile);
		}
		@Override
		public void translateParameter(String parameter) {
			_outputFile.outputLine("MATRIX");
			_outputFile.setWrapingGroupChars('(', ')');
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
		
		private void writeItem(Item item) {
			StringBuilder itemOut = new StringBuilder();
			itemOut.append("'");
			itemOut.append(_itemFormatter.formatItemDescription(item));
			itemOut.append("'");
			_outputFile.outputLine(itemOut.toString());
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
		public void translateParameter(String parameter) {
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
		public void translateParameter(String parameter) {
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

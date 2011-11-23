package au.org.ala.delta.translation.henning86;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.AbstractIterativeTranslator;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.delta.DeltaWriter;
import au.org.ala.delta.translation.parameter.PaupHenningAttributes;
import au.org.ala.delta.util.Pair;

/**
 * Implements the translation into Nexus format as specified using the TRANSLATE
 * INTO PAUP FORMAT directive.
 * 
 * KEY WORDS REQUIRED FOR PAUP SPECIFICATIONS.
 *     1 - COMMENT. 2 - PARAMETERS. 3 - SYMBOLS.
 *     4 - UNORDERED. 5 - DATA. 6 - GO. 7 - WEIGHTS. 8 - END.
 *
 * 
 * PROCEDURE FOR DETERMINING HENNING VALUES (SAME AS FOR PAUP).
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
public class Henning86Translator extends AbstractIterativeTranslator {

	private static final int ITEM_NAME_LENGTH = 10;
	private static final int OUTPUT_COLUMNS = 80;
	
	private DeltaContext _context;
	private PrintFile _outputFile;
	private FilteredDataSet _dataSet;
	private PaupHenningAttributes _attributeWriter;
	private ItemFormatter _itemFormatter;
	
	public Henning86Translator(DeltaContext context, FilteredDataSet dataSet, 
			PrintFile outputFile, 
			CharacterFormatter characterFormatter, ItemFormatter itemFormatter) {
		_context = context;
		_dataSet = dataSet;
		_outputFile = outputFile;
		if (_outputFile != null) {
			_outputFile.setWrapingGroupChars('\'', '\'');
			_outputFile.setLineWrapIndent(0);
			_outputFile.setIndent(0);
			_outputFile.setTrimInput(false, true);
			_outputFile.setPrintWidth(OUTPUT_COLUMNS);
		}
		_itemFormatter = itemFormatter;
		_attributeWriter = new Henning86Attributes(outputFile, context, dataSet, itemFormatter, OUTPUT_COLUMNS, ITEM_NAME_LENGTH);
	}

	@Override
	public void beforeFirstItem() {
		_outputFile.outputLine("xread");
		writeHeading();
		writeSpecs();
	}

	protected void writeHeading() {
		String heading = _context.getHeading(HeadingType.HEADING);
		if (StringUtils.isNotBlank(heading)) {
			if (heading.startsWith("'")) {
				heading = heading.substring(1);
			}
			if (heading.endsWith("'")) {
				heading = heading.substring(0, heading.length()-1);
			}
			_outputFile.outputLine("'"+heading+"'");
		}
	}

	protected void writeSpecs() {
		int numChars = _dataSet.getNumberOfFilteredCharacters();
		int numItems = _dataSet.getNumberOfFilteredItems();
		
		StringBuilder specs = new StringBuilder();
		specs.append(numChars).append(" ").append(numItems);
		_outputFile.outputLine(specs.toString());
	}
	
	@Override
	public void beforeItem(Item item) {
		_attributeWriter.writeItem(item);
	}

	@Override
	public void afterLastItem() {
		_outputFile.printBufferLine();
		_outputFile.outputLine(";");
		
		writeReliabilities();
		writeCharacterTypes();
		_outputFile.outputLine("proc / ;");
	}
	
	protected void writeReliabilities() {
		StringBuilder weightsOut = new StringBuilder();
		weightsOut.append("ccode [");
		
		List<Pair<Integer, String>> reliabilities = new ArrayList<Pair<Integer,String>>();
		Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
		DecimalFormat format = new DecimalFormat("00");
		
		while (characters.hasNext()) {
			IdentificationKeyCharacter character = characters.next();
			double tmpReliability = _context.getCharacterReliability(character.getCharacterNumber());
			String reliabilityStr = "/"+format.format(tmpReliability*10);
			reliabilities.add(new Pair<Integer, String>(character.getFilteredCharacterNumber()-1, reliabilityStr));
		}
		
		DeltaWriter writer = new DeltaWriter();
		weightsOut.append(writer.valueRangeToString(reliabilities, '.', " ", false));
		weightsOut.append(";");
		_outputFile.outputLine(weightsOut.toString());
	}
	
	protected void writeCharacterTypes() {
		List<Pair<Integer, String>> types = new ArrayList<Pair<Integer,String>>();
		Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
		while (characters.hasNext()) {
			IdentificationKeyCharacter character = characters.next();
			CharacterType type = character.getCharacterType();
			String value = "+";
			if (type == CharacterType.UnorderedMultiState) {
				value = "-";
			}
			types.add(new Pair<Integer, String>(character.getFilteredCharacterNumber()-1, value));
		}
		
		
		StringBuilder out = new StringBuilder();
		out.append("ccode ");
		DeltaWriter writer = new DeltaWriter();
		out.append(writer.valueRangeToString(types, '.', "", false));
		out.append(";");
		_outputFile.setLineWrapIndent(1);
		_outputFile.setIndentOnLineWrap(true);
		_outputFile.outputLine(out.toString());
	}
	
	class Henning86Attributes extends PaupHenningAttributes {

		public Henning86Attributes(PrintFile outputFile, DeltaContext context, FilteredDataSet dataSet,
				ItemFormatter itemFormatter, int outputColumns, int itemLength) {
			super(outputFile, context, dataSet, itemFormatter, outputColumns, itemLength);
		}

		@Override
		protected void writeItemName(boolean nameOnNewLine, Item item) {
			String itemName = _itemFormatter.formatItemDescription(item);
			StringBuilder tmpName = new StringBuilder();
			for (int i=0; i<itemName.length(); i++) {
				char letter = itemName.charAt(i);
				if (Character.isLetterOrDigit(letter)) {
					tmpName.append(letter);
				}
				else if (Character.isWhitespace(letter)) {
					tmpName.append("_");
				}
			}
			super.writeItemName(true, tmpName.toString());
		}
		
		
		
	}
	

}

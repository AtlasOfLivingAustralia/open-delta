package au.org.ala.delta.translation.print;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.AbstractIterativeTranslator;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.Words;
import au.org.ala.delta.translation.Words.Word;
import au.org.ala.delta.translation.delta.DeltaWriter;

/**
 * Writes the character list to the print file.
 */
public class UncodedCharactersPrinter extends AbstractIterativeTranslator {
	
	private ItemListTypeSetter _typeSetter;
	private PrintFile _printFile;
	private ItemFormatter _itemFormatter;
	private DeltaContext _context;
	protected List<Character> _uncodedChars;
	private DeltaWriter _deltaWriter;
	private boolean _omitItemDescription;
	
	public UncodedCharactersPrinter(
			DeltaContext context, 
			PrintFile printFile, 
			ItemFormatter itemFormatter,
			ItemListTypeSetter typeSetter,
			boolean omitItemDescription) {
		_typeSetter = typeSetter;
		_printFile = printFile;
		_itemFormatter = itemFormatter;
		_deltaWriter = new DeltaWriter();
		_context = context;
		_omitItemDescription = omitItemDescription;
	}
	
	@Override
	public void beforeFirstItem() {}
	
	@Override
	public void beforeItem(Item item) {
		if (!_omitItemDescription) {
			_typeSetter.beforeItem(item);
			_printFile.outputLine(_itemFormatter.formatItemDescription(item));
			_typeSetter.afterItemName();
		}
		_uncodedChars = new ArrayList<Character>();
	}

	
	@Override
	public void beforeAttribute(Attribute attribute) {
		Character character = attribute.getCharacter();
		Item item = attribute.getItem();
		if (_context.getDataSet().isUncoded(item, character)) {
			_uncodedChars.add(attribute.getCharacter());
		}
	}
	
	@Override
	public void afterItem(Item item) {
		StringBuilder uncoded = new StringBuilder();
		uncoded.append(Words.word(Word.NOT_CODED)).append(":");
		
		appendUncodedCharacters(uncoded);
		
		_printFile.setIndent(6);
		_printFile.capitaliseNextWord();
		_printFile.outputLine(uncoded.toString());
		_printFile.setIndent(0);
	}
	
	protected void appendUncodedCharacters(StringBuilder out) {
		out.append(" ");
		List<Integer> charNums = new ArrayList<Integer>();
		for (Character character : _uncodedChars) {
			charNums.add(character.getCharacterId());
		}
		
		out.append(_deltaWriter.rangeToString(charNums));
	}

	
	@Override
	public void afterLastItem() {
		_typeSetter.afterLastItem();
	}
}

package au.org.ala.delta.translation.print;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.Words;
import au.org.ala.delta.translation.Words.Word;

public class UncodedCharactersTranslator extends UncodedCharactersPrinter {
	private CharacterFormatter _characterFormatter;
	public UncodedCharactersTranslator(
			DeltaContext context, 
			PrintFile printFile, 
			ItemFormatter itemFormatter,
			CharacterFormatter characterFormatter,
			ItemListTypeSetter typeSetter) {
		super(context, printFile, itemFormatter, typeSetter);
		_characterFormatter = characterFormatter;
	}
	
	
	protected void appendUncodedCharacters(StringBuilder out) {
		for (Character character : _uncodedChars) {
			out.append(" ");
			out.append(_characterFormatter.formatCharacterDescription(character));
			out.append(Words.word(Word.FULL_STOP));
		}
		
	}

}

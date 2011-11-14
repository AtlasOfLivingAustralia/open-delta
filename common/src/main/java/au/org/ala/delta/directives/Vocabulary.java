package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.translation.Words;
import au.org.ala.delta.translation.Words.Word;

public class Vocabulary extends AbstractFormattingDirective {

	public Vocabulary() {
		super("vocabulary");
	}

	@Override
	public void processMark(DeltaContext context, TypeSettingMark mark) {
		Words.setWord(Word.values()[mark.getId()], mark.getMarkText());
	}
	
	@Override
	public int getOrder() {
		return 4;
	}

}

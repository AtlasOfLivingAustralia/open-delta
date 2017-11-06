/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.translation.Words;
import au.org.ala.delta.translation.Words.Word;

public class Vocabulary extends AbstractFormattingDirective {

	public Vocabulary() {
		super("vocabulary");
	}

	@Override
	public void processMark(DeltaContext context, TypeSettingMark mark) {
		int wordId = mark.getId()-1;
		if (wordId >= 0 && wordId < Word.values().length) {
			Words.setWord(Word.values()[wordId], mark.getMarkText());
		}
	}
	
	@Override
	public int getOrder() {
		return 4;
	}

}

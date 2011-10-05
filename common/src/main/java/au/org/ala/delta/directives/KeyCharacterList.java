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

import java.io.StringReader;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.CharacterListParser;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the KEY CHARACTER LIST directive and initiates the character translation
 * operation. (@see au.org.ala.delta.directives.TranslateInto)
 * The KEY CHARACTER LIST directive cannot be used with the CHARACTER LIST
 * directive.
 */
public class KeyCharacterList extends AbstractTextDirective {

	public static final String[] CONTROL_WORDS = {"key", "character", "list"};
	
	public KeyCharacterList() {
		super(CONTROL_WORDS);
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_OTHER;
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
		
		context.setKeyCharacterListUsed(true);
		String data = args.getFirstArgumentText();
		
		StringReader reader = new StringReader(data);
		CharacterListParser parser = new CharacterListParser(context, reader);
		parser.parse();
	}
}

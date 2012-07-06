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
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdWithIdListParser;
import au.org.ala.delta.directives.validation.CharacterNumberValidator;
import au.org.ala.delta.directives.validation.ItemNumberValidator;

import java.io.StringReader;
import java.util.HashSet;

/**
 * Parses and processes the EMPHASIZE CHARACTERS directive.
 */
public class EmphasizeCharacters extends AbstractCustomDirective {
	
	public static final String[] CONTROL_WORDS = {"emphasize", "characters"};
	
	
	public EmphasizeCharacters() {
		super(CONTROL_WORDS);
	}

	@Override
	protected DirectiveArgsParser createParser(DeltaContext context,
			StringReader reader) {
		return new IdWithIdListParser(context, reader, new ItemNumberValidator(context), new CharacterNumberValidator(context));
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_ITEMCHARLIST;
	}

	@Override
	public void process(DeltaContext context,
			DirectiveArguments directiveArguments) throws Exception {
		
		for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
			if (arg.getId() instanceof Integer) {
				context.emphasizeCharacters((Integer)arg.getId(), new HashSet<Integer>(arg.getDataList()));
			}
			else {
				context.emphasizeCharacters((String)arg.getId(), new HashSet<Integer>(arg.getDataList()));
			}
		}
		
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}

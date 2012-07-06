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
import au.org.ala.delta.directives.args.IntegerTextListParser;
import au.org.ala.delta.directives.validation.CharacterNumberValidator;

import java.io.StringReader;

/**
 * This class parses the CHARACTER HEADINGS directive.
 */
public class CharacterHeadings extends AbstractCustomDirective {

	public CharacterHeadings() {
		super("character", "headings");
	}
	
	@Override
	public int getArgType() {
		
		return DirectiveArgType.DIRARG_CHARTEXTLIST;
	}
	
	@Override
	protected DirectiveArgsParser createParser(DeltaContext context, StringReader reader) {
		return new IntegerTextListParser(context, reader, new CharacterNumberValidator(context));
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
		
		for (DirectiveArgument<?> arg : args.getDirectiveArguments()) {
			context.addCharacterHeading((Integer)arg.getId(), arg.getText().trim());
		}
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
	
}

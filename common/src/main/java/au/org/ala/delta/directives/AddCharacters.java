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
import java.util.HashSet;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdWithIdListParser;

public class AddCharacters extends AbstractCustomDirective {
	
	public static final String[] CONTROL_WORDS = {"add", "characters"};
	
	public AddCharacters() {
		super(CONTROL_WORDS);
	}

	@Override
	protected DirectiveArgsParser createParser(DeltaContext context,
			StringReader reader) {
		return new IdWithIdListParser(context, reader);
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
				context.addCharacters((Integer)arg.getId(), new HashSet<Integer>(arg.getDataList()));
			}
			else {
				context.addCharacters((String)arg.getId(), new HashSet<Integer>(arg.getDataList()));
			}
		}
		
	}
	
	@Override
    public int getOrder() {
    	return 4;
    }
}

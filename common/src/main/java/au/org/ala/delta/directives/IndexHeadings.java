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
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.StringTextListParser;

/**
 * This class processes the INDEX HEADINGS directive.
 */
public class IndexHeadings extends AbstractCustomDirective {

	public static final String[] CONTROL_WORDS = {"index", "headings"};
	
	public IndexHeadings() {
		super(CONTROL_WORDS);
	}
	
	@Override
	public int getArgType() {
		
		return DirectiveArgType.DIRARG_ITEMTEXTLIST;
	}
	
	@Override
	protected DirectiveArgsParser createParser(DeltaContext context, StringReader reader) {
		return new StringTextListParser(context, reader);
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
		
		for (DirectiveArgument<?> arg : args.getDirectiveArguments()) {
			// The delimiter is stored with id = Short.MIN_VALUE
			if (arg.getId() instanceof String) {
				context.setIndexHeading((String)arg.getId(), arg.getText().trim());
			}
		}
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}

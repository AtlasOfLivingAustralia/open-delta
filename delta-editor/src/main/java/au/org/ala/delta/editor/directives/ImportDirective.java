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
package au.org.ala.delta.editor.directives;

import java.text.ParseException;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;

/**
 * Parses and imports all directives not of type DirectiveType.DIRARG_INTERNAL.
 */
public class ImportDirective extends AbstractDirective<ImportContext>{

	private Directive _directive;
	private DirectiveArguments _args;

	public ImportDirective(Directive directive) {
		super(directive.getName());
		_directive = directive;
	}

	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	@Override
	public int getArgType() {
		return _directive.getArgType();
	}

	@Override
	public void parse(ImportContext context, String data) throws ParseException {
		
		DirectiveArgsParser parser = DirectiveArgParserFactory.parserFor(_directive, context, data);
		
		if (_directive.getArgType() != DirectiveArgType.DIRARG_NONE) {
			parser.parse(); 
			_args = parser.getDirectiveArgs();
		}
		
		Logger.debug("Importer parsed directive: "+_directive.joinNameComponents()+" Arg type: "+_directive.getArgType());
	}

	/**
	 * The DirectiveFileImporter will never invoke this method.
	 */
	@Override
	public void process(ImportContext context,
			DirectiveArguments directiveArguments) throws Exception {
		throw new UnsupportedOperationException();
	}
}

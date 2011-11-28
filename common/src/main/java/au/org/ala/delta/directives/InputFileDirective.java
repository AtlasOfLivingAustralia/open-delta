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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.DirectiveError;

public abstract class InputFileDirective extends AbstractTextDirective {

	public InputFileDirective(String... directive) {
		super(directive);
	}

	protected void parseFile(DeltaContext context, File file) throws ParseException {
		try {
			if (file.exists()) {
				DirectiveParser<DeltaContext> parser = createParser();
				DirectiveParserObserver observer = context.getDirectiveParserObserver();
				if (observer != null) {
					parser.registerObserver(observer);
				}
				parser.parse(file, context);				
			} else {
				throw DirectiveError.asException(DirectiveError.Error.FILE_DOES_NOT_EXIST, 0);
			}
		}
		catch (IOException e) {
			throw DirectiveError.asException(DirectiveError.Error.FILE_INACCESSABLE, 0);
		}
	}

	
	protected DirectiveParser<DeltaContext> createParser() {
		return  ConforDirectiveFileParser.createInstance();
	}
}

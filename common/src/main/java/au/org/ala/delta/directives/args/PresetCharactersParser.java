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
package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.validation.DirectiveError;

public class PresetCharactersParser extends DirectiveArgsParser {

	public PresetCharactersParser(AbstractDeltaContext context, Reader reader) {
		super(context, reader);
	}

	@Override
	public void parse() throws ParseException {
		_args = new DirectiveArguments();
		
		readNext();
		int lastColumn = -1;
		int lastGroup = -1;
		skipWhitespace();
		
		
		while (_currentInt >= 0) {
			int charNum = readInteger();
			expect(',');
			readNext();
			
			int columnNum = readInteger();
			expect(':');
			readNext();
			
			int groupNum = readInteger();
			
            if (columnNum < lastColumn || groupNum <= lastGroup) {
            	throw DirectiveError.asException(DirectiveError.Error.VALUE_OUT_OF_ORDER, _position);
            }
            
			DirectiveArgument<?> arg = _args.addDirectiveArgument(charNum);
			arg.add(columnNum);
			arg.add(groupNum);
			
			lastGroup = groupNum;
            if (columnNum > lastColumn) {
                lastGroup = 0;
            }
            lastColumn = columnNum;
            skipWhitespace();
		}
		
	}
	
	

}

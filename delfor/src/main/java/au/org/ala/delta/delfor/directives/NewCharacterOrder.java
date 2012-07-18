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
package au.org.ala.delta.delfor.directives;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.delfor.format.CharacterReorderer;
import au.org.ala.delta.directives.AbstractRangeListDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.validation.CharacterNumberValidator;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.directives.validation.IntegerValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Processes the NEW CHARACTER ORDER directive.
 */
public class NewCharacterOrder extends AbstractRangeListDirective<DelforContext> {

	public NewCharacterOrder() {
		super("new", "character", "order");
	}
	
	private List<Integer> _newOrder;
	
	@Override
	protected void processNumber(DelforContext context, int number) throws DirectiveException {
		if (_newOrder.contains(number)) {
			// check all character numbers exist.
			if (_newOrder.size() != context.getDataSet().getNumberOfCharacters()) {
				throw DirectiveError.asException(DirectiveError.Error.CHARACTER_ALREADY_SPECIFIED, 0);
			}
		}
		_newOrder.add(number);
	}
	
	
	

	@Override
	public void process(DelforContext context, DirectiveArguments directiveArguments) throws Exception {
		_newOrder = new ArrayList<Integer>();
		
		super.process(context, directiveArguments);
		
		// check all character numbers exist.
		if (_newOrder.size() != context.getDataSet().getNumberOfCharacters()) {
			throw DirectiveError.asException(DirectiveError.Error.MISSING_DATA, 0);
		}
		
		context.addFormattingAction(new CharacterReorderer(_newOrder));
	}

	@Override
	public int getArgType() {
	     return DirectiveArgType.DIRARG_CHARLIST;
	}

	@Override
	public int getOrder() {
		return 4;
	}

    @Override
    protected IntegerValidator createValidator(DelforContext context) {
        return new CharacterNumberValidator(context);
    }
}

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

import java.text.ParseException;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.util.IntegerFunctor;

public abstract class AbstractRangeListDirective<C extends AbstractDeltaContext> extends AbstractDirective<C> {

	protected DirectiveArguments _args;
	
	protected AbstractRangeListDirective(String ...controlWords) {
		super(controlWords);
	}
	
	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	
	
	@Override
	public void parse(C context, String data) throws ParseException {
		_args = new DirectiveArguments();
		// data is a space separate list of ranges...
		String[] ranges = data.split(" ");
		for (String range : ranges) {
			IntRange r = parseRange(range);
			forEach(r, context, new IntegerFunctor<C>() {
				@Override
				public void invoke(C context, int number) {
					_args.addDirectiveArgument(number);
				}
			});
		}
	}

	@Override
	public void process(C context, DirectiveArguments directiveArguments)
			throws Exception {
		for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
			processNumber(context, (Integer)arg.getId());
		}
		
	}

	protected abstract void processNumber(C context, int number);

}

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

import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdListParser;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.directives.validation.IdValidator;

import java.io.StringReader;
import java.text.ParseException;

/**
 * Base class for directives that accept a space separated list of numbers.
 * @param <C> the type of context the directive executes in.
 */
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

        IdListParser parser = new IdListParser(context, new StringReader(data), createValidator(context));
        parser.parse();
        _args = parser.getDirectiveArgs();
	}

	@Override
	public void process(C context, DirectiveArguments directiveArguments)
			throws Exception {
		for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
			processNumber(context, (Integer)arg.getId());
		}
		
	}

    /**
     * Subclasses should override this method to do something with the number - normally this involves configuration
     * of the context.
     * @param context the current parsing / dataset context.
     * @param number the number to process.
     * @throws DirectiveException if there is an error.
     */
	protected abstract void processNumber(C context, int number) throws DirectiveException;

    /**
     * Subclasses should override this method to create a validator appropriate for the directive type.
     * @param context the current parsing/processing context.
     * @return either an appropriate instance of IdValidator or null if validation is not required.
     */
    protected abstract IdValidator createValidator(C context);

}

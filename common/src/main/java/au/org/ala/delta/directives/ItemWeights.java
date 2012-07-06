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
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdValueListParser;
import au.org.ala.delta.directives.validation.IdValidator;
import au.org.ala.delta.directives.validation.ItemNumberValidator;

import java.io.StringReader;
import java.text.ParseException;


/**
 * Processes the ITEM WEIGHTS directive.
 * @link http://delta-intkey.com/www/uguide.htm#_*ITEM_WEIGHTS_
 */
public class ItemWeights extends AbstractDirective<DeltaContext> {

	public static final String[] CONTROL_WORDS =  {"item", "weights"};
	
	
	/** The default weight for any characters not included in this directive */
	private static final double DEFAULT_WEIGHT = 1d;
	
	private DirectiveArguments _args;
	
	public ItemWeights() {
		super(CONTROL_WORDS);
	}

	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_ITEMREALLIST;
	}

	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
        IdValidator itemValidator = new ItemNumberValidator(context);
		IdValueListParser parser = new IdValueListParser(context, new StringReader(data), itemValidator);
		parser.parse();
		
		_args = parser.getDirectiveArgs();
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		
		for (int i=1; i<=context.getMaximumNumberOfItems(); i++) {
			context.addItemAbundancy(i, DEFAULT_WEIGHT);
		}
		for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
			double abundancy = weightToAbundancy( arg.getValue().doubleValue());
			context.addItemAbundancy((Integer)arg.getId(), abundancy);
		}
		
	}
	
	private double weightToAbundancy(double weight) {
		return Math.pow(2, weight-5);
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
}

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
import java.util.regex.Pattern;

import au.org.ala.delta.directives.validation.DirectiveException;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;

public abstract class AbstractCharacterListDirective<C extends AbstractDeltaContext, T> extends AbstractDirective<C> {

	private static Pattern CHAR_LIST_ITEM_PATTERN = Pattern.compile("^(\\d+),(.*)$|^(\\d+[-]\\d+),(.*)$");

	private DirectiveArguments args;
	
	public AbstractCharacterListDirective(String... controlWords) {
		super(controlWords);
	}
	
	@Override
	public DirectiveArguments getDirectiveArgs() {
		return args;
	}

	@Override
	public void parse(C context, String data) throws ParseException {
		args = new DirectiveArguments();
		String[] typeDescriptors = data.trim().split(" |\\n");
		for (String typeDescriptor : typeDescriptors) {
			typeDescriptor = typeDescriptor.trim();
			if (CHAR_LIST_ITEM_PATTERN.matcher(typeDescriptor).matches()) {
				String[] bits = typeDescriptor.trim().split(",");
				IntRange r = parseRange(bits[0]);
				
				for (int charNumber = r.getMinimumInteger(); charNumber <= r.getMaximumInteger(); ++charNumber) {
					addArgument(args, charNumber, bits[1]);
				}
			}
		}
	}

	@Override
	public void process(C context, DirectiveArguments directiveArguments) throws Exception {
		for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
			
			T rhs = interpretRHS(context, arg.valueAsString());
			processCharacter(context, (Integer)arg.getId(), rhs);
		}
		
	}

	protected abstract void addArgument(DirectiveArguments args, int charIndex, String value) throws DirectiveException;
	
	protected abstract T interpretRHS(C context, String rhs) throws DirectiveException;

	protected abstract void processCharacter(C context, int charIndex, T rhs) throws ParseException;

}

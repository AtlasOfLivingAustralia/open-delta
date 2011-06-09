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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * An AbstractCharacterSetDirective is a directive that takes a space separated list of 
 * character sets of the form: c1:c2:...cn where cn is a character number or a range of numbers.
 */
public abstract class AbstractCharacterSetDirective<C extends AbstractDeltaContext> extends AbstractDirective<C> {

	private static Pattern CHAR_SET_PATTERN = Pattern.compile("^(\\d+)[:-](.*)$");

	protected DirectiveArguments args;
	
	public AbstractCharacterSetDirective(String... controlWords) {
		super(controlWords);
	}
	
	@Override
	public DirectiveArguments getDirectiveArgs() {
		return args;
	}

	@Override
	public void process(C context, String data) throws Exception {
		args = new DirectiveArguments();
		String[] typeDescriptors = data.split(" |\\n");
		for (String typeDescriptor : typeDescriptors) {
			typeDescriptor = typeDescriptor.trim();
			if (CHAR_SET_PATTERN.matcher(typeDescriptor).matches()) {
				String[] bits = typeDescriptor.trim().split(":");
				
				List<Integer> characters = new ArrayList<Integer>();
				for (String bit : bits) {
					IntRange r = parseRange(bit);
					for (int i : r.toArray()) {
						characters.add(i);
					}
				}
				args.addDirectiveArgument(characters);
				processCharacterSet(context, characters);
			}
			
		}
	}

	protected abstract void processCharacterSet(C context, List<Integer> characters);

}

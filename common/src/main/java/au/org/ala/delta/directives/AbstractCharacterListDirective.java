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

import java.util.regex.Pattern;

import org.apache.commons.lang.math.IntRange;

public abstract class AbstractCharacterListDirective<C extends AbstractDeltaContext, T> extends AbstractDirective<C> {

	private static Pattern CHAR_LIST_ITEM_PATTERN = Pattern.compile("^(\\d+),(.*)$|^(\\d+[-]\\d+),(.*)$");

	public AbstractCharacterListDirective(String... controlWords) {
		super(controlWords);
	}

	@Override
	public void process(C context, String data) throws Exception {
		String[] typeDescriptors = data.split(" |\\n");
		for (String typeDescriptor : typeDescriptors) {
			typeDescriptor = typeDescriptor.trim();
			if (CHAR_LIST_ITEM_PATTERN.matcher(typeDescriptor).matches()) {
				String[] bits = typeDescriptor.trim().split(",");
				IntRange r = parseRange(bits[0]);
				T rhs = interpretRHS(context, bits[1]);
				for (int charIndex = r.getMinimumInteger(); charIndex <= r.getMaximumInteger(); ++charIndex) {
					processCharacter(context, charIndex, rhs);
				}
			}
		}
	}

	protected abstract T interpretRHS(C context, String rhs);

	protected abstract void processCharacter(C context, int charIndex, T rhs);

}

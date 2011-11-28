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
package au.org.ala.delta.directives.validation;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.directives.validation.DirectiveError.Error;

public class UniqueIdValidator {

	private Set<Integer> _ids;
	
	public UniqueIdValidator() {
		_ids = new HashSet<Integer>();
	}
	
	public DirectiveError validateIds(IntRange ids, int position) {
		for (int i : ids.toArray()) {
			if (_ids.contains(i)) {
				return new DirectiveError(Error.DUPLICATE_VALUE, position);
			}
			_ids.add(i);
		}
		return null;
	}
	
}

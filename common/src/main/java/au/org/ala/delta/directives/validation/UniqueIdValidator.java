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

import au.org.ala.delta.directives.validation.DirectiveError.Error;

import java.util.HashSet;
import java.util.Set;

/**
 * The UniqueIdValidator keeps track of ids that have been passed to the validateId method and will return
 * an error if a duplicate is found.
 */
public class UniqueIdValidator implements IdValidator {

	private Set<Integer> _ids;
	
	public UniqueIdValidator() {
		_ids = new HashSet<Integer>();
	}

    @Override
	public DirectiveError validateId(int id) {
		if (_ids.contains(id)) {
				return new DirectiveError(Error.DUPLICATE_VALUE, DirectiveError.UNKNOWN_POSITION);
		}
		_ids.add(id);

		return null;
	}
	
}

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
package au.org.ala.delta.ui;

import au.org.ala.delta.util.Predicate;

public abstract class GenericSearchPredicate<T> implements Predicate<T> {
	
	private String _term;
	private SearchOptions _options;
	
	protected GenericSearchPredicate(SearchOptions options) {
		_options = options;
		_term = options.getSearchTerm();
		if (!options.isCaseSensitive()) {
			_term = _term.toLowerCase();
		}
	}
	
	public SearchOptions getOptions() {
		return _options;
	}

	public String getTerm() {
		return _term;
	}
	
}

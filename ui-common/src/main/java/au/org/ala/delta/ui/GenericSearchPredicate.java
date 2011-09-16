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
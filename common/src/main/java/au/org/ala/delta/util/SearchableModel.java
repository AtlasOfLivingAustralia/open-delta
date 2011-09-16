package au.org.ala.delta.util;

import au.org.ala.delta.model.SearchDirection;

public interface SearchableModel<T> {

	T first(Predicate<T> predicate, int startIndex, SearchDirection direction);
	
	int size();

}

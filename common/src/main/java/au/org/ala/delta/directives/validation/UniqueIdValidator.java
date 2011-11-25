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

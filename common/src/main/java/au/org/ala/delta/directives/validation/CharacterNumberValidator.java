package au.org.ala.delta.directives.validation;

import au.org.ala.delta.DeltaContext;

/**
 * Validates that the parsed id maps to a valid character in the data set as well as supplied character numbers
 * are unique.
 */
public class CharacterNumberValidator extends UniqueIdValidator {

    private DeltaContext _context;

    public CharacterNumberValidator(DeltaContext context) {
        _context = context;
    }

    @Override
    public DirectiveError validateId(int id) {
        if (id < 1) {
            return new DirectiveError(DirectiveError.Error.ILLEGAL_VALUE_NO_ARGS, DirectiveError.UNKNOWN_POSITION);
        }
        else if (id > _context.getNumberOfCharacters()) {
            return new DirectiveError(DirectiveError.Error.CHARACTER_NUMBER_TOO_HIGH, DirectiveError.UNKNOWN_POSITION);
        }

        return super.validateId(id);
    }


}

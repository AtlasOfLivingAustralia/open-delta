package au.org.ala.delta.directives.validation;

import au.org.ala.delta.DeltaContext;

/**
 * Validates that the parsed id maps to a valid item in the data set as well as supplied item numbers
 * are unique.
 * Note that the validation is based on the supplied MAXIMUM NUMBER OF ITEMS - the actual number of items is
 * typically not known until the ITEM DESCRIPTIONS directive is parsed (which is order 5 and hence comes
 * last in a directives file).
 */
public class ItemNumberValidator extends UniqueIdValidator {

    private DeltaContext _context;

    public ItemNumberValidator(DeltaContext context) {
        _context = context;
    }

    @Override
    public DirectiveError validateId(int id) {
        if (id < 1) {
            return new DirectiveError(DirectiveError.Error.ILLEGAL_VALUE_NO_ARGS, DirectiveError.UNKNOWN_POSITION);
        }
        else if (id > _context.getMaximumNumberOfItems()) {
            return new DirectiveError(DirectiveError.Error.ILLEGAL_VALUE_NO_ARGS, DirectiveError.UNKNOWN_POSITION);
        }

        return super.validateId(id);
    }


}

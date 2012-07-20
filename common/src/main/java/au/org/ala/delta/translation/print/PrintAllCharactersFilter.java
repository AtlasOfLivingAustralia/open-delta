package au.org.ala.delta.translation.print;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.*;
import au.org.ala.delta.translation.delta.DeltaFormatDataSetFilter;

/**
 * Used as the filter for the PRINT CHARACTER LIST and PRINT ITEM DESCRIPTIONS PrintActions when the
 * PRINT ALL CHARACTERS directive is in force.
 */
public class PrintAllCharactersFilter extends DeltaFormatDataSetFilter {

    /**
     * Creates a new PrintAllCharactersFilter
     * @param context the DeltaContext the filter works from.
     */
    public PrintAllCharactersFilter(DeltaContext context) {
        super(context);
    }

    @Override
    public boolean filter(Item item) {
        return true;
    }

    @Override
    public boolean filter(au.org.ala.delta.model.Character character) {
        return true;
    }
}

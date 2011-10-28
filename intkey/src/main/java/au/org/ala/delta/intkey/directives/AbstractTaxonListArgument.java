package au.org.ala.delta.intkey.directives;

public abstract class AbstractTaxonListArgument<T> extends IntkeyDirectiveArgument<T> {

    protected static final String OVERRIDE_EXCLUDED_TAXA = "/T";

    /**
     * If true, excluded taxa are ignored when prompting the user to select
     * taxa. User will select from the list of all taxa.
     */
    protected boolean _selectFromAll;

    /**
     * If true, the "NONE" keyword is a permitted option
     */
    protected boolean _noneSelectionPermitted;

    public AbstractTaxonListArgument(String name, String promptText, boolean selectFromAll, boolean noneSelectionPermitted) {
        super(name, promptText, null);
        _selectFromAll = selectFromAll;
        _noneSelectionPermitted = noneSelectionPermitted;
    }

}

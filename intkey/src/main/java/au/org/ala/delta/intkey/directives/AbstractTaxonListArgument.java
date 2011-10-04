package au.org.ala.delta.intkey.directives;

public abstract class AbstractTaxonListArgument<T> extends IntkeyDirectiveArgument<T> {

    protected static final String OVERRIDE_EXCLUDED_TAXA = "/T";

    protected SelectionMode _defaultSelectionMode;

    /**
     * If true, excluded taxa are ignored when prompting the user to select
     * taxa. User will select from the list of all taxa.
     */
    protected boolean _selectFromAll;

    public AbstractTaxonListArgument(String name, String promptText, SelectionMode defaultSelectionMode, boolean selectFromAll) {
        super(name, promptText, null);
        _defaultSelectionMode = defaultSelectionMode;
        _selectFromAll = selectFromAll;
    }

}

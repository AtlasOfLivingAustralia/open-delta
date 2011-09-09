package au.org.ala.delta.intkey.directives;

import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;

public abstract class IntkeyDirectiveArgument<T> {

    protected static final String DEFAULT_DIALOG_WILDCARD = "?";
    protected static final String KEYWORD_DIALOG_WILDCARD = "?K";
    protected static final String LIST_DIALOG_WILDCARD = "?L";
    protected static final String LIST_DIALOG_AUTO_SELECT_SOLE_ITEM_WILDCARD = "?L1";

    protected String _name;
    protected String _promptText;
    protected T _initialValue;

    public IntkeyDirectiveArgument(String name, String promptText, T initialValue) {
        _name = name;
        _promptText = promptText;
        _initialValue = initialValue;
    }

    public String getName() {
        return _name;
    }

    public String getPromptText() {
        return _promptText;
    }

    public T getInitialValue() {
        return _initialValue;
    }

    abstract public T parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName) throws IntkeyDirectiveParseException;
}

package au.org.ala.delta.intkey.directives;

import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;

public abstract class IntkeyDirectiveArgument {
    
    protected static final String DEFAULT_DIALOG_WILDCARD = "?";
    protected static final String KEYWORD_DIALOG_WILDCARD = "?K";
    protected static final String LIST_DIALOG_WILDCARD = "?L";
    protected static final String LIST_DIALOG_AUTO_SELECT_SOLE_ITEM_WILDCARD = "?L1";

    private String _name;
    private String _promptText;

    public IntkeyDirectiveArgument(String name, String promptText) {
        _name = name;
        _promptText = promptText;
    }

    public String getName() {
        return _name;
    }

    public String getPromptText() {
        return _promptText;
    }

    abstract public Object parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName) throws IntkeyDirectiveParseException;
}

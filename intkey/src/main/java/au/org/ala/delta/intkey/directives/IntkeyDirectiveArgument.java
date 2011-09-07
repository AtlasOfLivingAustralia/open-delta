package au.org.ala.delta.intkey.directives;

import java.util.Queue;

public abstract class IntkeyDirectiveArgument {

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

    abstract public Object parseInput(Queue<String> inputTokens, DirectivePopulator populator, String directiveName) throws Exception;
}

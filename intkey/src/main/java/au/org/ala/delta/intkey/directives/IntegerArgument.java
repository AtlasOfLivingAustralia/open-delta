package au.org.ala.delta.intkey.directives;

import java.util.Queue;

public class IntegerArgument extends IntkeyDirectiveArgument {

    public IntegerArgument(String name, String promptText) {
        super(name, promptText);
    }

    @Override
    public Object parseInput(Queue<String> inputTokens, DirectivePopulator populator, String directiveName) throws Exception {
        String token = inputTokens.poll();

        if (token == null) {
            token = populator.promptForString(getPromptText(), null, directiveName);
        }

        if (token != null) {
            try {
                int parsedInteger = Integer.parseInt(token);
                return parsedInteger;
            } catch (NumberFormatException ex) {
                throw new IntkeyDirectiveParseException("Integer value required", ex);
            }
        }
        return null;
    }
}

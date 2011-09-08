package au.org.ala.delta.intkey.directives;

import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class RealArgument extends IntkeyDirectiveArgument {

    public RealArgument(String name, String promptText) {
        super(name, promptText);
    }

    @Override
    public Object parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName) throws IntkeyDirectiveParseException {
        String token = inputTokens.poll();

        if (token == null) {
            token = context.getDirectivePopulator().promptForString(getPromptText(), null, directiveName);
        }

        if (token != null) {
            try {
                double parsedDouble = Double.parseDouble(token);
                return parsedDouble;
            } catch (NumberFormatException ex) {
                throw new IntkeyDirectiveParseException("Real value required", ex);
            }
        }
        return null;
    }

}

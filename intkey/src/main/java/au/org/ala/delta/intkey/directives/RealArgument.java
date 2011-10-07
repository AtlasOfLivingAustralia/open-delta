package au.org.ala.delta.intkey.directives;

import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class RealArgument extends IntkeyDirectiveArgument<Double> {

    public RealArgument(String name, String promptText, double initialValue) {
        super(name, promptText, initialValue);
    }

    @Override
    public Double parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName, StringBuilder stringRepresentationBuilder) throws IntkeyDirectiveParseException {
        String token = inputTokens.poll();

        if (token == null || token.equals(DEFAULT_DIALOG_WILDCARD)) {
            token = context.getDirectivePopulator().promptForString(getPromptText(), null, directiveName);
        }

        if (token != null) {
            try {
                double parsedDouble = Double.parseDouble(token);
                stringRepresentationBuilder.append(" ");
                stringRepresentationBuilder.append(parsedDouble);
                return parsedDouble;
            } catch (NumberFormatException ex) {
                throw new IntkeyDirectiveParseException("Real value required", ex);
            }
        }
        return null;
    }

}

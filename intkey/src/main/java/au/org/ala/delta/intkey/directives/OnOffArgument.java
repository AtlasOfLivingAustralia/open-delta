package au.org.ala.delta.intkey.directives;

import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class OnOffArgument extends IntkeyDirectiveArgument<Boolean> {

    private static final String ON_VALUE = "ON";
    private static final String OFF_VALUE = "OFF";

    public OnOffArgument(String name, String promptText, boolean initialValue) {
        super(name, promptText, initialValue);
    }

    @Override
    public Boolean parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName, StringBuilder stringRepresentationBuilder) throws IntkeyDirectiveParseException {
        String token = inputTokens.poll();

        if (token == null || token.equals(DEFAULT_DIALOG_WILDCARD)) {
            return context.getDirectivePopulator().promptForOnOffValue(directiveName, getInitialValue());
        } else {
            if (token.equalsIgnoreCase(ON_VALUE)) {
                stringRepresentationBuilder.append(" ");
                stringRepresentationBuilder.append(ON_VALUE);
                return true;
            } else if (token.equalsIgnoreCase(OFF_VALUE)) {
                stringRepresentationBuilder.append(" ");
                stringRepresentationBuilder.append(OFF_VALUE);
                return false;
            } else {
                throw new IntkeyDirectiveParseException(String.format("Invalid value '%s', expecting '%s' or '%s'", token, ON_VALUE, OFF_VALUE));
            }
        }
    }

}

package au.org.ala.delta.intkey.directives;

import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class StringArgument extends IntkeyDirectiveArgument<String> {

    /**
     * ctor
     * 
     * @param name
     *            Argument name
     * @param promptText
     *            text to prompt user if a value is needed for the argument
     * @param initialValue
     *            initialValue to be shown when user is prompted.
     * @param spaceDelimited
     *            if true, any space character that is not surrounded by quotes
     *            will indicate the end of the argument value. If false, all
     *            available data is used to form the argument value
     */

    private boolean _spaceDelimited;

    public StringArgument(String name, String promptText, String initialValue, boolean spaceDelimited) {
        super(name, promptText, initialValue);
        _spaceDelimited = spaceDelimited;
    }

    @Override
    public String parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName, StringBuilder stringRepresentationBuilder) throws IntkeyDirectiveParseException {
        if (_spaceDelimited) {
            String token = inputTokens.poll();

            if (token == null || token.equals(DEFAULT_DIALOG_WILDCARD)) {
                token = context.getDirectivePopulator().promptForString(_promptText, _initialValue, directiveName);
            }

            stringRepresentationBuilder.append(" ");
            stringRepresentationBuilder.append(token);

            return token;
        } else {
            // If argument is not space delimited, we need to use all available
            // tokens in the queue to construct the value for
            // the argument.
            StringBuilder valueBuilder = new StringBuilder();
            while (!inputTokens.isEmpty()) {
                valueBuilder.append(inputTokens.poll());

                if (!inputTokens.isEmpty()) {
                    valueBuilder.append(" ");
                }
            }

            stringRepresentationBuilder.append(" ");
            stringRepresentationBuilder.append(valueBuilder.toString());

            return valueBuilder.toString();
        }
    }

}

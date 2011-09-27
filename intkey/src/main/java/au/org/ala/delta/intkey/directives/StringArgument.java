package au.org.ala.delta.intkey.directives;

import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class StringArgument extends IntkeyDirectiveArgument<String> {

    public StringArgument(String name, String promptText, String initialValue) {
        super(name, promptText, initialValue);
    }

    @Override
    public String parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName, StringBuilder stringRepresentationBuilder) throws IntkeyDirectiveParseException {
        String token = inputTokens.poll();
        
        if (token == null) {
            token = context.getDirectivePopulator().promptForString(_promptText, _initialValue, directiveName);
        } 
        
        stringRepresentationBuilder.append(" ");
        stringRepresentationBuilder.append(token);
        
        return token;
    }

}

package au.org.ala.delta.intkey.directives;

import java.util.Queue;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class StringArgument extends IntkeyDirectiveArgument {

    public StringArgument(String name, String promptText) {
        super(name, promptText);
    }

    @Override
    public Object parseInput(Queue<String> inputTokens, IntkeyContext context, String directiveName) throws IntkeyDirectiveParseException {
        String token = inputTokens.poll();
        
        if (token == null) {
            token = context.getDirectivePopulator().promptForString(this.getPromptText(), null, directiveName);
        } 
        
        return token;
    }

}

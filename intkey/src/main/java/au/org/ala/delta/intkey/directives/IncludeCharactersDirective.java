package au.org.ala.delta.intkey.directives;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.intkey.directives.invocation.IncludeCharactersDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class IncludeCharactersDirective extends IntkeyDirective {
    
    public IncludeCharactersDirective() {
        super("include", "characters");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        Set<Integer> includeCharacterNumbers = new HashSet<Integer>();
        
        List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);
        
        if (tokens.isEmpty()) {
            List<au.org.ala.delta.model.Character> selectedCharacters = context.getDirectivePopulator().promptForCharacters("INCLUDE CHARACTERS", false);
            for (au.org.ala.delta.model.Character ch: selectedCharacters) {
                includeCharacterNumbers.add(ch.getCharacterId());
            }
        } else {
            for (String token: tokens) {
                List<au.org.ala.delta.model.Character> tokenCharacters = ParsingUtils.parseCharacterToken(token, context);
                for (au.org.ala.delta.model.Character ch: tokenCharacters) {
                    includeCharacterNumbers.add(ch.getCharacterId());
                }
            }
        }
        
        if (includeCharacterNumbers.size() == 0) {
            return null;
        }
        
        return new IncludeCharactersDirectiveInvocation(includeCharacterNumbers);
    }
}

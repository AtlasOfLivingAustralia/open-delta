package au.org.ala.delta.intkey.directives;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.IntRange;

//TODO smashed out quickly to help with implementation of the USE directive.
//need to revisit in order to make it complete.

public class DefineCharactersDirective extends IntkeyDirective {

    public DefineCharactersDirective() {
        super("define", "characters");
    }

    @Override
    public IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        String keyword = null;
        Set<Integer> characterNumbers = new HashSet<Integer>();
        List<String> tokens = ParsingUtils.splitDataIntoSubCommands(data);

        for (int i = 0; i < tokens.size(); i++) {
            if (i == 0) {
                keyword = ParsingUtils.removeEnclosingQuotes(tokens.get(i));
            } else {
                IntRange r = parseRange(tokens.get(i));
                for (int charNum : r.toArray()) {
                    characterNumbers.add(charNum);
                }
            }
        }

        return new DefineCharactersDirectiveInvocation(keyword, characterNumbers);
    }

    class DefineCharactersDirectiveInvocation implements IntkeyDirectiveInvocation {

        String _keyword;
        Set<Integer> _characterNumbers;

        public DefineCharactersDirectiveInvocation(String keyword, Set<Integer> characterNumbers) {
            _keyword = keyword;
            _characterNumbers = characterNumbers;
        }

        @Override
        public void execute(IntkeyContext context) {
            context.addCharacterKeyword(_keyword, _characterNumbers);
        }

        @Override
        public String toString() {
            return String.format("define characters %s %s", _keyword, _characterNumbers.toString());
        }
        
        

    }

}

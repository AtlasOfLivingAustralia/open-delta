package au.org.ala.delta.intkey.directives;

//TODO need to prompt user for reliability value for each subcommand if the 
//value is not supplied by the user.
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.model.Character;

public class SetReliabilitiesDirective extends IntkeyDirective {

    public SetReliabilitiesDirective() {
        super("set", "reliabilities");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        IntkeyDataset dataset = context.getDataset();

        List<String> subCmds = ParsingUtils.tokenizeDirectiveCall(data);

        Map<Character, Float> reliabilitiesMap = new HashMap<Character, Float>();

        for (String subCmd : subCmds) {
            String[] tokens = subCmd.split(",");

            String strCharacters = tokens[0];
            String strReliability = tokens[1];

            List<Character> characters = new ArrayList<Character>();

            IntRange charRange = ParsingUtils.parseIntRange(strCharacters);
            if (charRange != null) {
                for (int index : charRange.toArray()) {
                    characters.add(dataset.getCharacter(index));
                }
            } else {
                characters = context.getCharactersForKeyword(strCharacters);
            }

            float reliability = Float.parseFloat(strReliability);

            for (Character ch : characters) {
                reliabilitiesMap.put(ch, reliability);
            }
        }

        return new SetReliabilitiesDirectiveInvocation(reliabilitiesMap);
    }

    private class SetReliabilitiesDirectiveInvocation implements IntkeyDirectiveInvocation {
        private Map<Character, Float> _reliabilitiesMap;

        public SetReliabilitiesDirectiveInvocation(Map<Character, Float> reliabilitiesMap) {
            _reliabilitiesMap = reliabilitiesMap;
        }

        @Override
        public boolean execute(IntkeyContext context) {
            for (Character ch : _reliabilitiesMap.keySet()) {
                ch.setReliability(_reliabilitiesMap.get(ch));
            }

            return true;
        }

    }
}

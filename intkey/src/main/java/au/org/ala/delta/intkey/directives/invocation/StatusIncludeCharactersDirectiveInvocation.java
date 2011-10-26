package au.org.ala.delta.intkey.directives.invocation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.util.Utils;

public class StatusIncludeCharactersDirectiveInvocation extends IntkeyDirectiveInvocation {

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        List<Character> includedCharacters = context.getIncludedCharacters();
        List<Integer> includedCharacterNumbers = new ArrayList<Integer>();
        for (Character ch : includedCharacters) {
            includedCharacterNumbers.add(ch.getCharacterId());
        }

        String formattedCharacterNumbers = Utils.formatIntegersAsListOfRanges(includedCharacterNumbers);

        builder.setTextColor(Color.BLUE);
        builder.appendText(UIUtils.getResourceString("Status.IncludeCharacters.title"));
        builder.setTextColor(Color.BLACK);
        builder.appendText(UIUtils.getResourceString("Status.IncludeCharacters.content", includedCharacters.size(), formattedCharacterNumbers));

        builder.endDocument();

        context.getUI().displayRTFReport(builder.toString(), UIUtils.getResourceString("Status.title"));

        return true;
    }
}

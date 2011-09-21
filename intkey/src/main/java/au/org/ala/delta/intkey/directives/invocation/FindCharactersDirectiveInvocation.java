package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.SearchUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.rtf.RTFBuilder;

public class FindCharactersDirectiveInvocation implements IntkeyDirectiveInvocation {

    private String _searchText;

    public void setSearchText(String searchText) {
        this._searchText = searchText;
    }

    @Override
    public boolean execute(IntkeyContext context) {

        CharacterFormatter characterFormatter = new CharacterFormatter(true, false, AngleBracketHandlingMode.REMOVE_SURROUNDING_REPLACE_INNER, false);

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        for (Character ch : context.getIncludedCharacters()) {
            if (SearchUtils.characterMatches(ch, _searchText, true)) {
                builder.appendText(characterFormatter.formatCharacterDescription(ch));
            }
        }

        builder.endDocument();

        context.getUI().displayRTFReport(builder.toString(), "Find");

        return true;
    }

}

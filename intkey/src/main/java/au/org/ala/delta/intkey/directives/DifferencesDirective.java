package au.org.ala.delta.intkey.directives;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.model.DiffUtils;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.MatchType;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFBuilder;

public class DifferencesDirective extends IntkeyDirective {

    public DifferencesDirective() {
        super("differences");
    }

    @Override
    public int getArgType() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {

        // Match settings default to those set on the context
        boolean matchUnknowns = true;
        boolean matchInapplicables = true;
        MatchType matchType = MatchType.OVERLAP;

        boolean omitTextCharacters = false;

        DifferencesDirectiveInvocation invoc = new DifferencesDirectiveInvocation(matchUnknowns, matchInapplicables, matchType, omitTextCharacters, context.getDataset().getCharacters(), context
                .getDataset().getTaxa());

        return invoc;
    }

    private class DifferencesDirectiveInvocation implements IntkeyDirectiveInvocation {

        private boolean _matchUnknowns;
        private boolean _matchInapplicables;
        private MatchType _matchType;

        private boolean _omitTextCharacters;

        private List<Character> _characters;
        private List<Item> _taxa;

        private CharacterFormatter _characterFormatter;
        private ItemFormatter _taxonFormatter;
        private AttributeFormatter _attributeFormatter;

        public DifferencesDirectiveInvocation(boolean matchUnknowns, boolean matchInapplicables, MatchType matchType, boolean omitTextCharacters, List<Character> characters, List<Item> taxa) {
            _matchUnknowns = matchUnknowns;
            _matchInapplicables = matchInapplicables;
            _omitTextCharacters = omitTextCharacters;
            _characters = new ArrayList<Character>(characters);
            _taxa = new ArrayList<Item>(taxa);
            _characterFormatter = new CharacterFormatter(false, true, true, false);
            _taxonFormatter = new ItemFormatter(false, true, true, false, false);
            _attributeFormatter = new AttributeFormatter(false, false);
        }

        @Override
        public boolean execute(IntkeyContext context) {
            List<Character> differences = new ArrayList<Character>();
            for (Character ch : _characters) {
                boolean match = DiffUtils.compareForTaxa(context.getDataset(), ch, _taxa, null, true, true, MatchType.OVERLAP);
                if (!match) {
                    differences.add(ch);
                }
            }

            RTFBuilder builder = new RTFBuilder();
            builder.startDocument();

            for (Character ch : differences) {
                List<Attribute> attrs = context.getDataset().getAttributesForCharacter(ch.getCharacterId());

                String charDescription = _characterFormatter.formatCharacterDescription(ch);
                builder.appendText(charDescription);

                builder.increaseIndent();

                for (Item taxon : _taxa) {
                    Attribute taxonAttr = attrs.get(taxon.getItemNumber() - 1);

                    String taxonDescription = _taxonFormatter.formatItemDescription(taxon);
                    builder.appendText(taxonDescription);

                    builder.increaseIndent();

                    String attributeDescription = _attributeFormatter.formatAttribute(taxonAttr);

                    if (StringUtils.isBlank(attributeDescription)) {
                        builder.appendText("not recorded");
                    } else {
                        builder.appendText(attributeDescription);
                    }

                    builder.decreaseIndent();

                }

                builder.decreaseIndent();
            }

            builder.setTextColor(Color.RED);
            builder.setFont(1);

            builder.appendText(String.format("%s differences", differences.size()));

            builder.endDocument();

            return true;
        }
    }
}

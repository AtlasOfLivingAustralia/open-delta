package au.org.ala.delta.intkey.directives.invocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.IntkeyDataset;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFBuilder;

public class DescribeDirectiveInvocation implements IntkeyDirectiveInvocation {

    private List<Item> _taxa;
    private List<Character> _characters;

    public void setTaxa(List<Item> taxa) {
        this._taxa = taxa;
    }

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        IntkeyDataset dataset = context.getDataset();
        Map<Character, String> charactersItemSubheadingMap = generateCharactersItemSubheadingMap(dataset);

        RTFBuilder builder = null;
        if (dataset.itemSubheadingsPresent() && !context.displayNumbering()) {
            builder = generateReportGroupedByItemSubheading(dataset, charactersItemSubheadingMap, context.displayNumbering(), context.displayUnknowns(), context.displayInapplicables());
        } else {
            builder = generateStandardReport(dataset, charactersItemSubheadingMap, context.displayNumbering(), context.displayUnknowns(), context.displayInapplicables());
        }

        context.getUI().displayRTFReport(builder.toString(), "Describe");

        return true;
    }

    private RTFBuilder generateStandardReport(IntkeyDataset dataset, Map<Character, String> charactersItemSubheadingMap, boolean displayNumbering, boolean displayInapplicables, boolean displayUnknowns) {
        ItemFormatter taxonFormatter = new ItemFormatter(displayNumbering, false, AngleBracketHandlingMode.REMOVE, false, false);
        CharacterFormatter characterFormatter = new CharacterFormatter(displayNumbering, false, AngleBracketHandlingMode.REMOVE_SURROUNDING_REPLACE_INNER, false);
        AttributeFormatter attributeFormatter = new AttributeFormatter(displayNumbering, false, false);

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        for (Item taxon : _taxa) {
            builder.appendText(taxonFormatter.formatItemDescription(taxon));
            builder.increaseIndent();

            String currentItemSubheading = null;

            for (Character ch : _characters) {
                Attribute attr = dataset.getAttribute(taxon.getItemNumber(), ch.getCharacterId());

                if (attr.isInapplicable() && !displayInapplicables) {
                    continue;
                }

                if (attr.isUnknown() && !displayUnknowns) {
                    continue;
                }

                String itemSubheading = charactersItemSubheadingMap.get(ch);
                if (itemSubheading != null && !itemSubheading.equals(currentItemSubheading)) {
                    builder.appendText(itemSubheading);
                    currentItemSubheading = itemSubheading;
                }

                String characterDescription = characterFormatter.formatCharacterDescription(ch);
                String attributeDescription = attributeFormatter.formatAttribute(attr);
                builder.appendText(String.format("%s %s", characterDescription, attributeDescription));

            }

            builder.decreaseIndent();
        }

        builder.endDocument();

        return builder;
    }

    private RTFBuilder generateReportGroupedByItemSubheading(IntkeyDataset dataset, Map<Character, String> charactersItemSubheadingMap, boolean displayNumbering, boolean displayInapplicables,
            boolean displayUnknowns) {
        ItemFormatter taxonFormatter = new ItemFormatter(displayNumbering, false, AngleBracketHandlingMode.REMOVE, false, false);
        CharacterFormatter characterFormatter = new CharacterFormatter(displayNumbering, true, AngleBracketHandlingMode.REMOVE_SURROUNDING_REPLACE_INNER, false);
        AttributeFormatter attributeFormatter = new AttributeFormatter(displayNumbering, false, true);

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        for (Item taxon : _taxa) {
            builder.appendText(taxonFormatter.formatItemDescription(taxon));
            builder.increaseIndent();

            String currentItemSubheading = null;
            StringBuilder itemSubheadingTextBuilder = new StringBuilder();

            for (Character ch : _characters) {
                Attribute attr = dataset.getAttribute(taxon.getItemNumber(), ch.getCharacterId());

                if (attr.isInapplicable() && !displayInapplicables) {
                    continue;
                }

                if (attr.isUnknown() && !displayUnknowns) {
                    continue;
                }

                String itemSubheading = charactersItemSubheadingMap.get(ch);
                if (itemSubheading != null && !itemSubheading.equals(currentItemSubheading)) {
                    builder.appendText(itemSubheadingTextBuilder.toString());
                    currentItemSubheading = itemSubheading;
                    itemSubheadingTextBuilder = new StringBuilder();
                    itemSubheadingTextBuilder.append(currentItemSubheading);
                    itemSubheadingTextBuilder.append(" ");
                }

                String characterDescription = characterFormatter.formatCharacterDescription(ch);
                String attributeDescription = attributeFormatter.formatAttribute(attr);
                itemSubheadingTextBuilder.append(String.format("%s %s. ", characterDescription, attributeDescription));
            }

            if (itemSubheadingTextBuilder.length() > 0) {
                builder.appendText(itemSubheadingTextBuilder.toString());
            }

            builder.decreaseIndent();
        }

        builder.endDocument();

        return builder;
    }

    // Use a linked hashmap to maintain keys in insertion order
    private Map<Character, String> generateCharactersItemSubheadingMap(IntkeyDataset ds) {
        if (!ds.itemSubheadingsPresent()) {
            return Collections.EMPTY_MAP;
        }

        Map<Character, String> retMap = new HashMap<Character, String>();

        String currentSubHeading = null;

        for (Character ch : ds.getCharacters()) {
            if (ch.getItemSubheading() != null) {
                currentSubHeading = ch.getItemSubheading();
            }

            retMap.put(ch, currentSubHeading);
        }

        return retMap;
    }

}

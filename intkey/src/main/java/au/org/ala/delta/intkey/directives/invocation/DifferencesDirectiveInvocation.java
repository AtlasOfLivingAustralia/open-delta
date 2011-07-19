package au.org.ala.delta.intkey.directives.invocation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.DiffUtils;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.MatchType;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFBuilder;

public class DifferencesDirectiveInvocation implements IntkeyDirectiveInvocation {
    private boolean _matchUnknowns;
    private boolean _matchInapplicables;
    private MatchType _matchType;

    private boolean _omitTextCharacters;

    private List<au.org.ala.delta.model.Character> _characters;
    private List<Item> _taxa;

    private CharacterFormatter _characterFormatter;
    private ItemFormatter _taxonFormatter;
    private AttributeFormatter _attributeFormatter;

    private boolean _includeSpecimen;

    public DifferencesDirectiveInvocation(boolean matchUnknowns, boolean matchInapplicables, MatchType matchType, boolean omitTextCharacters, boolean includeSpecimen,
            List<au.org.ala.delta.model.Character> characters, List<Item> taxa) {
        _matchUnknowns = matchUnknowns;
        _matchInapplicables = matchInapplicables;
        _matchType = matchType;
        _includeSpecimen = includeSpecimen;
        _omitTextCharacters = omitTextCharacters;
        _characters = new ArrayList<au.org.ala.delta.model.Character>(characters);
        _taxa = new ArrayList<Item>(taxa);
        _characterFormatter = new CharacterFormatter(false, true, true, false, false);
        _taxonFormatter = new ItemFormatter(false, true, true, false, false, false);
        _attributeFormatter = new AttributeFormatter(false, false, true, true, false);
    }

    @Override
    public boolean execute(IntkeyContext context) {
        List<au.org.ala.delta.model.Character> differences = new ArrayList<au.org.ala.delta.model.Character>();

        Specimen specimen = null;
        if (_includeSpecimen) {
            specimen = context.getSpecimen();
        }

        for (au.org.ala.delta.model.Character ch : _characters) {
            if (_includeSpecimen && !specimen.hasValueFor(ch)) {
                continue;
            }
            
            if (ch instanceof TextCharacter && _omitTextCharacters) {
                continue;
            }
            
            boolean match = DiffUtils.compareForTaxa(context.getDataset(), ch, _taxa, specimen, _matchUnknowns, _matchInapplicables, _matchType);
            if (!match) {
                differences.add(ch);
            }
        }

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        for (au.org.ala.delta.model.Character ch : differences) {

            List<Attribute> attrs = context.getDataset().getAttributesForCharacter(ch.getCharacterId());

            String charDescription = _characterFormatter.formatCharacterDescription(ch);
            builder.appendText(charDescription);
            
            builder.increaseIndent();

            if (_includeSpecimen) {
                builder.appendText("Specimen");
                // TODO need to refactor specimen class to take attribute
                // values directly.
                Attribute attr = DiffUtils.createAttributeForSpecimenValue(specimen, ch);

                builder.increaseIndent();
                
                printAttributeValue(attr, builder);

                builder.decreaseIndent();
            }

            for (Item taxon : _taxa) {
                Attribute taxonAttr = attrs.get(taxon.getItemNumber() - 1);

                String taxonDescription = _taxonFormatter.formatItemDescription(taxon);
                builder.appendText(taxonDescription);

                builder.increaseIndent();

                printAttributeValue(taxonAttr, builder);

                builder.decreaseIndent();

            }

            builder.decreaseIndent();
            builder.appendText("");
        }

        builder.setTextColor(Color.RED);
        builder.setFont(1);

        if (differences.size() == 0) {
            builder.appendText("No differences");
        } else if (differences.size() == 1) {
            builder.appendText(String.format("%s difference", differences.size()));
        } else {
            builder.appendText(String.format("%s differences", differences.size()));
        }

        builder.endDocument();

        context.getUI().displayRTFReport(builder.toString(), "Differences");

        return true;
    }
    
    private void printAttributeValue(Attribute attr, RTFBuilder builder) {
        if (attr.isInapplicable() && attr.isUnknown()) {
            builder.appendText("not applicable");
        } else if (attr.isUnknown() ){
            builder.appendText("not recorded");
        } else {
            String attributeDescription = _attributeFormatter.formatAttribute(attr);
            builder.appendText(attributeDescription);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DIFFERENCES ");

        switch (_matchType) {
        case OVERLAP:
            builder.append("/O ");
            break;
        case SUBSET:
            builder.append("/S ");
            break;
        case EXACT:
            builder.append("/E ");
            break;
        default:
            throw new RuntimeException("Unrecognised match type");
        }

        if (_matchUnknowns) {
            builder.append("/U ");
        }

        if (_matchInapplicables) {
            builder.append("/I ");
        }

        builder.append("(");

        if (_includeSpecimen) {
            builder.append(" ");
            builder.append(IntkeyContext.SPECIMEN_KEYWORD);
        }

        for (Item taxon : _taxa) {
            builder.append(" ");
            builder.append(taxon.getItemNumber());
        }
        builder.append(" )");

        for (Character ch : _characters) {
            builder.append(" ");
            builder.append(ch.getCharacterId());
        }

        return builder.toString();
    }

}

package au.org.ala.delta.intkey.directives.invocation;

import java.awt.Color;
import java.util.List;

import au.org.ala.delta.intkey.model.DiffUtils;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.MatchType;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.util.Pair;

public class SimilaritiesDirectiveInvocation extends IntkeyDirectiveInvocation {

    private MatchType _matchType;
    private boolean _matchUnknowns = false;
    private boolean _matchInapplicables = false;

    private boolean _useGlobalMatchValues = true;

    private List<Character> _characters;
    private List<Item> _taxa;
    private boolean _includeSpecimen = false;

    private CharacterFormatter _characterFormatter;
    private ItemFormatter _taxonFormatter;
    private AttributeFormatter _attributeFormatter;

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    public void setSelectedTaxaSpecimen(Pair<List<Item>, Boolean> pair) {
        this._taxa = pair.getFirst();
        this._includeSpecimen = pair.getSecond();
    }

    public void setMatchOverlap(boolean matchOverlap) {
        if (matchOverlap) {
            _matchType = MatchType.OVERLAP;
            _useGlobalMatchValues = false;
        }
    }

    public void setMatchSubset(boolean matchSubset) {
        if (matchSubset) {
            _matchType = MatchType.SUBSET;
            _useGlobalMatchValues = false;
        }
    }

    public void setMatchExact(boolean matchExact) {
        if (matchExact) {
            _matchType = MatchType.EXACT;
            _useGlobalMatchValues = false;
        }
    }

    public void setMatchUnknowns(boolean matchUnknowns) {
        this._matchUnknowns = matchUnknowns;
        _useGlobalMatchValues = false;
    }

    public void setMatchInapplicables(boolean matchInapplicables) {
        this._matchInapplicables = matchInapplicables;
        _useGlobalMatchValues = false;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        if (_useGlobalMatchValues) {
            _matchType = context.getMatchType();
            _matchUnknowns = context.getMatchUnknowns();
            _matchInapplicables = context.getMatchInapplicables();
        }

        _characterFormatter = new CharacterFormatter(context.displayNumbering(), CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.REMOVE, false, true);
        _taxonFormatter = new ItemFormatter(context.displayNumbering(), CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, false, false, false);
        _attributeFormatter = new AttributeFormatter(context.displayNumbering(), false, CommentStrippingMode.RETAIN_SURROUNDING_STRIP_INNER, AngleBracketHandlingMode.RETAIN, false, context
                .getDataset().getOrWord());

        Specimen specimen = null;
        if (_includeSpecimen) {
            specimen = context.getSpecimen();
        }

        List<au.org.ala.delta.model.Character> similarities = DiffUtils.determineSimilaritiesForTaxa(context.getDataset(), _characters, _taxa, specimen, _matchUnknowns, _matchInapplicables,
                _matchType);

        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();

        for (au.org.ala.delta.model.Character ch : similarities) {

            List<Attribute> attrs = context.getDataset().getAttributesForCharacter(ch.getCharacterId());

            String charDescription = _characterFormatter.formatCharacterDescription(ch);
            builder.appendText(charDescription);

            builder.increaseIndent();

            if (_includeSpecimen) {
                builder.appendText(UIUtils.getResourceString("DifferencesDirective.Specimen"));
                // TODO need to refactor specimen class to take attribute
                // values directly.
                //Attribute attr = DiffUtils.createAttributeForSpecimenValue(specimen, ch);
                Attribute attr = specimen.getAttributeForCharacter(ch);
                
                builder.increaseIndent();

                builder.appendText(_attributeFormatter.formatAttribute(attr));

                builder.decreaseIndent();
            }

            for (Item taxon : _taxa) {
                Attribute taxonAttr = attrs.get(taxon.getItemNumber() - 1);

                String taxonDescription = _taxonFormatter.formatItemDescription(taxon);
                builder.appendText(taxonDescription);

                builder.increaseIndent();

                builder.appendText(_attributeFormatter.formatAttribute(taxonAttr));

                builder.decreaseIndent();

            }

            builder.decreaseIndent();
            builder.appendText("");
        }

        builder.setTextColor(Color.RED);
        builder.setFont(1);

        if (similarities.size() == 0) {
            builder.appendText(UIUtils.getResourceString("SimilaritiesDirective.NoDifferences"));
        } else if (similarities.size() == 1) {
            builder.appendText(UIUtils.getResourceString("SimilaritiesDirective.OneDifference"));
        } else {
            builder.appendText(UIUtils.getResourceString("SimilaritiesDirective.ManyDifferences", similarities.size()));
        }

        builder.endDocument();

        context.getUI().displayRTFReport(builder.toString(), UIUtils.getResourceString("SimilaritiesDirective.ReportTitle"));

        return true;
    }
}

package au.org.ala.delta.intkey.directives.invocation;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.DiffUtils;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.MatchType;
import au.org.ala.delta.intkey.model.specimen.Specimen;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

public class OutputSimilaritiesDirectiveInvocation extends IntkeyDirectiveInvocation {

    private MatchType _matchType;
    private boolean _matchUnknowns = false;
    private boolean _matchInapplicables = false;
    private boolean _omitTextCharacters = false;

    private boolean _useGlobalMatchValues = true;

    private List<Character> _characters;
    private List<Item> _taxa;
    private boolean _includeSpecimen = false;

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

    public void setOmitTextCharacters(boolean omitTextCharacters) {
        this._omitTextCharacters = omitTextCharacters;
    }

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        if (_useGlobalMatchValues) {
            _matchType = context.getMatchType();
            _matchUnknowns = context.getMatchUnknowns();
            _matchInapplicables = context.getMatchInapplicables();
        }

        Specimen specimen = null;
        if (_includeSpecimen) {
            specimen = context.getSpecimen();
        }

        List<au.org.ala.delta.model.Character> similarities = DiffUtils.determineSimilaritiesForTaxa(context.getDataset(), _characters, _taxa, specimen, _matchUnknowns, _matchInapplicables,
                _matchType);

        List<Integer> similarCharNumbers = new ArrayList<Integer>();
        for (au.org.ala.delta.model.Character ch : similarities) {
            similarCharNumbers.add(ch.getCharacterId());
        }

        try {
            if (context.getLastOutputLineWasComment()) {
                context.setLastOutputLineWasComment(false);
            } else {
                context.appendToOutputFile("OUTPUT SIMILARITIES");
            }
            context.appendToOutputFile(Utils.formatIntegersAsListOfRanges(similarCharNumbers));
        } catch (IllegalStateException ex) {
            throw new IntkeyDirectiveInvocationException("NoOutputFileOpen.error");
        }

        return true;
    }

}

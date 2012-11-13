/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey.directives.invocation;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DiffUtils;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MatchType;
import au.org.ala.delta.model.Specimen;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

public class OutputSimilaritiesDirectiveInvocation extends LongRunningIntkeyDirectiveInvocation<Void> {

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
    public Void doRunInBackground(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        int numberOfTaxa = _taxa.size();
        if (_includeSpecimen) {
            numberOfTaxa++;
        }

        if (numberOfTaxa < 2) {
            throw new IntkeyDirectiveInvocationException("TwoTaxaRequiredForComparison.error");
        }
        
        if (_useGlobalMatchValues) {
            _matchType = context.getMatchType();
            _matchUnknowns = context.getMatchUnknowns();
            _matchInapplicables = context.getMatchInapplicables();
        }

        Specimen specimen = null;
        if (_includeSpecimen) {
            specimen = context.getSpecimen();
        }
        
        progress(UIUtils.getResourceString("OutputSimilaritiesDirective.Progress.Calculating"));

        List<au.org.ala.delta.model.Character> similarities = DiffUtils.determineSimilaritiesForTaxa(context.getDataset(), _characters, _taxa, specimen, _matchUnknowns, _matchInapplicables,
                _matchType);
        
        int numCharactersProcessed = 0;

        List<Integer> similarCharNumbers = new ArrayList<Integer>();
        for (au.org.ala.delta.model.Character ch : similarities) {
            similarCharNumbers.add(ch.getCharacterId());
            
            int progressPercent = (int) Math.floor((((double) numCharactersProcessed) / similarities.size()) * 100);
            progress(UIUtils.getResourceString("OutputSimilaritiesDirective.Progress.Generating", progressPercent));
        }

        try {
            if (context.getLastOutputLineWasComment()) {
                context.setLastOutputLineWasComment(false);
            } else {
                context.appendTextToOutputFile(this.toString());
            }
            context.appendTextToOutputFile(Utils.formatIntegersAsListOfRanges(similarCharNumbers));
        } catch (IllegalStateException ex) {
            throw new IntkeyDirectiveInvocationException("NoOutputFileOpen.error");
        }

        return null;
    }

    @Override
    protected void handleProcessingDone(IntkeyContext context, Void result) {
        // do nothing - output file is updated by doRunInBackground
    }

}
